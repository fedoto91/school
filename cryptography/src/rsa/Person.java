import java.util.Random;
/**
 * Used to test public-key crypto-system, RSA 
 * Generate a public key for this person, consisting of exponent,e, and modulus, m.
 * Generate a private key, consisting of an exponent, d.
 * Provide access to the public key only.
 * Used to test public-key crypto-system, RSA
 * @author Scott
 */
public class Person {

	RSA rsa = new RSA();
	private long mod;
	private long exp;
	private long private_key;
	private long n, p, q;
	
	public Person(){
		Random rand = new Random();
		p = rsa.randPrime();
		q = rsa.randPrime();
		n = (p - 1) * (q - 1);
		mod = p * q;
		exp = rand.nextLong();
		private_key = rsa.inverse(exp, n);
	}
	
	/**
	 * Decrypt the cipher text
	 * @param cipher to decrypt
	 * @return a string of plain text
	 */
	public String decrypt(long[] cipher){
		// TODO this is probably wrong
		String plain_text = "";
		for(long l : cipher){
			plain_text += rsa.longTo2Char(l);
		}
		return plain_text;
	}
	
	/**
	 * Encrypt a plain text message to she.
	 * @param msg plain text
	 * @param she Person you're interacting with
	 * @return An array of longs, which is the cipher text
	 */
	public long[] encryptTo(String msg){
		// Convert to numeric first
		//   then use toLong
		long[] cipher = {};
		String num_string;
		for(int i=0; i < msg.length(); i++){
			char c = msg.charAt(i);
			int num = Character.getNumericValue(c);
			num_string += num;
			// String of 2-digit numeric values
		}
		// Converts each character in numeric form to a long
		//   and adds it to the long array cipher
		for(int i=0; i < num_string.length(); i=i+2){
			cipher.add(rsa.toLong(num_string, i));
		}
		return cipher;
	}
	
	/**
	 * Access the public encryption exponent
	 * @return The public encryption exponent for this Person
	 */
	public long getE(){
		return exp;
	}
	
	/**
	 * Access the public modulus
	 * @return The public modulus for this Person
	 */
	public long getM(){
		return mod;
	}
}
