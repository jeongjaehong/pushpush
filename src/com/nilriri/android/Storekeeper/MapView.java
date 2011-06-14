package com.nilriri.android.Storekeeper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Toast;

import com.nilriri.android.Common;

public class MapView extends View {
    public static final int MODE_PRIVATE = 0;

    protected int mItemSize;

    protected static int mXTileCount;
    protected static int mYTileCount;

    private static int mXOffset;
    private static int mYOffset;
    private int colCount;

    private Bitmap[] mMapItemArray;

    private int[][] mMap;

    private Context mContext;

    private final Paint mPaint = new Paint();

    public int mWidth = 0;
    public int mHeight = 0;

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;

        // TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TileView);
        //mItemSize = a.getInt(R.styleable.TileView_tileSize, 36);

        int difficulty = Prefs.getDifficultly(context);
        //getPreferences(MODE_PRIVATE).getInt(Common.PREF_KEY3, Common.MEDIUM);
        //Prefs.getDifficultly(context);
        int mLevel = Prefs.getMaxLevel(context);

        int startLevel = 0;
        int endLevel = 0;

        switch (difficulty) {
            case Common.EASY:
                startLevel = R.string.easy001;
                endLevel = context.getResources().getInteger(R.integer.easyend);
                break;
            case Common.MEDIUM:
                startLevel = R.string.medium001;
                endLevel = context.getResources().getInteger(R.integer.mediumend);
                break;
            case Common.HARD:
                startLevel = R.string.hard001;
                endLevel = context.getResources().getInteger(R.integer.hardend);
                break;

        }

        int resId = startLevel + mLevel - 1;

        Log.d(" resId", "  resId= " + resId);

        String mapStr = "";
        if (mLevel <= endLevel) {
            mapStr = context.getResources().getString(resId);
        } else {
            mapStr = context.getResources().getString(R.string.endingmap);
        }

        mapStr = mapStr.replace("9", " ");

        String maps[] = Common.tokenFn(mapStr, ",");

        colCount = 0;

        int rowCount = 0;
        for (int i = 0; i < maps.length; i++) {
            if (!"".equals(maps[i].trim()))
                rowCount += 1;

            Log.d(" maps[i].trim()", "  maps[i].trim()= " + maps[i].trim() + "=" + maps[i].trim().length());
            if (colCount < maps[i].trim().length()) {
                Log.d(" maps[i].trim()", "  maps[i].trim()= " + maps[i].trim() + "=" + maps[i].trim().length());
                colCount = maps[i].trim().length();
            }
        }

        colCount = colCount > rowCount ? colCount : rowCount;

        mItemSize = Prefs.getIconsize(context);

