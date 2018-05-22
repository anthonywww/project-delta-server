package com.github.anthonywww.projectdeltaserver.networking.packets;

import com.github.anthonywww.projectdeltaserver.networking.Packet;
import com.github.anthonywww.projectdeltaserver.networking.PacketHeader;

public class S100HandshakeAckPacket extends Packet {
	
	private String name;
	private String version;
	private String operatingSystem;
	
	public S100HandshakeAckPacket(byte[] data) {
		super(PacketHeader.HANDSHAKE_ACK, data);
		
	}
	
	
	
}
