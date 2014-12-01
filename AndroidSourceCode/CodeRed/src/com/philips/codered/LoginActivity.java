package com.philips.codered;

import org.json.JSONException;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Toast;

import com.philips.codered.ServerRequest.OnGeneralExceptionListener;
import com.philips.codered.ServerRequest.OnJSONExceptionListener;
import com.philips.codered.ServerRequest.OnNetworkUnavailableListener;
import com.philips.codered.ServerRequest.OnServerResponseListener;


public class LoginActivity extends Activity implements OnClickListener, OnServerResponseListener,
OnJSONExceptionListener, OnGeneralExceptionListener,
OnNetworkUnavailableListener
{
	ImageButton loginButton;
	LoginServerRequest loginrequest;
	ImageButton registerButton;	
	SharedPreferences settings;

	
	boolean isAlreadyLoggedIn  = false;
	

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		
		Bundle bundle = getIntent().getExtras();
		
		//Get the preference manager
				settings = PreferenceManager.getDefaultSharedPreferences(this);
				
				
				//Check if the user is already logged in
				isAlreadyLoggedIn = settings.getBoolean(Constants.SHARED_ISLOGGEDIN, false);
				
				if(true == isAlreadyLoggedIn)
				{
					try{
					if (bundle != null
							&& bundle.getString("keyShake").equals(ShakerService.SHAKE))
					{
						
						
						
						navigatetosettingsscreen(true);
						
						
					}
					else{
						
						//Toast.makeText(LoginActivity.this, " alreadyloggin : "+isAlreadyLoggedIn, Toast.LENGTH_SHORT).show();
						
						navigatetosettingsscreen(false);
					}
					
					}catch(Exception e){
						navigatetosettingsscreen(false);
					}
					
				}

		//Load the login screen
		setContentView(R.layout.login);
		
		
		

		//Associate the listeners
		loginButton = (ImageButton) findViewById(R.id.loginBtn);
		loginButton.setOnClickListener(this);
		
		registerButton = (ImageButton) findViewById(R.id.create_account);
		registerButton.setOnClickListener(this);
		
		
		
	}
	
	
	//navigate to settings screen
	private void navigatetosettingsscreen(boolean isShaked)
	{
		
		Intent i=new Intent(LoginActivity.this,HomeScreen.class);
		i.putExtra("key", isShaked);
		startActivity(i);
		
		finish();
	}

	@Override
	public void onClick(View view) 
	{
		//Get the view id
		final int viewid  = view.getId();
		switch(viewid)
		{
		case R.id.loginBtn:
		{
			loginrequest = new LoginServerRequest("hello", "world", this);

			// Set the appropriate listeners with the server request
			loginrequest.setOnServerResponseListener(this);
			loginrequest.setOnJSONExceptionListener(this);
			loginrequest.setOnGeneralExceptionListener(this);
			loginrequest.setOnNetworkUnavailableListener(this);

			//Execute the request
			loginrequest.execute();
		}
		break;



		case R.id.create_account:
		{
			//Invoke registeration activity
			Intent intent = new Intent(this, RegisterationActivity.class);
			startActivity(intent);
		}
		break;


		default:
			break;
		}

	}

	@Override
	public void onStop()
	{
		// Cancel any pending logout request
		if (null != loginrequest && AsyncTask.Status.FINISHED != loginrequest.getStatus())
		{
			loginrequest.abort();
		}

		// Call the super class implementation
		super.onStop();
	}

	@Override
	public void onNetworkException(NetworkErrorException exception) 
	{
		int duration = Toast.LENGTH_SHORT;
		//Toast toast = Toast.makeText(getApplicationContext(), R.string.txtNetworkError, duration);
		//toast.show();
	}

	@Override
	public void onGeneralException(Exception exception) 
	{
		int duration = Toast.LENGTH_SHORT;
		//Toast toast = Toast.makeText(getApplicationContext(), R.string.txtUnknownError, duration);
		//toast.show();

	}

	@Override
	public void onJSONException(JSONException exception) 
	{
		int duration = Toast.LENGTH_SHORT;
		//Toast toast = Toast.makeText(getApplicationContext(), R.string.txtServerError, duration);
		//toast.show();

	}


	private void respondtologin(Message msg)
	{
		Bundle bundle;
		Long result = Constants.FAILURE_RESPONSECODE;

		bundle = msg.getData();
		result = bundle.getLong(Constants.STRING_RESPONSECODE_KEY);

		if (Constants.JSONSUCCESS_RESPONSECODE == result)
		{
			/*int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(getApplicationContext(), R.string.txtLoginSuccess, duration);
			toast.show();*/
		}
		
		//If the user is not logged in
		if(false == isAlreadyLoggedIn)
		{
			//Store that user is now logged in
			isAlreadyLoggedIn = true;
			SharedPreferences.Editor editor = settings.edit();
			editor.putBoolean(Constants.SHARED_ISLOGGEDIN, isAlreadyLoggedIn);
			editor.commit();
			
			//Navigate to settings screen
			navigatetosettingsscreen(false);
		}
	}

	@Override
	public void handle(Message msg)
	{
		if (null != msg)
		{
			// If the response is the expected one then only proceed
			if (Constants.LOGIN_RESP == msg.what)
			{
				respondtologin(msg);
			}

		}
	}
}
