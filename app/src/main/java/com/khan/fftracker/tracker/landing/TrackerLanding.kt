package com.khan.fftracker.tracker.landing

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityRecognitionClient
import com.google.android.gms.location.ActivityTransitionResult
import com.khan.fftracker.BuildConfig
import com.khan.fftracker.DashBoard.DashBoard_New
import com.khan.fftracker.logCalls.LogCalls_Debug
import com.khan.fftracker.logCalls.LogCalls_Debug.TAG
import com.khan.fftracker.Navigation_Drawer.Drawer
import com.khan.fftracker.Network_Volley.IsAdmin
import com.khan.fftracker.Prefrences.Prefs_OperationKotlin
import com.khan.fftracker.R
import com.khan.fftracker.autoverse_mvvm.Network_Stuff.ApiRetrofit
import com.khan.fftracker.autoverse_mvvm.geofence.geofenceList.AutoVerseGeofenceList
import com.khan.fftracker.autoverse_mvvm.networkApi.ResultApi
import com.khan.fftracker.databinding.TrackerlandingBinding
import com.khan.fftracker.tracker.TrackerConstant
import com.khan.fftracker.tracker.aApiTracker.TrackerApi
import com.khan.fftracker.tracker.aApiTracker.TrackerRepo
import com.khan.fftracker.tracker.landing.adapter.TrackerLandingPagerAdaptor
import com.khan.fftracker.tracker.landing.dataModel.Sliding
import com.khan.fftracker.tracker.landing.viewModel.TrackerLandingVM
import com.khan.fftracker.tracker.locationPermission.GetLocationPermission
import com.khan.fftracker.tracker.trackerDashboard.ActivityTransitionsUtil
import com.khan.fftracker.tracker.trackerDashboard.TrackerDashboard
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TrackerLanding : Fragment() {

    private lateinit var pendingIntent: PendingIntent
    var mView: View? = null
    var binding: TrackerlandingBinding? = null

    private lateinit var viewModel: TrackerLandingVM

    /// IsAdmin: Class for using check whether customer or admin logged in
    var isAdmin: IsAdmin? = null

    lateinit var client: ActivityRecognitionClient

    // Action fired when transitions are triggered.
    var TRANSITIONS_RECEIVER_ACTION: String =
        BuildConfig.APPLICATION_ID + "TRANSITIONS_RECEIVER_ACTION"
    private var mTransitionsReceiver: TransitionsReceiver? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.trackerlanding, container, false)

        mView = binding!!.root

        setupViewModel()

        /// Initialize class
        initializeAdminClass()

        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            onBackPressedCallback
        )


        // The Activity Recognition Client returns a
        // list of activities that a user might be doing
        client = ActivityRecognition.getClient(requireActivity())
      //  activityTransition();
        return mView

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()

    }

