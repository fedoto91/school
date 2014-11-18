package rsa;

//Project 2
//Driver and class methods for RSA Public Key cryptography

import java.io.*;
import java.math.*;
import java.util.*;	// Random number generator

public class Driver
{
public static void main (String args[])
{ 	
	Person Alice = new Person();
	Person Bob = new Person();

	String msg = new String ("Bob, let's have lunch."); 	// message to be sent to Bob
	long []  cipher;
	cipher =  Alice.encryptTo(msg, Bob);			// encrypted, with Bob's public key

	System.out.println ("Message is: " + msg);
	System.out.println ("Alice sends:");
	RSA.show (cipher);

	System.out.println ("Bob decodes and reads: " + Bob.decrypt (cipher));	// decrypted,
								// with Bob's private key.
	System.out.println ();

	msg = new String ("No thanks, I'm busy");
	cipher = Bob.encryptTo (msg, Alice);
	
	System.out.println ("Message is: " + msg);
	System.out.println ("Bob sends:");
	RSA.show (cipher);

	System.out.println ("Alice decodes and reads: " + Alice.decrypt (cipher));
}




//..........  place class methods here

}	
