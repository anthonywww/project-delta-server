package com.github.anthonywww.projectdeltaserver.networking;

public enum PacketHeader {
	
	/**
	 * Server & Client Bound.
	 * Disconnect packet to close the connection cleanly, usually without a message.
	 * Optional Payload: UTF-8 message
	 */
	DISCONNECT((byte) 0xEE),
	
	/**
	 * Client Bound.
	 * The initial handshake packet sent to the server must contain this header, and a payload containing the client info.
	 * Mandatory Payload: client information. (X, Y, MonitorWidth, MonitorHeight, OS, CPU, GPU, Memory)
	 */
	HANDSHAKE((byte) 0x01),
	
	/**
	 * Server Bound.
	 * A response to the client HANDSHAKE packet acknowledging and accepting the connection
	 * Mandatory Payload: server information.
	 */
	HANDSHAKE_ACK((byte) 0x02),
	
	/**
	 * Server Bound.
	 * A heartbeat from the server to calculate delta's (starts immediately)
	 * Mandatory Payload: server's system time in milliseconds
	 */
	SYNC((byte) 0x03),
	
	/**
	 * Client Bound.
	 * A heartbeat acknowledgement from the client to the sync request.
	 * Mandatory Payload: client's system time in milliseconds
	 */
	SYNC_ACK((byte) 0x04),
	
	/**
	 * Server Bound.
	 * Show the display
	 * Optional Payload: UTF-8 message
	 */
	DISPLAY_SHOW((byte) 0x05),
	
	/**
	 * Server Bound.
	 * Hide the display
	 * Optional Payload: UTF-8 message
	 */
	DISPLAY_HIDE((byte) 0x06),
	
	/**
	 * Client Bound.
	 * The display was created
	 * Optional Payload: UTF-8 message
	 */
	DISPLAY_SHOW_ACK((byte) 0x07),
	
	/**
	 * Server Bound.
	 * Hide the display
	 * Optional Payload: UTF-8 message
	 */
	DISPLAY_HIDE_ACK((byte) 0x08),
	
	/**
	 * Server Bound.
	 * Tell the client to draw some text to the screen.
	 * Mandatory Payload: (XPosition|YPosition|MessageID|Text)
	 * 
	 * @param XPosition int = The x position on the client's screen.
	 * @param YPosition int = The y position on the client's screen.
	 * @param MessageID int = A unique number to identify the message so it can be referenced later.
	 * @param Text string = The message you want to display.
	 */
	TEXT_CREATE((byte) 0x09),
	
	/**
	 * Server Bound.
	 * Tell the client to delete a text-message on the screen using the MessageID
	 * Mandatory Payload: (MessageID)
	 * 
	 * @param MessageID int = The unique number identifying the message to be deleted.
	 */
	TEXT_DELETE((byte) 0x0A),
	
	/**
	 * Client Bound.
	 * Tell the server the text is now being rendered.
	 * Mandatory Payload: (MessageID)
	 * 
	 * @param MessageID int = The unique number identifying the message to be deleted.
	 */
	TEXT_CREATE_ACK((byte) 0x0B),
	
	
	/**
	 * Client Bound.
	 * Tell the server the MessageID has successfully been deleted.
	 * Mandatory Payload: (int:MessageID)
	 */
	TEXT_DELETE_ACK((byte) 0x0C),
	
	/**
	 * Server Bound.
	 * Audio load request from the server to the client.
	 * Mandatory Payload: resource name to load
	 */
	AUDIO_LOAD((byte) 0x0D),
	
	/**
	 * Server Bound.
	 * Audio play request from the server to the client.
	 * Mandatory Payload: resource name to play
	 */
	AUDIO_PLAY((byte) 0x0E),
	
	/**
	 * Server Bound.
	 * Stop the music playing
	 * Mandatory Payload: resource name to pause
	 */
	AUDIO_PAUSE((byte) 0x0F),
	
	/**
	 * Server Bound.
	 * Stop the music playing
	 * Mandatory Payload: resource name to stop
	 */
	AUDIO_STOP((byte) 0x10),
	
	/**
	 * Server Bound.
	 * Set the audio volume
	 * Mandatory Payload: float volume from 0.0 to 1.0
	 */
	AUDIO_VOLUME((byte) 0x11),
	
	/**
	 * Server Bound.
	 * Set the audio pitch
	 * Mandatory Payload: float pitch
	 */
	AUDIO_PITCH((byte) 0x12),
	
	/**
	 * Client Bound.
	 * Tell the server the audio has been loaded successfully
	 * Mandatory Payload: resource loaded
	 */
	AUDIO_LOAD_ACK((byte) 0x13),
	
	/**
	 * Client Bound.
	 * The client acknowledged the play request and has started playing.
	 * Mandatory Payload: resource playing
	 */
	AUDIO_PLAY_ACK((byte) 0x14),
	
	/**
	 * Client Bound.
	 * Tell the server the audio has been paused
	 * Mandatory Payload: resource paused
	 */
	AUDIO_PAUSE_ACK((byte) 0x15),
	
	/**
	 * Client Bound.
	 * Tell the server the audio has been stopped
	 * Mandatory Payload: resource stopped
	 */
	AUDIO_STOP_ACK((byte) 0x16),
	
	/**
	 * Client Bound.
	 * Tell the server the audio volume changed
	 * Mandatory Payload: float volume from 0.0 to 1.0
	 */
	AUDIO_VOLUME_ACK((byte) 0x17),
	
	/**
	 * Client Bound.
	 * Tell the server the audio pitch changed
	 * Mandatory Payload: float pitch
	 */
	AUDIO_PITCH_ACK((byte) 0x18),
	
	
	
	
	
	
	
	
	
	
	/**
	 * Server Bound.
	 * Tell the client to delete itself off the client machine (used for easy clean-up).
	 */
	DELETE((byte) 0xFE),
	
	/**
	 * Client Bound.
	 * Tell the server the client deletion was successful.
	 */
	DELETE_ACK((byte) 0xFF);
	
	private final byte value;
	
	private PacketHeader(byte value) {
		this.value = value;
	}
	
	public byte getValue() {
		return value;
	}
	
}
