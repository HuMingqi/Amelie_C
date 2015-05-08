package com.diandian.coolco.emilie.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.CardView;
import android.util.Size;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.diandian.coolco.emilie.R;
import com.diandian.coolco.emilie.adapter.CommonBaseAdapter;
import com.diandian.coolco.emilie.dialog.ProgressDialog;
import com.diandian.coolco.emilie.model.Image;
import com.diandian.coolco.emilie.utility.Dimension;
import com.diandian.coolco.emilie.utility.ExtraDataName;
import com.diandian.coolco.emilie.utility.NetHelper;
import com.diandian.coolco.emilie.utility.Url;
import com.diandian.coolco.emilie.widget.DetectSwipeGestureRelativeLayout;
import com.diandian.coolco.emilie.widget.NumberProgressCircle;
import com.diandian.coolco.emilie.widget.PlaceHolderImageView;
import com.etsy.android.grid.StaggeredGridView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

import org.apache.commons.lang.ObjectUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import roboguice.inject.InjectView;

public class SimilarImgActivity extends BaseActivity {

//    @InjectView(R.id.root)
//    private DetectSwipeGestureRelativeLayout rootView;

    @InjectView(R.id.sgv_similar_img)
    private StaggeredGridView similarImgGridView;

    private List<Image> datas;
    private BaseAdapter adapter;
    private Menu mainMenu;
    private static DisplayImageOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_similar_img);

        init();
    }

    private void init() {
//        Intent intent = getIntent();
//        String croppedSrcImgPath = intent.getStringExtra(ExtraDataName.CROPPED_SRC_IMG_PATH);
//        new SearchSimilarImgAysncTask().execute(croppedSrcImgPath);

        options = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.drawable.ic_stub)
//                .showImageForEmptyUri(R.drawable.ic_empty)
//                .showImageOnFail(R.drawable.ic_error)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .displayer(new FadeInBitmapDisplayer(300))
                .build();

        datas = new ArrayList<Image>();

        // this is very bad coding style, it may call memory leak
        Image image1 = new Image();
        Image image2 = new Image();
        Image image3 = new Image();
        Image image4 = new Image();
        Image image5 = new Image();
        image1.setDownloadUrl("http://myron-mydomain.stor.sinaapp.com/1114163041364330.png");
        image2.setDownloadUrl("http://myron-mydomain.stor.sinaapp.com/14148082703290.png");
        image3.setDownloadUrl("http://myron-mydomain.stor.sinaapp.com/14148082653051.png");
        image4.setDownloadUrl("http://myron-mydomain.stor.sinaapp.com/14148324678681.png");
        image5.setDownloadUrl("http://myron-mydomain.stor.sinaapp.com/14148324678680.png");
        image1.setSize(new Point(540, 405));
        image2.setSize(new Point(500, 331));
        image3.setSize(new Point(1440, 1280));
        image4.setSize(new Point(736, 1003));
        image5.setSize(new Point(1080, 1440));
        datas.add(image1);
        datas.add(image2);
        datas.add(image3);
        datas.add(image4);
        datas.add(image5);
        /*
        datas.add(new Image() {
            {
                setDownloadUrl("http://myron-mydomain.stor.sinaapp.com/1114163041364330.png");
            }
        });
        datas.add(new Image() {
            {
                setDownloadUrl("http://myron-mydomain.stor.sinaapp.com/14148082703290.png");
            }
        });
        datas.add(new Image() {
            {
                setDownloadUrl("http://myron-mydomain.stor.sinaapp.com/14148082653051.png");
            }
        });
        datas.add(new Image() {
            {
                setDownloadUrl("http://myron-mydomain.stor.sinaapp.com/14148324678681.png");
            }
        });
        datas.add(new Image() {
            {
                setDownloadUrl("http://myron-mydomain.stor.sinaapp.com/14148324678680.png");
            }
        });

        datas.add(new Image() {
            {
                setDownloadUrl("http://myron-mydomain.stor.sinaapp.com/14148502153040.png");
            }
        });
        datas.add(new Image() {
            {
                setDownloadUrl("http://myron-mydomain.stor.sinaapp.com/14148503848361.png");
            }
        });
        datas.add(new Image() {
            {
                setDownloadUrl("http://myron-mydomain.stor.sinaapp.com/14148503848362.png");
            }
        });
        */
        TextView clothesNumTextView = new TextView(this);
        clothesNumTextView.setText("找到2,123件服饰");
        clothesNumTextView.setTextSize(14);
        clothesNumTextView.setTextColor(getResources().getColor(android.R.color.secondary_text_dark_nodisable));
        int padding = (int) Dimension.dp2px(this, 12);
        clothesNumTextView.setPadding(padding, padding, padding, padding);
        similarImgGridView.addHeaderView(clothesNumTextView);

        adapter = new CommonBaseAdapter<Image>(context, R.layout.grid_item_similar_img, datas, SimilarImgGridViewHolder.class);
        SwingBottomInAnimationAdapter animationAdapter = new SwingBottomInAnimationAdapter(adapter);
        animationAdapter.setAbsListView(similarImgGridView);
        similarImgGridView.setAdapter(animationAdapter);


        similarImgGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                int headerViewsCount = ((ListView) parent).getHeaderViewsCount();
                int headerViewsCount = similarImgGridView.getHeaderViewsCount();
                int calibratedPosition = position - headerViewsCount;
                if (calibratedPosition >= 0 && calibratedPosition < datas.size()){
                    startSimilarImgDetailActivity(datas.get(calibratedPosition), calibratedPosition, view);
                }
