package com.nilriri.android.Storekeeper.dao;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.util.Log;

import com.nilriri.android.Storekeeper.R;
import com.nilriri.android.Storekeeper.dao.Constants.BSite1;

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

        onCreateIndex(db);

        onInsertData(db);
    }

    public void onCreateBible(SQLiteDatabase db) {

        StringBuffer query = null;

        query = new StringBuffer();
        query.append("CREATE TABLE " + BSite1.VERSION_TABLE_NAME + " (");
        query.append("    " + BSite1._ID + " INTEGER PRIMARY KEY AUTOINCREMENT ");
        query.append("    ," + BSite1.DIFFICULTY + " INTEGER ");
        query.append("    ," + BSite1.LEVEL + " INTEGER ");
        query.append("    ," + BSite1.SEQ + " INTEGER ");
        query.append("    ," + BSite1.STEP + " INTEGER ");
        query.append("    ," + BSite1.X + " INTEGER ");
        query.append("    ," + BSite1.Y + " INTEGER ");
        query.append("    ," + BSite1.BEFORE + " INTEGER ");
        query.append("    ," + BSite1.AFTER + " INTEGER ");
        query.append("    ) ");

        Log.d("onCreate", "Create....Bible Database=" + query.toString());

        db.execSQL(query.toString());

    }

    public void onCreateIndex(SQLiteDatabase db) {

        db.execSQL("CREATE INDEX idx_" + BSite1.VERSION_TABLE_NAME + "_01 ON " + BSite1.VERSION_TABLE_NAME + "  (difficulty, level, seq ) ");
        db.execSQL("CREATE INDEX idx_" + BSite1.VERSION_TABLE_NAME + "_02 ON " + BSite1.VERSION_TABLE_NAME + "  (difficulty, level, seq, step ) ");

        Log.d("onCreate", "Create....Index");
    }

    public void onInsertData(SQLiteDatabase db) {

        String sqls[] = mContext.getResources().getStringArray(R.array.sample_play);
        db.beginTransaction();

        for (int i = 0; i < sqls.length; i++) {
            db.execSQL(sqls[i]);
        }

        db.setTransactionSuccessful();
        db.endTransaction();

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.d("onUpgrade_Schedule", "oldVersion=" + oldVersion + ",newVersion=" + newVersion);

        StringBuffer query = new StringBuffer();
        query.append(" DROP TABLE IF EXISTS " + BSite1.VERSION_TABLE_NAME + " ");
        db.execSQL(query.toString());

        this.onCreateBible(db);
        this.onCreateIndex(db);

    }
}