import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

public class SepiaFilter extends Filter{

	public SepiaFilter(){
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

				int sRed = (int) (.393 * red + .769 * green + .189 * blue);
				int sGreen = (int) (.349 * red + .686 * green + .168 * blue);
				int sBlue =  (int) (.272 * red + .534 * green + .131 * blue);

				int outRGB = 0xFF;

				if(sRed < 255){
					outRGB = (outRGB << 8) | sRed;
				}

				else{
					outRGB = (outRGB << 8) | 0xFF;
				}

				if(sGreen < 255){
					outRGB = (outRGB << 8) | sGreen;
				}

				else{
					outRGB = (outRGB << 8) | 0xFF;
				}

				if(sBlue < 255){
					outRGB = (outRGB << 8) | sBlue;
				}

				else{
					outRGB = (outRGB << 8) | 0xFF;
				}

				pixels[j] = outRGB;
			}

			frames[i].setRGB(0, 0, width, height, pixels, 0, width);
		}
	}
}