/***
 * Excerpted from "Hello, Android!",
 * published by The Pragmatic Bookshelf.
 * Copyrights apply to this code. It may not be used to create training material, 
 * courses, books, articles, and the like. Contact us if you are in doubt.
 * We make no guarantees that this code is fit for any purpose. 
 * Visit http://www.pragmaticprogrammer.com/titles/eband for more book information.
 ***/
package com.nilriri.android.Storekeeper;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.nilriri.android.Common;

public class Prefs extends PreferenceActivity {
    // Option names and default values
    private static final String OPT_MUSIC = "music";
    private static final boolean OPT_MUSIC_DEF = false;
    private static final String OPT_PLAYBACK = "playback";
    private static final boolean OPT_PLAYBACK_DEF = true;
    private static final String OPT_ICONSIZE = "iconsize";
    private static final String OPT_ICONSIZE_DEF = "24";

    public static final String PREF_KEY0 = "WarehousemanLevel0";
    public static final String PREF_KEY1 = "WarehousemanLevel1";
    public static final String PREF_KEY2 = "WarehousemanLevel2";
    public static final String PREF_KEY3 = "WarehousemanDifficulty";

    public static final String PREF_KEY4 = "WarehousemanCurLeve";

    /** Get the current value of the hints option */
    public static boolean getPlayback(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(OPT_PLAYBACK, OPT_PLAYBACK_DEF);
    }

    /** Get the current value of the music option */
    public static boolean getMusic(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(OPT_MUSIC, OPT_MUSIC_DEF);
    }

    public static int getDifficultly(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getInt(PREF_KEY3, Common.MEDIUM);
    }

    public static void setDifficultly(Context context, int diff) {
        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(PREF_KEY3, diff).commit();
    }

    public static int getMaxLevel(Context context) {
        int difficulty = getDifficultly(context);

        switch (difficulty) {
            case Common.EASY:
                return PreferenceManager.getDefaultSharedPreferences(context).getInt(PREF_KEY0, 1);
            case Common.MEDIUM:
                return PreferenceManager.getDefaultSharedPreferences(context).getInt(PREF_KEY1, 1);
            case Common.HARD:
                return PreferenceManager.getDefaultSharedPreferences(context).getInt(PREF_KEY2, 1);
            default:
                return 1;
        }

    }

    public static int getCurLevel(Context context) {

        return PreferenceManager.getDefaultSharedPreferences(context).getInt(PREF_KEY4, getMaxLevel(context));

    }

    public static void setCurLevel(Context context, int level) {

        PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(PREF_KEY4, level).commit();

    }

    public static void setMaxLevel(Context context, int level) {
        int difficulty = getDifficultly(context);

        switch (difficulty) {
            case Common.EASY:
                PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(PREF_KEY0, level).commit();
            case Common.MEDIUM:
                PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(PREF_KEY1, level).commit();
            case Common.HARD:
                PreferenceManager.getDefaultSharedPreferences(context).edit().putInt(PREF_KEY2, level).commit();
        }

    }

    /** Get the current value of the music option */
    public static int getIconsize(Context context) {
        String size = PreferenceManager.getDefaultSharedPreferences(context).getString(OPT_ICONSIZE, OPT_ICONSIZE_DEF);

        return Integer.parseInt(size);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
    }

}
