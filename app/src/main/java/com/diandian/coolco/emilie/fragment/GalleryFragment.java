package com.diandian.coolco.emilie.fragment;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
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
import com.diandian.coolco.emilie.activity.SimilarImgActivity;
import com.diandian.coolco.emilie.activity.SrcImgCropActivity;
import com.diandian.coolco.emilie.adapter.CommonBaseAdapter;
import com.diandian.coolco.emilie.adapter.LocalImgGridItemViewHolder;
import com.diandian.coolco.emilie.dialog.ProgressDialog;
import com.diandian.coolco.emilie.model.ImageFolder;
import com.diandian.coolco.emilie.popupWindow.ImageDirListPopupWindow;
import com.diandian.coolco.emilie.utility.Dimension;
import com.diandian.coolco.emilie.utility.Event;
import com.diandian.coolco.emilie.utility.ExtraDataName;
import com.diandian.coolco.emilie.utility.MyApplication;
import com.diandian.coolco.emilie.utility.Preference;
import com.diandian.coolco.emilie.utility.PreferenceKey;
import com.diandian.coolco.emilie.utility.SuperToastUtil;
import com.diandian.coolco.emilie.utility.TranstionAnimationUtil;
import com.diandian.coolco.emilie.utility.StartCropping;
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

import de.greenrobot.event.EventBus;
import de.greenrobot.event.util.AsyncExecutor;
import roboguice.inject.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
public class GalleryFragment extends BaseFragment {

    @InjectView(R.id.gv_local_img)
    private GridView localImgGridView;
    @InjectView(R.id.rl_bottom_bar)
    private RelativeLayout bottomBarRelativeLayout;
    @InjectView(R.id.tv_chosen_dir)
    private TextView chosenDirTextView;
    @InjectView(R.id.tv_dir_img_count)
    private TextView chosenDirImgCountTextView;

    private Dialog progressDialog;
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
    private ImageDirListPopupWindow mImageDirListPopupWindow;

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
        init();
    }


    private void init() {
        chosenDirTextView.setCompoundDrawables(null, null, new IconDrawable(getActivity().getApplicationContext(), Iconify.IconValue.md_signal_cellular_4_bar)
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
//        AnimationAdapter animationAdapter = new ScaleInAnimationAdapter(gridAdapter);
//        animationAdapter.setAbsListView(localImgGridView);
        localImgGridView.setAdapter(gridAdapter);

        currImageFolder = allImgFolder;

        updateBottomBar();
    }


    private void initImgFoldersPopupWindow() {
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

            if (imgs.size() > 0) {
                imageFolder.setFirstImagePath(imgs.get(0));
            } else {
                imageFolder.setFirstImagePath(null);
            }
            imageFolder.setCount(imgs.size());
        }


        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.popup_window_img_dir_list, null);
        mImageDirListPopupWindow = new ImageDirListPopupWindow(
                contentView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                (int) (Dimension.getScreenHeight(getActivity()) * 0.7),
                imageFolders);
        mImageDirListPopupWindow.setAnimationStyle(R.style.anim_popup_dir);
        mImageDirListPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                chosenDirTextView.setTextColor(Color.parseColor("#e7e7e7"));
                chosenDirImgCountTextView.setTextColor(Color.parseColor("#e7e7e7"));
                setWindowAlpha(1.0f);
            }
        });
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


    public void onEventMainThread(ImageFolder imageFolder) {
        onImageFolderSelected(imageFolder);
    }

    public void onEventMainThread(Event.NoExternalStorageException noExternalStorageException) {
        SuperToastUtil.showToast(getActivity().getApplicationContext(), "未发现外部存储");
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

                mImageDirListPopupWindow.showAsDropDown(bottomBarRelativeLayout, 0, 0);

                setWindowAlpha(0.5f);
                chosenDirTextView.setTextColor(Color.parseColor("#ffffff"));
                chosenDirImgCountTextView.setTextColor(Color.parseColor("#ffffff"));
            }
        });

        localImgGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                startSrcImgCropActivity(currentFolderImgs.get(position), view);
                Preference.setPrefBoolean(getActivity().getApplicationContext(), PreferenceKey.IS_SRC_IMG_STORAGE_COMPLETED, true);

                startSrcImgCropActivity(currentFolderImgs.get(position),view);//***crop and select class of clothes before searching    by hiocde

//                TranstionAnimationUtil.startSimilarImgActivity
//                        (getActivity(), currentFolderImgs.get(position), view);
            }
        });
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void startSrcImgCropActivity(String srcImgPath, View itemView) {
        //set flag
        Preference.setPrefBoolean(getActivity().getApplicationContext(), PreferenceKey.IS_SRC_IMG_STORAGE_COMPLETED, true);

        //set animation
        ActivityOptionsCompat options = null;
//        if (Build.VERSION.SDK_INT >= 21) {
        options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), itemView, getResources().getString(R.string.similar_img_transtion_dest));
//        } else {
        options = ActivityOptionsCompat.makeScaleUpAnimation(itemView, 0, 0, itemView.getWidth(), itemView.getHeight());
//        }

        //add extra data
        Intent intent = new Intent(getActivity(), SrcImgCropActivity.class);
        intent.putExtra(ExtraDataName.SRC_IMG_PATH, srcImgPath);

        if (Build.VERSION.SDK_INT > 15) {
            getActivity().startActivity(intent, options.toBundle());
        } else {
            getActivity().startActivity(intent);
        }
    }


    public void onImageFolderSelected(ImageFolder selectedFolder) {
        mImageDirListPopupWindow.dismiss();

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
                return filename.endsWith(".jpg") || filename.endsWith(".png")
                        || filename.endsWith(".jpeg");
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
