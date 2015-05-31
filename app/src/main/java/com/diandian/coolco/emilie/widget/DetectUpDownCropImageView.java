package com.diandian.coolco.emilie.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.diandian.coolco.emilie.utility.Event;
import com.edmodo.cropper.CropImageView;

import de.greenrobot.event.EventBus;


public class DetectUpDownCropImageView extends CropImageView {

    public DetectUpDownCropImageView(Context context) {
        super(context);
        init();
    }

    public DetectUpDownCropImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
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
