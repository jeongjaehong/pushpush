package com.nilriri.android.Storekeeper.dao;

import android.provider.BaseColumns;

public final class Constants {
    public static final String DATABASE_NAME = "storekeeper.sqlite";
    public static final int DATABASE_VERSION = 5;

    public static final String DEFAULT_SORT_ORDER = "_id DESC";

    // This class cannot be instantiated
    private Constants() {
    }

    /**
     * HistoryData table
     */
    public static final class HistoryData implements BaseColumns {
        // This class cannot be instantiated
        private HistoryData() {
        }

        public static final String HISTORY_TABLE_NAME = "history";
        public static final String BACKUP_TABLE_NAME = "backup";

        public static final String _ID = "_id";
        public static final String DIFFICULTY = "difficulty";
        public static final String LEVEL = "level";
        public static final String SEQ = "seq";
        public static final String STEP = "step";
        public static final String X = "x";
        public static final String Y = "y";
        public static final String BEFORE = "before";
        public static final String AFTER = "after";

        public static final int COL_ID = 0;
        public static final int COL_DIFFICULTY = 1;
        public static final int COL_LEVEL = 2;
        public static final int COL_SEQ = 3;
        public static final int COL_STEP = 4;
        public static final int COL_X = 5;
        public static final int COL_Y = 6;
        public static final int COL_BEFORE = 7;
        public static final int COL_AFTER = 8;

    }

}
