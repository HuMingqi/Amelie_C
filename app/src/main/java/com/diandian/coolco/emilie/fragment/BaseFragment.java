package com.diandian.coolco.emilie.fragment;


import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

import com.diandian.coolco.emilie.activity.SrcImgObtainActivity;

import roboguice.fragment.RoboFragment;

public class BaseFragment extends RoboFragment {

    protected Context context;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = activity.getApplicationContext();
    }

    /**
     * Called by CameraFragment or GalleyFragment
     * @param imgPath
     */
    protected void imgObtained(String imgPath){
        SrcImgObtainActivity activity = (SrcImgObtainActivity) getActivity();
        activity.startSrcImgCropActivity(imgPath);
    }
}
