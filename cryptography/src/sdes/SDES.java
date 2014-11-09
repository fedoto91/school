package sdes;

import java.util.Arrays;
import java.util.Scanner;

/**
 * SDES
 * 
 * @author (Eugene Fedotov)
 * @version (October 16, 2014)
 */

public class SDES {

	boolean[] bitArray;

	/**
	 * Send the byteArray to stdout
	 */
	void show(byte[] byteArray) {
		System.out.println(Arrays.toString(byteArray));
	}

	/**
	 * Send the bitArray to stdout as 1's and 0's
	 */
	void show(boolean[] inp) {
		System.out.println(Arrays.toString(inp));
	}

	/**
	 * Encrypt the given string using SDES Each character produces a byte of
	 * cipher.
	 * 
	 * @param msg
	 *            the plaintext you want to encrypt
	 * @return the encrypted plaintext as a series of bytes
	 */
	byte[] encrypt(String msg) {
		System.out.println("Enter a 10-bit key:");
		key10 = getKey10(new Scanner(System.in));
		K1 = expPerm(key10, epvK1);
		K2 = expPerm(key10, epvK2);
		byte[] block = msg.getBytes();

		for (int i = 0; i < block.length; i++) {
			block[i] = encryptByte(block[i]);
		}

		return block;
	}

	/**
	 * Decrypt the given byte array.
	 * 
	 * @param cipher
	 *            the bytes you want to decrypt
	 * @return decrypted bytes
	 */
	byte[] decrypt(byte[] cipher) {
		byte[] plain = new byte[cipher.length];

		for (int i = 0; i < cipher.length; i++) {
			plain[i] = decryptByte(cipher[i]);
		}

		return plain;
	}

	boolean[] K1, K2, IP, key10;

	int[] epvIP = { 1, 5, 2, 0, 3, 7, 4, 7 };
	int[] epvRIP = { 3, 0, 2, 4, 6, 1, 7, 5 }; // inverse IP
	int[] epvK1 = { 0, 6, 8, 3, 7, 2, 9, 5 };
	int[] epvK2 = { 7, 2, 5, 4, 9, 1, 8, 0 };

	/**
	 * Encrypt a single byte using SDES
	 * 
	 * @param b
	 *            one byte you want to encrypt return the encrypted byte
	 */
	byte encryptByte(byte b) {
		bitArray = byteToBitArray(b);

		IP = expPerm(bitArray, epvIP);

		bitArray = f(IP, K1); // round 1
		bitArray = concat(rh(bitArray), lh(bitArray)); // switch
		bitArray = f(bitArray, K2); // round 2

		return bitArrayToByte(expPerm(bitArray, epvRIP));
	}

	/**
	 * Decrypt a single byte using SDES
	 * 
	 * @param b
	 *            one byte you want to decrypt return the decrypted byte
	 */
	byte decryptByte(byte b) {

		bitArray = byteToBitArray(b);
		IP = expPerm(bitArray, epvIP);
		bitArray = f(IP, K2); // round 1
		bitArray = concat(rh(bitArray), lh(bitArray)); // switch
		bitArray = f(bitArray, K1); // round 2

		return bitArrayToByte(expPerm(bitArray, epvRIP));
	}

	/**
	 * Extract a permutation of the input array based on a permutation vector
	 * "epv".
	 * 
	 * @param in
	 * @param epv
	 * @return
	 */
	public static boolean[] expPerm(boolean[] in, int[] epv) {
		boolean[] result = new boolean[epv.length];
		for (int i = 0; i < epv.length; i++) {
			result[i] = in[epv[i]];
		}
		return result;
	}

	/**
	 * Gets a 10-digit key from a scanner. TODO: Further testing - I'm not very
	 * familiar with scanners
	 * 
	 * @param s
	 *            the scanner to read from
	 * @return a String containing the values read from the scanner
	 */
	public static boolean[] getKey10(Scanner s) {
		boolean[] result = new boolean[10];
		String keyString = s.nextLine();

		if (keyString.length() != 10) {
			System.err.println("Error: must input 10-bit key");
			return null;
		}

		for (int i = 0; i < 10; i++) {
			switch (keyString.charAt(i)) {
			case '1':
				result[i] = true;
				break;
			case '0':
				result[i] = false;
				break;
			default:
				System.err.println("Invalid digit");
				break;
			}
		}
		return result;
	}

