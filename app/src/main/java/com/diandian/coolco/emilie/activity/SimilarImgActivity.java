package com.diandian.coolco.emilie.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.TextView;
import android.widget.Toast;

import com.diandian.coolco.emilie.R;
import com.diandian.coolco.emilie.adapter.CommonBaseAdapter;
import com.diandian.coolco.emilie.adapter.SimilarImgGridViewHolder;
import com.diandian.coolco.emilie.dialog.ProgressDialog;
import com.diandian.coolco.emilie.model.Image;
import com.diandian.coolco.emilie.utility.Dimension;
import com.diandian.coolco.emilie.utility.ExtraDataName;
import com.diandian.coolco.emilie.utility.NetHelper;
import com.diandian.coolco.emilie.utility.Url;
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

public class SimilarImgActivity extends BaseActivity {

    @InjectView(R.id.sgv_similar_img)
    private StaggeredGridView similarImgGridView;
    @InjectView(R.id.dv_src_image)
    private SimpleDraweeView srcImageDraweeView;
    @InjectView(R.id.tv_similar_image_number)
    private TextView similarImageNumberTextView;
    @InjectView(R.id.tv_search_use_time)
    private TextView searchUseTimeTextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_similar_img);

        init();
    }

    private void init() {
        Intent intent = getIntent();
        String croppedSrcImgPath = intent.getStringExtra(ExtraDataName.CROPPED_SRC_IMG_PATH);
//        new SearchSimilarImgAysncTask().execute(croppedSrcImgPath);
        String uriString = String.format("file://%s", croppedSrcImgPath);
        srcImageDraweeView.setImageURI(Uri.parse(uriString));

//        Drawable editDrawable = new IconDrawable(this, Iconify.IconValue.md_mode_edit).sizeDp(64).color(Color.parseColor("#80e7e7e7"));
//        srcImageDraweeView.getHierarchy().setControllerOverlay(editDrawable);

        Drawable clothesDrawable = new IconDrawable(this, Iconify.IconValue.md_local_florist).sizeDp(54).color(getResources().getColor(R.color.ab_icon));
        Drawable clockDrawable = new IconDrawable(this, Iconify.IconValue.md_query_builder).sizeDp(54).color(getResources().getColor(R.color.ab_icon));
        similarImageNumberTextView.setCompoundDrawables(null, clothesDrawable, null, null);
        searchUseTimeTextView.setCompoundDrawables(null, clockDrawable, null, null);

        datas = new ArrayList<Image>();

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


//        TextView clothesNumTextView = new TextView(this);
//        clothesNumTextView.setText("找到2,123件服饰");
//        clothesNumTextView.setTextSize(14);
//        clothesNumTextView.setTextColor(getResources().getColor(android.R.color.secondary_text_light));
//        int padding = (int) Dimension.dp2px(this, 12);
//        clothesNumTextView.setPadding(padding, padding, padding, padding);
//        similarImgGridView.addHeaderView(clothesNumTextView);

        smoothInterpolator = new AccelerateDecelerateInterpolator();

        originalGridSize = getResources().getDimensionPixelOffset(R.dimen.size_src_image);
        targetGridSize = Dimension.dp2px(this, 48);

        srcImageMinScaleRatio = targetGridSize / originalGridSize;
        srcImageMaxTranslateX = Dimension.getScreenWidth(this) - originalGridSize - getResources().getDimensionPixelOffset(R.dimen.padding_header)*2;

        gridMargin = Dimension.dp2px(this, 8);
        searchUseTimeMaxTranslateX = Dimension.getScreenWidth(this)-gridMargin-searchUseTimeTextView.getRight()+2*gridMargin+2*targetGridSize;

        srcImageDraweeView.setPivotX(originalGridSize);
        similarImageNumberTextView.setPivotX(originalGridSize);
        searchUseTimeTextView.setPivotX(originalGridSize);

        headerHeight = originalGridSize + getResources().getDimensionPixelOffset(R.dimen.padding_header);

        View placeHolderView = new View(this);
        placeHolderView.setMinimumHeight(((int) headerHeight));
        similarImgGridView.addHeaderView(placeHolderView);

        adapter = new CommonBaseAdapter<Image>(context, R.layout.grid_item_similar_img, datas, SimilarImgGridViewHolder.class);
        SwingBottomInAnimationAdapter animationAdapter = new SwingBottomInAnimationAdapter(adapter);
        animationAdapter.setAbsListView(similarImgGridView);
        similarImgGridView.setAdapter(animationAdapter);

        similarImgGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                animate();
//                animateSimilarImageNumber();
            }
        });


        similarImgGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int headerViewsCount = similarImgGridView.getHeaderViewsCount();
                int calibratedPosition = position - headerViewsCount;
                if (calibratedPosition >= 0 && calibratedPosition < datas.size()) {
                    startSimilarImgDetailActivity(datas.get(calibratedPosition), calibratedPosition, view);
                }

            }
        });
    }

    private void animateSimilarImageNumber() {
        similarImageNumberTextView.setTranslationX(srcImageDraweeView.getTranslationX());
    }

    private void animate() {
        final int scrollY = getScrollY();

        float scaleRatio = (originalGridSize - scrollY) / originalGridSize;
        scaleRatio = clamp(scaleRatio, srcImageMinScaleRatio, 1.0f);
        srcImageDraweeView.setScaleX(scaleRatio);
        srcImageDraweeView.setScaleY(scaleRatio);
        similarImageNumberTextView.setScaleX(scaleRatio);
        similarImageNumberTextView.setScaleY(scaleRatio);
        searchUseTimeTextView.setScaleX(scaleRatio);
        searchUseTimeTextView.setScaleY(scaleRatio);

//        float searchUseTimeTranslateX = clamp(scrollY-(originalGridSize - targetGridSize), 0, searchUseTimeMaxTranslateX);
        float searchUseTimeTranslateX = clamp(scrollY-(originalGridSize -targetGridSize), 0, 2*targetGridSize+2*gridMargin);
        searchUseTimeTextView.setTranslationX(searchUseTimeTranslateX);

        float searchUseTimeVisualLeft = searchUseTimeTextView.getLeft() + searchUseTimeTextView.getTranslationX() + originalGridSize *(1-scaleRatio);
        float simliarImgNumTranslateX = searchUseTimeVisualLeft - gridMargin - similarImageNumberTextView.getRight();
        similarImageNumberTextView.setTranslationX(simliarImgNumTranslateX);

        float similarImageNumVisualLeft = searchUseTimeVisualLeft - originalGridSize * scaleRatio - gridMargin;
        float srcImageTranslateX = similarImageNumVisualLeft - gridMargin - srcImageDraweeView.getRight();
        srcImageDraweeView.setTranslationX(srcImageTranslateX);

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
    private void startSimilarImgDetailActivity(Image image, int pos, View itemView) {
        ActivityOptionsCompat options = null;
        if (Build.VERSION.SDK_INT > 21) {
            options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, itemView, getResources().getString(R.string.similar_img_transtion_dest));
        } else {
            options = ActivityOptionsCompat.makeScaleUpAnimation(itemView, 0, 0, itemView.getWidth(), itemView.getHeight());
        }
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


    class SearchSimilarImgAysncTask extends AsyncTask<String, ObjectUtils.Null, Integer> {

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(SimilarImgActivity.this, "正在加载...");
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
                if (responseJsonObject.get("status") != 1) {
                    return 0;
                }
//                JSONArray similarImgUrlJsonArray = responseJsonObject.getJSONArray("data");
                String similarImgUrlsString = responseJsonObject.getString("data");
                Type type = new TypeToken<List<String>>() {
                }.getType();
                List<String> similarImgUrls = gson.fromJson(similarImgUrlsString, type);
                /*
                datas.clear();
                for (String similarImgUrl :similarImgUrls) {
                    Image image = new Image();
                    image.setDownloadUrl(similarImgUrl);
                    datas.add(image);
                }*/
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
                Toast.makeText(context, "抱歉，出错了~", Toast.LENGTH_SHORT).show();
                return;
            }
            adapter.notifyDataSetChanged();
        }
    }
}
