package com.diandian.coolco.emilie.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.diandian.coolco.emilie.R;
import com.diandian.coolco.emilie.adapter.CommonFragmentAdapter;
import com.diandian.coolco.emilie.fragment.CameraFragment;
import com.diandian.coolco.emilie.fragment.GalleryFragment;
import com.diandian.coolco.emilie.utility.Dimension;
import com.diandian.coolco.emilie.utility.ExtraDataName;

import java.util.ArrayList;
import java.util.List;


public class SrcImgObtainActivity extends BaseActivity {

    private ViewPager obtainImgViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_src_img_obtain);
        init();
    }

    private void init() {
        obtainImgViewPager = (ViewPager) findViewById(R.id.vp_obtain_img);
        List<Class<? extends Fragment>> fragmentClasses = new ArrayList<Class<? extends Fragment>>();
        fragmentClasses.add(CameraFragment.class);
        fragmentClasses.add(GalleryFragment.class);
        FragmentPagerAdapter obtainImgFragmentAdapter = new CommonFragmentAdapter(getSupportFragmentManager(), fragmentClasses);
        obtainImgViewPager.setAdapter(obtainImgFragmentAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_src_img_obtain, menu);
        initMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.action_app_settings:
                startSettingActivity();
                break;
            case R.id.action_search_history:

                break;
            case R.id.action_my_collection:
                startCollectionActivity();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void startCollectionActivity(){
        Intent intent = new Intent(this, CollectionActivity.class);
        startActivity(intent);
    }

    private void startSettingActivity() {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }


    /**
     * Called by BaseFragment
     *
     * @param srcImgPath
     */
    public void startSrcImgCropActivity(String srcImgPath) {
        Intent intent = new Intent(this, SrcImgCropActivity.class);
        intent.putExtra(ExtraDataName.SRC_IMG_PATH, srcImgPath);
        startActivity(intent);
    }

    /**
     * return to the camera fragment before leaving app
     */
    @Override
    public void onBackPressed() {
        if (obtainImgViewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            obtainImgViewPager.setCurrentItem(0, true);
        }
    }

    /**
     * called by camera fragment, when go2galleryImageView is clicked.
     */
    public void go2gallery() {
        obtainImgViewPager.setCurrentItem(1, true);
    }
}