package com.philips.codered;

import android.app.Application;
import android.content.Context;


public class App extends Application 
{

	private static App instance;

	public App() 
	{
		instance = this;
	}

	//Return the context of the app
	public static Context getContext() 
	{
		return instance.getApplicationContext();
	}

}