package com.diandian.coolco.emilie.fragment;


import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.diandian.coolco.emilie.R;
import com.diandian.coolco.emilie.activity.SrcImgCropActivity;
import com.diandian.coolco.emilie.adapter.CommonBaseAdapter;
import com.diandian.coolco.emilie.adapter.LocalImgGridItemViewHolder;
import com.diandian.coolco.emilie.dialog.ProgressDialog;
import com.diandian.coolco.emilie.model.ImageFolder;
import com.diandian.coolco.emilie.tmp.ListImageDirPopupWindow;
import com.diandian.coolco.emilie.utility.Dimension;
import com.diandian.coolco.emilie.utility.Event;
import com.diandian.coolco.emilie.utility.ExtraDataName;
import com.diandian.coolco.emilie.utility.MyApplication;
import com.diandian.coolco.emilie.utility.Preference;
import com.diandian.coolco.emilie.utility.PreferenceKey;
import com.diandian.coolco.emilie.utility.SuperToastUtil;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;
import com.nhaarman.listviewanimations.appearance.AnimationAdapter;
import com.nhaarman.listviewanimations.appearance.simple.ScaleInAnimationAdapter;

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

import de.greenrobot.event.EventBus;
import de.greenrobot.event.util.AsyncExecutor;
import roboguice.inject.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
public class GalleryFragment extends BaseFragment implements ListImageDirPopupWindow.OnImageDirSelected {

    @InjectView(R.id.gv_local_img)
    private GridView localImgGridView;
    @InjectView(R.id.rl_bottom_bar)
    private RelativeLayout bottomBarRelativeLayout;
    @InjectView(R.id.tv_chosen_dir)
    private TextView chosenDirTextView;
    @InjectView(R.id.tv_dir_img_count)
    private TextView chosenDirImgCountTextView;

    private boolean initialized;
    private ProgressDialog progressDialog;
    /**
     * the data for grid adapter
     */
    private List<String> currentFolderImgs;
    private ImageFolder allImgFolder;
    private List<String> allImgPaths;
    private BaseAdapter gridAdapter;
    private List<ImageFolder> imageFolders;
    /**
     * store current image folder, do nothing when user switch to the same folder
     */
    private ImageFolder currImageFolder;
    private ListImageDirPopupWindow mListImageDirPopupWindow;

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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser && !initialized) {
            init();
            initialized = true;
        }
    }

    private void init() {

        chosenDirTextView.setCompoundDrawables(null, null, new IconDrawable(context, Iconify.IconValue.md_signal_cellular_4_bar)
                .colorRes(R.color.ab_icon)
                .sizeDp(14), null);

        scanImages();

        initEvent();
    }

    private void data2view() {
        sortImg(allImgPaths);

        //New an image folder called 'all images'
        allImgFolder = new ImageFolder();
        allImgFolder.setDir("/所有图片");
        allImgFolder.setCount(allImgPaths.size());

        if (allImgPaths.size() > 0) {
            allImgFolder.setFirstImagePath(allImgPaths.get(0));
        } else {
            allImgFolder.setFirstImagePath(null);
        }
        imageFolders.add(0, allImgFolder);

        currentFolderImgs = new ArrayList<String>();
        currentFolderImgs.addAll(allImgPaths);
        gridAdapter = new CommonBaseAdapter<String>(getActivity(), R.layout.grid_item_local_img, currentFolderImgs, LocalImgGridItemViewHolder.class);
//        SwingBottomInAnimationAdapter animationAdapter = new SwingBottomInAnimationAdapter(gridAdapter);
        AnimationAdapter animationAdapter = new ScaleInAnimationAdapter(gridAdapter);
        animationAdapter.setAbsListView(localImgGridView);
        localImgGridView.setAdapter(animationAdapter);

        currImageFolder = allImgFolder;

        updateBottomBar();
    }


    private void initImgFoldersPopupWindow() {
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
            if (imageFolder.getDir().equals("/所有图片")) {
                continue;
            }

            List<String> imgs = getImgs(imageFolder);

            //del the empty dir, I don't how it was create
            if (imgs == null) {
                iterator.remove();
                continue;
            }

            sortImg(imgs);

            if (imgs.size() > 0 ) {
                imageFolder.setFirstImagePath(imgs.get(0));
            } else {
                imageFolder.setFirstImagePath(null);
            }
            imageFolder.setCount(imgs.size());
        }


        mListImageDirPopupWindow = new ListImageDirPopupWindow(
                ViewGroup.LayoutParams.MATCH_PARENT, (int) (Dimension.getScreenHeight(getActivity()) * 0.7),
                imageFolders, LayoutInflater.from(context)
                .inflate(R.layout.popup_window_img_dir_list, null));
        mListImageDirPopupWindow.setAnimationStyle(R.style.anim_popup_dir);
        mListImageDirPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                chosenDirTextView.setTextColor(Color.parseColor("#e7e7e7"));
                chosenDirImgCountTextView.setTextColor(Color.parseColor("#e7e7e7"));
                setWindowAlpha(1.0f);
            }
        });
        mListImageDirPopupWindow.setOnImageDirSelected(this);
    }

    private void setWindowAlpha(float alpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = alpha;
        getActivity().getWindow().setAttributes(lp);
    }


    private void scanImages() {
        progressDialog = ProgressDialog.show(getActivity(), "正在加载...");
        ((MyApplication) getActivity().getApplication()).getAsyncExecutor().execute(new ScanImageTask());
    }

    public void onEventMainThread(Event.NoExternalStorageException noExternalStorageException) {
        SuperToastUtil.showToast(context, "未发现外部存储");
    }

    public void onEventMainThread(Event.ScanImgCompletedEvent scanImgCompletedEvent) {
        onScanImgCompleted();
    }

    public void onScanImgCompleted() {
        data2view();

        initImgFoldersPopupWindow();

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void initEvent() {

        bottomBarRelativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mListImageDirPopupWindow.showAsDropDown(bottomBarRelativeLayout, 0, 0);

                setWindowAlpha(0.5f);
                chosenDirTextView.setTextColor(Color.parseColor("#ffffff"));
                chosenDirImgCountTextView.setTextColor(Color.parseColor("#ffffff"));
            }
        });

        localImgGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startSrcImgCropActivity(currentFolderImgs.get(position), view);
