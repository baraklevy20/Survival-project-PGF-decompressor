import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import javax.imageio.ImageIO;

public class PGFUtils {
	public static final int TOTAL_THREADS = 10;
	
	private static int totalThreads;
	
	public static void compressPGF(File file) throws IOException {
		BufferedImage image = ImageIO.read(file);
		byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
		
		ArrayList<Byte> output = new ArrayList<Byte>();
		
		output.add((byte) (image.getWidth() >> 0));
		output.add((byte) (image.getWidth() >> 8));
		output.add((byte) (image.getHeight() >> 0));
		output.add((byte) (image.getHeight() >> 8));
		
		int totalColors = 0;
		int[] colors = new int[256];
		
		for (int i = 0; i < pixels.length; i += 3) {
			int startColor = getColor(pixels, i);
			int startIndex = i;
			
			int colorIndex = containsColor(totalColors, colors, startColor);
			
			if (colorIndex == -1) {
				colors[totalColors] = startColor;
				colorIndex = totalColors;
				totalColors++;
			}
			
			while ((i + 3 - startIndex) / 3 <= 62 && (i + 3) % image.getWidth() != 0 && i + 3 < pixels.length && getColor(pixels, i + 3) == startColor) {
				i += 3;
			}
			
			if (i != startIndex) {
				output.add((byte)(192 + (i - startIndex) / 3 + 1));
				output.add((byte) colorIndex);
			}
			else {
				if (colorIndex > 192) {
					output.add((byte) 193); // 1 color trick
					output.add((byte) colorIndex);
				}
				else {
					output.add((byte) colorIndex);
				}
			}
		}
		
		output.add((byte) 12);
		
		for (int i = 0; i < colors.length; i++) {
			output.add((byte) (colors[i] >> 16));
			output.add((byte) (colors[i] >> 8));
			output.add((byte) (colors[i] >> 0));
			System.out.println(colors[i]);
		}
		
		System.out.println("Colors: " + totalColors);
		
		byte[] outputArray = new byte[output.size()];
		
		for (int i = 0; i < outputArray.length; i++) {
			outputArray[i] = output.get(i);
		}
		
		FileOutputStream outputStream = new FileOutputStream(new File(file.getPath().replace("png", "pgf")));
		outputStream.write(outputArray);
		outputStream.close();
		
		System.out.println(file.getPath() + " was compressed");
	}
	
	private static int containsColor(int totalColors, int[] colors, int color) {
		for (int i = 0; i < totalColors; i++) {
			if (colors[i] == color) {
				return i;
			}
		}
		
		return -1;
	}
	
	public static void decompressPGF(File file) throws IOException {
		decompressPGF(file, false);
	}
	
	public static void decompressPGF(File file, boolean isWithThreads) throws IOException {
		FileInputStream input = new FileInputStream(file);
		
		int width = readTwoBytes(input);
		int height = readTwoBytes(input);
		byte[] imageArray = new byte[(int)(file.length() - 0x304)];
		input.read(imageArray);
		
		int[] pixelsArray = new int[width * height];
		
		int k = 0;
		
		for (int i = 0; i < imageArray.length - 1; i++) {
			if ((imageArray[i] & 0xFF) <= 192) {
				pixelsArray[k++] = imageArray[i] & 0xFF;
			}
			else {
				for (int j = 0; j < (imageArray[i] & 0xFF) - 192; j++) {
					pixelsArray[k + j] = imageArray[i + 1] & 0xFF;
				}
				
				k += (imageArray[i] & 0xFF) - 192;
				i++;
			}
		}
		
		byte[] palette = new byte[0x300];
		System.out.println(Arrays.toString(palette));
		input.read(palette);
		
		int[] pixels = new int[3 * width * height];
		
		for (int i = 0; i < pixels.length / 3; i++) {
			pixels[3 * i] = palette[3 * pixelsArray[i]];
			pixels[3 * i + 1] = palette[3 * pixelsArray[i] + 1];
			pixels[3 * i + 2] = palette[3 * pixelsArray[i] + 2];
		}
		
		input.close();
		
		ImageUtils.saveImage(ImageUtils.getImageFromPixelsArray(pixels, width, height), "new_" + file.getPath().replace("pgf", "png"));
		file.delete();
		
		if (isWithThreads) {
			synchronized (pixelsArray) {
				totalThreads--;
			}
		}
	}
	
	public static void decompressDirectory(File root) throws IOException {
		if (root.isFile()) {
			if (root.getName().endsWith("pgf")) {
				if (totalThreads < TOTAL_THREADS) {
					totalThreads++;
					new Thread(new Runnable() {
						@Override
						public void run() {
							try {
								decompressPGF(root, true);
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}).start();
				}
				else {
					decompressPGF(root, false);
				}
			}
		}
		else {
			for (File file : root.listFiles()) {
				decompressDirectory(file);
			}
		}
	}
	
	public static int getColor(byte[] arr, int offset) {
		int byte0 = arr[offset] & 0xFF;
		int byte1 = arr[offset + 1] & 0xFF;
		int byte2 = arr[offset + 2] & 0xFF;
		
		return (byte2 << 16) + (byte1 << 8) + byte0;
	}
	
	public static int readTwoBytes(FileInputStream input) throws IOException {
		byte[] bytes = new byte[2];
		input.read(bytes);
		
		return (bytes[0] & 0xFF) + ((bytes[1] & 0xFF) << 8);
	}
}
