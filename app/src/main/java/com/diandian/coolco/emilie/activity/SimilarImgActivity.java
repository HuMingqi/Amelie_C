package com.diandian.coolco.emilie.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.diandian.coolco.emilie.R;
import com.diandian.coolco.emilie.adapter.CommonBaseAdapter;
import com.diandian.coolco.emilie.adapter.SimilarImgGridViewHolder;
import com.diandian.coolco.emilie.dialog.ProgressDialog;
import com.diandian.coolco.emilie.model.Image;
import com.diandian.coolco.emilie.model.TmpJsonImage;
import com.diandian.coolco.emilie.utility.Dimension;
import com.diandian.coolco.emilie.utility.Event;
import com.diandian.coolco.emilie.utility.ExtraDataName;
import com.diandian.coolco.emilie.utility.NetHelper;
import com.diandian.coolco.emilie.utility.NetworkManager;
import com.diandian.coolco.emilie.utility.Preference;
import com.diandian.coolco.emilie.utility.PreferenceKey;
import com.diandian.coolco.emilie.utility.SuperToastUtil;
import com.diandian.coolco.emilie.utility.Url;
import com.diandian.coolco.emilie.widget.ShowCaseShadowView;
import com.etsy.android.grid.StaggeredGridView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;

import org.apache.commons.lang.ObjectUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import roboguice.inject.InjectView;

public class SimilarImgActivity extends BaseActivity implements View.OnClickListener {

    @InjectView(R.id.sgv_similar_img)
    private StaggeredGridView similarImgGridView;

    @InjectView(R.id.dv_src_image)
    private SimpleDraweeView srcImageDraweeView;
    @InjectView(R.id.tv_similar_image_number)
    private TextView similarImageNumberTextView;
    @InjectView(R.id.tv_search_use_time)
    private TextView searchUseTimeTextView;

    @InjectView(R.id.ll_card_container)
    private View cardContaner;
    @InjectView(R.id.fl_src_image)
    private View srcImageContainerView;
    @InjectView(R.id.fl_similar_image_number)
    private View similarImageNumberView;
    @InjectView(R.id.fl_search_use_time)
    private View searchUseTimeView;

    @InjectView(R.id.iv_crop_image_again)
    private ImageView cropImageAgainImageView;

    @InjectView(R.id.rl_show_case_container)
    private View showCaseContainer;
    @InjectView(R.id.cv_show_case_shadow)
    private ShowCaseShadowView showCaseShadowView;
    @InjectView(R.id.iv_hand)
    private ImageView handImageView;
    @InjectView(R.id.v_click_point)
    private View clickPointView;

    private List<Image> datas;
    private BaseAdapter adapter;
    private float originalGridSize;
    private float srcImageMinScaleRatio;
    private float targetGridSize;
    private AccelerateDecelerateInterpolator smoothInterpolator;
    private float srcImageMaxTranslateX;
    private float headerHeight;
    private float gridMargin;
    private float searchUseTimeMaxTranslateX;
    private String croppedSrcImgPath;
    private Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActionBar();
        setContentView(R.layout.activity_similar_img);

