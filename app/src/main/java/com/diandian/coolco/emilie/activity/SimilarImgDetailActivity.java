package com.diandian.coolco.emilie.activity;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.diandian.coolco.emilie.R;
import com.diandian.coolco.emilie.model.Image;
import com.diandian.coolco.emilie.utility.ExtraDataName;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import roboguice.inject.InjectView;

public class SimilarImgDetailActivity extends BaseActivity {

    @InjectView(R.id.iv_similar_img)
    private ImageView similarImgView;
    @InjectView(R.id.vp_similar_img)
    private ViewPager similarImgViewPager;
    private Menu mainMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_similar_img_detail);

        init();
    }

    private void init() {
        Intent intent = getIntent();
//        ArrayList<Image> imgs = (ArrayList<Image>) intent.getParcelableExtra(ExtraDataName.SIMILAR_IMGS);
        ArrayList<Image> imgs = intent.getParcelableArrayListExtra(ExtraDataName.SIMILAR_IMGS);
        int initPos = intent.getIntExtra(ExtraDataName.SIMILAR_IMG_INIT_POS, 0);
        PagerAdapter adapter = new SimilarImgViewPagerAdapter(imgs);
        similarImgViewPager.setAdapter(adapter);
        similarImgViewPager.setCurrentItem(initPos, true);
//        ImageLoader.getInstance().displayImage(img.getDownloadUrl(), similarImgView);

    }

    class SimilarImgViewPagerAdapter extends PagerAdapter{

        private List<Image> images;
        private List<View> views;

        SimilarImgViewPagerAdapter(List<Image> images) {
            this.images = images;
            this.views = new ArrayList<View>();
            for (int i = 0; i < images.size(); i++) {
                views.add(null);
            }
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
//            return super.instantiateItem(container, position);
            View view = views.get(position);
            if (view == null) {
                view = new ImageView(SimilarImgDetailActivity.this);
                views.set(position, view);
            }
            container.addView(view);
            ImageLoader.getInstance().displayImage(images.get(position).getDownloadUrl(), (ImageView) view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//            super.destroyItem(container, position, object);
//            container.removeViewAt(position);
            container.removeView(views.get(position));
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_similar_img_detail, menu);
        menu.findItem(R.id.ab_button_list).setIcon(
                new IconDrawable(this, Iconify.IconValue.md_more_vert)
                        .colorRes(R.color.ab_icon)
                        .actionBarSize());
        mainMenu = menu;
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

        if (id == android.R.id.home) {
//            NavUtils.navigateUpFromSameTask(this);
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
                mainMenu.performIdentifierAction(R.id.ab_button_list, 0);
                return true;
        }

        return super.onKeyDown(keycode, e);
    }
}
