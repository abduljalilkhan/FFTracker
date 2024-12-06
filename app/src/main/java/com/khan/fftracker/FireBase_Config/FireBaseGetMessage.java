package com.khan.fftracker.FireBase_Config;

import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
import static android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.khan.fftracker.AdminMyPCP.Admin_Chat.Admin_MainChat;
import com.khan.fftracker.AdminMyPCP.Admin_Chat.Admin_MainChat_Content;
import com.khan.fftracker.Location_FusedAPI.RestartService_Reciver;
import com.khan.fftracker.LogCalls.LogCalls_Debug;
import com.khan.fftracker.R;
import com.khan.fftracker.Video_Create_Customer.Video_List_Customer;
import com.khan.fftracker.login_Stuffs.Landing_Activity;
import com.khan.fftracker.login_Stuffs.Login_Contstant;
import com.khan.fftracker.login_Stuffs.Push_Notification;

import org.json.JSONObject;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * Created by Abdul Jalil Khan on 4/19/2017.
 */
public class FireBaseGetMessage extends FirebaseMessagingService {
    private static final String TAG = "json";
    Context context;
    public static int NOTIFICATION_ID = 1;
    private NotificationManager notificationManager;
    NotificationCompat.Builder builder;
    public static JSONObject jsonObject;
    private SharedPreferences sharedPreferences;
    Intent mIntent;
    String strChatNtify;

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

        Log.d(TAG, "onNewToken: " + s);
        //getting registration token from firebase API
        String strToken = FirebaseInstanceId.getInstance().getToken();
        SharedPreferences sharedPreferences = getSharedPreferences(Login_Contstant.MY_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("tokendevice", strToken);
        editor.commit();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "notification" + remoteMessage.getData().toString());
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Data Payload: " + remoteMessage.getData().toString());

