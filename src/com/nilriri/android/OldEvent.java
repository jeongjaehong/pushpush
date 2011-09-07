package com.nilriri.android;

public class OldEvent {
	private float x;
	private float y;

	public OldEvent(float f, float g) {
		this.x = f;
		this.y = g;
	}

	public void set(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}

	@Override
	public String toString() {
		return "x=" + this.x + ",y=" + this.y;
	}
}