import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.Scanner;

public class Main {
	
	public static final String TEXT_FILE = "test.txt";
	
	//11101011 01001001 10011101 11010001 11011101 10100101 10011101 00011100 01101110 11
	
	//String to be read in from input file
	public static StringBuilder DECODED_MESSAGE = new StringBuilder();
	
	public static String END_CODE;
	
	public static void main(String[] args) {
		//testCodingTree();
		long startTime = System.nanoTime(); 
		readTextFile();
		compress(startTime);
	}
	
	public static void readTextFile() {
		Scanner inputFile;
		
		try {
			inputFile = new Scanner(new File(TEXT_FILE));
			
			while (inputFile.hasNextLine()) {
				StringBuilder line = new StringBuilder();
				line.append(inputFile.nextLine());
				if (inputFile.hasNextLine()) {
					line.append("\r\n");
				}
				DECODED_MESSAGE.append(line.toString());
			}
			
		} catch (Exception e) {
			System.out.println(e);
		}
		
		//System.out.println(DECODED_MESSAGE);
	}
	
	public static void compress(long startTime) {
		CodingTree tree = new CodingTree(DECODED_MESSAGE.toString());
		Map<Character, String> codes = tree.getCodes();
		String encodedMessage = tree.getBits();
		Byte[] byteData = createByteData(encodedMessage);
		createCodesFile(codes);
		createCompressedFile(byteData);
		displayStats(startTime);
		createOriginalFile(tree, codes);
	}	
		
	public static void displayStats(long startTime) {
		File orig = new File(TEXT_FILE);
		File comp = new File("compressed.txt");
		System.out.println("Original size: " + orig.length() / 1000 + "KB");
		System.out.println("Compressed size: " + comp.length() / 1000 + "KB");
		double ratio = (double) (comp.length() / 1000) / (double) (orig.length() / 1000) * 100;
		DecimalFormat df = new DecimalFormat("#.##");
		System.out.println("Compression ratio: " +  df.format(ratio) + "%");
		long endTime = System.nanoTime();
		long duration = (endTime - startTime) / 1000000;
		System.out.println("Elapsed time: " + duration + "ms");
	}
	
	public static void testCodingTree() {
		CodingTree tree = new CodingTree("ANNA HAS A BANANA IN A BANDANA");
		Map<Character, String> codes = tree.getCodes();
		String encodedMessage = tree.getBits();
		
		//System.out.println(encodedMessage);
		
		END_CODE = tree.getEndCode();
		Byte[] byteData = createByteData(encodedMessage);
		
		createCompressedFile(byteData);
		String decompressedEncodedMessage = null;
		
		try {
			decompressedEncodedMessage = decompress();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String decodedMessage = tree.decode(decompressedEncodedMessage, codes);
		System.out.println(decodedMessage);
	}
	
	private static Byte[] createByteData(String encodedMessage) {
		int arrayLength = encodedMessage.length() / 8;
		
		Byte[] byteArr = new Byte[arrayLength + 1];
		String[] byteData = new String[arrayLength + 1];
		
		for (int i = 0; i < arrayLength; i++) {
			String strByte = encodedMessage.substring(i * 8, i * 8 + 8);
			int intByte = Integer.parseInt(strByte, 2);
			Byte b = (byte) intByte;
			byteArr[i] = b;
			byteData[i] = Integer.toBinaryString((b & 0xFF) + 0x100).substring(1);
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
	
	private static void createCodesFile(Map<Character, String> codes) {
		PrintWriter writer = null;
		
		try {
			writer = new PrintWriter("codes.txt", "UTF-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		writer.println(codes);
		writer.close();
	}
	
	private static void createCompressedFile(Byte[] byteData) {
		FileOutputStream out = null;
		
		try {
			out = new FileOutputStream("compressed.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		for (int i = 0; i < byteData.length; i++) {
		
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
	
	private static void createOriginalFile(CodingTree tree, Map<Character, String> codes) {
		String encodedMessage = null;
		
		try {
			encodedMessage = decompress();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		String decodedMessage = tree.decode(encodedMessage, codes);
		
		PrintWriter writer = null;
		
		try {
			writer = new PrintWriter("test2.txt", "UTF-8");
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		writer.println(decodedMessage);
		writer.close();
	}
	
	//Decompress binary file into String of bits, to be decoded
	private static String decompress() throws IOException {
		StringBuilder bytes = new StringBuilder();
		File binaryFile = new File("compressed.txt");
		FileInputStream inFile = null;
		
		try {
			inFile = new FileInputStream(binaryFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		String[] byteData = new String[inFile.available()];
		
		for (int i = 0; i < byteData.length; i++) {
			int b = inFile.read();
			byteData[i] = Integer.toBinaryString((b & 0xFF) + 0x100).substring(1);
			//http://stackoverflow.com/questions/12310017/how-to-convert-a-byte-to-its-binary-string-representation
			bytes.append(Integer.toBinaryString((b & 0xFF) + 0x100).substring(1));
		}

		return bytes.toString();
	}
}
