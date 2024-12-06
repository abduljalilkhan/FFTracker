package com.khan.fftracker.tracker.alertSetting.placeAlerts.placeHistory

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.khan.fftracker.logCalls.LogCalls_Debug
import com.khan.fftracker.Network_Volley.IsAdmin
import com.khan.fftracker.R
import com.khan.fftracker.RecylerViewClicked.RecyclerViewItemListener
import com.khan.fftracker.autoverse_mvvm.Network_Stuff.ApiRetrofit
import com.khan.fftracker.autoverse_mvvm.networkApi.ResultApi
import com.khan.fftracker.databinding.TrackerAlertHistoryBinding
import com.khan.fftracker.tracker.aApiTracker.TrackerApi
import com.khan.fftracker.tracker.aApiTracker.TrackerRepo
import com.khan.fftracker.tracker.alertSetting.placeAlerts.placeHistory.dataModel.Notification
import com.khan.fftracker.tracker.alertSetting.placeAlerts.placeHistory.placeAdaptor.AlertHistoryTrackerAdaptor
import com.khan.fftracker.tracker.alertSetting.placeAlerts.placeHistory.viewModel.AlertHistoryTrackerVM
import com.khan.fftracker.tracker.friendsDetail.FriendDetailsTracker

class AlertHistoryTracker : Fragment(), RecyclerViewItemListener {


    private lateinit var adaptor: AlertHistoryTrackerAdaptor
    private val TAG = AlertHistoryTracker::class.simpleName

    var binding: TrackerAlertHistoryBinding? = null
    var mView: View? = null

    lateinit var viewModel: AlertHistoryTrackerVM

    /// IsAdmin: Class for using check whether customer or admin logged in
    var isAdmin: IsAdmin? = null


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.tracker_alert_history, container, false)

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()


    }

    private fun setupViewModel() {

        val apiInterface = ApiRetrofit.getRetrofitInstance().create(TrackerApi::class.java)
        // View Model with factory
        val factory = AlertHistoryTrackerVM.VMFactory(TrackerRepo(apiInterface))

        viewModel = ViewModelProvider(this, factory)[AlertHistoryTrackerVM::class.java]
        ///////////////////////////////////////////////////

        binding!!.lifecycleOwner = this
        binding!!.viewModel = viewModel
    }

    private fun setupObservers() {



        //Observe data changes when api called
        viewModel.getAlertHistoryResponse().observe(viewLifecycleOwner) {
            when (it.statusApi) {
                ResultApi.StatusApi.SUCCESS -> {
                    hideProgressBar()
                    setRecyclerView(it.data!!.notifications)

                }

                ResultApi.StatusApi.ERROR -> {
                    Toast.makeText(activity, it.msg, Toast.LENGTH_SHORT).show()
                    LogCalls_Debug.d(TAG, "ERROR")
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

    //data set to rv
    private fun setRecyclerView(list: List<Notification>) {

        adaptor = AlertHistoryTrackerAdaptor(requireActivity(), list, this)
        binding!!.rv.adapter = adaptor

        LogCalls_Debug.d(TAG, "adaptor recylerview " + adaptor!!.itemCount)
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

    override fun onItemClick(get: HashMap<String, Any>, pos: Int) {}
    override fun onItemClickObject(id: Int, any: Any, pos: Int) {

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


