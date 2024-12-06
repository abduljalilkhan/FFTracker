package com.khan.fftracker.FireBase_Config;

import static com.khan.fftracker.login_Stuffs.Login_New.TAG;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.firebase.iid.FirebaseInstanceId;
import com.khan.fftracker.Navigation_Drawer.Drawer_MyPCP;
import com.khan.fftracker.Network_Volley.AppSingleton;
import com.khan.fftracker.Network_Volley.HttpStringRequest;
import com.khan.fftracker.Network_Volley.Network_Stuffs;
import com.khan.fftracker.login_Stuffs.Login_Contstant;
import com.khan.fftracker.login_Stuffs.Push_Notification;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Abdul Jalil Khan on 4/17/2017.
 */
public class Push_Service extends Service {
    SharedPreferences sharedPreferences;
    private JSONObject jsonObject;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
       // Log.d("Service", "OnBind Service");
        // Toast.makeText(getApplicationContext(),"bind service",Toast.LENGTH_LONG).show();
        return null;
    }

    @Override
    public void onCreate() {
        Log.d(TAG+"Push_Service", "OnCreate Service");
        // Toast.makeText(getApplicationContext(),"Create service",Toast.LENGTH_LONG).show();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
       // Log.d("Service", "onStartCommand Service");
        try {
            Bundle bundle = intent.getExtras();

            // Toast.makeText(getApplicationContext(),"onStartCommand service",Toast.LENGTH_LONG).show();
            sharedPreferences = getSharedPreferences(Login_Contstant.MY_PREFS, Context.MODE_PRIVATE);
            DashBoard_Webservices();
            sharedPreferences.edit().putBoolean("notify", false).commit();
            Drawer_MyPCP.strNotify = null;
        }
        catch (NullPointerException nu){
            nu.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        // Toast.makeText(getApplicationContext(),"onDestroy service",Toast.LENGTH_LONG).show();
        Log.d(TAG+" Service", "Destroy Service");
    }

    private void DashBoard_Webservices() {
        HashMap<String, String> map = new HashMap<>();
        map.put("function", "pushnotificationreceived");
        map.put("ContractID", sharedPreferences.getString(Login_Contstant.CONTARCT_ID, null));
        map.put("CustomerID", sharedPreferences.getString(Login_Contstant.CUSTOMER_ID, null));
        map.put("user_id", sharedPreferences.getString(Login_Contstant.USER_ID, null));
        map.put("role_id", sharedPreferences.getString(Login_Contstant.ROLE_ID, null));
        map.put("regid", ""+FirebaseInstanceId.getInstance().getToken());
       // map.put("GeofenctID", "44");
        map.put("os", "android");
        map.put("device", "android");
        map.put("isVcsUser", sharedPreferences.getString(Login_Contstant.IS_VCS_USER,null));
        map.put(Login_Contstant.AUTH_TOKEN,sharedPreferences.getString(Login_Contstant.AUTH_TOKEN,null));


        try {
            for (int i = 0; i < FireBaseGetMessage.jsonObject.length(); i++) {

                map.put(FireBaseGetMessage.jsonObject.names().getString(i),
                        ""+FireBaseGetMessage.jsonObject.get(FireBaseGetMessage.jsonObject.names().getString(i)));

               // Log.d("QID",""+FireBaseGetMessage.jsonObject.get(FireBaseGetMessage.jsonObject.names().getString(i)));
            }
        }
            catch (JSONException e) {
                e.printStackTrace();
            }
        catch (NullPointerException nu){
            Log.d(TAG+" exception Push",nu.getMessage());
        }
        Log.d(TAG, "DashBoard_Webservices: "+map.toString());
        Log.d(TAG, "DashBoard_Webservices: pushnotificationreceived");

           // Log.d("Customer ID", sharedPreferences.getString(Login_Contstant.CONTARCT_ID, null) + " " +
              //  sharedPreferences.getString(Login_Contstant.CUSTOMER_ID, null) + " " + sharedPreferences.getString(Login_Contstant.USER_ID, null));

        //   progressDialog=progressDialog.show(getActivity(),"Loading","Your request is in progress",false,false);

        HttpStringRequest httpStringRequest = new HttpStringRequest(Network_Stuffs.LOGIN_URL, map, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //stop service itself calling this method
                stopSelf();
                int success;
                SharedPreferences.Editor editor=sharedPreferences.edit();
                try {
                    jsonObject = new JSONObject(response);
                    Log.d("Json Rspnse push srvice", String.valueOf(jsonObject));
                    success = jsonObject.getInt(Network_Stuffs.SUCCESS);
                    if (success == 1) {
                        if (jsonObject.getInt("isUrl")!=0){

                            editor.putString(Push_Notification.PUSH_URL,jsonObject.getString("LinkUrl"));
                            editor.putBoolean(Push_Notification.PUSH_is_URL,true);
                            editor.commit();

                        }
                        else {
//

                           // editor.putString(Push_Notification.PUSH_URL,str);
                            editor.putString(Push_Notification.PUSH_URL,jsonObject.getString("LinkHtml"));
                            editor.putBoolean(Push_Notification.PUSH_is_URL,false);
                            editor.commit();

                        }
                       // Toast.makeText(getApplicationContext(), "data in service", Toast.LENGTH_LONG).show();

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (NullPointerException nu) {
                    //Toast.makeText(getActivity(),"Application crased",Toast.LENGTH_LONG).show();
                    Log.d(TAG, "app crased");
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                stopSelf();


            }
        });
        httpStringRequest.setShouldCache(false);
        httpStringRequest.setRetryPolicy(new DefaultRetryPolicy(0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppSingleton.getInstance().addToRequestQueue(httpStringRequest);
    }
}
