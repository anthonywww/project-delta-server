package com.github.anthonywww.projectdeltaserver.utils;

public class Vector2f {
	
	private float x;
	private float y;
	
	public Vector2f() {
		this.x = 0.0f;
		this.y = 0.0f;
	}
	
	public Vector2f(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void setX(float x) {
		this.x = x;
	}
	
	public void setY(float y) {
		this.y = y;
	}
	
	public float getX() {
		return x;
	}
	
	public float getY() {
		return y;
	}
	
}
