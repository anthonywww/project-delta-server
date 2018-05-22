package com.github.anthonywww.projectdeltaserver.networking.packets;

import java.io.UnsupportedEncodingException;

import com.github.anthonywww.projectdeltaserver.networking.Packet;
import com.github.anthonywww.projectdeltaserver.networking.PacketHeader;

public class B100DisconnectPacket extends Packet {
	
	private final String message;
	
	public B100DisconnectPacket(byte[] data) throws UnsupportedEncodingException {
		super(PacketHeader.DISCONNECT, data);
		
		if (data != null) {
			message = new String(data, Packet.CHARSET);
		} else {
			message = null;
		}
		
		this.setValid();
	}
	
	public String getDisconnectMessage() {
		return message;
	}
	
}
