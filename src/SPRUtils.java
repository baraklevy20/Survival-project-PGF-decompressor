import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SPRUtils {
	public static void main(String[] args) throws IOException {
//		read(new File("Decompressed images\\interface\\lobby\\room_node\\champ_icon.spr"));
		read(new File("Decompressed images\\game\\character\\character1.spr"));
	}
	
	public static void read(File file) throws IOException {
		FileInputStream input = new FileInputStream(file);
		byte[] bytes = new byte[(int) file.length()];
		input.read(bytes);
		input.close();
		
		ByteBuffer buffer = ByteBuffer.wrap(bytes);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		
		int fileNameLength = buffer.getInt();
		String fileName = readString(buffer, fileNameLength);
		
		int unused = buffer.getInt(); // The game doesn't read this field
		int numberOfSprites = buffer.getInt();
		
		System.out.println("Positions:");
		
		for (int i = 0; i < numberOfSprites; i++) {
			int startX = buffer.getInt();
			int startY = buffer.getInt();
			int endX = buffer.getInt();
			int endY = buffer.getInt();
			System.out.printf("(%d,%d)->(%d,%d)\n", startX, startY, endX, endY);
		}
		
		System.out.println("Unknowns:");
		
		for (int i = 0; i < numberOfSprites * 2; i++) {
			int startX = buffer.getInt();
			int startY = buffer.getInt();
			int endX = buffer.getInt();
			int endY = buffer.getInt();
			System.out.printf("(%d,%d)->(%d,%d)\n", startX, startY, endX, endY);
		}
		
		System.out.println("Unknowns 2:");
		
		for (int i = 0; i < numberOfSprites; i++) {
			int startX = buffer.getInt();
			int startY = buffer.getInt();
			System.out.printf("(%d,%d)\n", startX, startY);
		}
		
		System.out.println(bytes.length - buffer.position());
	}
	
	private static String readString(ByteBuffer buffer, int length) {
		byte[] bytes = new byte[length];
		buffer.get(bytes);
		
		return new String(bytes);
	}
}
