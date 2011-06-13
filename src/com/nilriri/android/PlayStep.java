package com.nilriri.android;


public class PlayStep {
    public int x;
    public int y;
    public int before;
    public int after;

    public PlayStep(int newX, int newY, int newBefore, int newAfter) {
        x = newX;
        y = newY;
        before = newBefore;
        after = newAfter;
    }

    @Override
    public String toString() {
        return "PlayStep: [" + x + "," + y + "," + before + "," + after + "]";
    }
}