//    @RequiresApi(Build.VERSION_CODES.O)
//    override fun onStart() {
//        super.onStart()
//        // TODO: Register the BroadcastReceiver to listen for activity transitions.
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            requireActivity().registerReceiver(
//                mTransitionsReceiver,
//                IntentFilter(TRANSITIONS_RECEIVER_ACTION),
//                Context.RECEIVER_EXPORTED
//            )
//        } else {
//            requireActivity().registerReceiver(
//                mTransitionsReceiver,
//                IntentFilter(TRANSITIONS_RECEIVER_ACTION)
//            )
//        }
//    }

    private fun activityTransition() {
        LogCalls_Debug.d(TAG, "activityTransition")
        getPendingIntent()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf<String>(Manifest.permission.ACTIVITY_RECOGNITION),
                21
            )
        } else {
            requestForUpdates()
        }


        if (ContextCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            LogCalls_Debug.d(TAG, "ACTIVITY_RECOGNITION PERMISSION_GRANTED  ")
            requestForUpdates()
        }

    }

    // To register for changes we have to also supply the requestActivityTransitionUpdates() method
    // with the PendingIntent object that will contain an intent to the component
    // (i.e. IntentService, BroadcastReceiver etc.) that will receive and handle updates appropriately.
    private fun requestForUpdates() {
        LogCalls_Debug.d(TAG, "requestForUpdates")
        client
            .requestActivityTransitionUpdates(
                ActivityTransitionsUtil().getActivityTransitionRequest(),
                pendingIntent
            )
            .addOnSuccessListener {
                showToast("successful registration")
                LogCalls_Debug.d(TAG, "addOnSuccessListener")
            }
            .addOnFailureListener {
                showToast("Unsuccessful registration")
                LogCalls_Debug.d(TAG, "addOnFailureListener")
            }
    }

    class TransitionsReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d(TAG, "onReceive(): $intent")
            if (!TextUtils.equals(
                    BuildConfig.APPLICATION_ID + "TRANSITIONS_RECEIVER_ACTION",
                    intent.action
                )
            ) {
                LogCalls_Debug.d(
                    TAG,
                    "Received an unsupported action in TransitionsReceiver: action = " +
                            intent.action
                )
                return
            }

            if (ActivityTransitionResult.hasResult(intent)) {
                val result = ActivityTransitionResult.extractResult(intent)
                for (event in result!!.transitionEvents) {
                    val info =
                        "Transition: " + ActivityTransitionsUtil().toActivityString(event.activityType) +
                                " (" + ActivityTransitionsUtil().toTransitionType(event.transitionType) + ")" + "   " +
                                SimpleDateFormat("HH:mm:ss", Locale.US).format(Date())
                    Toast.makeText(context, info, Toast.LENGTH_LONG).show()
                    LogCalls_Debug.d(TAG, info)
                }
            }
        }
    }

    // Deregistering from updates
    // call the removeActivityTransitionUpdates() method
    // of the ActivityRecognitionClient and pass
    // ourPendingIntent object as a parameter
    private fun deregisterForUpdates() {
        client
            .removeActivityTransitionUpdates(pendingIntent)
            .addOnSuccessListener {
                pendingIntent.cancel()
                showToast("successful deregistration")
            }
            .addOnFailureListener { e: Exception ->
                showToast("unsuccessful deregistration")
            }
    }

    // creates and returns the PendingIntent object which holds
    // an Intent to an BroadCastReceiver class
    private fun getPendingIntent() {
//        val requestID = System.currentTimeMillis().toInt()
//        val intent = Intent(context, ActivityTransitionReceiver::class.java)
//        pendingIntent= PendingIntent.getBroadcast(requireActivity(), requestID, intent,
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
//                PendingIntent.FLAG_CANCEL_CURRENT
//            } else {
//                PendingIntent.FLAG_MUTABLE
//            }
//        )


        // TODO: Initialize PendingIntent that will be triggered when a activity transition occurs.

        // TODO: Initialize PendingIntent that will be triggered when a activity transition occurs.
        val intent = Intent(TRANSITIONS_RECEIVER_ACTION)
        pendingIntent =
            PendingIntent.getBroadcast(
                requireActivity(), 0, intent, if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                    0
                } else {
                    PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_ALLOW_UNSAFE_IMPLICIT_INTENT
                }
            )


        // TODO: Create a BroadcastReceiver to listen for activity transitions.
        // The receiver listens for the PendingIntent above that is triggered by the system when an
        // activity transition occurs.

        mTransitionsReceiver = TransitionsReceiver()
    }

    private fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG)
            .show()
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////
    private fun setupViewModel() {

        val apiInterface = ApiRetrofit.getRetrofitInstance().create(TrackerApi::class.java)
        // View Model with factory
        val factory = TrackerLandingVM.VMFactory(TrackerRepo(apiInterface))

        viewModel = ViewModelProvider(this, factory)[TrackerLandingVM::class.java]
        ///////////////////////////////////////////////////

        binding!!.lifecycleOwner = this
        binding!!.viewModel = viewModel
    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        //if (batteryLevelReceiver!=null) {
//
//        //  }
//        try {
//            // TODO: Unregister activity transition receiver when user leaves the app.
//
//
//            // TODO: Unregister activity transition receiver when user leaves the app.
//            requireActivity().unregisterReceiver(mTransitionsReceiver)
//            deregisterForUpdates()
//
//            LogCalls_Debug.d(TAG, "ondestroy dashboardTracker")
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }

    private fun setupObservers() {
        LogCalls_Debug.d(
            TAG,
            " true of alse " + (Prefs_OperationKotlin.readBoolean(
                TrackerConstant.TRACKER_FIRST_TIME_LAUNCH,
                false
            ))
        )

        viewModel.getNavigatePermission().observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { // Only proceed if the event has never been handled
                if ((Prefs_OperationKotlin.readBoolean(
                        TrackerConstant.TRACKER_FIRST_TIME_LAUNCH,
                        false
                    )) ||
                    ActivityCompat.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {

                   // (activity as Drawer?)!!.getFragment(TrackerHistory(), -1)
                    (activity as Drawer?)!!.getFragment(TrackerDashboard(), -1)

                } else {
                    (activity as Drawer?)!!.getFragment(GetLocationPermission(), -1)
                }
                //(activity as Drawer?)!!.getFragment(TrackerDashboard(),-1)
            }
            LogCalls_Debug.d(LogCalls_Debug.TAG, " getNavigateDashBoard ")
        }
        //Observe data changes when api called
        viewModel.getSliderResponse().observe(viewLifecycleOwner) {
            when (it.statusApi) {
                ResultApi.StatusApi.SUCCESS -> {
                    hideProgressBar()
                    setViewPagerData(it.data!!.slidings)
                }

                ResultApi.StatusApi.ERROR -> {
                    Toast.makeText(activity, it.msg, Toast.LENGTH_SHORT).show()
                    LogCalls_Debug.d(LogCalls_Debug.TAG, "ERROR")
                    hideProgressBar()
                }

                ResultApi.StatusApi.LOADING -> {
                    //  Keyboard_Close(activity).keyboard_Close_Down()
                    showProgressBar()
                }

                ResultApi.StatusApi.FAILURE -> {
                    Toast.makeText(activity, it.msg, Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    private fun setViewPagerData(listSlider: List<Sliding>) {
        //set adaptor
        val customPagerAdapter = TrackerLandingPagerAdaptor(requireActivity(), listSlider)
        binding!!.gcLanding.adapter = customPagerAdapter


        //set indicator dots to view pager
        binding!!.titles.setViewPager(binding!!.gcLanding)
        binding!!.btnGetStarted.visibility = View.VISIBLE
    }

    fun showProgressBar() {
        isAdmin!!.showhideLoader(View.VISIBLE)
    }

    fun hideProgressBar() {
        isAdmin!!.showhideLoader(View.GONE)
    }


    /// Initialize class
    private fun initializeAdminClass() {
        isAdmin = IsAdmin(activity)
    }


    ////////////// When on Back button pressed fragment navigate to Main DashBoard fragment
    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true /* Enabled by default */) {
            override fun handleOnBackPressed() {
                // Handle the back button event
                LogCalls_Debug.d(
                    LogCalls_Debug.TAG + AutoVerseGeofenceList::class.java.name,
                    "handleOnBackPressed false"
                )
                closeFragment()

            }

            private fun closeFragment() {

                (activity as Drawer?)!!.getFragment(DashBoard_New(), -2)

            }
        }
}