            try {
                jsonObject = new JSONObject(remoteMessage.getData().get("data").toString());

                sendPushNotification(jsonObject);
            } catch (Exception e) {
                Log.d(TAG, "FireBaseGetMessage: " + e.getMessage());
            }
        }
    }

    private void sendPushNotification(JSONObject json) {
        Log.d(TAG, "sendPushNotification");
        try {

            sharedPreferences = getSharedPreferences(Login_Contstant.MY_PREFS, MODE_PRIVATE);
            sharedPreferences.edit().putBoolean("notify", true).commit();

            //apply aap count badges
            ShortcutBadger.applyCount(FireBaseGetMessage.this, jsonObject.getInt("badge")); //for 1.1.4+

            check_Push_Intent();

            //Intent, pass data to other class
            mIntent = new Intent(this, Landing_Activity.class);
            //put extra data in bundle
            intentData(mIntent);

            mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            if (isAppInForeground()) {
                Log.d(TAG, "sendPushNotification: isAppInForeground " + isAppInForeground());

                Intent broadcastIntent = new Intent(this, RestartService_Reciver.class);
              //put extra data in bundle
                intentData(broadcastIntent);

                sendBroadcast(broadcastIntent);

                return;
            }
//            else {
//                startActivity(mIntent);
//            }

            Log.d(TAG, "sendPushNotification: y or N " + isAppInForeground());
            // startActivity(mIntent);


            PendingIntent pendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, mIntent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);

            //notification manager
            notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

            //notification sound
            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

            //channel id
            String channelId = getString(R.string.default_notification_channel_id);

            NotificationCompat.Builder builder1 = new NotificationCompat.Builder(this, channelId);
            //set background color and icon
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder1.setColor(getResources().getColor(R.color.blue));
                builder1.setSmallIcon(R.drawable.logo_notify);
                //builder.setLargeIcon(BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.delete_appoint));
            } else {
                builder1.setSmallIcon(R.drawable.logo_notify);
            }

            builder1.setContentTitle(jsonObject.getString("title"))
                    //.setStyle(new NotificationCompat.BigTextStyle()
                    // .bigText(jsonObject.getString("title")))
                    .setAutoCancel(true).setContentText(jsonObject.getString("message")).setTicker(getString(R.string.app_name)).
                    setSound(alarmSound).setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE);

            builder1.setContentIntent(pendingIntent);
            builder1.setDeleteIntent(createOnDismissedIntent(context, 1));

            // Since android Oreo notification channel is needed.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(channelId, getString(R.string.app_name)+" push notification", NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }

            notificationManager.notify(NOTIFICATION_ID, builder1.build());
            NOTIFICATION_ID++;
            //  Log.d("Notification_ID",""+NOTIFICATION_ID);

            //apply aap count badges
            //  ShortcutBadger.applyCount(FireBaseGetMessage.this, jsonObject.getInt("badge")); //for 1.1.4+


        } catch (Exception e) {
            Log.d("sendPushNotifcation Err", e.getMessage());
        }

    }

    //put extra data in bundle
    private void intentData(Intent mIntent) {
        try {
            mIntent.putExtra("title", jsonObject.getString("title"));
            mIntent.putExtra("message", jsonObject.getString("message"));
            mIntent.putExtra("image", jsonObject.getString("image"));
            mIntent.putExtra("pushtitle", jsonObject.getString("pushtitle"));
            mIntent.putExtra("push", jsonObject.getString("push"));
            mIntent.putExtra("noti_type", jsonObject.getString("noti_type"));
            mIntent.putExtra("notify", "1");
            mIntent.putExtra(Admin_MainChat_Content.CHAT_NOTIFY, strChatNtify);//for chat admin maincontent,open directly chat class fragment
            mIntent.putExtra("usertype", jsonObject.getString("usertype"));
        } catch (Exception e) {
            LogCalls_Debug.d(TAG, "intentData  " + e.getMessage());
        }
    }

    private void check_Push_Intent() {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(Push_Notification.PUSH_JSON,jsonObject.toString());

        try {

            if (jsonObject.has("detail")) {
                editor.putString(Video_List_Customer.VIDEO_JSON, jsonObject.getString("detail"));
                editor.putString(Push_Notification.PUSH_CHAT_DETAIL, jsonObject.getString("detail"));
                editor.putString(Video_List_Customer.VIDEO_TYPE, "0");
            }

            if (jsonObject.has("threadid")) {

                editor.putString(Admin_MainChat.THREAD_ID, jsonObject.getString("threadid"));

                strChatNtify = "1";

            } else {
                strChatNtify = "0";
            }

            editor.commit();
        } catch (Exception e) {
            Log.d("json", "check_Push_Intent error "+e.getMessage());
        }
    }

    private PendingIntent createOnDismissedIntent(Context context, int notificationId) {

        Intent intent = new Intent(getApplicationContext(), SwipeNotificationBraodCastReciever.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), notificationId, intent, PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }


    private boolean isAppInForeground() {
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
                ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
                String foregroundTaskPackageName = foregroundTaskInfo.topActivity.getPackageName();

                return foregroundTaskPackageName.toLowerCase().equals(getPackageName().toLowerCase());
            } else {
                ActivityManager.RunningAppProcessInfo appProcessInfo = new ActivityManager.RunningAppProcessInfo();
                ActivityManager.getMyMemoryState(appProcessInfo);
                if (appProcessInfo.importance == IMPORTANCE_FOREGROUND || appProcessInfo.importance == IMPORTANCE_VISIBLE) {
                    return true;
                }
//                KeyguardManager km = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
//                // App is foreground, but screen is locked, so show notification
//                return km.inKeyguardRestrictedInputMode();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}


/////////Applying badge count on launcher icon of the app.Show to no of notifications
//            if (sharedPreferences.getInt("badge",0)==0){
//                SharedPreferences.Editor editor=sharedPreferences.edit();
//                editor.putInt("badge",1).commit();
//                ShortcutBadger.applyCount(FireBaseGetMessage.this, 1); //for 1.1.4+
//
//            }
//            else {
//                SharedPreferences.Editor editor=sharedPreferences.edit();
//                editor.putInt("badge",sharedPreferences.getInt("badge",0)+1).commit();
//                ShortcutBadger.applyCount(FireBaseGetMessage.this, sharedPreferences.getInt("badge",0)); //for 1.1.4+
//            }