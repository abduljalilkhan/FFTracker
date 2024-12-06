package com.khan.fftracker.Location_FusedAPI;

import static com.khan.fftracker.login_Stuffs.Login_New.TAG;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.khan.fftracker.Navigation_Drawer.Drawer;
import com.khan.fftracker.Network_Volley.AppSingleton;
import com.khan.fftracker.Network_Volley.HttpStringRequest;
import com.khan.fftracker.Network_Volley.Network_Stuffs;
import com.khan.fftracker.login_Stuffs.Login_Contstant;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

//public class Location_Worker{
//
//}
public class Location_Worker extends Worker implements LocationListener, GoogleApiClient.ConnectionCallbacks
        , GoogleApiClient.OnConnectionFailedListener  {



    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 20;
    LocationRequest locationRequest;
    GoogleApiClient googleApiClient;
    Location currentLocation;

    private static final int INTERVAL = 3000;
    private static final int FASTEST_INTERVAL = 3000;

   // Handler handler = new Handler();
    Runnable runnable;
    SharedPreferences sharedPreferences;
    private JSONObject jsonObject;
    public static double location_Lat = 0;
    public static double location_Long = 0;

    static final int JOB_ID = 1000;
   // private Handler  handler1 = new Handler();
    private Runnable runnable1;

    FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest mLocationRequest;

    public Location_Worker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);

//        Log.d("json Location_Worker", "Location_Worker");
//        if (checkGooglePlayServices()) {
//            createLocationRequest();
//            if (googleApiClient == null) {
//                googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
//                        .addApi(LocationServices.API)
//                        .addConnectionCallbacks(this)
//                        .addOnConnectionFailedListener(this)
//                        .build();
//            }
//        }
//
//        sharedPreferences = getApplicationContext().getSharedPreferences(Login_Contstant.MY_PREFS, Context.MODE_PRIVATE);
//        if (googleApiClient != null) {
//            googleApiClient.connect();
//            //log.d("Location Service", "onStartCalled Connected");
//        }


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
    }

    @NonNull
    @Override
    public Worker.Result doWork() {

        // Do the work here--in this case, compress the stored images.
        // In this example no parameters are passed; the task is
        // assumed to be "compress the whole library."
      //  myCompress();



        Log.d("json Location_Worker", "Location_Worker");
//        if (checkGooglePlayServices()) {
//            createLocationRequest();
//            if (googleApiClient == null) {
//                googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
//                        .addApi(LocationServices.API)
//                        .addConnectionCallbacks(this)
//                        .addOnConnectionFailedListener(this)
//                        .build();
//            }
//        }
//
//        sharedPreferences = getApplicationContext().getSharedPreferences(Login_Contstant.MY_PREFS, Context.MODE_PRIVATE);
//        if (googleApiClient != null) {
//            googleApiClient.connect();
//            //log.d("Location Service", "onStartCalled Connected");
//        }
//
//
//
//
//
//
//
//
//
//        Log.d("json doWork", "doWork");
//        if (googleApiClient.isConnected()){
//            startLocationLat_Long();
//        }

        Handler handler = new Handler(Looper.getMainLooper());

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Run your task here
                getlocation();

            }
        }, 1000 );


        // Indicate success or failure with your return value:
       // return Result.SUCCESS;
        return Result.success();

        // (Returning RETRY tells WorkManager to try this task again
        // later; FAILURE says not to try again.)
    }


    private void getlocation() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(Drawer.mActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(Drawer.mActivity, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object
                                    Log.d(TAG, "onSuccess worker: "+location.getLongitude()+" londg woker "+location.getLatitude());
                                }
                            }
                        });
            }
        }


        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(3000); // two minute interval
        mLocationRequest.setFastestInterval(3000);
          mLocationRequest.setSmallestDisplacement(3);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(Drawer.mActivity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);
                Log.d(TAG, "onSuccess worker: requestLocationUpdates");

            }
        }

        //  removeLocation();



    }


    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                Log.d("json", "Location: worker " + location.getLatitude() + " " + location.getLongitude());
               // mLastLocation = location;
             //   Log.d(TAG, "getlocation: "+mLastLocation.toString());

               // comman_stuff_interface.comman_Stuff(location.getLatitude()+"loc"+location.getLongitude());

                Handler handler=new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                       // mFusedLocationClient.removeLocationUpdates(mLocationCallback);

                    }
                },1000);


            }
        }

    };



//    public void onCreate() {
//        super.onCreate();
//        if (checkGooglePlayServices()) {
//            createLocationRequest();
//            if (googleApiClient == null) {
//                googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
//                        .addApi(LocationServices.API)
//                        .addConnectionCallbacks(this)
//                        .addOnConnectionFailedListener(this)
//                        .build();
//            }
//        }
//
//        sharedPreferences = getSharedPreferences(Login_Contstant.MY_PREFS, Context.MODE_PRIVATE);
//        if (googleApiClient != null) {
//            googleApiClient.connect();
//            //log.d("Location Service", "onStartCalled Connected");
//        }
//        Log.d("json Service on_Current", "onCreate Called");
//    }



//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        Log.d(TAG, "onDestroy: Location_Current ");
//        handler.removeCallbacks(runnable);
//        if (googleApiClient != null) {
//            googleApiClient.disconnect();
//        }
//
////        Intent broadcastIntent = new Intent(this, RestartService_Reciver.class);
////
////        sendBroadcast(broadcastIntent);
//    }

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
        //log.d("Location Service", "Connected");

        startLocationLat_Long();


    }

    public static boolean checkPermission(final Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    private void startLocationLat_Long() {
        try {

//        }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                if (!checkPermission(getApplicationContext())) {
                    return;
                }
            }
            /// Periodically request for location updates after some time

            //// get last location of the device

            currentLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

            //log.d("Location Service", "Location Update started");

            if (currentLocation != null) {
                //log.d("Location Service", "start location updated");
                Log.d("json Updte Loc_Worker", "Lat" + currentLocation.getLatitude());
                //log.d("Update Location", "Long" + currentLocation.getLongitude());
                callToWebservice();
               // Toast.makeText(getApplicationContext(),"toast",Toast.LENGTH_LONG).show();
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        } catch (Exception v) {
            v.printStackTrace();
            //log.d("Location lat long","Exception");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        //log.d("Location Service", "connection Suspended");
    }

    @Override
    public void onLocationChanged(Location location) {
        //log.d("Location Service", "on Location Chnaged");
        //   Toast.makeText(getApplicationContext(),"Location Changed",Toast.LENGTH_LONG).show();

        currentLocation = location;
        if (currentLocation != null) {
            location_Lat = currentLocation.getLatitude();
            location_Long = currentLocation.getLongitude();
            //log.d("Location Service", "on Location Chnaged Not Null");
            Log.d("jsoncurrent Loc_Worker", "Lat" + currentLocation.getLatitude());
            Log.d("json current Loc_Worker", "Long" + currentLocation.getLongitude());
            callToWebservice();
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //log.d("Location Service", "Connection failed");
    }


    private void callToWebservice() {

       // Toast.makeText(getApplicationContext(),"toast",Toast.LENGTH_LONG).show();

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

        Log.d("json", "callToWebservice:Location_Worker " + map.toString());
        HttpStringRequest httpStringRequest = new HttpStringRequest(Network_Stuffs.LOGIN_URL, map, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    jsonObject = new JSONObject(response);
                    Log.d("Json Response", "" + jsonObject);

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
    }

}


