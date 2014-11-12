package sdes;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Scanner;

/**
 * SDES
 * 
 * @author (Eugene Fedotov)
 * @author (Jonathan Frederickson)
 * @author (Matthew Ng)
 * @version (October 16, 2014)
 */

public class SDES {

	boolean[] bitArray;
	
	boolean[] K1, K2, IP, key10;
	
	final int[] epvIP = { 1, 5, 2, 0, 3, 7, 4, 6 };
	final int[] epvRIP = { 3, 0, 2, 4, 6, 1, 7, 5 }; // inverse IP
	final int[] epvK1 = { 0, 6, 8, 3, 7, 2, 9, 5 };
	final int[] epvK2 = { 7, 2, 5, 4, 9, 1, 8, 0 };
	
	
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
		if(K1 == null || K2 == null) {
			System.out.println("Enter a 10-bit key:");
			getKey10(new Scanner(System.in));
		}

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
		if(K1 == null || K2 == null) {
			System.out.println("Enter a 10-bit key:");
			getKey10(new Scanner(System.in));
		}
		
		byte[] plain = new byte[cipher.length];

		for (int i = 0; i < cipher.length; i++) {
			plain[i] = decryptByte(cipher[i]);
		}

		return plain;
	}

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
	 * Gets a 10-digit key from a scanner and stores round keys
	 * in K1 and K2
	 * 
	 * @param s
	 *            the scanner to read from
	 * @return a String containing the values read from the scanner
	 */
	public void getKey10(Scanner s) {
		boolean[] result = new boolean[10];
		String keyString = s.nextLine();

		if (keyString.length() != 10) {
			System.err.println("Error: must input 10-bit key");
			return;
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
		K1 = expPerm(result, epvK1);
		K2 = expPerm(result, epvK2);
	}

	/**
	 * 
	 * @param bytes
	 * @return
	 * @throws UnsupportedEncodingException 
	 */
	public static String byteArrayToString(byte[] bytes) throws UnsupportedEncodingException {
		return new String(bytes, "UTF-8");
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
	 * Exclusive OR function. Example: 101 XOR 110 == 011
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

	/**
	 * Concatenates two bit arrays together
	 * 
	 * @param a1
	 * @param a2
	 * 
	 * @return a1 concatenated with a2
	 */
	public static boolean[] concat(boolean[] a1, boolean[] a2) {
		boolean[] result = new boolean[a1.length + a2.length];
		for (int i = 0; i < a1.length; i++) {
			result[i] = a1[i];
		}
		for (int i = a1.length; i < result.length; i++) {
			result[i] = a2[i - a1.length];
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

	/**
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
		
		boolean[][][] s0Block = new boolean[4][4][2];
		boolean[][][] s1Block = new boolean[4][4][2];
		
		s0Block[0][0][0] = false;
		s0Block[0][0][1] = true;
		//--------------------------
		s0Block[0][1][0] = false;
		s0Block[0][1][1] = false;
		//--------------------------
		s0Block[0][2][0] = true;
		s0Block[0][2][1] = true;
		//--------------------------
		s0Block[0][3][0] = true;
		s0Block[0][3][1] = false;
		//--------------------------
		//--------------------------
		s0Block[1][0][0] = true;
		s0Block[1][0][1] = true;
		//--------------------------
		s0Block[1][1][0] = true;
		s0Block[1][1][1] = false;
		//--------------------------
		s0Block[1][2][0] = false;
		s0Block[1][2][1] = true;
		//--------------------------
		s0Block[1][3][0] = false;
		s0Block[1][3][1] = false;
		//--------------------------
		//--------------------------
		s0Block[2][0][0] = false;
		s0Block[2][0][1] = false;
		//--------------------------
		s0Block[2][1][0] = true;
		s0Block[2][1][1] = false;
		//--------------------------
		s0Block[2][2][0] = false;
		s0Block[2][2][1] = true;
		//--------------------------
		s0Block[2][3][0] = true;
		s0Block[2][3][1] = true;
		//--------------------------
		//--------------------------
		s0Block[3][0][0] = true;
		s0Block[3][0][1] = true;
		//--------------------------
		s0Block[3][1][0] = false;
		s0Block[3][1][1] = true;
		//--------------------------
		s0Block[3][2][0] = true;
		s0Block[3][2][1] = true;
		//--------------------------
		s0Block[3][3][0] = true;
		s0Block[3][3][1] = false;
		//--------------------------
		//--------------------------
		
		
		s1Block[0][0][0] = false;
		s1Block[0][0][1] = false;
		//--------------------------
		s1Block[0][1][0] = false;
		s1Block[0][1][1] = true;
		//--------------------------
		s1Block[0][2][0] = true;
		s1Block[0][2][1] = false;
		//--------------------------
		s1Block[0][3][0] = true;
		s1Block[0][3][1] = true;
		//--------------------------
		//--------------------------
		s1Block[1][0][0] = true;
		s1Block[1][0][1] = false;
		//--------------------------
		s1Block[1][1][0] = false;
		s1Block[1][1][1] = false;
		//--------------------------
		s1Block[1][2][0] = false;
		s1Block[1][2][1] = true;
		//--------------------------
		s1Block[1][3][0] = true;
		s1Block[1][3][1] = true;
		//--------------------------
		//--------------------------
		s1Block[2][0][0] = true;
		s1Block[2][0][1] = true;
		//--------------------------
		s1Block[2][1][0] = false;
		s1Block[2][1][1] = false;
		//--------------------------
		s1Block[2][2][0] = false;
		s1Block[2][2][1] = true;
		//--------------------------
		s1Block[2][3][0] = false;
		s1Block[2][3][1] = false;
		//--------------------------
		//--------------------------
		s1Block[3][0][0] = true;
		s1Block[3][0][1] = false;
		//--------------------------
		s1Block[3][1][0] = false;
		s1Block[3][1][1] = true;
		//--------------------------
		s1Block[3][2][0] = false;
		s1Block[3][2][1] = false;
		//--------------------------
		s1Block[3][3][0] = true;
		s1Block[3][3][1] = true;
		//--------------------------
		//--------------------------
		
		boolean[] left = lh(xor(k, expPerm(x, EP)));
		boolean[] right = rh(xor(k, expPerm(x, EP)));
		boolean[] s0 = new boolean[2];
		boolean[] s1 = new boolean[2];;
		
		//**********************************//
		//             S-Box 1              //
		//**********************************//
		
		if (left[0] == false && left[3] == false) // row 1
		{
			if (left[1] == false && left[2] == false) // col 1
			{
				s0[0] = s0Block[0][0][0];
				s0[1] = s0Block[0][0][1];
			}
			
			if (left[1] == false && left[2] == true) // col 2
			{
				s0[0] = s0Block[0][1][0];
				s0[1] = s0Block[0][1][1];
			}
			
			if (left[1] == true && left[2] == false) // col 3
			{
				s0[0] = s0Block[0][2][0];
				s0[1] = s0Block[0][2][1];
			}
			
			if (left[1] == true && left[2] == true) // col 4
			{
				s0[0] = s0Block[0][3][0];
				s0[1] = s0Block[0][3][1];
			}
		}
		
		if (left[0] == false && left[3] == true) // row 2
		{
			if (left[1] == false && left[2] == false) // col 1
			{
				s0[0] = s0Block[1][0][0];
				s0[1] = s0Block[1][0][1];
			}
			
			if (left[1] == false && left[2] == true) // col 2
			{
				s0[0] = s0Block[1][1][0];
				s0[1] = s0Block[1][1][1];
			}
			
			if (left[1] == true && left[2] == false) // col 3
			{
				s0[0] = s0Block[1][2][0];
				s0[1] = s0Block[1][2][1];
			}
			
			if (left[1] == true && left[2] == true) // col 4
			{
				s0[0] = s0Block[1][3][0];
				s0[1] = s0Block[1][3][1];
			}
		}
		
		if (left[0] == true && left[3] == false) // row 3
		{
			if (left[1] == false && left[2] == false) // col 1
			{
				s0[0] = s0Block[2][0][0];
				s0[1] = s0Block[2][0][1];
			}
			
			if (left[1] == false && left[2] == true) // col 2
			{
				s0[0] = s0Block[2][1][0];
				s0[1] = s0Block[2][1][1];
			}
			
			if (left[1] == true && left[2] == false) // col 3
			{
				s0[0] = s0Block[2][2][0];
				s0[1] = s0Block[2][2][1];
			}
			
			if (left[1] == true && left[2] == true) // col 4
			{
				s0[0] = s0Block[2][3][0];
				s0[1] = s0Block[2][3][1];
			}
		}
		
		if (left[0] == true && left[3] == true) // row 4
		{
			if (left[1] == false && left[2] == false) // col 1
			{
				s0[0] = s0Block[3][0][0];
				s0[1] = s0Block[3][0][1];
			}
			
			if (left[1] == false && left[2] == true) // col 2
			{
				s0[0] = s0Block[3][1][0];
				s0[1] = s0Block[3][1][1];
			}
			
			if (left[1] == true && left[2] == false) // col 3
			{
				s0[0] = s0Block[3][2][0];
				s0[1] = s0Block[3][2][1];
			}
			
			if (left[1] == true && left[2] == true) // col 4
			{
				s0[0] = s0Block[3][3][0];
				s0[1] = s0Block[3][3][1];
			}
		}
		
		//**********************************//
		//             S-Box 2              //
		//**********************************//
		
		if (right[0] == false && right[3] == false) // row 1
		{
			if (left[1] == false && left[2] == false) // col 1
			{
				s1[0] = s1Block[0][0][0];
				s1[1] = s1Block[0][0][1];
			}
			
			if (right[1] == false && right[2] == true) // col 2
			{
				s1[0] = s1Block[0][1][0];
				s1[1] = s1Block[0][1][1];
			}
			
			if (right[1] == true && right[2] == false) // col 3
			{
				s1[0] = s1Block[0][2][0];
				s1[1] = s1Block[0][2][1];
			}
			
			if (right[1] == true && right[2] == true) // col 4
			{
				s1[0] = s1Block[0][3][0];
				s1[1] = s1Block[0][3][1];
			}
		}
		
		if (right[0] == false && right[3] == true) // row 2
		{
			if (right[1] == false && right[2] == false) // col 1
			{
				s1[0] = s1Block[1][0][0];
				s1[1] = s1Block[1][0][1];
			}
			
			if (right[1] == false && right[2] == true) // col 2
			{
				s1[0] = s1Block[1][1][0];
				s1[1] = s1Block[1][1][1];
			}
			
			if (right[1] == true && right[2] == false) // col 3
			{
				s1[0] = s1Block[1][2][0];
				s1[1] = s1Block[1][2][1];
			}
			
			if (right[1] == true && right[2] == true) // col 4
			{
				s1[0] = s1Block[1][3][0];
				s1[1] = s1Block[1][3][1];
			}
		}
		
		if (right[0] == true && right[3] == false) // row 3
		{
			if (right[1] == false && right[2] == false) // col 1
			{
				s1[0] = s1Block[2][0][0];
				s1[1] = s1Block[2][0][1];
			}
			
			if (right[1] == false && right[2] == true) // col 2
			{
				s1[0] = s1Block[2][1][0];
				s1[1] = s1Block[2][1][1];
			}
			
			if (right[1] == true && right[2] == false) // col 3
			{
				s1[0] = s1Block[2][2][0];
				s1[1] = s1Block[2][2][1];
			}
			
			if (right[1] == true && right[2] == true) // col 4
			{
				s1[0] = s1Block[2][3][0];
				s1[1] = s1Block[2][3][1];
			}
		}
		
		if (right[0] == true && right[3] == true) // row 4
		{
			if (right[1] == false && right[2] == false) // col 1
			{
				s1[0] = s1Block[3][0][0];
				s1[1] = s1Block[3][0][1];
			}
			
			if (right[1] == false && right[2] == true) // col 2
			{
				s1[0] = s1Block[3][1][0];
				s1[1] = s1Block[3][1][1];
			}
			
			if (right[1] == true && right[2] == false) // col 3
			{
				s1[0] = s1Block[3][2][0];
				s1[1] = s1Block[3][2][1];
			}
			
			if (right[1] == true && right[2] == true) // col 4
			{
				s1[0] = s1Block[3][3][0];
				s1[1] = s1Block[3][3][1];
			}
		}

		boolean[] result = expPerm(concat(s0, s1), p4); // P4(s0 || s1)
		return result;
	}

}
