/*
 * Copyright (C) 2010 The Jeong Jaehong (jeongjaehong@gmail.com)
 *
 */

package com.nilriri.android.Storekeeper;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.nilriri.android.Common;
import com.nilriri.android.OldEvent;
import com.nilriri.android.PlaySteps;
import com.nilriri.android.Storekeeper.dao.AppConst;

public class Main extends Activity implements OnTouchListener {
    private MyApplication application;
    private StoreKeeperView mStoreKeeperView;
    private OldEvent oldEvent;
    public boolean isDragMove = false;

    // Menu item ids    
    public static final int MENU_ITEM_ABOUT = Menu.FIRST;
    public static final int MENU_ITEM_PLAYBACK = Menu.FIRST + 1;
    public static final int MENU_ITEM_PLAY = Menu.FIRST + 2;
    public static final int MENU_ITEM_RELOADLEVEL = Menu.FIRST + 3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.main);

        application = (MyApplication) getApplication();

        mStoreKeeperView = (StoreKeeperView) findViewById(R.id.mainmapview);
        mStoreKeeperView.setMsgView((TextView) findViewById(R.id.msg), (TextView) findViewById(R.id.info));

        //Log.e("StoreKeeperView", "fileList : " + this.fileList().toString());

        if (savedInstanceState == null) {
            mStoreKeeperView.setMode(StoreKeeperView.READY);
        } else {
            Bundle map = savedInstanceState.getBundle(Common.ICICLE_KEY);
            if (map != null) {
                mStoreKeeperView.restoreState(map);

                if (this.mStoreKeeperView.mStorekeeper == null) {
                    StartGame(Prefs.getDifficultly(this), Prefs.getMaxLevel(this), 0);
                }
            } else {
                mStoreKeeperView.setMode(StoreKeeperView.PAUSE);
            }
        }

        //mStoreKeeperView.setOnCreateContextMenuListener(this);
        mStoreKeeperView.setOnTouchListener(this);

        findViewById(R.id.uparrow).setOnTouchListener(this);
        findViewById(R.id.downarrow).setOnTouchListener(this);
        findViewById(R.id.leftarrow).setOnTouchListener(this);
        findViewById(R.id.rightarrow).setOnTouchListener(this);

        findViewById(R.id.playback).setOnTouchListener(this);
        findViewById(R.id.reload).setOnTouchListener(this);

        oldEvent = new OldEvent(-1, -1);
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

        Log.d(AppConst.TAG, view.toString());
        Log.d(AppConst.TAG, event.toString());

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:

                if (isDragMove && view.getId() == R.id.mainmapview) {

                    float xoffset = event.getX() - oldEvent.getX();
                    float yoffset = event.getY() - oldEvent.getY();

                    Log.d(AppConst.TAG, "xoffset=" + xoffset + ",yoffset=" + yoffset);

                    if (xoffset > 50 && Math.abs(xoffset) > Math.abs(yoffset)) {
                        mStoreKeeperView.mNextDirection = Common.RIGHT;
                        Log.d(AppConst.TAG, "right");
                    } else if (xoffset < -50 && Math.abs(xoffset) > Math.abs(yoffset)) {
                        mStoreKeeperView.mNextDirection = Common.LEFT;
                        Log.d(AppConst.TAG, "left");
                    } else if (yoffset > 50 && Math.abs(xoffset) < Math.abs(yoffset)) {
                        mStoreKeeperView.mNextDirection = Common.DOWN;
                        Log.d(AppConst.TAG, "down");
                    } else if (yoffset < -50 && Math.abs(xoffset) < Math.abs(yoffset)) {
                        mStoreKeeperView.mNextDirection = Common.UP;
                        Log.d(AppConst.TAG, "up");
                    } else {
                        //oldEvent.set(event.getX(), event.getY());
                        Log.d(AppConst.TAG, oldEvent.toString());
                        return false;
                    }

                    oldEvent.set(event.getX(), event.getY());
                    mStoreKeeperView.moveStorekeeper(false);
                    mStoreKeeperView.update();
                    return false;

                }

                break;
            case MotionEvent.ACTION_DOWN:
                if (isDragMove) {

                    Log.d(AppConst.TAG, "initilize...");
                    oldEvent.set(event.getX(), event.getY());
                    return true;

                } else {
                    oldEvent.set(event.getX(), event.getY());
                    mStoreKeeperView.setMode(StoreKeeperView.RUNNING);
                    return true;
                }
            case MotionEvent.ACTION_UP:

                Vibrator vi = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
                vi.vibrate(new long[] { 250 }, 0);

                switch (view.getId()) {
                    case R.id.leftarrow:
                        mStoreKeeperView.mNextDirection = Common.LEFT;
                        break;
                    case R.id.rightarrow:
                        mStoreKeeperView.mNextDirection = Common.RIGHT;
                        break;
                    case R.id.uparrow:
                        mStoreKeeperView.mNextDirection = Common.UP;
                        break;
                    case R.id.downarrow:
                        mStoreKeeperView.mNextDirection = Common.DOWN;
                        break;
                    case R.id.playback:
                        //mStoreKeeperView.mNextDirection = Common.DOWN;

                        //TODO: refactoring...
                        if (Prefs.getPlayback(this)) {
                            mStoreKeeperView.playBack();
                        } else {

                            Toast.makeText(this, getResources().getString(R.string.notfound_playback), Toast.LENGTH_LONG).show();

                            mStoreKeeperView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
                            return (true);
                        }

                        return false;
                    case R.id.reload:
                        //mStoreKeeperView.mNextDirection = Common.DOWN;

                        //TODO: refactoring...
                        int difficulty = Prefs.getDifficultly(this);
                        int maxLevel = Prefs.getMaxLevel(Main.this);

                        StartGame(difficulty, maxLevel, 0);

                        return false;
                    case R.id.mainmapview:

                        if (isDragMove) {
                            oldEvent.set(event.getX(), event.getY());
                            return true;
                        }

                        float xoffset = event.getX() - oldEvent.getX();
                        float yoffset = event.getY() - oldEvent.getY();

                        if (xoffset > 10 && Math.abs(xoffset) > Math.abs(yoffset)) {
                            mStoreKeeperView.mNextDirection = Common.RIGHT;
                        } else if (xoffset < -10 && Math.abs(xoffset) > Math.abs(yoffset)) {
                            mStoreKeeperView.mNextDirection = Common.LEFT;
                        } else if (yoffset > 10 && Math.abs(xoffset) < Math.abs(yoffset)) {
                            mStoreKeeperView.mNextDirection = Common.DOWN;
                        } else if (yoffset < -10 && Math.abs(xoffset) < Math.abs(yoffset)) {
                            mStoreKeeperView.mNextDirection = Common.UP;
                        } else {
                            float manx = (mStoreKeeperView.mStorekeeper.x * mStoreKeeperView.getItemSize()) + (mStoreKeeperView.getItemSize() * 0.5f);
                            float many = (mStoreKeeperView.mStorekeeper.y * mStoreKeeperView.getItemSize()) + (mStoreKeeperView.getItemSize() * 0.5f);

                            xoffset = event.getX() - manx;
                            yoffset = event.getY() - many;

                            if (xoffset > 10 && Math.abs(xoffset) > Math.abs(yoffset)) {
                                mStoreKeeperView.mNextDirection = Common.LEFT;
                            } else if (xoffset < -10 && Math.abs(xoffset) > Math.abs(yoffset)) {
                                mStoreKeeperView.mNextDirection = Common.RIGHT;
                            } else if (yoffset > 10 && Math.abs(xoffset) < Math.abs(yoffset)) {
                                mStoreKeeperView.mNextDirection = Common.UP;
                            } else if (yoffset < -10 && Math.abs(xoffset) < Math.abs(yoffset)) {
                                mStoreKeeperView.mNextDirection = Common.DOWN;
                            } else {
                                break;
                            }
                        }
                        oldEvent.set(event.getX(), event.getY());

                        break;
                }

                oldEvent.set(event.getX(), event.getY());
                if (!mStoreKeeperView.moveStorekeeper(false)) {
                    vi.vibrate(new long[] { 250, 50, 100 }, 2);
                }
                mStoreKeeperView.update();
                break;
            default:
                return false;
                //return super.onTouchEvent(event);

        }

        return false;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int difficulty = Prefs.getDifficultly(this);
        int maxLevel = Prefs.getMaxLevel(Main.this);

        switch (item.getItemId()) {
            case R.id.about:
                startActivity(new Intent(this, About.class));
                return true;
            case R.id.prevlevel:
                StartGame(difficulty, maxLevel, -1);
                return true;
            case R.id.nextlevel:
                if (mStoreKeeperView.isClearLevel()) {
                    int clearLevel = mStoreKeeperView.getLevel() + 1;
                    if (maxLevel >= clearLevel) {
                        Prefs.setMaxLevel(this, clearLevel);
                    }
                    StartGame(difficulty, maxLevel, 1);
                } else {
                    Prefs.setMaxLevel(this, mStoreKeeperView.getLevel() + 1);
                    StartGame(difficulty, mStoreKeeperView.getLevel() + 1, 1);
                }
                return true;
            case R.id.reloadlevel:
                StartGame(difficulty, maxLevel, 0);
                return true;
            case R.id.newgame:
                openNewGameDialog();
                return true;

            case R.id.datamanage:
                startActivity(new Intent(Main.this, ManageData.class));
                return true;
            case R.id.playback: {

                if (Prefs.getPlayback(this)) {
                    mStoreKeeperView.playBack();
                } else {

                    Toast.makeText(this, getResources().getString(R.string.notfound_playback), Toast.LENGTH_LONG).show();

                    mStoreKeeperView.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
                    return (true);
                }

                return true;
            }
            case R.id.playlevel:

                mStoreKeeperView.playHistory(difficulty, mStoreKeeperView.getLevel());
                return true;
            case R.id.settings:
                startActivity(new Intent(this, Prefs.class));
                return true;
        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mStoreKeeperView.setMode(StoreKeeperView.PAUSE);
        Music.stop(this);

        if (mStoreKeeperView.isClearLevel()) {
            //int difficulty = getPreferences(MODE_PRIVATE).getInt(Common.PREF_KEY3, Common.MEDIUM);
            int difficulty = Prefs.getDifficultly(this);
            switch (difficulty) {
                case 0:
                    Prefs.setMaxLevel(this, mStoreKeeperView.getLeft());
                    //getPreferences(MODE_PRIVATE).edit().putInt(Common.PREF_KEY0, mStoreKeeperView.getLeft()).commit();
                    break;
                case 1:
                    Prefs.setMaxLevel(this, mStoreKeeperView.getLeft());
                    //getPreferences(MODE_PRIVATE).edit().putInt(Common.PREF_KEY1, mStoreKeeperView.getLeft()).commit();
                    break;
                case 2:
                    Prefs.setMaxLevel(this, mStoreKeeperView.getLeft());
                    //getPreferences(MODE_PRIVATE).edit().putInt(Common.PREF_KEY2, mStoreKeeperView.getLeft()).commit();
                    break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        this.isDragMove = Prefs.getDragMove(this);

        if (Prefs.getMoveHandle(this)) {
            findViewById(R.id.movehandle).setVisibility(View.VISIBLE);
            isDragMove = false;
        } else {
            findViewById(R.id.movehandle).setVisibility(View.GONE);
        }

        int level = 1;
        //int difficulty = getPreferences(MODE_PRIVATE).getInt(Common.PREF_KEY3, Common.MEDIUM);
        int difficulty = Prefs.getDifficultly(this);
        switch (difficulty) {
            case 0:
                level = Prefs.getMaxLevel(this);
                //level = getPreferences(MODE_PRIVATE).getInt(Common.PREF_KEY0, 1);                
                break;
            case 1:
                level = Prefs.getMaxLevel(this);
                //level = getPreferences(MODE_PRIVATE).getInt(Common.PREF_KEY1, 1);
                break;
            case 2:
                level = Prefs.getMaxLevel(this);
                //level = getPreferences(MODE_PRIVATE).getInt(Common.PREF_KEY2, 1);
                break;
        }

        mStoreKeeperView.setLevel(difficulty, level);

        mStoreKeeperView.initNewGame(difficulty);

        StartGame(difficulty, level, 0);

        if (Prefs.getMusic(this))
            Music.play(this, R.raw.main);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBundle(Common.ICICLE_KEY, mStoreKeeperView.saveState());
    }

    private void openNewGameDialog() {

        new AlertDialog.Builder(this).setTitle(R.string.new_game_title).setItems(R.array.difficulty, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialoginterface, int difficulty) {

                //getPreferences(MODE_PRIVATE).edit().putInt(Common.PREF_KEY3, difficulty).commit();
                Prefs.setDifficultly(Main.this, difficulty);

                int maxLevel = 1;
                switch (difficulty) {
                    case Common.EASY:
                        maxLevel = Prefs.getMaxLevel(Main.this);
                        break;
                    case Common.MEDIUM:
                        maxLevel = Prefs.getMaxLevel(Main.this);
                        break;
                    case Common.HARD:
                        maxLevel = Prefs.getMaxLevel(Main.this);
                        break;
                }
                StartGame(difficulty, maxLevel, 0);
            }
        }).show();
    }

    public boolean isExistsHistory(int difficulty, int level) {
        return this.application.getDataHelper().queryHistoryExists(difficulty, level);
    }

    public Cursor getHistory(int difficulty, int level) {
        return this.application.getDataHelper().queryHistoryCursor(difficulty, level);
    }

    public void insertHistory(int difficulty, int level, ArrayList<PlaySteps> history) {
        this.application.getDataHelper().insertHistory(difficulty, level, history);
    }

    public void StartGame(int difficulty, int playLevel, int offSet) {
        //Log.e("StoreKeeperView", "difficulty : " + difficulty);
        //Log.e("StoreKeeperView", "difficulty : " + difficulty);
        mStoreKeeperView.setMode(StoreKeeperView.READY);
        mStoreKeeperView.startGame(difficulty, playLevel, offSet);
        mStoreKeeperView.setMode(StoreKeeperView.RUNNING);

        if (Prefs.getMusic(this)) {
            Music.play(this, R.raw.game);
        }
    }
}
