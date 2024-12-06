package com.khan.fftracker.tracker.alertSetting.placeAlerts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import com.khan.fftracker.logCalls.LogCalls_Debug
import com.khan.fftracker.Navigation_Drawer.Drawer
import com.khan.fftracker.Network_Volley.IsAdmin
import com.khan.fftracker.R
import com.khan.fftracker.RecylerViewClicked.RecyclerViewItemListener
import com.khan.fftracker.autoverse_mvvm.Network_Stuff.ApiRetrofit
import com.khan.fftracker.autoverse_mvvm.networkApi.ResultApi
import com.khan.fftracker.databinding.TrackrplacealertFriendlistBinding
import com.khan.fftracker.tracker.TrackerConstant
import com.khan.fftracker.tracker.alertSetting.placeAlerts.placeHistory.AlertHistoryTracker
import com.khan.fftracker.tracker.alertSetting.placeAlerts.viewModel.PlaceAlertFriendListVM
import com.khan.fftracker.tracker.friendPlaces.FriendPlaces
import com.khan.fftracker.tracker.friendsDetail.FriendDetailsTracker
import com.khan.fftracker.tracker.aApiTracker.TrackerApi
import com.khan.fftracker.tracker.aApiTracker.TrackerRepo
import com.khan.fftracker.tracker.trackerDashboard.dataModel.Contract
import com.khan.fftracker.tracker.userMenuSetting.friendSettings.friendAdaptor.FriendListSettingAdaptor

class PlaceAlertFriendList : Fragment(), RecyclerViewItemListener {


    private lateinit var adaptor: FriendListSettingAdaptor
    private val TAG = PlaceAlertFriendList::class.simpleName

    var binding: TrackrplacealertFriendlistBinding? = null
    var mView: View? = null

    lateinit var viewModel: PlaceAlertFriendListVM

    /// IsAdmin: Class for using check whether customer or admin logged in
    var isAdmin: IsAdmin? = null


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.trackrplacealert_friendlist, container, false)

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

        /*   fragments communication
                    Check fragment if data is changed in next fragment.
                    So this fragment will observe changes and perform actions
                 */
        setFragmentResultListener(TrackerConstant.TRACKER_PLACE_ALERT_LISTENER) { requestKey, bundle ->
            ////
            // We use a String here, but any type that can be put in a Bundle is supported.
            val resultPlaceRegistered = bundle.getString("resultPlaceRegistered")
            // Do something with the result.
            LogCalls_Debug.d(TAG, "" + resultPlaceRegistered)
            if (resultPlaceRegistered.equals("yes")) {
                LogCalls_Debug.d(TAG, "" + "resultPlaceRegistered")
                if (this::adaptor.isInitialized)
                    adaptor.updatePlaceRegistered(viewModel.updatePlaceRegistered())
            }
        }
    }

    private fun setupViewModel() {

        val apiInterface = ApiRetrofit.getRetrofitInstance().create(TrackerApi::class.java)
        // View Model with factory
        val factory = PlaceAlertFriendListVM.VMFactory(TrackerRepo(apiInterface))

        viewModel = ViewModelProvider(this, factory)[PlaceAlertFriendListVM::class.java]
        ///////////////////////////////////////////////////

        binding!!.lifecycleOwner = this
        binding!!.viewModel = viewModel
    }

    private fun setupObservers() {

        ///observer: navigate to  ChooseAddress fragment
        viewModel.getNavigateFriendInfo().observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { // Only proceed if the event has never been handled
                (activity as Drawer?)!!.getFragment(FriendPlaces(), -1)
            }
            LogCalls_Debug.d(TAG, " getNavigateFriendInfo ")
        }
        ///observer: navigate to  fragment
        viewModel.getNavigateAlertHistory().observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { // Only proceed if the event has never been handled
                (activity as Drawer?)!!.getFragment(AlertHistoryTracker(), -1)
            }
            LogCalls_Debug.d(TAG, " getNavigateAlertHistory ")
        }

        //Observe data changes when api called
        viewModel.getFriendListResponse().observe(viewLifecycleOwner) {
            when (it.statusApi) {
                ResultApi.StatusApi.SUCCESS -> {
                    hideProgressBar()
                    setRecyclerView(it.data!!.contracts)

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
    private fun setRecyclerView(list: List<Contract>) {

        adaptor = FriendListSettingAdaptor(requireActivity(), list, this)
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

        if (any is Contract) {
            viewModel.onNavigateFriendInfo(any)
        }

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


