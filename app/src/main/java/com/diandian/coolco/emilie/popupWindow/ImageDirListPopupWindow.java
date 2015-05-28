package com.diandian.coolco.emilie.popupWindow;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.diandian.coolco.emilie.R;
import com.diandian.coolco.emilie.adapter.CommonBaseAdapter;
import com.diandian.coolco.emilie.adapter.LocalImageFolderListViewHolder;
import com.diandian.coolco.emilie.model.ImageFolder;

import java.util.List;

import de.greenrobot.event.EventBus;

public class ImageDirListPopupWindow extends PopupWindow {
    private ListView imageFolderListView;
    private View contentView;
    private List<ImageFolder> datas;

    public ImageDirListPopupWindow(View contentView, int width, int height, List<ImageFolder> datas) {
        super(contentView, width, height);
        this.contentView = contentView;
        this.datas = datas;
        setBackgroundDrawable(new ColorDrawable());
        setFocusable(true);
        initViews();
        initEvents();
    }

    public void initViews() {
        imageFolderListView = (ListView) contentView.findViewById(R.id.lv_image_folder);
        BaseAdapter imageFolderListAdapter = new CommonBaseAdapter<>(contentView.getContext(), R.layout.list_item_local_image_folder, datas, LocalImageFolderListViewHolder.class);
        imageFolderListView.setAdapter(imageFolderListAdapter);
    }

    public void initEvents() {
        imageFolderListView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                EventBus.getDefault().post(datas.get(position));
            }
        });
    }
}
