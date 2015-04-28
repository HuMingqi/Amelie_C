package com.diandian.coolco.emilie.fragment;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.diandian.coolco.emilie.R;
import com.diandian.coolco.emilie.model.ImageFolder;
import com.diandian.coolco.emilie.tmp.CustomGridAdapter;
import com.diandian.coolco.emilie.tmp.ListImageDirPopupWindow;
import com.diandian.coolco.emilie.utility.Preference;
import com.diandian.coolco.emilie.utility.PreferenceKey;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import roboguice.inject.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
public class GalleryFragment extends BaseFragment  implements ListImageDirPopupWindow.OnImageDirSelected {

    @InjectView(R.id.gv_local_img)
    private GridView imgGridView;
    @InjectView(R.id.rl_bottom_bar)
    private RelativeLayout bottomBarRelativeLayout;
    @InjectView(R.id.tv_chosen_dir)
    private TextView chosenDirTextView;
    @InjectView(R.id.tv_dir_img_count)
    private TextView chosenDirImgCountTextView;

    private boolean initialized;


    public GalleryFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gallery, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        init();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

//        init();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            // load data here
            if (!initialized) {
                init();
            }
        }else{
            // fragment is no longer visible
        }
    }


    private void init() {
        initialized = true;

        chosenDirTextView.setCompoundDrawables(null, null, new IconDrawable(context, Iconify.IconValue.md_signal_cellular_4_bar)
                .colorRes(R.color.ab_icon)
                .sizeDp(14), null);
//        chosenDirTextView.setCompoundDrawablePadding();

        DisplayMetrics outMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        screenHeight = outMetrics.heightPixels;

        getImages();
        initEvent();
    }

    private ProgressDialog mProgressDialog;

    /**
     * 所有的图片~!!!!the data for grid adapter
     */
    private List<String> currentFolderImgs;

    private ImageFolder allImgFolder;
    private List<String> allImgs;

//    private GridView imgGridView;
    private CustomGridAdapter mGridAdapter;
    /**
     * 临时的辅助类，用于防止同一个文件夹的多次扫描
     */
    private HashSet<String> mDirPaths = new HashSet<String>();

    /**
     * 扫描拿到所有的图片文件夹~!!!!the data for listview adapter
     */
    private List<ImageFolder> imageFolders = new ArrayList<ImageFolder>();

    /**
     * store current image folder, do nothing when user switch to the same folder
     */
    private ImageFolder currImageFolder;

