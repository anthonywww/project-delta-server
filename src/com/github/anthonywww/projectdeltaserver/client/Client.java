package com.github.anthonywww.projectdeltaserver.client;

import java.net.Socket;

public class Client implements IClient {
	
	private Socket socket;
	
	public Client(Socket socket) {
		this.socket = socket;
	}
	
	
	
	
}
