package com.diandian.coolco.emilie.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.diandian.coolco.emilie.utility.Event;

import de.greenrobot.event.EventBus;


public class DetectSwipeUpDownRelativeLayout extends DetectSwipeGestureRelativeLayout {

    public DetectSwipeUpDownRelativeLayout(Context context) {
        super(context);
        init();
    }

    public DetectSwipeUpDownRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DetectSwipeUpDownRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        handleTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    private void handleTouchEvent(MotionEvent ev) {
        switch (ev.getAction()){
            case MotionEvent.ACTION_DOWN:{
                EventBus.getDefault().post(new Event.CropDownEvent());
                break;
            }
            case MotionEvent.ACTION_UP:case MotionEvent.ACTION_CANCEL:{
                EventBus.getDefault().post(new Event.CropUpEvent());
                break;
            }
        }
    }
}
