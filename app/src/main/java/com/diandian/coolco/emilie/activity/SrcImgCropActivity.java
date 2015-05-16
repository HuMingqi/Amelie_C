package com.diandian.coolco.emilie.activity;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.diandian.coolco.emilie.R;
import com.diandian.coolco.emilie.dialog.ProgressDialog;
import com.diandian.coolco.emilie.utility.BitmapStorage;
import com.diandian.coolco.emilie.utility.Dimension;
import com.diandian.coolco.emilie.utility.Event;
import com.diandian.coolco.emilie.utility.ExtraDataName;
import com.diandian.coolco.emilie.utility.Preference;
import com.diandian.coolco.emilie.utility.PreferenceKey;
import com.edmodo.cropper.CropImageView;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import roboguice.inject.InjectView;

public class SrcImgCropActivity extends BaseActivity implements View.OnClickListener {

    @InjectView(R.id.crop_image_view)
    private CropImageView cropImageView;
    @InjectView(R.id.iv_crop)
    private ImageView triggleCropImageView;
    @InjectView(R.id.tv_skip_crop)
    private TextView skipCropTextView;
    @InjectView(R.id.rl_bottom_bar)
    private RelativeLayout bottomBarRelativeLayout;

    private String srcImgPath;

    private Handler handler;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_src_img_crop);
        init();
    }

    private void init() {
        Intent intent = getIntent();
        srcImgPath = intent.getStringExtra(ExtraDataName.SRC_IMG_PATH);

        //check flag
        boolean isSrcImgStorageCompleted = Preference.getPrefBoolean(context, PreferenceKey.IS_SRC_IMG_STORAGE_COMPLETED);
        if (isSrcImgStorageCompleted) {
            srcImgStorageCompleted();
        } else {
            progressDialog = ProgressDialog.show(this, "正在加载...");
        }

        triggleCropImageView.setImageDrawable(
                new IconDrawable(context, Iconify.IconValue.md_crop)
                        .colorRes(R.color.ab_icon)
                        .actionBarSize());
        triggleCropImageView.setOnClickListener(this);
        skipCropTextView.setOnClickListener(this);
        initLayoutAnimation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler = new Handler(new Handler.Callback() {

            @Override
            public boolean handleMessage(Message message) {
                switch (message.what) {
                    case 0:
                        bottomBarRelativeLayout.setVisibility(View.VISIBLE);
                        break;

                    default:
                        break;
                }
                return false;
            }
        });
        handler.sendEmptyMessageDelayed(0, 100);
    }

    public void initLayoutAnimation() {
        LayoutTransition optionsTransition = new LayoutTransition();
        setupAnimations(optionsTransition);
        ((ViewGroup) findViewById(R.id.root)).setLayoutTransition(optionsTransition);
    }

    private void setupAnimations(LayoutTransition transition) {
        long animationDuration = 200;

        transition.setStagger(LayoutTransition.CHANGE_APPEARING, 0);
        transition.setStagger(LayoutTransition.CHANGE_DISAPPEARING, 0);
        transition.setStartDelay(LayoutTransition.CHANGE_APPEARING, 0);
        transition.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 0);
        transition.setDuration(LayoutTransition.DISAPPEARING, 0);
        transition.setDuration(LayoutTransition.APPEARING, animationDuration);

        ObjectAnimator slideIn = ObjectAnimator.ofFloat(null, "translationY", Dimension.dp2px(this, 50), 0).setDuration(animationDuration);
//        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(null, "alpha", 0.2f, 1).setDuration(transition.getDuration(LayoutTransition.APPEARING));
//        AnimatorSet animInSet = new AnimatorSet();
//        animInSet.playTogether(slideIn, fadeIn);
//        animInSet.setDuration(transition.getDuration(LayoutTransition.APPEARING));
//        animIn.setInterpolator(new AccelerateDecelerateInterpolator());
        transition.setAnimator(LayoutTransition.APPEARING, slideIn);

