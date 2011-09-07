/*
 * Copyright (C) 2010 The Jeong Jaehong (jeongjaehong@gmail.com)
 *
 */

package com.nilriri.android.Storekeeper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.nilriri.android.Common;
import com.nilriri.android.OldEvent;

public class StoreKeeperMain extends Activity implements OnTouchListener {

	private StoreKeeperView mStoreKeeperView;
	private OldEvent oldEvent;

	// Menu item ids
	public static final int MENU_ITEM_ABOUT = Menu.FIRST;
	public static final int MENU_ITEM_PLAYBACK = Menu.FIRST + 1;
	public static final int MENU_ITEM_PLAY = Menu.FIRST + 2;
	public static final int MENU_ITEM_RELOADLEVEL = Menu.FIRST + 3;

	private void openNewGameDialog() {

		new AlertDialog.Builder(this).setTitle(R.string.new_game_title)
				.setItems(R.array.difficulty,
						new DialogInterface.OnClickListener() {
							public void onClick(
									DialogInterface dialoginterface,
									int difficulty) {

								// getPreferences(MODE_PRIVATE).edit().putInt(Common.PREF_KEY3,
								// difficulty).commit();
								Prefs.setDifficultly(StoreKeeperMain.this,
										difficulty);

								int maxLevel = 1;
								switch (difficulty) {
								case Common.EASY:
									maxLevel = Prefs
											.getMaxLevel(StoreKeeperMain.this);
									break;
								case Common.MEDIUM:
									maxLevel = Prefs
											.getMaxLevel(StoreKeeperMain.this);
									break;
								case Common.HARD:
									maxLevel = Prefs
											.getMaxLevel(StoreKeeperMain.this);
									break;
								}
								StartGame(difficulty, maxLevel, 0);
							}
						}).show();
	}

	public void StartGame(int difficulty, int playLevel, int offSet) {
		// Log.e("StoreKeeperView", "difficulty : " + difficulty);
		// Log.e("StoreKeeperView", "difficulty : " + difficulty);
		mStoreKeeperView.setMode(StoreKeeperView.READY);
		mStoreKeeperView.startGame(difficulty, playLevel, offSet);
		mStoreKeeperView.setMode(StoreKeeperView.RUNNING);

		if (Prefs.getMusic(this)) {
			Music.play(this, R.raw.game);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.main);

		mStoreKeeperView = (StoreKeeperView) findViewById(R.id.warehouseman);
		mStoreKeeperView.setMsgView((TextView) findViewById(R.id.msg),
				(TextView) findViewById(R.id.info));

		// Log.e("StoreKeeperView", "fileList : " + this.fileList().toString());

		if (savedInstanceState == null) {
			mStoreKeeperView.setMode(StoreKeeperView.READY);
		} else {
			Bundle map = savedInstanceState.getBundle(Common.ICICLE_KEY);
			if (map != null) {
				mStoreKeeperView.restoreState(map);

				// ����� �� �����Ͱ� ���������� ������� ������ ó������ �ٽ�
				// �����ϵ��� �Ѵ�.
				if (this.mStoreKeeperView.mStorekeeper == null) {
					StartGame(Prefs.getDifficultly(this), Prefs
							.getMaxLevel(this), 0);
				}
			} else {
				mStoreKeeperView.setMode(StoreKeeperView.PAUSE);
			}
		}

		mStoreKeeperView.setOnCreateContextMenuListener(this);
		mStoreKeeperView.setOnTouchListener(this);
		oldEvent = new OldEvent(-1, -1);
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			oldEvent.set(event.getX(), event.getY());
			mStoreKeeperView.setMode(StoreKeeperView.RUNNING);
			return false;
		case MotionEvent.ACTION_UP:
			float xoffset = event.getX() - oldEvent.getX();
			float yoffset = event.getY() - oldEvent.getY();

			Vibrator vi = (Vibrator) this.getSystemService(VIBRATOR_SERVICE);
			vi.vibrate(new long[] { 250 }, 0);

			if (xoffset > 10 && Math.abs(xoffset) > Math.abs(yoffset)) {
				mStoreKeeperView.mNextDirection = Common.RIGHT;
			} else if (xoffset < -10 && Math.abs(xoffset) > Math.abs(yoffset)) {
				mStoreKeeperView.mNextDirection = Common.LEFT;
			} else if (yoffset > 10 && Math.abs(xoffset) < Math.abs(yoffset)) {
				mStoreKeeperView.mNextDirection = Common.DOWN;
			} else if (yoffset < -10 && Math.abs(xoffset) < Math.abs(yoffset)) {
				mStoreKeeperView.mNextDirection = Common.UP;
			} else {
				float manx = (mStoreKeeperView.mStorekeeper.x * mStoreKeeperView
						.getItemSize())
						+ (mStoreKeeperView.getItemSize() * 0.5f);
				float many = (mStoreKeeperView.mStorekeeper.y * mStoreKeeperView
						.getItemSize())
						+ (mStoreKeeperView.getItemSize() * 0.5f);

				xoffset = event.getX() - manx;
				yoffset = event.getY() - many;

				if (xoffset > 10 && Math.abs(xoffset) > Math.abs(yoffset)) {
					mStoreKeeperView.mNextDirection = Common.LEFT;
				} else if (xoffset < -10
						&& Math.abs(xoffset) > Math.abs(yoffset)) {
					mStoreKeeperView.mNextDirection = Common.RIGHT;
				} else if (yoffset > 10
						&& Math.abs(xoffset) < Math.abs(yoffset)) {
					mStoreKeeperView.mNextDirection = Common.UP;
				} else if (yoffset < -10
						&& Math.abs(xoffset) < Math.abs(yoffset)) {
					mStoreKeeperView.mNextDirection = Common.DOWN;
				} else {
					break;
				}
			}
			// �ʱ�ȭ.
			oldEvent.set(event.getX(), event.getY());
			if (!mStoreKeeperView.moveStorekeeper(false)) {
				vi.vibrate(new long[] { 250, 50, 100 }, 2);
			}
			mStoreKeeperView.update();
			break;
		default:
			return false;
			// return super.onTouchEvent(event);

		}

