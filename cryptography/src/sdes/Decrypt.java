package sdes;
import java.util.Scanner;
import java.io.UnsupportedEncodingException;

public class Decrypt {
	public static void main(String[] args) throws UnsupportedEncodingException {
		SDES sdes = new SDES();
		byte[] bytes = new byte[] {-115,-17,-47,-113,-43,-47,15,84,-43,-113,-17,84,-43,79,58,15,64,-113,-43,65,-47,127,84,64,-43,-61,79,-43,93,-61,-14,15,-43,-113,84,-47,127,-43,127,84,127,10,84,15,64,43};
		sdes.getKey10(new Scanner("0111111101"));
		System.out.println(sdes.byteArrayToString(sdes.decrypt(bytes)));
	}
}
