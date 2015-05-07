package com.diandian.coolco.emilie.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.diandian.coolco.emilie.R;
import com.diandian.coolco.emilie.utility.Logcat;

/**
 * 重新 measure 和 layout，将 header 和 footer 绘制在屏幕以外
 */
public class PullUpDownLinearLayout extends LinearLayout {

    private final static float SCROLL_RATIO = 0.35f;
    private static final long DURATION = 200;
    private static final long ROTATE_ANIM_DURATION = 180;

    private enum STATE {RELEASE_TO_TRIGGER_PULL_DOWN_ACTION, PULL_TO_TRIGGER_PULL_DOWN_ACTION, PULL_TO_TRIGGER_PULL_UP_ACTION, RELEASE_TO_TRIGGER_PULL_UP_ACTION, NORMAL}

    private STATE state = STATE.NORMAL;

    private View headerView;
    private View contentView;
    private View footerView;

    private float lastY = -1;

    private int footerViewHeight;
    private TextView footerTextView;
    private TextView headerTextView;
    private Animation rotateUpAnim;
    private Animation rotateDownAnim;
    private ImageView footerArrowImageView;
    private ImageView headerArrowImageView;


    public PullUpDownLinearLayout(Context context) {
        super(context);
        init();
    }

    public PullUpDownLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PullUpDownLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        rotateUpAnim = new RotateAnimation(0.0f, -180.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        rotateUpAnim.setDuration(ROTATE_ANIM_DURATION);
        rotateUpAnim.setFillAfter(true);
        rotateDownAnim = new RotateAnimation(-180.0f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        rotateDownAnim.setDuration(ROTATE_ANIM_DURATION);
        rotateDownAnim.setFillAfter(true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        headerView = getChildAt(0);
        contentView = getChildAt(1);
        footerView = getChildAt(2);

        footerView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
        footerViewHeight = footerView.getMeasuredHeight();
        LayoutParams layoutParams = (LayoutParams) footerView.getLayoutParams();
        layoutParams.height = footerViewHeight;
        footerView.setLayoutParams(layoutParams);

        headerArrowImageView = (ImageView) findViewById(R.id.iv_pud_header_arrow);
        headerTextView = (TextView) findViewById(R.id.tv_pud_header_operation_hint);

        footerArrowImageView = (ImageView) findViewById(R.id.iv_pud_footer_arrow);
        footerTextView = (TextView) footerView.findViewById(R.id.tv_pud_footer_operation_hint);

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                handleTouchEvent(event);
                return true;
            }
        });

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        handleTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    private void handleTouchEvent(MotionEvent event) {
        if (lastY == -1) {
            lastY = event.getY();
        }
        float dy = event.getY() - lastY;
        lastY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                scrollBy(0, (int) (-dy * SCROLL_RATIO));
                updateHeaderFooterView();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (getScrollY() >= footerView.getMeasuredHeight()) {
                    if (pullListener != null) {
                        pullListener.onPullDown();
                    }
                    Logcat.e("footer trigger");
                } else if (getScrollY() <= -headerView.getMeasuredHeight()) {
                    if (pullListener != null){
                        pullListener.onPullUp();
                    }
                    Logcat.e("header trigger");
                }
                smoothScrollTo(0);
                break;
        }

    }

    private void updateHeaderFooterView() {
        int scrollY = getScrollY();
        if (scrollY < -headerView.getMeasuredHeight()) {//[-infinite, -headerViewHeight]
            if (state != STATE.RELEASE_TO_TRIGGER_PULL_DOWN_ACTION) {
                headerTextView.setText("松开立即分享");
                headerArrowImageView.startAnimation(rotateUpAnim);
                state = STATE.RELEASE_TO_TRIGGER_PULL_DOWN_ACTION;
            }
        } else if (scrollY < 0) {//[-headerViewHeight, 0]
            if (state != STATE.PULL_TO_TRIGGER_PULL_DOWN_ACTION) {
                headerTextView.setText("下拉快速分享");
                headerArrowImageView.startAnimation(rotateDownAnim);
                state = STATE.PULL_TO_TRIGGER_PULL_DOWN_ACTION;
            }
        } else if (scrollY < footerView.getMeasuredHeight()) {//[0, footerViewHeight]
            if (state != STATE.PULL_TO_TRIGGER_PULL_UP_ACTION) {
                footerTextView.setText("上拉快速收藏");
                footerArrowImageView.startAnimation(rotateUpAnim);
                state = STATE.PULL_TO_TRIGGER_PULL_UP_ACTION;
            }
        } else {//[footerViewHeight, infinite]
            if (state != STATE.RELEASE_TO_TRIGGER_PULL_UP_ACTION) {
                footerTextView.setText("松开立即收藏");
                footerArrowImageView.startAnimation(rotateDownAnim);
                state = STATE.RELEASE_TO_TRIGGER_PULL_UP_ACTION;
            }
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//        super.onLayout(changed, l, t, r, b);

        headerView.layout(0, t - headerView.getMeasuredHeight(), 800, t);
        contentView.layout(0, t, 800, b);
        footerView.layout(0, b, 800, b + footerView.getMeasuredHeight());
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //remeasure content view, let it occupy all the visible space
        contentView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
//        contentView.measure(MeasureSpec.makeMeasureSpec(getMeasuredWidth(), MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(getMeasuredHeight(), MeasureSpec.EXACTLY));
//        //remeasure footer view, because it has no height in system measure
//        footerView.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
    }

    private void smoothScrollTo(int toScrollY) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(getScrollY(), toScrollY);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.setDuration(DURATION);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float curScrollY = (Float) animation.getAnimatedValue();
                scrollTo(0, curScrollY.intValue());
            }
        });
        valueAnimator.start();
    }

    private PullListener pullListener;

    public void setPullListener(PullListener pullListener) {
        this.pullListener = pullListener;
    }

    public static interface PullListener {
        public void onPullDown();

        public void onPullUp();
    }
}