        //Log.d("RRRRRRRRRRR", " maxCount= " + maxCount + ", rowCount= " + rowCount + ", mItemSize= " + mItemSize);
        //Log.d("XXXXXXXXXXXX", " mXTileCount= " + mXTileCount + ", mYTileCount= " + mYTileCount + ", mItemSize= " + mItemSize);
        /*
        mXTileCount = (int) Math.floor(this.getWidth() / mItemSize);
        mYTileCount = (int) Math.floor(this.getHeight() / mItemSize);
        if (maxCount > mXTileCount || maxCount > mYTileCount) {

            mItemSize = (int) Math.floor(this.getWidth() / maxCount);

            String msg = mContext.getResources().getString(R.string.change_iconsize);
            msg += " (" + mItemSize + " x " + mItemSize + ") ";
            Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();

            mXTileCount = (int) Math.floor(this.getWidth() / mItemSize);
            mYTileCount = (int) Math.floor(this.getHeight() / mItemSize);

        }
        */

    }

    public void clearMap() {
        for (int x = 0; x < mXTileCount; x++) {
            for (int y = 0; y < mYTileCount; y++) {
                setMapItem(0, x, y);
            }
        }
    }

    public void loadMapItem(int key, Drawable tile) {
        Bitmap bitmap = Bitmap.createBitmap(mItemSize, mItemSize, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        tile.setBounds(0, 0, mItemSize, mItemSize);
        tile.draw(canvas);

        mMapItemArray[key] = bitmap;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint background = new Paint();
        background.setColor(getResources().getColor(android.R.color.white));
        canvas.drawRect(0, 0, getWidth(), getHeight(), background);

        for (int x = 0; x < mXTileCount; x += 1) {
            for (int y = 0; y < mYTileCount; y += 1) {
                if (mMap[x][y] > 0) {
                    canvas.drawBitmap(mMapItemArray[mMap[x][y]], mXOffset + x * mItemSize, mYOffset + y * mItemSize, mPaint);
                } else {
                    canvas.drawBitmap(mMapItemArray[9], mXOffset + x * mItemSize, mYOffset + y * mItemSize, mPaint);
                }
            }
        }

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
        mHeight = h;
        resetItemSize(w, h, Prefs.getCurLevel(mContext));//, oldw, oldh);
    }

    public void resetItemSize(int w, int h, int mLevel) {

        if (w == 0 || h == 0)
            return;

        int difficulty = Prefs.getDifficultly(mContext);
        //getPreferences(MODE_PRIVATE).getInt(Common.PREF_KEY3, Common.MEDIUM);
        //Prefs.getDifficultly(context);

        int startLevel = 0;
        int endLevel = 0;

        switch (difficulty) {
            case Common.EASY:
                startLevel = R.string.easy001;
                endLevel = mContext.getResources().getInteger(R.integer.easyend);
                break;
            case Common.MEDIUM:
                startLevel = R.string.medium001;
                endLevel = mContext.getResources().getInteger(R.integer.mediumend);
                break;
            case Common.HARD:
                startLevel = R.string.hard001;
                endLevel = mContext.getResources().getInteger(R.integer.hardend);
                break;

        }

        int resId = startLevel + mLevel - 1;

        Log.d(" resId", "  PlayLevel= " + mLevel);
        Log.d(" resId", "  resId= " + resId);

        String mapStr = "";
        if (mLevel <= endLevel) {
            mapStr = mContext.getResources().getString(resId);
        } else {
            Log.d(" resId", "  Use Ending Map... ");
            mapStr = mContext.getResources().getString(R.string.endingmap);
        }

        mapStr = mapStr.replace("9", " ");

        String maps[] = Common.tokenFn(mapStr, ",");

        colCount = 0;

        int rowCount = 0;
        for (int i = 0; i < maps.length; i++) {
            if (!"".equals(maps[i].trim()))
                rowCount += 1;

            Log.d(" maps[i].trim()", "  maps[i].trim()= " + maps[i].trim() + "=" + maps[i].trim().length());
            if (colCount < maps[i].trim().length()) {
                Log.d(" maps[i].trim()", "  maps[i].trim()= " + maps[i].trim() + "=" + maps[i].trim().length());
                colCount = maps[i].trim().length();
            }
        }

        colCount = colCount > rowCount ? colCount : rowCount;

        mItemSize = Prefs.getIconsize(mContext);

        /////////////////////////////////////////////////////////////////    

        mXTileCount = (int) Math.floor(w / mItemSize);
        mYTileCount = (int) Math.floor(h / mItemSize);

        Log.d("StoreKeeperView1111", "colCount= " + colCount + ", rowCount= " + rowCount + ", mItemSize= " + mItemSize);
        Log.d("StoreKeeperView1111", "mXTileCount= " + mXTileCount + ", mYTileCount= " + mYTileCount + ", mItemSize= " + mItemSize);

        if (colCount >= mXTileCount || rowCount >= mYTileCount) {

            int wsize = (int) Math.floor(w / colCount);
            int hsize = (int) Math.floor(h / rowCount);

            // 둘 중 더 적은 사이즈를 사용한다.
            mItemSize = wsize > hsize ? hsize : wsize;

            String msg = mContext.getResources().getString(R.string.change_iconsize);
            msg += " (" + mItemSize + " x " + mItemSize + ") ";
            Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();

            mXTileCount = (int) Math.floor(w / mItemSize);
            mYTileCount = (int) Math.floor(h / mItemSize);

            resetTiles(10);
            loadMapItem(Common.LINE, mContext.getResources().getDrawable(R.drawable.l));
            loadMapItem(Common.HOME, mContext.getResources().getDrawable(R.drawable.h));
            loadMapItem(Common.YES, mContext.getResources().getDrawable(R.drawable.y));
            loadMapItem(Common.NO, mContext.getResources().getDrawable(R.drawable.n));
            loadMapItem(Common.MAN, mContext.getResources().getDrawable(R.drawable.m));
            loadMapItem(Common.BLANK, mContext.getResources().getDrawable(R.drawable.b));

        }

        Log.d("StoreKeeperView", "mXTileCount= " + mXTileCount + ", mYTileCount= " + mYTileCount + ", mItemSize= " + mItemSize);

        mXOffset = ((w - (mItemSize * mXTileCount)) / 2);
        mYOffset = ((h - (mItemSize * mYTileCount)) / 2);

        // mMap = new int[mXTileCount][mYTileCount];
        int maxindex = mXTileCount > mYTileCount ? mXTileCount : mYTileCount;
        mMap = new int[maxindex][maxindex];
        clearMap();
    }

    public void resetTiles(int tilecount) {
        mMapItemArray = new Bitmap[tilecount];
    }

    public int getItemSize() {
        return this.mItemSize;
    }

    public void setMapItem(int tileindex, int x, int y) {

        if (mMap != null) {
            //Log.d("TTTTTTTTT", " mXTileCount= " + mXTileCount + ", mYTileCount= " + mYTileCount);
            //Log.d("TTTTTTTTT", " x= " + x + ", y= " + y);

            mMap[x][y] = tileindex;

        }
    }

    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfo) {
        // TODO Auto-generated method stub

    }

    public boolean onContextItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        return false;
    }

}
