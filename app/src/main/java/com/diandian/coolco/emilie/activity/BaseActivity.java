package com.diandian.coolco.emilie.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import com.diandian.coolco.emilie.R;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;

import roboguice.activity.RoboActionBarActivity;


//
public class BaseActivity extends RoboActionBarActivity{

    protected Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();

        ActionBar actionBar = getSupportActionBar();
//        actionBar.setIcon(new IconDrawable(context, Iconify.IconValue.md_arrow_back)
//                .colorRes(R.color.ab_icon)
//                .actionBarSize());
        actionBar.setHomeAsUpIndicator(new IconDrawable(context, Iconify.IconValue.md_arrow_back)
                .colorRes(R.color.ab_icon)
                .actionBarSize());
        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setDisplayShowHomeEnabled(true);
//        actionBar.setDisplayUseLogoEnabled(true);
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
    }
}