        init();
    }

    private void init() {
        Intent intent = getIntent();
        croppedSrcImgPath = intent.getStringExtra(ExtraDataName.CROPPED_SRC_IMG_PATH);

        if (!NetworkManager.isOnline(this)) {
            SuperToastUtil.showToast(this, getString(R.string.no_network_tip));
            finish();
            return;
        }
        String uriString = String.format("file://%s", croppedSrcImgPath);
        srcImageDraweeView.setImageURI(Uri.parse(uriString));

        new SearchSimilarImgAysncTask().execute(croppedSrcImgPath);

        //check flag
/*        boolean isSrcImgStorageCompleted = Preference.getPrefBoolean(context, PreferenceKey.IS_SRC_IMG_STORAGE_COMPLETED);
        if (isSrcImgStorageCompleted) {
            srcImageReady();
        } else {
            progressDialog = ProgressDialog.show(this, "正在加载...");
        }*/

//        Drawable editDrawable = new IconDrawable(this, Iconify.IconValue.md_mode_edit).sizeDp(64).color(Color.parseColor("#80e7e7e7"));
//        srcImageDraweeView.getHierarchy().setControllerOverlay(editDrawable);

        srcImageContainerView.setOnClickListener(this);

        datas = new ArrayList<Image>();
/*

        Image image1 = new Image();
        Image image2 = new Image();
        Image image3 = new Image();
        Image image4 = new Image();
        Image image5 = new Image();
        Image image6 = new Image();
        Image image7 = new Image();
        image1.setDownloadUrl("http://myron-mydomain.stor.sinaapp.com/1114163041364330.png");
        image2.setDownloadUrl("http://myron-mydomain.stor.sinaapp.com/14148082703290.png");
        image3.setDownloadUrl("http://myron-mydomain.stor.sinaapp.com/14148082653051.png");
        image4.setDownloadUrl("http://myron-mydomain.stor.sinaapp.com/14148324678681.png");
        image5.setDownloadUrl("http://myron-mydomain.stor.sinaapp.com/14148324678680.png");
        image6.setDownloadUrl("http://myron-mydomain.stor.sinaapp.com/114160635449341.png");
        image7.setDownloadUrl("http://myron-mydomain.stor.sinaapp.com/114274579243230.png");
        image1.setWidth(540);
        image1.setHeight(405);

        image2.setWidth(500);
        image2.setHeight(331);

        image3.setWidth(1440);
        image3.setHeight(1280);

        image4.setWidth(736);
        image4.setHeight(1003);

        image5.setWidth(1080);
        image5.setHeight(1440);

        image6.setWidth(1440);
        image6.setHeight(1280);

        image7.setWidth(1024);
        image7.setHeight(1024);

        datas.add(image1);
        datas.add(image2);
        datas.add(image3);
        datas.add(image4);
        datas.add(image5);
        datas.add(image6);
        datas.add(image7);

*/
        smoothInterpolator = new AccelerateDecelerateInterpolator();

        originalGridSize = (Dimension.getScreenWidth(this) - 4 * Dimension.dp2px(this, 8)) / 3;
        targetGridSize = Dimension.dp2px(this, 48);

        srcImageMinScaleRatio = targetGridSize / originalGridSize;
        srcImageMaxTranslateX = Dimension.getScreenWidth(this) - originalGridSize - getResources().getDimensionPixelOffset(R.dimen.padding_header) * 2;

        gridMargin = Dimension.dp2px(this, 8);
        searchUseTimeMaxTranslateX = Dimension.getScreenWidth(this) - gridMargin - searchUseTimeView.getRight() + 2 * gridMargin + 2 * targetGridSize;

        srcImageContainerView.setPivotX(originalGridSize);
        similarImageNumberView.setPivotX(originalGridSize);
        searchUseTimeView.setPivotX(originalGridSize);

        headerHeight = originalGridSize + getResources().getDimensionPixelOffset(R.dimen.padding_header);

        int iconSize = (int) (originalGridSize * 0.5);
        int iconColor = getResources().getColor(R.color.ab_icon);
        int editIconSize = (int) (originalGridSize / 3);
        Drawable clothesDrawable = new IconDrawable(this, Iconify.IconValue.md_local_florist).sizePx(iconSize).color(iconColor);
        Drawable clockDrawable = new IconDrawable(this, Iconify.IconValue.md_query_builder).sizePx(iconSize).color(iconColor);
        Drawable editDrawable = new IconDrawable(this, Iconify.IconValue.md_edit).sizePx(editIconSize).color(Color.parseColor("#ffffff")).alpha(100);
        similarImageNumberTextView.setCompoundDrawables(null, clothesDrawable, null, null);
        searchUseTimeTextView.setCompoundDrawables(null, clockDrawable, null, null);
        cropImageAgainImageView.setImageDrawable(editDrawable);
//        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) cropImageAgainImageView.getLayoutParams();
//        layoutParams.width = editIconSize;
//        layoutParams.height = editIconSize;
//        cropImageAgainImageView.setLayoutParams(layoutParams);


        View placeHolderView = new View(this);
        placeHolderView.setMinimumHeight(((int) headerHeight));
        similarImgGridView.addHeaderView(placeHolderView);

        adapter = new CommonBaseAdapter<Image>(context, R.layout.grid_item_similar_img, datas, SimilarImgGridViewHolder.class);
//        SwingBottomInAnimationAdapter animationAdapter = new SwingBottomInAnimationAdapter(adapter);
//        animationAdapter.setAbsListView(similarImgGridView);
//        similarImgGridView.setAdapter(animationAdapter);
        similarImgGridView.setAdapter(adapter);

        similarImgGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                animate();
            }
        });


        similarImgGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int headerViewsCount = similarImgGridView.getHeaderViewsCount();
                int calibratedPosition = position - headerViewsCount;
                if (calibratedPosition >= 0 && calibratedPosition < datas.size()) {
                    startSimilarImgDetailActivity(calibratedPosition, view);
                }

            }
        });
        
        initOperationHint();

        initLayoutAnimation();
