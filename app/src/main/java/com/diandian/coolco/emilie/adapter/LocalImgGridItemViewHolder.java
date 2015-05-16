package com.diandian.coolco.emilie.adapter;

import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import com.diandian.coolco.emilie.R;
import com.facebook.drawee.view.SimpleDraweeView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

public class LocalImgGridItemViewHolder extends CommonBaseAdapter.CommonViewHolder<String> {
    /*
    private ImageView imageView;
    private static DisplayImageOptions options;

    static {
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.local_img_grid_item_empty_grey)
                .showImageForEmptyUri(R.drawable.local_img_grid_item_empty_grey)
                .showImageOnFail(R.drawable.local_img_grid_item_empty_grey)
                .cacheInMemory(true)
//                .cacheOnDisk(true)//delete this make list scroll more smoothly
//                .considerExifParams(true)
//                .bitmapConfig(Bitmap.Config.RGB_565)
//                .displayer(new FadeInBitmapDisplayer(300))
                .build();
    }
    */
    private SimpleDraweeView draweeView;

    public LocalImgGridItemViewHolder(View convertView) {
        draweeView = (SimpleDraweeView) convertView;
    }

    @Override
    public void setItem(String item) {
        String uri = String.format("file://%s", item);
//        ImageLoader.getInstance().displayImage(uri, imageView, options);
        draweeView.setImageURI(Uri.parse(uri));
    }
}