		return false;

	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View view,
			ContextMenuInfo menuInfo) {

		// Setup the menu header
		menu.setHeaderTitle(getResources().getString(R.string.apptitle));

		menu.add(0, MENU_ITEM_ABOUT, 0, R.string.about_help_label);
		menu.add(0, MENU_ITEM_RELOADLEVEL, 0, R.string.reload_level);
		menu.add(0, MENU_ITEM_PLAY, 0, R.string.saved_step_play);

	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		switch (item.getItemId()) {

		case MENU_ITEM_RELOADLEVEL:
			StartGame(Prefs.getDifficultly(this), Prefs.getMaxLevel(this), 0);
			return true;
		case MENU_ITEM_PLAY: {

			mStoreKeeperView.playHistory(Prefs.getDifficultly(this),
					mStoreKeeperView.getLevel());
			return true;
		}
		case MENU_ITEM_ABOUT: {
			startActivity(new Intent(this, About.class));

			return true;
		}

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
		// int difficulty =
		// getPreferences(MODE_PRIVATE).getInt(Common.PREF_KEY3, Common.MEDIUM);
		int difficulty = Prefs.getDifficultly(this);

		int maxLevel = 1;
		switch (difficulty) {
		case 0:
			maxLevel = Prefs.getMaxLevel(StoreKeeperMain.this);
			break;
		case 1:
			maxLevel = Prefs.getMaxLevel(StoreKeeperMain.this);
			break;
		case 2:
			maxLevel = Prefs.getMaxLevel(StoreKeeperMain.this);
			break;
		}

		switch (item.getItemId()) {

		case R.id.prevlevel:
			StartGame(difficulty, maxLevel, -1);
			return true;
		case R.id.nextlevel:
			if (mStoreKeeperView.isClearLevel()) {
				int clearLevel = mStoreKeeperView.getLevel() + 1;

				if (maxLevel >= clearLevel) {
					switch (difficulty) {
					case 0:
						Prefs.setMaxLevel(this, clearLevel);
						// getPreferences(MODE_PRIVATE).edit().putInt(Common.PREF_KEY0,
						// clearLevel).commit();
						break;
					case 1:
						Prefs.setMaxLevel(this, clearLevel);
						// getPreferences(MODE_PRIVATE).edit().putInt(Common.PREF_KEY1,
						// clearLevel).commit();
						break;
					case 2:
						Prefs.setMaxLevel(this, clearLevel);
						// getPreferences(MODE_PRIVATE).edit().putInt(Common.PREF_KEY2,
						// clearLevel).commit();
						break;
					}
				}
				StartGame(difficulty, maxLevel, 1);
			} else {
				StartGame(difficulty, mStoreKeeperView.getLevel() + 1, 1);
			}

			return true;
		case R.id.reloadlevel:
			StartGame(difficulty, maxLevel, 0);
			return true;
		case R.id.newgame:
			openNewGameDialog();
			return true;
		case R.id.playback: {

			if (Prefs.getPlayback(this)) {
				Log.e("XX", "Action Cancel...");
				mStoreKeeperView.playBack();
			} else {

				Toast.makeText(this,
						getResources().getString(R.string.notfound_playback),
						Toast.LENGTH_LONG).show();

				// Log.e(TAG, "Action cncel option is FALSE ");
				mStoreKeeperView.startAnimation(AnimationUtils.loadAnimation(
						this, R.anim.shake));
				return (true);
			}

			return true;
		}
		case R.id.playlevel:

			mStoreKeeperView.playHistory(difficulty, mStoreKeeperView
					.getLevel());
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
			// int difficulty =
			// getPreferences(MODE_PRIVATE).getInt(Common.PREF_KEY3,
			// Common.MEDIUM);
			int difficulty = Prefs.getDifficultly(this);
			switch (difficulty) {
			case 0:
				Prefs.setMaxLevel(this, mStoreKeeperView.getLeft());
				// getPreferences(MODE_PRIVATE).edit().putInt(Common.PREF_KEY0,
				// mStoreKeeperView.getLeft()).commit();
				break;
			case 1:
				Prefs.setMaxLevel(this, mStoreKeeperView.getLeft());
				// getPreferences(MODE_PRIVATE).edit().putInt(Common.PREF_KEY1,
				// mStoreKeeperView.getLeft()).commit();
				break;
			case 2:
				Prefs.setMaxLevel(this, mStoreKeeperView.getLeft());
				// getPreferences(MODE_PRIVATE).edit().putInt(Common.PREF_KEY2,
				// mStoreKeeperView.getLeft()).commit();
				break;
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		int level = 1;
		// int difficulty =
		// getPreferences(MODE_PRIVATE).getInt(Common.PREF_KEY3, Common.MEDIUM);
		int difficulty = Prefs.getDifficultly(this);
		switch (difficulty) {
		case 0:
			level = Prefs.getMaxLevel(this);
			// level = getPreferences(MODE_PRIVATE).getInt(Common.PREF_KEY0, 1);
			break;
		case 1:
			level = Prefs.getMaxLevel(this);
			// level = getPreferences(MODE_PRIVATE).getInt(Common.PREF_KEY1, 1);
			break;
		case 2:
			level = Prefs.getMaxLevel(this);
			// level = getPreferences(MODE_PRIVATE).getInt(Common.PREF_KEY2, 1);
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

}
