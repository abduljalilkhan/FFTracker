package com.khan.fftracker.Permission_Granted

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.khan.fftracker.logCalls.LogCalls_Debug
import com.khan.fftracker.logCalls.LogCalls_Debug.TAG


class PermissionFragmentKotlin : Fragment() {

    val TAG_PERMISSIONS_FRAGMENT = "permissionsKotlin"

    val REQUEST_ID_MULTIPLE_PERMISSIONS = 101
    val ALL_FILES_PERMISSION = 229
    val PHONE_REQUEST = 212
    private lateinit var permissionListener: PermissionListener
    private lateinit var permissionsFragment: PermissionFragmentKotlin
    var allgranted: Boolean = false

//
//    private lateinit var activityResultLauncher: ActivityResultLauncher<Array<String>>

    init {
//        registerPermissinRequest()
    }

    companion object {
        fun newInstance(): PermissionFragmentKotlin {
            return PermissionFragmentKotlin()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setRetainInstance(true)
    }

    /**
     * Sets the listener on which we will call permissionListnerGranted()
     * @param permissionListner pointer to the class implementing the PermissionsFragment.PermissionListner
     */
    fun setPermissionGrantedListener(permissionGranted: PermissionListener) {
        permissionListener = permissionGranted
    }


    ///check permissions for camera,storage and record(microphone) as well as storage permission device running
    ///on above OS version 10
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun isPermissionGranted() {
        // Request to camera ,storage and record audio permission
        //if all permissions are granted then call listener interface to invoke activity/fragment for further action
        cameraGalleryRequestPermissions()

        // hasPermissions(appPermission)
    }

    // util method
//    private fun hasPermissions(permissions: Array<String>): Boolean = permissions.all {
//        ActivityCompat.checkSelfPermission(this.requireActivity(), it) == PackageManager.PERMISSION_GRANTED
//    }
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun cameraGalleryRequestPermissions() {
        Log.d(TAG, "checkAndRequestPermissions: " + "")
        val appPermission = arrayOf(
                Manifest.permission.CAMERA,

                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.READ_MEDIA_VIDEO)

        activityResultLauncher.launch(permissions())

        ActivityCompat.requestPermissions(requireActivity(),
                permissions(),
                1);

    }

    var storage_permissions = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    )

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    var storage_permissions_33 = arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.CAMERA

    )

    fun permissions(): Array<String> {
        val p: Array<String>
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            p = storage_permissions_33
        } else {
            p = storage_permissions
        }
        return p
    }


    val activityResultLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        LogCalls_Debug.d(TAG + " activityResultLauncher", "" + result.toString())
        allgranted = true
        for (a in result.values) {
            allgranted = allgranted && a
        }
        if (allgranted) {
            permissionGranted()
            //  Toast.makeText(activity, "Permission granted", Toast.LENGTH_LONG).show()
            return@registerForActivityResult
        }

//        val granted = result.entries.all {
//            it.value == true
//        }
//        if (granted) {
//          //  displayCameraFragment()
//        }
    }

    /**
     * Called upon the permissions being granted. Notifies the permission listener.
     */
    fun permissionGranted() {
        Log.d(TAG, "permissionGranted: ")
        permissionListener.permissionGrantedListener("storage")

    }

    fun checkPhoneCallPermissions() {
        permissionPhoneCall.launch(Manifest.permission.CALL_PHONE)
    }

    var permissionPhoneCall = registerForActivityResult(RequestPermission()) { result ->
        if (result) {
            permissionListener.permissionGrantedListener("phoneCall")
        } else {
            Toast.makeText(activity, "Permission Denied", Toast.LENGTH_LONG).show()
        }
    }

    //////////////////////////// contact permission request
    fun contactPermissionGranted() {
        permissionContact.launch(Manifest.permission.READ_CONTACTS)
    }

    private var permissionContact = registerForActivityResult(
            RequestPermission()
    ) { result ->
        if (result) {
            permissionListener.permissionGrantedListener("contact")
        } else {
            Toast.makeText(activity, "Permission Denied", Toast.LENGTH_LONG).show()
        }
    }


    /////////////////////////////////Push Notification permission
    val requestPermissionLauncher = registerForActivityResult(RequestPermission())
            { isGranted: Boolean ->
                if (isGranted) {

                } else {

                }
            }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun requestPushNotificationPermission() {
                // Permission has not been asked yet
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)

        }

//    /**
//     * Define the interface of a permission fragment listener
//     */
//    interface PermissionListner {
//        fun permissionListnerGranted()
//    }

}
