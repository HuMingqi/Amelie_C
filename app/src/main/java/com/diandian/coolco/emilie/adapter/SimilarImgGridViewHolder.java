package com.diandian.coolco.emilie.adapter;

import android.graphics.Point;
import android.view.View;

import com.diandian.coolco.emilie.model.Image;
import com.diandian.coolco.emilie.widget.WebImageContainer;

public class SimilarImgGridViewHolder extends CommonBaseAdapter.CommonViewHolder<Image> {
    private WebImageContainer webImageContainer;

    public SimilarImgGridViewHolder(View convertView) {
        webImageContainer = (WebImageContainer) convertView;
    }

    @Override
    public void setItem(Image item) {
        webImageContainer.setImageSize(new Point(item.getWidth(), item.getHeight()));
        webImageContainer.setImageUrl(item.getDownloadUrl());
    }
}
