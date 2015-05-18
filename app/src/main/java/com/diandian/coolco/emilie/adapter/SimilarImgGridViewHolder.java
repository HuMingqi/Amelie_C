package com.diandian.coolco.emilie.adapter;

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
        webImageContainer.setImageSize(item.getSize());
        webImageContainer.setImageUrl(item.getDownloadUrl());
    }
}