//    private RelativeLayout bottomBarRelativeLayout;
//
//    private TextView chosenDirTextView;
//    private TextView chosenDirImgCountTextView;
//    int totalCount = 0;

    private int screenHeight;

    private ListImageDirPopupWindow mListImageDirPopupWindow;

    private Handler mHandler = new Handler()
    {
        public void handleMessage(android.os.Message msg)
        {
            mProgressDialog.dismiss();

            // 为View绑定数据
            data2View();

            // 初始化展示文件夹的popupWindw,!!!turn the order, because the 'allImageFloder' need particular treat
            initListDirPopupWindw();
        }
    };



    /**
     * 为View绑定数据
     */
    private void data2View()
    {
        sortImg(allImgs);

        //New an image folder called 'all images'
        allImgFolder = new ImageFolder();
        allImgFolder.setDir("/所有图片");
        allImgFolder.setCount(allImgs.size());
        allImgFolder.setFirstImagePath(allImgs.get(0));
        imageFolders.add(0, allImgFolder);

        currentFolderImgs = new ArrayList<String>();
        currentFolderImgs.addAll(allImgs);
        /**
         * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
         */
        mGridAdapter = new CustomGridAdapter(context, currentFolderImgs, R.layout.grid_item_local_img);
        imgGridView.setAdapter(mGridAdapter);

        currImageFolder = allImgFolder;

        updateBottomBar();
    }


    ;

    /**
     * 初始化展示文件夹的popupWindw
     */
    private void initListDirPopupWindw()
    {
        //sort every folder's images, but don't store it ,because it can be very large
        /*u can't del element in for-each loop, it will arise concurrentModificationException, but iterator.remove is safe
        for (ImageFolder imageFolder : imageFolders){
            if (imageFolder.getDir().equals("/所有图片")){
                continue;
            }

            List<String> imgs = getImgs(imageFolder);

            sortImg(imgs);

            imageFolder.setFirstImagePath(imgs.get(0));
            imageFolder.setCount(imgs.size());
        }
        */
        for (Iterator<ImageFolder> iterator = imageFolders.iterator(); iterator.hasNext(); ) {
            ImageFolder imageFolder = iterator.next();

            //all image is an virtual dir, u can't get images from its dir, skip it
            if (imageFolder.getDir().equals("/所有图片")){
                continue;
            }

            List<String> imgs = getImgs(imageFolder);

            //del the empty dir, I don't how it was create
            if (imgs == null) {
                iterator.remove();
                continue;
            }

            sortImg(imgs);

            imageFolder.setFirstImagePath(imgs.get(0));
            imageFolder.setCount(imgs.size());
        }


        mListImageDirPopupWindow = new ListImageDirPopupWindow(
                ViewGroup.LayoutParams.MATCH_PARENT, (int) (screenHeight * 0.7),
                imageFolders, LayoutInflater.from(context)
                .inflate(R.layout.popup_window_img_dir_list, null));

        mListImageDirPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener()
        {

            @Override
            public void onDismiss()
            {

//                bottomBarRelativeLayout.setBackground(new ColorDrawable(Color.parseColor("#cc000000")));
                chosenDirTextView.setTextColor(Color.parseColor("#e7e7e7"));
                chosenDirImgCountTextView.setTextColor(Color.parseColor("#e7e7e7"));

                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = 1.0f;
                getActivity().getWindow().setAttributes(lp);
            }
        });
        // 设置选择文件夹的回调
        mListImageDirPopupWindow.setOnImageDirSelected(this);


    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState)
//    {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.fragment_gallery);
//
//        DisplayMetrics outMetrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
//        screenHeight = outMetrics.heightPixels;
//
//        initView();
//        getImages();
//        initEvent();
//
//    }

    /**
     * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描，最终获得jpg最多的那个文件夹
     */
    private void getImages()
    {
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED))
        {
            Toast.makeText(context, "暂无外部存储", Toast.LENGTH_SHORT).show();
            return;
        }
        // 显示进度条
//        mProgressDialog = ProgressDialog.show(getActivity(), "正在加载...");
        mProgressDialog = ProgressDialog.show(getActivity(), null, "正在加载...");

        new Thread(new Runnable()
        {
            @Override
            public void run()
            {

                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = getActivity()
                        .getContentResolver();

                // 只查询jpeg和png的图片
                Cursor mCursor = mContentResolver.query(mImageUri, null,
                        MediaStore.Images.Media.MIME_TYPE + "=? or "
                                + MediaStore.Images.Media.MIME_TYPE + "=?",
                        new String[] { "image/jpeg", "image/png" },
                        MediaStore.Images.Media.DATE_MODIFIED);

                allImgs = new ArrayList<String>();

                while (mCursor.moveToNext())
                {
                    // 获取图片的路径
                    String path = mCursor.getString(mCursor
                            .getColumnIndex(MediaStore.Images.Media.DATA));

                    //add to allImgFolder
                    allImgs.add(path);

                    // 获取该图片的父路径名
                    File parentFile = new File(path).getParentFile();
                    if (parentFile == null)
                        continue;
                    String dirPath = parentFile.getAbsolutePath();

                    ImageFolder imageFolder = null;
                    // 利用一个HashSet防止多次扫描同一个文件夹（不加这个判断，图片多起来还是相当恐怖的~~）
                    if (mDirPaths.contains(dirPath))
                    {
                        continue;
                    } else
                    {
                        mDirPaths.add(dirPath);
                        // 初始化imageFloder
                        imageFolder = new ImageFolder();
                        imageFolder.setDir(dirPath);
                    }

                    imageFolders.add(imageFolder);

                }
                mCursor.close();

                // 扫描完成，辅助的HashSet也就可以释放内存了
                mDirPaths = null;

                // 通知Handler扫描图片完成
                mHandler.sendEmptyMessage(0x110);

            }
        }).start();

    }

    /**
     * 初始化View
     */
