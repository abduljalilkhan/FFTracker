package com.khan.fftracker.tracker.alertSetting

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.khan.fftracker.logCalls.LogCalls_Debug
import com.khan.fftracker.Navigation_Drawer.Drawer
import com.khan.fftracker.R
import com.khan.fftracker.databinding.TrackerManageAlertBinding
import com.khan.fftracker.tracker.alertSetting.batteryAlert.BatteryAlert
import com.khan.fftracker.tracker.alertSetting.nearByAlert.NearByAlert
import com.khan.fftracker.tracker.alertSetting.placeAlerts.PlaceAlertFriendList
import com.khan.fftracker.tracker.alertSetting.viewModel.ManageAlertsVM

class ManageAlerts : Fragment() {
    
    private val TAG = ManageAlerts::class.simpleName

    var binding: TrackerManageAlertBinding? = null
    var mView: View? = null

    lateinit var viewModel: ManageAlertsVM


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.tracker_manage_alert, container, false)

        mView = binding!!.root

        setupViewModel()


        requireActivity().onBackPressedDispatcher.addCallback(
                requireActivity(),
                onBackPressedCallback
        )


        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()

    }

    private fun setupViewModel() {


        viewModel = ViewModelProvider(this)[ManageAlertsVM::class.java]
        ///////////////////////////////////////////////////

        binding!!.lifecycleOwner = this
        binding!!.viewModel = viewModel
    }

    private fun setupObservers() {

        ///observer: navigate to  fragment
        viewModel.getNavigatePlaceAlert().observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { // Only proceed if the event has never been handled
                (activity as Drawer?)!!.getFragment(PlaceAlertFriendList(), -1)
            }
            LogCalls_Debug.d(TAG, " getNavigateFriendDetail ")
        }



        ///observer: navigate to  fragment
        viewModel.getNavigateNearAlert().observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { // Only proceed if the event has never been handled
                (activity as Drawer?)!!.getFragment(NearByAlert(), -1)
            }
            LogCalls_Debug.d(TAG, " getNavigateFriendDetail ")
        }


        ///observer: navigate to  fragment
        viewModel.getNavigateBatteryAlert().observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { // Only proceed if the event has never been handled
                (activity as Drawer?)!!.getFragment(BatteryAlert(), -1)
            }
            LogCalls_Debug.d(TAG, " getNavigateFriendDetail ")
        }
    }

    ////////////// When on Back button pressed fragment navigate to prevoius fragment
    private val onBackPressedCallback: OnBackPressedCallback =
            object : OnBackPressedCallback(true /* Enabled by default */) {
                override fun handleOnBackPressed() {

                    // Handle the back button event
                    LogCalls_Debug.d(
                            LogCalls_Debug.TAG + ManageAlerts::class.java.name,
                            "handleOnBackPressed false"
                    )
                    closeFragment()

                }
            }

    private fun closeFragment() {

        requireActivity().supportFragmentManager.popBackStack()

    }
}


