package com.nilriri.android.Storekeeper;

import java.util.ArrayList;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

import com.nilriri.android.PlayStep;
import com.nilriri.android.PlaySteps;
import com.nilriri.android.Storekeeper.dao.DaoImpl;
import com.nilriri.android.Storekeeper.dao.Constants.HistoryData;

public class HistoryDaoImpl extends DaoImpl {

 
    public HistoryDaoImpl(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);

    }

    public void delete(int mVersion, Long id) {
        String sql = "DELETE FROM " + HistoryData.HISTORY_TABLE_NAME + " WHERE " + HistoryData._ID + "=" + id;
        getWritableDatabase().execSQL(sql);
    }

    public void deleteAll(int mVersion) {
        String sql = "DELETE FROM " + HistoryData.HISTORY_TABLE_NAME;
        getWritableDatabase().execSQL(sql);
    }

    public void insert(int difficulty, int level, ArrayList<PlaySteps> history) {
        Log.d("DaoImpl-insert", "insert start...");

        ContentValues val = null;

        int limit = history.size();

        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();

        db.execSQL("DELETE FROM " + HistoryData.HISTORY_TABLE_NAME + " WHERE " + HistoryData.DIFFICULTY + " = " + difficulty + " AND " + HistoryData.LEVEL + "=" + level + " ");

        for (int row = 0; row < limit; row++) {

            PlaySteps palys = (PlaySteps) history.get(row);

            for (int i = 0; i < palys.play.size(); i++) {
                PlayStep step = palys.play.get(i);

                val = new ContentValues();

                val.put("difficulty", difficulty);
                val.put("level", level);
                val.put("seq", row);
                val.put("step", i);
                val.put("x", step.x);
                val.put("y", step.y);
                val.put("before", step.before);
                val.put("after", step.after);

                db.insert(HistoryData.HISTORY_TABLE_NAME, null, val);
            }
        }
        db.setTransactionSuccessful();

        db.endTransaction();

    }

    public ArrayList<PlaySteps> queryHistory(int difficulty, int level) {

        ArrayList<PlaySteps> playHistory = new ArrayList<PlaySteps>();

        SQLiteDatabase db = getReadableDatabase();

        Log.d("DaoImpl-insert", "difficulty=" + difficulty + ",level=" + level);

        StringBuffer query = new StringBuffer();
        query.append("SELECT  ");
        query.append("   distinct seq  ");
        query.append("FROM " + HistoryData.HISTORY_TABLE_NAME + " ");
        query.append("WHERE " + HistoryData.DIFFICULTY + " = ? ");
        query.append("AND " + HistoryData.LEVEL + " = ? ");
        query.append("ORDER BY SEQ ");

        String selectionArgs[] = new String[] { difficulty + "", level + "" };
        Cursor cursor = db.rawQuery(query.toString(), selectionArgs);

        while (cursor.moveToNext()) {

            StringBuffer query2 = new StringBuffer();
            query2.append("SELECT  ");
            query2.append("     x, y, before, after ");
            query2.append("FROM " + HistoryData.HISTORY_TABLE_NAME + " ");
            query2.append("WHERE " + HistoryData.DIFFICULTY + " = ? ");
            query2.append("AND " + HistoryData.LEVEL + " = ? ");
            query2.append("AND " + HistoryData.SEQ + " = ? ");
            query2.append("ORDER BY STEP ");

            String selectionArgs2[] = new String[] { difficulty + "", level + "", cursor.getString(0) };
            Cursor cursor2 = db.rawQuery(query2.toString(), selectionArgs2);

            while (cursor2.moveToNext()) {

                Log.d("DaoImpl-insert", "cursor2=" + cursor2.getString(0) + "," + cursor2.getString(1) + "," + cursor2.getString(2) + "," + cursor2.getString(3) + ";");
                PlaySteps play = new PlaySteps();

                play.add(cursor2.getInt(0), cursor2.getInt(1), cursor2.getInt(2), cursor2.getInt(3));

                playHistory.add(play);
            }

        }

        return playHistory;
    }
    
    
    public Cursor queryHistory2(int difficulty, int level) {

      
    
        StringBuffer query = new StringBuffer();
        query.append("SELECT  ");
        query.append("   x, y  ");
        query.append("FROM " + HistoryData.HISTORY_TABLE_NAME + " ");
        query.append("WHERE " + HistoryData.DIFFICULTY + " = ? ");
        query.append("AND " + HistoryData.LEVEL + " = ? ");
        query.append("AND " + HistoryData.AFTER + " = 4 ");
        query.append("ORDER BY SEQ ");

        String selectionArgs[] = new String[] { difficulty + "", level + "" };
      

        return getReadableDatabase().rawQuery(query.toString(), selectionArgs);
    }
    
}