//    private void initView()
//    {
//        imgGridView = (GridView) findViewById(R.id.id_gridView);
//        chosenDirTextView = (TextView) findViewById(R.id.id_choose_dir);
//        chosenDirImgCountTextView = (TextView) findViewById(R.id.id_total_count);
//
//        bottomBarRelativeLayout = (RelativeLayout) findViewById(R.id.id_bottom_ly);
//
//    }

    private void initEvent()
    {
        /**
         * 为底部的布局设置点击事件，弹出popupWindow
         */
        bottomBarRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListImageDirPopupWindow
                        .setAnimationStyle(R.style.anim_popup_dir);
                mListImageDirPopupWindow.showAsDropDown(bottomBarRelativeLayout, 0, 0);

                // 设置背景颜色变暗
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = .5f;
                getActivity().getWindow().setAttributes(lp);
//                bottomBarRelativeLayout.setBackgroundColor(Color.parseColor("#f9f9f9"));
//                bottomBarRelativeLayout.setBackground(new ColorDrawable(Color.parseColor("#fff9f9f9")));
                chosenDirTextView.setTextColor(Color.parseColor("#ffffff"));
                chosenDirImgCountTextView.setTextColor(Color.parseColor("#ffffff"));
            }
        });

        imgGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //set flag
                Preference.setPrefBoolean(context, PreferenceKey.IS_SRC_IMG_STORAGE_COMPLETED, true);

                imgObtained(currentFolderImgs.get(position));
            }
        });
    }

    @Override
    public void imgDirSelected(ImageFolder folder)
    {
        if (folder.equals(currImageFolder)){
//            bottomBarRelativeLayout.setBackgroundColor(Color.parseColor("#cc000000"));
            mListImageDirPopupWindow.dismiss();

            return;
        }
        currImageFolder = folder;

        List<String> selectedImgs = null;
        if (folder.getDir().equals("/所有图片")){
            selectedImgs = allImgs;
        } else {
            selectedImgs = getImgs(folder);
            sortImg(selectedImgs);
        }

        currentFolderImgs.clear();
        currentFolderImgs.addAll(selectedImgs);
        mGridAdapter.notifyDataSetChanged();

        /**
         * 可以看到文件夹的路径和图片的路径分开保存，极大的减少了内存的消耗；
         */
//		mGridAdapter = new CustomGridAdapter(getApplicationContext(), currentFolderImgs,
//				R.layout.grid_item_local_img);
//		imgGridView.setAdapter(mGridAdapter);
//        chosenDirImgCountTextView.setText(folder.getCount() + "张");
//        chosenDirTextView.setText(folder.getName());
        updateBottomBar();

        mListImageDirPopupWindow.dismiss();

    }

    private List<String> getImgs(ImageFolder folder) {
        List<String> imgs = new ArrayList<String>();
        String[] imgNames = new File(folder.getDir()).list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                if (filename.endsWith(".jpg") || filename.endsWith(".png")
                        || filename.endsWith(".jpeg"))
                    return true;
                return false;
            }
        });

        // It seems there would be empty dir
        if (imgNames == null) {
            return null;
        }

        String dirPrefix = folder.getDir() + "/";
        for (int i = 0; i < imgNames.length; i++) {
            imgs.add(dirPrefix + imgNames[i]);
        }
        return imgs;
    }

    private void sortImg(List<String> imgs) {
        //Sort the image, descending in time.
        final Map<String, Long> imgLastModifiedTime = new HashMap<String, Long>();
        for (String img : imgs){
            File file = new File(img);
            imgLastModifiedTime.put(img, file.lastModified());
        }
        Collections.sort(imgs, new Comparator<String>() {
            @Override
            public int compare(String lhs, String rhs) {
                long rTime = imgLastModifiedTime.get(rhs);
                long lTime = imgLastModifiedTime.get(lhs);
                if (rTime < lTime) {
                    return -1;
                } else if (rTime == lTime) {
                    return 0;
                } else {
                    return 1;
                }

            }
        });
    }

    private void updateBottomBar() {
        chosenDirTextView.setText(currImageFolder.getName());
        chosenDirImgCountTextView.setText(currImageFolder.getCount() + "张");
    }

}
