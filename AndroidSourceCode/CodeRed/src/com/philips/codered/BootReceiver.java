package com.philips.codered;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * @author dinesh.sharma_1@philips.com
 * @date Nov 26, 2014
 */
public class BootReceiver extends BroadcastReceiver 
{
    public void onReceive(Context context, Intent intent) 
    {
        Intent i = new Intent(context, ShakerService.class);
        context.startService(i);
    }
}
