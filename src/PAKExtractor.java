import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class PAKExtractor {
	ByteBuffer buffer;
	FileInputStream input;
	
	public PAKExtractor() throws IOException {
		File file = new File("image.idx");
		FileInputStream input = new FileInputStream(file);
		byte[] bytes = new byte[(int) file.length()];
		
		input.read(bytes);
		
		buffer = ByteBuffer.wrap(bytes);
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		
		read();
		
		input.close();
	}
	
	public void read() throws IOException {
		input = new FileInputStream(new File("image.pak"));
		input.read(new byte[38]);
		
		buffer.get(new byte[34]);
		int numberOfFiles = buffer.getInt();
		System.out.println(numberOfFiles);
		for (int i = 1; i <= numberOfFiles; i++) { // 10698
			System.out.print(i + " ");
			readFile();
		}
	}
	
	public void readFile() throws IOException {
		buffer.getInt();
		
		byte[] nameBytes = new byte[255];
		buffer.get(nameBytes);
		int length = 0;
		
		for (int i = 0; i < nameBytes.length; i++) {
			if (nameBytes[i] == 0) {
				length = i;
				break;
			}
		}
		
		int size = buffer.getInt();
		int offset = buffer.getInt();
		
		String name = new String(nameBytes, 0, length);
		System.out.println(name);
		File f = createFile(name);
		FileOutputStream output = new FileOutputStream(f);
		
		byte[] bytes = new byte[size];
		input.read(bytes);
		output.write(bytes);
		output.flush();
		output.close();
		
		System.out.println("offset : " + offset + " size : " + size + " " + name);
		
	}
	
	public File createFile(String s) throws IOException {
		String[] folders = s.split("\\\\");
		File current = new File(folders[0]);
		current.mkdir();
		
		for (int i = 1; i < folders.length - 1; i++) {
			current = new File(current.getPath() + "\\" + folders[i]);
			current.mkdir();
		}
		
		current = new File(current.getPath() + "\\" + folders[folders.length - 1]);
		current.createNewFile();
		
		return current;
	}
	
	public String getHex(int s) {
		return String.format("%04X ", s);
	}
}