	/**
	 * 
	 * @param bytes
	 * @return
	 */
	public static String byteArrayToString(byte[] bytes) {
		StringBuilder build = new StringBuilder();
		for (byte b : bytes) {
			build.append(b);
		}
		return build.toString();
	}

	/**
	 * Extract only left half of an array
	 * 
	 * @param in
	 * @return
	 */
	public static boolean[] lh(boolean[] in) {
		int midpoint = in.length / 2;
		boolean[] result;

		// Compensate for the possibility of odd array lengths
		if (in.length % 2 == 1) {
			result = new boolean[in.length - (midpoint + 1)];
		} else
			result = new boolean[in.length - midpoint];
		for (int i = 0; i < result.length; i++) {
			result[i] = in[i];
		}
		return result;
	}

	/**
	 * Extract only right half of an array
	 * 
	 * @param in
	 * @return
	 */
	public static boolean[] rh(boolean[] in) {
		int midpoint = in.length / 2;
		boolean[] result = new boolean[in.length - midpoint];
		int arrayCount = 0;
		for (int i = midpoint; i < in.length; i++) {
			result[arrayCount] = in[i];
			arrayCount++;
		}
		return result;
	}

	/**
	 * Exclusive OR function. Example: 101 XOR 110 == 011 TODO: Put a truth
	 * table here maybe?
	 * 
	 * @param a1
	 * @param a2
	 * @return a1 XOR a2
	 */
	public static boolean[] xor(boolean[] a1, boolean[] a2) {
		if (a1.length != a2.length)
			return null;
		boolean[] result = new boolean[a1.length];
		for (int i = 0; i < a1.length; i++) {
			result[i] = a1[i] ^ a2[i];
		}
		return result;
	}

	public static boolean[] concat(boolean[] a1, boolean[] a2) {
		boolean[] result = new boolean[a1.length + a2.length];
		for (int i = 0; i < a1.length; i++) {
			result[i] = a1[i];
		}
		for (int j = a1.length; j < result.length; j++) {
			result[j] = a2[j - 4];
		}
		return result;
	}

	/**
	 * Converts a bit array (an array of booleans) into a byte
	 * 
	 * @param bits
	 *            an array of boolean variables representing bits
	 * @return the parameter in byte form
	 */
	public static byte bitArrayToByte(boolean[] bits) {
		if (bits.length > 8)
			throw new IllegalArgumentException();

		// Loop starting from the LSB
		byte result = 0;
		int i = bits.length - 1;
		byte mask = (byte) 0b00000001;
		while (i >= 0) {
			// If the bit at this position is true, logical AND the mask with
			// the result. The mask will have a bit at the same position as
			// the current array position.
			if (bits[i]) {
				// If we were right-shifting anywhere, we would need to
				// AND with 0xFF to avoid sign weirdness
				result = (byte) (result | mask);
			}

			// Shift the mask left, decrement array position
			mask = (byte) (mask << 1);
			i--;
		}
		return result;
	}

	/**
	 * Converts a byte into a bit array (array of booleans)
	 * 
	 * @param b
	 * @return
	 */
	public static boolean[] byteToBitArray(byte b) {
		// This pretty much works like bitArrayToByte above
		// TODO: Comment this better
		boolean[] result = new boolean[8];
		byte mask = (byte) 0b00000001;
		for (int i = 7; i >= 0; i--) {
			if ((byte) (b & mask) != 0) {
				result[i] = true;
			} else
				result[i] = false;
			mask = (byte) (mask << 1);
		}
		return result;
	}

	/*
	 * This is the 'round' function.
	 * 
	 * @param The message to be encrypted
	 * 
	 * @param The key to be used.
	 * 
	 * @return An array of encrypted boolean values
	 */
	public static boolean[] f(boolean[] x, boolean[] k) {
		boolean[] XOR;
		boolean[] result;

		XOR = xor(lh(x), feistel(k, rh(x))); // L(x) XOR F(k, R(X))
		result = concat(XOR, rh(x)); // XOR || R(X)
		return result;
	}

