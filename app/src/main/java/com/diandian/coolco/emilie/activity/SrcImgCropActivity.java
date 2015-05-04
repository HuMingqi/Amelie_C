package com.diandian.coolco.emilie.activity;

import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.diandian.coolco.emilie.utility.ExtraDataName;
import com.diandian.coolco.emilie.utility.Preference;
import com.diandian.coolco.emilie.utility.PreferenceKey;
import com.edmodo.cropper.CropImageView;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;

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
    private BroadcastReceiver broadcastReceiver;
    private String srcImgPath;
    private ProgressDialog progressDialog;
    private Handler handler;
    private float bottomBarHeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_src_img_crop);

        init();
    }

    private void init() {
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
        bottomBarRelativeLayout.setVisibility(View.GONE);
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
        transition.setStagger(LayoutTransition.CHANGE_APPEARING, 0);
        transition.setStagger(LayoutTransition.CHANGE_DISAPPEARING, 0);
        transition.setStartDelay(LayoutTransition.CHANGE_APPEARING, 0);
        transition.setStartDelay(LayoutTransition.CHANGE_DISAPPEARING, 0);
        transition.setDuration(LayoutTransition.DISAPPEARING, 0);
        transition.setDuration(LayoutTransition.APPEARING, 200);

        ObjectAnimator animIn = ObjectAnimator.ofFloat(null, "translationY", bottomBarHeight, 0).setDuration(
                transition.getDuration(LayoutTransition.APPEARING));
//        animIn.setInterpolator(new AccelerateDecelerateInterpolator());
        transition.setAnimator(LayoutTransition.APPEARING, animIn);
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
        cropImageView.setImageBitmap(BitmapFactory.decodeFile(srcImgPath));

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        //reset flag
        Preference.setPrefBoolean(context, PreferenceKey.IS_SRC_IMG_STORAGE_COMPLETED, false);

        //unregister broadcast receiver
        unregisterReceiver();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_src_img_crop, menu);
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

        if (id == android.R.id.home) {
//            NavUtils.navigateUpFromSameTask(this);
            finish();
            return true;
        }

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
