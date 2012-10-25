package com.nilriri.android.Storekeeper;

import java.io.File;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.nilriri.android.StoreKeeper;
import com.nilriri.android.Storekeeper.R;
import com.nilriri.android.Storekeeper.dao.AppConst;
import com.nilriri.android.Storekeeper.dao.DataHelper;
import com.nilriri.android.Storekeeper.dao.FileUtil;

/**
 * Simplified import/export DB activity.
 * 
 * @author ccollins
 *
 */
public class ManageData extends Activity {

    private MyApplication application;

    private Button exportDbToSdButton;
    private Button importDbFromSdButton;
    private Button clearDbButton;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        application = (MyApplication) getApplication();

        setContentView(R.layout.managedata);

        exportDbToSdButton = (Button) findViewById(R.id.exportdbtosdbutton);
        exportDbToSdButton.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
                new AlertDialog.Builder(ManageData.this).setMessage(R.string.are_you_sure_this_will_overwrite_any_existing_backup_data_).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (isExternalStorageAvail()) {
                            new ExportDatabaseTask().execute();
                            ManageData.this.startActivity(new Intent(ManageData.this, StoreKeeper.class));
                        } else {
                            Toast.makeText(ManageData.this, R.string.external_storage_is_not_available_unable_to_export_data_, Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                }).show();
            }
        });

        importDbFromSdButton = (Button) findViewById(R.id.importdbfromsdbutton);
        importDbFromSdButton.setOnClickListener(new OnClickListener() {
            public void onClick(final View v) {
                new AlertDialog.Builder(ManageData.this).setMessage(R.string.are_you_sure_this_will_overwrite_existing_current_data_).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        if (isExternalStorageAvail()) {
                            Log.i(AppConst.TAG, getString(R.string.importing_database_from_external_storage_and_resetting_database));
                            new ImportDatabaseTask().execute();
                            // sleep momentarily so that database reset stuff has time to take place (else Main reloads too fast)
                            SystemClock.sleep(500);
                            ManageData.this.startActivity(new Intent(ManageData.this, Main.class));
                        } else {
                            Toast.makeText(ManageData.this, R.string.external_storage_is_not_available_unable_to_export_data_, Toast.LENGTH_SHORT).show();
                        }
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                    }
                }).show();
            }
        });

    }

    private boolean isExternalStorageAvail() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    private class ExportDatabaseTask extends AsyncTask<Void, Void, Boolean> {
        private final ProgressDialog dialog = new ProgressDialog(ManageData.this);

        // can use UI thread here
        @Override
        protected void onPreExecute() {
            dialog.setMessage(getString(R.string.exporting_database_msg));
            dialog.show();
        }

        // automatically done on worker thread (separate from UI thread)
        @Override
        protected Boolean doInBackground(final Void... args) {

            File dbFile = new File(Environment.getDataDirectory() + "/data/"+DataHelper.PACKAGE_NAME + "/databases/" + DataHelper.DATABASE_NAME);

            File exportDir = new File(Environment.getExternalStorageDirectory(), "PushPushIII");
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }
            File file = new File(exportDir, dbFile.getName());

            try {
                file.createNewFile();
                FileUtil.copyFile(dbFile, file);
                return true;
            } catch (IOException e) {
                Log.e(AppConst.TAG, e.getMessage(), e);
                return false;
            }
        }

        // can use UI thread here
        @Override
        protected void onPostExecute(final Boolean success) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if (success) {
                Toast.makeText(ManageData.this, R.string.export_successful_msg, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ManageData.this, R.string.export_failed, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class ImportDatabaseTask extends AsyncTask<Void, Void, String> {
        private final ProgressDialog dialog = new ProgressDialog(ManageData.this);

        @Override
        protected void onPreExecute() {
            dialog.setMessage(getString(R.string.importing_dialog_msg));
            dialog.show();
        }

        // could pass the params used here in AsyncTask<String, Void, String> - but not being re-used
        @Override
        protected String doInBackground(final Void... args) {

            File dbBackupFile = new File(Environment.getExternalStorageDirectory() + "/PushPushIII/" + DataHelper.DATABASE_NAME);
            if (!dbBackupFile.exists()) {
                return getString(R.string.backfile_not_found);
            } else if (!dbBackupFile.canRead()) {
                return getString(R.string.backupfile_can_not_read);
            }

            File dbFile = new File(Environment.getDataDirectory() + "/data/" + DataHelper.PACKAGE_NAME + "/databases/" + DataHelper.DATABASE_NAME);
            if (dbFile.exists()) {
                dbFile.delete();
            }

            try {
                dbFile.createNewFile();
                FileUtil.copyFile(dbBackupFile, dbFile);
                ManageData.this.application.getDataHelper().resetDbConnection();
                return null;
            } catch (IOException e) {
                Log.e(AppConst.TAG, e.getMessage(), e);
                return e.getMessage();
            }
        }

        @Override
        protected void onPostExecute(final String errMsg) {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            if (errMsg == null) {
                Toast.makeText(ManageData.this, R.string.import_successful_msg, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ManageData.this, R.string.import_failed_msg + errMsg, Toast.LENGTH_SHORT).show();
            }
        }
    }
}