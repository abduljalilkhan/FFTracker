package com.khan.fftracker.Location_FusedAPI;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.khan.fftracker.FireBase_Config.Push_Service;
import com.khan.fftracker.FireBase_Config.Show_Push_Notification;
import com.khan.fftracker.Navigation_Drawer.Drawer;
import com.khan.fftracker.login_Stuffs.Push_Notification;

public class RestartService_Reciver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("json Broadcast Listened", "Service tried to stop");
        //     Toast.makeText(context, "Service restarted", Toast.LENGTH_SHORT).show();

//        Intent i = new Intent();
//        i.setClassName(context.getPackageName(), Landing_Activity.class.getName());
//        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        context.startActivity(i);

        try {
            Bundle bundle = intent.getExtras();

            /// Check if notification is tapped sent by push notification
            new Push_Notification(Drawer.mActivity).pushNotification(bundle);

            // Show notification of push notification
            new Show_Push_Notification(Drawer.mActivity).notificationDialogue();

            //call webservics fro push notification
            Intent intent1 = new Intent(context, Push_Service.class);
            context.startService(intent1);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            context.startForegroundService(new Intent(context, Location_Long_Lat.class));
//        } else {
        //  context.startService(new Intent(context, Location_Long_Lat.class));
        // Location_Current.enqueueWork(context, new Intent());
        // }
    }
}
