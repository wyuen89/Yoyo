import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

public class GrayscaleFilter extends Filter{

	public GrayscaleFilter(){
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
				int avg = (red + green + blue) / 3;

				String avgHex = Integer.toHexString(avg);

				//ensures hex value is two chars
				if(avg <= 15){
					avgHex = "0" + avgHex;
				}

				String hex = "0x" + avgHex + avgHex + avgHex;

				pixels[j] = Integer.decode(hex) | 0xFF000000;
			}

			frames[i].setRGB(0, 0, width, height, pixels, 0, width);
		}
	}
}