package com.diandian.coolco.emilie.fragment;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.diandian.coolco.emilie.R;
import com.diandian.coolco.emilie.utility.Dimension;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;

import roboguice.inject.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends BaseFragment {

    @InjectView(R.id.tv_time_collection)
    private TextView collectionCountTextView;
    @InjectView(R.id.tv_time_share)
    private TextView shareCountTextView;
    @InjectView(R.id.tv_time_login)
    private TextView loginCountTextView;

    @InjectView(R.id.iv_avatar_bg)
    private ImageView avatarBgImageView;

    public AccountFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_account, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setIcon();
        animateAvatarBg();
    }

    private void setIcon(){
        Drawable collectionDrawable = new IconDrawable(getActivity().getApplicationContext(), Iconify.IconValue.md_favorite_outline).actionBarSize().colorRes(R.color.black).alpha(70);
        Drawable shareDrawable = new IconDrawable(getActivity().getApplicationContext(), Iconify.IconValue.md_share).actionBarSize().colorRes(R.color.black).alpha(70);
        Drawable loginDrawable = new IconDrawable(getActivity().getApplicationContext(), Iconify.IconValue.md_search).actionBarSize().colorRes(R.color.black).alpha(70);
        collectionCountTextView.setCompoundDrawables(collectionDrawable, null, null, null);
        shareCountTextView.setCompoundDrawables(shareDrawable, null, null, null);
        loginCountTextView.setCompoundDrawables(loginDrawable, null, null, null);
        int padding = (int) Dimension.dp2px(getActivity().getApplicationContext(), 4);
        collectionCountTextView.setCompoundDrawablePadding(padding);
        shareCountTextView.setCompoundDrawablePadding(padding);
        loginCountTextView.setCompoundDrawablePadding(padding);
    }

    private void animateAvatarBg() {
        avatarBgImageView.post(new Runnable() {
            @Override
            public void run() {
                mScaleFactor = (float) avatarBgImageView.getHeight() / (float) avatarBgImageView.getDrawable().getIntrinsicHeight();
                mMatrix.postScale(mScaleFactor, mScaleFactor);
                avatarBgImageView.setImageMatrix(mMatrix);
                animate();
            }
        });
    }

    private static final int RightToLeft = 1;
    private static final int LeftToRight = 2;
    private static final int DURATION = 13000;

    private ValueAnimator mCurrentAnimator;
    private final Matrix mMatrix = new Matrix();
    private float mScaleFactor;
    private int mDirection = RightToLeft;
    private RectF mDisplayRect = new RectF();

    private void animate() {
        updateDisplayRect();
        if(mDirection == RightToLeft) {
            animate(mDisplayRect.left, mDisplayRect.left - (mDisplayRect.right - avatarBgImageView.getWidth()));
        } else {
//            animate(mDisplayRect.left, 0.0f);
            animate(mDisplayRect.left - (mDisplayRect.right - avatarBgImageView.getWidth()), mDisplayRect.left);
        }
    }

    private void animate(float from, float to) {
        MatrixImageView matrixImageView = new MatrixImageView(avatarBgImageView, mScaleFactor);
        mCurrentAnimator = ObjectAnimator.ofFloat(matrixImageView, "matrixTranslateX", from, to);
        mCurrentAnimator.setDuration(DURATION);
        mCurrentAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (mDirection == RightToLeft)
                    mDirection = LeftToRight;
                else
                    mDirection = RightToLeft;

                animate();
            }
        });
        mCurrentAnimator.start();
    }

    private void updateDisplayRect() {
        mDisplayRect.set(0, 0, avatarBgImageView.getDrawable().getIntrinsicWidth(), avatarBgImageView.getDrawable().getIntrinsicHeight());
        mMatrix.mapRect(mDisplayRect);
    }

    static class MatrixImageView {
        private final ImageView mImageView;
        private float mScaleFactor;
        private final Matrix mMatrix = new Matrix();

        public MatrixImageView(ImageView imageView, float scaleFactor) {
            this.mImageView = imageView;
            this.mScaleFactor = scaleFactor;
        }

        public void setMatrixTranslateX(float dx) {
            mMatrix.reset();
            mMatrix.postScale(mScaleFactor, mScaleFactor);
            mMatrix.postTranslate(dx, 0);
            mImageView.setImageMatrix(mMatrix);
        }
    }
}
