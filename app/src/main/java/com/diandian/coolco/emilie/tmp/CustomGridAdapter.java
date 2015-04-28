package com.diandian.coolco.emilie.tmp;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.diandian.coolco.emilie.R;
import com.malinskiy.materialicons.IconDrawable;
import com.malinskiy.materialicons.Iconify;

import java.util.LinkedList;
import java.util.List;

public class CustomGridAdapter extends CommonAdapter<String>
{

	/**
	 * 用户选择的图片，存储为图片的完整路径
	 */
	public static List<String> mSelectedImage = new LinkedList<String>();
//    private final Drawable defaultDrawable;

    /**
	 * 文件夹路径
	 */
//	private String mDirPath;

	public CustomGridAdapter(Context context, List<String> mDatas, int itemLayoutId)
	{
		super(context, mDatas, itemLayoutId);
//		this.mDirPath = dirPath;
//        defaultDrawable = new IconDrawable(context, Iconify.IconValue.md_filter_drama)
//                .color(Color.parseColor("#474747"))
//                .sizeDp(160);

	}

	@Override
	public void convert(final ViewHolder helper, final String item)
	{
		//设置no_pic
//		helper.setImageResource(R.id.iv_local_img_grid_item, 0);
//        helper.setImageDrawable(R.id.iv_local_img_grid_item, defaultDrawable);
		//设置no_selected
//				helper.setImageResource(R.id.id_item_select,
//						R.drawable.picture_unselected);
		//设置图片
		helper.setImageByUrl(R.id.iv_local_img_grid_item, item);
		
		final ImageView mImageView = helper.getView(R.id.iv_local_img_grid_item);
//		final ImageView mSelect = helper.getView(R.id.id_item_select);
		
		mImageView.setColorFilter(null);
		//设置ImageView的点击事件
//		mImageView.setOnClickListener(new OnClickListener()
//		{
//			//选择，则将图片变暗，反之则反之
//			@Override
//			public void onClick(View v)
//			{
//				// 已经选择过该图片
//				if (mSelectedImage.contains(item))
//				{
//					mSelectedImage.remove(item);
//					mSelect.setImageResource(R.drawable.picture_unselected);
//					mImageView.setColorFilter(null);
//				} else
//				// 未选择该图片
//				{
//					mSelectedImage.add(item);
//					mSelect.setImageResource(R.drawable.pictures_selected);
//					mImageView.setColorFilter(Color.parseColor("#77000000"));
//				}
//			}
//		});
		
		/**
		 * 已经选择过的图片，显示出选择过的效果
		 */
//		if (mSelectedImage.contains(item))
//		{
//			mSelect.setImageResource(R.drawable.pictures_selected);
//			mImageView.setColorFilter(Color.parseColor("#77000000"));
//		}

	}
}
