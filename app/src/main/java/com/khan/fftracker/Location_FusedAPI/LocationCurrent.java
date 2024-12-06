package com.khan.fftracker.Location_FusedAPI;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.JobIntentService;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.iid.FirebaseInstanceId;
import com.khan.fftracker.DashBoard.DashboardJsonResponse;
import com.khan.fftracker.LogCalls.LogCalls_Debug;
import com.khan.fftracker.Network_Volley.AppSingleton;
import com.khan.fftracker.Network_Volley.HttpStringRequest;
import com.khan.fftracker.Network_Volley.Network_Stuffs;
import com.khan.fftracker.login_Stuffs.Login_Contstant;
import com.khan.fftracker.tracker.TrackerConstant;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LocationCurrent extends JobIntentService implements LocationListener, GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 20;
    LocationRequest locationRequest;
    GoogleApiClient googleApiClient;
    Location currentLocation;

    private static final int INTERVAL = 10000*3;
    private static final int FASTEST_INTERVAL = 10000*3;

    Handler handler = new Handler();
    Runnable runnable;

    SharedPreferences sharedPreferences;
    private JSONObject jsonObject;
    public static double location_Lat = 0;
    public static double location_Long = 0;

    static final int JOB_ID = 100;
    private Handler  handler1 = new Handler();
    private Runnable runnable1;

    int level = -1;

     static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, LocationCurrent.class, JOB_ID, work);
        //Log.d("json enqueueWork", "enqueueWork Called");
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        sharedPreferences = getSharedPreferences(Login_Contstant.MY_PREFS, Context.MODE_PRIVATE);
         if (sharedPreferences.getString(TrackerConstant.TRACKER_BACKGROUND,"0").equals("1")) {
             batteryLevel();
         }


                if (checkGooglePlayServices()) {
                    createLocationRequest();
                    if (googleApiClient == null) {
                        googleApiClient = new GoogleApiClient.Builder(LocationCurrent.this)
                                .addApi(LocationServices.API)
                                .addConnectionCallbacks(LocationCurrent.this)
                                .addOnConnectionFailedListener(LocationCurrent.this)
                                .build();
                    }
                }
             //   Log.d("json", "run:onHandleWork2");



                if (googleApiClient != null) {
                    googleApiClient.connect();
                    //log.d("Location Service", "onStartCalled Connected");
                }

                if (googleApiClient.isConnected()){
                   // startLocationLat_Long();
                }
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Location_Current ");

        try {
            if (sharedPreferences.getString(TrackerConstant.TRACKER_BACKGROUND,"0").equals("1")) {
                unregisterReceiver(batteryLevelReceiver);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setSmallestDisplacement(3)
                .setInterval(INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);

    }

    private boolean checkGooglePlayServices() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getApplicationContext());
        if (ConnectionResult.SUCCESS == status) {
            //log.d("GooglePlay Service", "available");
            return true;
        } else {
            //log.d("GooglePlay Service", "not availble");
            return false;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG+"Location Service", "Connected");
        startLocationLat_Long();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG+"Location Service", "connection Suspended");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG+"Location Service", "on Location Chnaged");
          // Toast.makeText(getApplicationContext(),"Location Changed",Toast.LENGTH_LONG).show();

        currentLocation = location;
        if (currentLocation != null) {
            location_Lat = currentLocation.getLatitude();
            location_Long = currentLocation.getLongitude();
            //log.d("Location Service", "on Location Chnaged Not Null");
            Log.d("jsoncurrent Location", "Lat" + currentLocation.getLatitude());
            Log.d("json current Location", "Long" + currentLocation.getLongitude());
            //callToWebservice();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG+"Location Service", "Connection failed");
    }

    public static boolean checkPermission(final Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationLat_Long() {
        try {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (!checkPermission(this)) {
                    return;
                }
            }

            //// get last location of the device
            currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

            //log.d("Location Service", "Location Update started");

            if (currentLocation != null) {
                //log.d("Location Service", "start location updated");
                Log.d("json Updte Loction_Crnt", "Lat" + currentLocation.getLatitude());
                //log.d("Update Location", "Long" + currentLocation.getLongitude());
                callToWebservice();
            }

            /// Periodically request for location updates after some time
          //  LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        } catch (Exception v) {
            v.printStackTrace();
            //log.d("Location lat long","Exception");
        }

    }
    private void batteryLevel() {

        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(batteryLevelReceiver, batteryLevelFilter);

    }
    private final BroadcastReceiver batteryLevelReceiver = new BroadcastReceiver() {
        public void onReceive(@NotNull Context  context, @NotNull Intent intent) {

            int rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
            calculteBatteryPercentage(rawlevel,scale);
        }
    };

    private void calculteBatteryPercentage(int rawlevel, int scale) {
//        int level = -1;
        if (rawlevel >= 0 && scale > 0) {
            level = rawlevel * 100 / scale;
        }
       LogCalls_Debug.d(TAG, level+" battery");
    }


    private void callToWebservice() {

        try {


            HashMap<String, String> map = new HashMap<>();
            map.put("function", "geofence");
            map.put("ContractID", sharedPreferences.getString(Login_Contstant.CONTARCT_ID, null));
            map.put("CustomerID", sharedPreferences.getString(Login_Contstant.CUSTOMER_ID, null));
            map.put("user_id", sharedPreferences.getString(Login_Contstant.USER_ID, null));
            map.put("role_id", sharedPreferences.getString(Login_Contstant.ROLE_ID, null));
            map.put("DealerID", sharedPreferences.getString(Login_Contstant.DEALER_ID, null));
            map.put("device_id", "" + FirebaseInstanceId.getInstance().getToken());
            map.put("device", "android");
            map.put("lat", "" + currentLocation.getLatitude());
            map.put("lng", "" + currentLocation.getLongitude());
            map.put("fcm", "1");
            map.put("os", "android");
            map.put("isVcsUser", sharedPreferences.getString(Login_Contstant.IS_VCS_USER, null));
            map.put(Login_Contstant.AUTH_TOKEN, sharedPreferences.getString(Login_Contstant.AUTH_TOKEN, null));

            map.put("activityRecognition", "still");


            if (sharedPreferences.getBoolean(Login_Contstant.GUEST_PREFS, false)) {
                map.put("IsGuest", "1");

            } else {
                map.put("IsGuest", "0");
            }

            if (sharedPreferences.getString(TrackerConstant.TRACKER_BACKGROUND,"0").equals("1")) {
                map.put("Battery", level+"");
            }
            //check if starguard(atoverse) user
            //1==starguard user
            String isAutoVerse=DashboardJsonResponse.getDashboardJsonResponse().getString("EnableAwareWithStargard");
            if (isAutoVerse.equals("1")){
                map.put("StarGurardAvere", isAutoVerse);
            }


            Log.d("json", "callToWebservice:current " + map.toString());

            HttpStringRequest httpStringRequest = new HttpStringRequest(Network_Stuffs.LOGIN_URL, map, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {

                    try {
                        jsonObject = new JSONObject(response);
                       // Toast.makeText(getApplicationContext(),jsonObject.toString(),Toast.LENGTH_LONG).show();
                        Log.d("Json Response current", "" + jsonObject);

                    } catch (JSONException e) {
                        e.printStackTrace();

                    } catch (NullPointerException nu) {

                        //log.d("App crased", "app crased");
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
                    } catch (NullPointerException nu) {
                        //log.d("App log", "Problem in FDashBoard");
                    }
                }
            });
            httpStringRequest.setShouldCache(false);
            httpStringRequest.setRetryPolicy(new DefaultRetryPolicy(0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            AppSingleton.getInstance().addToRequestQueue(httpStringRequest);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}

//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        sharedPreferences = getSharedPreferences(Login_Contstant.MY_PREFS, Context.MODE_PRIVATE);
//        if (googleApiClient != null) {
//            googleApiClient.connect();
//            //log.d("Location Service", "onStartCalled Connected");
//        }
//
//        if (googleApiClient.isConnected()){
//            startLocationLat_Long();
//        }
//        //log.d("Location Service", "onStartCalled");
//        return START_STICKY;
//    }
//////////////////////////////////////////////////////////////////////////////////////////////////////
