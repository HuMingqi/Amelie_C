package com.diandian.coolco.emilie.activity;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.diandian.coolco.emilie.R;
import com.diandian.coolco.emilie.utility.ActionName;
import com.diandian.coolco.emilie.utility.BitmapStorage;
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
    private BroadcastReceiver broadcastReceiver;
    private String srcImgPath;
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

        //register broadcast
        broadcastReceiver = createBroadcastReceiver();
        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastReceiver, new IntentFilter(ActionName.SRC_IMG_STORAGE_COMPLETED));

        //check flag
        boolean isSrcImgStorageCompleted = Preference.getPrefBoolean(context, PreferenceKey.IS_SRC_IMG_STORAGE_COMPLETED);
        if (isSrcImgStorageCompleted){
            srcImgStorageCompleted();
        } else {
//            progressDialog = ProgressDialog.show(this, "正在加载...");
            progressDialog = ProgressDialog.show(this, null,  "正在加载...");
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_crop:
                cropImg();
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
