package com.github.anthonywww.projectdeltaserver;

import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;

import com.github.anthonywww.projectdeltaserver.commands.ExitCommand;
import com.github.anthonywww.projectdeltaserver.networking.Server;

import net.hashsploit.hTerminal.HTerminal;
import net.hashsploit.hTerminal.ICLICommand;
import net.hashsploit.hTerminal.ICLIEvent;
import net.hashsploit.hTerminal.IInvalidCommandHandler;

public class ProjectDeltaServer {
	
	public static final String NAME = "Project Delta Synchronization Server";
	public static final String VERSION = "0.1.0";
	
	private static ProjectDeltaServer instance;
	
	private boolean running;
	private HTerminal console;
	private Configuration config;
	private Server server;
	
	public ProjectDeltaServer(String[] args) {
		instance = this;
		this.running = true;
		this.console = new HTerminal();
		this.console.setPrompt(HTerminal.colorize("§e§lServer>§r "));
		this.console.setLevel(Level.INFO);
		
		// Set up console
		this.console.setInvalidCommandHandler(new IInvalidCommandHandler() {
			@Override
			public void invalidInvoke(String arg0, String[] arg1) {
				console.print(Level.INFO, "Invalid command, type 'help' for a list of commands.");
			}
		});
		
		this.console.registerEvent(new ICLIEvent() {
			@Override
			public void eofInterruptEvent() {
				console.print(Level.FINE, "Caught ^D (EOF), type 'exit' to quit.");
			}
			@Override
			public void onCommandEvent(String command, String[] parameters, ICLICommand commandEvent) {
				// Nothing
			}
			@Override
			public void onReturnEvent(String text) {
				// Nothing
			}
			@Override
			public void userInterruptEvent() {
				console.print(Level.FINE, "Caught ^C (SIGTERM), simulating 'exit' event ...");
				running = false;
			}
		});
		
		// Register CLI Commands
		this.console.registerCommand(new ExitCommand());
		
		
		// Load configuration file
		final String configComment = "# " + NAME + " v" + VERSION + " configuration file";
		final HashMap<String, String> configDefaults = new HashMap<String, String>();
		configDefaults.put(ConfigKey.SERVER_ADDRESS.id, "127.0.0.1");
		configDefaults.put(ConfigKey.SERVER_PORT.id, "11234");
		configDefaults.put(ConfigKey.SERVER_HEARTBEAT.id, "3");
		configDefaults.put(ConfigKey.SERVER_THREADS.id, "4");
		configDefaults.put(ConfigKey.MAX_CLIENTS.id, "60");
		configDefaults.put(ConfigKey.LOG_LEVEL.id, "INFO");
		
		this.config = new Configuration("/server.conf", configDefaults, configComment);
		
		
		// Start the server
		try {
			this.server = new Server(config.getAsString(ConfigKey.SERVER_ADDRESS.id), config.getAsInt(ConfigKey.SERVER_PORT.id));
		} catch (IOException e) {
			// FIXME: Handle exception
			e.printStackTrace();
		}
		
		
	}
	
	
	public Configuration getConfiguration() {
		return config;
	}
	
	
	public Server getServer() {
		return server;
	}
	
	
	/**
	 * Server Configuration keys
	 */
	public enum ConfigKey {
		SERVER_ADDRESS("server-address"),
		SERVER_PORT("server-port"),
		SERVER_HEARTBEAT("server-heartbeat"),
		SERVER_THREADS("server-threads"),
		MAX_CLIENTS("max-clients"),
		LOG_LEVEL("log-level");
		
		public final String id;
		
		private ConfigKey(String id) {
			this.id = id;
		}
	}
	
	/**
	 * Get the current running instance of the Project Delta Synchronization Server
	 * @return
	 */
	public static ProjectDeltaServer getInstance() {
		return instance;
	}
	
}
