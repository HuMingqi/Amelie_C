package com.diandian.coolco.emilie.utility;
import java.util.ArrayList;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.FloatMath;

import de.greenrobot.event.EventBus;

public class ShakeDetector implements SensorEventListener {

	static final int UPDATE_INTERVAL = 100;

	long mLastUpdateTime;
	
	long mLastShakeTime = 0;

	float mLastX, mLastY, mLastZ;
	Context mContext;
	SensorManager mSensorManager;
	ArrayList<OnShakeListener> mListeners;

	public int shakeThreshold = 4000;

	public ShakeDetector(Context context) {
		mContext = context;
		mSensorManager = (SensorManager) context
				.getSystemService(Context.SENSOR_SERVICE);
		mListeners = new ArrayList<OnShakeListener>();
	}

	public interface OnShakeListener {
		void onShake();
	}

	public void registerOnShakeListener(OnShakeListener listener) {
		if (mListeners.contains(listener))
			return;
		mListeners.add(listener);
	}

	public void unregisterOnShakeListener(OnShakeListener listener) {
		mListeners.remove(listener);
	}

	public void start() {
		if (mSensorManager == null) {
			throw new UnsupportedOperationException();
		}
		Sensor sensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		if (sensor == null) {
			throw new UnsupportedOperationException();
		}
		boolean success = mSensorManager.registerListener(this, sensor,
				SensorManager.SENSOR_DELAY_GAME);
		if (!success) {
			throw new UnsupportedOperationException();
		}
	}

	public void stop() {
		if (mSensorManager != null)
			mSensorManager.unregisterListener(this);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}


	@Override
	public void onSensorChanged(SensorEvent event) {
		long currentTime = System.currentTimeMillis();
		long diffTime = currentTime - mLastUpdateTime;
		if (diffTime < UPDATE_INTERVAL)
			return;
		mLastUpdateTime = currentTime;
		float x = event.values[0];
		float y = event.values[1];
		float z = event.values[2];
		float deltaX = x - mLastX;
		float deltaY = y - mLastY;
		float deltaZ = z - mLastZ;
		mLastX = x;
		mLastY = y;
		mLastZ = z;
		float delta = FloatMath.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ
				* deltaZ)
				/ diffTime * 10000;
		if (delta > shakeThreshold) {
			if (currentTime - mLastShakeTime > 1000) {
				EventBus.getDefault().post(new Event.ShakeEvent());
				mLastShakeTime = currentTime;
			}
		}
	}

	private void notifyListeners() {
		for (OnShakeListener listener : mListeners) {
			listener.onShake();
		}
	}
}
