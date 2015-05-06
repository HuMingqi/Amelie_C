package com.diandian.coolco.emilie.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import com.diandian.coolco.emilie.R;
import com.diandian.coolco.emilie.adapter.CommonFragmentAdapter;
import com.diandian.coolco.emilie.fragment.CameraFragment;
import com.diandian.coolco.emilie.fragment.GalleryFragment;
import com.diandian.coolco.emilie.utility.Dimension;
import com.diandian.coolco.emilie.utility.ExtraDataName;
import com.github.johnpersano.supertoasts.SuperToast;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;

import java.util.ArrayList;
import java.util.List;


public class SrcImgObtainActivity extends BaseActivity {

    private ViewPager obtainImgViewPager;
    private Menu mainMenu;
    private int screenWidth;
    private float bottomBarHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_src_img_obtain);
        getSupportActionBar().setIcon(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        init();
    }

    private void init() {
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        bottomBarHeight = Dimension.dp2px(this, 50);

        obtainImgViewPager = (ViewPager) findViewById(R.id.vp_obtain_img);
        List<Class<? extends Fragment>> fragmentClasses = new ArrayList<Class<? extends Fragment>>();
        fragmentClasses.add(CameraFragment.class);
        fragmentClasses.add(GalleryFragment.class);
        FragmentPagerAdapter obtainImgFragmentAdapter = new CommonFragmentAdapter(getSupportFragmentManager(), fragmentClasses);
//        obtainImgViewPager.setOffscreenPageLimit(0);
//        obtainImgViewPager.setPageTransformer(false, new MyPageTransformer());
        obtainImgViewPager.setAdapter(obtainImgFragmentAdapter);
    }

//    class MyPageTransformer implements ViewPager.PageTransformer {

//        private View cameraBottomBar, galleryBottomBar;

//        @Override
//        public void transformPage(View page, float position) {
//            page.findViewById(R.id.rl_bottom_bar).setTranslationY(bottomBarHeight*Math.abs(position));
//            View captureView = page.findViewById(R.id.iv_capture);
//            View go2galleyView = page.findViewById(R.id.iv_go2gallery);
//            View chosenDirView = page.findViewById(R.id.tv_chosen_dir);
//            View dirImgCountView = page.findViewById(R.id.tv_dir_img_count);
//            if (captureView != null) {
//                captureView.setTranslationY(bottomBarHeight*position*-1);
//            }
//            if (go2galleyView != null) {
//                go2galleyView.setTranslationY(bottomBarHeight*position*-1);
//            }
//            if (chosenDirView != null) {
//                chosenDirView.setTranslationY(bottomBarHeight*position);
//            }
//            if (dirImgCountView != null) {
//                dirImgCountView.setTranslationY(bottomBarHeight*position);
//            }
//        }
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_src_img_obtain, menu);

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
//        if (id == R.id.action_app_settings) {
//            startSettingActivity();
//            return true;
//        }

        switch (id){
            case R.id.action_app_settings:
                startSettingActivity();
                break;
            case R.id.action_search_history:
                final SuperToast superToast = new SuperToast(this);
                superToast.setAnimations(SuperToast.Animations.FADE);
                superToast.setDuration(SuperToast.Duration.MEDIUM);
                superToast.setBackground(SuperToast.Background.GRAY);
                superToast.setTextSize(SuperToast.TextSize.MEDIUM);
//                superToast.setIcon(new IconDrawable(this, Iconify.IconValue.md_done), SuperToast.IconPosition.LEFT);
//                superToast.setIcon(R.drawable.test, SuperToast.IconPosition.LEFT);
                superToast.show();

                startActivity(new Intent(this, TestPullUpDownActivity.class));


                break;

        }

        return super.onOptionsItemSelected(item);
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

    /**
     * called by camera fragment, when go2galleryImageView is clicked.
     */
    public void go2gallery(){
        obtainImgViewPager.setCurrentItem(1, true);
    }

//    private void toggleMoreMenu() {
//        if (moreMenuItem.getSubMenu().hasVisibleItems()){
//
//        }
//        onOptionsItemSelected(moreMenuItem);
//        moreMenuItem.expandActionView();
//        mainMenu.performIdentifierAction(R.id.ab_button_list, 0);
//    }
}
