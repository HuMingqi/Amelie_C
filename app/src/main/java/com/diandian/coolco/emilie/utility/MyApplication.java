package com.diandian.coolco.emilie.utility;

import android.app.Application;
import android.content.Context;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class MyApplication extends Application {

	public static void initImageLoader(Context paramContext) {
		ImageLoaderConfiguration localImageLoaderConfiguration = new ImageLoaderConfiguration
				.Builder(paramContext)
				.threadPriority(3)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.writeDebugLogs()
				.build();
		ImageLoader.getInstance().init(localImageLoaderConfiguration);
	}

	public void onCreate() {
		super.onCreate();
		initImageLoader(getApplicationContext());
	}

	public void onLowMemory() {
		super.onLowMemory();
	}

	public void onTerminate() {
		super.onTerminate();
	}

}