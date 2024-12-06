package com.khan.fftracker.Permission_Granted

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.khan.fftracker.logCalls.LogCalls_Debug


class LocationPermission : Fragment() {
    private val TAG = LocationPermission::class.simpleName
    private lateinit var permissionListener:PermissionListener
    private var isGranted: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    fun setPermissionGrantedListener(permissionGranted: PermissionListener){
        permissionListener=permissionGranted
    }

    fun setLocationPermission() {

        locationPermission.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        ))
    }

    private val locationPermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        isGranted = true
        for (a in result.values) {
            isGranted = isGranted && a
        }
        if (isGranted){
            permissionListener.permissionGrantedListener("accessFine")
        }
        else{
            permissionListener.permissionGrantedListener("NoAccessFine")
        }
    }
    /* ------------------------------------------------------------------------------------------------------------------------------ */
    /*Check backgound is set to " Allow all time" or not if not selected then call setBackGroundLocPermission
     function to open permission seting to set "Allow all time" manually*/
    @RequiresApi(Build.VERSION_CODES.Q)
    fun isCheckBackgroundLocPermission(): Boolean {
        if (ContextCompat.checkSelfPermission(requireActivity(),Manifest.permission.ACCESS_BACKGROUND_LOCATION)!=PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.ACCESS_BACKGROUND_LOCATION)){
                return true
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return true
            }
        }
        permissionListener.permissionGrantedListener("accessBackGround")
        return false
    }
 /* ------------------------------------------------------------------------------------------------------------------------------ */

    @RequiresApi(Build.VERSION_CODES.Q)
    fun setBackGroundLocPermission(){
        if (ContextCompat.checkSelfPermission(requireActivity(),Manifest.permission.ACCESS_BACKGROUND_LOCATION)!=PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(),Manifest.permission.ACCESS_BACKGROUND_LOCATION)){
                backGroundLocPermission.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                return
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // We can't show tri-state dialog when permission is already granted.
                // So, go to the location permission settings screen directly.
                showLocationPermissionSettingsDashboard()
                return
            }
        }
        permissionListener.permissionGrantedListener("accessBackGround")
    }

    private val backGroundLocPermission=registerForActivityResult(ActivityResultContracts.RequestPermission()){ result ->
        if (result){
            permissionListener.permissionGrantedListener("accessBackGround")
        }
        else{
            permissionListener.permissionGrantedListener("NoAccessBackGround")
        }
    }


    /* ------------------------------------------------------------------------------------------------------------------------------ */
    private fun showLocationPermissionSettingsDashboard() {
        LogCalls_Debug.d(TAG," showLocationPermissionSettingsDashboard")
        val intent=Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${BuildConfig.APPLICATION_ID}"))

        settingLauncher.launch(intent)

        // requireActivity().startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:${BuildConfig.APPLICATION_ID}")))
    }



    private val settingLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult()){ result ->

        if (result.resultCode== Activity.RESULT_OK){
            permissionListener.permissionGrantedListener("accessBackGround")
        }
        else{
            permissionListener.permissionGrantedListener("NoAccessBackGround")
        }

    }

    /* ------------------------------------------------------------------------------------------------------------------------------ */


     fun getBatteryOptimize(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val pm = requireActivity().getSystemService(Context.POWER_SERVICE) as PowerManager?
            if (!pm!!.isIgnoringBatteryOptimizations(BuildConfig.APPLICATION_ID)) {
                permissionListener.permissionGrantedListener("isBatteryOptimize")
                return true
            }

            permissionListener.permissionGrantedListener("batteryOptimize")
            return false

        }
         permissionListener.permissionGrantedListener("batteryOptimize")
        return false
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun setBatteryOptimizeAllow() {
       // if (getBatteryOptimize()){
            val intent = Intent()
            intent.action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
            intent.data = Uri.parse("package:${BuildConfig.APPLICATION_ID}")
            batteryOptimizePermission.launch(intent)
          //  return
       // }
       // permissionListener.permissionGrantedListener("batteryOptimize")

    }



    @RequiresApi(Build.VERSION_CODES.N)
    val batteryOptimizePermission = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            permissionListener.permissionGrantedListener("batteryOptimize")
            LogCalls_Debug.d(LogCalls_Debug.TAG, "batteryOptimizePermission true")
        }
        else{
            permissionListener.permissionGrantedListener("NOBatteryOptimize")

            LogCalls_Debug.d(LogCalls_Debug.TAG, "batteryOptimizePermission false")
        }
//        if (!getBatteryOptimize()){
//            permissionListener.permissionGrantedListener("batteryOptimize")
//        }
        LogCalls_Debug.d(LogCalls_Debug.TAG, " getBatteryOptimize "+getBatteryOptimize())

    }



    /* ------------------------------------------------------------------------------------------------------------------------------ */


    fun setEnableGPS() {

        checkGPS()
    }
    private fun checkGPS() {
        val locationRequest = LocationRequest.create()
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val settingsClient = LocationServices.getSettingsClient(requireActivity())
        val task = settingsClient.checkLocationSettings(builder.build())

        task.addOnSuccessListener(requireActivity()) {
            LogCalls_Debug.d(LogCalls_Debug.TAG, "GPS_main " + "OnSuccess")
            permissionListener.permissionGrantedListener("enableGps")

        }
        task.addOnFailureListener(requireActivity()) { e ->
            LogCalls_Debug.d(LogCalls_Debug.TAG, "GPS_main" + " GPS off")
            // GPS off
            if (e is ResolvableApiException) {
                try {

                    val intentSenderRequest = IntentSenderRequest.Builder(e.resolution).build()
                    resolutionForResult.launch(intentSenderRequest)
                } catch (e1: IntentSender.SendIntentException) {
                    e1.printStackTrace()
                }
            }
        }
    }




    private val resolutionForResult = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { activityResult ->
        if (activityResult.resultCode == Activity.RESULT_OK) {
            permissionListener.permissionGrantedListener("enableGps")
            //startLocationUpdates() or do whatever you want
            LogCalls_Debug.d(LogCalls_Debug.TAG, "resolutionForResult " + "OnSuccess")
        }
        else {
            permissionListener.permissionGrantedListener("NoEnableGps")
            LogCalls_Debug.d(LogCalls_Debug.TAG, "resolutionForResult " + "failed")
        }
    }
}