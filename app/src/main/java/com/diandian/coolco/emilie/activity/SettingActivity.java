package com.diandian.coolco.emilie.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.diandian.coolco.emilie.R;


public class SettingActivity extends BaseActivity implements OnItemClickListener{
	private Context context;
	private ListView list1;
	private ListView list2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_setting);
		
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		context = getApplicationContext();
		
		list1 = (ListView) findViewById(R.id.list1);
		list2 = (ListView) findViewById(R.id.list2);
		String[] listStr1 = {"欢迎页", "用户协议", "常见问题"};
		String[] listStr2 = {"告诉朋友", "意见反馈", "版本升级", "给我们评分", "开源许可", "关于我们"};
		ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(context, R.layout.list_item_setting, listStr1);
		ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(context, R.layout.list_item_setting, listStr2);
		list1.setAdapter(adapter1);
		list2.setAdapter(adapter2);
		list1.setOnItemClickListener(this);
		list2.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (parent == list1) {
			switch (position) {
			case 0:
				break;
			case 1:{
//				Intent intent = new Intent(this, WebViewActivity.class);
//				intent.putExtra("title", "用户协议");
//				intent.putExtra("url", URL.AGREEMENT);
//				startActivity(intent);
				break;
			}
			case 2:{
//				Intent intent = new Intent(this, WebViewActivity.class);
//				intent.putExtra("title", "常见问题");
//				intent.putExtra("url", URL.PROBLEM);
//				startActivity(intent);
				break;
			}

			default:
				break;
			}
		} else {
			switch (position) {
			case 0:
				Intent sendIntent = new Intent();    
				sendIntent.setAction(Intent.ACTION_SEND);    
				sendIntent.setType("text/*");    
				sendIntent.putExtra(Intent.EXTRA_TEXT, "我在使用i家政客户端，随时随地查看周边家政人员，方便快捷，推荐你也试试哦：[http://housekeeping.sinaapp.com]");    
				startActivity(sendIntent);
				break;
			case 1:
//				startActivity(new Intent(this, FeedbackActivity.class));
				break;
			case 2:
				
				break;
			case 3:{
				Uri uri = Uri.parse("market://details?id="+getPackageName());  
				Intent intent = new Intent(Intent.ACTION_VIEW,uri);  
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
				startActivity(intent);
				break;
			}
			case 4: {
//				Intent intent = new Intent(this, WebViewActivity.class);
//				intent.putExtra("title", "关于我们");
//				intent.putExtra("url", URL.ABOUT_US);
//				startActivity(intent);
				break;
			}

			default:
				break;
			}
		}
	}
	
	
}
