package com.diandian.coolco.emilie.dialog;

import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.diandian.coolco.emilie.R;


public class MenuDialog extends Dialog {
	private MenuListener menuListener;
	private List<String> menuStrings;
	private Context context;
	
	public MenuDialog(Context context, List<String> menuStrings) {
		super(context, R.style.confirmDialogTheme);
		this.context = context;
		this.menuStrings = menuStrings;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_menu);

		setUpViews();

		setWidth();
	}
	
	private void setUpViews() {
		ListView menuListView = (ListView) findViewById(R.id.lv_menu);
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.list_item_menu, menuStrings);
		menuListView.setAdapter(adapter);
		menuListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
				if (menuListener != null) {
					menuListener.onMenuItemClick(position);
					dismiss();
				}
			}
		});
	}

	/** Set the dialog's width as 80% of the screen */
	private void setWidth() {
		Window window = getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		params.width = (int) (getContext().getResources().getDisplayMetrics().widthPixels * 0.8);
		window.setAttributes(params);
	}
	
	public void setMenuListener(MenuListener menuListener) {
		this.menuListener = menuListener;
	}
	
	public interface MenuListener {
		public void onMenuItemClick(int position);
	}
}