//                imgObtained(currentFolderImgs.get(position));
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void startSrcImgCropActivity(String srcImgPath, View itemView) {
        //set flag
        Preference.setPrefBoolean(context, PreferenceKey.IS_SRC_IMG_STORAGE_COMPLETED, true);

        //set animation
        ActivityOptionsCompat options = null;
        if (Build.VERSION.SDK_INT > 21) {
            options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), itemView, getResources().getString(R.string.similar_img_transtion_dest));
        } else {
            options = ActivityOptionsCompat.makeScaleUpAnimation(itemView, 0, 0, itemView.getWidth(), itemView.getHeight());
        }

        //add extra data
        Intent intent = new Intent(getActivity(), SrcImgCropActivity.class);
        intent.putExtra(ExtraDataName.SRC_IMG_PATH, srcImgPath);

        if (Build.VERSION.SDK_INT > 15) {
            getActivity().startActivity(intent, options.toBundle());
        } else {
            getActivity().startActivity(intent);
        }
    }

    @Override
    public void imgDirSelected(ImageFolder selectedFolder) {
        mListImageDirPopupWindow.dismiss();

        if (selectedFolder.equals(currImageFolder)) {
            return;
        }
        currImageFolder = selectedFolder;

        List<String> selectedImgs;
        if (selectedFolder.getDir().equals("/所有图片")) {
            selectedImgs = allImgPaths;
        } else {
            selectedImgs = getImgs(selectedFolder);
            sortImg(selectedImgs);
        }

        currentFolderImgs.clear();
        currentFolderImgs.addAll(selectedImgs);
        gridAdapter.notifyDataSetChanged();

        updateBottomBar();
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
        for (String img : imgs) {
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

    class ScanImageTask implements AsyncExecutor.RunnableEx {

        @Override
        public void run() throws Event.NoExternalStorageException {
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                throw new Event.NoExternalStorageException();
            }

            Uri externalContentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            ContentResolver contentResolver = getActivity().getApplicationContext().getContentResolver();

            Cursor cursor = contentResolver.query(externalContentUri, null,
                    MediaStore.Images.Media.MIME_TYPE + "=? or "
                            + MediaStore.Images.Media.MIME_TYPE + "=? or "
                            + MediaStore.Images.Media.MIME_TYPE + "=?",
                    new String[]{"image/jpeg", "image/png", "image/jpg"},
                    MediaStore.Images.Media.DATE_MODIFIED);

            allImgPaths = new ArrayList<>();
            imageFolders = new ArrayList<>();

            // used to avoid adding the same image folder multiple times
            HashSet<String> imgDirPathSet = new HashSet<>();

            while (cursor.moveToNext()) {
                String imgPath = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));

                //add to allImgFolder
                allImgPaths.add(imgPath);

                File parentFile = new File(imgPath).getParentFile();
                if (parentFile == null)
                    continue;
                String dirPath = parentFile.getAbsolutePath();

                if (!imgDirPathSet.contains(dirPath)) {
                    ImageFolder imageFolder = new ImageFolder();
                    imageFolder.setDir(dirPath);
                    imageFolders.add(imageFolder);

                    imgDirPathSet.add(dirPath);
                }
            }
            cursor.close();

            EventBus.getDefault().post(new Event.ScanImgCompletedEvent());
        }
    }

}
