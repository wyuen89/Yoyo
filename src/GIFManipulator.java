import java.lang.Throwable;
import java.util.Arrays;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.awt.image.ColorModel;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.IIOImage;
import javax.imageio.ImageWriteParam;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.NamedNodeMap;

class GIFManipulator{
	String path;
	private BufferedImage[] frames;
	private IIOMetadata[] metadata;
	private int[] imageBuild = null;
	private int size = 0;
	private int maxWidth = 0;

	public GIFManipulator(String fileName){
		path = fileName;
		extractFrames();

		imageBuild = new int[frames[0].getWidth() * frames[0].getHeight()];

		decodeFrames();
	}

	public BufferedImage[] getFrames(){
		return frames;
	}

	public IIOMetadata[] getMetadata(){
		return metadata;
	}

	public int getNumFrames(){
		return size;
	}

	public GIFData reverse(){
		BufferedImage[] revFrames = new BufferedImage[size];
		IIOMetadata[] revMetadata = new IIOMetadata[size];

		for(int i = 0; i < size; i++){
			revFrames[i] = frames[size - i - 1];
			revMetadata[i] = metadata[size - i - 1];
		}

		//last frame done first to extract ApplicationsExtension node to apply to first frame
		IIOMetadataNode lastMeta = (IIOMetadataNode)revMetadata[size-1].getAsTree("javax_imageio_gif_image_1.0");
		Node appNode = null;
		
		for(int i = 0; i < lastMeta.getLength(); i++){
			Node currNode = lastMeta.item(i);

			if(currNode.getNodeName().equals("ApplicationExtensions")){
				ImageWriter writer = ImageIO.getImageWritersByFormatName("gif").next();
				IIOMetadata newMetadata = writer.getDefaultImageMetadata(new ImageTypeSpecifier(revFrames[i]), null);

				appNode = lastMeta.removeChild(currNode);

				try{
					newMetadata.setFromTree(newMetadata.getNativeMetadataFormatName(), lastMeta);
				}catch(IIOInvalidTreeException e){
					e.printStackTrace();
				}

				revMetadata[size-1] = newMetadata;
				break;
			}
		}

		for(int i = 0; i < size - 1; i++){
			Node currMeta = revMetadata[i].getAsTree("javax_imageio_gif_image_1.0");
			Node nextMeta = revMetadata[i+1].getAsTree("javax_imageio_gif_image_1.0");
			NodeList currChildren = currMeta.getChildNodes();
			NodeList nextChildren = nextMeta.getChildNodes();

			String newDelay = "10";

			for(int j = 0; j < nextChildren.getLength(); j++){
				Node nextNode = nextChildren.item(j);

				if(nextNode.getNodeName().equals("GraphicControlExtension")){
					NamedNodeMap attributes = nextNode.getAttributes();
					Node delayNode = attributes.getNamedItem("delayTime");
					newDelay = delayNode.getNodeValue();
					break;
				}
			}

			IIOMetadataNode tempMeta = (IIOMetadataNode)revMetadata[i].getAsTree("javax_imageio_gif_image_1.0");

			if(i == 0){
				tempMeta.appendChild(appNode);
			}

			for(int j = 0; j < tempMeta.getLength(); j++){
				Node currNode = tempMeta.item(j);

				if(currNode.getNodeName().equals("GraphicControlExtension")){
					((IIOMetadataNode)currNode).setAttribute("delayTime", newDelay);
					break;
				}
			}

			ImageWriter writer = ImageIO.getImageWritersByFormatName("gif").next();
			IIOMetadata newMetadata = writer.getDefaultImageMetadata(new ImageTypeSpecifier(revFrames[i]), null);

			try{
				newMetadata.setFromTree(newMetadata.getNativeMetadataFormatName(), tempMeta);
			}catch(IIOInvalidTreeException e){
				e.printStackTrace();
			}


			revMetadata[i] = newMetadata;
		}

		return new GIFData(revFrames, revMetadata);
	}

