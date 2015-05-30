package com.diandian.coolco.emilie.fragment;


import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.support.v8.renderscript.Element;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.diandian.coolco.emilie.R;
import com.diandian.coolco.emilie.activity.CameraActivity;
import com.diandian.coolco.emilie.activity.GalleryActivity;
import com.diandian.coolco.emilie.utility.Dimension;
import com.diandian.coolco.emilie.widget.PanningImageView;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;

import roboguice.inject.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchFragment extends BaseFragment implements View.OnClickListener {
    @InjectView(R.id.tv_go2camera)
    private TextView go2cameraTextView;
    @InjectView(R.id.tv_go2gallery)
    private TextView go2galleryTextView;

    @InjectView(R.id.iv_go2camera)
    private ImageView go2cameraImageView;
    @InjectView(R.id.iv_go2gallery)
    private ImageView go2galleryImageView;

    @InjectView(R.id.fl_root)
    private View rootView;

    //   @InjectView(R.id.piv_search_bg)
    //  private PanningImageView panningImageView;

    public SearchFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
//        blur(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher), rootView, 24);
        Bitmap bitmap = blurImage(BitmapFactory.decodeResource(getResources(), R.drawable.search_bg_new));
        rootView.setBackgroundDrawable(new BitmapDrawable(getResources(), bitmap));
//        panningImageView.setImageBitmap(blurImage(BitmapFactory.decodeResource(getResources(), R.drawable.panning)));
        int iconSizeInDp = 64;
        int iconSize = getResources().getDimensionPixelOffset(R.dimen.size_search_fragment_button) / 2;
        IconDrawable cameraIconDrawable = new IconDrawable(getActivity(), Iconify.IconValue.md_photo_camera).colorRes(R.color.ab_icon).sizePx(iconSize);
        IconDrawable galleryIconDrawable = new IconDrawable(getActivity(), Iconify.IconValue.md_photo).colorRes(R.color.ab_icon).sizePx(iconSize);
//        go2cameraTextView.setCompoundDrawables(null, cameraIconDrawable, null, null);
//        go2galleryTextView.setCompoundDrawables(null, galleryIconDrawable, null, null);
//        go2cameraTextView.setOnClickListener(this);
//        go2galleryTextView.setOnClickListener(this);
        go2cameraImageView.setImageDrawable(cameraIconDrawable);
        go2galleryImageView.setImageDrawable(galleryIconDrawable);
        go2cameraImageView.setOnClickListener(this);
        go2galleryImageView.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_go2camera:
                startCameraActivity();
                break;
            case R.id.iv_go2gallery:
                startGalleryActivity();
                break;
        }
    }

    private void startGalleryActivity() {
        Intent intent = new Intent(getActivity(), GalleryActivity.class);
        startActivity(intent);
    }

    private void startCameraActivity() {
        Intent intent = new Intent(getActivity(), CameraActivity.class);
        startActivity(intent);
    }

    Bitmap blurImage(Bitmap input) {
        RenderScript rsScript = RenderScript.create(getActivity());
        Allocation alloc = Allocation.createFromBitmap(rsScript, input);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rsScript, Element.U8_4(rsScript));
        blur.setRadius(25);
        blur.setInput(alloc);

        Bitmap result = Bitmap.createBitmap(input.getWidth(), input.getHeight(), input.getConfig());
        Allocation outAlloc = Allocation.createFromBitmap(rsScript, result);
        blur.forEach(outAlloc);
        outAlloc.copyTo(result);

        rsScript.destroy();
        return result;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private void blur(Bitmap bkg, View view, float radius) {
        Bitmap overlayBitmap = Bitmap.createBitmap(
                Dimension.getScreenWidth(getActivity()),
                Dimension.getScreenHeight(getActivity()),
                bkg.getConfig());
//        Canvas canvas = new Canvas(overlayBitmap);
//        canvas.drawBitmap(bkg, -view.getLeft(),
//                -view.getTop(), null);
        RenderScript renderScript = RenderScript.create(getActivity());
        Allocation allocation = Allocation.createFromBitmap(
                renderScript, overlayBitmap);
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(
                renderScript, allocation.getElement());
        blur.setInput(allocation);
        blur.setRadius(radius);
        blur.forEach(allocation);
        allocation.copyTo(overlayBitmap);
        view.setBackground(new BitmapDrawable(
                getResources(), overlayBitmap));
        renderScript.destroy();
    }
}
