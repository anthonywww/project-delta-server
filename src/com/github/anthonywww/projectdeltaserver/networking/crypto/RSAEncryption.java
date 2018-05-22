package com.github.anthonywww.projectdeltaserver.networking.crypto;

import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.logging.Level;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import com.github.anthonywww.projectdeltaserver.ProjectDeltaServer;
import com.github.anthonywww.projectdeltaserver.utils.ByteUtils;

public class RSAEncryption {
	
	private int keyLength;
	private Cipher cipher;
	private KeyPairGenerator keyGen;
	private KeyPair pair;
	private PrivateKey privateKey;
	private PublicKey publicKey;
	
	protected RSAEncryption(int keyLength) throws NoSuchAlgorithmException, SecurityException, NoSuchPaddingException, NoSuchProviderException, NullPointerException {
		this.keyLength = keyLength;
		this.cipher = Cipher.getInstance("RSA", "BC");
		this.keyGen = KeyPairGenerator.getInstance("RSA", "BC");
		this.keyGen.initialize(keyLength);
		
		ProjectDeltaServer.getInstance().print(Level.INFO, String.format("[RSAEncryption] Generating a " + keyLength + "-bit RSA keypair ..."));
		this.pair = keyGen.generateKeyPair();
		
		this.privateKey = pair.getPrivate();
		this.publicKey = pair.getPublic();
		
		ProjectDeltaServer.getInstance().print(Level.INFO, String.format("[RSAEncryption] Done! (Format: %s) (SHA-1 %s)", publicKey.getFormat(), new String(ByteUtils.bytesToHex(ByteUtils.sha1(this.publicKey.getEncoded())))));
	}
	
	public byte[] encrypt(byte[] data) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		byte[] encryptedData = null;
		cipher.init(Cipher.ENCRYPT_MODE, publicKey);
		
		ProjectDeltaServer.getInstance().print(Level.FINEST, "[RSAEncryption] Encrypt: BlockSize: " + cipher.getBlockSize());
		
		encryptedData = cipher.doFinal(data);
		return encryptedData;
	}
	
	public byte[] decrypt(byte[] encryptedData) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
		byte[] data = null;
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		
		ProjectDeltaServer.getInstance().print(Level.FINEST, "[RSAEncryption] Decrypt: BlockSize: " + cipher.getBlockSize());
		
		data = cipher.doFinal(encryptedData);
		return data;
	}
	
	public PrivateKey getPrivateKey() {
		return this.privateKey;
	}

	public PublicKey getPublicKey() {
		return this.publicKey;
	}

}
