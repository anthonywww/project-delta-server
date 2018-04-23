package com.github.anthonywww.projectdeltaserver.commands;

import net.hashsploit.hTerminal.ICLICommand;

public class ExitCommand implements ICLICommand {

	@Override
	public String commandName() {
		return "exit";
	}
	
	@Override
	public String commandDescription() {
		return "Gracefully disconnect all clients and shutdown the server";
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
	public void invoke(String[] arg0) {
		
	}
	
	
}
