package com.nilriri.android.Storekeeper;

import android.app.Application;

import com.nilriri.android.Storekeeper.dao.DataHelper;

public class MyApplication extends Application {

    private DataHelper dataHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        dataHelper = new DataHelper(this);
    }

    @Override
    public void onTerminate() {
        // NOTE - this method is not guaranteed to be called
        dataHelper.cleanup();
        super.onTerminate();
    }

    public DataHelper getDataHelper() {
        return dataHelper;
    }

    public void setDataHelper(DataHelper dataHelper) {
        this.dataHelper = dataHelper;
    }

}
