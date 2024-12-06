package com.khan.fftracker.tracker.landing

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.khan.fftracker.DashBoard.DashBoard_New
import com.khan.fftracker.logCalls.LogCalls_Debug
import com.khan.fftracker.Navigation_Drawer.Drawer
import com.khan.fftracker.Network_Volley.AppSingleton
import com.khan.fftracker.Network_Volley.IsAdmin
import com.khan.fftracker.Prefrences.Prefs_OperationKotlin
import com.khan.fftracker.R
import com.khan.fftracker.autoverse_mvvm.geofence.geofenceList.AutoVerseGeofenceList
import com.khan.fftracker.autoverse_mvvm.networkApi.ResultApi
import com.khan.fftracker.databinding.TrackerlandingBinding
import com.khan.fftracker.tracker.TrackerConstant
import com.khan.fftracker.tracker.landing.adapter.TrackerLandingPagerAdaptor
import com.khan.fftracker.tracker.landing.dataModel.Sliding
import com.khan.fftracker.tracker.landing.viewModel.TrackerLandingVM
import com.khan.fftracker.tracker.locationPermission.GetLocationPermission
import com.khan.fftracker.tracker.trackerDashboard.TrackerDashboard
import javax.inject.Inject

class TrackerLandingEx : Fragment() {

    var mView: View? = null
    var binding: TrackerlandingBinding? = null

    private lateinit var viewModel: TrackerLandingVM

    /// IsAdmin: Class for using check whether customer or admin logged in
    var isAdmin: IsAdmin? = null


    @Inject
    lateinit var viewModelFactory:ViewModelProvider.Factory
//    @Inject
//    lateinit var retrofit: Retrofit
//
//    @Inject
//    lateinit var apiInterface: TrackerApi

//    @Inject
//    lateinit var viewModelFactory: TrackerLandingVM.MainViewModelFactory
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.trackerlanding, container, false)

        mView = binding!!.root

        (requireActivity().application as AppSingleton).getRetrofitComponent().inject(this)
        setupViewModel()

        /// Initialize class
        initializeAdminClass()

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), onBackPressedCallback)

        return mView

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()

    }

    private fun setupViewModel() {

//        val apiInterface = ApiRetrofit.getRetrofitInstance().create(TrackerApi::class.java)
//        // View Model with factory
//        val factory = TrackerLandingVM.VMFactory(TrackerRepo(apiInterface))

        viewModel = ViewModelProvider(this, viewModelFactory)[TrackerLandingVM::class.java]

        ///////////////////////////////////////////////////

        binding!!.lifecycleOwner = this
        binding!!.viewModel = viewModel

    }


    private fun setupObservers() {
        LogCalls_Debug.d(
            LogCalls_Debug.TAG," true of alse "+(Prefs_OperationKotlin.readBoolean(
                TrackerConstant.TRACKER_FIRST_TIME_LAUNCH, false)))

        viewModel.getNavigatePermission().observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { // Only proceed if the event has never been handled
                if ((Prefs_OperationKotlin.readBoolean(TrackerConstant.TRACKER_FIRST_TIME_LAUNCH, false)) ||
                    ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
    private val onBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true /* Enabled by default */) {
        override fun handleOnBackPressed() {
            // Handle the back button event
            LogCalls_Debug.d(LogCalls_Debug.TAG + AutoVerseGeofenceList::class.java.name, "handleOnBackPressed false")
            closeFragment()

        }

        private fun closeFragment() {

            (activity as Drawer?)!!.getFragment(DashBoard_New(), -2)

        }
    }
}