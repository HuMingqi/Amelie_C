package com.diandian.coolco.emilie.fragment;


import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.ActivityOptionsCompat;
import android.test.mock.MockApplication;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.diandian.coolco.emilie.R;
import com.diandian.coolco.emilie.activity.SimilarImgDetailActivity;
import com.diandian.coolco.emilie.adapter.CommonBaseAdapter;
import com.diandian.coolco.emilie.adapter.SimilarImgGridViewHolder;
import com.diandian.coolco.emilie.dialog.ProgressDialog;
import com.diandian.coolco.emilie.model.Image;
import com.diandian.coolco.emilie.utility.DBHelper;
import com.diandian.coolco.emilie.utility.Event;
import com.diandian.coolco.emilie.utility.ExtraDataName;
import com.diandian.coolco.emilie.utility.Logcat;
import com.diandian.coolco.emilie.utility.MyApplication;
import com.etsy.android.grid.StaggeredGridView;
import com.nhaarman.listviewanimations.appearance.simple.SwingBottomInAnimationAdapter;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.util.AsyncExecutor;
import roboguice.inject.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
public class CollectionFragment extends BaseFragment {

    @InjectView(R.id.sgv_collection_fragment)
    private StaggeredGridView collectionGridView;

    private CommonBaseAdapter<Image> adapter;
    private List<Image> datas;
    private ProgressDialog progressDialog;


    public CollectionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_collection, container, false);
        collectionGridView = (StaggeredGridView) rootView;
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getCollection();
    }

    public void getCollection(){
        progressDialog = ProgressDialog.show(getActivity(), "正在加载...");
        ((MyApplication) getActivity().getApplication()).getAsyncExecutor().execute(new GetCollectionTask());
    }

    private void setUpGridView() {
        adapter = new CommonBaseAdapter<Image>(getActivity(), R.layout.grid_item_similar_img, datas, SimilarImgGridViewHolder.class);
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

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void startSimilarImgDetailActivity(Image image, int pos, View itemView) {
        ActivityOptionsCompat options = null;
        if (Build.VERSION.SDK_INT > 21) {
            options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), itemView, getActivity().getResources().getString(R.string.similar_img_transtion_dest));
        } else {
            options = ActivityOptionsCompat.makeScaleUpAnimation(itemView, 0, 0, itemView.getWidth(), itemView.getHeight());
        }
        Intent intent = new Intent(getActivity(), SimilarImgDetailActivity.class);
        intent.putParcelableArrayListExtra(ExtraDataName.SIMILAR_IMGS, (ArrayList<Image>) datas);
        intent.putExtra(ExtraDataName.SIMILAR_IMG_INIT_POS, pos);
        if (Build.VERSION.SDK_INT > 15) {
            getActivity().startActivity(intent, options.toBundle());
        } else {
            getActivity().startActivity(intent);
        }
    }

    public void onEventMainThread(Event.GetCollectionCompletedEvent event){
        progressDialog.dismiss();
        setUpGridView();
    }

    class GetCollectionTask implements AsyncExecutor.RunnableEx{

        @Override
        public void run() throws Exception {
            final DBHelper dbHelper = new DBHelper(getActivity().getApplicationContext());
            datas = dbHelper.getImage();
            dbHelper.close();
            for (int i = 0; i < datas.size(); i++) {
                datas.get(i).setCollected(true);
            }
            EventBus.getDefault().post(new Event.GetCollectionCompletedEvent());
        }
    }
}
