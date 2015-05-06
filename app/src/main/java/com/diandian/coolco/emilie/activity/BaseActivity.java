package com.diandian.coolco.emilie.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.diandian.coolco.emilie.R;
import com.diandian.coolco.emilie.utility.OnSwipeTouchListener;
import com.diandian.coolco.emilie.widget.SwipeBackLayout;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;

import roboguice.activity.RoboActionBarActivity;


//
public class BaseActivity extends RoboActionBarActivity{

    protected Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = getApplicationContext();

        ActionBar actionBar = getSupportActionBar();
//        actionBar.setIcon(new IconDrawable(context, Iconify.IconValue.md_arrow_back)
//                .colorRes(R.color.ab_icon)
//                .actionBarSize());
        actionBar.setHomeAsUpIndicator(new IconDrawable(context, Iconify.IconValue.md_arrow_back)
                .colorRes(R.color.ab_icon)
                .actionBarSize());
        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setDisplayShowHomeEnabled(true);
//        actionBar.setDisplayUseLogoEnabled(true);
//        actionBar.setDisplayHomeAsUpEnabled(true);
//        actionBar.setDefaultDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getWindow().getDecorView().findViewById(android.R.id.content).setOnTouchListener(new OnSwipeTouchListener(BaseActivity.this){
            @Override
            public void onSwipeRight() {
                finish();
            }
        });
    }

    /*
    private SwipeBackLayout swipeBackLayout;
    private ImageView ivShadow;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(getContainer());
        View view = LayoutInflater.from(this).inflate(layoutResID, null);
        swipeBackLayout.addView(view);
        setDragEdge(SwipeBackLayout.DragEdge.LEFT);
    }

    private View getContainer() {
        RelativeLayout container = new RelativeLayout(this);
        swipeBackLayout = new SwipeBackLayout(this);
        swipeBackLayout.setOnSwipeBackListener(this);
        ivShadow = new ImageView(this);
        ivShadow.setBackgroundColor(Color.parseColor("#66000000"));
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        container.addView(ivShadow, params);
        container.addView(swipeBackLayout);
        return container;
    }

    public void setDragEdge(SwipeBackLayout.DragEdge dragEdge) {
        swipeBackLayout.setDragEdge(dragEdge);
    }

    public SwipeBackLayout getSwipeBackLayout() {
        return swipeBackLayout;
    }

    @Override
    public void onViewPositionChanged(float fractionAnchor, float fractionScreen) {
        ivShadow.setAlpha(1 - fractionScreen);
    }
    */
}
