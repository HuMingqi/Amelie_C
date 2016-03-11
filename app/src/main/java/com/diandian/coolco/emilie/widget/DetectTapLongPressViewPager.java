package com.diandian.coolco.emilie.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class DetectTapLongPressViewPager extends ViewPager {

    private GestureDetector gestureDetector;

    public DetectTapLongPressViewPager(Context context) {
        super(context);
        init();
    }

    public DetectTapLongPressViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (gestureListener != null) {
                    gestureListener.onTap();
                }
                return super.onSingleTapConfirmed(e);
            }

            @Override
            public boolean onDoubleTap(MotionEvent e) {
                if (gestureListener != null) {
                    gestureListener.onDoubleTap();
                }
                return super.onDoubleTap(e);
            }

            @Override
            public void onLongPress(MotionEvent e) {
                if (gestureListener != null) {
                    gestureListener.onLongPress();
                }
                super.onLongPress(e);
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        gestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    private GestureListener gestureListener;

    public void setGestureListener(GestureListener gestureListener) {
        this.gestureListener = gestureListener;
    }

    public interface GestureListener {
        void onTap();
        void onLongPress();
        void onDoubleTap();
    }
}
