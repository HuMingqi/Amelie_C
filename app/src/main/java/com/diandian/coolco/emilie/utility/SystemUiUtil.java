package com.diandian.coolco.emilie.utility;

import android.app.Activity;

public class SystemUiUtil {

    public static void hideSystemUi(Activity activity) {
        SystemUiHelper systemUiHelper = new SystemUiHelper(activity, SystemUiHelper.LEVEL_HIDE_STATUS_BAR, 0);
        systemUiHelper.hide();
    }
}
