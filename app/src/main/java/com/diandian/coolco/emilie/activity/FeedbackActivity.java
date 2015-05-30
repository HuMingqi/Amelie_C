package com.diandian.coolco.emilie.activity;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.diandian.coolco.emilie.R;
import com.diandian.coolco.emilie.utility.Event;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;

import de.greenrobot.event.EventBus;

public class FeedbackActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        initActionBar();
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#e7e7e7")));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_feedback, menu);
        MenuItem item = menu.findItem(R.id.action_send_feedback);
        IconDrawable sendIcon = new IconDrawable(this, Iconify.IconValue.md_send).actionBarSize().colorRes(R.color.ab_icon);
        item.setIcon(sendIcon);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_feedback) {
            EventBus.getDefault().post(new Event.SendFeedBackEvent());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
