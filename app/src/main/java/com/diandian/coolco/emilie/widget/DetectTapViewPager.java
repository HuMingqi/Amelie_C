package com.diandian.coolco.emilie.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class DetectTapViewPager extends ViewPager {

    private GestureDetector tapGestureDetector;

    public DetectTapViewPager(Context context) {
        super(context);
        init();
    }

    public DetectTapViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        tapGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (tapListener != null) {
                    tapListener.onTap();
                }
                return super.onSingleTapConfirmed(e);
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        tapGestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    private TapListener tapListener;

    public void setTapListener(TapListener tapListener) {
        this.tapListener = tapListener;
    }

    public interface TapListener {
        public void onTap();
    }
}
