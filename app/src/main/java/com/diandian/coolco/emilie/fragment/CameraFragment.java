package com.diandian.coolco.emilie.fragment;


import android.content.Context;
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
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.diandian.coolco.emilie.R;
import com.diandian.coolco.emilie.activity.SrcImgObtainActivity;
import com.diandian.coolco.emilie.utility.BitmapStorage;
import com.diandian.coolco.emilie.utility.Event;
import com.diandian.coolco.emilie.utility.MyApplication;
import com.diandian.coolco.emilie.utility.Preference;
import com.diandian.coolco.emilie.utility.PreferenceKey;
import com.diandian.coolco.emilie.utility.SuperToastUtil;
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

    @InjectView(R.id.fl_camera)
    private FrameLayout cameraFrameLayout;
    @InjectView(R.id.iv_capture)
    private ImageView captureImageView;
    @InjectView(R.id.iv_go2gallery)
    private ImageView go2galleryImageView;

    private Camera camera;

    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            camera.startPreview();

            File pictureFile = BitmapStorage.getOutputMediaFile(context, BitmapStorage.MEDIA_TYPE_IMAGE);
            if (pictureFile == null) {
                Log.d(TAG, "Error creating media file, check storage permissions: ");
                return;
            }

            rotateAndSavePicInBg(data, pictureFile);
//            saveBitmap(data, pictureFile);
//            srcImgStorageCompleted();
            imgObtained(pictureFile.getAbsolutePath());
        }
    };

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
        Preference.setPrefBoolean(context, PreferenceKey.IS_SRC_IMG_STORAGE_COMPLETED, true);

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
        captureImageView.setImageDrawable(new IconDrawable(context, Iconify.IconValue.md_camera)
                .colorRes(R.color.ab_icon)
                .actionBarSize());
        go2galleryImageView.setOnClickListener(this);

        if (!checkCameraHardware(context)) {
            SuperToastUtil.showToast(context, "摄像头不可用");
            return;
        }

        camera = getCameraInstance();
        if (camera == null) {
            SuperToastUtil.showToast(context, "打开摄像头失败，请重试");
            return;
        }
        SurfaceView cameraPreviewSurfaceView = new CameraPreviewSurfaceView(context, camera);
        cameraFrameLayout.addView(cameraPreviewSurfaceView, 0);

        captureImageView.setOnClickListener(this);
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
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
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
            case R.id.iv_go2gallery:
                ((SrcImgObtainActivity) getActivity()).go2gallery();
                break;
        }
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
    }
}
