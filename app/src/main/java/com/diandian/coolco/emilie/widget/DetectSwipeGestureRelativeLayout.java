package com.diandian.coolco.emilie.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.RelativeLayout;

import com.diandian.coolco.emilie.utility.Event;

import de.greenrobot.event.EventBus;


public class DetectSwipeGestureRelativeLayout extends RelativeLayout {

    private GestureDetector gestureDetector;
    private int minumumFlingVelocity;
    private int overflingDistance;

    public DetectSwipeGestureRelativeLayout(Context context) {
        super(context);
        init();
    }

    public DetectSwipeGestureRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DetectSwipeGestureRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        gestureDetector = new GestureDetector(getContext(), new GestureListener());
        ViewConfiguration viewConfiguration = ViewConfiguration.get(getContext());
        minumumFlingVelocity = viewConfiguration.getScaledMinimumFlingVelocity();
        overflingDistance = viewConfiguration.getScaledOverflingDistance();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        gestureDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_THRESHOLD = 200;
        private static final int SWIPE_VELOCITY_THRESHOLD = 800;

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            boolean result = false;
            try {
                float diffY = e2.getY() - e1.getY();
                float diffX = e2.getX() - e1.getX();
                if (Math.abs(diffX) > Math.abs(diffY)) {
                    if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
//                    if (Math.abs(diffX) > overflingDistance && Math.abs(velocityX) > minumumFlingVelocity) {
                        if (diffX > 0) {
                            onSwipeRight();
                        } else {
                            onSwipeLeft();
                        }
                    }
                    result = true;
                }
                else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffY > 0) {
                        onSwipeBottom();
                    } else {
                        onSwipeTop();
                    }
                }
                result = true;

            } catch (Exception exception) {
                exception.printStackTrace();
            }
            return result;
        }
    }

    public void onSwipeRight() {
//        if (listener != null){
//            listener.onSwipeRight();
//        }
        EventBus.getDefault().post(new Event.SwipeRightEvent());
    }

    public void onSwipeLeft() {
    }

    public void onSwipeTop() {
    }

    public void onSwipeBottom() {
    }

    private SwipeRightListener listener;

    public void setListener(SwipeRightListener listener) {
        this.listener = listener;
    }

    public interface SwipeRightListener {
        public void onSwipeRight();
    }
}
