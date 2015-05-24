package com.diandian.coolco.emilie.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.diandian.coolco.emilie.R;
import com.diandian.coolco.emilie.adapter.CommonBaseAdapter;
import com.diandian.coolco.emilie.adapter.SimilarImgGridViewHolder;
import com.diandian.coolco.emilie.model.Image;
import com.diandian.coolco.emilie.utility.DBHelper;
import com.diandian.coolco.emilie.utility.ExtraDataName;
import com.etsy.android.grid.StaggeredGridView;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;

import java.util.ArrayList;
import java.util.List;

import roboguice.inject.InjectView;

public class CollectionActivity extends DbSupportBaseActivity {

    @InjectView(R.id.sgv_collection)
    private StaggeredGridView collectionGridView;
    private CommonBaseAdapter<Image> adapter;
    private List<Image> datas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);
        init();
    }

    public void init(){
        getData();

        adapter = new CommonBaseAdapter<Image>(context, R.layout.grid_item_similar_img, datas, SimilarImgGridViewHolder.class);
        SwingBottomInAnimationAdapter animationAdapter = new SwingBottomInAnimationAdapter(adapter);
        animationAdapter.setAbsListView(collectionGridView);
        collectionGridView.setAdapter(animationAdapter);
        collectionGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int headerViewsCount = collectionGridView.getHeaderViewsCount();
                int calibratedPosition = position - headerViewsCount;
                if (calibratedPosition >= 0 && calibratedPosition < datas.size()) {
                    startSimilarImgDetailActivity(datas.get(calibratedPosition), calibratedPosition, view);
                }

            }
        });
    }

    public void getData(){
        final DBHelper dbHelper = getDBHelper();
        datas = dbHelper.getImage();
        dbHelper.close();
        for (int i = 0; i < datas.size(); i++) {
            datas.get(i).setCollected(true);
        }
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
            startActivityForResult(intent, 0, options.toBundle());
        } else {
            startActivityForResult(intent, 0);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == RESULT_OK && data != null){
            ArrayList<Image> imagesNeedRemove = data.getParcelableArrayListExtra(ExtraDataName.REMOVE_IMGS);
            if (imagesNeedRemove != null) {
                datas.removeAll(imagesNeedRemove);
//                adapter.notifyDataSetChanged();
                adapter = new CommonBaseAdapter<Image>(context, R.layout.grid_item_similar_img, datas, SimilarImgGridViewHolder.class);
                SwingBottomInAnimationAdapter animationAdapter = new SwingBottomInAnimationAdapter(adapter);
                animationAdapter.setAbsListView(collectionGridView);
                collectionGridView.setAdapter(animationAdapter);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_collection, menu);
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
}
