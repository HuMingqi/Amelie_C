package com.diandian.coolco.emilie.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.diandian.coolco.emilie.R;
import com.diandian.coolco.emilie.utility.BitmapStorage;
import com.facebook.common.logging.FLog;
import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.BaseDataSubscriber;
import com.facebook.datasource.DataSource;
import com.facebook.datasource.DataSubscriber;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.imagepipeline.core.ExecutorSupplier;
import com.facebook.imagepipeline.core.ImagePipeline;
import com.facebook.imagepipeline.datasource.BaseBitmapDataSubscriber;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.memory.PooledByteBuffer;
import com.facebook.imagepipeline.request.ImageRequest;

import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;

public class WebImageContainer extends RelativeLayout {

    private TextView loadingProgressTextView;
    private SizeAdjustableSimpleDraweeView draweeView;
    private View progerssBar;

    private String imageUrl;

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

//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        requestDisallowInterceptTouchEvent(true);
//        return super.dispatchTouchEvent(ev);
//    }

    public void setImageSize(Point imageSize) {
        setDraweeViewDrawableSize(imageSize.x, imageSize.y);
    }

    public void setDraweeViewDrawableSize(int width, int height) {
        draweeView.setDrawableWidth(width);
        draweeView.setDrawableHeight(height);
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(getResources());
        GenericDraweeHierarchy hierarchy = builder
                .setProgressBarImage(new ProgressBarDrawable() {
                    @Override
                    protected boolean onLevelChange(int level) {
                        setLoadingProgress(level);
                        return true;
                    }

                    @Override
                    public void draw(Canvas canvas) {

                    }
                })
                .setFadeDuration(getResources().getInteger(R.integer.drawee_view_fade_duration))
                .setPlaceholderImage(new ColorDrawable(getResources().getColor(R.color.drawee_view_place_holder)))
                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                .setPressedStateOverlay(new ColorDrawable(getResources().getColor(R.color.drawee_view_press_state_overlay_color)))
                .build();
//        GenericDraweeHierarchy hierarchy = draweeView.getHierarchy();
//        hierarchy.setProgressBarImage(new ProgressBarDrawable(){
//            @Override
//            protected boolean onLevelChange(int level) {
//                setLoadingProgress(level);
//                return true;
//            }
//
//            @Override
//            public void draw(Canvas canvas) {
//
//            }
//        });
        draweeView.setHierarchy(hierarchy);
        draweeView.setImageURI(Uri.parse(imageUrl));
    }

    private void setLoadingProgress(int level) {
        if (level == 0) {
            progerssBar.setVisibility(VISIBLE);
            loadingProgressTextView.setVisibility(VISIBLE);
        } else if (level == 10000) {
            progerssBar.setVisibility(GONE);
            loadingProgressTextView.setVisibility(GONE);
        }

        loadingProgressTextView.setText(String.format("%3d%%", ((int) (level / 100))));
    }

    public void saveWebImage() {
        ImageRequest imageRequest = ImageRequest.fromUri(imageUrl);
        ImagePipeline imagePipeline = Fresco.getImagePipeline();

//        DataSource<CloseableReference<CloseableImage>> dataSource =
//                imagePipeline.fetchImageFromBitmapCache(imageRequest, null);
        DataSource<CloseableReference<CloseableImage>> dataSource =
                imagePipeline.fetchDecodedImage(imageRequest, null);
//        try {
//            CloseableReference<CloseableImage> imageReference = dataSource.getResult();
//            if (imageReference != null) {
//                try {
//                    CloseableImage image = imageReference.get();
//                    // do something with the image
//                    return BitmapStorage.saveImg(getContext(), image)
//                } finally {
//                    CloseableReference.closeSafely(imageReference);
//                }
//            }
//        } finally {
//            dataSource.close();
//        }

        dataSource.subscribe(new BaseBitmapDataSubscriber() {
            @Override
            public void onNewResultImpl(@Nullable Bitmap bitmap) {
                // You can use the bitmap in only limited ways
                // No need to do any cleanup.
                BitmapStorage.saveImg(getContext(), bitmap);
            }

            @Override
            public void onFailureImpl(DataSource dataSource) {
                // No cleanup required here.
            }
        }, new ScheduledThreadPoolExecutor(3));
        return ;
    }
}
