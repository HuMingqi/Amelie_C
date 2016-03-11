package com.diandian.coolco.emilie.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class PanningImageView extends ImageView implements ValueAnimator.AnimatorUpdateListener {


    private enum Direction {RIGHT2LEFT, LEFT2RIGHT}

    private Direction direction;

    private static final int DURATION = 15000;

    private float scaleRatio;
//    private float maxTranslateX;
    private final Matrix matrix = new Matrix();
    private ValueAnimator animator;

    public PanningImageView(Context context) {
        super(context);
    }

    public PanningImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PanningImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        init();
    }

    private void init() {
        scaleRatio = ((float) getMeasuredHeight())/getDrawable().getIntrinsicHeight();
        matrix.postScale(scaleRatio, scaleRatio);
        setImageMatrix(matrix);

        RectF rectF = new RectF(0, 0, getDrawable().getIntrinsicWidth(), getDrawable().getIntrinsicHeight());
        matrix.mapRect(rectF);
//        maxTranslateX = rectF.width() - getMeasuredWidth();

        animator = ValueAnimator.ofFloat(rectF.left, rectF.left-(rectF.right-getMeasuredWidth()));
        animator.setDuration(DURATION);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.addUpdateListener(this);
        post(new Runnable() {
            @Override
            public void run() {
                animator.start();
            }
        });
    }

    @Override
    public void onAnimationUpdate(ValueAnimator valueAnimator) {
        matrix.reset();
        matrix.postScale(scaleRatio, scaleRatio);
        matrix.postTranslate((Float) valueAnimator.getAnimatedValue(), 0);
        setImageMatrix(matrix);
    }
}
