package com.diandian.coolco.emilie.adapter;

import android.net.Uri;
import android.view.View;

import com.facebook.drawee.view.SimpleDraweeView;

public class LocalImgGridItemViewHolder extends CommonBaseAdapter.CommonViewHolder<String> {

    private SimpleDraweeView draweeView;

    public LocalImgGridItemViewHolder(View convertView) {
        draweeView = (SimpleDraweeView) convertView;
    }

    @Override
    public void setItem(String item) {
        String uri = String.format("file://%s", item);
        draweeView.setImageURI(Uri.parse(uri));
    }
}
