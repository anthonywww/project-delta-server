package com.github.anthonywww.projectdeltaserver.utils;

public class Timer {
	
	private long start;
	private long end;
	
	/**
	 * Creates a new timer, measures time in milliseconds between start() and stop()
	 */
	public Timer() {
		this.start = 0L;
		this.end = 0L;
	}
	
	/**
	 * Start the timer
	 */
	public void start() {
		this.start = System.currentTimeMillis();
	}
	
	/**
	 * Stop the timer
	 */
	public void stop() {
		this.end = System.currentTimeMillis();
	}
	
	/**
	 * Get the delta time between start() and stop() as a long
	 * @return
	 */
	public long getAsLong() {
		return end - start;
	}
	
	/**
	 * Get the delta time in milliseconds between start() and stop()
	 * @return
	 */
	public int getDelta() {
		return (int) ((end - start) % Integer.MAX_VALUE);
	}
	
}
