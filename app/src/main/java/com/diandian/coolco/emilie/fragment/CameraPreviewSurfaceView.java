package com.diandian.coolco.emilie.fragment;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.diandian.coolco.emilie.utility.Logcat;

import java.util.List;

@SuppressWarnings("ALL")
public class CameraPreviewSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private static final String TAG = "CameraPreview";

    private Context context;
    private SurfaceHolder holder;
    private Camera camera;
    private Camera.Size optimalPreviewSize;
    private Camera.Size optimalPictureSize;

    public CameraPreviewSurfaceView(Context context, Camera camera) {
        super(context);
        this.context = context;
        this.camera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        holder = getHolder();
        holder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // empty. surfaceChanged will take care of stuff
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // empty. Take care of releasing the Camera preview in your activity.
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        Logcat.e(TAG, "surfaceChanged => w=" + w + ", h=" + h);
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
        if (this.holder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            camera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }

        // set preview size and make any resize, rotate or reformatting changes here
        // start preview with new settings
        try {
            Camera.Parameters parameters = camera.getParameters();
            parameters.setPreviewSize(optimalPreviewSize.width, optimalPreviewSize.height);

            parameters.setPictureSize(optimalPictureSize.width, optimalPictureSize.height);

            //STEP #1: Get rotation degrees
//            Camera.CameraInfo info = new Camera.CameraInfo();
//            Camera.getCameraInfo(Camera.CameraInfo.CAMERA_FACING_BACK, info);
//            int rotation = ((Activity) context).getWindowManager().getDefaultDisplay().getRotation();
//            int degrees = 0;
//            switch (rotation) {
//                case Surface.ROTATION_0: degrees = 0; break; //Natural orientation
//                case Surface.ROTATION_90: degrees = 90; break; //Landscape left
//                case Surface.ROTATION_180: degrees = 180; break;//Upside down
//                case Surface.ROTATION_270: degrees = 270; break;//Landscape right
//            }
//            int rotate = (info.orientation - degrees + 360) % 360;

            //STEP #2: Set the 'rotation' parameter
//            parameters.setRotation(rotate);

            camera.setParameters(parameters);
            camera.setDisplayOrientation(90);
            camera.setPreviewDisplay(this.holder);
            camera.startPreview();

        } catch (Exception e){
            Logcat.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);

        List<Camera.Size> supportedPreviewSizes = camera.getParameters().getSupportedPreviewSizes();
        if (supportedPreviewSizes != null) {
            optimalPreviewSize = getOptimalSize(supportedPreviewSizes, width, height);
        }

        List<Camera.Size> supportedPictureSizes = camera.getParameters().getSupportedPictureSizes();
        if (supportedPictureSizes != null){
            optimalPictureSize = getOptimalSize(supportedPictureSizes, optimalPreviewSize.width, optimalPreviewSize.height);
        }

        float ratio;
        if(optimalPreviewSize.height >= optimalPreviewSize.width)
            ratio = (float) optimalPreviewSize.height / (float) optimalPreviewSize.width;
        else
            ratio = (float) optimalPreviewSize.width / (float) optimalPreviewSize.height;

        // One of these methods should be used, second method squishes preview slightly
        setMeasuredDimension(width, (int) (width * ratio));
//        setMeasuredDimension((int) (width * ratio), height);
    }

    private Camera.Size getOptimalSize(List<Camera.Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) h / w;

        if (sizes == null)
            return null;

        Camera.Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        for (Camera.Size size : sizes) {
            double ratio = (double) size.height / size.width;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE)
                continue;

            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Camera.Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }

        return optimalSize;
    }
}