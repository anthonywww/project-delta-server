package com.github.anthonywww.projectdeltaserver.networking;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.UUID;
import java.util.logging.Level;
import java.util.regex.Pattern;

import com.github.anthonywww.projectdeltaserver.ProjectDeltaServer;
import com.github.anthonywww.projectdeltaserver.utils.ByteUtils;

public class Client implements IClient {
	
	private UUID uuid;
	private Server server;
	private SocketChannel socketChannel;
	private ByteBuffer writeBuffer;
	private String address;
	private int port;
	private boolean authenticated;
	private long connectionStarted;
	private long lastSyncPacket;
	private int latency;
	private long clientTime;
	
	private String clientName;
	private String clientVersion;
	private int locationX;
	private int locationY;
	private int screenWidth;
	private int screenHeight;
	private String operatingSystem;
	private String cpu;
	private String gpu;
	private int memory;
	
	public Client(Server server, SocketChannel socketChannel) {
		this.uuid = UUID.randomUUID();
		this.server = server;
		this.socketChannel = socketChannel;
		this.writeBuffer = ByteBuffer.allocate(48);
		this.address = socketChannel.socket().getInetAddress().getHostAddress();
		this.port = socketChannel.socket().getPort();
		this.authenticated = false;
		this.connectionStarted = System.currentTimeMillis();
		this.lastSyncPacket = this.connectionStarted;
		this.latency = 0;
		this.clientTime = 0;
		this.locationX = 0;
		this.locationY = 0;
		
		// 1. Expecting client HANDSHAKE with payload of info
		// 2. Send HANDSHAKE_ACCEPT packet with payload of server info
		// 3. Validate variables and set the connection as authenticated (valid)
		
		// Register protocol handlers
		
	}
	
	public void read(byte[] data) {
		
		
		
		
		// FIXME: very rudimentary checks (until packet event system is fully implemented)
		if (data.length >= 1) {
			PacketHeader header = null;
			byte[] payload = null;
			String headerString = "UNKNOWN";
			String payloadString = "";
			
			for (PacketHeader h : PacketHeader.values()) {
				if (data[0] == h.getValue()) {
					header = h;
					headerString = h.name();
					break;
				}
			}
			
			if (data.length > 1) {
				payload = Arrays.copyOfRange(data, 1, data.length);
				payloadString = new String(payload, Charset.forName(Packet.CHARSET));
			}
			
			ProjectDeltaServer.getInstance().print(Level.FINE, "Packet from client: [" + headerString + "] " + payloadString);
			
			
			if (header == PacketHeader.DISCONNECT) {
				disconnect();
				return;
			}
			
			if (header == PacketHeader.HANDSHAKE) {
				// Parse payload
				String[] parts = payloadString.split(Pattern.quote("|"));
				
				if (parts.length != 10) {
					disconnect("Invalid payload handshake");
					return;
				}
				
				try {
					this.clientName = parts[0].trim();
					this.clientVersion = parts[1].trim();
					this.locationX = Integer.parseInt(parts[2].trim());
					this.locationY = Integer.parseInt(parts[3].trim());
					this.screenWidth = Integer.parseInt(parts[4].trim());
					this.screenHeight = Integer.parseInt(parts[5].trim());
					this.operatingSystem = parts[6].trim();
					this.cpu = parts[7].trim();
					this.gpu = parts[8].trim();
					this.memory = Integer.parseInt(parts[9].trim());
				} catch (NumberFormatException e) {
					disconnect("Invalid payload data");
					return;
				}
				
				for (Client c : server.getClients()) {
					if (c.isAuthenticated()) {
						if (c.getLocationX() == this.locationX && c.getLocationY() == this.locationY) {
							disconnect("Coordinates taken");
							return;
						}
					}
				}
				
				ProjectDeltaServer.getInstance().print(Level.INFO, "Client connected (" + getAddress() + ":" + getPort() + "/" + getUUID() + ")");
				send(PacketHeader.HANDSHAKE_ACK, server.getServerInfo());
				this.authenticated = true;
				this.lastSyncPacket = System.currentTimeMillis();
				return;
			}
			
			// Only check these events if the client has authenticated
			if (isAuthenticated()) {
				if (header == PacketHeader.HANDSHAKE) {
					disconnect("Already authenticated once!");
					return;
				}
				
				if (header == PacketHeader.SYNC_ACK) {
					// Parse payload
					
					// TODO: get the delta/latency by subtracting this heartbeat time from the last heartbeat time.
					// TODO: set clientTime = the payload 
					
					
					this.lastSyncPacket = System.currentTimeMillis();
				}
				
			}
		}
		
	}
	
