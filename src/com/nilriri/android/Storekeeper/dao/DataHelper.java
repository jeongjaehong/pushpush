package com.nilriri.android.Storekeeper.dao;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.nilriri.android.Common;
import com.nilriri.android.PlayStep;
import com.nilriri.android.PlaySteps;
import com.nilriri.android.Storekeeper.R;

/**
 * Oversimplified Android DB example.
 * Includes SQLite foreign keys and unique constraints - though contrived example.
 * 
 * @author ccollins
 *
 */
public class DataHelper {

    public static final String DATABASE_NAME = "storekeeper.sqlite";
    public static final String PACKAGE_NAME = "com.nilriri.android.Storekeeper";

    public static final int DATABASE_VERSION = 5;

    public static final String DEFAULT_SORT_ORDER = "_id DESC";

    private SQLiteDatabase db;

    // can use precompiled statements - put em here, then init in ctor (db.compileStatement(stmt))

    public DataHelper(Context context) {
        OpenHelper openHelper = new OpenHelper(context);
        db = openHelper.getWritableDatabase();

        if (openHelper.isDbCreated()) {

            String sqls[] = context.getResources().getStringArray(R.array.sample_play);
            db.beginTransaction();
            try {
                db.execSQL("delete from " + HistoryDataColumns.BACKUP_TABLE_NAME + "");
                db.execSQL("insert into " + HistoryDataColumns.BACKUP_TABLE_NAME + " select * from " + HistoryDataColumns.HISTORY_TABLE_NAME + "");
                db.execSQL("delete from " + HistoryDataColumns.HISTORY_TABLE_NAME + "");

                for (int i = 0; i < sqls.length; i++) {
                    db.execSQL(sqls[i]);
                }

                db.execSQL("insert into " + HistoryDataColumns.HISTORY_TABLE_NAME + " select * from " + HistoryDataColumns.BACKUP_TABLE_NAME + "");
                db.setTransactionSuccessful();
            } catch (Exception e) {

            }
            db.endTransaction();

        }
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    public void resetDbConnection() {
        Log.i(AppConst.TAG, "resetting database connection (close and re-open).");
        cleanup();
        db = SQLiteDatabase.openDatabase("/data/data/com.totsp.database/databases/sample.db", null, SQLiteDatabase.OPEN_READWRITE);
    }

    public void cleanup() {
        if ((db != null) && db.isOpen()) {
            db.close();
        }
    }

    public void insertHistory(int difficulty, int level, ArrayList<PlaySteps> history) {

        db.beginTransaction();

        try {

            ContentValues val = null;

            int limit = history.size();

            if (queryHistoryExists(difficulty, level)) {
                //db.execSQL("DELETE FROM " + HistoryDataColumns.HISTORY_TABLE_NAME + " WHERE " + HistoryDataColumns.DIFFICULTY + " = " + difficulty + " AND " + HistoryDataColumns.LEVEL + "=" + level + " ");

                StringBuilder whereClause = new StringBuilder();
                whereClause.append(HistoryDataColumns.DIFFICULTY).append(" = ? ");
                whereClause.append(" AND ").append(HistoryDataColumns.LEVEL).append(" = ? ");

                db.delete(HistoryDataColumns.HISTORY_TABLE_NAME, whereClause.toString(), new String[] { String.valueOf(difficulty), String.valueOf(level) });

            }

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

                    db.insert(HistoryDataColumns.HISTORY_TABLE_NAME, null, val);
                }
            }

            db.setTransactionSuccessful();
        } catch (SQLException e) {
            Log.e(AppConst.TAG, "Error inserting book", e);
        } finally {
            db.endTransaction();
        }

    }

    public ArrayList<PlaySteps> queryHistory(int difficulty, int level) {

        ArrayList<PlaySteps> playHistory = new ArrayList<PlaySteps>();

        Log.d("DaoImpl-insert", "difficulty=" + difficulty + ",level=" + level);

        StringBuilder query = new StringBuilder();
        query.append(" SELECT  ");
        query.append("   DISTINCT   ");
        query.append(HistoryDataColumns.SEQ);
        query.append(" FROM " + HistoryDataColumns.HISTORY_TABLE_NAME + " ");
        query.append(" WHERE " + HistoryDataColumns.DIFFICULTY + " = ? ");
        query.append(" AND " + HistoryDataColumns.LEVEL + " = ? ");
        query.append(" ORDER BY SEQ ");

        String selectionSEQArgs[] = new String[] { String.valueOf(difficulty), String.valueOf(level) };
        Cursor cursorSEQ = db.rawQuery(query.toString(), selectionSEQArgs);

        query.setLength(0);
        query.append(" SELECT  ");
        query.append(HistoryDataColumns.X);
        query.append(",").append(HistoryDataColumns.Y);
        query.append(",").append(HistoryDataColumns.BEFORE);
        query.append(",").append(HistoryDataColumns.AFTER);
        query.append(" FROM " + HistoryDataColumns.HISTORY_TABLE_NAME + " ");
        query.append(" WHERE " + HistoryDataColumns.DIFFICULTY + " = ? ");
        query.append(" AND " + HistoryDataColumns.LEVEL + " = ? ");
        query.append(" AND " + HistoryDataColumns.SEQ + " = ? ");
        query.append(" ORDER BY STEP ");

        while (cursorSEQ.moveToNext()) {
            String selectionHistoryArgs[] = new String[] { String.valueOf(difficulty), String.valueOf(level), cursorSEQ.getString(0) };
            Cursor cursorHistory = db.rawQuery(query.toString(), selectionHistoryArgs);
            while (cursorHistory.moveToNext()) {
                PlaySteps play = new PlaySteps();
                play.add(cursorHistory.getInt(0), cursorHistory.getInt(1), cursorHistory.getInt(2), cursorHistory.getInt(3));
                playHistory.add(play);
            }

            if ((cursorHistory != null) && !cursorHistory.isClosed()) {
                cursorHistory.close();
            }
        }

        if ((cursorSEQ != null) && !cursorSEQ.isClosed()) {
            cursorSEQ.close();
        }

        return playHistory;
    }

    public boolean queryHistoryExists(int difficulty, int level) {
        boolean result = false;

        Cursor c = queryHistoryCursor(difficulty, level);

        if (c.moveToFirst()) {
            return true;
        }
        if ((c != null) && !c.isClosed()) {
            c.close();
        }

        return result;
    }

    public Cursor queryHistoryCursor(int difficulty, int level) {

        StringBuilder whereClause = new StringBuilder();
        whereClause.append(HistoryDataColumns.DIFFICULTY).append(" = ? ");
        whereClause.append(" AND ").append(HistoryDataColumns.LEVEL).append(" = ? ");
        whereClause.append(" AND ").append(HistoryDataColumns.AFTER).append(" = ? ");

        String orderby = HistoryDataColumns.SEQ;

        return db.query(HistoryDataColumns.HISTORY_TABLE_NAME, new String[] { HistoryDataColumns.X, HistoryDataColumns.Y }, whereClause.toString(), new String[] { String.valueOf(difficulty), String.valueOf(level), String.valueOf(Common.MAN) }, null, null, orderby, null);

    }
}