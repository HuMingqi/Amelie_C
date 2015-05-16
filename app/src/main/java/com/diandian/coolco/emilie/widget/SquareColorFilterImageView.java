package com.diandian.coolco.emilie.widget;

import android.content.Context;
import android.util.AttributeSet;

public class SquareColorFilterImageView extends ColorFilterImageView {

	public SquareColorFilterImageView(Context context) {
		this(context, null, 0);
	}

	public SquareColorFilterImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SquareColorFilterImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(widthMeasureSpec, widthMeasureSpec);
	}
}
