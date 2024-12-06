package com.khan.fftracker.tracker.friendPlaces

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import com.khan.fftracker.logCalls.LogCalls_Debug
import com.khan.fftracker.Navigation_Drawer.Drawer
import com.khan.fftracker.Network_Volley.IsAdmin
import com.khan.fftracker.R
import com.khan.fftracker.RecylerViewClicked.RecyclerViewItemListener
import com.khan.fftracker.autoverse_mvvm.Network_Stuff.ApiRetrofit
import com.khan.fftracker.autoverse_mvvm.networkApi.ResultApi
import com.khan.fftracker.databinding.TrackerFriendPlacesBinding
import com.khan.fftracker.tracker.TrackerConstant
import com.khan.fftracker.tracker.addTrackerGeofence.AddTrackerGeofence
import com.khan.fftracker.tracker.friendPlaces.adaptor.FriendPlacesAdaptorType
import com.khan.fftracker.tracker.friendPlaces.dataModel.Geofencelists
import com.khan.fftracker.tracker.friendPlaces.viewModel.FriendPlacesVM
import com.khan.fftracker.tracker.friendsDetail.FriendDetailsTracker
import com.khan.fftracker.tracker.aApiTracker.TrackerApi
import com.khan.fftracker.tracker.aApiTracker.TrackerRepo


class FriendPlaces : Fragment(), RecyclerViewItemListener {


    private lateinit var adaptor: FriendPlacesAdaptorType
    private val TAG = FriendPlaces::class.simpleName

    var binding: TrackerFriendPlacesBinding? = null
    var mView: View? = null

    lateinit var viewModel: FriendPlacesVM

    /// IsAdmin: Class for using check whether customer or admin logged in
    var isAdmin: IsAdmin? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.tracker_friend_places, container, false)

        mView = binding!!.root

        setupViewModel()
        /// Initialize class
        initializeAdminClass()


        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), onBackPressedCallback)


        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
        initRecyclerView()

        /*   fragments communication
                    Check fragment if data is changed in next fragment.
                    So this fragment will observe changes and perform actions
                   */
        setFragmentResultListener(TrackerConstant.TRACKER_FRIEND_PLACES_LISTENER) { requestKey, bundle ->
            // We use a String here, but any type that can be put in a Bundle is supported.
            val result = bundle.getString("isApiCall")
            // Do something with the result.
            LogCalls_Debug.d(TAG, "" + result)
            if (result.equals("yes")) {
                viewModel.onGeofenceListApi()
            }
        }
    }

    private fun initRecyclerView() {
        LogCalls_Debug.d(TAG, "initRecyclerView")

        adaptor = FriendPlacesAdaptorType(requireActivity(), viewModel.getGeofenceList().value!!, this)
        binding!!.rvPlaces.adapter = adaptor
    }

    private fun setupViewModel() {

        val apiInterface = ApiRetrofit.getRetrofitInstance().create(TrackerApi::class.java)
        // View Model with factory
        val factory = FriendPlacesVM.VMFactory(TrackerRepo(apiInterface))

        viewModel = ViewModelProvider(this, factory)[FriendPlacesVM::class.java]
        ///////////////////////////////////////////////////

        binding!!.lifecycleOwner = this
        binding!!.viewModel = viewModel
    }

    private fun setupObservers() {


        ///observer: navigate to  ChooseAddress fragment
        viewModel.getNavigateAddGeofence().observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { // Only proceed if the event has never been handled

                ///pass data to next fragment
                val bundle = Bundle()
                bundle.putString(TrackerConstant.GEOFENCE_EDIT, viewModel.getGeoFenceEdit().value)

                val fragment = AddTrackerGeofence()
                fragment.arguments = bundle
                (activity as Drawer?)!!.getFragment(fragment, -1)
            }
            LogCalls_Debug.d(LogCalls_Debug.TAG, " getNavigateAddGeofence ")
        }


        //Observe data changes when api called
        viewModel.getGeofenceListResponse().observe(viewLifecycleOwner) {
            when (it.statusApi) {
                ResultApi.StatusApi.SUCCESS -> {
                    hideProgressBar()
                    /*   fragments communication
                     Notify mostly previous fragment that data is changed. So previous fragment will be changed according to their need*/
                    passDataPreviousFragment()
                    viewModel.savePlaceRegistered(it.data!!.places_register)
                    setRecyclerView(it.data.geofencelists)

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

        viewModel.getNotificationOnOffResponse().observe(viewLifecycleOwner) {
            when (it.statusApi) {
                ResultApi.StatusApi.SUCCESS -> {
                    hideProgressBar()
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

    private fun setRecyclerView(list: List<Geofencelists>) {
        viewModel.clearList(list)

        adaptor!!.notifyDataSetChanged()

        LogCalls_Debug.d(LogCalls_Debug.TAG, "setRecyclerView")

    }
    /*   fragments communication
                     Notify mostly previous fragment that data is changed. So previous fragment will be changed according to their need*/
    private fun passDataPreviousFragment() {
        // Use the Kotlin extension in the fragment-ktx artifact.
        setFragmentResult(TrackerConstant.TRACKER_PLACE_ALERT_LISTENER, bundleOf("resultPlaceRegistered" to "yes"))
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
        if (any is Geofencelists) {
            // viewModel.onSaveGeoFenceResponse(any)

            when (id) {
                 //recylerview has two viewtype i.e regular and header
                R.id.imgAlarm -> {

                    // if geofence exist then lat and lng tag has a value and it will not be 0
                    //So call api for notification on/off
                    if (any.lat !="0") {
                        viewModel.onNotificationOnOffApi(any, pos)
                        adaptor.notifyDataSetChanged()
                    }
                    else{
                        //else navigate to add geofence. if geofence is not added then lat and lng value is 0
                       // viewModel.onNavigateAddGeofence("0")
                        viewModel.onSaveGeoFenceResponse(any)
                    }
                }
                // if regular item clicked then edit or delete geofence
                else ->
                    viewModel.onSaveGeoFenceResponse(any)

            }
        }
        //if header item clicked then add geofence
        else {
            viewModel.onNavigateAddGeofence("0")
        }

    }


    ////////////// When on Back button pressed fragment navigate to prevoius fragment
    private val onBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true /* Enabled by default */) {
        override fun handleOnBackPressed() {

            // Handle the back button event
            LogCalls_Debug.d(LogCalls_Debug.TAG + FriendDetailsTracker::class.java.name, "handleOnBackPressed false")
            closeFragment()

        }
    }

    private fun closeFragment() {

        requireActivity().supportFragmentManager.popBackStack()

    }
}


