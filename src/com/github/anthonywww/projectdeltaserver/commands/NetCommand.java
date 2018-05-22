package com.github.anthonywww.projectdeltaserver.commands;

import java.util.Arrays;
import java.util.logging.Level;

import com.github.anthonywww.projectdeltaserver.ProjectDeltaServer;
import com.github.anthonywww.projectdeltaserver.networking.Client;
import com.github.anthonywww.projectdeltaserver.networking.PacketHeader;

import net.hashsploit.hTerminal.ICLICommand;

public class NetCommand implements ICLICommand {

	@Override
	public String commandName() {
		return "net";
	}
	
	@Override
	public String commandDescription() {
		return "Issue network commands to control connected clients";
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
		
		// Disconnect all clients: net disconnect 
		// Disconnect specific clients: net disconnect 0,0 3,2 6,4
		
		if (args.length >= 1) {
			//String[] clients = Arrays.copyOfRange(args, 2, args.length);		
			
			if (args[0].equalsIgnoreCase("status")) {
				StringBuilder sb = new StringBuilder();
				
				ProjectDeltaServer.getInstance().print(Level.INFO, "Incomplete\n" + sb.toString());
				return;
			}
			
			
			
			//////////////////////
			if (args[0].equalsIgnoreCase("display")) {
				
				if (args.length == 1) {
					ProjectDeltaServer.getInstance().print(Level.INFO, "Invalid arguments: §adisplay§r §c<open/close>§r §e[clients]§r");
					return;
				}
				
				if (args[1].equalsIgnoreCase("open")) {
					
					for (Client c : ProjectDeltaServer.getInstance().getServer().getClients()) {
						c.send(PacketHeader.DISPLAY_SHOW, null);
					}
					
				} else if (args[1].equalsIgnoreCase("close")) {
					
					for (Client c : ProjectDeltaServer.getInstance().getServer().getClients()) {
						c.send(PacketHeader.DISPLAY_HIDE, null);
					}
					
				}
				
				
			}
			
			if (args[0].equalsIgnoreCase("justdoit")) {
				for (Client c : ProjectDeltaServer.getInstance().getServer().getClients()) {
					if (c.getLocationY() == 0) {
						c.send(PacketHeader.AUDIO_PLAY, new String("program/1_StringOrchestra").getBytes());
						c.send(PacketHeader.AUDIO_PLAY, new String("program/14_BassGuitar").getBytes());
						c.send(PacketHeader.AUDIO_PLAY, new String("program/2_StringOrchestraBG").getBytes());
					} else if (c.getLocationY() == 1) {
						c.send(PacketHeader.AUDIO_PLAY, new String("program/6_Fiddle").getBytes());
						c.send(PacketHeader.AUDIO_PLAY, new String("program/7_Fiddle").getBytes());
						c.send(PacketHeader.AUDIO_PLAY, new String("program/8_Fiddle").getBytes());
						c.send(PacketHeader.AUDIO_PLAY, new String("program/4_Piano").getBytes());
						c.send(PacketHeader.AUDIO_PLAY, new String("program/3_SquareWave").getBytes());
					} else if (c.getLocationY() == 2) {
						c.send(PacketHeader.AUDIO_PLAY, new String("program/11_Ocarina").getBytes());
						c.send(PacketHeader.AUDIO_PLAY, new String("program/9_Bells").getBytes());
						c.send(PacketHeader.AUDIO_PLAY, new String("program/15_ReverseCymbal").getBytes());
					} else if (c.getLocationY() == 3) {
						c.send(PacketHeader.AUDIO_PLAY, new String("program/10_Drums").getBytes());
						c.send(PacketHeader.AUDIO_PLAY, new String("program/12_ElectricGuitar").getBytes());
						c.send(PacketHeader.AUDIO_PLAY, new String("program/13_ElectricGuitar").getBytes());
					}
				}
			}
			//////////////////////////////
			
			if (args[0].equalsIgnoreCase("disconnect")) {
				StringBuilder sb = new StringBuilder();
				String[] clients = Arrays.copyOfRange(args, 1, args.length);
				
				if (args.length >= 2) {
					for (String clientString : clients) {
						String xString = clientString.split(",")[0];
						String yString = clientString.split(",")[1];
						try {
							int x = Integer.parseInt(xString);
							int y = Integer.parseInt(yString);
							for (Client c : ProjectDeltaServer.getInstance().getServer().getClients()) {
								if (c.getLocationX() == x && c.getLocationY() == y) {
									c.disconnect();
								}
							}
						} catch (NumberFormatException e) {
							ProjectDeltaServer.getInstance().print(Level.INFO, "Invalid arguments: §adisconnect§r §e[clients]§r");
							return;
						}
					}
				} else {
					for (Client c : ProjectDeltaServer.getInstance().getServer().getClients()) {
						c.disconnect();
					}
				}
				
				ProjectDeltaServer.getInstance().print(Level.INFO, sb.toString());
				return;
			}
			
			if (args[0].equalsIgnoreCase("audio")) {
				String action = args[1];
				String file = args[2];
				PacketHeader packetAction = PacketHeader.AUDIO_PLAY;
				
				if (action.equalsIgnoreCase("play")) {
					packetAction = PacketHeader.AUDIO_PLAY;
				} else if (action.equalsIgnoreCase("pause")) {
					packetAction = PacketHeader.AUDIO_PAUSE;
				} else if (action.equalsIgnoreCase("stop")) {
					packetAction = PacketHeader.AUDIO_STOP;
				} else if (action.equalsIgnoreCase("pitch")) {
					packetAction = PacketHeader.AUDIO_PITCH;
				} else if (action.equalsIgnoreCase("volume")) {
					packetAction = PacketHeader.AUDIO_VOLUME;
				}
				
				if (args.length > 3) {
					String[] clients = Arrays.copyOfRange(args, 3, args.length);
					
					for (String clientString : clients) {
						String xString = clientString.split(",")[0];
						String yString = clientString.split(",")[1];
						try {
							int x = Integer.parseInt(xString);
							int y = Integer.parseInt(yString);
							for (Client c : ProjectDeltaServer.getInstance().getServer().getClients()) {
								if (c.getLocationX() == x && c.getLocationY() == y) {
									c.send(packetAction, file.getBytes());
								}
							}
						} catch (NumberFormatException e) {
							ProjectDeltaServer.getInstance().print(Level.INFO, "Invalid arguments: §aaudio§r §c<play/pause/stop>§r §c<file>§r §e[clients]§r");
							return;
						}
					}
					
					return;
				} else if (args.length == 3){
					
					for (Client c : ProjectDeltaServer.getInstance().getServer().getClients()) {
						c.send(packetAction, file.getBytes());
					}
					
				} else {
					ProjectDeltaServer.getInstance().print(Level.INFO, "Invalid arguments: §aaudio§r §c<play/pause/stop>§r §c<file>§r §e[clients]§r");
				}
			}
			
			
			return;
		}
		
		// TODO: This command can be used with [0,0] to specify a specific client, or without to specify all clients.
		// TODO: Command should show in-depth statistic on client
		
		StringBuilder sb = new StringBuilder();
		sb.append("Net help context:\n\n");
		sb.append("\n");
		sb.append("    Required: §c<params>§r\n");
		sb.append("    Optional: §e[params]§r\n");
		sb.append("    Construct: net §c<action>§r §e[clients]§r\n");
		sb.append("               Clients are expressed as §a1,2§r or §a0,0§r or §a3,10§r\n");
		sb.append("\n");
		sb.append("    Possible actions:\n");
		//sb.append("    > §astatus§r §e[clients]§r - §3View the status of the client§r\n");
		sb.append("    > §adisconnect§r §e[clients]§r - §3Disconnect the client from the network§r\n");
		sb.append("    > §aaudio§r §c<play/pause/stop>§r §c<file>§r §e[clients]§r - §3Audio control§r\n");
		sb.append("    > §adisplay§r §c<open/close>§r §e[clients]§r - §3Graphics control§r\n");
		
		ProjectDeltaServer.getInstance().print(Level.INFO, sb.toString());
	}
	
	
	
}
