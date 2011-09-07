package com.nilriri.android;

public class MapItem {

	public int x;
	public int y;
	public int state;

	public MapItem(int newX, int newY, int newState) {
		x = newX;
		y = newY;
		state = newState;
	}

	public boolean equalpos(MapItem other) {
		if (x == other.x && y == other.y) {
			return true;
		}
		return false;
	}

	public boolean equalpos(StoreKeeper storekeeper) {
		if (x == storekeeper.x && y == storekeeper.y) {
			return true;
		}
		return false;
	}

	public boolean equalpos(PlayStep step) {
		if (x == step.x && y == step.y) {
			return true;
		}
		return false;
	}

	public boolean equals(MapItem other) {
		if (x == other.x && y == other.y && state == other.state) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		return "MapItem: [" + x + "," + y + "," + state + "]";
	}
}
