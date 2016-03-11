package com.diandian.coolco.emilie.utility;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;

import com.diandian.coolco.emilie.activity.SimilarImgActivity;

public class TranstionAnimationUtil {
    public static void startSimilarImgActivity(Activity activity, String srcImgPath, View fromView){

//        ActivityOptionsCompat options = null;
//        if (Build.VERSION.SDK_INT >= 21) {
//            options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, fromView, activity.getResources().getString(R.string.similar_img_transtion_dest));
//        } else {
//            options = ActivityOptionsCompat.makeScaleUpAnimation(fromView, 0, 0, fromView.getWidth(), fromView.getHeight());
//        }

        Intent intent = new Intent(activity, SimilarImgActivity.class);
        intent.putExtra(ExtraDataName.CROPPED_SRC_IMG_PATH, srcImgPath);

//        if (Build.VERSION.SDK_INT > 15) {
//            activity.startActivity(intent, options.toBundle());
//        } else {
            activity.startActivity(intent);
//        }

        activity.finish();
    }
}
