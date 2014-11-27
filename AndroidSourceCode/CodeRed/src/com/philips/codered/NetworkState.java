package com.philips.codered;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class NetworkState
{

	public static boolean isAvailable(Context context) 
	{
		boolean hasInternet = false;
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnectedOrConnecting()) 
		{
			hasInternet = true;
		}
		return hasInternet;
	}

}
