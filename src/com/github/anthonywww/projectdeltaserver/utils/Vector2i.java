package com.github.anthonywww.projectdeltaserver.utils;

public class Vector2i {
	
	private int x;
	private int y;
	
	public Vector2i() {
		this.x = 0;
		this.y = 0;
	}
	
	public Vector2i(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
}
