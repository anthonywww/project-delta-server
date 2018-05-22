package com.github.anthonywww.projectdeltaserver.commands;

import java.util.logging.Level;

import com.github.anthonywww.projectdeltaserver.ProjectDeltaServer;

import net.hashsploit.hTerminal.ICLICommand;

public class HelpCommand implements ICLICommand {

	@Override
	public boolean addToCompleter() {
		return true;
	}

	@Override
	public boolean caseSensitive() {
		return false;
	}

	@Override
	public String commandDescription() {
		return "List commands and their descriptions";
	}

	@Override
	public String commandName() {
		return "help";
	}

	@Override
	public void invoke(String[] args) {
		StringBuilder sb = new StringBuilder();
		if (args.length == 1) {
			sb.append("Similar commands to '§c").append(args[0]).append("§r:'\n");
			for (ICLICommand c : ProjectDeltaServer.getInstance().getRegisteredCommands()) {
				if (c.commandName().startsWith(args[0])) {
					sb.append("    > §a").append(c.commandName()).append("§r - §3").append(c.commandDescription()).append("§r\n");
				}
			}
		} else {
			sb.append("Registered Commands:\n");
			sb.append("    > §aexit§r - §3Shutdown the server and quit§r\n");
			for (ICLICommand c : ProjectDeltaServer.getInstance().getRegisteredCommands()) {
				sb.append("    > §a").append(c.commandName()).append("§r - §3").append(c.commandDescription()).append("§r\n");
			}
		}
		
		ProjectDeltaServer.getInstance().print(Level.INFO, sb.toString());
	}

}
