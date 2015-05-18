package com.diandian.coolco.emilie.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.diandian.coolco.emilie.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BitmapStorage {
    private static final String TAG = BitmapStorage.class.getSimpleName();

    public static String storeImg(Context context, Bitmap bm, String imgName) {
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

    public static String saveImg(Context context, Bitmap bitmap) {
        if (bitmap == null){
            Logcat.e("bitmap == null");
            return null;
        }
        File imgFile = getOutputMediaFile(context, MEDIA_TYPE_IMAGE);
        if (imgFile == null) {
            Logcat.e("saveImg imgFile == null");
            return null;
        }
        try {
            FileOutputStream os = new FileOutputStream(imgFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();
            return imgFile.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Logcat.e(TAG, e.toString());
        } catch (IOException e) {
            e.printStackTrace();
            Logcat.e(TAG, e.toString());
        }
        return null;
    }

    public static String getImgDir(Context context) {
        return Environment.getExternalStorageDirectory().toString()
                + "/" + getApplicationName(context);
    }

    public static String getApplicationName(Context context) {
//        int stringId = context.getApplicationInfo().labelRes;
//        return context.getString(stringId);
        return context.getResources().getString(R.string.app_name);
    }

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    /** Create a file Uri for saving an image or video */
//    private static Uri getOutputMediaFileUri(int type){
//        return Uri.fromFile(getOutputMediaFile(type));
//    }

    /**
     * Create a File for saving an image or video
     */
    public static File getOutputMediaFile(Context context, int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            SuperToastUtil.showToast(context, "未检测到SD卡，操作失败~");
            return null;
        }

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), getApplicationName(context));
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                SuperToastUtil.showToast(context, "无法创建目录，操作失败~");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }
}
