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
//PART 1////////////////////////////////////////////////////////////////////
public class Person {

	private long m;
	private long e;
	private long d;
	RSA rsa;

	/**
	 * Generate a public key for this person, consisting of exponent,e, and
	 * modulus, m. Generate a private key, consisting of an exponent, d. Provide
	 * access to the public key only.
	 */
	public Person() {
		Random rand = new Random();
		long p = RSA.randPrime(357, 46340, rand);
		System.out.println("p: " + p);
		long q = RSA.randPrime(357, 46340, rand);
		System.out.println("q: " + q);
		
		m = p*q; // must be greater than 127127
		System.out.println("p*q (or m): " + m);
		
		long n = (p-1)*(q-1);
		System.out.println("n: " + n);
		
		e = RSA.relPrime(n, rand);
		while(Long.compare(e,50000L) > 0)
		{
			e = RSA.relPrime(n, rand);
		}
		System.out.println("e: " + e);
		
		d = RSA.inverse(e, n);
		System.out.println("d: " + d);
	}

	/**
	 * Encrypt a plain text message to she.
	 * 
	 * @param msg
	 * @param she
	 * @return An array of longs, which is the cipher text
	 */
	public long[] encryptTo(String msg, Person she) {
		if(msg.length() % 2 != 0)
		{
			msg += " ";
		}
		
		long[] chars = new long[msg.length()/2];
		int ndx = 0;
		for(int i = 0; i <= msg.length() - 2; i+=2)
		{
			long twoCharVals = RSA.toLong(msg, i);
			chars[ndx] = (RSA.modPower(twoCharVals, she.getE(), she.getM()));
			ndx++;
		}
		return chars;
	}

	/**
	 * Decrypt the cipher text
	 * 
	 * @param cipher
	 * @return a string of plain text
	 */
	public String decrypt(long[] cipher) {
		String str = "";
		for(int i = 0; i < cipher.length; i++)
		{
			long decrChars = (RSA.modPower(cipher[i], d, m));
			str += RSA.longTo2Chars(decrChars);
		}
		return str;
	}
	
	
	
	
	//////////////////////////////////////////////////////////////////////////////////////////
	
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

}
