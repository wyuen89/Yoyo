import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

public class ColorIsoFilter extends Filter{

	String color;
	int range;

	public ColorIsoFilter(String color){
		super();

		this.color = color;
		range = 15;
	}

	public ColorIsoFilter(String color, int range){
		super();

		this.color = color;
		this.range = range;
	}

	public void applyFilter(BufferedImage[] frames){
		ColorModel isoCM = frames[0].getColorModel();
		int isoColor = Integer.decode(color) | 0xFF000000;
		int isoRed = isoCM.getRed(isoColor);
		int isoGreen = isoCM.getGreen(isoColor);		
		int isoBlue = isoCM.getBlue(isoColor);
		int isoHue = getHue(isoRed, isoGreen, isoBlue);

		for(int i = 0; i < frames.length; i++){
			int width = frames[i].getWidth();
			int height = frames[i].getHeight();
			int[] pixels = frames[i].getRGB(0, 0, width, height, null, 0, width);
			ColorModel cm = frames[i].getColorModel();

			for(int j = 0; j < pixels.length; j++){
				int red = cm.getRed(pixels[j]);
				int green = cm.getGreen(pixels[j]);
				int blue = cm.getBlue(pixels[j]);
				int hue = getHue(red, green, blue);
				hue = (hue + 360) % 360;

				if(range < Math.abs(isoHue - hue)){
					int avg = (red + green + blue) / 3;

					String avgHex = Integer.toHexString(avg);

					//ensures hex value is two chars
					if(avg <= 15){
						avgHex = "0" + avgHex;
					}

					String hex = "0x" + avgHex + avgHex + avgHex;

					pixels[j] = Integer.decode(hex) | 0xFF000000;
				}
			}

			frames[i].setRGB(0, 0, width, height, pixels, 0, width);
		}
	}

	private int getHue(int red, int green, int blue){
		Character max;
		int maxVal;
		int minVal;
		double delta;

		if(red < green){
			maxVal = green;
			max = new Character('g');
			minVal = red;
		}

		else{
			maxVal = red;
			max = new Character('r');
			minVal = green;
		}

		if(maxVal < blue){
			maxVal = blue;
			max = new Character('b');
		}

		if(blue < minVal){
			minVal = blue;
		}

		delta = maxVal - minVal;

		if(delta == 0.0){
			return 0;
		}

		else if(max.equals('r')){
			return (int) (60 * (((green - blue) / delta) % 6));
		}

		else if(max.equals('g')){
			return (int) (60 * (((blue - red)/delta) + 2));
		}

		else{
			return (int) (60 * (((red - green)/delta) + 4));
		}
	}
}