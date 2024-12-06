package com.khan.fftracker.Network_Volley;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.iid.FirebaseInstanceId;
import com.khan.fftracker.DashBoard.Dashboard_Constants;
import com.khan.fftracker.Prefrences.Prefs_Operation;
import com.khan.fftracker.R;
import com.khan.fftracker.login_Stuffs.Login_Contstant;

import java.util.HashMap;



public class IsAdmin {
    private Activity activity;
    private SharedPreferences sharedPreferences;

    public IsAdmin(Activity activiy) {
        activity = activiy;
        sharedPreferences = activity.getSharedPreferences(Login_Contstant.MY_PREFS, Context.MODE_PRIVATE);

    }

    public IsAdmin() {

    }

    public boolean isAdmin_or_Customer() {
        return sharedPreferences.getString(Login_Contstant.ROLE_ID, null) != null && (sharedPreferences.getString(Login_Contstant.ROLE_ID, null).equals("3")
                || sharedPreferences.getString(Login_Contstant.ROLE_ID, null).equals("1") || sharedPreferences.getString(Login_Contstant.ROLE_ID, null).equals("2") ||
                sharedPreferences.getString(Login_Contstant.ROLE_ID, null).equals("7") || sharedPreferences.getString(Login_Contstant.ROLE_ID, null).equals("8") ||
                sharedPreferences.getString(Login_Contstant.ROLE_ID, null).equals("9"));
    }

    public HashMap<String, String> hashMap_Params(HashMap<String, String> map) {


        map.put("user_id", Prefs_Operation.readPrefs(Login_Contstant.USER_ID, ""));
        map.put("role_id", Prefs_Operation.readPrefs(Login_Contstant.ROLE_ID, ""));
        map.put("os", "android");
        map.put(Login_Contstant.AUTH_TOKEN, Prefs_Operation.readPrefs(Login_Contstant.AUTH_TOKEN, ""));

        map.put("regid", "FirebaseInstanceId.getInstance().getToken()");


        // if admin is logged in
        if (Prefs_Operation.readPrefs(Login_Contstant.ROLE_ID, null) != null) {
            if (Prefs_Operation.readPrefs(Login_Contstant.ROLE_ID, "").equals("3")
                    || Prefs_Operation.readPrefs(Login_Contstant.ROLE_ID, "").equals("1")
                    || Prefs_Operation.readPrefs(Login_Contstant.ROLE_ID, "").equals("2")
                    || Prefs_Operation.readPrefs(Login_Contstant.ROLE_ID, "").equals("7")
                    || Prefs_Operation.readPrefs(Login_Contstant.ROLE_ID, "").equals("8")
                    || Prefs_Operation.readPrefs(Login_Contstant.ROLE_ID, "").equals("9")) {

                map.put("pcp_user_id", Prefs_Operation.readPrefs(Login_Contstant.PCP_USER_ID, ""));
                map.put("isAdmin", "1");
                Log.d("Admin", "json");

                return map;

            }
            //// Customer is logged in
            else {
                map.put("isAdmin", "0");

                map.put("ContractID", Prefs_Operation.readPrefs(Login_Contstant.CONTARCT_ID, ""));
                map.put("CustomerID", Prefs_Operation.readPrefs(Login_Contstant.CUSTOMER_ID, ""));
                map.put("isVcsUser", Prefs_Operation.readPrefs(Login_Contstant.IS_VCS_USER, ""));

                ///if customer is logged in
                if (Prefs_Operation.readPrefs(Login_Contstant.GUEST_PREFS, false)) {
                    map.put("IsGuest", "1");

                } else {
                    map.put("IsGuest", "0");
                }
                Log.d("Customer", "json");
                return map;
            }
        }
        map.put("regid", FirebaseInstanceId.getInstance().getToken());



        return map;
    }

    //show/hide loader view
    public void showhideLoader(int showhide) {

        if (showhide==View.VISIBLE){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    activity.findViewById(R.id.imgLoader).setVisibility(showhide);
                }
            }, 1000);
        }
        else {

            activity.findViewById(R.id.imgLoader).setVisibility(showhide);
        }
    }

    ////set loader gif to image view
    public void setLoader() {
        ImageView  imgLoader = (ImageView) activity.findViewById(R.id.imgLoader);
        imgLoader.setVisibility(View.VISIBLE);
        Glide.with(activity).load(Prefs_Operation.readPrefs(Dashboard_Constants.DEFAULT_LOADER,"http://")).into(imgLoader);
    }

    ///get loader imageview instance
    public ImageView getLoaderView() {
        ImageView  imgLoader = (ImageView) activity.findViewById(R.id.imgLoader);
        return imgLoader;
    }
}
