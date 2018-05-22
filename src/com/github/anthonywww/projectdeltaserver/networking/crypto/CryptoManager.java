package com.github.anthonywww.projectdeltaserver.networking.crypto;

import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.Random;
import java.util.logging.Level;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.github.anthonywww.projectdeltaserver.ProjectDeltaServer;
import com.github.anthonywww.projectdeltaserver.utils.ByteUtils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class CryptoManager {
	
	public static final int CIPHER_SIZE = 4096;
	
	private BASE64Decoder b64d;
	private BASE64Encoder b64e;
	private RSAEncryption rsa;
	private AESEncryption aes;

	public CryptoManager() {
		
		ProjectDeltaServer.getInstance().print(Level.INFO, "Loading cryptographic ciphers ...");
		
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
		
		this.b64d = new BASE64Decoder();
		this.b64e = new BASE64Encoder();
		
		try {
			int maxRSABits = Cipher.getMaxAllowedKeyLength("RSA");
			ProjectDeltaServer.getInstance().print(Level.FINE, "[CryptoManager] Asymmetric RSA max key length is " + maxRSABits + (maxRSABits == Integer.MAX_VALUE ? "-bit (Unlimited Strength Jurisdiction)" : "-bit"));
			
			int maxAESBits = Cipher.getMaxAllowedKeyLength("AES");
			ProjectDeltaServer.getInstance().print(Level.FINE, "[CryptoManager] Symmetric AES max key length is " + maxAESBits + (maxAESBits == Integer.MAX_VALUE ? "-bit (Unlimited Strength Jurisdiction)" : "-bit"));
			
			this.rsa = new RSAEncryption(CIPHER_SIZE);
		} catch (NoSuchAlgorithmException | SecurityException | NoSuchPaddingException | NoSuchProviderException | NullPointerException e) {
			ProjectDeltaServer.getInstance().handleException(e);
		}
		
		String rawString = new String(new Random().nextInt(9999) + "");
		ProjectDeltaServer.getInstance().print(Level.FINEST, "Testing RSA encryption, using string '" + rawString + "' ...");
		
		String encrypted = new String(encryptRSA(rawString.getBytes()), Charset.forName("UTF-8"));
		ProjectDeltaServer.getInstance().print(Level.FINEST, "Encrypted RSA message: '" + encrypted + "'");
		
		String decrypted = new String(decryptRSA(encrypted.getBytes()), Charset.forName("UTF-8"));
		ProjectDeltaServer.getInstance().print(Level.FINEST, "Decrypted RSA message: '" + decrypted + "'");
		
		
		
		
//		rawString = new String(ByteUtils.md5(new String().getBytes()), Charset.forName("UTF-8"));
//		ProjectDeltaServer.getInstance().print(Level.FINEST, "Testing AES encryption, using string '" + rawString + "' ...");
//		
//		encrypted = new String(encryptAES(rawString.getBytes()), Charset.forName("UTF-8"));
//		ProjectDeltaServer.getInstance().print(Level.FINEST, "Encrypted ASES message: '" + rawString + "'");
//		
//		decrypted = new String(decryptAES(encrypted.getBytes()), Charset.forName("UTF-8"));
//		ProjectDeltaServer.getInstance().print(Level.FINEST, "Decrypted AES message: '" + decrypted + "'");
		
	}
	
	/**
	 * Encrypt using RSA an array of bytes
	 * @param data
	 * @return
	 */
	public byte[] encryptRSA(byte[] data) {
		try {
			return rsa.encrypt(data);
		} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NullPointerException e) {
			ProjectDeltaServer.getInstance().handleException(e);
		}
		return null;
	}
	
	/**
	 * Decrypt using RSA an array of byte
	 * @param data
	 * @return
	 */
	public byte[] decryptRSA(byte[] data) {
		try {
			return rsa.decrypt(data);
		} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NullPointerException e) {
			ProjectDeltaServer.getInstance().handleException(e);
		}
		return null;
	}
	
//	/**
//	 * Encrypt using AES an array of bytes
//	 * @param data
//	 * @return
//	 */
//	public byte[] encryptAES(byte[] data) {
//		try {
//			return aes.encrypt(data);
//		} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
//			ProjectDeltaServer.getInstance().handleException(e);
//		}
//		return null;
//	}
//	
//	/**
//	 * Decrypt using AES an array of byte
//	 * @param data
//	 * @return
//	 */
//	public byte[] decryptAES(byte[] data) {
//		try {
//			return aes.decrypt(data);
//		} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
//			ProjectDeltaServer.getInstance().handleException(e);
//		}
//		return null;
//	}
	
	
}
