package com.khan.fftracker.Location_FusedAPI;

import static com.khan.fftracker.login_Stuffs.Login_New.TAG;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.util.Log;

public class Location_Receiver extends BroadcastReceiver {
    MediaPlayer mp;
    @Override
    public void onReceive(Context context, Intent intent) {
      //  mp=MediaPlayer.create(context, R.raw.all_button_press);
       // mp.start();
     //   Toast.makeText(context, "Alarm....", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onReceive:Location_Receiver ");
        try {
            // Explicitly specify that GcmIntentService will handle the intent.
            ComponentName comp = new ComponentName(context.getPackageName(), LocationCurrent.class.getName());
            // Start the service, keeping the device awake while it is launching.
            // startWakefulService(context, (intent.setComponent(comp)));
            //setResultCode(Activity.RESULT_OK);
            LocationCurrent.enqueueWork(context, (intent.setComponent(comp)));

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
