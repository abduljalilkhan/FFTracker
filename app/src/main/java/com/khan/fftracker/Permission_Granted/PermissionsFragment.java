package com.khan.fftracker.Permission_Granted;

import static android.os.Build.VERSION.SDK_INT;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class PermissionsFragment extends Fragment {

    public static final String TAG_PERMISSIONS_FRAGMENT = "permissionsJava";

    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;
    private static final int ALL_FILES_PERMISSION = 229;
    private static final int PHONE_REQUEST = 212;
    PermissionListner permissionListner;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
    ///check permissions for camera,storage and record(microphone) as well as storage permission device running
    ///on above OS version 10
    public void isPermissionGranted() {
            // Request to camera ,storage and record audio permission
            //if all permissions are granted then call listener interface to invoke activity/fragment for further action
            if (checkAndRequestPermissions()) {
                Log.d(TAG, "checkAllFilesPermissions: isPermissionGranted ");
                permissionGranted();
            }
    }

    //Check if device running on version 11 or above then check MANAGE_EXTERNAL_STORAGE permission
    ///for all files access etc
    ///Request for storage access if permission is not granted
    ////deviceRunningVersion_R() : Device running on version 11 or above return true

    ////Device running on version 11 or above return true
    private boolean deviceRunningVersion_R() {
        return SDK_INT >= Build.VERSION_CODES.R;
    }


    /// Request to camera ,storage and record audio permission
    public boolean checkAndRequestPermissions() {
        int ExtstorePermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int cameraPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
        int recordPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO);

        List<String> listPermissionsNeeded = new ArrayList<>();

        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (SDK_INT < Build.VERSION_CODES.R) {
            Log.d(TAG, "checkAndRequestPermissions: ");
            if (ExtstorePermission != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
        }
        if (recordPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            requestPermissions(listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    ///phone call permission
    public boolean isPhonePermissionGranted() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions( new String[]{Manifest.permission.CALL_PHONE}, PHONE_REQUEST);
            return false;
        }

        return true;
    }

    ///phone Camera permission
    public boolean isCameraPermissionGranted() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions( new String[]{Manifest.permission.CAMERA}, REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }

        return true;
    }

    /**
     * Called upon the permissions being granted. Notifies the permission listener.
     */
    private void permissionGranted() {
        if (permissionListner != null) {
            Log.d(TAG, "permissionGranted: ");
                permissionListner.permissionListnerGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: ");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS:
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(getActivity(), "Requires Access to Camara.", Toast.LENGTH_SHORT).show();

                    //getActivity().getSupportFragmentManager().popBackStack();
                } else if (SDK_INT < Build.VERSION_CODES.R &&
                        ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getActivity(), "Requires Access to Your Storage.", Toast.LENGTH_SHORT).show();

                } else if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(getActivity(), "Requires Access to Your Record.", Toast.LENGTH_SHORT).show();
                    // getActivity().getSupportFragmentManager().popBackStack();
                } else {
                    Log.d(TAG, "onRequestPermissionsResult: permission granted");
                    permissionGranted();  // Permission was just granted by the user.
                }
                ///phone call permission
                break;
            case PHONE_REQUEST:
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(getActivity(), "Requires Access to Phone Call.", Toast.LENGTH_SHORT).show();

                }
                     else {
                Log.d(TAG, "onRequestPermissionsResult: permission granted");
                permissionGranted();  // Permission was just granted by the user.
            }
                break;
        }
    }

    /**
     * Sets the listener on which we will call permissionListnerGranted()
     * @param permissionListner pointer to the class implementing the PermissionsFragment.PermissionListner
     */
    public void setPermissionListner(PermissionListner permissionListner) {
        this.permissionListner = permissionListner;
    }


    /**
     * Define the interface of a permission fragment listener
     */
    public interface PermissionListner {
        void permissionListnerGranted();
    }

}
