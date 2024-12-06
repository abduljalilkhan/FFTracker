package com.khan.fftracker.tracker.locationPermission

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.khan.fftracker.Item_Interface.CommonStuffInterface
import com.khan.fftracker.logCalls.LogCalls_Debug
import com.khan.fftracker.Navigation_Drawer.Drawer
import com.khan.fftracker.Network_Volley.IsAdmin

import com.khan.fftracker.Prefrences.Prefs_OperationKotlin
import com.khan.fftracker.R
import com.khan.fftracker.autoverse_mvvm.Network_Stuff.ApiRetrofit
import com.khan.fftracker.autoverse_mvvm.geofence.geofenceList.AutoVerseGeofenceList
import com.khan.fftracker.databinding.GetLocationPermissionBinding
import com.khan.fftracker.tracker.TrackerConstant
import com.khan.fftracker.tracker.customAlertDialogue.AlertDialogueTracker
import com.khan.fftracker.tracker.locationPermission.viewModel.GetLocPermissionVM
import com.khan.fftracker.tracker.aApiTracker.TrackerApi
import com.khan.fftracker.tracker.aApiTracker.TrackerRepo
import com.khan.fftracker.tracker.trackerDashboard.TrackerDashboard
import com.khan.fftracker.Permission_Granted.PermissionListener

class GetLocationPermission : Fragment(), PermissionListener, CommonStuffInterface {

    private var isLocationPermissionAllowed: Boolean = false
    private val TAG = GetLocationPermission::class.simpleName

    var mView: View? = null
    var binding: GetLocationPermissionBinding? = null

    lateinit var viewModel: GetLocPermissionVM

    //run time permission fragment
    val TAG_LOC_PERMISSIONS_FRAGMENT = "locationPermissions"
    lateinit var locationPermissionFragment: LocationPermission

    /// IsAdmin: Class for using check whether customer or admin logged in
    var isAdmin: IsAdmin? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.get_location_permission, container, false)

        mView = binding!!.root

        setupViewModel()

        ///set permission fragment
        setPermission()

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), onBackPressedCallback)

        Prefs_OperationKotlin.writeBoolean(TrackerConstant.TRACKER_FIRST_TIME_LAUNCH,true)

        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
    }

    private fun setupObservers() {

        viewModel.getNavigatePermission().observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { // Only proceed if the event has never been handled
                setBackGroundLocPermission()
            }
            LogCalls_Debug.d(LogCalls_Debug.TAG, " getNavigatePermission ")
        }
        viewModel.getTrackerDashBoard().observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { // Only proceed if the event has never been handled
             //  viewModel.onLocationPermission()
                (activity as Drawer?)!!.getFragment(TrackerDashboard(), -1)
            }
            LogCalls_Debug.d(LogCalls_Debug.TAG, " getTrackerDashBoard ")
        }

    }

    private fun setupViewModel() {

        val apiInterface = ApiRetrofit.getRetrofitInstance().create(TrackerApi::class.java)
        // View Model with factory
        val factory = GetLocPermissionVM.VMFactory(TrackerRepo(apiInterface))

        viewModel = ViewModelProvider(this, factory)[GetLocPermissionVM::class.java]
        ///////////////////////////////////////////////////


        binding!!.lifecycleOwner = this
        binding!!.viewModel = viewModel
    }


    ///set permission fragment
    private fun setPermission() {
        /// Runtime location permissions
        locationPermissionFragment = LocationPermission()

        requireActivity().supportFragmentManager.beginTransaction().add(locationPermissionFragment, TAG_LOC_PERMISSIONS_FRAGMENT).commit()
        locationPermissionFragment.setPermissionGrantedListener(this)

    }

    private fun setBackGroundLocPermission() {
        //val handler = Handler(Looper.getMainLooper())

     //   handler.postDelayed({

            locationPermissionFragment.setLocationPermission()
        //}, 2000)
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun permissionGrantedListener(strResult: String) {
        LogCalls_Debug.d(LogCalls_Debug.TAG, "permissionGrantedListener " + strResult)
        when (strResult) {
            "accessFine" -> {
                LogCalls_Debug.d(LogCalls_Debug.TAG, "location granted")
                //locationPermissionFragment.setBackGroundLocPermission()
                if (locationPermissionFragment.isCheckBackgroundLocPermission()) {
                    AlertDialogueTracker(requireActivity()).dialoguePermissionSetting(this)
                }
            }

            "accessBackGround" -> {
                locationPermissionFragment.getBatteryOptimize()
            }

            "isBatteryOptimize" -> {
                AlertDialogueTracker(requireActivity()).dialogueBatteryOptimize(this)
                //locationPermissionFragment.setBatteryOptimizeAllow()
            }

            "batteryOptimize" -> {
                locationPermissionFragment.setEnableGPS()
            }

            "enableGps" -> {
                viewModel.onTrackerDashBoard()
            }

        }
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun commonStuffListener(s: String?) {
        when {
            s.equals("allowBatterOptimize") -> {
                locationPermissionFragment.setBatteryOptimizeAllow()
            }
            s.equals("cancelAllowBatterOptimize") -> {
                locationPermissionFragment.getBatteryOptimize()
            }
            s.equals("accessFine") -> {
                locationPermissionFragment.setBackGroundLocPermission()
            }
        }
    }

    ////////////////////////////////////////////////


    ////////////// When on Back button pressed fragment navigate to Main DashBoard fragment
    private val onBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true /* Enabled by default */) {
        override fun handleOnBackPressed() {
            // Handle the back button event
            LogCalls_Debug.d(LogCalls_Debug.TAG+ AutoVerseGeofenceList::class.java.name, "handleOnBackPressed false")
            closeFragment()

        }

        private fun closeFragment() {
            requireActivity().supportFragmentManager.popBackStack()

        }
    }


}