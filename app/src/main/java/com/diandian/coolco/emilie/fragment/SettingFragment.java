package com.diandian.coolco.emilie.fragment;


import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.diandian.coolco.emilie.R;
import com.diandian.coolco.emilie.activity.FeedbackActivity;
import com.diandian.coolco.emilie.dialog.ProgressDialog;
import com.diandian.coolco.emilie.utility.Dimension;
import com.diandian.coolco.emilie.utility.IntentUtil;
import com.diandian.coolco.emilie.utility.SuperToastUtil;
import com.diandian.coolco.emilie.utility.Url;

import roboguice.inject.InjectView;


/**
 * A simple {@link Fragment} subclass.
 */
public class SettingFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    @InjectView(R.id.ls_setting)
    private ListView settingListView;

    private BaseAdapter adapter;

    public SettingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_setting, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        View headerView = new View(getActivity());
        View footerView = new View(getActivity());
        headerView.setMinimumHeight((int) Dimension.dp2px(getActivity(), 8));
        footerView.setMinimumHeight((int) Dimension.dp2px(getActivity(), 8));
        settingListView.addHeaderView(headerView);
        settingListView.addFooterView(footerView);
        settingListView.setHeaderDividersEnabled(false);
        settingListView.setFooterDividersEnabled(false);
        String[] strings = {"欢迎页", "用户协议", "常见问题", "告诉朋友", "意见反馈", "版本升级", "给我们评分", "开源许可", "关于我们"};
        adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_setting, strings);
        settingListView.setAdapter(adapter);
        settingListView.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        int calibratedPosition = position - settingListView.getHeaderViewsCount();
        switch (calibratedPosition) {
            case 0: {
                break;
            }
            case 1: {
                IntentUtil.startWebActivity(getActivity(), Url.PROTOCOL);
                break;
            }
            case 2: {
                IntentUtil.startWebActivity(getActivity(), Url.FAQ);
                break;
            }
            case 3: {
                String chooserTitle = "分享";
                String subject = "Amelie";
                String text = "我正在使用爱美丽，请你也来试用~balabala~[http://myron.sinaapp.com/list]";
                IntentUtil.startShareActivity(getActivity(), chooserTitle, subject, text);
                break;
            }
            case 4: {
                Intent intent = new Intent(getActivity(), FeedbackActivity.class);
                getActivity().startActivity(intent);
                break;
            }
            case 5: {
                final Dialog dialog = ProgressDialog.show(getActivity(), "正在检查新版本...");
                settingListView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                        SuperToastUtil.showToast(getActivity(), "当前已是最新版本");
                    }
                }, 1000);
                break;
            }
            case 6: {
                Uri uri = Uri.parse("market://details?id=" + getActivity().getPackageName());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            }
            case 7:
                IntentUtil.startWebActivity(getActivity(), Url.LICENSE);
                break;
            case 8:
                IntentUtil.startWebActivity(getActivity(), Url.ABOUT);
                break;
            case 9:
                break;
        }
    }
}
