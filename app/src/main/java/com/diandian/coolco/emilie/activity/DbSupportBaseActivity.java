package com.diandian.coolco.emilie.activity;

import com.diandian.coolco.emilie.utility.DBHelper;
import com.j256.ormlite.android.apptools.OpenHelperManager;

public class DbSupportBaseActivity extends BaseActivity {

    private DBHelper dbHelper = null;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            OpenHelperManager.releaseHelper();
            dbHelper = null;
        }
    }

    protected DBHelper getDBHelper() {
        if (dbHelper == null) {
            dbHelper = OpenHelperManager.getHelper(this, DBHelper.class);
        }
        return dbHelper;
    }
}