	public synchronized void send(PacketHeader header, byte[] payload) {
		
		if (this.socketChannel == null) {
			ProjectDeltaServer.getInstance().print(Level.WARNING, "Failed to send(header, payload); a socketChannel is null!");
			return;
		}
		
		if (!socketChannel.isConnected()) {
			ProjectDeltaServer.getInstance().print(Level.WARNING, "Failed to send(header, payload); a socketChannel is closed, but not null!");
			return;
		}
		
		// Clear the write buffer to be ready for more data
		writeBuffer.clear();
		writeBuffer.put(new byte[writeBuffer.capacity()]);
		writeBuffer.clear();
		
		// Put data
		writeBuffer.put(header.getValue());
		
		// A payload was provided provided
		if (payload != null) {
			try {
				writeBuffer.put(payload);
			} catch (BufferOverflowException e) {
				// BUFFER OVERFLOW, trying to put too much data into a small buffer
				ProjectDeltaServer.getInstance().handleException(e);
			}
		}
		
		// Change byte order to network byte-order
		writeBuffer.order(ByteOrder.BIG_ENDIAN);
		
		// Change to read mode
		writeBuffer.flip();
		
		// While there's still data in the writeBuffer to pull from
		while (writeBuffer.hasRemaining() && socketChannel.isConnected()) {
		    try {
				socketChannel.write(writeBuffer);
			} catch (IOException e) {
				try {
					socketChannel.close();
				} catch (IOException e2) {}
				break;
			}
		}
		
		// Change to write mode
		writeBuffer.flip();
		
		// Not needed
		//this.socketChannel.socket().getOutputStream().flush();
	}
	
	/**
	 * Send a Sync packet
	 */
	public void heartbeat() {
		long now = System.currentTimeMillis();
		long lastMessageMilliseconds = now - this.lastSyncPacket;
		
		if (this.isAuthenticated()) {
			if (lastMessageMilliseconds > server.getConnectionTimeout()) {
				//disconnect("Connection time-out");
			}
			
			byte[] timeInBytes = ByteUtils.longToBytes(now);
			this.send(PacketHeader.SYNC, timeInBytes);
			
		} else {
			if (lastMessageMilliseconds > server.getAuthTimeout()) {
				disconnect("Authentication time-out");
			}
		}
		
	}
	
	/**
	 * Send a Disconnect packet to this client (close this socket and disconnect)
	 */
	public void disconnect() {
		disconnect(null);
	}
	
	/**
	 * Send a Disconnect packet to this client with a reason (close this socket and disconnect)
	 */
	public synchronized void disconnect(String reason) {
		try {
			if (reason == null) {
				this.send(PacketHeader.DISCONNECT, null);
			} else {
				this.send(PacketHeader.DISCONNECT, reason.getBytes(Packet.CHARSET));
			}
			
			socketChannel.close();
		} catch (IOException e) {
			ProjectDeltaServer.getInstance().handleException(e);
		}
	}
	
	
	public String getUUID() {
		return uuid.toString();
	}
		
	/**
	 * Return the current socket channel
	 * @return
	 */
	protected SocketChannel getSocketChannel() {
		return socketChannel;
	}
	
	/**
	 * Get the client's IP Address
	 * @return
	 */
	public String getAddress() {
		return address;
	}
	
	/**
	 * Get the client's port
	 * @return
	 */
	public int getPort() {
		return port;
	}
	
	/**
	 * Returns true if the client fully authenticated
	 * @return
	 */
	public boolean isAuthenticated() {
		return authenticated;
	}
	
	/**
	 * Returns the timestamp in which the connection started.
	 * @return
	 */
	public long getConnectionStartTime() {
		return connectionStarted;
	}
	
	/**
	 * Return the timestamp of the last sync packet received.
	 * @return
	 */
	public long getLastSyncPacketTime() {
		return lastSyncPacket;
	}
	
	/**
	 * Returns the average latency in milliseconds
	 * @return
	 */
	public int getLatency() {
		return latency;
	}
	
	/**
	 * Get the client's current time in milliseconds
	 * @return
	 */
	public long getClientTime() {
		return clientTime;
	}
	
	/**
	 * Get the computer's X location
	 * @return
	 */
	public int getLocationX() {
		return locationX;
	}
	
	/**
	 *  Get the computer's Y location
	 * @return
	 */
	public int getLocationY() {
		return locationY;
	}
	
	public String getClientName() {
		return clientName;
	}
	
	public String getClientVersion() {
		return clientVersion;
	}
	
	public int getScreenWidth() {
		return screenWidth;
	}
	
	public int getScreenHeight() {
		return screenHeight;
	}
	
	public String getOperatingSystem() {
		return operatingSystem;
	}
	
	public String getCPU() {
		return cpu;
	}
	
	public String getGPU() {
		return gpu;
	}
	
	public int getMemory() {
		return memory;
	}
	
}
