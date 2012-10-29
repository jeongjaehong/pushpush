package com.nilriri.android.Storekeeper;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import com.nilriri.android.Common;
import com.nilriri.android.MapItem;
import com.nilriri.android.PlayStep;
import com.nilriri.android.PlaySteps;
import com.nilriri.android.StoreKeeper;

public class StoreKeeperView extends MapView {

    // 게임의 상태정보
    private int mMode = READY;
    private static final int PLAYBAKLIMIT = 6;
    public static final int PAUSE = 0;

    public static final int READY = 1;
    public static final int RUNNING = 2;
    public static final int LOSE = 3;
    // 창고지기의 이동제어
    private int mDirection = Common.UP;
    public int mNextDirection = Common.UP;

    // 점수및 화면 갱신주기
    private int mLevel = 0;
    private int mDifficulty = 0;

    private long mScore = 0;

    private long mMoveDelay = 10;
    private long mLastMove;

    private TextView mMsgText;
    private TextView mInfoText;

    private StoreKeeper newStorekeeper = null;

    public StoreKeeper mStorekeeper = null;;

    private ArrayList<MapItem> mMapItemList = new ArrayList<MapItem>();
    private ArrayList<PlaySteps> mPlayStepList = new ArrayList<PlaySteps>();
    private ArrayList<PlaySteps> mPlayHistory = new ArrayList<PlaySteps>();
    private Cursor mHistoryCursor;

    private Handler mHandler;

    private final Main main;

