import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CodingTree {
	
	private String myMessage;
	
	private Node myRoot;
	
	private StringBuilder myCharCode = new StringBuilder();
	
	private StringBuilder myEndCode = new StringBuilder();
	
	private Map<Character, Integer> myCharFrequencies = new HashMap<Character, Integer>();
	
	private List<Node> myNodeList = new ArrayList<Node>();
	
	private Map<Character, String> codes = new HashMap<Character, String>();
	
	private StringBuilder bits = new StringBuilder();
	
	public CodingTree(String message) {
		myMessage = message;
		createCharFrequencies();		
		createCharNodes();
		createTree();
		createCharCodes();
		createBits();
		//System.out.println(myEndCode.toString());
	}
	
	private class Node implements Comparator<Node> {
		Character myChar;
		Integer myFrequency;
		Node myLeft;
		Node myRight;
		
		@Override
		public int compare(Node firstNode, Node secondNode) {
			return firstNode.myFrequency - secondNode.myFrequency;
		}
		
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("{");
			sb.append(myChar);
			sb.append(", ");
			sb.append(myFrequency);
			sb.append("}");
			return sb.toString();
		}
	}
	
	//Create map of character frequencies
	private void createCharFrequencies() {
		//Map<Character, Integer> charFrequencies = new HashMap<Character, Integer>();
		
		for (int i = 0; i < myMessage.length(); i++) {
			char c = myMessage.charAt(i);
			
			if (myCharFrequencies.containsKey(c)) {
				myCharFrequencies.put(c, myCharFrequencies.get(c) + 1);
			} else {
				myCharFrequencies.put(c, 1);
			}
		}
	}
	
	//Create list of character nodes
	private void createCharNodes() {	
		for (Character c : myCharFrequencies.keySet()) {
			Node charNode = new Node();
			charNode.myChar = c;
			charNode.myFrequency = myCharFrequencies.get(c);
			myNodeList.add(charNode);
		}
	}
	
	//Create Huffman Tree
	private void createTree() {
		while(myNodeList.size() != 1) {
			Collections.sort(myNodeList, new Node());
			
			Node leastFreqNode1 = myNodeList.get(0);
			Node leastFreqNode2 = myNodeList.get(1);
			
			Node weightNode = new Node();
			weightNode.myChar = null;
			weightNode.myFrequency = leastFreqNode1.myFrequency + leastFreqNode2.myFrequency;
			weightNode.myLeft = leastFreqNode1;
			weightNode.myRight = leastFreqNode2;
			
			myNodeList.remove(0);
			myNodeList.remove(0);
			myNodeList.add(0, weightNode);
		}
		myRoot = myNodeList.get(0);
	}
	
	private void createCharCodes() {
		createCharCodes(myRoot);
	}
	
	private void createCharCodes(Node node) {
		if (node.myLeft != null) {
			myCharCode.append("0");
			createCharCodes(node.myLeft);
		}
		
		if (node.myRight != null) {
			myCharCode.append("1");
			createCharCodes(node.myRight);
		}
		
		if (node.myLeft == null && node.myRight == null) {
			codes.put(node.myChar, myCharCode.toString());
			
			if (myCharCode.length() > myEndCode.length()) {
				String endCode = myCharCode.toString().substring(0, myCharCode.length() - 1);
				myEndCode = new StringBuilder(endCode);	
				//codes.put(null, myEndCode.toString());
			}
			
		}
		
		if (myCharCode.length() > 0) {
			myCharCode.deleteCharAt(myCharCode.length() - 1);
		}
		
	}
	
	private void createBits() {
		for (int i = 0; i < myMessage.length(); i++) {
			char c = myMessage.charAt(i);
			String charCode = codes.get(c);
			bits.append(charCode);
		}
	}
	
	public Map<Character, String> getCodes() {
		return codes;
	}
	
	public String getEndCode() {
		return myEndCode.toString();
	}
	
	public String getBits() {
		return bits.toString();
	}
									  /*Find a way to sort the map by value*/
	public String decode(String bits, Map<Character, String> codes) {	
		StringBuilder decodedMessage = new StringBuilder();
		String encodedMessage = bits;
		
		while (encodedMessage.length() != 0) {
			
			for (Character c : codes.keySet()) {
				
				String charCode = codes.get(c);
				String subEncoded = null;
				
				if (charCode.length() <= encodedMessage.length()) {
					subEncoded = encodedMessage.substring(0, charCode.length());
				}
				
				if (codes.containsValue(subEncoded) && charCode.equals(subEncoded)) {
					decodedMessage.append(c);
					encodedMessage = encodedMessage.substring(charCode.length(), encodedMessage.length());
				}
				
			}
		}
		
		return decodedMessage.toString();
	}
}
