package com.diandian.coolco.emilie.activity;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.diandian.coolco.emilie.R;
import com.diandian.coolco.emilie.dialog.ProgressDialog;
import com.diandian.coolco.emilie.utility.BitmapStorage;
import com.diandian.coolco.emilie.utility.Dimension;
import com.diandian.coolco.emilie.utility.Event;
import com.diandian.coolco.emilie.utility.ExtraDataName;
import com.diandian.coolco.emilie.utility.Preference;
import com.diandian.coolco.emilie.utility.PreferenceKey;
import com.diandian.coolco.emilie.utility.SystemUiHelper;
import com.diandian.coolco.emilie.utility.SystemUiUtil;
import com.edmodo.cropper.CropImageView;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;

import roboguice.inject.InjectView;

public class SrcImgCropActivity extends BaseActivity implements View.OnClickListener {

    @InjectView(R.id.crop_image_view)
    private CropImageView cropImageView;
    @InjectView(R.id.iv_crop)
    private ImageView triggleCropImageView;
    //    @InjectView(R.id.tv_skip_crop)
//    private TextView skipCropTextView;
//    @InjectView(R.id.rl_bottom_bar)
//    private RelativeLayout bottomBarRelativeLayout;
    @InjectView(R.id.iv_crop_cancel)
    private ImageView cancelImageView;
    @InjectView(R.id.iv_crop_done)
    private ImageView doneImageView;
    @InjectView(R.id.iv_crop_retry)
    private ImageView retryImageView;

    @InjectView(R.id.iv_crop_tmp_result)
    private ImageView cropTmpImageView;

    private String srcImgPath;

    //    private Handler handler;
    private Dialog progressDialog;
    private Bitmap tmpBmp;
    private long animationDuration;
    private LayoutTransition optionsTransition;
    private ViewGroup rootView;
    private int kind=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_src_img_crop);
//        initActionBar();
        init();
    }

    private void init() {
        kind=0;
        RadioGroup group=(RadioGroup)findViewById(R.id.kinds);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup grp, int checkedId) {
                RadioButton checkR=(RadioButton) grp.findViewById(checkedId);
                String ckind=(String)checkR.getText();
                switch(ckind){
                    case "上衣":
                        kind=0;
                        break;
                    case "下衣":
                        kind=1;
                        break;
                    case "连衣裙":
                        kind=2;
                        break;
                }
            }
        }
        );

        getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.bg_activity)));
        Intent intent = getIntent();
        srcImgPath = intent.getStringExtra(ExtraDataName.SRC_IMG_PATH);

        //check flag
        boolean isSrcImgStorageCompleted = Preference.getPrefBoolean(context, PreferenceKey.IS_SRC_IMG_STORAGE_COMPLETED);
        if (isSrcImgStorageCompleted) {
            srcImgStorageCompleted();
        } else {
            progressDialog = ProgressDialog.show(this, "正在加载...");
        }

        int iconSize = getResources().getDimensionPixelOffset(R.dimen.size_float_action_button) / 2;
        triggleCropImageView.setImageDrawable(new IconDrawable(context, Iconify.IconValue.md_crop)
                .colorRes(R.color.ab_icon).actionBarSize());
//                .sizePx(iconSize));
        cancelImageView.setImageDrawable(new IconDrawable(getApplicationContext(), Iconify.IconValue.md_close)
                .colorRes(R.color.ab_icon).actionBarSize());
//                .sizePx(iconSize));
        retryImageView.setImageDrawable(new IconDrawable(getApplicationContext(), Iconify.IconValue.md_refresh)
                .colorRes(R.color.ab_icon).actionBarSize());
//                .sizePx(iconSize));
        doneImageView.setImageDrawable(new IconDrawable(getApplicationContext(), Iconify.IconValue.md_done)
                .colorRes(R.color.ab_icon).actionBarSize());
//                .sizePx(iconSize));

        triggleCropImageView.setOnClickListener(this);
        cancelImageView.setOnClickListener(this);
        retryImageView.setOnClickListener(this);
        doneImageView.setOnClickListener(this);

//        skipCropTextView.setOnClickListener(this);
        initLayoutAnimation();
        triggleCropImageView.post(new Runnable() {
            @Override
            public void run() {
                triggleCropImageView.setVisibility(View.VISIBLE);
            }
        });

        SystemUiUtil.hideSystemUi(this);
    }


    /*

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

    */
    public void initLayoutAnimation() {
        optionsTransition = new LayoutTransition();
        setupAnimations(optionsTransition);
        rootView = (ViewGroup) findViewById(R.id.root);
//        enableLayoutChangeAnimation();
    }

    private void enableLayoutChangeAnimation() {
        rootView.setLayoutTransition(optionsTransition);
    }

    private void disableLayoutChangeAnimation() {
        rootView.setLayoutTransition(null);
    }

    private void setupAnimations(LayoutTransition transition) {
        animationDuration = 200;
        Interpolator interpolator = new AccelerateDecelerateInterpolator();

        transition.setStagger(LayoutTransition.CHANGE_APPEARING, 0);
        transition.setStagger(LayoutTransition.CHANGE_DISAPPEARING, 0);
        transition.setStartDelay(LayoutTransition.CHANGE_APPEARING, 0);
        transition.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 0);
        transition.setDuration(LayoutTransition.DISAPPEARING, animationDuration);
        transition.setDuration(LayoutTransition.APPEARING, animationDuration);
        transition.setInterpolator(LayoutTransition.DISAPPEARING, interpolator);
        transition.setInterpolator(LayoutTransition.APPEARING, interpolator);

        int translation = getResources().getDimensionPixelOffset(R.dimen.size_float_action_button) + getResources().getDimensionPixelOffset(R.dimen.margin_float_action_button);

        ObjectAnimator slideIn = ObjectAnimator.ofFloat(null, "translationY", translation, 0).setDuration(animationDuration);
        transition.setAnimator(LayoutTransition.APPEARING, slideIn);
        ObjectAnimator slideOut = ObjectAnimator.ofFloat(null, "translationY", 0, translation).setDuration(animationDuration);
        transition.setAnimator(LayoutTransition.DISAPPEARING, slideOut);
