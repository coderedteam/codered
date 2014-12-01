package com.philips.codered;

public final class Constants
{
	private Constants()
	{
		// restrict instantiation
	}


	//URL lists
	final public static String LOGIN_URL = "http://54.169.18.40:8080/api/user/login";
	final public static String REGISTER_URL = "http://54.169.18.40:8080/api/user";
	
	//3rd party social networking urls
	final public static String TWITTER_URL = "http://54.169.18.40:8080/connect/twitter";
	
	final public static String SEND_SIGNAL_URL = "http://54.169.18.40:8080/api/accident/distress_signal";
	
	final public static String RESCUE_SIGNAL_URL = "http://54.169.18.40:8080/api/accident/rescued_signal";
	
	
	
	//JSON request parameters
	public static final String INTENT_DATA_EMAIL = "email";
	public static final String INTENT_DATA_PASSWORD = "password";
	public static final String INTENT_DATA_SOSNO = "sosNo";
	public static final String INTENT_DATA_BLOODGROUP = "bloodGroup";
	public static final String INTENT_DATA_PERSONALNO = "personalNo";
	
	
	//SharedPreferences
	public static final String SHARED_ISLOGGEDIN = "loginstatus";
	
	
	


	public static final String JSON_RESPONSE_CODE = "code";
	final public static long FAILURE_RESPONSECODE = 500l;
	final public static String EMPTY_STRING = "";
	public static final String STRING_NEWLINE = "\n";
	public static final String STRING_CHARENCODING_ISO88591 = "iso-8859-1";
	public static final int INTEGER_EIGHT = 8;
	public static final String HTTP_REQ_CONTENT_TYPE_KEY = "Content-Type";
	public static final String HTTP_REQ_CONTENT_TYPE_VALUE = "application/json";
	public static final String HTTP_REQ_ACCEPT = "Accept";

	public static final String HTTP_REQ_CACHE_CONTROL_KEY = "Cache-Control";
	public static final String HTTP_REQ_CACHE_CONTROL_VALUE = "no-cache, no-store, must-revalidate";
	
	public static final String STRING_RESPONSECODE_KEY = "responsecode";

	public static final int SERVER_TIMEOUT = 7000;

	public static final long JSONSUCCESS_RESPONSECODE = 200l;


	public static final int LOGIN_RESP  = 100;
	public static final int REGISTER_RESP = 200;



}
/*******************************************************************************************************************************************
 * ******************************************************END OF
 * FILE************************************************************************
 * ****************************************************************************************************************************************/