//        AnimatorSet animAppear = new AnimatorSet();
//        animAppear.setDuration(animationDuration).playTogether(
//                ObjectAnimator.ofFloat(bottomBarRelativeLayout, "alpha", 0.2f, 1),
//                ObjectAnimator.ofFloat(bottomBarRelativeLayout, "translationY", bottomBarHeight, 0));
//        transition.setAnimator(LayoutTransition.APPEARING, animAppear);
    }

    public void onEventMainThread(Event.SrcImgSavedEvent event) {
        srcImgStorageCompleted();
    }

    private void srcImgStorageCompleted() {
//        try {
//            Uri imageUri = Uri.parse(String.format("file://%s", srcImgPath));
//            Uri imageUri = Uri.fromFile(new File(srcImgPath));
        cropImageView.setImageBitmap(getThumbnail(srcImgPath));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        String uri = String.format("file://%s", srcImgPath);
//        ImageLoader.getInstance().displayImage(uri, (com.nostra13.universalimageloader.core.imageaware.ImageAware) cropImageView);

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        //reset flag
        Preference.setPrefBoolean(context, PreferenceKey.IS_SRC_IMG_STORAGE_COMPLETED, false);
    }

    public Bitmap getThumbnail(String srcImgPath) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(srcImgPath, options);
        int width = Dimension.getScreenWidth(this);
        int height = Dimension.getScreenHeight(this) - ((int) Dimension.dp2px(this, 100)) - Dimension.getStatusBarHeight(this);
        double ratio = 1;
        if (options.outWidth < width && options.outHeight < height) {
            ratio = 1;
        } else {
//            ratio = Math.min(((float) width)/ ((float) options.outWidth), ((float) height)/ ((float) options.outHeight));
//            ratio = Math.min((float) options.outWidth / width, (float) options.outHeight / height);
            ratio = Math.max((float) options.outWidth / width, (float) options.outHeight / height);
        }
        options.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        options.inJustDecodeBounds = false;
        Bitmap src = BitmapFactory.decodeFile(srcImgPath, options);

        if (ratio == 1){
            return src;
        }
        // create scaled bitmap
        Bitmap dst = null;
        if ((float) options.outWidth / width > (float) options.outHeight / height){
            dst = Bitmap.createScaledBitmap(src, width, ((int) (((float) width) * options.outHeight / options.outWidth)), false);
        } else {
            dst = Bitmap.createScaledBitmap(src, ((int) (((float) height) * options.outWidth / options.outHeight)), height, false);
        }
        if (src != dst) {
            src.recycle();
        }
        return dst;

    }

    public Bitmap getThumbnail(Uri uri) throws FileNotFoundException, IOException {
        InputStream input = getContentResolver().openInputStream(uri);

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(input, null, options);
//        input.close();
        if ((options.outWidth == -1) || (options.outHeight == -1))
            return null;

        int width = cropImageView.getMeasuredWidth();
        int height = cropImageView.getMeasuredHeight();
        double ratio = 1;
        if (options.outWidth < width && options.outHeight < height) {
            ratio = 1;
        } else {
            ratio = Math.min(((float) width) / ((float) options.outWidth), ((float) height) / ((float) options.outHeight));
        }

//        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        options.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        options.inJustDecodeBounds = false;
//        bitmapOptions.inDither=true;//optional
//        bitmapOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
//        input = getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, options);
        input.close();
        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio) {
        int k = Integer.highestOneBit((int) Math.floor(ratio));
        if (k == 0) return 1;
        else return k;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_src_img_crop, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_crop:
                cropImg();
                break;
            case R.id.tv_skip_crop:
                startSimilarImgActivity(srcImgPath);
                break;
        }
    }

    private void cropImg() {
        String imgName = System.currentTimeMillis() + ".jpg";
        Bitmap bm = cropImageView.getCroppedImage();
        String imgPath = BitmapStorage.storeImg(context, bm, imgName);
        startSimilarImgActivity(imgPath);
    }

    private void startSimilarImgActivity(String imgPath) {
        Intent intent = new Intent(this, SimilarImgActivity.class);
        intent.putExtra(ExtraDataName.CROPPED_SRC_IMG_PATH, imgPath);
        startActivity(intent);
    }

}
