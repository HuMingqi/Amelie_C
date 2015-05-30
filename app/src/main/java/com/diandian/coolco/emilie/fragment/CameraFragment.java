package com.diandian.coolco.emilie.fragment;


import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.diandian.coolco.emilie.R;
import com.diandian.coolco.emilie.activity.SimilarImgActivity;
import com.diandian.coolco.emilie.utility.BitmapStorage;
import com.diandian.coolco.emilie.utility.CameraPreviewSurfaceView;
import com.diandian.coolco.emilie.utility.Dimension;
import com.diandian.coolco.emilie.utility.Event;
import com.diandian.coolco.emilie.utility.ExtraDataName;
import com.diandian.coolco.emilie.utility.MyApplication;
import com.diandian.coolco.emilie.utility.Preference;
import com.diandian.coolco.emilie.utility.PreferenceKey;
import com.diandian.coolco.emilie.utility.SuperToastUtil;
import com.diandian.coolco.emilie.utility.SystemUiHelper;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.util.AsyncExecutor;
import roboguice.inject.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressWarnings("deprecation")
public class CameraFragment extends BaseFragment implements View.OnClickListener {

    private final static String TAG = CameraFragment.class.getSimpleName();
    private enum  Mode {CAPTURE, CHOOSE};

    @InjectView(R.id.fl_camera)
    private FrameLayout cameraFrameLayout;
    @InjectView(R.id.iv_capture)
    private ImageView captureImageView;

    @InjectView(R.id.iv_camera_cancel)
    private ImageView cancelImageView;
    @InjectView(R.id.iv_camera_retry)
    private ImageView retryImageView;
    @InjectView(R.id.iv_camera_done)
    private ImageView doneImageView;

    @InjectView(R.id.fl_root)
    private ViewGroup rootView;

//    @InjectView(R.id.iv_go2gallery)
//    private ImageView go2galleryImageView;

    private Camera camera;
    private static byte[] picData;
    private String srcImgPath;

    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            setFloatActionButtonVisiblity(Mode.CHOOSE);
            CameraFragment.picData = data;
        }
    };


    private void startSimilarImgActivity(String srcImgPath) {
        Intent intent = new Intent(getActivity(), SimilarImgActivity.class);
        intent.putExtra(ExtraDataName.CROPPED_SRC_IMG_PATH, srcImgPath);

        getActivity().startActivity(intent);
    }

    private void rotateAndSavePicInBg(final byte[] data, final File pictureFile) {
        ((MyApplication) getActivity().getApplication()).getAsyncExecutor().execute(new AsyncExecutor.RunnableEx() {
            @Override
            public void run() throws Exception {
                byte[] rotatedBitmapData = rotateBitmap(data);
                saveBitmap(rotatedBitmapData, pictureFile);
                srcImgStorageCompleted();
            }
        });
    }

    private void saveBitmap(byte[] data, File pictureFile) {
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(data);
            fos.close();
        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }

    private byte[] rotateBitmap(final byte[] data) {
        Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, false);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    private void srcImgStorageCompleted() {
        //set flag
        Preference.setPrefBoolean(getActivity().getApplicationContext(), PreferenceKey.IS_SRC_IMG_STORAGE_COMPLETED, true);

        //send event
        EventBus.getDefault().post(new Event.SrcImgSavedEvent());
    }

    public CameraFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        int iconSize = getResources().getDimensionPixelOffset(R.dimen.size_float_action_button)/2;
        captureImageView.setImageDrawable(new IconDrawable(getActivity().getApplicationContext(), Iconify.IconValue.md_camera)
                .colorRes(R.color.ab_icon)
                .actionBarSize());
//                .sizePx(iconSize));
        cancelImageView.setImageDrawable(new IconDrawable(getActivity().getApplicationContext(), Iconify.IconValue.md_close)
                .colorRes(R.color.ab_icon).actionBarSize());
//                .sizePx(iconSize));
        retryImageView.setImageDrawable(new IconDrawable(getActivity().getApplicationContext(), Iconify.IconValue.md_refresh)
                .colorRes(R.color.ab_icon).actionBarSize());
//                .sizePx(iconSize));
        doneImageView.setImageDrawable(new IconDrawable(getActivity().getApplicationContext(), Iconify.IconValue.md_done)
                .colorRes(R.color.ab_icon).actionBarSize());
