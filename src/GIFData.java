import java.awt.image.BufferedImage;
import javax.imageio.metadata.IIOMetadata;

class GIFData{
	private BufferedImage[] frames;
	private IIOMetadata[] metadata;

	public GIFData(BufferedImage[] inFrames , IIOMetadata[] inMeta){
		frames = inFrames;
		metadata = inMeta;
	}

	public BufferedImage[] getFrames(){
		return frames;
	}

	public IIOMetadata[] getMetadata(){
		return metadata;
	}
}