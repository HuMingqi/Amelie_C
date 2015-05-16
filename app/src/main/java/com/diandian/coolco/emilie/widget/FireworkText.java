package com.diandian.coolco.emilie.widget;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Parcel;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.ForegroundColorSpan;
import android.util.Property;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class FireworkText {

    private TextView textView;
    private long duration;

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
    private ObjectAnimator objectAnimator;

    public FireworkText(TextView textView) {
        this.textView = textView;
    }

    public void setDuration(long duration) {
        this.duration = duration;
        initAnimation();
    }

    private void initAnimation() {
        final FireworksSpanGroup spanGroup = new FireworksSpanGroup(0);
        //init the group with multiple spans
        //set spans on the ActionBar spannable title
        CharSequence plainString = textView.getText();
        final SpannableString mActionBarTitleSpannableString = new SpannableString(plainString);
        for (int i = 0; i < plainString.length(); i++) {
            MutableForegroundColorSpan span = new MutableForegroundColorSpan(0, textView.getTextColors().getDefaultColor());
            spanGroup.addSpan(span);
            mActionBarTitleSpannableString.setSpan(span, i, i+1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        }
        spanGroup.init();
        objectAnimator = ObjectAnimator.ofFloat(spanGroup, FIREWORKS_GROUP_PROGRESS_PROPERTY, 0.0f, 1.0f);
        objectAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                //refresh the ActionBar title

                textView.setText(mActionBarTitleSpannableString);
            }
        });
        objectAnimator.setDuration(duration);
    }

    public void startAnimation(){
        objectAnimator.start();
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
