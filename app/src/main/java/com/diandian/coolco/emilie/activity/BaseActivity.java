package com.diandian.coolco.emilie.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import roboguice.activity.RoboActionBarActivity;


public class BaseActivity extends RoboActionBarActivity{

    protected Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();
    }
}
