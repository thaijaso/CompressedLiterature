import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

public class Main {
	
	//Encoded bits for the string: "ANNA HAS A BANANA IN A BANDANA"
	public static final String ENCODED_MESSAGE = "11101011010010011001110111010001110111011010010110011101000111000110111011";
	
	public static void main(String[] args) {
		CodingTree tree = new CodingTree("ANNA HAS A BANANA IN A BANDANA");
		Map<Character, String> codes = tree.getCodes();
		String encodedMessage = tree.getBits();
		Byte[] byteData = createByteData(encodedMessage);
		
		writeToBinaryFile(byteData);
		String decompressedEncodedMessage = null;
		
		try {
			decompressedEncodedMessage = decompress();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String decodedMessage = tree.decode(decompressedEncodedMessage, codes);
		System.out.println(decodedMessage);
	}
	
	public static Byte[] createByteData(String encodedMessage) {
		int arrayLength = encodedMessage.length() / 8;
		
		Byte[] byteArr = new Byte[arrayLength + 1];
		
		for (int i = 0; i < arrayLength; i++) {
			String strByte = encodedMessage.substring(i * 8, i * 8 + 8);
			int intByte = Integer.parseInt(strByte, 2);
			Byte b = (byte) intByte;
			byteArr[i] = b;
		}
		
		String lastStrByte = encodedMessage.substring(arrayLength * 8);
		int lastStrByteLen = lastStrByte.length();
		
		for (int i = 0; i < 8 - lastStrByteLen; i++) {
			lastStrByte = lastStrByte.concat("0");		//this should not be 0 since it might be a code
		}
		
		int lastIntByte = Integer.parseInt(lastStrByte);
		Byte lastByte = (byte) lastIntByte;
		byteArr[arrayLength] = lastByte;
		
		return byteArr;
	}
	
	public static void writeToBinaryFile(Byte[] byteData) {
		FileOutputStream out = null;
		
		try {
			out = new FileOutputStream("test");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		for (int i = 0; i < byteData.length; i++) {
			int intByte = (int) byteData[i];
			char charByte = (char) intByte;
			
			try {
				//write ascii to chars to file
				out.write(byteData[i].byteValue());
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		
		try {
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	//Decompress binary file into String of bits, to be decoded
	public static String decompress() throws IOException {
		StringBuilder bytes = new StringBuilder();
		File binaryFile = new File("test");
		FileInputStream inFile = null;
		
		try {
			inFile = new FileInputStream(binaryFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		String[] byteData = new String[inFile.available()];
		
		for (int i = 0; i < byteData.length; i++) {
			int b = inFile.read();
			byteData[i] = Integer.toBinaryString(b);
			//http://stackoverflow.com/questions/12310017/how-to-convert-a-byte-to-its-binary-string-representation
			bytes.append(Integer.toBinaryString((b & 0xFF) + 0x100).substring(1));
		}

		return bytes.toString();
	}
}
