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
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;

import com.nilriri.android.Common;

public class Prefs extends PreferenceActivity {
    // Option names and default values
    private static final String OPT_MUSIC = "music";
    private static final boolean OPT_MUSIC_DEF = false;
    private static final String OPT_PLAYBACK = "playback";
    private static final boolean OPT_PLAYBACK_DEF = true;
    private static final String OPT_DRAGMOVE = "dragmove";
    private static final boolean OPT_DRAGMOVE_DEF = false;
    private static final String OPT_MOVEHANDLE = "movehandle";
    private static final boolean OPT_MOVEHANDLE_DEF = false;
    private static final String OPT_ICONSIZE = "iconsize";
    private static final String OPT_ICONSIZE_DEF = "24";

    public static final String PREF_KEY0 = "WarehousemanLevel0";
    public static final String PREF_KEY1 = "WarehousemanLevel1";
    public static final String PREF_KEY2 = "WarehousemanLevel2";
    public static final String PREF_KEY3 = "WarehousemanDifficulty";

    public static final String PREF_KEY4 = "WarehousemanCurLeve";

    private CheckBoxPreference movehandle = null;
    private CheckBoxPreference dragmove = null;

    public static boolean getPlayback(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(OPT_PLAYBACK, OPT_PLAYBACK_DEF);
    }

    public static boolean getDragMove(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(OPT_DRAGMOVE, OPT_DRAGMOVE_DEF);
    }

    public static boolean getMoveHandle(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(OPT_MOVEHANDLE, OPT_MOVEHANDLE_DEF);
    }

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

        movehandle = (CheckBoxPreference) findPreference(this.OPT_MOVEHANDLE);
        movehandle.setOnPreferenceClickListener(new myOnPreferenceClickListener());

        dragmove = (CheckBoxPreference) findPreference(this.OPT_DRAGMOVE);
        dragmove.setOnPreferenceClickListener(new myOnPreferenceClickListener());

    }

    public class myOnPreferenceClickListener implements OnPreferenceClickListener {
        public boolean onPreferenceClick(Preference preference) {
            CheckBoxPreference cpf = (CheckBoxPreference) preference;

            if (OPT_MOVEHANDLE.equals(cpf.getKey())) {
                if (cpf.isChecked()) {
                    PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putBoolean(OPT_DRAGMOVE, false).commit();
                    dragmove.setEnabled(false);
                    dragmove.setChecked(false);
                } else {
                    dragmove.setEnabled(true);
                }
            } else if (OPT_DRAGMOVE.equals(cpf.getKey())) {
                if (cpf.isChecked()) {
                    PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit().putBoolean(OPT_MOVEHANDLE, false).commit();
                    movehandle.setEnabled(false);
                    movehandle.setChecked(false);
                } else {
                    movehandle.setEnabled(true);
                }
            }

            return false;
        }
    }

}
