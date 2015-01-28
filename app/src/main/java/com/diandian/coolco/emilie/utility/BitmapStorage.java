package com.diandian.coolco.emilie.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

import com.diandian.coolco.emilie.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapStorage {
    private static final String TAG = BitmapStorage.class.getSimpleName();

    public static String saveImg(Context context, Bitmap bm, String imgName) {
        String imgDir = Environment.getExternalStorageDirectory().toString()
                + "/" + getApplicationName(context);
        String imgPath = imgDir + "/" + imgName;
        new File(imgDir).mkdirs();
        File imgFile = new File(imgPath);
        try {
            FileOutputStream os = new FileOutputStream(imgFile);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
            return imgPath;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Logcat.e(TAG, e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Logcat.e(TAG, e.toString());
        }
        return null;
    }

    public static String getApplicationName(Context context) {
//        int stringId = context.getApplicationInfo().labelRes;
//        return context.getString(stringId);
        return context.getResources().getString(R.string.app_name);
    }
}
