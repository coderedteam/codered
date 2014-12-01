package com.philips.codered;

import java.util.Calendar;

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
import android.widget.ImageButton;
import android.widget.Toast;


public class RegisterationActivity extends Activity implements OnClickListener, OnServerResponseListener,
OnJSONExceptionListener, OnGeneralExceptionListener,
OnNetworkUnavailableListener
{
	ImageButton registerButton;
	ImageButton loginButton;
	
	RegisterationServerRequest registerequest;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.register);

		//Associate the listeners
		registerButton = (ImageButton) findViewById(R.id.btnRegisterMe);
		registerButton.setOnClickListener(this);
		
		loginButton = (ImageButton) findViewById(R.id.btnlogin);
		loginButton.setOnClickListener(this);
		
		
	}

	@Override
	public void onClick(View view) 
	{
		//Get the view id
		final int viewid  = view.getId();
		switch(viewid)
		{
		case R.id.btnRegisterMe:
		{
			//Calendar.getInstance().get(Calendar.MINUTE);
			registerequest = new RegisterationServerRequest("hello"+Calendar.getInstance().get(Calendar.MINUTE)+Calendar.getInstance().get(Calendar.SECOND), "world", 12345l, "B+", 12345l, this);

			// Set the appropriate listeners with the server request
			registerequest.setOnServerResponseListener(this);
			registerequest.setOnJSONExceptionListener(this);
			registerequest.setOnGeneralExceptionListener(this);
			registerequest.setOnNetworkUnavailableListener(this);

			//Execute the request
			registerequest.execute();
		}
		break;
		
		case R.id.btnlogin:
		{
			//Invoke login activity
			Intent intent = new Intent(this, LoginActivity.class);
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
		if (null != registerequest && AsyncTask.Status.FINISHED != registerequest.getStatus())
		{
			registerequest.abort();
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


	private void respondtoregister(Message msg)
	{
		Bundle bundle;
		Long result = Constants.FAILURE_RESPONSECODE;

		bundle = msg.getData();
		result = bundle.getLong(Constants.STRING_RESPONSECODE_KEY);

		if (Constants.JSONSUCCESS_RESPONSECODE == result)
		{
			int duration = Toast.LENGTH_SHORT;
			Toast toast = Toast.makeText(getApplicationContext(), R.string.txtRegisterationSuccess, duration);
			toast.show();
		}

	}

	@Override
	public void handle(Message msg)
	{
		if (null != msg)
		{
			// If the response is the expected one then only proceed
			if (Constants.REGISTER_RESP == msg.what)
			{
				respondtoregister(msg);
			}

		}
	}
}
