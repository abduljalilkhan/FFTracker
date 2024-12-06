package com.khan.fftracker.tracker.alertSetting.nearByAlert

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.khan.fftracker.logCalls.LogCalls_Debug
import com.khan.fftracker.Network_Volley.IsAdmin
import com.khan.fftracker.R
import com.khan.fftracker.autoverse_mvvm.Network_Stuff.ApiRetrofit
import com.khan.fftracker.autoverse_mvvm.networkApi.ResultApi
import com.khan.fftracker.databinding.TrackerNearAlertBinding
import com.khan.fftracker.tracker.alertSetting.nearByAlert.viewModel.NearByAlertVM
import com.khan.fftracker.tracker.friendsDetail.FriendDetailsTracker
import com.khan.fftracker.tracker.aApiTracker.TrackerApi
import com.khan.fftracker.tracker.aApiTracker.TrackerRepo

class NearByAlert : Fragment() {

    private val TAG = NearByAlert::class.simpleName

    var binding: TrackerNearAlertBinding? = null
    var mView: View? = null

    lateinit var viewModel: NearByAlertVM

    /// IsAdmin: Class for using check whether customer or admin logged in
    var isAdmin: IsAdmin? = null


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.tracker_near_alert, container, false)

        mView = binding!!.root

        setupViewModel()
        /// Initialize class
        initializeAdminClass()


        requireActivity().onBackPressedDispatcher.addCallback(
                requireActivity(),
                onBackPressedCallback
        )


        return mView
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()

    }


    private fun setupViewModel() {

        val apiInterface = ApiRetrofit.getRetrofitInstance().create(TrackerApi::class.java)
        // View Model with factory
        val factory = NearByAlertVM.VMFactory(TrackerRepo(apiInterface))

        viewModel = ViewModelProvider(this, factory)[NearByAlertVM::class.java]
        ///////////////////////////////////////////////////

        binding!!.lifecycleOwner = this
        binding!!.viewModel = viewModel
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun setupObservers() {

        viewModel.getNavigateCancel().observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { // Only proceed if the event has never been handled
                closeFragment()
            }
        }

///observer: navigate to  AutoVerseGeofenceRadius fragment
        viewModel.getSeekBarRadiusProgress().observe(viewLifecycleOwner) {
            viewModel.onSeekBarRadiusProgress(it)
        }

        viewModel.getNearbyAlertApiResponse().observe(viewLifecycleOwner) {
            LogCalls_Debug.d(LogCalls_Debug.TAG, "getProfileImageSavedResponse")
            when (it.statusApi) {
                ResultApi.StatusApi.SUCCESS -> {
                    hideProgressBar()
                    viewModel.manageResponse()
                    Toast.makeText(activity, it.data!!.message, Toast.LENGTH_LONG).show()
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

    ////////////// When on Back button pressed fragment navigate to prevoius fragment
    private val onBackPressedCallback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* Enabled by default */) {
                override fun handleOnBackPressed() {

                    // Handle the back button event
                    LogCalls_Debug.d(
                            LogCalls_Debug.TAG + FriendDetailsTracker::class.java.name,
                            "handleOnBackPressed false"
                    )
                    closeFragment()

                }
            }

    private fun closeFragment() {

        requireActivity().supportFragmentManager.popBackStack()

    }

}


