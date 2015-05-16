package com.diandian.coolco.emilie.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;

public class ColorFilterGridView extends GridView {

    private int prePosition = -1;

    public ColorFilterGridView(Context context) {
        super(context);
    }
    public ColorFilterGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public ColorFilterGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        int action = event.getAction();

        int curPostion = pointToPosition(((int) event.getX()), ((int) event.getY()));
        if (curPostion == -1 && prePosition != -1){
            cancelItemViewAt(prePosition, event);
        } else if (curPostion != -1 && prePosition ==-1){
            downItemViewAt(curPostion, event);
        } else if (curPostion != -1 && prePosition != -1 && curPostion != prePosition){
            downItemViewAt(curPostion, event);
            cancelItemViewAt(prePosition, event);
        }
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                prePosition = curPostion;
                break;
            case MotionEvent.ACTION_MOVE:
                prePosition = curPostion;
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (curPostion != -1){
                    cancelItemViewAt(curPostion, event);
                }
                prePosition = -1;
                break;

            default:
                break;
        }

        return super.dispatchTouchEvent(event);
    }

    private void downItemViewAt(int position, MotionEvent event) {
        View itemView = getChildAt(position - getFirstVisiblePosition());

        MotionEvent cancelEvent = MotionEvent.obtain(event);
        cancelEvent.setAction(MotionEvent.ACTION_DOWN | (event.getActionIndex()<< MotionEvent.ACTION_POINTER_INDEX_SHIFT));
        itemView.dispatchTouchEvent(cancelEvent);
    }

    private void cancelItemViewAt(int position, MotionEvent event) {
        View itemView = getChildAt(position - getFirstVisiblePosition());

        MotionEvent cancelEvent = MotionEvent.obtain(event);
        cancelEvent.setAction(MotionEvent.ACTION_CANCEL | (event.getActionIndex()<< MotionEvent.ACTION_POINTER_INDEX_SHIFT));
        itemView.dispatchTouchEvent(cancelEvent);
    }
}
