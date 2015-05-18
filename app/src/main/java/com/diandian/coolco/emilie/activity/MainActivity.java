package com.diandian.coolco.emilie.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.diandian.coolco.emilie.R;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;

import roboguice.inject.InjectView;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @InjectView(R.id.tv_go2camera)
    private TextView go2cameraTextView;
    @InjectView(R.id.tv_go2gallery)
    private TextView go2galleryTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        setContentView(R.layout.activity_main);

        init();
    }

    private void init() {
        int iconSizeInDp = 80;
        IconDrawable cameraIconDrawable = new IconDrawable(this, Iconify.IconValue.md_photo_camera).colorRes(R.color.main_entry).sizeDp(iconSizeInDp);
        IconDrawable galleryIconDrawable = new IconDrawable(this, Iconify.IconValue.md_photo).colorRes(R.color.main_entry).sizeDp(iconSizeInDp);
        go2cameraTextView.setCompoundDrawables(null, cameraIconDrawable, null, null);
        go2galleryTextView.setCompoundDrawables(null, galleryIconDrawable, null, null);
        go2cameraTextView.setOnClickListener(this);
        go2galleryTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_go2camera:
                break;
            case R.id.tv_go2gallery:
                break;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        initMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startSrcImgObtainActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startSrcImgObtainActivity() {
        Intent intent = new Intent(this, SrcImgObtainActivity.class);
        startActivity(intent);
    }

}
