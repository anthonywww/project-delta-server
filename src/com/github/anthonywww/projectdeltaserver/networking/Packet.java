package com.github.anthonywww.projectdeltaserver.networking;

import java.io.UnsupportedEncodingException;

import com.github.anthonywww.projectdeltaserver.ProjectDeltaServer;

public abstract class Packet {
	
	public static final String CHARSET = "UTF-8";
	public static final String DELIMITER = "|";
	public static final String DELIMITER_ESCAPE = "\\|";
	private final PacketHeader header;
	protected final byte[] data;
	private boolean valid;
	
	public Packet(PacketHeader header, byte[] data) {
		this.header = header;
		this.data = data;
		this.valid = false;
	}
	
	/**
	 * Mark the packet as valid
	 * @param invalid
	 */
	public void setValid() {
		this.valid = false;
	}
	
	/**
	 * Is this packet valid (not missing/corrupt data)
	 * @return
	 */
	public boolean isValid() {
		return valid;
	}
	
	/**
	 * Get the packet header of this packet
	 * @return
	 */
	public final PacketHeader getHeader() {
		return header;
	}
	
	/**
	 * Get the raw byte[] data of this packet
	 */
	public final byte[] getData() {
		return data;
	}
	
	/**
	 * Get a textual representation of the packet
	 */
	@Override
	public final String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('<');
		if (!valid) {
			sb.append("Invalid");
		}
		sb.append("Packet>");
		sb.append(this.getClass().getSimpleName());
		sb.append("[");
		sb.append(header.name());
		sb.append("|");
		sb.append(data.length);
		sb.append("|");
		
		try {
			sb.append(new String(data, "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			ProjectDeltaServer.getInstance().handleException(e);
		}
		
		sb.append("]");
		
		return sb.toString();
	}
	
	
}
