package com.philips.codered;

import org.json.JSONException;

import android.accounts.NetworkErrorException;
import android.os.AsyncTask;
import android.os.Message;
import com.philips.codered.App;
import com.philips.codered.NetworkState;


abstract public class ServerRequest<Param, Progress, Result> extends
AsyncTask<Param, Progress, Result>
{
	// Denotes the associated JSON exception listener (if any)
	private OnJSONExceptionListener jsonExceptionListener;

	// Denotes the n/w unavailable listener
	private OnNetworkUnavailableListener networkUnavailableListener;

	// Denotes the General Exception listener
	private OnGeneralExceptionListener generalExceptionListener;

	// Denotes the onComplete listener
	private OnServerResponseListener completeListener;

	private JSONException jsonException;
	private Exception generalException;

	// Denotes, if the async task has been aborted
	private boolean isAborted = false;

	
	public boolean isAborted()
	{
		return isAborted;
	}

	private boolean isComplete = false;

	
	public boolean isComplete()
	{
		return isComplete;
	}

	public void setOnServerResponseListener(OnServerResponseListener completeListener)
	{
		this.completeListener = completeListener;
	}

	public static interface OnJSONExceptionListener
	{
		// Re-direct the call to the base class JSON exception handler
		public void onJSONException(JSONException exception);
	}

	
	public static interface OnGeneralExceptionListener
	{
		// Re-direct the call to the base class JSON exception handler
		public void onGeneralException(Exception exception);
	}


	
	public static interface OnServerResponseListener
	{
		// Re-direct the call to the base class onComplete handler
		public void handle(Message msg);
	}

	
	public static interface OnNetworkUnavailableListener
	{
		// Re-direct the call to the base class n/w exception handler
		public void onNetworkException(NetworkErrorException exception);
	}

	
	public void setOnJSONExceptionListener(OnJSONExceptionListener l)
	{
		this.jsonExceptionListener = l;
	}


	
	public void setOnGeneralExceptionListener(OnGeneralExceptionListener l)
	{
		this.generalExceptionListener = l;
	}


	
	public void setOnNetworkUnavailableListener(
			OnNetworkUnavailableListener networkUnavailableListener)
	{
		this.networkUnavailableListener = networkUnavailableListener;
	}

	// ctor
	public ServerRequest()
	{
		// Base class ctor invocation
		super();
	}

	
	@SuppressWarnings("unchecked")
	public void execute()
	{
		execute(null, null);
	}

	/**
	 * Silly AsynTask has the cancel marked as final. Use abort instead;
	 */
	public void abort()
	{
		isAborted = true;

		// cancel the current running async task
		cancel(true);
	}

	@Override
	protected void onPreExecute()
	{
		// Call the base class pre-execute
		super.onPreExecute();
		
		// init the async task state
		isComplete = false;
		isAborted = false;

		// Check of the current network connection
		boolean hasNetworkConnection = NetworkState.isAvailable(App
				.getContext());
		if (!hasNetworkConnection)
		{
			// Check if there is any n/w unavailable listener attached, then
			// call it handler
			if (networkUnavailableListener != null)
			{
				// Intimate n/w unavailable to the handler
				networkUnavailableListener
				.onNetworkException(new NetworkErrorException(
						App.getContext()
						.getResources()
						.getString(R.string.txtInternetConnectionNotPresent
								)));
			}

			// Don't proceed further to execute the async task
			abort();
		}
		
		else
		{
			//Call the specialized class method, if it needs to perform something, prior to sending the server request.
			prepareforServerRequest();
		}
	}

	/*Executes in the ASYNC child thread 
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected Result doInBackground(Param... param)
	{
		Result retcode = null;

		// Check if the asynctask is already in cancelled state before
		// proceeding
		if (false == isCancelled())
		{
			// Lets start working !!
			try
			{
				// Call the specialized class method over here
				send();

			} catch (JSONException e)
			{
				jsonException = e;

			}
			catch(Exception e)
			{
				generalException = e;
			}
		}

		return retcode;
	}

	/*
	 * (non-Javadoc) Handler response received
	 * 
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(Result result)
	{
		// Call the base class version
		super.onPostExecute(result);
		
		//Call the specialized class, do something before proceeding with parsing of the server response
		onServerResponse();
		
		// Mark successfull completion of the async request to the server
		isComplete = true;

		// If async task has been cancelled, don't proceed further
		if (isCancelled() || isAborted())
		{
			return;
		}

		// Check error conditions - 
		//If json exception has occurred
		if (null != jsonException)
		{
			// Notify json exception listener, about the same
			if (null != jsonExceptionListener)
			{
				jsonExceptionListener.onJSONException(jsonException);
			}
		}
		//If general exception has occurred
		else if (null != generalException)
		{
			// Notify json exception listener, about the same
			if (null != generalExceptionListener)
			{
				generalExceptionListener.onGeneralException(generalException);
			}
		}

		// SUCCESS!
		else
		{

			// Notify the complete listener of the response received from the
			// server
			if (null != completeListener)
			{
				Message msg  = packageServerResponse();
				completeListener.handle(msg);
			}
		}
	}
	
	
	abstract protected void prepareforServerRequest();
		

	
	abstract protected void send() throws JSONException, Exception;
	
	
	abstract protected void onServerResponse();
	

	abstract protected Message packageServerResponse();
}
