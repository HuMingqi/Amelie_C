package com.diandian.coolco.emilie.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.diandian.coolco.emilie.R;
import com.diandian.coolco.emilie.dialog.ClothesInfoDialogFragment;
import com.diandian.coolco.emilie.model.Image;
import com.diandian.coolco.emilie.utility.DBHelper;
import com.diandian.coolco.emilie.utility.ExtraDataName;
import com.diandian.coolco.emilie.utility.IntentUtil;
import com.diandian.coolco.emilie.utility.SuperToastUtil;
import com.diandian.coolco.emilie.widget.DetectTapLongPressViewPager;
import com.diandian.coolco.emilie.widget.PullUpDownLinearLayout;
import com.diandian.coolco.emilie.widget.WebImageContainer;

import java.util.ArrayList;
import java.util.List;

import roboguice.inject.InjectView;

public class SimilarImgDetailActivity extends DbSupportBaseActivity implements PullUpDownLinearLayout.PullListener, ViewPager.OnPageChangeListener {

    @InjectView(R.id.vp_similar_img)
    private ViewPager viewPager;

    private ActionBar actionBar;

    private MenuItem collectionMenu;

    /**
     * record the collected changes, and persistent them in the end;
     */
    private List<Boolean> collected = new ArrayList<>();

    private ArrayList<Image> images;
    private PagerAdapter adapter;
    private List<Image> imagesNeedRemove;
    private List<Image> imagesNeedAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_similar_img_detail);

        init();
    }

    private void init() {
        initViewPager();
        initActionBar();
    }

    private void initViewPager() {
        Intent intent = getIntent();
        images = intent.getParcelableArrayListExtra(ExtraDataName.SIMILAR_IMGS);

        initCollected();

        int initPos = intent.getIntExtra(ExtraDataName.SIMILAR_IMG_INIT_POS, 0);
        adapter = new SimilarImgViewPagerAdapter();
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(initPos, true);
        viewPager.setOnPageChangeListener(this);
    }

    private void initCollected() {
        for (int i = 0; i < images.size(); i++) {
            collected.add(images.get(i).isCollected());
        }
    }

    /**
     * make action bar toggle visibility once viewpager get taped
     */
    private void initActionBar() {
        actionBar = getSupportActionBar();

        ((DetectTapLongPressViewPager) viewPager).setTapLongPressListener(new DetectTapLongPressViewPager.TapLongPressListener() {
            @Override
            public void onTap() {
//                toggleActionBarAndSystemUiVisiblity();
            }

            @Override
            public void onLongPress() {
            }
        });
    }

    private void toggleActionBarAndSystemUiVisiblity() {
        if (actionBar.isShowing()) {
            actionBar.hide();
        } else {
            actionBar.show();
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        updateCollectionMenuText(collected.get(position));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    class SimilarImgViewPagerAdapter extends PagerAdapter {

        private LayoutInflater inflater;

        SimilarImgViewPagerAdapter() {
            inflater = LayoutInflater.from(SimilarImgDetailActivity.this);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Image image = images.get(position);
            PullUpDownLinearLayout page = (PullUpDownLinearLayout) inflater.inflate(R.layout.viewpage_similar_img_detail, container, false);
            page.setTag(position);

            if (image.isCollected()) {
                page.setHint("下拉取消收藏", "松开取消收藏", "上拉快速分享", "松开立即分享");
            } else {
                page.setHint("下拉添加收藏", "松开添加收藏", "上拉快速分享", "松开立即分享");
            }
            page.setPullListener(SimilarImgDetailActivity.this);

            WebImageContainer webImageContainer = (WebImageContainer) page.findViewById(R.id.wic_viewpage_similar_img_detail);
            webImageContainer.setImageUrl(image.getDownloadUrl());

            container.addView(page);
            return page;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }
    }


    @Override
    public void onPullDown() {
        toggleCollection();
    }

    private void toggleCollection() {
        final int index = viewPager.getCurrentItem();

        if (collected.get(index)) {
            collected.set(index, Boolean.FALSE);
            SuperToastUtil.showToast(SimilarImgDetailActivity.this, "取消收藏成功！");
        } else {
            collected.set(index, Boolean.TRUE);
            SuperToastUtil.showToast(SimilarImgDetailActivity.this, "添加收藏成功！");
        }

        PullUpDownLinearLayout page = (PullUpDownLinearLayout) viewPager.findViewWithTag(index);
        if (collected.get(index)) {
            page.setHint("下拉取消收藏", "松开取消收藏", "上拉快速分享", "松开立即分享");
        } else {
            page.setHint("下拉添加收藏", "松开添加收藏", "上拉快速分享", "松开立即分享");
        }
        updateCollectionMenuText(collected.get(index));
    }

    private void updateCollectionMenuText(boolean collected){
        collectionMenu.setTitle(collected ? "取消收藏" : "添加收藏");
    }

    @Override
    public void onPullUp() {
        shareClothes();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_similar_img_detail, menu);
        collectionMenu = menu.findItem(R.id.action_add_to_collection);
        initMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.action_add_to_collection:
                toggleCollection();
                break;
            case R.id.action_share_clothes:
                shareClothes();
                break;
            case R.id.action_download:
                downloadImage();
                break;
            case R.id.action_go_shopping:
                goShopping();
                break;
            case R.id.action_info:
                showClothesInfo();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void shareClothes() {
        String chooserTitle = "分享";
        String subject = "Amelie";
        String text = "好漂漂~balabala~[http://myron.sinaapp.com/list]";
        IntentUtil.startShareActivity(this, chooserTitle, subject, text);
    }

    private void goShopping() {
        Intent intent = new Intent(this, WebActivity.class);
        startActivity(intent);
    }

    private void downloadImage() {
        View currentPage = viewPager.findViewWithTag(viewPager.getCurrentItem());
        WebImageContainer webImageContainer = (WebImageContainer) currentPage.findViewById(R.id.wic_viewpage_similar_img_detail);
        webImageContainer.saveWebImage();
        SuperToastUtil.showToast(this, "图片已保存到相册");
    }

    private void showClothesInfo() {
        DialogFragment clothesInfoDialog = new ClothesInfoDialogFragment("Lancome 百褶裙 X7Y8902", this);
        clothesInfoDialog.show(getSupportFragmentManager(), "clothesInfo");
    }

    @Override
    protected void onPause() {
        figureChanges();
        persistentChanges();
        super.onPause();
    }

    private void persistentChanges() {
        final DBHelper dbHelper = new DBHelper(this);
        dbHelper.insertImage(imagesNeedAdd);
        dbHelper.removeImage(imagesNeedRemove);
    }

    private void figureChanges() {
        imagesNeedAdd = new ArrayList<>();
        imagesNeedRemove = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            final Image image = images.get(i);
            final Boolean originFlag = image.isCollected();
            final Boolean currentFlag = collected.get(i);
            if (originFlag && !currentFlag){
                imagesNeedRemove.add(image);
                continue;
            }
            if (!originFlag && currentFlag){
                imagesNeedAdd.add(image);
            }
        }
    }

    public void setChangesResult(){
        Intent intent = new Intent();
        intent.putParcelableArrayListExtra(ExtraDataName.REMOVE_IMGS, ((ArrayList<Image>) imagesNeedRemove));
        setResult(RESULT_OK, intent);
    }

    @Override
    public void finish() {
        figureChanges();
        setChangesResult();
        super.finish();
    }
}
