package com.nilriri.android;

import java.util.ArrayList;

public class PlaySteps {

    // public PlayStep[] play = new PlayStep[3];
    public ArrayList<PlayStep> play = new ArrayList<PlayStep>();

    public PlaySteps() {
        play.clear();
    }

    public void add(int newX, int newY, int newBefore, int newAfter) {
        play.add(new PlayStep(newX, newY, newBefore, newAfter));
    }

    @Override
    public String toString() {
        String str = "";
        for (int i = 0; i < 3; i++) {
            if (i == 0)
                str = play.get(i).toString();
            else
                str += "," + play.get(i).toString();
        }

        return "PlaySteps: [" + str + "]";
    }
}
