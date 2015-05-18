package com.diandian.coolco.emilie.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
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

    private List<Image> datas;
    private BaseAdapter adapter;

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

        datas = new ArrayList<Image>();

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

        TextView clothesNumTextView = new TextView(this);
        clothesNumTextView.setText("找到2,123件服饰");
        clothesNumTextView.setTextSize(14);
        clothesNumTextView.setTextColor(getResources().getColor(android.R.color.secondary_text_light_nodisable));
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
                int headerViewsCount = similarImgGridView.getHeaderViewsCount();
                int calibratedPosition = position - headerViewsCount;
                if (calibratedPosition >= 0 && calibratedPosition < datas.size()){
                    startSimilarImgDetailActivity(datas.get(calibratedPosition), calibratedPosition, view);
                }

            }
        });
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
