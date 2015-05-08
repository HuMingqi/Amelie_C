package com.diandian.coolco.emilie.activity;

import android.animation.AnimatorSet;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.diandian.coolco.emilie.R;
import com.diandian.coolco.emilie.dialog.ProgressDialog;
import com.diandian.coolco.emilie.utility.ActionName;
import com.diandian.coolco.emilie.utility.BitmapStorage;
import com.diandian.coolco.emilie.utility.Dimension;
import com.diandian.coolco.emilie.utility.Event;
import com.diandian.coolco.emilie.utility.ExtraDataName;
import com.diandian.coolco.emilie.utility.OnSwipeTouchListener;
import com.diandian.coolco.emilie.utility.Preference;
import com.diandian.coolco.emilie.utility.PreferenceKey;
import com.diandian.coolco.emilie.widget.DetectSwipeGestureRelativeLayout;
import com.edmodo.cropper.CropImageView;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import roboguice.inject.InjectView;

public class SrcImgCropActivity extends BaseActivity implements View.OnClickListener {

//    @InjectView(R.id.crop_image_view)
    private CropImageView cropImageView;
//    @InjectView(R.id.iv_crop)
    private ImageView triggleCropImageView;
//    @InjectView(R.id.tv_skip_crop)
    private TextView skipCropTextView;
//    @InjectView(R.id.rl_bottom_bar)
    private RelativeLayout bottomBarRelativeLayout;
    private BroadcastReceiver broadcastReceiver;
    private String srcImgPath;
    private ProgressDialog progressDialog;
    private Handler handler;
    private float bottomBarHeight;

    private View rootView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_src_img_crop);

        init();
    }

    private void init() {
        cropImageView = (CropImageView) findViewById(R.id.crop_image_view);
        triggleCropImageView = (ImageView) findViewById(R.id.iv_crop);
        skipCropTextView = (TextView) findViewById(R.id.tv_skip_crop);
        bottomBarRelativeLayout = (RelativeLayout) findViewById(R.id.rl_bottom_bar);
        rootView = findViewById(R.id.root);


        bottomBarHeight = Dimension.dp2px(this, 50);

        Intent intent = getIntent();
        srcImgPath = intent.getStringExtra(ExtraDataName.SRC_IMG_PATH);

        //register broadcast
        broadcastReceiver = createBroadcastReceiver();
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, new IntentFilter(ActionName.SRC_IMG_STORAGE_COMPLETED));

        //check flag
        boolean isSrcImgStorageCompleted = Preference.getPrefBoolean(context, PreferenceKey.IS_SRC_IMG_STORAGE_COMPLETED);
        if (isSrcImgStorageCompleted){
            srcImgStorageCompleted();
        } else {
            progressDialog = ProgressDialog.show(this, "正在加载...");
//            progressDialog = ProgressDialog.show(this, null,  "正在加载...");
//            progressDialog = new ProgressDialog(SrcImgCropActivity.this);
//            progressDialog.setTitle(null);
//            progressDialog.setMessage("加载中...");
//            progressDialog.show();
        }

        triggleCropImageView.setImageDrawable(
                new IconDrawable(context, Iconify.IconValue.md_crop)
                        .colorRes(R.color.ab_icon)
                        .actionBarSize());
        triggleCropImageView.setOnClickListener(this);
        skipCropTextView.setOnClickListener(this);
//        bottomBarRelativeLayout.setVisibility(View.GONE);
        initLayoutAnimation();
        ((DetectSwipeGestureRelativeLayout) rootView).setListener(new DetectSwipeGestureRelativeLayout.SwipeRightListener() {
            @Override
            public void onSwipeRight() {
                finish();
            }
        });
//        cropImageView.setOnTouchListener(new OnSwipeTouchListener(SrcImgCropActivity.this){
//            @Override
//            public void onSwipeRight() {
//                finish();
//            }
//        });

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

        ObjectAnimator slideIn = ObjectAnimator.ofFloat(null, "translationY", bottomBarHeight, 0).setDuration(animationDuration);
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

    private BroadcastReceiver createBroadcastReceiver(){
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String actionName = intent.getAction();
                if (actionName.equals(ActionName.SRC_IMG_STORAGE_COMPLETED)){
                    srcImgStorageCompleted();
                }
            }
        };
    }

    private void srcImgStorageCompleted() {
        try {
            cropImageView.setImageBitmap(getThumbnail(Uri.fromFile(new File(srcImgPath))));
        } catch (IOException e) {
            e.printStackTrace();
        }
//        String uri = String.format("file://%s", srcImgPath);
//        ImageLoader.getInstance().displayImage(uri, (com.nostra13.universalimageloader.core.imageaware.ImageAware) cropImageView);

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        //reset flag
        Preference.setPrefBoolean(context, PreferenceKey.IS_SRC_IMG_STORAGE_COMPLETED, false);

        //unregister broadcast receiver
        unregisterReceiver();
    }

    public Bitmap getThumbnail(Uri uri) throws FileNotFoundException, IOException {
        InputStream input = getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
//        onlyBoundsOptions.inDither=true;//optional
//        onlyBoundsOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
            return null;

//        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;
//        double ratio = (originalSize > THUMBNAIL_SIZE) ? (originalSize / THUMBNAIL_SIZE) : 1.0;
        int width = Dimension.getScreenWidth(this);
        int height = Dimension.getScreenHeight(this);
        double ratio = 1;
        if (onlyBoundsOptions.outWidth < width && onlyBoundsOptions.outHeight < height){
            ratio = 1;
        } else {
            ratio = Math.min(((float) width)/ ((float) onlyBoundsOptions.outWidth), ((float) height)/ ((float) onlyBoundsOptions.outHeight));
        }

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inDither=true;//optional
        bitmapOptions.inPreferredConfig=Bitmap.Config.ARGB_8888;//optional
        input = getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();
        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio){
        int k = Integer.highestOneBit((int)Math.floor(ratio));
        if(k==0) return 1;
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

//        if (id == android.R.id.home) {
////            NavUtils.navigateUpFromSameTask(this);
//            finish();
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
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

    @Override
    protected void onPause() {
        unregisterReceiver();
        super.onPause();
    }

    /**
     * unregister broadcast receiver
     */
    private void unregisterReceiver() {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver);
    }
}