/*

        LayoutTransition transition = new LayoutTransition();
        int layoutAnimationDuration = 1000;
        transition.setDuration(layoutAnimationDuration);
        transition.setInterpolator(LayoutTransition.APPEARING, new AccelerateDecelerateInterpolator());
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(null, "scaleX", 0 , 1).setDuration(layoutAnimationDuration);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(null, "scaleY", 0 , 1).setDuration(layoutAnimationDuration);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(scaleX, scaleY);
        transition.setAnimator(LayoutTransition.APPEARING, animatorSet);
        ((ViewGroup) cardContaner).setLayoutTransition(transition);

*/

    }

    private void showCaseCrop() {
        boolean userLearnedCrop = Preference.getPrefBoolean(this, PreferenceKey.SHOW_CASE_CROP);

        if (!userLearnedCrop) {
            Rect cropImageAgainRect = new Rect(((int) gridMargin), ((int) gridMargin), ((int) (gridMargin + originalGridSize)), ((int) (gridMargin + originalGridSize)));
            showCaseShadowView.setEmptyRect(cropImageAgainRect);

            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) handImageView.getLayoutParams();
            int handMargin = (int) (gridMargin + originalGridSize/2);
            params.leftMargin = handMargin;
            params.topMargin = handMargin;
            handImageView.setLayoutParams(params);

            int clickPointSize = getResources().getDimensionPixelOffset(R.dimen.size_click_point);
            params = (RelativeLayout.LayoutParams) clickPointView.getLayoutParams();
            params.leftMargin = handMargin;
            params.topMargin = ((int) (handMargin - clickPointSize * 0.35f));
            clickPointView.setLayoutParams(params);

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
//            animatorSet.setInterpolator(new AccelerateDecelerateInterpolator());
            animatorSet.playTogether(scaleXAnimator, scaleYAnimator, alphaAnimator);

            animatorSet.start();

            showCaseContainer.setVisibility(View.VISIBLE);
            showCaseContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showCaseContainer.setVisibility(View.GONE);
                    Preference.setPrefBoolean(SimilarImgActivity.this, PreferenceKey.SHOW_CASE_CROP, true);
                }
            });
        }
    }

    private void initOperationHint() {

    }

    private void initLayoutAnimation(){

    }

    private void srcImageReady() {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

        //reset flag
        Preference.setPrefBoolean(context, PreferenceKey.IS_SRC_IMG_STORAGE_COMPLETED, false);

        if (!NetworkManager.isOnline(this)) {
            SuperToastUtil.showToast(this, getString(R.string.no_network_tip));
//            srcImageDraweeView.post(new Runnable() {
//                @Override
//                public void run() {
            finish();
//                }
//            });
            return;
        }
        String uriString = String.format("file://%s", croppedSrcImgPath);
        srcImageDraweeView.setImageURI(Uri.parse(uriString));
        new SearchSimilarImgAysncTask().execute(croppedSrcImgPath);
    }

    public void onEventMainThread(Event.SrcImgSavedEvent event) {
        srcImageReady();
    }

    private void animate() {
        final int scrollY = getScrollY();

        float scaleRatio = (originalGridSize - scrollY) / originalGridSize;
        scaleRatio = clamp(scaleRatio, srcImageMinScaleRatio, 1.0f);
        srcImageContainerView.setScaleX(scaleRatio);
        srcImageContainerView.setScaleY(scaleRatio);
        similarImageNumberView.setScaleX(scaleRatio);
        similarImageNumberView.setScaleY(scaleRatio);
        searchUseTimeView.setScaleX(scaleRatio);
        searchUseTimeView.setScaleY(scaleRatio);

//        float searchUseTimeTranslateX = clamp(scrollY-(originalGridSize - targetGridSize), 0, searchUseTimeMaxTranslateX);
        float searchUseTimeTranslateX = clamp(scrollY - (originalGridSize - targetGridSize), 0, 2 * targetGridSize + 2 * gridMargin);
        searchUseTimeView.setTranslationX(searchUseTimeTranslateX);

        float searchUseTimeVisualLeft = searchUseTimeView.getLeft() + searchUseTimeView.getTranslationX() + originalGridSize * (1 - scaleRatio);
        float simliarImgNumTranslateX = searchUseTimeVisualLeft - gridMargin - similarImageNumberView.getRight();
        similarImageNumberView.setTranslationX(simliarImgNumTranslateX);

        float similarImageNumVisualLeft = searchUseTimeVisualLeft - originalGridSize * scaleRatio - gridMargin;
        float srcImageTranslateX = similarImageNumVisualLeft - gridMargin - srcImageContainerView.getRight();
        srcImageContainerView.setTranslationX(srcImageTranslateX);

//        float translateRatio = ((float) scrollY) / originalGridSize;
//        translateRatio = clamp(translateRatio, 0.0f, 1.0f);
//        translateRatio = smoothInterpolator.getInterpolation(translateRatio);
//        final float translateX = translateRatio * srcImageMaxTranslateX;
//        srcImageDraweeView.setTranslationX(translateX);
//

//        similarImageNumberTextView.setTranslationX(translateX);
//

//        searchUseTimeTextView.setTranslationX(translateX);

//        GenericDraweeHierarchy hierarchy = srcImageDraweeView.getHierarchy();
//        RoundingParams roundingParams = hierarchy.getRoundingParams();
//        roundingParams.setCornersRadius()
//        hierarchy.setRoundingParams();
    }

    public int getScrollY() {
        View c = similarImgGridView.getChildAt(0);
        if (c == null) {
            return 0;
        }
        if (c instanceof ViewGroup) {
            return Integer.MAX_VALUE;
        } else {
            return -c.getTop();
        }
    }

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(value, max));
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void startSimilarImgDetailActivity(int pos, View itemView) {
        ActivityOptionsCompat options = null;
//        if (Build.VERSION.SDK_INT >= 21) {
//            options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, itemView, getResources().getString(R.string.similar_img_transtion_dest));
//        } else {
        options = ActivityOptionsCompat.makeScaleUpAnimation(itemView, 0, 0, itemView.getWidth(), itemView.getHeight());
//        }
        Intent intent = new Intent(this, SimilarImgDetailActivity.class);
        intent.putParcelableArrayListExtra(ExtraDataName.SIMILAR_IMGS, (ArrayList<Image>) datas);
        intent.putExtra(ExtraDataName.SIMILAR_IMG_INIT_POS, pos);
        if (Build.VERSION.SDK_INT > 15) {
            startActivity(intent, options.toBundle());
        } else {
            startActivity(intent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_similar_img, menu);

        initMenu(menu);
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
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fl_src_image:
                startSrcImgCropActivity();
                break;
        }
    }

    private void startSrcImgCropActivity() {
        Preference.setPrefBoolean(getApplicationContext(), PreferenceKey.IS_SRC_IMG_STORAGE_COMPLETED, true);
        Intent intent = new Intent(this, SrcImgCropActivity.class);
        intent.putExtra(ExtraDataName.SRC_IMG_PATH, croppedSrcImgPath);
        startActivity(intent);
    }


    class SearchSimilarImgAysncTask extends AsyncTask<String, ObjectUtils.Null, Integer> {

        private Dialog progressDialog;
        private long searchStartTime;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(SimilarImgActivity.this, "正在搜索...");
            searchStartTime = System.currentTimeMillis();
        }

        @Override
        protected Integer doInBackground(String... params) {
            String croppedSrcImgPath = params[0];
            Bitmap croppedSrcImgBm = BitmapFactory.decodeFile(croppedSrcImgPath);
            String imgName = System.currentTimeMillis() + ".jpg";
            String responseString = NetHelper.sendRequest(Url.SEARCH_SIMILAR_IMAGES, null, imgName, croppedSrcImgBm);
            if (responseString == null) {
                return 0;
            }
            Gson gson = new Gson();
            try {
                JSONObject responseJsonObject = new JSONObject(responseString);
                //if (responseJsonObject.get("status") != 1) {
                if (responseJsonObject.getInt("status") != 1) {
                    return 0;
                }
//                JSONArray similarImgUrlJsonArray = responseJsonObject.getJSONArray("data");

/*

                String similarImgUrlsString = responseJsonObject.getString("data");
                Type type = new TypeToken<List<String>>() {
                }.getType();
                List<String> similarImgUrls = gson.fromJson(similarImgUrlsString, type);

                datas.clear();
                for (String similarImgUrl : similarImgUrls) {
                    Image image = new Image();
                    image.setDownloadUrl(similarImgUrl);
                    image.setWidth(1100);
                    image.setHeight(1500);
                    datas.add(image);
                }

*/
                String jsonString = responseJsonObject.getString("data");
                Type type = new TypeToken<List<TmpJsonImage>>(){}.getType();
                List<TmpJsonImage> tmpJsonImages = gson.fromJson(jsonString, type);
                datas.clear();
                for (TmpJsonImage tmpJsonImage : tmpJsonImages){
                    Image image = new Image();
                    image.setDownloadUrl(tmpJsonImage.getDownload_url());
                    image.setWidth(tmpJsonImage.getWidth());
                    image.setHeight(tmpJsonImage.getHeight());
                    image.setShoppingUrl(tmpJsonImage.getShopping_url());
                    image.setDescription(tmpJsonImage.getOther_info());
                    datas.add(image);
                }
                return 1;
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return 0;
        }

        @Override
        protected void onPostExecute(Integer result) {
            progressDialog.dismiss();
            if (result != 1) {
//                Toast.makeText(context, "抱歉，出错了~", Toast.LENGTH_SHORT).show();
                SuperToastUtil.showToast(context, "抱歉，出错了~");
                finish();
                return;
            }

            similarImageNumberTextView.setText("找到" + adapter.getCount() + "件");
            long searchUseTime = System.currentTimeMillis() - searchStartTime;
            float searchUseTimeFormatted = searchUseTime / 10;
            searchUseTimeTextView.setText("用时"+searchUseTimeFormatted/100+"秒");

            srcImageContainerView.setVisibility(View.VISIBLE);
            similarImageNumberView.setVisibility(View.VISIBLE);
            searchUseTimeView.setVisibility(View.VISIBLE);

            adapter.notifyDataSetChanged();

            showCaseCrop();
        }
    }
}
