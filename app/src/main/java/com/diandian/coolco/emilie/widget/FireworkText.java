package com.diandian.coolco.emilie.widget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Parcel;
import android.support.v7.app.ActionBar;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.util.Property;
import android.widget.TextView;

import com.diandian.coolco.emilie.R;

import java.util.ArrayList;

public class FireworkText {

    private ActionBar supportActionBar;
    private TextView textView;

    private ObjectAnimator objectAnimator;
    private long duration;
    private int animationTextBeg = -1;
    private int animationTextEnd = -1;

    private static final Property<FireworksSpanGroup, Float> FIREWORKS_GROUP_PROGRESS_PROPERTY =
            new Property<FireworksSpanGroup, Float>(Float.class, "FIREWORKS_GROUP_PROGRESS_PROPERTY") {

                @Override
                public void set(FireworksSpanGroup spanGroup, Float value) {
                    spanGroup.setAlpha(value);
                }

                @Override
                public Float get(FireworksSpanGroup spanGroup) {
                    return spanGroup.getAlpha();
                }
            };


    public FireworkText(TextView textView) {
        this.textView = textView;
    }

    public FireworkText(ActionBar supportActionBar) {
        this.supportActionBar = supportActionBar;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setAnimationTextScope(int beg, int end) {
        this.animationTextBeg = beg;
        this.animationTextEnd = end;
    }

    private void setUpAnimation() {
        final FireworksSpanGroup spanGroup = new FireworksSpanGroup(0);
        //init the group with multiple spans
        //set spans on the ActionBar spannable title
        CharSequence plainString = null;
        if (textView != null) {
            plainString = textView.getText();
        }
        if (supportActionBar != null) {
            plainString = supportActionBar.getTitle();
        }
        final SpannableString spannableString = new SpannableString(plainString);
        if (animationTextBeg == -1) {
            animationTextBeg = 0;
        }
        if (animationTextEnd == -1) {
            animationTextEnd = plainString.length();
        }
        for (int i = animationTextBeg; i < animationTextEnd; i++) {
            int color = Color.parseColor("#ffffff");
            if (textView != null) {
                color = textView.getTextColors().getDefaultColor();
            }
            if (supportActionBar != null) {
                color = supportActionBar.getThemedContext().getResources().getColor(R.color.actionbar_title);
            }
            MutableForegroundColorSpan span = new MutableForegroundColorSpan(0, color);
            spanGroup.addSpan(span);
            spannableString.setSpan(span, i, i + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        }
        spanGroup.init();
        objectAnimator = ObjectAnimator.ofFloat(spanGroup, FIREWORKS_GROUP_PROGRESS_PROPERTY, 0.0f, 1.0f);
        objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (textView != null) {
                    textView.setText(spannableString);
                }
                if (supportActionBar != null) {
                    supportActionBar.setTitle(spannableString);
                }
            }
        });
        objectAnimator.setDuration(duration);
    }

    public void startAnimation() {
        setUpAnimation();
        objectAnimator.start();
    }

    public void startAnimationIndefinitely() {
        setUpAnimation();
        objectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        objectAnimator.setRepeatMode(ObjectAnimator.RESTART);
        objectAnimator.start();
    }

    public void stopAnimation() {
        objectAnimator.end();
        supportActionBar = null;
        textView = null;
    }

    private static final class FireworksSpanGroup {
        private final float mAlpha;
        private final ArrayList<MutableForegroundColorSpan> mSpans;

        private FireworksSpanGroup(float alpha) {
            mAlpha = alpha;
            mSpans = new ArrayList<MutableForegroundColorSpan>();
        }

        public void addSpan(MutableForegroundColorSpan span) {
            span.setAlpha((int) (mAlpha * 255));
            mSpans.add(span);
        }

        public void init() {
//            Collections.shuffle(mSpans);
        }

        public void setAlpha(float alpha) {
            int size = mSpans.size();
            float total = 1.0f * size * alpha;

            for (int index = 0; index < size; index++) {
                MutableForegroundColorSpan span = mSpans.get(index);
                if (total >= 1.0f) {
                    span.setAlpha(255);
                    total -= 1.0f;
                } else {
                    span.setAlpha((int) (total * 255));
                    total = 0.0f;
                }
            }
        }

        public float getAlpha() {
            return mAlpha;
        }
    }

    private static class MutableForegroundColorSpan extends ForegroundColorSpan {

        private int mAlpha = 255;
        private int mForegroundColor;

        public MutableForegroundColorSpan(int alpha, int color) {
            super(color);
            mAlpha = alpha;
            mForegroundColor = color;
        }

        public MutableForegroundColorSpan(Parcel src) {
            super(src);
            mForegroundColor = src.readInt();
            mAlpha = src.readInt();
        }

        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(mForegroundColor);
            dest.writeFloat(mAlpha);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(getForegroundColor());
        }

        /**
         * @param alpha from 0 to 255
         */
        public void setAlpha(int alpha) {
            mAlpha = alpha;
        }

        public void setForegroundColor(int foregroundColor) {
            mForegroundColor = foregroundColor;
        }

        public float getAlpha() {
            return mAlpha;
        }

        @Override
        public int getForegroundColor() {
            return Color.argb(mAlpha, Color.red(mForegroundColor), Color.green(mForegroundColor), Color.blue(mForegroundColor));
        }
    }
}
