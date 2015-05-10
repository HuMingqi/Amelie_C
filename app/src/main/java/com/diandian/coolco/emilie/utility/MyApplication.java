package com.diandian.coolco.emilie.utility;

import android.app.Application;
import android.content.Context;
import android.view.ViewConfiguration;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.lang.reflect.Field;

import de.greenrobot.event.util.AsyncExecutor;

public class MyApplication extends Application {

    private AsyncExecutor asyncExecutor;

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
        asyncExecutor = AsyncExecutor.create();
        hack2showOverflow();
    }

    private void hack2showOverflow() {
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
            if(menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception ex) {
            // Ignore
        }
    }

    public void onLowMemory() {
		super.onLowMemory();
	}

	public void onTerminate() {
		super.onTerminate();
	}

    public AsyncExecutor getAsyncExecutor() {
        return asyncExecutor;
    }
}