package com.khan.fftracker.FireBase_Config;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Abdul Jalil Khan on 4/14/2017.
 */
public class SwipeNotificationBraodCastReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Intent intent1=new Intent(context,Push_Service.class);
        context.startService(intent1);

    }
}
