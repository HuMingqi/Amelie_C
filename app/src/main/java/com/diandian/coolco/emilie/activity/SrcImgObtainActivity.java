package com.diandian.coolco.emilie.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.diandian.coolco.emilie.R;
import com.diandian.coolco.emilie.adapter.CommonFragmentAdapter;
import com.diandian.coolco.emilie.fragment.CameraFragment;
import com.diandian.coolco.emilie.fragment.GalleryFragment;
import com.diandian.coolco.emilie.utility.ExtraDataName;

import java.util.ArrayList;
import java.util.List;


public class SrcImgObtainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_src_img_obtain);
        init();
    }

    private void init() {
        ViewPager obtainImgViewPager = (ViewPager) findViewById(R.id.vp_obtain_img);
        List<Class<? extends Fragment>> fragmentClasses = new ArrayList<Class<? extends Fragment>>();
        fragmentClasses.add(CameraFragment.class);
        fragmentClasses.add(GalleryFragment.class);
        FragmentPagerAdapter obtainImgFragmentAdapter = new CommonFragmentAdapter(getSupportFragmentManager(), fragmentClasses);
        obtainImgViewPager.setAdapter(obtainImgFragmentAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_src_img_get, menu);
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



    /**
     * Called by BaseFragment
     * @param srcImgPath
     */
    public void startSrcImgCropActivity(String srcImgPath) {
        Intent intent = new Intent(this, SrcImgCropActivity.class);
        intent.putExtra(ExtraDataName.SRC_IMG_PATH, srcImgPath);
        startActivity(intent);
    }
}
