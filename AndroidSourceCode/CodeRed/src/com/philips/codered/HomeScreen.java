package com.philips.codered;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Calendar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * @author dinesh.sharma_1@philips.com
 * @date Nov 27, 2014
 */
public class HomeScreen extends Activity
{

	MediaPlayer mPlayer;

	Intent serviceIntent;
	Dialog dialog;
	TextView text2;

	CounterClass timer;

	String locationStringUrl = "https://www.google.co.in/maps/@13.0421416,77.6249732";

	private Shaker mShaker;
	private SensorManager mSensorManager;
	private Sensor mAccelerometer;

	public static long THREHOLD_TIME = 15000L;
	public static long INTERVAL_TIME = 1000L;

	Camera mCamera;
	Camera.Parameters mParameters;
	int delay = 100; // in ms
	boolean isFlashOn = false;

	Thread thread;

	boolean switchService = false;

	ImageButton panicBtn;
	
	String resStringJson;
	String locationString;
	
	boolean dialogDismiss=false;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);

		panicBtn = (ImageButton) findViewById(R.id.panic_btn);

		startYouService();

		Bundle bundle = getIntent().getExtras();

		if (bundle != null && bundle.getBoolean("key"))
		{

			// Toast.makeText(HomeScreen.this,
			// "bundle "+bundle.getBoolean("key"), Toast.LENGTH_SHORT).show();
			if (!switchService)
				switchOnService();

		}

	}

	private void switchOnService()
	{

		switchService = true;

		startTimer();

		playMusic();

		increaseMaxVolume();

		enableMobileDataWifi();

		startBlink(50L, 10000L);

		changeBtn();
	}

	private void changeBtn()
	{

		if (!switchService)
		{
			panicBtn.setBackgroundResource(R.drawable.panic_btn);
		} else
		{
			panicBtn.setBackgroundResource(R.drawable.rescued_btn);
		}
	}

	private void startBlink(final long delay, final long times)
	{
		thread = new Thread()
		{
			public void run()
			{
				try
				{

					for (int i = 0; i < times * 2; i++)
					{

						// Log.e("FLASH", "flash : " + isFlashOn);

						if (isFlashOn)
						{
							turnOffFlash();
						} else
						{
							turnOnFlash();
						}
						sleep(delay);
					}

				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		};
		thread.start();
	}

	private void stopBlink()
	{

		if (thread != null)
		{
			try
			{
				isFlashOn = false;
				thread.interrupt();
			} catch (Exception e)
			{
				e.printStackTrace();
			}

		}

	}

	public void turnOnFlash()
	{

		if (mCamera == null)
		{

			mCamera = Camera.open();
		}

		try
		{
			Parameters parameters = mCamera.getParameters();
			parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
			mCamera.setParameters(parameters);

			mCamera.setPreviewTexture(new SurfaceTexture(0));

			mCamera.startPreview();
			mCamera.autoFocus(new AutoFocusCallback()
			{
				public void onAutoFocus(boolean success, Camera camera)
				{
				}
			});

			isFlashOn = true;
		} catch (Exception e)
		{
			// We are expecting this to happen on devices that don't support
			// autofocus.
		}
	}

	public void turnOffFlash()
	{

		if (mCamera == null)
		{

			mCamera = Camera.open();
		}

		try
		{
			Parameters parameters = mCamera.getParameters();
			parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
			mCamera.setParameters(parameters);
			mCamera.stopPreview();

			isFlashOn = false;

		} catch (Exception e)
		{
			// We are expecting this to happen on devices that don't support
			// autofocus.
		}
	}

	private void switchOFFService()
	{

		switchService = false;

		stopTimer();

		stopMusic();

		turnOffFlash();

		stopBlink();
		
		if(dialogDismiss)
			new RescueToServer().execute();

		if (mCamera != null)
		{
			mCamera.release();
		}

		HomeScreen.this.finish();

	}

	private void startTimer()
	{

		timer = new CounterClass(THREHOLD_TIME, INTERVAL_TIME);
		timer.start();
	}

	private void stopTimer()
	{

		if (timer != null)
		{
			timer.cancel();
		}

	}

	private void increaseMaxVolume()
	{

		AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

		am.setStreamVolume(AudioManager.STREAM_MUSIC,
				am.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

	}

	private void setAlarmService()
	{
		Intent myAlarm = new Intent(getApplicationContext(),
				AlarmReceiver.class);
		// myAlarm.putExtra("project_id", project_id); //Put Extra if needed
		PendingIntent recurringAlarm = PendingIntent.getBroadcast(
				getApplicationContext(), 0, myAlarm,
				PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarms = (AlarmManager) this
				.getSystemService(Context.ALARM_SERVICE);
		Calendar updateTime = Calendar.getInstance();
		// updateTime.setWhatever(0); //set time to start first occurence of
		// alarm
		alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP,
				updateTime.getTimeInMillis(), THREHOLD_TIME, recurringAlarm); // you
		// can
		// modify
		// the
		// interval
		// of
		// course
	}

	private void restartMusic()
	{
		stopMusic();

		mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.sound);
		mPlayer.setLooping(true);
		mPlayer.start();// Start playing the music
	}

	private void enableMobileDataWifi()
	{

		try
		{

			// mobile data enabling

			ConnectivityManager dataManager;
			dataManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			Method dataMtd = ConnectivityManager.class.getDeclaredMethod(
					"setMobileDataEnabled", boolean.class);
			dataMtd.setAccessible(true);
			dataMtd.invoke(dataManager, true); // True - to enable data
												// connectivity .

			// wifi enabling

			WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			wifiManager.setWifiEnabled(true);

		} catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	private void startYouService()
	{

		serviceIntent = new Intent(HomeScreen.this, ShakerService.class);
		startService(serviceIntent);
	}

	public void click(View v)
	{
		if (v.getId() == R.id.panic_btn)
		{

			// stopService();

			if (switchService)
			{
				switchOFFService();
			} else
			{
				switchOnService();
			}

		}

		else if (v.getId() == R.id.settingBtn)
		{
			Intent i = new Intent(HomeScreen.this, SettingsActivity.class);
			startActivity(i);
		}
	}

	private void stopService()
	{
		serviceIntent = new Intent(HomeScreen.this, ShakerService.class);
		stopService(serviceIntent);

	}

	private void playMusic()
	{

		mPlayer = MediaPlayer.create(getApplicationContext(), R.raw.ticking);
		mPlayer.setLooping(true);
		mPlayer.start();// Start playing the music
	}

	private void stopMusic()
	{

		if (mPlayer != null && mPlayer.isPlaying())
		{// If music is playing already
			mPlayer.stop();// Stop playing the music
		}
	}

	private void performActions()
	{

		
		dialogDismiss=true;
		
		new SendToServer().execute();
	}

	/*
	 * private void makeCall() {
	 * 
	 * }
	 */

	// ---sends an SMS message to another device---
	private void sendSMS(String phoneNumber, String message)
	{

		try
		{
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(phoneNumber, null, message, null, null);

			// Toast.makeText(getApplicationContext(),
			// "Message Sent",Toast.LENGTH_LONG).show();
		} catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public void showDialog()
	{

		dialog = new Dialog(HomeScreen.this, R.style.CustomDialog);

		dialog.setContentView(R.layout.custom_dialog);

		dialog.setCancelable(false);

		text2 = (TextView) dialog.findViewById(R.id.text2);

		Button ignoreBtn = (Button) dialog.findViewById(R.id.ignore);

		ignoreBtn.setOnClickListener(new OnClickListener()
		{

			@Override
			public void onClick(View arg0)
			{
				dialog.dismiss();

				switchOFFService();

			}
		});

		dialog.show();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume()
	{
		super.onResume();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause()
	{
		super.onPause();

		/*
		 * if(timer!=null){ timer.cancel(); }
		 */
	}

	private class CounterClass extends CountDownTimer
	{
		public CounterClass(long millisInFuture, long countDownInterval)
		{
			super(millisInFuture, countDownInterval);

			showDialog();
		}

		@Override
		public void onFinish()
		{
			dialog.dismiss();

			performActions();

		}

		@Override
		public void onTick(long millisUntilFinished)
		{
			Log.v("TIMER tick", "tick : " + millisUntilFinished);

			text2 = (TextView) dialog.findViewById(R.id.text2);

			text2.setText((millisUntilFinished / 1000) + " Seconds");
		}
	}

	/*****************************************
	 * 
	 */

	private class SendToServer extends AsyncTask<Void, Void, Void>
	{

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Void doInBackground(Void... params)
		{

			InputStream stream = null;
			String strJson = Constants.EMPTY_STRING;
			JSONObject jObj = null;

			try
			{
				// URL for the request
				StringBuilder url = new StringBuilder(
						Constants.SEND_SIGNAL_URL
								+ "?lat=13.0421416&lon=77.6249732&showHealthData=true&showLocationData=true");

				Log.v("URL", "URL : " + url.toString());

				HttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams,
						Constants.SERVER_TIMEOUT);
				DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);

				HttpPost request = new HttpPost(url.toString());
				request.addHeader(Constants.HTTP_REQ_CONTENT_TYPE_KEY,
						Constants.HTTP_REQ_CONTENT_TYPE_VALUE);
				request.addHeader(Constants.HTTP_REQ_ACCEPT,
						Constants.HTTP_REQ_CONTENT_TYPE_VALUE);

				request.setHeader(Constants.HTTP_REQ_CACHE_CONTROL_KEY,
						Constants.HTTP_REQ_CACHE_CONTROL_VALUE);

				JSONObject Parent = new JSONObject();

				StringEntity se = new StringEntity(Parent.toString());
				request.setEntity(se);
				HttpResponse httpResponse = httpClient.execute(request);
				HttpEntity httpEntity = httpResponse.getEntity();
				stream = httpEntity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(stream,
								Constants.STRING_CHARENCODING_ISO88591),
						Constants.INTEGER_EIGHT);
				StringBuilder sb = new StringBuilder();

				String line = null;
				while ((line = reader.readLine()) != null)
				{
					sb.append(line);
					sb.append(Constants.STRING_NEWLINE);
				}
				stream.close();
				resStringJson = sb.toString();

				Log.v("JSON", "json string : " + resStringJson);

				// Log the JSON response

				//jObj = new JSONObject(strJson);
				
				/*if(jObj!=null){
					locationString=jObj.getString("result");
				}*/

			} catch (Exception e)
			{
				e.printStackTrace();
			}

			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Void result)
		{
			super.onPostExecute(result);

			restartMusic();

			changeBtn();

			sendSMS(getResources().getString(R.string.txtSMS_no),
					getResources().getString(R.string.txtSMS_msg) + " "
							+ resStringJson);

		}

	}

	private class RescueToServer extends AsyncTask<Void, Void, Void>
	{

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Void doInBackground(Void... params)
		{

			InputStream stream = null;
			String strJson = Constants.EMPTY_STRING;
			JSONObject jObj = null;

			try
			{
				// URL for the request
				StringBuilder url = new StringBuilder(
						Constants.RESCUE_SIGNAL_URL);

				Log.v("URL", "URL : " + url.toString());

				HttpParams httpParams = new BasicHttpParams();
				HttpConnectionParams.setConnectionTimeout(httpParams,
						Constants.SERVER_TIMEOUT);
				DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);

				HttpPost request = new HttpPost(url.toString());
				request.addHeader(Constants.HTTP_REQ_CONTENT_TYPE_KEY,
						Constants.HTTP_REQ_CONTENT_TYPE_VALUE);
				request.addHeader(Constants.HTTP_REQ_ACCEPT,
						Constants.HTTP_REQ_CONTENT_TYPE_VALUE);

				request.setHeader(Constants.HTTP_REQ_CACHE_CONTROL_KEY,
						Constants.HTTP_REQ_CACHE_CONTROL_VALUE);

				JSONObject Parent = new JSONObject();

				StringEntity se = new StringEntity(Parent.toString());
				request.setEntity(se);
				HttpResponse httpResponse = httpClient.execute(request);
				HttpEntity httpEntity = httpResponse.getEntity();
				stream = httpEntity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(stream,
								Constants.STRING_CHARENCODING_ISO88591),
						Constants.INTEGER_EIGHT);
				StringBuilder sb = new StringBuilder();

				String line = null;
				while ((line = reader.readLine()) != null)
				{
					sb.append(line);
					sb.append(Constants.STRING_NEWLINE);
				}
				stream.close();
				strJson = sb.toString();

				Log.v("JSON", "json string : " + strJson);

				// Log the JSON response

				//jObj = new JSONObject(strJson);

			} catch (Exception e)
			{
				e.printStackTrace();
			}

			return null;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Void result)
		{
			super.onPostExecute(result);

		}

	}

}
