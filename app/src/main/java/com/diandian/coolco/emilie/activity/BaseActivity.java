package com.diandian.coolco.emilie.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBar;

import com.diandian.coolco.emilie.R;
import com.diandian.coolco.emilie.utility.Event;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;

import de.greenrobot.event.EventBus;
import roboguice.activity.RoboActionBarActivity;


public class BaseActivity extends RoboActionBarActivity{

    protected Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();

        initActionBar();

        registerEventBus();
    }

    @Override
    protected void onDestroy() {
        unregisterEventBus();

        super.onDestroy();
    }

    private void registerEventBus() {
        EventBus.getDefault().register(this);
    }

    private void unregisterEventBus() {
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(Event.SwipeRightEvent event){
        finish();
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(new IconDrawable(context, Iconify.IconValue.md_arrow_back)
                .colorRes(R.color.ab_icon)
                .actionBarSize());
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
}
