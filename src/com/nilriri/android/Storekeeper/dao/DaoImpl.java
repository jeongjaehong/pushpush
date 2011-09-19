package com.nilriri.android.Storekeeper.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

import com.nilriri.android.Storekeeper.R;
import com.nilriri.android.Storekeeper.dao.Constants.HistoryData;

public abstract class DaoImpl extends SQLiteOpenHelper {

    private Context mContext;

    public DaoImpl(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d("onCreate", "CurrentVersion=" + db.getVersion());
        onCreateBible(db);

        onCreateBackup(db);

        onCreateIndex(db);

        onInsertData(db);
    }

    public void onCreateBible(SQLiteDatabase db) {

        try {
            StringBuffer query = null;

            query = new StringBuffer();
            query.append("CREATE TABLE IF NOT EXISTS " + HistoryData.HISTORY_TABLE_NAME + " (");
            query.append("    " + HistoryData._ID + " INTEGER PRIMARY KEY AUTOINCREMENT ");
            query.append("    ," + HistoryData.DIFFICULTY + " INTEGER ");
            query.append("    ," + HistoryData.LEVEL + " INTEGER ");
            query.append("    ," + HistoryData.SEQ + " INTEGER ");
            query.append("    ," + HistoryData.STEP + " INTEGER ");
            query.append("    ," + HistoryData.X + " INTEGER ");
            query.append("    ," + HistoryData.Y + " INTEGER ");
            query.append("    ," + HistoryData.BEFORE + " INTEGER ");
            query.append("    ," + HistoryData.AFTER + " INTEGER ");
            query.append("    ) ");

            Log.d("onCreate", "Create....Bible Database=" + query.toString());

            db.execSQL(query.toString());
        } catch (Exception e) {

        }
    }

    public void onCreateBackup(SQLiteDatabase db) {
        try {
            StringBuffer query = null;

            query = new StringBuffer();
            query.append("CREATE TABLE IF NOT EXISTS " + HistoryData.BACKUP_TABLE_NAME + " (");
            query.append("    " + HistoryData._ID + " INTEGER PRIMARY KEY AUTOINCREMENT ");
            query.append("    ," + HistoryData.DIFFICULTY + " INTEGER ");
            query.append("    ," + HistoryData.LEVEL + " INTEGER ");
            query.append("    ," + HistoryData.SEQ + " INTEGER ");
            query.append("    ," + HistoryData.STEP + " INTEGER ");
            query.append("    ," + HistoryData.X + " INTEGER ");
            query.append("    ," + HistoryData.Y + " INTEGER ");
            query.append("    ," + HistoryData.BEFORE + " INTEGER ");
            query.append("    ," + HistoryData.AFTER + " INTEGER ");
            query.append("    ) ");

            Log.d("onCreate", "Create....Bible Database=" + query.toString());

            db.execSQL(query.toString());
        } catch (Exception e) {

        }

    }

    public void onCreateIndex(SQLiteDatabase db) {
        try {
            db.execSQL("CREATE INDEX idx_" + HistoryData.HISTORY_TABLE_NAME + "_01 ON " + HistoryData.HISTORY_TABLE_NAME + "  (difficulty, level, seq ) ");
            db.execSQL("CREATE INDEX idx_" + HistoryData.HISTORY_TABLE_NAME + "_02 ON " + HistoryData.HISTORY_TABLE_NAME + "  (difficulty, level, seq, step ) ");

        } catch (Exception e) {

        }
        Log.d("onCreate", "Create....Index");
    }

    public void onInsertData(SQLiteDatabase db) {

        String sqls[] = mContext.getResources().getStringArray(R.array.sample_play);
        db.beginTransaction();
        try {
            db.execSQL("delete from " + HistoryData.BACKUP_TABLE_NAME + "");
            db.execSQL("insert into " + HistoryData.BACKUP_TABLE_NAME + " select * from " + HistoryData.HISTORY_TABLE_NAME + "");
            db.execSQL("delete from " + HistoryData.HISTORY_TABLE_NAME + "");

            for (int i = 0; i < sqls.length; i++) {
                db.execSQL(sqls[i]);
            }

            db.execSQL("insert into " + HistoryData.HISTORY_TABLE_NAME + " select * from " + HistoryData.BACKUP_TABLE_NAME + "");
            db.setTransactionSuccessful();
        } catch (Exception e) {

        }
        db.endTransaction();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.d("onUpgrade_Schedule", "oldVersion=" + oldVersion + ",newVersion=" + newVersion);

        StringBuffer query = new StringBuffer();
        query.append(" DROP TABLE IF EXISTS " + HistoryData.HISTORY_TABLE_NAME + " ");
        db.execSQL(query.toString());

        onCreateBible(db);

        onCreateIndex(db);

        onCreateBackup(db);

        onInsertData(db);
    }
}