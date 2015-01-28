package com.diandian.coolco.emilie.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.diandian.coolco.emilie.R;
import com.diandian.coolco.emilie.utility.BitmapStorage;
import com.diandian.coolco.emilie.utility.ExtraDataName;
import com.edmodo.cropper.CropImageView;

import roboguice.inject.InjectView;

public class SrcImgCropActivity extends BaseActivity implements View.OnClickListener {

    @InjectView(R.id.crop_image_view)
    private CropImageView cropImageView;
    @InjectView(R.id.tv_crop)
    private TextView cropTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_src_img_crop);

        init();
    }

    private void init() {
        Intent intent = getIntent();
        String srcImgPath = intent.getStringExtra(ExtraDataName.SRC_IMG_PATH);
        cropImageView.setImageBitmap(BitmapFactory.decodeFile(srcImgPath));

        cropTextView.setOnClickListener(this);
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
            case R.id.tv_crop:
                cropImg();
                break;
        }
    }

    private void cropImg() {
        String imgName = System.currentTimeMillis() + ".jpg";
        Bitmap bm = cropImageView.getCroppedImage();
        String imgPath = BitmapStorage.saveImg(context, bm, imgName);
        startSimilarImgActivity(imgPath);
    }

    private void startSimilarImgActivity(String imgPath) {
        Intent intent = new Intent(this, SimilarImgActivity.class);
        intent.putExtra(ExtraDataName.CROPPED_SRC_IMG_PATH, imgPath);
        startActivity(intent);
    }
}
