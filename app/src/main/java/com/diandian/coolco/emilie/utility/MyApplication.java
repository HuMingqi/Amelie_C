package com.diandian.coolco.emilie.utility;

import android.app.Application;
import android.content.Context;
import android.view.ViewConfiguration;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import java.lang.reflect.Field;

import de.greenrobot.event.util.AsyncExecutor;

public class MyApplication extends Application {

    private AsyncExecutor asyncExecutor;
    private ShakeDetector shakeDetector;

    public static RefWatcher getRefWatcher(Context context) {
        MyApplication application = (MyApplication) context.getApplicationContext();
        return application.refWatcher;
    }

    private RefWatcher refWatcher;

//    public static void initImageLoader(Context paramContext) {
//		ImageLoaderConfiguration localImageLoaderConfiguration = new ImageLoaderConfiguration
//				.Builder(paramContext)
//				.threadPriority(3)
//				.denyCacheImageMultipleSizesInMemory()
//				.discCacheFileNameGenerator(new Md5FileNameGenerator())
//				.tasksProcessingOrder(QueueProcessingType.LIFO)
//				.writeDebugLogs()
//				.build();
//		ImageLoader.getInstance().init(localImageLoaderConfiguration);
//	}

	public void onCreate() {
		super.onCreate();
//		initImageLoader(getApplicationContext());
        asyncExecutor = AsyncExecutor.create();
//        hack2showOverflow();
        startShakeDetection();
        refWatcher = LeakCanary.install(this);
        Fresco.initialize(getApplicationContext());
    }

    private void startShakeDetection() {
        shakeDetector = new ShakeDetector(getApplicationContext());
        shakeDetector.start();
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
        stopShakeDetection();
	}

    public void onTerminate() {
		super.onTerminate();
        stopShakeDetection();
	}

    private void stopShakeDetection() {
        shakeDetector.stop();
    }

    public AsyncExecutor getAsyncExecutor() {
        return asyncExecutor;
    }
}