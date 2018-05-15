import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

public class NegativeFilter extends Filter{
	public NegativeFilter(){
		super();
	}

	public void applyFilter(BufferedImage[] frames){
		for(int i = 0; i < frames.length; i++){
			int width = frames[i].getWidth();
			int height = frames[i].getHeight();
			int[] pixels = frames[i].getRGB(0, 0, width, height, null, 0, width);
			ColorModel cm = frames[i].getColorModel();

			for(int j = 0; j < pixels.length; j++){
				int red = cm.getRed(pixels[j]);
				int green = cm.getGreen(pixels[j]);
				int blue = cm.getBlue(pixels[j]);

				int nRed = 255 - red;
				int nGreen = 255 - green;
				int nBlue =  255 - blue;

				int outRGB = 0xFF;

				outRGB = (outRGB << 8) | nRed;
				outRGB = (outRGB << 8) | nGreen;
				outRGB = (outRGB << 8) | nBlue;

				pixels[j] = outRGB;
			}

			frames[i].setRGB(0, 0, width, height, pixels, 0, width);
		}
	}
}