    public StoreKeeperView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.main = (Main) context;
        initWarehouseView();

    }

    private int[] coordArrayListToArray(ArrayList<MapItem> cvec) {
        int count = cvec.size();
        int[] rawArray = new int[count * 2];
        for (int index = 0; index < count; index++) {
            MapItem c = cvec.get(index);
            rawArray[2 * index] = c.x;
            rawArray[2 * index + 1] = c.y;
        }
        return rawArray;
    }

    private void delMapItem(MapItem mapitem) {
        int itemcount = mMapItemList.size() - 1;

        for (int stockindex = itemcount; stockindex >= 0; stockindex--) {
            MapItem mapItem = mMapItemList.get(stockindex);

            if (mapItem.equals(mapitem)) {
                mMapItemList.remove(stockindex);
            }
        }
    }

    private void delMapItem(PlayStep step) {
        int itemcount = mMapItemList.size() - 1;

        for (int index = itemcount; index >= 0; index--) {
            MapItem mapItem = mMapItemList.get(index);

            if (mapItem.equalpos(step)) {
                mMapItemList.remove(index);
            }
        }
    }

    private void drawMap(int difficulty) {

        Resources res = getContext().getResources();
        String mapStr = "";

        int startLevel = 0;
        int endLevel = 0;

        switch (difficulty) {
            case Common.EASY:
                startLevel = R.string.easy001;
                endLevel = res.getInteger(R.integer.easyend);
                break;
            case Common.MEDIUM:
                startLevel = R.string.medium001;
                endLevel = res.getInteger(R.integer.mediumend);
                break;
            case Common.HARD:
                startLevel = R.string.hard001;
                endLevel = res.getInteger(R.integer.hardend);
                break;
            default:
                //Log.e(TAG, "Difficulty is " + difficulty);
        }

        int resId = startLevel + mLevel - 1;

        //Log.e(TAG, "resId is " + resId);
        //Log.e(TAG, "endLevel is " + endLevel);

        if (mLevel <= endLevel) {
            mapStr = res.getString(resId);
        } else {

            Toast.makeText(getContext(), res.getString(R.string.msg_ending), Toast.LENGTH_LONG).show();

            mapStr = res.getString(R.string.endingmap);
        }

        //Log.e(TAG, "mapStr is " + mapStr);
        if ("".equals(mapStr) || mapStr.length() < 400) {
            mMsgText.setText("Map not found... ");
            mMsgText.setVisibility(View.VISIBLE);
            return;
        } else {
            if (mMsgText != null) {
                mMsgText.setText("Level : " + this.getLevel() + " / " + endLevel);
                mMsgText.setVisibility(View.VISIBLE);
            }
        }

        if (this.main.isExistsHistory(Prefs.getDifficultly(this.main), mLevel)) {
            mInfoText.setText("Play Data Exists...");
        } else {
            mInfoText.setText("");
        }

        String[] map = MapUtil.Str2Map(mapStr, ",");

        for (int x = 0; x < map.length && x < mXTileCount; x++) {
            for (int y = 0; y < map[0].length() && y < mYTileCount; y++) {
                int itemIndex = Integer.parseInt(map[y].charAt(x) + "");

                if (itemIndex == Common.MAN) {
                    mStorekeeper = new StoreKeeper(x, y);
                    setMapItem(Common.BLANK, x, y);
                } else if (itemIndex == Common.NO || itemIndex == Common.YES || itemIndex == Common.HOME || itemIndex == Common.LINE) {
                    mMapItemList.add(new MapItem(x, y, itemIndex));
                    setMapItem(Common.BLANK, x, y);
                } else {
                    setMapItem(itemIndex, x, y);
                }
            }
        }

    }

    public int getLevel() {
        return this.mLevel;
    }

    private MapItem getMapItembyStorekeeper(MapItem mapitem) {
        int itemcount = mMapItemList.size();

        for (int index = 0; index < itemcount; index++) {
            MapItem mapItem = mMapItemList.get(index);

            if (mapItem.equalpos(mapitem)) {
                return mapItem;
            }
        }
        return new MapItem(mapitem.x, mapitem.y, Common.BLANK);
    }

    private MapItem getMapItembyStorekeeper(StoreKeeper storekeeper) {
        int itemcount = mMapItemList.size();

        for (int index = 0; index < itemcount; index++) {
            MapItem mapItem = mMapItemList.get(index);

            if (mapItem.equalpos(storekeeper)) {
                return mapItem;
            }
        }
        return new MapItem(-1, -1, Common.BLANK);
    }

    public void initNewGame(int difficulty) {

        mStorekeeper = null;
        newStorekeeper = null;

        this.mMapItemList.clear();

        mPlayStepList.clear();
        mPlayHistory.clear();

        clearMap();
        drawMap(difficulty);
        mMoveDelay = 50;
        mScore = 0;
    }

    private void initWarehouseView() {
        setFocusable(true);

        Resources r = this.getContext().getResources();

        resetTiles(10);
        loadMapItem(Common.LINE, r.getDrawable(R.drawable.l));
        loadMapItem(Common.HOME, r.getDrawable(R.drawable.h));
        loadMapItem(Common.YES, r.getDrawable(R.drawable.y));
        loadMapItem(Common.NO, r.getDrawable(R.drawable.n));
        loadMapItem(Common.MAN, r.getDrawable(R.drawable.m));
        loadMapItem(Common.BLANK, r.getDrawable(R.drawable.b));

    }

    public boolean isClearLevel() {
        int itemcount = mMapItemList.size();

        int yesCnt = 0;

        for (int index = 0; index < itemcount; index++) {
            MapItem mapItem = mMapItemList.get(index);

            if (mapItem.state == Common.NO) {
                return false;
            }
            if (mapItem.state == Common.YES) {
                yesCnt++;
            }
        }

        return (yesCnt > 0);
    }

    private boolean isValidMove(MapItem olditem, MapItem nextitem) {

        if ((olditem.state == Common.HOME || olditem.state == Common.BLANK) || (nextitem.state == Common.BLANK || nextitem.state == Common.HOME)) {
            return true;
        }
        //Log.e(TAG, "olditem:" + olditem.toString());
        //Log.e(TAG, "nextitem:" + nextitem.toString());
        return false;

    }

    public boolean moveStorekeeper(boolean isPlay) {

        StoreKeeper tmpStorekeeper = mStorekeeper;

        //Log.e(TAG, "mStorekeeper start :" + mStorekeeper.toString());

        int newx = 0;
        int newy = 0;

        mDirection = mNextDirection;

        switch (mDirection) {
            case Common.RIGHT: {
                newx = 1;
                newy = 0;
                break;
            }
            case Common.LEFT: {
                newx = -1;
                newy = 0;
                break;
            }
            case Common.UP: {
                newx = 0;
                newy = -1;
                break;
            }
            case Common.DOWN: {
                newx = 0;
                newy = 1;
                break;
            }
            default:
                return false;
        }
        // 창고지기의 새로 옮겨갈 위치.
        tmpStorekeeper = new StoreKeeper(mStorekeeper.x + newx, mStorekeeper.y + newy);

        // 새로 옮겨갈 위치에있는 기존 아이템 확인.
        MapItem oldItem = getMapItembyStorekeeper(tmpStorekeeper);

        // 기존 아이템이 밀려나가야하면 밀려나가야할 위치에 있는 아이템
        MapItem nextItem = getMapItembyStorekeeper(new MapItem(oldItem.x + newx, oldItem.y + newy, Common.BLANK));

        //Log.e(TAG, "oldItem:" + oldItem.toString());
        //Log.e(TAG, "nextItem:" + nextItem.toString());

        // 밀려나가야할 아이템이 밀릴수 없거나 다음 이동장소가 벽인경우에는 이동취소.
        if (!isValidMove(oldItem, nextItem)) {
            //Log.e(TAG, "Return...");
            if (!this.main.isDragMove) {
                startAnimation(AnimationUtils.loadAnimation(main, R.anim.shake));
            }

            update();
            return false;
        }

        PlaySteps play = new PlaySteps();

        // 기존 아이템의 상태에 따라 상태변경.
        play.add(mStorekeeper.x, mStorekeeper.y, getMapItembyStorekeeper(mStorekeeper).state, Common.MAN);
        switch (oldItem.state) {
            case Common.BLANK: {
                // 공터
                break;
            }
            case Common.LINE: {
                // 벽이 있으면 리턴      
                if (!this.main.isDragMove) {
                    startAnimation(AnimationUtils.loadAnimation(main, R.anim.shake));
                }
                return false;
            }
            case Common.HOME: {
                // 창고.
                break;
            }
            case Common.YES: { //창고에있는짐
                //mStockList.remove(oldItem);
                if (nextItem.state == Common.HOME) {
                    play.add(oldItem.x, oldItem.y, oldItem.state, Common.HOME);
                    play.add(nextItem.x, nextItem.y, nextItem.state, Common.YES);

                    delMapItem(oldItem);
                    delMapItem(nextItem);
                    mMapItemList.add(new MapItem(oldItem.x, oldItem.y, Common.HOME));
                    mMapItemList.add(new MapItem(nextItem.x, nextItem.y, Common.YES));
                } else {
                    play.add(oldItem.x, oldItem.y, oldItem.state, Common.HOME);
                    play.add(nextItem.x, nextItem.y, nextItem.state, Common.NO);

                    delMapItem(oldItem);
                    delMapItem(nextItem);
                    mMapItemList.add(new MapItem(oldItem.x, oldItem.y, Common.HOME));
                    mMapItemList.add(new MapItem(nextItem.x, nextItem.y, Common.NO));
                }
                break;
            }
            case Common.NO: { //공터에 있는짐
                if (nextItem.state == Common.HOME) {
                    play.add(oldItem.x, oldItem.y, oldItem.state, Common.BLANK);
                    play.add(nextItem.x, nextItem.y, nextItem.state, Common.YES);

                    delMapItem(oldItem);
                    delMapItem(nextItem);
                    mMapItemList.add(new MapItem(oldItem.x, oldItem.y, Common.BLANK));
                    mMapItemList.add(new MapItem(nextItem.x, nextItem.y, Common.YES));
                } else {
                    play.add(oldItem.x, oldItem.y, oldItem.state, Common.BLANK);
                    play.add(nextItem.x, nextItem.y, nextItem.state, oldItem.state);

                    delMapItem(oldItem);
                    delMapItem(nextItem);
                    mMapItemList.add(new MapItem(oldItem.x, oldItem.y, Common.BLANK));
                    mMapItemList.add(new MapItem(nextItem.x, nextItem.y, oldItem.state));
                }
                break;
            }
        }

        mPlayStepList.add(play);
        if (!isPlay) {
            mPlayHistory.add(play);
        }
        Log.e("XX", "mPlayStepList=" + mPlayStepList.size());
        // 가장 최근목록만 남겨놓고 삭제한다.
        for (int idx = 0; idx < mPlayStepList.size() - PLAYBAKLIMIT; idx++) {
            Log.e("XX", "Action delete index=" + idx);
            mPlayStepList.remove(idx);
        }

        // 이동에 따른 아이템 설정이 완료되면 
        newStorekeeper = tmpStorekeeper;

        setMapItem(Common.BLANK, mStorekeeper.x, mStorekeeper.y);

        if (isPlay) {
            mStorekeeper = newStorekeeper;
            update();
        }
        return true;
    }

    private void undoStorekeeper(PlaySteps undoPlay) {

        for (int i = 0; i < undoPlay.play.size(); i++) {
            PlayStep step = undoPlay.play.get(i);

            //Log.e(TAG, "PlayStep:" + step.toString());
            switch (step.after) {
                case Common.MAN: {
                    setMapItem(Common.BLANK, mStorekeeper.x, mStorekeeper.y);
                    newStorekeeper = new StoreKeeper(step.x, step.y);
                    break;
                }
                default: {
                    //setMapItem(BLANK, step.x, step.y);
                    delMapItem(step);
                    mMapItemList.add(new MapItem(step.x, step.y, step.before));
                }
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent msg) {

        //Log.d(TAG, "onKeyDown: keycode=" + keyCode + ", event=" + msg);
        //int difficulty = main.getPreferences(Context.MODE_PRIVATE).getInt(Common.PREF_KEY3, Common.MEDIUM);
        //int difficulty = Prefs.getDifficultly(this.getContext());

        //startGame(difficulty, mLevel, 0);

        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            mNextDirection = Common.UP;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            mNextDirection = Common.DOWN;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            mNextDirection = Common.LEFT;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            mNextDirection = Common.RIGHT;
        } else {
            return false;
        }
        moveStorekeeper(false);
        update();

        return super.onKeyDown(keyCode, msg);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //Log.e(TAG, "onSizeChanged:w=" + w + ",h=" + h + ",oldw=" + oldw + ",oldh=" + oldh);
        //int difficulty = main.getPreferences(Context.MODE_PRIVATE).getInt(Common.PREF_KEY3, Common.MEDIUM);
        int difficulty = Prefs.getDifficultly(this.getContext());

        drawMap(difficulty);
        update();
    }

    public void restoreState(Bundle icicle) {
        setMode(PAUSE);

        mMapItemList = stockArrayToArrayList(icicle.getIntArray("mStockList"));
        mDirection = icicle.getInt("mDirection");
        mNextDirection = icicle.getInt("mNextDirection");
        mMoveDelay = icicle.getLong("mMoveDelay");
        mScore = icicle.getLong("mScore");
        mStorekeeper = storekeeperArrayToStorekeeper(icicle.getIntArray("mStoreKeeper"));
    }

    public Bundle saveState() {
        Bundle map = new Bundle();

        map.putIntArray("mStockList", coordArrayListToArray(mMapItemList));
        map.putInt("mDirection", Integer.valueOf(mDirection));
        map.putInt("mNextDirection", Integer.valueOf(mNextDirection));
        map.putLong("mMoveDelay", Long.valueOf(mMoveDelay));
        map.putLong("mScore", Long.valueOf(mScore));
        map.putIntArray("mStoreKeeper", StorekeeperToArray(mStorekeeper));

        return map;
    }

    public void playHistory(int difficulty, int level) {

        this.setMode(RUNNING);

        if (this.main.isExistsHistory(difficulty, level)) {
            mHistoryCursor = this.main.getHistory(difficulty, level);
            mHandler = new Handler();
            mHistoryPlayer.run();
        } else {
            Toast.makeText(getContext(), getResources().getString(R.string.msg_notfoundsavedata), Toast.LENGTH_LONG).show();

        }
    }

    public void setLevel(int difficulty, int level) {
        try {
            this.mLevel = level;
            this.mDifficulty = difficulty;
        } catch (Exception e) {
            this.mLevel = 1;
            this.mDifficulty = 0;//EASY
        }

        Prefs.setCurLevel(this.main, this.mLevel);
    }

    public int getDifficulty() {
        return this.mDifficulty;
    }

    public void setMode(int newMode) {
        int oldMode = mMode;
        mMode = newMode;

        if (newMode == RUNNING & oldMode != RUNNING) {

            update();
            return;
        }

        Resources res = getContext().getResources();
        CharSequence str = "";
        if (newMode == PAUSE) {
            str = res.getText(R.string.mode_pause);
        } else if (newMode == READY) {
            str = res.getText(R.string.mode_ready);
        } else

        if (!"".equals(str))
            Toast.makeText(getContext(), str, Toast.LENGTH_LONG).show();

    }

    public void setMsgView(TextView msgView, TextView infoView) {
        mMsgText = msgView;
        mInfoText = infoView;
    }

    public void startGame(int difficulty, int maxLevel, int offSet) {

        if (mLevel + offSet <= maxLevel && mLevel + offSet >= 1) {
            this.setLevel(difficulty, mLevel + offSet);
        } else {
            if (1 > mLevel + offSet) {
                this.setLevel(difficulty, 1);
            } else {
                this.setLevel(difficulty, maxLevel);
            }
        }

        resetItemSize(getWidth(), getHeight(), this.mLevel);
        if (mMode == READY | mMode == LOSE) {
            //Log.e(TAG, "startGame()...");

            initNewGame(difficulty);
            setMode(RUNNING);

            update();
        } else if (mMode == PAUSE) {
            setMode(RUNNING);

            update();
        }
    }

    private ArrayList<MapItem> stockArrayToArrayList(int[] rawArray) {
        ArrayList<MapItem> stockArrayList = new ArrayList<MapItem>();

        int coordCount = rawArray.length;
        for (int index = 0; index < coordCount; index += 3) {
            MapItem c = new MapItem(rawArray[index], rawArray[index + 1], rawArray[index + 2]);
            stockArrayList.add(c);
        }
        return stockArrayList;
    }

    private StoreKeeper storekeeperArrayToStorekeeper(int[] rawArray) {
        if (rawArray.length > 1) {
            StoreKeeper storekeeper = new StoreKeeper(rawArray[0], rawArray[1]);

            return storekeeper;
        } else {
            return null;
        }
    }

    private int[] StorekeeperToArray(StoreKeeper storekeeper) {
        int[] rawArray = new int[2];

        rawArray[0] = storekeeper.x;
        rawArray[1] = storekeeper.y;

        return rawArray;
    }

    public void update() {
        if (mMode == RUNNING) {
            long now = System.currentTimeMillis();

            if (now - mLastMove > mMoveDelay) {
                //clearMap();
                //drawMap();
                updateMap();
                mLastMove = now;
            }

            invalidate();

            if (isClearLevel()) {
                //main.nextLevelStart(getLevel() + 1);

                Intent i = new Intent(main, Alert.class);

                main.startActivity(i);

                mPlayStepList.clear();

                if (mPlayHistory.size() > 2) {
                    this.main.insertHistory(mDifficulty, this.getLevel(), mPlayHistory);
                }

                mPlayHistory.clear();

                int clearLevel = getLevel() + 1;
                switch (getDifficulty()) {
                    case 0:
                        //if (clearLevel > main.getPreferences(Main.MODE_PRIVATE).getInt(Common.PREF_KEY0, 1))
                        //main.getPreferences(Main.MODE_PRIVATE).edit().putInt(Common.PREF_KEY0, clearLevel).commit();
                        if (clearLevel > Prefs.getMaxLevel(this.getContext()))
                            Prefs.setMaxLevel(this.getContext(), clearLevel);
                        break;
                    case 1:
                        if (clearLevel > Prefs.getMaxLevel(this.getContext()))
                            Prefs.setMaxLevel(this.getContext(), clearLevel);
                        break;
                    case 2:
                        if (clearLevel > Prefs.getMaxLevel(this.getContext()))
                            Prefs.setMaxLevel(this.getContext(), clearLevel);
                        break;
                }

                setMode(StoreKeeperView.READY);
                startGame(getDifficulty(), getLevel() + 1, 1);
                setMode(StoreKeeperView.RUNNING);

            }
        }

    }

    private void updateMap() {

        int itemCount = mMapItemList.size();

        for (int stockindex = 0; stockindex < itemCount; stockindex++) {
            MapItem mapItem = mMapItemList.get(stockindex);

            setMapItem(mapItem.state, mapItem.x, mapItem.y);

        }

        if (newStorekeeper != null) {
            mStorekeeper = newStorekeeper;
        }

        if (mStorekeeper != null) {
            setMapItem(Common.MAN, mStorekeeper.x, mStorekeeper.y);
        }

    }

    public void playBack() {
        if (mPlayStepList.size() > 0) {
            int lastAction = mPlayStepList.size() - 1;
            undoStorekeeper(mPlayStepList.get(lastAction));
            mPlayStepList.remove(lastAction);
            update();
        }
        Log.d("XXXXXXX", "Size=" + mPlayStepList.size());
    }

    private Runnable mHistoryPlayer = new Runnable() {
        public void run() {
            if (mHistoryCursor.moveToNext()) {

                //Log.e("mForceCheckBoxRunnable", "before=" + mStorekeeper.toString());
                if (mHistoryCursor.getInt(0) > mStorekeeper.x) {
                    mNextDirection = Common.RIGHT;
                    //Log.e("XXXX", "mNextDirection=RIGHT" + mStorekeeper.toString());
                } else if (mHistoryCursor.getInt(0) < mStorekeeper.x) {
                    mNextDirection = Common.LEFT;
                    //Log.e("XXXX", "mNextDirection=LEFT" + mStorekeeper.toString());
                } else if (mHistoryCursor.getInt(1) > mStorekeeper.y) {
                    mNextDirection = Common.DOWN;
                    //Log.e("XXXX", "mNextDirection=DOWN" + mStorekeeper.toString());
                } else if (mHistoryCursor.getInt(1) < mStorekeeper.y) {
                    mNextDirection = Common.UP;
                    //Log.e("XXXX", "mNextDirection=UP" + mStorekeeper.toString());
                } else {
                    //Log.e("XXXX", "=============return=================");
                    mHandler.postDelayed(this, 500);
                    return;
                }
                //Log.e("XXXX", "move==============================");
                moveStorekeeper(true);

            } else {
                mHandler.removeCallbacks(mHistoryPlayer);
                return;
            }

            // Force toggle again in a second
            mHandler.postDelayed(this, 500);
        }
    };

}