	public GIFData boomerang(){
			BufferedImage[] boomFrames = new BufferedImage[size * 2 - 1];
			IIOMetadata[] boomMetadata = new IIOMetadata[size * 2 - 1];

			for(int i = 0; i < size - 1; i++){
				boomFrames[i] = frames[i];
				boomMetadata[i] = metadata[i];
			}

			GIFData revData = reverse();
			BufferedImage[] revFrames = revData.getFrames();
			IIOMetadata[] revMeta = revData.getMetadata();
			int offset = size - 1;

			for(int i = 0; i < size; i++){
				boomFrames[i + offset] = revFrames[i];
				boomMetadata[i + offset] = revMeta[i];
			}

			return new GIFData(boomFrames, boomMetadata);
	}

	public GIFData original(){
		return new GIFData(frames, metadata);
	}

	public void printMetadata(){
		for(int i = 0; i < size; i++){
			printMetadata(i, metadata);
		}
	}

	public void printMetadata(IIOMetadata[] inMeta){
		for(int i = 0; i < inMeta.length; i++){
			printMetadata(i, inMeta);
		}
	}

	public void printMetadata(int idx, IIOMetadata[] inMeta){
		System.out.print("Frame Number: " + (idx + 1) + "\n");
		Node frameMeta = inMeta[idx].getAsTree("javax_imageio_gif_image_1.0");
		NodeList childList = frameMeta.getChildNodes();

		for(int i = 0; i < childList.getLength(); i++){
			Node currNode = childList.item(i);
			NamedNodeMap nodeAttributes = currNode.getAttributes();

			System.out.print(currNode.getNodeName() + " Attributes:\n");

			for(int j = 0; j < nodeAttributes.getLength(); j++){
				Node currAttribute = nodeAttributes.item(j);

				System.out.print("\t" + currAttribute.getNodeName() + ": " + currAttribute.getNodeValue() + "\n");
			}
		}

		System.out.print("\n");

	}

	private void extractFrames(){
		ImageInputStream inStream = null;
		ImageReader reader = (ImageReader)ImageIO.getImageReadersByFormatName("gif").next();

		try{
    		inStream = ImageIO.createImageInputStream(new File(path));
    		reader.setInput(inStream, false);
    		size = reader.getNumImages(true);
    	
    		frames = new BufferedImage[size];
    		metadata = new IIOMetadata[size];

    		for(int i = 0; i < size; i++){
    			BufferedImage currFrame = reader.read(i);
    			BufferedImage newBuff = new BufferedImage(currFrame.getWidth(), currFrame.getHeight(), BufferedImage.TYPE_INT_ARGB);
    			newBuff.getGraphics().drawImage(currFrame, 0, 0, null);
    			frames[i] = newBuff;
    			metadata[i] = reader.getImageMetadata(i);

    			if(maxWidth < currFrame.getWidth()){
    				maxWidth = currFrame.getWidth();
    			}
    		}

    	}catch(IOException e){
    		e.printStackTrace();

    	}finally{
    		if(inStream != null){
    			try{
    				inStream.close();
    			}catch(IOException e){
    				e.printStackTrace();
    			}
    		}
    	}
	}

	private void outputAsGif(BufferedImage img, int i){
		try{
			ImageIO.write(img, "GIF", new File(i + ".gif"));
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private void decodeFrames(){
		for(int i = 0; i < size; i++){
			BufferedImage currFrame = frames[i];
			WritableRaster alphaRaster = currFrame.getAlphaRaster();
			int width = currFrame.getWidth();
			int height = currFrame.getHeight();
			int[] currPixels = currFrame.getRGB(0, 0, width, height, null, 0, width);

			int[] alpha = new int[currPixels.length]; 
			alphaRaster.getPixels(0, 0, width, height, alpha);

			decode(alpha, imageBuild, currPixels, maxWidth, width);

			frames[i].setRGB(0, 0, width, height, currPixels, 0, width);
		}
	}

	private void decode(int[] alpha, int[] build, int[] currFrame, int length, int scan){
		for(int i = 0; i < alpha.length/scan; i++){
			for(int j = 0; j < scan; j++){
				if(alpha[i * scan + j] == 0){
					currFrame[i * scan + j] = build[i * length + j];
				}

				else{
					build[i * length + j] = currFrame[i * scan + j] | 0xFF000000;
				}
			}
		}
	}
}