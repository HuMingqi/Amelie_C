package com.diandian.coolco.emilie.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.diandian.coolco.emilie.R;
import com.diandian.coolco.emilie.activity.CameraActivity;
import com.diandian.coolco.emilie.activity.GalleryActivity;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;

import roboguice.inject.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends BaseFragment implements View.OnClickListener{
    @InjectView(R.id.tv_go2camera)
    private TextView go2cameraTextView;
    @InjectView(R.id.tv_go2gallery)
    private TextView go2galleryTextView;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        int iconSizeInDp = 64;
        IconDrawable cameraIconDrawable = new IconDrawable(getActivity(), Iconify.IconValue.md_photo_camera).colorRes(R.color.main_entry).sizeDp(iconSizeInDp);
        IconDrawable galleryIconDrawable = new IconDrawable(getActivity(), Iconify.IconValue.md_photo).colorRes(R.color.main_entry).sizeDp(iconSizeInDp);
        go2cameraTextView.setCompoundDrawables(null, cameraIconDrawable, null, null);
        go2galleryTextView.setCompoundDrawables(null, galleryIconDrawable, null, null);
        go2cameraTextView.setOnClickListener(this);
        go2galleryTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_go2camera:
                startCameraActivity();
                break;
            case R.id.tv_go2gallery:
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
}
