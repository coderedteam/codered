package com.philips.codered;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;

/**
 * @author dinesh.sharma_1@philips.com
 * @date Nov 26, 2014
 */
public class ShakerService extends Service implements Shaker.OnShakeListener
{

	private Shaker mShaker;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;
	
	PowerManager.WakeLock wl;
	
	public static final String SHAKE="shake_sound";

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}

	public void onCreate()
	{
		super.onCreate();
		
		this.mSensorManager = ((SensorManager) getSystemService("sensor"));
		this.mAccelerometer = this.mSensorManager.getDefaultSensor(1);
		mShaker = new Shaker(this);
		mShaker.setOnShakeListener(this);
		
		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TAG");
		wl.acquire();
		
	}

	@Override
	public void onShake()
	{
		
		 Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		 // Vibrate for 100 milliseconds
		 v.vibrate(100);
		 
		 //launch the application
		 Intent i = getApplicationContext().getPackageManager().getLaunchIntentForPackage(getPackageName());
		 
		 i.putExtra("keyShake",SHAKE);
		 getApplicationContext().startActivity(i);
	}

	
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		Log.v("Service", "on start command");
		
		
		return START_STICKY;

	}
	
	/* (non-Javadoc)
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		
		wl.release();
	}
}
