package com.diandian.coolco.emilie.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.diandian.coolco.emilie.R;
import com.diandian.coolco.emilie.model.Image;
import com.diandian.coolco.emilie.utility.ExtraDataName;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.nostra13.universalimageloader.core.ImageLoader;

import roboguice.inject.InjectView;

public class SimilarImgDetailActivity extends BaseActivity {

    @InjectView(R.id.iv_similar_img)
    private ImageView similarImgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_similar_img_detail);

        init();
    }

    private void init() {
        Intent intent = getIntent();
        Image img = intent.getParcelableExtra(ExtraDataName.SIMILAR_IMG);
        ImageLoader.getInstance().displayImage(img.getDownloadUrl(), similarImgView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_similar_img_detail, menu);
        menu.findItem(R.id.ab_button_list).setIcon(
                new IconDrawable(this, Iconify.IconValue.md_more_vert)
                        .colorRes(R.color.ab_icon)
                        .actionBarSize());
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
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
