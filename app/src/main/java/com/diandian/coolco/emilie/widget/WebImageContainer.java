package com.diandian.coolco.emilie.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.diandian.coolco.emilie.R;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;

public class WebImageContainer extends RelativeLayout {

    private TextView loadingProgressTextView;
    private SizeAdjustableSimpleDraweeView draweeView;
    private View progerssBar;

    public WebImageContainer(Context context) {
        super(context);
        init();
    }

    public WebImageContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WebImageContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        inflater.inflate(R.layout.custom_widget_web_image_container, this, true);
        draweeView = (SizeAdjustableSimpleDraweeView) findViewById(R.id.dv_web_img);
        progerssBar = findViewById(R.id.pb_loading);
        loadingProgressTextView = (TextView) findViewById(R.id.tv_loading_progress);
    }

    public void setImageSize(Point imageSize) {
        setDraweeViewDrawableSize(imageSize.x, imageSize.y);
    }

    public void setDraweeViewDrawableSize(int width, int height){
        draweeView.setDrawableWidth(width);
        draweeView.setDrawableHeight(height);
    }

    public void setImageUrl(String imageUrl) {
        GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(getResources());
        GenericDraweeHierarchy hierarchy = builder.setProgressBarImage(new ProgressBarDrawable(){
            @Override
            protected boolean onLevelChange(int level) {
                setLoadingProgress(level);
                return true;
            }

            @Override
            public void draw(Canvas canvas) {

            }
        }).build();
        draweeView.setHierarchy(hierarchy);
        draweeView.setImageURI(Uri.parse(imageUrl));
    }

    private void setLoadingProgress(int level) {
        if (level == 0){
            progerssBar.setVisibility(VISIBLE);
            loadingProgressTextView.setVisibility(VISIBLE);
        } else if (level == 10000){
            progerssBar.setVisibility(GONE);
            loadingProgressTextView.setVisibility(GONE);
        }

        loadingProgressTextView.setText(String.format("%3d%%", ((int) (level / 100))));
    }

}
