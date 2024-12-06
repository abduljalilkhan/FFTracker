package com.khan.fftracker.Location_FusedAPI;

import static com.khan.fftracker.login_Stuffs.Login_New.TAG;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.khan.fftracker.Item_Interface.CommonStuffInterface;
import com.khan.fftracker.LogCalls.LogCalls_Debug;

import java.util.List;

public class GetCurrentLocation extends AppCompatActivity{
    private static final int INTERVAL = 1000*15;
    private static final int FASTEST_INTERVAL = 1000*15;
    private Activity activity;

    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    FusedLocationProviderClient mFusedLocationClient;

    CommonStuffInterface comman_stuff_interface;
    private static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 20;

    public GetCurrentLocation(Activity context, CommonStuffInterface stuffInterface) {
        activity = context;
        comman_stuff_interface=stuffInterface;
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
       //checkLocationPermission();
        Log.d(TAG, "GetCurrentLocation: ");

    }

    public void getCurrentLocation() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object
                                    Log.d(TAG, "getCurrentLocation onSuccess: "+location.getLongitude()+" londg "+location.getLatitude());
                                    comman_stuff_interface.commonStuffListener(location.getLatitude()+"loc"+location.getLongitude());
                                }
                            }
                        });
            }
        }

    }

    public Location getlocation() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
                                    // Logic to handle location object
                                    Log.d(TAG, "onSuccess: "+location.getLongitude()+" londg "+location.getLatitude());
                                }
                            }
                        });
            }
        }


        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(INTERVAL); // two minute interval
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                //Location Permission already granted
                mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, null);

                return mLastLocation;

            }
        }

      //  removeLocation();
        return mLastLocation;
    }

    public void removeLocation() {
        Handler handler=new Handler();
        handler.postDelayed(() -> {
            LogCalls_Debug.d(TAG, "removeLocation: ");
             mFusedLocationClient.removeLocationUpdates(mLocationCallback);

        },1000);
    }


    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                Log.d("json", "LocationCallback " + location.getLatitude() + " " + location.getLongitude());
                mLastLocation = location;
                Log.d(TAG, "getlocationLast: "+mLastLocation.toString());
                LogCalls_Debug.d(LogCalls_Debug.TAG,mLastLocation.getSpeed()+" speed");

                comman_stuff_interface.commonStuffListener(location.getLatitude()+"loc"+location.getLongitude());

                Handler handler=new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                       // Toast.makeText(activity,mLastLocation.getLatitude()+"",Toast.LENGTH_LONG).show();
                       // mFusedLocationClient.removeLocationUpdates(mLocationCallback);

                    }
                },1000);


            }
        }

    };


    ///////// Check permission for location getiing lat, long
    @TargetApi(Build.VERSION_CODES.M)
    public boolean checkLocationPermission() {

        Log.d("location", "checkLocationPermission");
        if (activity.checkSelfPermission( Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return false;
        } else {
            //doShowContacts();
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        Log.d("json", "onRequestPermissionsResult");
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ActivityCompat.checkSelfPermission(activity,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        Log.d("json", "PERMISSION_GRANTED");
                        //Request location updates:
                      //  startLocationLat_Long(getActivity(), googleApiClient, location);

                    }

                } else {
                    Log.d("json", "PERMISSION_denied");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
            }

        }
    }

}
