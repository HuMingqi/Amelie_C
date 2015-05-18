package com.diandian.coolco.emilie.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.diandian.coolco.emilie.R;
import com.diandian.coolco.emilie.dialog.ClothesInfoDialogFragment;
import com.diandian.coolco.emilie.utility.Event;
import com.diandian.coolco.emilie.utility.MyApplication;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.squareup.leakcanary.RefWatcher;

import de.greenrobot.event.EventBus;
import roboguice.activity.RoboActionBarActivity;


public class BaseActivity extends RoboActionBarActivity{

    protected Context context;
    private boolean guideFeedbackDialogIsShowing;
    private Menu mainMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();
        initActionBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerEventBus();
    }

    @Override
    protected void onPause() {
        unregisterEventBus();
        super.onPause();
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

    public void onEventMainThread(Event.ShakeEvent event){
        if (!guideFeedbackDialogIsShowing) {
            DialogFragment dialogFragment = new ClothesInfoDialogFragment("您遇到什么问题了吗？", this);
            dialogFragment.show(getSupportFragmentManager(), "shake");
            guideFeedbackDialogIsShowing = true;
        }
    }

    public void onEventMainThread(Event.GuideFeedbackDialogDismissEvent event){
        guideFeedbackDialogIsShowing = false;
    }

    private void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(new IconDrawable(context, Iconify.IconValue.md_arrow_back)
                .colorRes(R.color.ab_icon)
                .actionBarSize());
        actionBar.setDisplayHomeAsUpEnabled(true);
    }


    protected void initMenu(Menu menu) {
        MenuItem moreMenuItem = menu.findItem(R.id.ab_button_list);
        if (moreMenuItem != null) {
            moreMenuItem.setIcon(
                    new IconDrawable(this, Iconify.IconValue.md_more_vert)
                            .colorRes(R.color.ab_icon)
                            .actionBarSize());
            mainMenu = menu;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * open or close the more menu when pressing menu hardware button
     */
    @Override
    public boolean onKeyDown(int keycode, KeyEvent e) {
        switch (keycode) {
            case KeyEvent.KEYCODE_MENU:
                if (mainMenu != null) {
                    mainMenu.performIdentifierAction(R.id.ab_button_list, 0);
                    return true;
                }
                break;
        }

        return super.onKeyDown(keycode, e);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = MyApplication.getRefWatcher(this);
        refWatcher.watch(this);

    }
}
