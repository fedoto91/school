package rsa;

import java.io.*;
import java.math.*;
import java.util.*;

/**
 * Implementation of simple RSA Public Key Cryptography system.
 * In practice, large keys would be employed, using a BigNumber class.
 * For purposes of this project, ints and/or longs are used.
 * A block size of two characters is used when encrypting the plain text.
 * 
 * @authors Eugene Fedotov, Joe Desiderio, Scott Ritchie
 * 
 */
public class RSA {
	public static void main(String args[]) {
		Person Alice = new Person();
		Person Bob = new Person();

		// message to be sent to Bob
		String msg = new String("Bob, let's have lunch.");

		long[] cipher;
		// encrypted, with Bob's public key
		cipher = Alice.encryptTo(msg, Bob);

		System.out.println("Message is: " + msg);
		System.out.println("Alice sends:");
		show(cipher);

		// decrypted, with Bob's private key
		System.out.println("Bob decodes and reads: " + Bob.decrypt(cipher));
		System.out.println();

		msg = new String("No thanks, I'm busy");
		cipher = Bob.encryptTo(msg, Alice);

		System.out.println("Message is: " + msg);
		System.out.println("Bob sends:");
		show(cipher);

		System.out.println("Alice decodes and reads: " + Alice.decrypt(cipher));
	}

	// .......... place class methods here

	/**
	 * Find the multiplicative inverse of a long int
	 * 
	 * @param e
	 * @param m
	 * @return The inverse of e, mod m. Uses the extended Eulidean Algorithm
	 */
	public static long inverse(long e, long m) {
		return (long) 0;
	}

	/**
	 * Display an array of longs on stdout
	 * 
	 * @param cipher
	 */
	public static void show(long[] cipher) {

	}

	/**
	 * Raise a number, b, to a power, p, modulo m
	 * 
	 * @param b
	 * @param p
	 * @param m
	 * @return b^p (mod m)
	 */
	public static long modPower(long b, long p, long m) {
		return (long) 0;
	}

	/**
	 * Find a random prime number
	 * 
	 * @param m
	 * @param n
	 * @param rand
	 * @return A random prime in the range m..n, using rand to generate the
	 *         number
	 */
	public static long randPrime(int m, int n, Random rand) {
		return (long) 0;
	}

	/**
	 * Find a random number relatively prime to a given long int
	 * 
	 * @param n
	 * @param rand
	 * @return a random number relatively prime to n
	 */
	public static long relPrime(long n, Random rand) {
		return (long) 0;
	}

	/**
	 * Convert two numeric chars to long int
	 * 
	 * @param msg
	 * @param p
	 * @return the two digit number beginning at position p of msg as a long
	 *         int.
	 */
	public static long toLong(String msg, int p) {
		return (long) 0;
	}

	/**
	 * Convert a long to 2 chars
	 * 
	 * @param x
	 * @return The string made up two numeric digits representing x
	 */
	public static String longTo2Chars(long x) {
		return "";
	}

}