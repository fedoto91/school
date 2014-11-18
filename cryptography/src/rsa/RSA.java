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

	//PART2 - Joe///////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Find the multiplicative inverse of a long int
	 * Uses the extended Euclidean Algorithm.
	 * 
	 * @param e, the number for which to find the multiplicative inverse
	 * @param m, the relative modulus
	 * @return The multiplicative inverse of e, mod m
	 */
	public static long inverse(long e, long m) {
		if(e == 1)
			return m+1;
		
		// setup
		long r2Prev = m;
		long rPrev = e;
		long u2Prev = 0;
		long uPrev = 1;
		long rCur = 0;
		long qCur = 0;
		long uCur = 0;

		// calculate "table"
		while(rCur != 1) {
			//calculate the current line of the table
			rCur = r2Prev % rPrev;
			qCur = r2Prev / rPrev;
			uCur = u2Prev - uPrev * qCur;
			
			// shift all the values for the next line
			r2Prev = rPrev;
			rPrev = rCur;
			u2Prev = uPrev;
			uPrev = uCur;
		}
		
		// if negative, add mod value to make positive
		if (uCur < 0)
			return uCur + m;
		
		return uCur;
	}
	
	/**
	 * Raise a number, b, to a power, p, modulo m
	 * 
	 * @param b, the base number to raise to the given power
	 * @param p, the power to raise the given base to
	 * @param m, the modulo to be used 
	 * @return b^p (mod m)
	 */
	public static long modPower(long b, long p, long m) {
		
		if(m == 0)
		{
			System.out.print("Error! Modulo must be greater than zero: ");
			return -1;
		}
		if(p == 0)
			return 1 % m;
		if(p == 1)
			return b % m;
		
		if(b > m)
		{
			b = b % m;
		}
		
		long reducedP;
		if(m > 1)
		{
			reducedP = p % (m-1);
			if(reducedP == 0)
				return 1;
		}
		else
			reducedP = 0;
		
		long result = (b * b) % m;
		for (int i = 2; i < reducedP; i++)
		{
			result = (result * b) % m;
		}
		return result;
	}
	
	//End-PART2///////////////////////////////////////////////////////////////////////////////////////////

	/**
	 * Display an array of longs on stdout
	 * 
	 * @param cipher
	 */
	public static void show(long[] cipher) {
		System.out.println(cipher);
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
		long rNum = rand.nextInt(n) + m;
		// primality test
		boolean isPrime = false;
		while (!isPrime){
			if (rNum <= 1){
				rNum = rand.nextInt(n) + m;
			}
			else if (rNum % 2 == 0 || rNum % 3 == 0){
				rNum = rand.nextInt(n) + m;
			}
			else{
				for (int k = 0; 6 * k + 1 <= Math.sqrt(rNum); k++){
					if (rNum == (6 * k + 1)){
						isPrime = true;
					}
				}
				rNum = rand.nextInt(n) + m;
			}
		}	
		return rNum;	
	}

	/**
	 * Find a random number relatively prime to a given long int
	 * 
	 * @param n
	 * @param rand
	 * @return a random number relatively prime to n
	 */
	public static long relPrime(long n, Random rand) {
		long rNum = rand.nextInt();
		return inverse(n, rNum);
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
		return Long.parseLong(Character.toString(msg.charAt(p)) + Character.toString(msg.charAt(p + 1)));
	}

	/**
	 * Convert a long to 2 chars
	 * 
	 * @param x
	 * @return The string made up two numeric digits representing x
	 */
	public static String longTo2Chars(long x) {
		return String.valueOf(x);
	}

}
