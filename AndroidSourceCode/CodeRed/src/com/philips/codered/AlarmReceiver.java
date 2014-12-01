
package com.philips.codered;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;

public class AlarmReceiver extends BroadcastReceiver 
{
    @Override
    public void onReceive(Context context, Intent intent) 
    {
    	 Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		 // Vibrate for 500 milliseconds
		 v.vibrate(500);
		 
        Intent myService = new Intent(context, ShakerService.class);
        context.startService(myService);
    }
}
