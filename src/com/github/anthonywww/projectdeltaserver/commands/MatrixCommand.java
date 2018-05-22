package com.github.anthonywww.projectdeltaserver.commands;

import java.util.logging.Level;

import com.github.anthonywww.projectdeltaserver.ProjectDeltaServer;
import com.github.anthonywww.projectdeltaserver.networking.Client;

import net.hashsploit.hTerminal.ICLICommand;

public class MatrixCommand implements ICLICommand {
	
	public static final int MIN_SIZE = 2;
	
	@Override
	public String commandName() {
		return "matrix";
	}
	
	@Override
	public String commandDescription() {
		return "View an ASCII matrix representation of the network";
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
		// Display a map/grid
		
		/*
		 * 
		 * [ , ] [ , ] [ , ] [ , ]
		 * [ , ] [ , ] [ , ] [ , ]
		 * [ , ] [ , ] [ , ] [ , ]
		 * [ , ] [ , ] [ , ] [ , ]
		 * 
		 */
		
		int totalColumns = MIN_SIZE;
		int totalRows = MIN_SIZE;
		int totalConnected = 0;
		
		sb.append("ASCII network matrix:\n");
		for (Client c : ProjectDeltaServer.getInstance().getServer().getClients()) {
			if (c.isAuthenticated()) {
				if (c.getLocationX() >= totalColumns) {
					totalColumns = c.getLocationX() + 1;
				}
				if (c.getLocationY() >= totalRows) {
					totalRows = c.getLocationY() + 1;
				}
				totalConnected++;
			}
		}
		
		
		
		sb.append(" - Matrix: ").append(totalColumns).append('x').append(totalRows).append(" (").append(totalRows * totalColumns).append(")\n");
		sb.append(" - Clients: ").append(totalConnected).append('\n');
		sb.append("   ").append('+').append(' ');
		
		for (int y=0; y<totalColumns; y++) {
			sb.append("-----");
			
			if (y <= totalColumns-2) {
				sb.append("-");
			}
		}
		
		sb.append('\n');
		
		for (int y=0;y<totalRows;y++) {
			sb.append("   |");
			sb.append(' ');
			for (int x=0;x<totalColumns;x++) {
				boolean state = false;
				
				for (Client c : ProjectDeltaServer.getInstance().getServer().getClients()) {
					if (c.isAuthenticated()) {
						if (c.getLocationX() == x && c.getLocationY() == y) {
							state = true;
						}
					}
				}
				
				if (state) {
					sb.append("§a");
				} else {
					sb.append("§8");
				}
				
				sb.append('[');
				
				sb.append(x);
				
				sb.append(',');
				
				sb.append(y);
				
				sb.append("]§r ");
			}
			sb.append('\n');
		}
		
		
		
		ProjectDeltaServer.getInstance().print(Level.INFO, sb.toString());
	}
	
	
	
}