//                .sizePx(iconSize));
//        go2galleryImageView.setOnClickListener(this);

        if (!checkCameraHardware(getActivity().getApplicationContext())) {
            SuperToastUtil.showToast(getActivity(), "摄像头不可用");
            return;
        }

        camera = getCameraInstance();
        if (camera == null) {
            SuperToastUtil.showToast(getActivity(), "打开摄像头失败，请重试");
            return;
        }
        SurfaceView cameraPreviewSurfaceView = new CameraPreviewSurfaceView(getActivity(), camera);
        cameraFrameLayout.addView(cameraPreviewSurfaceView, 0);

        captureImageView.setOnClickListener(this);
        cancelImageView.setOnClickListener(this);
        retryImageView.setOnClickListener(this);
        doneImageView.setOnClickListener(this);

        setUpLayoutChangeAnimation();
        captureImageView.post(new Runnable() {
            @Override
            public void run() {
                captureImageView.setVisibility(View.VISIBLE);
            }
        });

        SystemUiHelper systemUiHelper = new SystemUiHelper(getActivity(), SystemUiHelper.LEVEL_HIDE_STATUS_BAR, 0);
        systemUiHelper.hide();
    }

    private void setUpLayoutChangeAnimation() {
        LayoutTransition transition = new LayoutTransition();
        long animationDuration = 200;
        long staggerDuration = 100;
        Interpolator interpolator = new AccelerateDecelerateInterpolator();

        transition.setStagger(LayoutTransition.APPEARING, staggerDuration);
        transition.setStagger(LayoutTransition.DISAPPEARING, staggerDuration);
        transition.setStartDelay(LayoutTransition.APPEARING, animationDuration);
        transition.setStartDelay(LayoutTransition.DISAPPEARING, 0);
        transition.setDuration(LayoutTransition.DISAPPEARING, animationDuration);
        transition.setDuration(LayoutTransition.APPEARING, animationDuration);
        transition.setInterpolator(LayoutTransition.DISAPPEARING, interpolator);
        transition.setInterpolator(LayoutTransition.APPEARING, interpolator);

        int translation = getResources().getDimensionPixelOffset(R.dimen.size_float_action_button) + getResources().getDimensionPixelOffset(R.dimen.margin_float_action_button);
        ObjectAnimator slideIn = ObjectAnimator.ofFloat(null, "translationY", translation, 0).setDuration(animationDuration);
        ObjectAnimator slideOut = ObjectAnimator.ofFloat(null, "translationY", 0, translation).setDuration(animationDuration);
        transition.setAnimator(LayoutTransition.APPEARING, slideIn);
        transition.setAnimator(LayoutTransition.DISAPPEARING, slideOut);

        rootView.setLayoutTransition(transition);
    }

    @Override
    public void onDestroyView() {
        releaseCamera();
        super.onDestroyView();
    }

//    @Override
//    public void onPause() {
//        releaseCamera();
//        super.onPause();
//    }

    private void releaseCamera() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

    private boolean checkCameraHardware(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }

    public static Camera getCameraInstance() {
        Camera camera = null;
        try {
            camera = Camera.open();
        } catch (Exception e) {
            Log.d(TAG, "Camera.open() Fail");
        }
        return camera;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_capture:
                capture();
                break;
            case R.id.iv_camera_cancel:
                cancel();
                break;
            case R.id.iv_camera_retry:
                retry();
                break;
            case R.id.iv_camera_done:
                done();
                break;
//            case R.id.iv_go2gallery:
//                ((SrcImgObtainActivity) getActivity()).go2gallery();
//                break;
        }
    }

    private void cancel() {
        getActivity().finish();
    }

    private void retry() {
        camera.startPreview();
        setFloatActionButtonVisiblity(Mode.CAPTURE);
    }

    private void setFloatActionButtonVisiblity(Mode mode) {
        switch (mode){
            case CAPTURE:
                cancelImageView.setVisibility(View.INVISIBLE);
                retryImageView.setVisibility(View.INVISIBLE);
                doneImageView.setVisibility(View.INVISIBLE);
                captureImageView.setVisibility(View.VISIBLE);
                break;
            case CHOOSE:

                captureImageView.setVisibility(View.INVISIBLE);
                cancelImageView.setVisibility(View.VISIBLE);
                retryImageView.setVisibility(View.VISIBLE);
                doneImageView.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void setFloatActionButtonVisiblity(int invisible, int visible) {
        cancelImageView.setVisibility(invisible);
        retryImageView.setVisibility(invisible);
        doneImageView.setVisibility(invisible);
        captureImageView.setVisibility(visible);
    }

    private void done(){
        File pictureFile = BitmapStorage.getOutputMediaFile(getActivity().getApplicationContext(), BitmapStorage.MEDIA_TYPE_IMAGE);
        if (pictureFile == null) {
            SuperToastUtil.showToast(getActivity(), "拍照失败");
            return;
        }

        rotateAndSavePicInBg(picData, pictureFile);

        srcImgPath = pictureFile.getAbsolutePath();
        startSimilarImgActivity(srcImgPath);
    }

    private void capture() {
//        camera.takePicture(new Camera.ShutterCallback() {
//            @Override
//            public void onShutter() {
//                //TODO:make a sound, tell user capture success
//            }
//        }, null, pictureCallback);
        camera.autoFocus(new Camera.AutoFocusCallback() {

            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                CameraFragment.this.camera.takePicture(new Camera.ShutterCallback() {
                    @Override
                    public void onShutter() {
                        //TODO:make a sound, tell user capture success
                    }
                }, null, pictureCallback);
            }
        });
//        setFloatActionButtonVisiblity(Mode.CHOOSE);
    }
}
