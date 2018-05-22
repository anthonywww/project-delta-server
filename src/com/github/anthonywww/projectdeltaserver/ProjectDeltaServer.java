package com.github.anthonywww.projectdeltaserver;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import com.github.anthonywww.projectdeltaserver.commands.ConfigCommand;
import com.github.anthonywww.projectdeltaserver.commands.DeltaCommand;
import com.github.anthonywww.projectdeltaserver.commands.HelpCommand;
import com.github.anthonywww.projectdeltaserver.commands.MatrixCommand;
import com.github.anthonywww.projectdeltaserver.commands.NetCommand;
import com.github.anthonywww.projectdeltaserver.networking.Server;
import com.github.anthonywww.projectdeltaserver.networking.crypto.CryptoManager;
import com.github.anthonywww.projectdeltaserver.utils.Timer;

import net.hashsploit.hTerminal.HTerminal;
import net.hashsploit.hTerminal.ICLICommand;
import net.hashsploit.hTerminal.ICLIEvent;
import net.hashsploit.hTerminal.IInvalidCommandHandler;

public class ProjectDeltaServer {
	
	public static final String NAME = "Project Delta Synchronization Server";
	public static final String VERSION = "0.8.1";
	
	private static ProjectDeltaServer instance;
	
	private Timer timer;
	private HTerminal console;
	private Configuration config;
	private CryptoManager crypto;
	private Server server;
	
	private int initTimeConsole;
	private int initTimeReadConfig;
	private int initTimeServices;
	
