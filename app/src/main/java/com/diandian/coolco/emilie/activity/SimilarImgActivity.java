package com.diandian.coolco.emilie.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Toast;

import com.diandian.coolco.emilie.R;
import com.diandian.coolco.emilie.adapter.CommonBaseAdapter;
import com.diandian.coolco.emilie.model.Image;
import com.diandian.coolco.emilie.utility.ExtraDataName;
import com.diandian.coolco.emilie.utility.NetHelper;
import com.diandian.coolco.emilie.utility.Url;
import com.etsy.android.grid.StaggeredGridView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.commons.lang.ObjectUtils;
import org.json.JSONArray;
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
        Intent intent = getIntent();
        String croppedSrcImgPath = intent.getStringExtra(ExtraDataName.CROPPED_SRC_IMG_PATH);
        new SearchSimilarImgAysncTask().execute(croppedSrcImgPath);

        datas = new ArrayList<Image>();
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
//        adapter = new CommonBaseAdapter<Image>(this, R.layout.grid_item_similar_img, datas, SimilarImgGridViewHolder.class);
        adapter = new CommonBaseAdapter<Image>(context, R.layout.grid_item_similar_img, datas, SimilarImgGridViewHolder.class);
        similarImgGridView.setAdapter(adapter);
        similarImgGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                 startSimilarImgDetailActivity(datas.get(position));
            }
        });
    }

    private void startSimilarImgDetailActivity(Image image) {
        Intent intent = new Intent(this, SimilarImgDetailActivity.class);
        intent.putExtra(ExtraDataName.SIMILAR_IMG, image);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_similar_img, menu);
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

    static class SimilarImgGridViewHolder extends CommonBaseAdapter.CommonViewHolder<Image> {
        private ImageView imageView;
//        private IconDrawable defaultDrawable;

        public SimilarImgGridViewHolder(View convertView) {
//            ((CardView)convertView).setPreventCornerOverlap(false);
//            imageView = (ImageView) convertView.findViewById(R.id.iv_similar_img_grid_item);
            imageView = (ImageView) convertView;
//            defaultDrawable = new IconDrawable(convertView.getContext(), Iconify.IconValue.md_filter_drama).color(Color.parseColor("#474747")).sizeDp(120);
//            imageView.setImageDrawable(defaultDrawable);
        }

        @Override
        public void setItem(Image item) {
            ImageLoader.getInstance().displayImage(item.getDownloadUrl(), imageView);
        }
    }

    class SearchSimilarImgAysncTask extends AsyncTask<String, ObjectUtils.Null, Integer>{

        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(SimilarImgActivity.this, null, "正在加载...");
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
                if (responseJsonObject.get("status") != 1){
                    return 0;
                }
//                JSONArray similarImgUrlJsonArray = responseJsonObject.getJSONArray("data");
                String similarImgUrlsString = responseJsonObject.getString("data");
                Type type = new TypeToken<List<String>>(){}.getType();
                List<String> similarImgUrls = gson.fromJson(similarImgUrlsString, type);
                datas.clear();
                for (String similarImgUrl :similarImgUrls) {
                    Image image = new Image();
                    image.setDownloadUrl(similarImgUrl);
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
            if (result != 1){
                Toast.makeText(context, "抱歉，出错了~", Toast.LENGTH_SHORT).show();
                return;
            }
            adapter.notifyDataSetChanged();
        }
    }
}
