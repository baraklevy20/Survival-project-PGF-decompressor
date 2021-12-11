import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageUtils {
	public static BufferedImage getImageFromPixelsArray(int[] pixels, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        WritableRaster raster = (WritableRaster) image.getData();
        raster.setPixels(0,0,width,height,pixels);
        image.setData(raster);
        
        return image;
    }
	
	public static void saveImage(BufferedImage img, String ref) {  
	    try {
	        ImageIO.write(img, "png", new File(ref));  
	    } catch (IOException e) {  
	        e.printStackTrace();  
	    }  
	}
}