//        transition.setAnimator(LayoutTransition.CHANGE_DISAPPEARING, slideOut);
    }

    public void onEventMainThread(Event.SrcImgSavedEvent event) {
        srcImgStorageCompleted();
    }

    public void onEventMainThread(Event.CropDownEvent event) {
        triggleCropImageView.setVisibility(View.INVISIBLE);
    }

    public void onEventMainThread(Event.CropUpEvent event) {
        triggleCropImageView.setVisibility(View.VISIBLE);
    }

    private void srcImgStorageCompleted() {
        cropImageView.setImageBitmap(getThumbnail(srcImgPath));

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
//        int height = Dimension.getScreenHeight(this) - ((int) Dimension.dp2px(this, 100)) - Dimension.getStatusBarHeight(this);
        int height = Dimension.getScreenHeight(this);
        double ratio = 1;
        if (options.outWidth < width && options.outHeight < height) {
            ratio = 1;
        } else {
            ratio = Math.max((float) options.outWidth / width, (float) options.outHeight / height);
        }
        options.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        options.inJustDecodeBounds = false;
        Bitmap sampledBitmap = BitmapFactory.decodeFile(srcImgPath, options);

        if (ratio == 1) {
            return sampledBitmap;
        }

        Bitmap scaledBitmap = null;
        if ((float) options.outWidth / width > (float) options.outHeight / height) {
            scaledBitmap = Bitmap.createScaledBitmap(sampledBitmap, width, ((int) (((float) width) * options.outHeight / options.outWidth)), false);
        } else {
            scaledBitmap = Bitmap.createScaledBitmap(sampledBitmap, ((int) (((float) height) * options.outWidth / options.outHeight)), height, false);
        }
        if (sampledBitmap != scaledBitmap) {
            sampledBitmap.recycle();
        }
        return scaledBitmap;

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
                crop();
                break;
            case R.id.iv_crop_cancel:
                cancel();
                break;
            case R.id.iv_crop_done:
                done();
                break;
            case R.id.iv_crop_retry:
                retry();
                break;

//            case R.id.tv_skip_crop:
//                startSimilarImgActivity(srcImgPath);
//                break;
        }
    }

    private void crop() {
        tmpBmp = cropImageView.getCroppedImage();

        disableLayoutChangeAnimation();
        cropImageView.setVisibility(View.INVISIBLE);
        cropTmpImageView.setImageBitmap(tmpBmp);
        cropTmpImageView.setVisibility(View.VISIBLE);

        enableLayoutChangeAnimation();
        triggleCropImageView.setVisibility(View.INVISIBLE);
        cancelImageView.postDelayed(new Runnable() {
            @Override
            public void run() {
                cancelImageView.setVisibility(View.VISIBLE);
                doneImageView.setVisibility(View.VISIBLE);
                retryImageView.setVisibility(View.VISIBLE);
            }
        }, animationDuration);

    }

    private void retry() {
        disableLayoutChangeAnimation();
        cropTmpImageView.setVisibility(View.INVISIBLE);
        cropImageView.setVisibility(View.VISIBLE);

        enableLayoutChangeAnimation();
        cancelImageView.setVisibility(View.INVISIBLE);
        doneImageView.setVisibility(View.INVISIBLE);
        retryImageView.setVisibility(View.INVISIBLE);
        triggleCropImageView.postDelayed(new Runnable() {
            @Override
            public void run() {
                triggleCropImageView.setVisibility(View.VISIBLE);
            }
        }, animationDuration);
    }

    private void cancel() {
        finish();
    }


    private void done() {
        String imgName = System.currentTimeMillis() + ".jpg";
        String imgPath = BitmapStorage.storeImg(context, tmpBmp, imgName);
        Preference.setPrefBoolean(getApplicationContext(), PreferenceKey.IS_SRC_IMG_STORAGE_COMPLETED, true);
        startSimilarImgActivity(imgPath);
    }

    private void startSimilarImgActivity(String imgPath) {
        Intent intent = new Intent(this, SimilarImgActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(ExtraDataName.CROPPED_SRC_IMG_PATH, imgPath);
        startActivity(intent);
        finish();
    }

}
