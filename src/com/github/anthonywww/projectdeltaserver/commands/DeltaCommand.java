package com.github.anthonywww.projectdeltaserver.commands;

import java.util.logging.Level;

import com.github.anthonywww.projectdeltaserver.ProjectDeltaServer;
import com.github.anthonywww.projectdeltaserver.networking.Client;

import net.hashsploit.hTerminal.ICLICommand;

public class DeltaCommand implements ICLICommand {

	@Override
	public String commandName() {
		return "delta";
	}
	
	@Override
	public String commandDescription() {
		return "Shows the delta times of the server and the clients";
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
		
		// TODO: argument 0 = one client in specific?
		
		//                x, y    Sys time delta
		// &d[Server]&r   -, -    800ms
		// &3(Client)&r   3, 3    803ms
		StringBuilder sb = new StringBuilder();
		sb.append("    Peer      X, Y  Sys time delta\n");
		sb.append("    §d[Server]§r  -, -  ").append(ProjectDeltaServer.getInstance().getServer().getHeartBeatDelta()).append("ms\n");
		
		for (Client c : ProjectDeltaServer.getInstance().getServer().getClients()) {
			sb.append("    §3(Client)§r  ").append(c.getLocationX()).append(", ").append(c.getLocationY()).append("  ").append(System.currentTimeMillis() - c.getLastSyncPacketTime()).append("ms\n");
		}
		
		ProjectDeltaServer.getInstance().print(Level.INFO, "Delta time of server and clients:\n" + sb.toString() + "\n");
	}
	
	
	
}
