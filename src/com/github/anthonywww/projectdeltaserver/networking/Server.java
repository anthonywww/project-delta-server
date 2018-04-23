package com.github.anthonywww.projectdeltaserver.networking;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;
import java.util.HashSet;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Server {

	private String address;
	private int port;
	private ServerSocketChannel serverChannel;
	private Selector selector;
	private ByteBuffer readBuffer;
	private ExecutorService executorService;
	private HashSet<Client> clients;

	public Server(String address, int port) throws IOException {
		this.address = address;
		this.port = port;
		this.selector = initSelector();
		this.readBuffer = ByteBuffer.allocateDirect(2048);
		this.executorService = Executors.newFixedThreadPool(4);
		this.clients = new HashSet<Client>();
		
		
		// Submit lambda task to executorService
		this.executorService.submit(() -> {
			try {
				while (executorService.isShutdown() && selector.isOpen()) {
					
					// Wait for an event one of the registered channels
					this.selector.select();
	
					// Iterate over the set of keys for which events are available
					Iterator<SelectionKey> selectedKeys = this.selector.selectedKeys().iterator();
					
					while (selectedKeys.hasNext()) {
						
						SelectionKey key = (SelectionKey) selectedKeys.next();
						
						// Remove the last key from the selectedKeys
						selectedKeys.remove();
	
						if (!key.isValid()) {
							continue;
						}
	
						// Check what event is available and deal with it
						if (key.isAcceptable()) {
							this.handleAccept(key);
							
						} else if (key.isReadable()) {
							this.handleRead(key);
							
						}
					}
				}
			} catch (IOException e) {
				// FIXME: Handle exception
				e.printStackTrace();
			}
		});
		
		
		// TODO: Log server started
	}
	
	
	/**
	 * Shutdown the internal server
	 */
	public void shutdown() {
		// TODO: Log server shutting down/terminating ...
		// TODO: Broadcast disconnect packet to all active clients
		// TODO: Disconnect all clients
		
		
		
		
		// Issue executorService shutdown
		executorService.shutdownNow();

		try {
			if (!executorService.isTerminated()) {
				executorService.awaitTermination(1000, TimeUnit.MILLISECONDS);
			}
		} catch (InterruptedException e) {
			// Discard exception
		}
		
		// Clear clients
		clients.clear();
		
		
		// TODO: Log server shutdown
	}
	
	/**
	 * Get the current address the server is bound to
	 * @return
	 */
	public String getAddress() {
		return address;
	}
	
	/**
	 * Get the current port the server is running on
	 * @return
	 */
	public int getPort() {
		return port;
	}

	/**
	 * Returns true if the server is running
	 * 
	 * @return
	 */
	public boolean isRunning() {
		return !executorService.isShutdown();
	}
	
	/**
	 * Get a HashSet of clients
	 * @return
	 */
	public HashSet<Client> getClients() {
		return clients;
	}
	
	
	/**
	 * Create the selector and non-blocking server socket channels
	 * 
	 * @return
	 * @throws IOException
	 */
	private Selector initSelector() throws IOException {
		// Create a new selector
		Selector socketSelector = SelectorProvider.provider().openSelector();

		// Create a new non-blocking server socket channel
		this.serverChannel = ServerSocketChannel.open();
		serverChannel.configureBlocking(false);

		// Bind the server socket to the specified address and port
		InetSocketAddress isa = new InetSocketAddress(this.address, this.port);
		serverChannel.socket().bind(isa);

		// Register the server socket channel, indicating an interest in accepting new connections
		serverChannel.register(socketSelector, SelectionKey.OP_ACCEPT);

		return socketSelector;
	}
	
	
	
	private void handleAccept(SelectionKey key) throws IOException {
		// For an accept to be pending the channel must be a server socket channel.
		ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();

		// Accept the connection and make it non-blocking
		SocketChannel socketChannel = serverSocketChannel.accept();
		//Socket socket = socketChannel.socket();
		socketChannel.configureBlocking(false);

		// Register the new SocketChannel with our Selector, indicating we'd like to be notified when there's data waiting to be read
		socketChannel.register(this.selector, SelectionKey.OP_READ);
	}
	
	
	
	private void handleRead(SelectionKey key) throws IOException {
		SocketChannel socketChannel = (SocketChannel) key.channel();

		// Clear out our read buffer so it's ready for new data
		this.readBuffer.clear();

		// Attempt to read off the channel
		int numRead = -1;
		
		try {
			numRead = socketChannel.read(this.readBuffer);
			
		} catch (IOException e) {
			// The remote forcibly closed the connection, cancel the selection key and close the channel.
			key.cancel();
			socketChannel.close();
			// FIXME: If the connection was a client, remove them from the hashset and emit a disconnect event
			return;
		}

		if (numRead == -1) {
			// Remote entity shut the socket down cleanly. Do the same from our end and cancel the channel.
			key.channel().close();
			key.cancel();
			return;
		}

		// Hand the data off to worker
		//processData(this, socketChannel, this.readBuffer.array(), numRead);
	}
	
	
	
	
	
	
	

}
