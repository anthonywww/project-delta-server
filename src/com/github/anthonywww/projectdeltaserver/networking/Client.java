package com.github.anthonywww.projectdeltaserver.networking;

import java.nio.channels.SocketChannel;

public class Client implements IClient {
	
	private Server server;
	private SocketChannel socketChannel;
	private String address;
	private int port;
	
	public Client(Server server, SocketChannel socketChannel) {
		this.server = server;
		this.socketChannel = socketChannel;
		this.address = socketChannel.socket().getInetAddress().getHostAddress();
		this.port = socketChannel.socket().getPort();
		
		
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
	
}