	/*
	 * F(k,x) is a Feistel function F(k,x) = P4 (s0 (L (k xor EP(x))) || s1 (R
	 * (k xor EP(x)))
	 * 
	 * @param The key used.
	 * 
	 * @param The message to be encrypted.
	 * 
	 * @return An array of encrpyted boolean values.
	 */
	/*
	 * F(k,x) is a Feistel function F(k,x) = P4 (s0 (L (k xor EP(x))) || s1 (R
	 * (k xor EP(x)))
	 * 
	 * @param The key used.
	 * 
	 * @param The message to be encrypted.
	 * 
	 * @return An array of encrpyted boolean values.
	 */

	public static boolean[] feistel(boolean[] k, boolean[] x) {
		int[] p4 = { 1, 3, 2, 0 };
		int[] EP = { 3, 0, 1, 2, 1, 2, 3, 0 };
		String[][] s0Block = { { "X", "00", "01", "10", "11" },
				{ "00", "01", "00", "11", "10" },
				{ "01", "11", "10", "01", "00" },
				{ "10", "00", "10", "01", "11" },
				{ "11", "11", "01", "11", "10" } };
		String[][] s1Block = { { "X", "00", "01", "10", "11" },
				{ "00", "00", "01", "10", "11" },
				{ "01", "10", "00", "01", "11" },
				{ "10", "11", "00", "01", "00" },
				{ "11", "10", "01", "00", "11" } };
		// How do you do S-blocks?
		boolean[] s0 = lh(xor(k, expPerm(x, EP))); // s0(L(k XOR EP(x))
		boolean[] s1 = rh(xor(k, expPerm(x, EP))); // s1(R(k XOR EP(x))

		String[] s0Str = { "", "" };
		String[] s1Str = { "", "" };
		String s0BlockStr = "";
		String s1BlockStr = "";
		for (int i = 0; i < s0.length / 2; i++) { // Grab the first two bytes of
													// s0
			if (s0[i] == true)
				s0Str[0] = s0Str[0] + "1";
			else if (s0[i] == false)
				s0Str[0] = s0Str[0] + "0";
		}
		for (int i = s0.length / 2; i < s0.length; i++) { // grab the last two
															// bytes of s0
			if (s0[i] == true)
				s0Str[1] = s0Str[1] + "1";
			else if (s0[i] == false)
				s0Str[1] = s0Str[1] + "0";
		}
		for (int i = 0; i < s1.length / 2; i++) { // Grab the first two bytes of
													// s1
			if (s1[0] == true)
				s1Str[0] = s1Str[0] + "1";
			else if (s1[1] == false)
				s1Str[0] = s1Str[0] + "0";
		}
		for (int i = s1.length / 2; i < s1.length; i++) { // Grab the last two
															// bytes of s1
			if (s1[i] == true)
				s1Str[1] = s1Str[1] + "1";
			else if (s1[i] == false)
				s1Str[1] = s1Str[1] + "0";
		}

		for (int i = 0; i < 5; i++)
			// Use the first two bytes of s0 to find the correct column
			if (s0Str[0].equals(s0Block[0][i])) // Use the last two bytes of s0
												// to find the correct row
				for (int y = 0; y < 5; y++)
					// Set s0BlockStr to the string in the relative position of
					// the s block
					if (s0Str[1].equals(s0Block[y][0]))
						s0BlockStr = s0Block[y][i];

		for (int i = 0; i < 5; i++)
			if (s1Str[0].equals(s1Block[0][i]))
				for (int y = 0; y < 5; y++)
					if (s1Str[1].equals(s1Block[y][0]))
						s1BlockStr = s1Block[y][i];

		for (int i = 0; i < s0BlockStr.length(); i++) { // Change all the string
														// values of the
														// s0BlockStr to boolean
														// values
			if (s0BlockStr.charAt(i) == '1') // and throw them back into s0 and
												// s1.
				s0[i] = true;
			else if (s0BlockStr.charAt(i) == '0')
				s0[i] = false;
		}

		for (int i = 0; i < s1BlockStr.length(); i++) {
			if (s1BlockStr.charAt(i) == '1')
				s1[i] = true;
			else if (s1BlockStr.charAt(i) == '0')
				s1[i] = false;
		}

		boolean[] result = expPerm(concat(s0, s1), p4); // P4(s0 || s1)
		return result;
	}

}
