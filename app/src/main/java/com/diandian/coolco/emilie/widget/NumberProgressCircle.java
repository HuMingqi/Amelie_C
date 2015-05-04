package com.diandian.coolco.emilie.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.TextView;

public class NumberProgressCircle extends FrameLayout {

    private TextView numberTextView;

    public NumberProgressCircle(Context context) {
        super(context);
    }

    public NumberProgressCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NumberProgressCircle(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        getChildren();
    }

    private void getChildren() {
        numberTextView = (TextView) getChildAt(1);
    }

    public void setProgress(float progress){
        numberTextView.setText(String.format("%3d%%", ((int) (progress * 100))));
    }
}
