package com.github.anthonywww.projectdeltaserver.utils;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.github.anthonywww.projectdeltaserver.ProjectDeltaServer;

/**
 * Utility class for bytes and buffers
 */
public abstract class ByteUtils {

	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();
	private static ByteBuffer intBuffer = ByteBuffer.allocate(Integer.BYTES);
	private static ByteBuffer longBuffer = ByteBuffer.allocate(Long.BYTES);

	// Double prevent instantiation
	private ByteUtils() {}

	/**
	 * Convert a int to a byte array
	 * @param x
	 * @return
	 */
	public static byte[] intToBytes(int x) {
		intBuffer.clear();
		intBuffer.putLong(x);
		return longBuffer.array();
	}

	/**
	 * Convert a array of bytes to a int
	 * @param bytes
	 * @return
	 */
	public static long bytesToInt(byte[] bytes) {
		intBuffer.clear();
		intBuffer.put(bytes);
		intBuffer.flip(); // needs to be flipped
		return intBuffer.getLong();
	}
	
	/**
	 * Convert a long to a array of bytes
	 * @param x
	 * @return
	 */
	public static byte[] longToBytes(long x) {
		longBuffer.clear();
		longBuffer.putLong(x);
		return longBuffer.array();
	}

	/**
	 * Convert a array of bytes to a long
	 * @param bytes
	 * @return
	 */
	public static long bytesToLong(byte[] bytes) {
		longBuffer.clear();
		longBuffer.put(bytes);
		longBuffer.flip(); // needs to be flipped
		return longBuffer.getLong();
	}
	
	/**
	 * Hash bytes using the SHA-512 algorithm
	 * @param data
	 * @return
	 */
	public static byte[] sha512(byte[] data) {
		MessageDigest digest = null;
		
		try {
			digest = MessageDigest.getInstance("SHA-512");
		} catch (NoSuchAlgorithmException e) {
			ProjectDeltaServer.getInstance().handleException(e);
		}
		
		return digest.digest(data);
	}
	
	/**
	 * Hash bytes using the SHA-256 algorithm
	 * @param data
	 * @return
	 */
	public static byte[] sha256(byte[] data) {
		MessageDigest digest = null;
		
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			ProjectDeltaServer.getInstance().handleException(e);
		}
		
		return digest.digest(data);
	}
	
	/**
	 * Hash bytes using the SHA-1 algorithm
	 * @param data
	 * @return
	 */
	public static byte[] sha1(byte[] data) {
		MessageDigest digest = null;
		
		try {
			digest = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			ProjectDeltaServer.getInstance().handleException(e);
		}
		
		return digest.digest(data);
	}
	
	/**
	 * Hash bytes using the MD5 algorithm
	 * @param data
	 * @return
	 */
	public static byte[] md5(byte[] data) {
		MessageDigest digest = null;
		
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			ProjectDeltaServer.getInstance().handleException(e);
		}
		
		return digest.digest(data);
	}
	
	/**
	 * Convert a array of bytes into a hexadecimal char array
	 * @param bytes to convert to hex
	 * @return
	 */
	public static char[] bytesToHex(byte[] b) {
		char[] hexChars = new char[b.length * 2];
		for (int j = 0; j < b.length; j++) {
			int v = b[j] & 0xFF;
			hexChars[j * 2] = HEX_ARRAY[v >>> 4];
			hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
		}
		return hexChars;
	}
	
	
	
}
