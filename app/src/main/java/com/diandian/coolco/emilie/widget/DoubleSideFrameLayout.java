package com.diandian.coolco.emilie.widget;

import android.content.Context;
import android.graphics.Camera;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;

public class DoubleSideFrameLayout extends FrameLayout {

    private static final long DURATION = 800;
    private View sideA;
    private View sideB;
//    private FlipAnimation flipAnimation;
//    private ReverseFlipAnimation reverseFlipAnimation;
//    private boolean flipped;

    public DoubleSideFrameLayout(Context context) {
        super(context);
    }

    public DoubleSideFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DoubleSideFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        retrieveChildrenView();
    }

    private void retrieveChildrenView() {
        sideA = getChildAt(0);
        sideB = getChildAt(1);
        sideA.setVisibility(VISIBLE);
        sideB.setVisibility(INVISIBLE);
    }

    public void flip() {
        startAnimation(getFlipAnimation());
//        Animation flipAnimation = flipped ? getReverseFlipAnimation() : getFlipAnimation();
//        startAnimation(flipAnimation);
//        flipped = !flipped;
//        FlipAnimation flipAnimation = getFlipAnimation();
//        startAnimation(flipAnimation);
    }

    private Animation getFlipAnimation() {
//        if (flipAnimation == null) {
        FlipAnimation flipAnimation = new FlipAnimation();
        flipAnimation.setFillAfter(true);
        flipAnimation.setDuration(DURATION);
        flipAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
//        }
        return flipAnimation;
    }

    /*

        private Animation getReverseFlipAnimation() {
    //        if (reverseFlipAnimation == null) {
                reverseFlipAnimation = new ReverseFlipAnimation();
                reverseFlipAnimation.setFillAfter(true);
                reverseFlipAnimation.setDuration(DURATION);
                reverseFlipAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
    //        }
            return reverseFlipAnimation;
        }

    */
    class FlipAnimation extends Animation {

        private Camera camera;
        private float centerX;
        private float centerY;
        private boolean swapSide;
        private float maxTranslateZ;

        public FlipAnimation() {
            maxTranslateZ = getContext().getResources().getDisplayMetrics().widthPixels * 0.5f;
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
            camera = new Camera();
            centerX = width / 2;
            centerY = height / 2;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            // Angle around the y-axis of the rotation at the given time. It is
            // calculated both in radians and in the equivalent degrees.
            final double radians = Math.PI * interpolatedTime;
            float degrees = 180 * interpolatedTime;

            // Once we reach the midpoint in the animation, we need to hide the
            // source view and show the destination view. We also need to change
            // the angle by 180 degrees so that the destination does not come in
            // flipped around. This is the main problem with SDK sample, it does not
            // do this.
            if (interpolatedTime >= 0.5f) {
                degrees += 180.f;

                if (!swapSide) {
                    if (sideA.getVisibility() == VISIBLE) {
                        sideA.setVisibility(INVISIBLE);
                        sideB.setVisibility(VISIBLE);
                    } else {
                        sideA.setVisibility(VISIBLE);
                        sideB.setVisibility(INVISIBLE);
                    }
                    swapSide = true;
                }
            }

            final Matrix matrix = t.getMatrix();
            final Camera camera = this.camera;

            camera.save();
            camera.translate(0.0f, 0.0f, (float) (maxTranslateZ * Math.sin(radians)));
            camera.rotateY(-degrees);
            camera.getMatrix(matrix);
            camera.restore();

            matrix.preTranslate(-centerX, -centerY);
            matrix.postTranslate(centerX, centerY);
        }
    }
/*

    class ReverseFlipAnimation extends Animation {

        private Camera camera;
        private float centerX;
        private float centerY;
        private boolean swapSide;
        private float maxTranslateZ;

        public ReverseFlipAnimation() {
            maxTranslateZ = getContext().getResources().getDisplayMetrics().widthPixels * 0.5f;
        }

        @Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
            camera = new Camera();
            centerX = width / 2;
            centerY = height / 2;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            // Angle around the y-axis of the rotation at the given time. It is
            // calculated both in radians and in the equivalent degrees.
            final double radians = Math.PI * interpolatedTime;
            float degrees = 180 * interpolatedTime;

            // Once we reach the midpoint in the animation, we need to hide the
            // source view and show the destination view. We also need to change
            // the angle by 180 degrees so that the destination does not come in
            // flipped around. This is the main problem with SDK sample, it does not
            // do this.
            if (interpolatedTime >= 0.5f) {
                degrees += 180.f;

                if (!swapSide) {
                    sideA.setVisibility(View.VISIBLE);
                    sideB.setVisibility(View.INVISIBLE);
                    swapSide = true;
                }
            }

            final Matrix matrix = t.getMatrix();
            final Camera camera = this.camera;

            camera.save();
            camera.translate(0.0f, 0.0f, (float) (maxTranslateZ * Math.sin(radians)));
            camera.rotateY(-degrees);
            camera.getMatrix(matrix);
            camera.restore();

            matrix.preTranslate(-centerX, -centerY);
            matrix.postTranslate(centerX, centerY);
        }
    }

*/

}
