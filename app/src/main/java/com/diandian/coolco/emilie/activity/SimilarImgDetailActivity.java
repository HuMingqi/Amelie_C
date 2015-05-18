package com.diandian.coolco.emilie.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.diandian.coolco.emilie.R;
import com.diandian.coolco.emilie.dialog.ClothesInfoDialogFragment;
import com.diandian.coolco.emilie.model.Image;
import com.diandian.coolco.emilie.utility.BitmapStorage;
import com.diandian.coolco.emilie.utility.ExtraDataName;
import com.diandian.coolco.emilie.utility.IntentUtil;
import com.diandian.coolco.emilie.utility.SuperToastUtil;
import com.diandian.coolco.emilie.utility.SystemUiHelper;
import com.diandian.coolco.emilie.widget.DetectTapLongPressViewPager;
import com.diandian.coolco.emilie.widget.PullUpDownLinearLayout;
import com.diandian.coolco.emilie.widget.WebImageContainer;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;

import java.util.ArrayList;
import java.util.List;

import roboguice.inject.InjectView;

public class SimilarImgDetailActivity extends BaseActivity{

    @InjectView(R.id.vp_similar_img)
    private ViewPager similarImgViewPager;
    private ActionBar actionBar;
    private SystemUiHelper systemUiHelper;

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
        ArrayList<Image> imgs = intent.getParcelableArrayListExtra(ExtraDataName.SIMILAR_IMGS);
        int initPos = intent.getIntExtra(ExtraDataName.SIMILAR_IMG_INIT_POS, 0);
        PagerAdapter adapter = new SimilarImgViewPagerAdapter(imgs);
        similarImgViewPager.setAdapter(adapter);
        similarImgViewPager.setCurrentItem(initPos, true);
    }

    /**
     * make action bar toggle visibility once viewpager get taped
     */
    private void initActionBar() {
        actionBar = getSupportActionBar();

        systemUiHelper = new SystemUiHelper(
                this,
//                SystemUiHelper.LEVEL_LEAN_BACK,   // Choose from one of the levels
//                SystemUiHelper.LEVEL_IMMERSIVE,   // Choose from one of the levels
                SystemUiHelper.LEVEL_HIDE_STATUS_BAR,   // Choose from one of the levels
//                SystemUiHelper.LEVEL_LOW_PROFILE,   // Choose from one of the levels
                0);

//        actionBar.hide();
        ((DetectTapLongPressViewPager) similarImgViewPager).setTapLongPressListener(new DetectTapLongPressViewPager.TapLongPressListener() {
            @Override
            public void onTap() {
                toggleActionBarAndSystemUiVisiblity();
            }

            @Override
            public void onLongPress() {
                showContextMenu();
            }
        });
    }

    private void showContextMenu() {
        /*
        List<String> menuStrings = new ArrayList<>();
        menuStrings.add("收藏");
        menuStrings.add("分享");
        menuStrings.add("保存到相册");
        menuStrings.add("去购买");
        menuStrings.add("查看服饰信息");
        MenuDialog menuDialog = new MenuDialog(this, menuStrings);
        menuDialog.setMenuListener(new MenuDialog.MenuListener() {
            @Override
            public void onMenuItemClick(int position) {
                //TODO:
            }
        });
        menuDialog.show();
        */
        DialogFragment dialogFragment = new MenuDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "menu");
    }

    private void toggleActionBarAndSystemUiVisiblity() {
        if (actionBar.isShowing()){
            actionBar.hide();
            systemUiHelper.hide();
        } else {
            actionBar.show();
            systemUiHelper.show();
        }
    }

    class SimilarImgViewPagerAdapter extends PagerAdapter {

        private List<Image> images;
        private List<View> views;
        private LayoutInflater inflater;

        SimilarImgViewPagerAdapter(List<Image> images) {
            this.images = images;
            this.views = new ArrayList<View>();
            for (int i = 0; i < images.size(); i++) {
                views.add(null);
            }
            inflater = LayoutInflater.from(SimilarImgDetailActivity.this);
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
//            return super.instantiateItem(container, position);
//            View view = views.get(position);
//            if (view == null) {
//                view = new ImageView(SimilarImgDetailActivity.this);
            Image image = images.get(position);
            View view = inflater.inflate(R.layout.viewpage_similar_img_detail, container, false);
//                view = LayoutInflater.from(SimilarImgDetailActivity.this).inflate(R.layout.viewpage_similar_img_detail, container, false);
//                views.set(position, view);
//            }
            view.setTag(position);
            WebImageContainer webImageContainer = (WebImageContainer) view.findViewById(R.id.wic_viewpage_similar_img_detail);
            webImageContainer.setImageUrl(image.getDownloadUrl());

//            setPullCallback((PullUpDownLinearLayout) view);

            container.addView(view);
//            SizeAdjustableSimpleDraweeView draweeView = (SizeAdjustableSimpleDraweeView) webImageContainer.findViewById(R.id.dv_web_img);
//            draweeView.setDrawableWidth(1000);
//            draweeView.setDrawableHeight(2300);
            //webImageContainer.setImageSize(image.getSize());
//            container.addView(view, position);
//            ImageLoader.getInstance().displayImage(images.get(position).getDownloadUrl(), (ImageView) (view.findViewById(R.id.iv_page_similar_img_detail)));
//            ImageLoader.getInstance().displayImage(images.get(position).getDownloadUrl(), (ImageView) view);

            return view;
        }

        private void setPullCallback(PullUpDownLinearLayout view) {
            view.setPullListener(new PullUpDownLinearLayout.PullListener() {
                @Override
                public void onPullDown() {
                    SuperToastUtil.showToast(SimilarImgDetailActivity.this, "添加收藏成功！");
                }

                @Override
                public void onPullUp() {
                    SuperToastUtil.showToast(SimilarImgDetailActivity.this, "分享成功！");
                }
            });
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
//            super.destroyItem(container, position, object);
//            container.removeViewAt(position);
//            container.removeView(views.get(position));
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
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_similar_img_detail, menu);
        menu.findItem(R.id.action_add_to_collection).setIcon(
                new IconDrawable(this, Iconify.IconValue.md_star)
                        .colorRes(R.color.ab_icon)
                        .actionBarSize());
        menu.findItem(R.id.action_share_clothes).setIcon(
                new IconDrawable(this, Iconify.IconValue.md_share)
                        .colorRes(R.color.ab_icon)
                        .actionBarSize());
//        mainMenu = menu;
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

        switch (item.getItemId()){
            case R.id.action_add_to_collection:
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

//        if (id == android.R.id.home) {
////            NavUtils.navigateUpFromSameTask(this);
//            finish();
//            return true;
//        }

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
//        View currentPage = similarImgViewPager.getChildAt(similarImgViewPager.getCurrentItem());
        View currentPage = similarImgViewPager.findViewWithTag(similarImgViewPager.getCurrentItem());
        WebImageContainer webImageContainer = (WebImageContainer) currentPage.findViewById(R.id.wic_viewpage_similar_img_detail);
        webImageContainer.saveWebImage();
        SuperToastUtil.showToast(this, "图片已保存到相册");
    }

    private void showClothesInfo() {
//        AppCompatDialog
//        AlertDialog alerDialog = new AlertDialog
        DialogFragment clothesInfoDialog = new ClothesInfoDialogFragment("Lancome 百褶裙 X7Y8902", this);
        clothesInfoDialog.show(getSupportFragmentManager(), "clothesInfo");
    }



    @SuppressLint("ValidFragment")
    public class MenuDialogFragment extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SimilarImgDetailActivity.this);
            builder.setItems(R.array.menu_similar_img_detail, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    switch (i){
                        case 0:
                            break;
                        case 1:
                            break;
                        case 2:
                            break;
                        case 3:
                            break;
                        case 4:
                            showClothesInfo();
                            break;
                    }
                }
            });
            return builder.create();
        }
    }

    /**
     * open or close the more menu when pressing menu hardware button
     */
//    @Override
//    public boolean onKeyDown(int keycode, KeyEvent e) {
//        switch (keycode) {
//            case KeyEvent.KEYCODE_MENU:
//                mainMenu.performIdentifierAction(R.id.ab_button_list, 0);
//                return true;
//        }
//
//        return super.onKeyDown(keycode, e);
//    }
}
