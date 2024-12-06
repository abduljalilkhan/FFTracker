package com.khan.fftracker.FireBase_Config;

import android.content.SharedPreferences;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Abdul Jalil Khan on 4/12/2017.
 */
public class FireBaseGetID extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {

        //getting registration token from firebase API
        String strToken= FirebaseInstanceId.getInstance().getToken();
        SharedPreferences sharedPreferences=getSharedPreferences(Login_Contstant.MY_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString("tokendevice",strToken);
        editor.commit();

       // Log.d("token",strToken);
    }
}
