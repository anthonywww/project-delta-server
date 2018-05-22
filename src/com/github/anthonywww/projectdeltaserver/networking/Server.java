package com.github.anthonywww.projectdeltaserver.networking;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import com.github.anthonywww.projectdeltaserver.ProjectDeltaServer;
import com.sun.org.apache.bcel.internal.generic.RETURN;

public class Server {

	private String address;
	private int port;
	private ServerSocketChannel serverChannel;
	private Selector selector;
	private ByteBuffer readBuffer;
	private ExecutorService executorService;
	private ScheduledExecutorService heartbeatService;
	private HashSet<Client> clients;
	private int authTimeout;
	private int connectionTimeout;
	private long lastHeartBeat;
	private int maxClients;
	
	public Server(String address, int port) throws IOException {
		this.address = address;
		this.port = port;
		this.readBuffer = ByteBuffer.allocate(4096);
		this.executorService = Executors.newSingleThreadExecutor();
		this.heartbeatService = Executors.newSingleThreadScheduledExecutor();
		this.clients = new HashSet<Client>();
		this.lastHeartBeat = System.currentTimeMillis();
		this.maxClients = ProjectDeltaServer.getInstance().getConfiguration().getAsInt(ProjectDeltaServer.ConfigKey.MAX_CLIENTS.id);
	}
	
	
	public void start() {
		
		// Initialize
		try {
			// Create a new selector
			selector = Selector.open();
	
			// Create a new non-blocking server socket channel
			serverChannel = ServerSocketChannel.open();
			serverChannel.configureBlocking(false);
	
			// Bind the server socket to the specified address and port
			InetSocketAddress isa = new InetSocketAddress(this.address, this.port);
			serverChannel.socket().bind(isa);
	
			// Register the server socket channel, indicating an interest in accepting new connections
			serverChannel.register(selector, SelectionKey.OP_ACCEPT);
		} catch (IOException e) {
			ProjectDeltaServer.getInstance().handleException(e);
			return;
		}
		
		// Submit a task to be periodically executed
		heartbeatService.scheduleAtFixedRate(() -> {
			heartbeat();
		}, 0L, ProjectDeltaServer.getInstance().getConfiguration().getAsInt(ProjectDeltaServer.ConfigKey.HEARTBEAT_INTERVAL.id), TimeUnit.MILLISECONDS);
		
		// Submit lambda task to executorService
		executorService.submit(() -> {
			try {
				while (!executorService.isShutdown()) {
					
					// Wait for an event one of the registered channels
					selector.select();

					// Iterate over the set of keys for which events are available
					Set<SelectionKey> selectedKeys = selector.selectedKeys();
					Iterator<SelectionKey> iterator = selectedKeys.iterator();
					
					while (iterator.hasNext()) {

						SelectionKey key = iterator.next();
						
						if (key.isAcceptable()) {
							register(selector, serverChannel);
						}

						if (key.isReadable()) {
							handleRead(key);
						}
						
						iterator.remove();
					}
				}
			} catch (IOException e) {
				ProjectDeltaServer.getInstance().handleException(e);
			}
		});
		
		ProjectDeltaServer.getInstance().print(Level.INFO, "Server started! (on §a" + address + ":" + port + "§r)");
	}
	
	/**
	 * Shutdown the internal server
	 */
	public synchronized void shutdown() {
		ProjectDeltaServer.getInstance().print(Level.INFO, "Shutting down internal server ...");
		
		// Disconnect all clients
		for(Client c : clients) {
			c.disconnect();
		}
		
		// Issue executorService shutdown
		executorService.shutdownNow();
		heartbeatService.shutdownNow();
		
		try {
			if (!executorService.isTerminated()) {
				executorService.awaitTermination(1000, TimeUnit.MILLISECONDS);
			}
		} catch (InterruptedException e) {
			// Discard exception
		}
		
		// Clear clients
		clients.clear();
		
		ProjectDeltaServer.getInstance().print(Level.INFO, "Internal server closed");
	}
	
	
	public synchronized void broadcast(PacketHeader header, byte[] payload) {
		for (Client c : clients) {
			c.send(header, payload);
		}
	}
	
