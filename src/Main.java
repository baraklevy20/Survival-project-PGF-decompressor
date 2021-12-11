import java.io.File;
import java.io.IOException;

public class Main {
	public static void main(String[] args) throws IOException {
		PGFUtils.compressPGF(new File("aaa.png"));
//		PGFUtils.compressPGF(new File("thisone.png"));
//		PGFUtils.decompressPGF(new File("thisone.pgf"));
//		PGFUtils.decompressDirectory(new File("images"));
	}
}