//                if (position >= headerViewsCount && position < datas.size() + headerViewsCount) {
//                    startSimilarImgDetailActivity(datas.get(position - headerViewsCount), position - headerViewsCount, view);
//                }
            }
        });

//        rootView.setListener(new DetectSwipeGestureRelativeLayout.SwipeRightListener() {
//            @Override
//            public void onSwipeRight() {
//                finish();
//            }
//        });
    }
//
//    public void onEvent(String noEvent){
//
//    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void startSimilarImgDetailActivity(Image image, int pos, View itemView) {
        ActivityOptionsCompat options = null;
        if (Build.VERSION.SDK_INT > 21) {
            options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, similarImgGridView.getChildAt(pos), getResources().getString(R.string.similar_img_transtion_dest));
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

        menu.findItem(R.id.ab_button_list).setIcon(
                new IconDrawable(this, Iconify.IconValue.md_more_vert)
                        .colorRes(R.color.ab_icon)
                        .actionBarSize());
        mainMenu = menu;

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

    static class SimilarImgGridViewHolder extends CommonBaseAdapter.CommonViewHolder<Image> {
        private ImageView imageView;
        private NumberProgressCircle numberProgressCircle;
//        private IconDrawable defaultDrawable;

        public SimilarImgGridViewHolder(View convertView) {
//            ((CardView)convertView).setPreventCornerOverlap(false);
//            imageView = (ImageView) convertView.findViewById(R.id.iv_similar_img_grid_item);
            imageView = (ImageView) convertView.findViewById(R.id.iv_similar_img_grid_item);
            numberProgressCircle = (NumberProgressCircle) convertView.findViewById(R.id.npc_similar_img_grid_item);
//            imageView.setMinimumHeight(imageView.getHeight());
//            imageView.setImageResource(R.drawable.similar_img_grid_item_empty_grey);
//            defaultDrawable = new IconDrawable(convertView.getContext(), Iconify.IconValue.md_filter_drama).color(Color.parseColor("#474747")).sizeDp(120);
//            imageView.setImageDrawable(defaultDrawable);
        }

        @Override
        public void setItem(Image item) {
//            Drawable placeholderDrawable = new ColorDrawable(Color.parseColor("#e7e7e7"));
//            placeholderDrawable.setBounds(0, 0, item.getSize().x, item.getSize().y);
//            imageView.setImageDrawable(placeholderDrawable);
//            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
//            Bitmap.Config conf = Bitmap.Config.RGB_565;
//            final Bitmap bitmap = Bitmap.createBitmap(item.getSize().x, item.getSize().y, conf);
//            bitmap.eraseColor(imageView.getContext().getResources().getColor(R.color.cardview_dark_background));
//            bitmap.eraseColor(Color.parseColor("#2b3132"));
//            bitmap.reconfigure(item.getSize().x, item.getSize().y, conf);
//            imageView.setImageBitmap(bitmap);
            ((PlaceHolderImageView) imageView).setDrawableWidth(item.getSize().x);
            ((PlaceHolderImageView) imageView).setDrawableHeight(item.getSize().y);

            ImageLoader.getInstance().displayImage(item.getDownloadUrl(), imageView, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {
                    numberProgressCircle.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                    numberProgressCircle.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    numberProgressCircle.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingCancelled(String s, View view) {
                    numberProgressCircle.setVisibility(View.GONE);
                }
            }, new ImageLoadingProgressListener() {
                @Override
                public void onProgressUpdate(String s, View view, int current, int total) {
                    numberProgressCircle.setProgress(current * 1.0f / total);
                }
            });
        }
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

    /**
     * open or close the more menu when pressing menu hardware button
     */
    @Override
    public boolean onKeyDown(int keycode, KeyEvent e) {
        switch (keycode) {
            case KeyEvent.KEYCODE_MENU:
                mainMenu.performIdentifierAction(R.id.ab_button_list, 0);
                return true;
        }

        return super.onKeyDown(keycode, e);
    }
}