	/**
	 * Called periodically automatically
	 */
	private void heartbeat() {
		int rate = (int) ((System.currentTimeMillis() - lastHeartBeat)/1e0);
		lastHeartBeat = System.currentTimeMillis();
		ProjectDeltaServer.getInstance().print(Level.FINEST, "Heartbeat! (" + rate + " ms) " + clients.size() + " clients connected.");
		
		// For each client
		Iterator<Client> iterator = clients.iterator();
		while (iterator.hasNext()) {
			Client c = iterator.next();

			// If the client is no longer connected, remove them
			if (!c.getSocketChannel().isConnected()) {
				iterator.remove();
				//clients.remove(c);
				continue;
			}
			
			c.heartbeat();
		}
		
	}
	
	
	private void register(Selector selector, ServerSocketChannel serverChannel) throws IOException {
		
		// Accept the connection and make it non-blocking
		SocketChannel clientChannel = serverChannel.accept();
		
		if (clients.size() >= maxClients) {
			clientChannel.close();
		}
		
		clientChannel.socket().setSoTimeout(connectionTimeout);
		clientChannel.socket().setTcpNoDelay(true);
		clientChannel.configureBlocking(false);

		// Register the new SocketChannel with our Selector, indicating we'd like to be notified when there's data waiting to be read
		clientChannel.register(selector, SelectionKey.OP_READ);
		
		ProjectDeltaServer.getInstance().print(Level.FINE, "Incoming connection (" + clientChannel.socket().getInetAddress().getHostAddress() + ":" + clientChannel.socket().getPort() + ")");
		
		this.clients.add(new Client(this, clientChannel));
	}
	
	
	private void handleRead(SelectionKey key) throws IOException {
		SocketChannel clientChannel = (SocketChannel) key.channel();
		
		// Attempt to read off the channel
		int numRead = -1;
		
		Client client = null;
		
		// Get the client instance by socket channel look-up
		for (Client c : clients) {
			if (c.getSocketChannel() == clientChannel) {
				client = c;
			}
		}
		
		if (client == null) {
			clientChannel.close();
			return;
		}
		
		// Clear the buffer
		readBuffer.clear();
		readBuffer.put(new byte[readBuffer.capacity()]);
		readBuffer.clear();
		
		try {
			numRead = clientChannel.read(readBuffer);
		} catch (IOException e) {
			// The remote peer forcibly closed the connection, cancel the selection key and close the channel.
			ProjectDeltaServer.getInstance().print(Level.FINE, "Forcefully closed connection of client " + (client.isAuthenticated() ? ("[" + client.getLocationX() + "," + client.getLocationY() + "]") : "") + " (" + client.getAddress() + ":" + client.getPort() + "/" + client.getUUID() + ")");
			client.disconnect();
			return;
		}

		// Remote client shut the socket down. Do the same from our end and cancel the channel.
		if (numRead == -1) {
			ProjectDeltaServer.getInstance().print(Level.FINE, "Closed connection of client (" + client.getAddress() + ":" + client.getPort() + "/" + client.getUUID() + ")");
			client.disconnect();
			return;
		}
		
		// Read the data in client
		readBuffer.order(ByteOrder.BIG_ENDIAN);
		client.read(readBuffer.array());
		
		// Flip to writing mode
		//readBuffer.flip();
		
		// Clear
		//readBuffer.clear();
	}
	
	
	public int getHeartBeatDelta() {
		int delta = (int) ((System.currentTimeMillis() - lastHeartBeat)/1e0);
		return delta;
	}
	
	/**
	 * Get the server MOTD tag for sending to the client
	 * @return
	 */
	public byte[] getServerInfo() {
		return new String(ProjectDeltaServer.NAME + "|" + ProjectDeltaServer.VERSION).getBytes(Charset.forName(Packet.CHARSET));
	}
	
	/**
	 * Set the timeout in milliseconds for clients to authenticate
	 * @param authTimeout
	 */
	public void setAuthTimeout(int authTimeout) {
		this.authTimeout = authTimeout;
	}
	
	/**
	 * Get the timeout in milliseconds that clients are required to authenticate within
	 * @return
	 */
	public int getAuthTimeout() {
		return authTimeout;
	}
	
	/**
	 * Set the timeout in milliseconds, for if a client does not send a 'heartbeat' within this recurring period, the connection is dropped
	 * @param connectionTimeout
	 */
	public void setConnectionTimeout(int connectionTimeout) {
		this.connectionTimeout = connectionTimeout;
	}
	
	/**
	 * Get the current timeout in milliseconds that clients must send a periodic 'heartbeat'
	 * @return
	 */
	public int getConnectionTimeout() {
		return connectionTimeout;
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
	public synchronized HashSet<Client> getClients() {
		return clients;
	}
	
}
