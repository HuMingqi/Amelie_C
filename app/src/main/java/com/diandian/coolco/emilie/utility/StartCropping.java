package com.diandian.coolco.emilie.utility;

import android.app.Activity;
import android.content.Intent;

import com.diandian.coolco.emilie.activity.SrcImgCropActivity;

/**
 * Created by mingq on 7/10/2016.
 */
public class StartCropping {
    public static void startCropActivity(Activity activity,String img_path){
        Preference.setPrefBoolean(activity.getApplicationContext(), PreferenceKey.IS_SRC_IMG_STORAGE_COMPLETED, true);
        Intent intent = new Intent(activity, SrcImgCropActivity.class);
        intent.putExtra(ExtraDataName.SRC_IMG_PATH, img_path);
        activity.startActivity(intent);
    }
}
