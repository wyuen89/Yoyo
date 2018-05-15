import java.util.*;
import javax.imageio.stream.FileImageInputStream;
import java.lang.Exception;
import javax.imageio.ImageReader;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.IIOImage;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;

class Yoyo{
	public static void main(String[] args){
		String file = null;
		String outFile = "output.gif";
		boolean reverse = false;
		boolean boom = false;
		String colorHex = null;
		int filterNum = 0;
		Filter filter = new OriginalFilter();

		int currArg = 0;

		for(int i = 0; i < args.length;){
			switch(args[i]){

				case "-r":
					reverse = true;
					break;

				case "-b":
					boom = true;
					break;

				case "-g":
					filter = new GrayscaleFilter();
					filterNum++;
					break;

				case "-i":
					if(i+1 < args.length){
						colorHex = args[i+1];
					}

					filter = new ColorIsoFilter(colorHex);
					filterNum++;
					i++;
					break;

				case "-s":
					filter = new SepiaFilter();
					filterNum++;
					break;

				case "-n":
					filter = new NegativeFilter();
					filterNum++;
					break;

				default:
					if(file == null){
						file = args[i];
					}

					else{
						outFile = args[i];
						break;
					}
			}

			i++;

		}

		if(file == null || (filterNum < 1) || (reverse && boom)){
			printUsage();
			System.exit(1);
		}

		GIFManipulator inGIF = new GIFManipulator(file);
		GIFData data = null;

		if(reverse){
			data = inGIF.reverse();
		}
		else if(boom){
			data = inGIF.boomerang();
		}
		else{
			data = inGIF.original();
		}
		
		filter.applyFilter(data.getFrames());
		writeGIF(outFile, data.getFrames(), data.getMetadata());			
	}

	private static void writeGIF(String fileName, BufferedImage[] frames, IIOMetadata[] metadata){

		try{
			ImageOutputStream output = ImageIO.createImageOutputStream(new File(fileName));
			ImageWriter writer = ImageIO.getImageWritersByFormatName("gif").next();

			writer.setOutput(output);
			writer.prepareWriteSequence(null);

			for(int i = 0; i < frames.length; i++){
				writer.writeToSequence(new IIOImage(frames[i], null, metadata[i]), null);
			}

		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private static void printUsage(){
		System.out.print("Usage: [-r | -b] [-g | -i colorHex | -s | -n] input_file [output_file_name]\n" + 
			"\t-r: Reverse\n" +
			"\t-b: Boomerang\n" +
			"\t-g: Grayscale\n" +
			"\t-i colorHex: Isolate color given as hexidecimal\n" +
			"\t-s: Sepia\n" +
			"\t-n: Negative\n" +
			"\tinput_file: GIF file input\n" +
			"\toutput_file_name: Desired output file name. Default is \"output.gif\"\n");
	}
}