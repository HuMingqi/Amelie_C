package com.diandian.coolco.emilie.utility;

import android.content.Context;
import android.view.Gravity;

import com.github.johnpersano.supertoasts.SuperToast;

public class SuperToastUtil {

    public static void showToast(Context context, String text){
        final SuperToast superToast = new SuperToast(context);
//        superToast.setAnimations(SuperToast.Animations.FADE);
        superToast.setAnimations(SuperToast.Animations.FLYIN);
//        superToast.setDuration(SuperToast.Duration.VERY_SHORT);
        superToast.setDuration(1000);
        superToast.setBackground(SuperToast.Background.BLACK);
        superToast.setTextSize(SuperToast.TextSize.SMALL);
        superToast.setText(text);
//                superToast.setIcon(new IconDrawable(this, Iconify.IconValue.md_done), SuperToast.IconPosition.LEFT);
//                superToast.setIcon(R.drawable.test, SuperToast.IconPosition.LEFT);
        superToast.show();
    }
}
