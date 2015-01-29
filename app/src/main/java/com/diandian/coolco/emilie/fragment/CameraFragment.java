package com.diandian.coolco.emilie.fragment;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.diandian.coolco.emilie.R;
import com.diandian.coolco.emilie.utility.ActionName;
import com.diandian.coolco.emilie.utility.BitmapStorage;
import com.diandian.coolco.emilie.utility.Logcat;
import com.diandian.coolco.emilie.utility.Preference;
import com.diandian.coolco.emilie.utility.PreferenceKey;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;

import java.io.IOException;
import java.util.List;

import roboguice.inject.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
public class CameraFragment extends BaseFragment implements View.OnClickListener {

    private final static String TAG = CameraFragment.class.getSimpleName();

    @InjectView(R.id.sv_camera)
    private SurfaceView surfaceView;
    @InjectView(R.id.iv_capture)
    private ImageView captureImageView;

    private Camera camera;
    private SurfaceHolder surfaceHolder;

    private SurfaceHolder.Callback surfaceHolderCallback = new SurfaceHolder.Callback() {

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            try {
                camera.setPreviewDisplay(holder);
                camera.startPreview();
            } catch (IOException e) {
                Logcat.e(TAG, "surfaceCreated Fail");
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            if (surfaceHolder.getSurface() == null) {
                return;
            }

            try {
                camera.stopPreview();
            } catch (Exception e) {
                Logcat.e(TAG, "camera.stopPreview Fail");
            }

            try {
                camera.setPreviewDisplay(surfaceHolder);
                camera.startPreview();
            } catch (Exception e) {
                Logcat.e(TAG, "camera.startPreview Fail");
            }
        }
    };

    private Camera.PictureCallback pictureCallback = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            CameraFragment.this.camera.stopPreview();

            String imgName = System.currentTimeMillis() + ".jpg";
            String imgPath = BitmapStorage.getImgDir(context) + "/" +imgName;

            rotateStoreSrcImgInBg(data, imgName);
//            rotateStoreSrcImgInBg();

            imgObtained(imgPath);

//            Logcat.e(TAG, System.currentTimeMillis()+"");

            //rotate the pic

//            try {
//                FileOutputStream fos = getActivity().openFileOutput(imgName, Context.MODE_PRIVATE);
//                BufferedOutputStream bos = new BufferedOutputStream(fos);
//                bm.compress(Bitmap.CompressFormat.JPEG, 10, bos);
//                bos.flush();
//                bos.close();
//            } catch (Exception e) {
//                Logcat.e(TAG, "Save Picture Fail");
//            }
//            Logcat.e(TAG, System.currentTimeMillis()+"");
//            Logcat.e(TAG, System.currentTimeMillis()+"");
//            Logcat.e(TAG, imgPath);
        }
    };

    private void rotateStoreSrcImgInBg(final byte[] data, final String imgName) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, false);

                BitmapStorage.storeImg(context, bm, imgName);

                srcImgStorageCompleted();
            }
        }).start();

    }
    private void rotateStoreSrcImgInBg() {
        new Thread(new Runnable() {
            @Override
            public void run() {
//                Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
//                Matrix matrix = new Matrix();
//                matrix.postRotate(90);
//                bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, false);
//
//                BitmapStorage.storeImg(context, bm, imgName);
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                srcImgStorageCompleted();
            }
        }).start();

    }

    private void srcImgStorageCompleted() {
        //set flag
        Preference.setPrefBoolean(context, PreferenceKey.IS_SRC_IMG_STORAGE_COMPLETED, true);

        //send broadcast
        Intent intent = new Intent(ActionName.SRC_IMG_STORAGE_COMPLETED);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
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
        if (!checkCameraHardware(context)) {
            Logcat.e(TAG, "No Camera");
            return;
        }

        camera = getCameraInstance();

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(surfaceHolderCallback);

        Camera.Parameters parameters = camera.getParameters();
        parameters.setPictureFormat(ImageFormat.JPEG);
        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        List<Camera.Size> supportedPictureSizes = parameters.getSupportedPictureSizes();
        int preViewPicSizeIndex = supportedPictureSizes.size() - 1;
//        for (Camera.Size supportedPreviewSize : supportedPreviewSizes) {
//
//        }
//        int previewWidth, previewHeight;
        parameters.setPreviewSize(supportedPreviewSizes.get(preViewPicSizeIndex).width, supportedPreviewSizes.get(preViewPicSizeIndex).height);
        parameters.setPictureSize(supportedPictureSizes.get(preViewPicSizeIndex).width, supportedPictureSizes.get(preViewPicSizeIndex).height);
        camera.setParameters(parameters);
        camera.setDisplayOrientation(90);

        captureImageView.setImageDrawable(new IconDrawable(context, Iconify.IconValue.md_camera)
                .colorRes(R.color.ab_icon)
                .actionBarSize());
        captureImageView.setOnClickListener(this);

//        captureImageView.setOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                camera.autoFocus(new Camera.AutoFocusCallback() {
//
//                    @Override
//                    public void onAutoFocus(boolean success, Camera camera) {
//                        CameraFragment.this.camera.takePicture(null, null, pictureCallback);
//                    }
//                });
//            }
//        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }
//
//    @Override
//    protected void onDestroy() {
//        if (camera != null) {
//            camera.stopPreview();
//            camera.release();
//            camera = null;
//        }
//        super.onDestroy();
//    }
//
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
        switch (v.getId()){
            case R.id.iv_capture:
                capture();
                break;
        }
    }

    private void capture() {
        camera.autoFocus(new Camera.AutoFocusCallback() {

            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                CameraFragment.this.camera.takePicture(null, null, pictureCallback);
            }
        });
    }
}
