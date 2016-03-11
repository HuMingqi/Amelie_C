package com.diandian.coolco.emilie.activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.diandian.coolco.emilie.R;
import com.diandian.coolco.emilie.dialog.ClothesInfoDialogFragment;
import com.diandian.coolco.emilie.model.Image;
import com.diandian.coolco.emilie.utility.DBHelper;
import com.diandian.coolco.emilie.utility.ExtraDataName;
import com.diandian.coolco.emilie.utility.IntentUtil;
import com.diandian.coolco.emilie.utility.MyApplication;
import com.diandian.coolco.emilie.utility.Preference;
import com.diandian.coolco.emilie.utility.PreferenceKey;
import com.diandian.coolco.emilie.utility.SuperToastUtil;
import com.diandian.coolco.emilie.widget.DetectTapLongPressViewPager;
import com.diandian.coolco.emilie.widget.DoubleSideFrameLayout;
import com.diandian.coolco.emilie.widget.PullUpDownLinearLayout;
import com.diandian.coolco.emilie.widget.WebImageContainer;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.util.AsyncExecutor;
import roboguice.inject.InjectView;

public class SimilarImgDetailActivity extends DbSupportBaseActivity implements PullUpDownLinearLayout.PullListener, ViewPager.OnPageChangeListener {

    @InjectView(R.id.vp_similar_img)
    private ViewPager viewPager;
    
