package com.diandian.coolco.emilie.adapter;

import android.view.View;

import com.diandian.coolco.emilie.model.Image;
import com.diandian.coolco.emilie.widget.WebImageContainer;

public class SimilarImgGridViewHolder extends CommonBaseAdapter.CommonViewHolder<Image> {
//    private SimpleDraweeView draweeView;
//    private NumberProgressCircle numberProgressCircle;
//    private IconDrawable defaultDrawable;
    private WebImageContainer webImageContainer;

    public SimilarImgGridViewHolder(View convertView) {
//            ((CardView)convertView).setPreventCornerOverlap(false);
//            draweeView = (ImageView) convertView.findViewById(R.id.iv_similar_img_grid_item);
//            draweeView = (SimpleDraweeView) convertView.findViewById(R.id.iv_similar_img_grid_item);
//            numberProgressCircleProgressCircle = (NumberProgressCircle) convertView.findViewById(R.id.npc_similar_img_grid_item);
//            draweeView.setMinimumHeight(draweeView.getHeight());
//            draweeView.setImageResource(R.drawable.similar_img_grid_item_empty_grey);
//            defaultDrawable = new IconDrawable(convertView.getContext(), Iconify.IconValue.md_filter_drama).color(Color.parseColor("#474747")).sizeDp(120);
//            draweeView.setImageDrawable(defaultDrawable);
        webImageContainer = (WebImageContainer) convertView;
    }

    @Override
    public void setItem(Image item) {
//            Drawable placeholderDrawable = new ColorDrawable(Color.parseColor("#e7e7e7"));
//            placeholderDrawable.setBounds(0, 0, item.getSize().x, item.getSize().y);
//            draweeView.setImageDrawable(placeholderDrawable);
//            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
//            Bitmap.Config conf = Bitmap.Config.RGB_565;
//            final Bitmap bitmap = Bitmap.createBitmap(item.getSize().x, item.getSize().y, conf);
//            bitmap.eraseColor(draweeView.getContext().getResources().getColor(R.color.cardview_dark_background));
//            bitmap.eraseColor(Color.parseColor("#2b3132"));
//            bitmap.reconfigure(item.getSize().x, item.getSize().y, conf);
//            draweeView.setImageBitmap(bitmap);
//        ((SizeAdjustableSimpleDraweeView) draweeView).setDrawableWidth(item.getSize().x);
//        ((SizeAdjustableSimpleDraweeView) draweeView).setDrawableHeight(item.getSize().y);
//        draweeView.setImageURI(Uri.parse(item.getDownloadUrl()));
        webImageContainer.setImageSize(item.getSize());
        webImageContainer.setImageUrl(item.getDownloadUrl());
        /*
        ImageLoader.getInstance().displayImage(item.getDownloadUrl(), draweeView, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
                numberProgressCircle.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                numberProgressCircle.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                numberProgressCircle.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
                numberProgressCircle.setVisibility(View.GONE);
            }
        }, new ImageLoadingProgressListener() {
            @Override
            public void onProgressUpdate(String s, View view, int current, int total) {
                numberProgressCircle.setProgress(current * 1.0f / total);
            }
        });
        */
    }
}
