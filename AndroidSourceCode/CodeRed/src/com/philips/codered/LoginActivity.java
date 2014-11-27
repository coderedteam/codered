package com.philips.codered;

import org.json.JSONException;
import com.philips.codered.ServerRequest.OnGeneralExceptionListener;
import com.philips.codered.ServerRequest.OnJSONExceptionListener;
import com.philips.codered.ServerRequest.OnNetworkUnavailableListener;
import com.philips.codered.ServerRequest.OnServerResponseListener;

import android.accounts.NetworkErrorException;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


public class LoginActivity extends Activity implements OnClickListener, OnServerResponseListener,
OnJSONExceptionListener, OnGeneralExceptionListener,
OnNetworkUnavailableListener
{
	Button loginButton;
	LoginServerRequest loginrequest;
	
	Button registerButton;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		//Load the login screen
		setContentView(R.layout.activity_login);

		//Associate the listeners
		loginButton = (Button) findViewById(R.id.btnSignIn);
		loginButton.setOnClickListener(this);
		
		registerButton = (Button) findViewById(R.id.btnNewAccount);
		registerButton.setOnClickListener(this);
	}

	@Override
	public void onClick(View view) 
	{
		//Get the view id
		final int viewid  = view.getId();
		switch(viewid)
		{
		case R.id.btnSignIn:
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



		case R.id.btnNewAccount:
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
		Toast toast = Toast.makeText(getApplicationContext(), R.string.txtNetworkError, duration);
		toast.show();
	}

	@Override
	public void onGeneralException(Exception exception) 
	{
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(getApplicationContext(), R.string.txtUnknownError, duration);
		toast.show();

	}

	@Override
	public void onJSONException(JSONException exception) 
	{
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(getApplicationContext(), R.string.txtServerError, duration);
		toast.show();

	}


	private void respondtologin(Message msg)
	{
		Bundle bundle;
		Long result = Constants.FAILURE_RESPONSECODE;

		bundle = msg.getData();
		result = bundle.getLong(Constants.STRING_RESPONSECODE_KEY);

		if (Constants.JSONSUCCESS_RESPONSECODE == result)
		{
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(getApplicationContext(), R.string.txtLoginSuccess, duration);
			toast.show();
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