    @InjectView(R.id.v_shadow)
    private View shadowView;
    @InjectView(R.id.ll_hint_pull_down)
    private View pullDownHintView;
    @InjectView(R.id.ll_hint_pull_up)
    private View pullUpHintView;
    @InjectView(R.id.rl_show_case_double_tap_container)
    private View doubleTapHintView;
    @InjectView(R.id.v_click_point)
    private View clickPointView;

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
        initActionBar();
        actionBar = getSupportActionBar();
        actionBar.hide();
        init();
    }

    private void init() {
        Intent intent = getIntent();
        images = intent.getParcelableArrayListExtra(ExtraDataName.SIMILAR_IMGS);

        initCollected();

        int initPos = intent.getIntExtra(ExtraDataName.SIMILAR_IMG_INIT_POS, 0);
        adapter = new SimilarImgViewPagerAdapter();
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(initPos, true);
        viewPager.setOnPageChangeListener(this);
/*

        SystemUiHelper systemUiHelper = new SystemUiHelper(
                this,
//                SystemUiHelper.LEVEL_LEAN_BACK,   // Choose from one of the levels
//                SystemUiHelper.LEVEL_IMMERSIVE,   // Choose from one of the levels
                SystemUiHelper.LEVEL_HIDE_STATUS_BAR,   // Choose from one of the levels
//                SystemUiHelper.LEVEL_LOW_PROFILE,   // Choose from one of the levels
                0);

        systemUiHelper.hide();

*/
        ((DetectTapLongPressViewPager) viewPager).setGestureListener(new DetectTapLongPressViewPager.GestureListener() {
            @Override
            public void onTap() {
                toggleActionBar();
            }

            @Override
            public void onDoubleTap() {
                flipPage();
            }

            @Override
            public void onLongPress() {
                showContextMenu();
            }
        });

        initShowCase();
    }

    private void flipPage() {
        View currentPageView = getCurrentPageView();
        DoubleSideFrameLayout doubleSideFrameLayout = (DoubleSideFrameLayout) currentPageView.findViewById(R.id.dsf_clothes);
        doubleSideFrameLayout.flip();
    }

    private void initShowCase(){
        initPullDownShowCase();
    }

    private void initPullDownShowCase() {
        boolean pullDownCaseShowed = Preference.getPrefBoolean(this, PreferenceKey.SHOW_CASE_PULL_DOWN);
        if (!pullDownCaseShowed){
            shadowView.setVisibility(View.VISIBLE);
            pullDownHintView.setVisibility(View.VISIBLE);
            pullDownHintView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pullDownHintView.setVisibility(View.GONE);
                    shadowView.setVisibility(View.GONE);
                    Preference.setPrefBoolean(SimilarImgDetailActivity.this, PreferenceKey.SHOW_CASE_PULL_DOWN, true);
                    initPullUpShowCase();
                }
            });
            float handMoveDistance = getResources().getDimensionPixelOffset(R.dimen.show_case_hand_move_distance);
            ValueAnimator animator = ObjectAnimator.ofFloat(pullDownHintView, "translationY", 0, handMoveDistance);
            animator.setDuration(2000);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setRepeatMode(ValueAnimator.RESTART);
            animator.start();
        }
    }

    private void initPullUpShowCase() {
        boolean pullUpCaseShowed = Preference.getPrefBoolean(this, PreferenceKey.SHOW_CASE_PULL_UP);
        if (!pullUpCaseShowed){
            shadowView.setVisibility(View.VISIBLE);
            pullUpHintView.setVisibility(View.VISIBLE);
            pullUpHintView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pullUpHintView.setVisibility(View.GONE);
                    shadowView.setVisibility(View.GONE);
                    Preference.setPrefBoolean(SimilarImgDetailActivity.this, PreferenceKey.SHOW_CASE_PULL_UP, true);
                    initDoubleTapShowCase();
                }
            });
            float handMoveDistance = getResources().getDimensionPixelOffset(R.dimen.show_case_hand_move_distance);
            ValueAnimator animator = ObjectAnimator.ofFloat(pullUpHintView, "translationY", 0, -handMoveDistance);
            animator.setDuration(2000);
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setRepeatMode(ValueAnimator.RESTART);
            animator.start();
        }
    }
    
    private void initDoubleTapShowCase() {
        boolean doubleTapCaseShowed = Preference.getPrefBoolean(this, PreferenceKey.SHOW_CASE_DOUBLE_TAP);
        if (!doubleTapCaseShowed){
            shadowView.setVisibility(View.VISIBLE);
            doubleTapHintView.setVisibility(View.VISIBLE);
            doubleTapHintView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    doubleTapHintView.setVisibility(View.GONE);
                    shadowView.setVisibility(View.GONE);
                    Preference.setPrefBoolean(SimilarImgDetailActivity.this, PreferenceKey.SHOW_CASE_DOUBLE_TAP, true);
                }
            });

            int clickPointSize = getResources().getDimensionPixelOffset(R.dimen.size_click_point);

            clickPointView.setPivotX(clickPointSize/2);
            clickPointView.setPivotY(clickPointSize/2);
            ValueAnimator scaleXAnimator = ObjectAnimator.ofFloat(clickPointView, "scaleX", 1, 2);
            ValueAnimator scaleYAnimator = ObjectAnimator.ofFloat(clickPointView, "scaleY", 1, 2);
            ValueAnimator alphaAnimator = ObjectAnimator.ofFloat(clickPointView, "alpha", 0.8f, 0);
            scaleXAnimator.setRepeatCount(ValueAnimator.INFINITE);
            scaleXAnimator.setRepeatMode(ValueAnimator.RESTART);
            scaleYAnimator.setRepeatCount(ValueAnimator.INFINITE);
            scaleYAnimator.setRepeatMode(ValueAnimator.RESTART);
            alphaAnimator.setRepeatCount(ValueAnimator.INFINITE);
            alphaAnimator.setRepeatMode(ValueAnimator.RESTART);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.setDuration(1000);
            animatorSet.playTogether(scaleXAnimator, scaleYAnimator, alphaAnimator);

            animatorSet.start();
        }
    }

    private void initCollected() {
        for (int i = 0; i < images.size(); i++) {
            collected.add(images.get(i).isCollected());
        }
    }


    private void toggleActionBar() {
        if (actionBar.isShowing()) {
            actionBar.hide();
        } else {
            actionBar.show();
        }
    }

    private void showContextMenu() {
        DialogFragment dialogFragment = new MenuDialogFragment();
//        dialogFragment.show(getSupportFragmentManager(), "menu");
        ((MenuDialogFragment) dialogFragment).showImmersive();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        updatePullUpDown();
        updateMenu();
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

            TextView clothesInfoTextView = (TextView) page.findViewById(R.id.tv_clothes_info);
            clothesInfoTextView.setText(image.getDescription());

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

        updatePullUpDown();
        updateMenu();
    }

    private void updatePullUpDown() {
        final int index = viewPager.getCurrentItem();
        PullUpDownLinearLayout page = getCurrentPageView();
        if (collected.get(index)) {
            page.setHint("下拉取消收藏", "松开取消收藏", "上拉快速分享", "松开立即分享");
        } else {
            page.setHint("下拉添加收藏", "松开添加收藏", "上拉快速分享", "松开立即分享");
        }
    }

    public PullUpDownLinearLayout getCurrentPageView(){
        final int index = viewPager.getCurrentItem();
        return (PullUpDownLinearLayout) viewPager.findViewWithTag(index);
    }


    private void updateMenu() {
        final int index = viewPager.getCurrentItem();
        collectionMenu.setTitle(collected.get(index) ? "取消收藏" : "添加收藏");
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

    @SuppressLint("ValidFragment")
    private class MenuDialogFragment extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            AlertDialog.Builder builder = new AlertDialog.Builder(SimilarImgDetailActivity.this);
            boolean currentClothesCollected = collected.get(viewPager.getCurrentItem());
            int menuStringArrayId = currentClothesCollected ? R.array.menu_similar_img_detail_remove_collection : R.array.menu_similar_img_detail_add_collection;
            builder.setItems(menuStringArrayId, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    switch (i) {
                        case 0:
                            toggleCollection();
                            break;
                        case 1:
                            shareClothes();
                            break;
                        case 2:
                            downloadImage();
                            break;
                        case 3:
                            goShopping();
                            break;
                        case 4:
                            showClothesInfo();
                            break;
                    }
                }
            });
            // Temporarily set the dialogs window to not focusable to prevent the short
            // popup of the navigation bar.
            Dialog dialog = builder.create();
            dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
            return dialog;
        }

        public void showImmersive() {

            // Show the dialog.
            show(getSupportFragmentManager(), "menu");

            // It is necessary to call executePendingTransactions() on the FragmentManager
            // before hiding the navigation bar, because otherwise getWindow() would raise a
            // NullPointerException since the window was not yet created.
            getFragmentManager().executePendingTransactions();

            // Hide the navigation bar. It is important to do this after show() was called.
            // If we would do this in onCreateDialog(), we would get a requestFeature()
            // error.
            getDialog().getWindow().getDecorView().setSystemUiVisibility(
                    getActivity().getWindow().getDecorView().getSystemUiVisibility()
            );

            // Make the dialogs window focusable again.
            getDialog().getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            );

        }
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
        String text = "好漂亮~"+images.get(viewPager.getCurrentItem()).getShoppingUrl();
        IntentUtil.startShareActivity(this, chooserTitle, subject, text);
    }

    private void goShopping() {
        Intent intent = new Intent(this, WebActivity.class);
        intent.putExtra(ExtraDataName.WEB_ACTIVITY_URL, images.get(viewPager.getCurrentItem()).getShoppingUrl());
        startActivity(intent);
    }

    private void downloadImage() {
        View currentPage = viewPager.findViewWithTag(viewPager.getCurrentItem());
        WebImageContainer webImageContainer = (WebImageContainer) currentPage.findViewById(R.id.wic_viewpage_similar_img_detail);
        webImageContainer.saveWebImage(this);
        SuperToastUtil.showToast(this, "图片已保存到相册");
    }

    private void showClothesInfo() {
        DialogFragment clothesInfoDialog = new ClothesInfoDialogFragment(images.get(viewPager.getCurrentItem()).getDescription(), this);
//        DialogFragment clothesInfoDialog = new ClothesInfoDialogFragment("Lancome 百褶裙 X7Y8902", this);
        clothesInfoDialog.show(getSupportFragmentManager(), "clothesInfo");
    }

    @Override
    protected void onPause() {
        figureChanges();
        persistentChanges();
        super.onPause();
    }

    private void persistentChanges() {
        ((MyApplication) getApplication()).getAsyncExecutor().execute(
                new PersistentCollectionChangeTask(
                        getApplicationContext(),
                        new ArrayList<Image>(imagesNeedRemove),
                        new ArrayList<Image>(imagesNeedAdd)
                )
        );
    }

    private void figureChanges() {
        imagesNeedAdd = new ArrayList<>();
        imagesNeedRemove = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            final Image image = images.get(i);
            final Boolean originFlag = image.isCollected();
            final Boolean currentFlag = collected.get(i);
            if (originFlag && !currentFlag) {
                imagesNeedRemove.add(image);
                continue;
            }
            if (!originFlag && currentFlag) {
                imagesNeedAdd.add(image);
            }
        }
    }

    static class PersistentCollectionChangeTask implements AsyncExecutor.RunnableEx {

        private List<Image> imagesNeedRemove;
        private List<Image> imagesNeedAdd;
        private Context context;

        public PersistentCollectionChangeTask(Context context, List<Image> imagesNeedRemove, List<Image> imagesNeedAdd) {
            this.context = context;
            this.imagesNeedRemove = imagesNeedRemove;
            this.imagesNeedAdd = imagesNeedAdd;
        }

        @Override
        public void run() throws Exception {
            final DBHelper dbHelper = new DBHelper(context);
            dbHelper.insertImage(imagesNeedAdd);
            dbHelper.removeImage(imagesNeedRemove);
            dbHelper.close();
        }
    }
}
