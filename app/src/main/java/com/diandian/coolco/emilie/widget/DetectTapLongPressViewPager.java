package com.diandian.coolco.emilie.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

public class DetectTapLongPressViewPager extends ViewPager {

    private GestureDetector tapGestureDetector;

    public DetectTapLongPressViewPager(Context context) {
        super(context);
        init();
    }

    public DetectTapLongPressViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        tapGestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapConfirmed(MotionEvent e) {
                if (tapLongPressListener != null) {
                    tapLongPressListener.onTap();
                }
                return super.onSingleTapConfirmed(e);
            }



            @Override
            public void onLongPress(MotionEvent e) {
                if (tapLongPressListener != null) {
                    tapLongPressListener.onLongPress();
                }
                super.onLongPress(e);
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        tapGestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    private TapLongPressListener tapLongPressListener;

    public void setTapLongPressListener(TapLongPressListener tapLongPressListener) {
        this.tapLongPressListener = tapLongPressListener;
    }

    public interface TapLongPressListener {
        void onTap();
        void onLongPress();
    }
}
