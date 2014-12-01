package com.philips.codered;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;


public class SettingsActivity extends Activity implements OnClickListener
{

	ImageButton twitterbtn;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		//Load the login screen
		setContentView(R.layout.settings);

		//Associate the listeners
		twitterbtn = (ImageButton) findViewById(R.id.twitterBtn);
		twitterbtn.setOnClickListener(this);
	}



	@Override
	public void onClick(View view) 
	{
		//Get the view id
		final int viewid  = view.getId();
		switch(viewid)
		{
		case R.id.twitterBtn:
		{
			//Open the browser with the url
			try 
			{
				Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.TWITTER_URL));
				startActivity(browserIntent);
			} catch (ActivityNotFoundException e) 
			{

			}
		}
		break;

		default:
			break;
		}

	}

}
