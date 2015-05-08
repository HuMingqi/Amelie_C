package com.diandian.coolco.emilie.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * you can set the drawable's size to this widget, it will set its size according to that.
 * used in stagger grid view, when the image from web has not arrived yet.
 */
public class PlaceHolderImageView extends ImageView {
    private int drawableWidth;
    private int drawableHeight;

    public PlaceHolderImageView(Context context) {
        super(context);
    }

    public PlaceHolderImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PlaceHolderImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setDrawableWidth(int drawableWidth) {
        this.drawableWidth = drawableWidth;
    }

    public void setDrawableHeight(int drawableHeight) {
        this.drawableHeight = drawableHeight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        int viewHeight = ((int) (((float) drawableHeight) / drawableWidth * viewWidth));
        int newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY);
        super.onMeasure(widthMeasureSpec, newHeightMeasureSpec);
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
