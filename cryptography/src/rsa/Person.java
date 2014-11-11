#

package rsa;

import java.io.*;
import java.math.*;
import java.util.*;

/**
 * Person represents a person containing both a public and
 * private key for use with RSA cryptography. Functionality
 * is provided to allow encryption of a message to be sent to
 * another person as well as decryption of a message received
 * from another person.
 * 
 * @authors Eugene Fedotov, Joe Desiderio, Scott Ritchie
 * 
 */
public class Person {

	private long m;
	private long e;
	private long d;

	/**
	 * Generate a public key for this person, consisting of exponent,e, and
	 * modulus, m. Generate a private key, consisting of an exponent, d. Provide
	 * access to the public key only.
	 */
	public Person() {

	}

	/**
	 * Access the public modulus
	 * 
	 * @return The public modulus for this Person
	 */
	public long getM() {
		return m;
	}

	/**
	 * Access the public encryption exponent
	 * 
	 * @return The public encryption exponent for this Person
	 */
	public long getE() {
		return e;
	}

	/**
	 * Encrypt a plain text message to she.
	 * 
	 * @param msg
	 * @param she
	 * @return An array of longs, which is the cipher text
	 */
	public long[] encryptTo(String msg, Person she) {
		return new long[0];
	}

	/**
	 * Decrypt the cipher text
	 * 
	 * @param cipher
	 * @return a string of plain text
	 */
	public String decrypt(long[] cipher) {
		return "";
	}

}
