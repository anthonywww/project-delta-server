package com.github.anthonywww.projectdeltaserver.commands;

import java.util.logging.Level;

import com.github.anthonywww.projectdeltaserver.ProjectDeltaServer;

import net.hashsploit.hTerminal.ICLICommand;

public class ConfigCommand implements ICLICommand {

	@Override
	public String commandName() {
		return "config";
	}
	
	@Override
	public String commandDescription() {
		return "View the current server configuration";
	}
	
	@Override
	public boolean caseSensitive() {
		return false;
	}
	
	@Override
	public boolean addToCompleter() {
		return true;
	}

	@Override
	public void invoke(String[] args) {
		StringBuilder sb = new StringBuilder();
		sb.append(ProjectDeltaServer.NAME).append(' ').append('v').append(ProjectDeltaServer.VERSION).append(" configuration values:");
		
		for (ProjectDeltaServer.ConfigKey key : ProjectDeltaServer.ConfigKey.values()) {
			sb.append("\n §e-§r ").append(key.id).append(" = ").append(ProjectDeltaServer.getInstance().getConfiguration().getAsString(key.id));
		}
		
		ProjectDeltaServer.getInstance().print(Level.INFO, sb.toString());
	}
	
	
	
}
