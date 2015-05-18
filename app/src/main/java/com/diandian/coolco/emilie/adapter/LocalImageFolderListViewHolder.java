package com.diandian.coolco.emilie.adapter;

import android.net.Uri;
import android.view.View;
import android.widget.TextView;

import com.diandian.coolco.emilie.R;
import com.diandian.coolco.emilie.model.Image;
import com.diandian.coolco.emilie.model.ImageFolder;
import com.diandian.coolco.emilie.widget.WebImageContainer;
import com.facebook.drawee.view.DraweeView;
import com.facebook.drawee.view.SimpleDraweeView;

public class LocalImageFolderListViewHolder extends CommonBaseAdapter.CommonViewHolder<ImageFolder> {

    private SimpleDraweeView firstImageDraweeView;
    private TextView imageFolderNameTextView;
    private TextView imageFolderSizeTextView;

    public LocalImageFolderListViewHolder(View convertView) {
        firstImageDraweeView = (SimpleDraweeView) convertView.findViewById(R.id.dv_local_img_folder_first_img);
        imageFolderNameTextView = (TextView) convertView.findViewById(R.id.tv_local_img_folder_name);
        imageFolderSizeTextView = (TextView) convertView.findViewById(R.id.tv_local_img_folder_size);
    }

    @Override
    public void setItem(ImageFolder item) {
        String uri = String.format("file://%s", item.getFirstImagePath());
        firstImageDraweeView.setImageURI(Uri.parse(uri));
        imageFolderNameTextView.setText(item.getName());
        imageFolderSizeTextView.setText(String.valueOf(item.getCount()));
    }
}
