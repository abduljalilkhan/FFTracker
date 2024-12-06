package com.khan.fftracker.Location_FusedAPI;

import static com.khan.fftracker.login_Stuffs.Login_New.TAG;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

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
import com.khan.fftracker.Network_Volley.AppSingleton;
import com.khan.fftracker.Network_Volley.HttpStringRequest;
import com.khan.fftracker.Network_Volley.Network_Stuffs;
import com.khan.fftracker.login_Stuffs.Login_Contstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Abdul Jalil Khan on 4/18/2017.
 */
public class Location_Long_Lat extends Service implements LocationListener, GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener {

    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 20;
    LocationRequest locationRequest;
    GoogleApiClient googleApiClient;
    Location currentLocation;

    private static final int INTERVAL = 7000;
    private static final int FASTEST_INTERVAL = 5000;

    Handler handler = new Handler();
    Runnable runnable;
    SharedPreferences sharedPreferences;
    private JSONObject jsonObject;
    public static double location_Lat=0;
    public static double location_Long=0;

    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

        if (checkGooglePlayServices()) {
            createLocationRequest();
            if (googleApiClient == null) {
                googleApiClient = new GoogleApiClient.Builder(this)
                        .addApi(LocationServices.API)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .build();
            }
        }
        Log.d("json Service LocLongLat", "onCreate Called");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sharedPreferences = getSharedPreferences(Login_Contstant.MY_PREFS, Context.MODE_PRIVATE);
        if (googleApiClient != null) {
            googleApiClient.connect();
            //log.d("Location Service", "onStartCalled Connected");
        }

        if (googleApiClient.isConnected()){
            startLocationLat_Long();
        }

//        String input = intent.getStringExtra("inputExtra");
//        createNotificationChannel();
//        Intent notificationIntent = new Intent(this, Landing_Activity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this,
//                0, notificationIntent, 0);
//        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setContentTitle("Foreground Service")
//                .setContentText(input)
//                .setSmallIcon(R.drawable.logo_notify)
//                .setContentIntent(pendingIntent)
//                .build();
//        startForeground(1, notification);

        //log.d("Location Service", "onStartCalled");
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: Location_Long_Lat ");
//        handler.removeCallbacks(runnable);
//        if (googleApiClient != null) {
//            googleApiClient.disconnect();
//        }

//        Intent broadcastIntent = new Intent(this, RestartService_Reciver.class);
//
//        sendBroadcast(broadcastIntent);
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

//        runnable = new Runnable() {
//            @Override
//            public void run() {
//                startLocationLat_Long();
//                handler.postDelayed(this, 10000);
//            }
//        };
//
//        handler.postDelayed(runnable, 10000);

    }

    public static boolean checkPermission(final Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationLat_Long() {
        try {
            //log.d("Location Service", "startLocation Lat long");
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//
//            ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//        }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (!checkPermission(this)) {
                    return;
                }
            }
            /// Periodically request for location updates after some time

            //// get last location of the device

            currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

            //log.d("Location Service", "Location Update started");

            if (currentLocation != null) {
                //log.d("Location Service", "start location updated");
               Log.d("json Update Location", "Lat" + currentLocation.getLatitude());
                //log.d("Update Location", "Long" + currentLocation.getLongitude());
                callToWebservice();
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        }
        catch (Exception v){
            v.printStackTrace();
            //log.d("Location lat long","Exception");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG+"Location Service", "connection Suspended");
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG+"Location Service", "on Location Chnaged");
          // Toast.makeText(getApplicationContext(),"Location Changed",Toast.LENGTH_LONG).show();

        currentLocation=location;
        if (currentLocation!=null){
            location_Lat=currentLocation.getLatitude();
            location_Long=currentLocation.getLongitude();
            //log.d("Location Service", "on Location Chnaged Not Null");
            Log.d("jsoncurrent Location","Lat"+currentLocation.getLatitude());
            Log.d("json current Location","Long"+currentLocation.getLongitude());
            callToWebservice();
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG+"Location Service", "Connection failed");
    }


    private void callToWebservice() {

        try{
        HashMap<String, String> map = new HashMap<>();
        map.put("function", "geofence");
        map.put("ContractID", sharedPreferences.getString(Login_Contstant.CONTARCT_ID, null));
        map.put("CustomerID", sharedPreferences.getString(Login_Contstant.CUSTOMER_ID, null));
        map.put("user_id", sharedPreferences.getString(Login_Contstant.USER_ID, null));
        map.put("role_id", sharedPreferences.getString(Login_Contstant.ROLE_ID, null));
        map.put("DealerID", sharedPreferences.getString(Login_Contstant.DEALER_ID, null));
        map.put("device_id", ""+FirebaseInstanceId.getInstance().getToken());
        map.put("device", "android");
        map.put("lat",""+currentLocation.getLatitude());
        map.put("lng", ""+currentLocation.getLongitude());
        map.put("fcm", "1");
        map.put("os", "android");
        map.put("isVcsUser", sharedPreferences.getString(Login_Contstant.IS_VCS_USER,null));
        map.put(Login_Contstant.AUTH_TOKEN,sharedPreferences.getString(Login_Contstant.AUTH_TOKEN,null));

        Log.d("json", "callToWebservice: "+map.toString());
        HttpStringRequest httpStringRequest = new HttpStringRequest(Network_Stuffs.LOGIN_URL, map, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    jsonObject = new JSONObject(response);
                    Log.d("Json Response LocLngLat",""+jsonObject);

                } catch (JSONException e) {
                    e.printStackTrace();

                } catch (NullPointerException nu) {
                    Log.d(TAG, "onResponse: "+nu.getMessage());
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

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}

