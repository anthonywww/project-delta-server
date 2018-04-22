package com.github.anthonywww.projectdeltaserver;

public class ProjectDeltaServer {
	
	public static final String NAME = "Project Delta Synchronization Server";
	public static final String VERSION = "0.1.0";
	
	private static ProjectDeltaServer instance;
	
	public ProjectDeltaServer(String[] args) {
		instance = this;
		
		
		
	}
	
	
	
	
	/**
	 * Get the current running instance of the Project Delta Synchronization Server
	 * @return
	 */
	public static ProjectDeltaServer getInstance() {
		return instance;
	}
	
}
