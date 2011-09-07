package com.nilriri.android;

public class StoreKeeper {

	public int x;
	public int y;

	public StoreKeeper(int newX, int newY) {
		x = newX;
		y = newY;
	}

	@Override
	public String toString() {
		return "Storekeeper: [" + x + "," + y + "]";
	}
}