	public ProjectDeltaServer(String[] args) {
		instance = this;
		
		System.out.println(NAME + " v" + VERSION);
		
		this.timer = new Timer();
		
		// Start console initialization
		timer.start();
		this.console = new HTerminal();
		this.console.setPrompt(HTerminal.colorize("§dServer>§r "));
		this.console.setLevel(Level.INFO);
		this.console.initialize();
		
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
				// Caught ^D (EOF)
				console.print(Level.FINE, "Caught ^D (EOF); Type 'exit' to shutdown the server.");
			}
			@Override
			public void onCommandEvent(String command, String[] parameters, ICLICommand commandEvent) {
				// Nothing
			}
			@Override
			public void onReturnEvent(String text) {
				if (text.equalsIgnoreCase("exit") ||
					text.equalsIgnoreCase("quit") ||
					text.equalsIgnoreCase("stop") ||
					text.equalsIgnoreCase("end")  ||
					text.equalsIgnoreCase("shutdown")) {
					
					print(Level.INFO, "Shutting down ...");
					shutdown();
				}
			}
			@Override
			public void userInterruptEvent() {
				// Caught ^C (SIGTERM)
				console.print(Level.FINE, "Caught ^C (SIGTERM); shutting down ...");
				shutdown();
			}
		});
		
		// Register CLI Commands
		console.print(Level.INFO, "Loading components ...");
		this.console.registerCommand(new NetCommand());
		this.console.registerCommand(new ConfigCommand());
		this.console.registerCommand(new DeltaCommand());
		this.console.registerCommand(new HelpCommand());
		this.console.registerCommand(new MatrixCommand());
		
		timer.stop();
		initTimeConsole = timer.getDelta();
		
		// Start loading configuration file
		timer.start();
		console.print(Level.INFO, "Loading configuration file ...");
		
		final String configComment = "" + NAME + " v" + VERSION + " configuration file";
		
		// Default configurations
		final HashMap<String, String> configDefaults = new HashMap<String, String>();
		configDefaults.put(ConfigKey.SERVER_ADDRESS.id, "127.0.0.1");
		configDefaults.put(ConfigKey.SERVER_PORT.id, "11234");
		configDefaults.put(ConfigKey.SERVER_THREADS.id, "4");
		configDefaults.put(ConfigKey.MAX_CLIENTS.id, "60");
		configDefaults.put(ConfigKey.AUTH_TIMEOUT.id, "1000");
		configDefaults.put(ConfigKey.HEARTBEAT_INTERVAL.id, "800");
		configDefaults.put(ConfigKey.HEARTBEAT_TIMEOUT.id, "1600");
		configDefaults.put(ConfigKey.LOG_LEVEL.id, "INFO");
		
		this.config = new Configuration("server.conf", configDefaults, configComment);
		
		// Set the console logger level to the configuration's value
		this.console.setLevel(Level.parse(this.config.getAsString(ConfigKey.LOG_LEVEL.id)));
		timer.stop();
		initTimeReadConfig = timer.getDelta();
		
		// Start services
		timer.start();
		// Cryptography disabled
		//initializeCrypto();
		
		// Start the server
		initializeServer();
		timer.stop();
		initTimeServices = timer.getDelta();
		
		print(Level.INFO, "Initialized! (in " + (initTimeConsole + initTimeReadConfig + initTimeServices) + "ms). Type 'help' for a list of commands.");
	}
	
	@SuppressWarnings("unused")
	private void initializeCrypto() {
		crypto = new CryptoManager();
	}
	
	private void initializeServer() {
		try {
			print(Level.INFO, "Starting internal server ...");
			this.server = new Server(config.getAsString(ConfigKey.SERVER_ADDRESS.id), config.getAsInt(ConfigKey.SERVER_PORT.id));
			this.server.start();
		} catch (IOException e) {
			print(Level.WARNING, "Server initialization error!");
			handleException(e);
			shutdown(1);
		}
	}
	
	public Configuration getConfiguration() {
		return config;
	}
	
	public Server getServer() {
		return server;
	}
	
	public List<ICLICommand> getRegisteredCommands() {
		return console.getRegisteredCommands();
	}
	
	/**
	 * Print to the console text
	 * @param level
	 * @param msg
	 */
	public void print(Level level, String msg) {
		this.console.print(level, msg);
	}
	
	/**
	 * Gracefully shutdown
	 */
	public synchronized void shutdown() {
		shutdown(0);
	}
	
	private synchronized void shutdown(int status) {
		if (this.server != null) {
			this.server.shutdown();
		}
		if (this.config != null) {
			this.config.save();
		}
		if (this.console != null) {
			this.console.shutdown();
		}
		
		System.exit(status);
	}
	
	/**
	 * Handle an exception or error
	 * @param e
	 */
	public void handleException(Throwable e) {
		//console.handleException(e);
		console.print(Level.SEVERE, "#### BEGIN EXCEPTION ####");
		console.print(Level.SEVERE, "Exception Parameters {");
		console.print(Level.SEVERE, "\t* Call Thread: " + Thread.currentThread().getName());
		console.print(Level.SEVERE, "\t* Exception Type: " + e.getClass().getName());
		console.print(Level.SEVERE, "\t* Exception Message: \"" + e.getMessage() + "\"");
		console.print(Level.SEVERE, "\t* Trace Length: " + e.getStackTrace().length);
		console.print(Level.SEVERE, "}");
		console.print(Level.SEVERE, "");
		console.print(Level.SEVERE, "Stack Trace {");
		for (int i=0; i<e.getStackTrace().length-1; i++) {
			String s = "| ";
			if (i == e.getStackTrace().length-2) {
				s = "|>";
			}
			console.print(Level.SEVERE, "\t" + s + " #" + (i+1) + " " + e.getStackTrace()[(e.getStackTrace().length-1)-i].toString());
		}
		console.print(Level.SEVERE, "}");
		
		if (console.getLevel() == Level.FINEST) {
			console.print(Level.SEVERE, "");
			console.print(Level.SEVERE, "Master Stack Trace Tree {");
			for(Thread t : Thread.getAllStackTraces().keySet()) {
				StackTraceElement[] v = Thread.getAllStackTraces().get(t);
				if (v.length > 0) {
					console.print(Level.SEVERE, "\tThread \"" + t.getName() + "\"");
					for (int i=0; i<v.length-1; i++) {
						String s = "| ";
						if (i == v.length-2) {
							s = "|>";
						}
						console.print(Level.SEVERE, "\t\t" + s + " #" + (i+1) + " " + v[(v.length-1)-i].toString());
					}
					console.print(Level.SEVERE, "");
				}
			}
			console.print(Level.SEVERE, "}");
		}
		console.print(Level.SEVERE, "#### END EXCEPTION ####");
	}
	
	
	/**
	 * Server Configuration keys
	 */
	public enum ConfigKey {
		SERVER_ADDRESS("server.address"),
		SERVER_PORT("server.port"),
		SERVER_THREADS("server.threads"),
		MAX_CLIENTS("server.max_clients"),
		AUTH_TIMEOUT("server.auth_timeout"),
		HEARTBEAT_INTERVAL("server.heartbeat_interval"),
		HEARTBEAT_TIMEOUT("server.heartbeat_timeout"),
		LOG_LEVEL("server.log_level");
		
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
