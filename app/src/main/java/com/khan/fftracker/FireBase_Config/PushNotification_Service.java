package com.khan.fftracker.FireBase_Config;

import static com.khan.fftracker.login_Stuffs.Login_New.TAG;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.google.firebase.iid.FirebaseInstanceId;
import com.khan.fftracker.AdminMyPCP.AdminDashBoard;
import com.khan.fftracker.Navigation_Drawer.Drawer;
import com.khan.fftracker.Network_Volley.AppSingleton;
import com.khan.fftracker.Network_Volley.HttpStringRequest;
import com.khan.fftracker.Network_Volley.Network_Stuffs;
import com.khan.fftracker.R;
import com.khan.fftracker.login_Stuffs.Login_Contstant;
import com.khan.fftracker.login_Stuffs.Push_Notification;

import org.json.JSONObject;

import java.util.HashMap;

///start service for new admin pending job
//if pending job >1 then show continuously alert dialogue
public class PushNotification_Service extends Service  {

    SharedPreferences sharedPreferences;

    //timer refersh every 30 secs
    Handler handler;
    Runnable runnable;
    Show_Push_Notification show_push_notification;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {


        Log.d(TAG+" Service PushNotifi", "onCreate Called");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sharedPreferences = getSharedPreferences(Login_Contstant.MY_PREFS, Context.MODE_PRIVATE);
        timer();
        show_push_notification= new Show_Push_Notification(Drawer.mActivity);
        //log.d("Location Service", "onStartCalled");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
        Log.d(TAG, "onDestroy: PushNotification_Service ");

    }




    //using to handler delay webservices call for 10 secs
    private void timer() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {

                services_Webservices();

                handler.postDelayed(this, 10000*3);
                Log.d(TAG, "run: timer ");

            }
        };
        handler.postDelayed(runnable, 10000*3);

    }

    private void services_Webservices() {

        HashMap<String, String> map = new HashMap<>();
        map.put("function", "adminhome");

        map.put("user_id", sharedPreferences.getString(Login_Contstant.USER_ID, null));
        map.put("pcp_user_id", sharedPreferences.getString(Login_Contstant.PCP_USER_ID, null));
        map.put("role_id", sharedPreferences.getString(Login_Contstant.ROLE_ID, null));
        map.put(Login_Contstant.AUTH_TOKEN, sharedPreferences.getString(Login_Contstant.AUTH_TOKEN, null));
        map.put("regid", "" + FirebaseInstanceId.getInstance().getToken());
        Log.d("json params", map.toString());
       HttpStringRequest stringRequest = new HttpStringRequest(Network_Stuffs.LOGIN_URL, map, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                int success;
                try {
                   JSONObject jsonObject = new JSONObject(response);
                    Log.d("JSonREsponse", String.valueOf(jsonObject));
                    success = jsonObject.getInt(Network_Stuffs.SUCCESS);
                    if (success == 1) {
                        if (!jsonObject.getString("pendingvideo_count").equals("0")){
                            // Show notification of push notification
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString(Push_Notification.PUSH_TITLE, "Pending jobs");
                            editor.putString(Push_Notification.PUSH_MESSAGE, "You have "+jsonObject.getString("pendingvideo_count") +" pending jobs. Click here to review them");
                            editor.putString(Push_Notification.PUSH_TYPE,"Service Video");
                            editor.putString(Push_Notification.PUSH_DIALOGUE_TITLE,"Pending Jobs Notification");
                            editor.putString(AdminDashBoard.VIDEO_TOTAL, jsonObject.getString("pendingvideo_count"));

                            editor.commit();
                            show_push_notification.notificationDialogue();

                            Intent local = new Intent();
                            local.setAction("com.hello.action");

                            sendBroadcast(local);
                        }
                    }

                } catch (Exception nu) {
                    Log.d("json", nu.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {


                try {
                    NetworkResponse networkResponse = error.networkResponse;
                     String errorMessage = getString(R.string.errorMessage);
                    if (networkResponse == null) {
                        if (error.getClass().equals(TimeoutError.class)) {
                            errorMessage = "Request timeout";
                        } else if (error.getClass().equals(NoConnectionError.class)) {
                            errorMessage = "Failed to connect server";
                        }
                    }

                } catch (NullPointerException nu) {
                    //log.d("Inbox messagedetail","NUll Adaptor");
                }

            }
        });
        stringRequest.setShouldCache(false);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppSingleton.getInstance().addToRequestQueue(stringRequest);

    }



}


