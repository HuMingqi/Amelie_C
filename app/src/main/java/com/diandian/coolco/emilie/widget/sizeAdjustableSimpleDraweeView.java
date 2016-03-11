package com.diandian.coolco.emilie.widget;

import android.content.Context;
import android.util.AttributeSet;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;

public class SizeAdjustableSimpleDraweeView extends SimpleDraweeView {

    private int drawableWidth;
    private int drawableHeight;
/*

    public SizeAdjustableSimpleDraweeView(Context context, GenericDraweeHierarchy hierarchy) {
        super(context, hierarchy);
    }

*/
    public SizeAdjustableSimpleDraweeView(Context context) {
        super(context);
    }

    public SizeAdjustableSimpleDraweeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SizeAdjustableSimpleDraweeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public void setDrawableWidth(int drawableWidth) {
        this.drawableWidth = drawableWidth;
    }

    public void setDrawableHeight(int drawableHeight) {
        this.drawableHeight = drawableHeight;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (drawableWidth != 0) {
            int viewWidth = MeasureSpec.getSize(widthMeasureSpec);
            int viewHeight = ((int) (((float) drawableHeight) / drawableWidth * viewWidth));
            int newHeightMeasureSpec = MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY);
            super.onMeasure(widthMeasureSpec, newHeightMeasureSpec);
        } else {
            int height = MeasureSpec.getSize(heightMeasureSpec);
            super.onMeasure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY));
        }
    }
}
