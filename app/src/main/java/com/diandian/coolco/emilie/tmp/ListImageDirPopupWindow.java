package com.diandian.coolco.emilie.tmp;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.diandian.coolco.emilie.R;
import com.diandian.coolco.emilie.model.ImageFolder;

import java.util.List;

public class ListImageDirPopupWindow extends BasePopupWindowForListView<ImageFolder>
{
	private ListView mListDir;

	public ListImageDirPopupWindow(int width, int height,
			List<ImageFolder> datas, View convertView)
	{
		super(convertView, width, height, true, datas);
	}

	@Override
	public void initViews()
	{
		mListDir = (ListView) findViewById(R.id.id_list_dir);
//        View emptyHeaderView = new View(mListDir.getContext());
//
//        mListDir.addHeaderView(emptyHeaderView);
		mListDir.setAdapter(new CommonAdapter<ImageFolder>(context, mDatas,
				R.layout.list_item_img_dir)
		{
			@Override
			public void convert(ViewHolder helper, ImageFolder item)
			{
				helper.setText(R.id.id_dir_item_name, item.getName());
				helper.setImageByUrl(R.id.id_dir_item_image,
						item.getFirstImagePath());
				helper.setText(R.id.id_dir_item_count, item.getCount() + "张");
			}
		});
	}

	public interface OnImageDirSelected
	{
		void imgDirSelected(ImageFolder floder);
	}

	private OnImageDirSelected mImageDirSelected;

	public void setOnImageDirSelected(OnImageDirSelected mImageDirSelected)
	{
		this.mImageDirSelected = mImageDirSelected;
	}

	@Override
	public void initEvents()
	{
		mListDir.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id)
			{

				if (mImageDirSelected != null)
				{
					mImageDirSelected.imgDirSelected(mDatas.get(position));
				}
			}
		});
	}

	@Override
	public void init()
	{
		// TODO Auto-generated method stub

	}

	@Override
	protected void beforeInitWeNeedSomeParams(Object... params)
	{
		// TODO Auto-generated method stub
	}

}
