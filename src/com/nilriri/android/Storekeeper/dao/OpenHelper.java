package com.nilriri.android.Storekeeper.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class OpenHelper extends SQLiteOpenHelper {

    private boolean dbCreated;

    OpenHelper(Context context) {
        super(context, DataHelper.DATABASE_NAME, null, DataHelper.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        try {

            // using StringBuilder here because it is easier to read/reuse lines
            StringBuilder query = new StringBuilder();

            query.append("CREATE TABLE IF NOT EXISTS " + HistoryDataColumns.HISTORY_TABLE_NAME + " (");
            query.append("    " + HistoryDataColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT ");
            query.append("    ," + HistoryDataColumns.DIFFICULTY + " INTEGER ");
            query.append("    ," + HistoryDataColumns.LEVEL + " INTEGER ");
            query.append("    ," + HistoryDataColumns.SEQ + " INTEGER ");
            query.append("    ," + HistoryDataColumns.STEP + " INTEGER ");
            query.append("    ," + HistoryDataColumns.X + " INTEGER ");
            query.append("    ," + HistoryDataColumns.Y + " INTEGER ");
            query.append("    ," + HistoryDataColumns.BEFORE + " INTEGER ");
            query.append("    ," + HistoryDataColumns.AFTER + " INTEGER ");
            query.append("    ) ");

            Log.d(AppConst.TAG, query.toString());

            db.execSQL(query.toString());

            query.setLength(0);
            query.append("CREATE TABLE IF NOT EXISTS " + HistoryDataColumns.BACKUP_TABLE_NAME + " (");
            query.append("    " + HistoryDataColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT ");
            query.append("    ," + HistoryDataColumns.DIFFICULTY + " INTEGER ");
            query.append("    ," + HistoryDataColumns.LEVEL + " INTEGER ");
            query.append("    ," + HistoryDataColumns.SEQ + " INTEGER ");
            query.append("    ," + HistoryDataColumns.STEP + " INTEGER ");
            query.append("    ," + HistoryDataColumns.X + " INTEGER ");
            query.append("    ," + HistoryDataColumns.Y + " INTEGER ");
            query.append("    ," + HistoryDataColumns.BEFORE + " INTEGER ");
            query.append("    ," + HistoryDataColumns.AFTER + " INTEGER ");
            query.append("    ) ");

            Log.d(AppConst.TAG, query.toString());

            db.execSQL(query.toString());

            db.execSQL("CREATE INDEX idx_" + HistoryDataColumns.HISTORY_TABLE_NAME + "_01 ON " + HistoryDataColumns.HISTORY_TABLE_NAME + "  (difficulty, level, seq ) ");
            db.execSQL("CREATE INDEX idx_" + HistoryDataColumns.HISTORY_TABLE_NAME + "_02 ON " + HistoryDataColumns.HISTORY_TABLE_NAME + "  (difficulty, level, seq, step ) ");

            dbCreated = true;

        } catch (Exception e) {

        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(AppConst.TAG, "SQLiteOpenHelper onUpgrade - oldVersion:" + oldVersion + " newVersion:" + newVersion);

        // export old data first, then upgrade, then import
        db.execSQL("DROP TABLE IF EXISTS " + HistoryDataColumns.HISTORY_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + HistoryDataColumns.BACKUP_TABLE_NAME);

        onCreate(db);
    }

    public boolean isDbCreated() {
        return dbCreated;
    }
}
