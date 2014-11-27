package com.philips.codered;


import android.app.Activity;
import android.os.Bundle;


public class LoginActivity extends Activity 
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		//Load the login screen
		setContentView(R.layout.activity_login);
	}
}
