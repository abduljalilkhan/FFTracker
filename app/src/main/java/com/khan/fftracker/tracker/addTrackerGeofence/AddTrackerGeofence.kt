package com.khan.fftracker.tracker.addTrackerGeofence

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Circle
import com.google.android.gms.maps.model.CircleOptions
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.khan.fftracker.logCalls.LogCalls_Debug
import com.khan.fftracker.Network_Volley.IsAdmin
import com.khan.fftracker.R
import com.khan.fftracker.autoverse_mvvm.Network_Stuff.ApiRetrofit
import com.khan.fftracker.autoverse_mvvm.networkApi.ResultApi
import com.khan.fftracker.commanStuff.Bitmap_Stuff.BitmapMarker
import com.khan.fftracker.databinding.CustommapMarkerBatteryBinding
import com.khan.fftracker.databinding.CustommapiewMarkerBinding
import com.khan.fftracker.databinding.TrackeraddGeofenceBinding
import com.khan.fftracker.login_Stuffs.Login_New
import com.khan.fftracker.tracker.TrackerConstant
import com.khan.fftracker.tracker.addTrackerGeofence.viewModel.AddTrackerGeofenceVM
import com.khan.fftracker.tracker.friendsDetail.FriendDetailsTracker
import com.khan.fftracker.tracker.aApiTracker.TrackerApi
import com.khan.fftracker.tracker.aApiTracker.TrackerRepo


class AddTrackerGeofence : Fragment(), OnMapReadyCallback {

    private var autocompleteFragment: AutocompleteSupportFragment? = null
    private lateinit var markerViewBinding: CustommapMarkerBatteryBinding

    private val TAG = AddTrackerGeofence::class.simpleName

    var binding: TrackeraddGeofenceBinding? = null
    var mView: View? = null

    lateinit var viewModel: AddTrackerGeofenceVM

    /// IsAdmin: Class for using check whether customer or admin logged in
    var isAdmin: IsAdmin? = null

    ///Google map
    var mGoogleMap: GoogleMap? = null
    var supportMapFragment: SupportMapFragment? = null
    var marker: Marker? = null
    private var mapZoom = 16.0f
    private var circle: Circle? = null

////////////////////////////////////////////////////////////////////


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.trackeradd_geofence, container, false)

        mView = binding!!.root

        setupViewModel()
        /// Initialize class
        initializeAdminClass()

        getDataFromBundle()

        //Initializing map
        initMap()

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), onBackPressedCallback)
        //////////////////////////////////////////////


        return mView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        markerViewBinding = CustommapMarkerBatteryBinding.inflate(LayoutInflater.from(context))
        // markerViewBinding!!.bin = viewModel

        setupObservers()
        if (!Places.isInitialized()) {
            Places.initialize(activity, requireActivity().resources.getString(R.string.google_maps_key))
        }
//         autocompleteFragment =
//             childFragmentManager.findFragmentById(R.id.autocomplete_fragment)
//                    as? AutocompleteSupportFragment
//        Handler().postDelayed(Runnable {
//            setUpPlaceView()
//        },1000)
        setUpPlaceView()

    }


    override fun onResume() {
        super.onResume()
        supportMapFragment!!.onResume()

    }

    //Intializing widgtes
    private fun initMap() {
        supportMapFragment = childFragmentManager.findFragmentById(R.id.googleMap) as SupportMapFragment?
        if (supportMapFragment != null) {
            supportMapFragment!!.getMapAsync(this)
        }
    }


    //get data from previous screen
    private fun getDataFromBundle() {

        //get data from previous fragment
        val bundle = this.arguments
        if (bundle != null) {

            viewModel.setBundleData(bundle.getString(TrackerConstant.GEOFENCE_EDIT))

        }
    }
    private fun setupViewModel() {

        val apiInterface = ApiRetrofit.getRetrofitInstance().create(TrackerApi::class.java)
        // View Model with factory
        val factory = AddTrackerGeofenceVM.VMFactory(TrackerRepo(apiInterface))

        viewModel = ViewModelProvider(this, factory)[AddTrackerGeofenceVM::class.java]
        ///////////////////////////////////////////////////

        binding!!.lifecycleOwner = this
        binding!!.viewModel = viewModel
    }

    private fun setupObservers() {
        viewModel.getNavigateCancel().observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { // Only proceed if the event has never been handled
                closeFragment()
            }
        }

        ///observer: navigate to  dashboard fragment
        viewModel.getIsMapReady().observe(viewLifecycleOwner) {
            LogCalls_Debug.d(LogCalls_Debug.TAG, " getIsMapReady ")
            /////// place marker on map getting current location lat and long coordinates
            if (it) {
                drawCircleRadiusMap()
            }
        }
        viewModel.getIsEdit().observe(viewLifecycleOwner) {
            if (it) {
               // strIsEdit = "1"
                Handler(Looper.getMainLooper()).postDelayed({
                    viewModel.onRadiusMap()
                }, 200)

            }
            //else {
//                val lat = bundle!!.getString(AutoVerseConstant.LAT)!!.toDouble()
//                val lng = bundle!!.getString(AutoVerseConstant.LNG)!!.toDouble()
//                viewModel.setClickedLatLng(LatLng(lat, lng))
//
//                //addressFromLatLng()
//
//            }

            LogCalls_Debug.d(LogCalls_Debug.TAG, " getGeofenceRadius ")
        }

        ///observer: navigate to  AutoVerseGeofenceRadius fragment
        viewModel.getRadiusMap().observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { // Only proceed if the event has never been handled
                circle!!.radius = (viewModel.getSeekBarRadiusProgress().value!!).toDouble()
            }
            LogCalls_Debug.d(LogCalls_Debug.TAG, " getNavigateGeofenceRadius ")
        }
            ///observer: navigate to  AutoVerseGeofenceRadius fragment
        viewModel.getZoomMapForRadius().observe(viewLifecycleOwner) {
            if (mGoogleMap != null)
                mGoogleMap!!.animateCamera(CameraUpdateFactory.newLatLngZoom(viewModel.getLatLng().value!!, viewModel.getZoomMapForRadius().value!!.toFloat()))
        }
        ///observer: navigate to  AutoVerseGeofenceRadius fragment
        viewModel.getSeekBarRadiusProgress().observe(viewLifecycleOwner) {

            if (circle != null)
                circle!!.radius = viewModel.getSeekBarRadiusProgress().value!!.toDouble()

            viewModel.zoomMapForRadius(viewModel.getSeekBarRadiusProgress().value!!)

            viewModel.setRadiusMeter(viewModel.getSeekBarRadiusProgress().value!!)
        }


        //Observe data changes when api called
        viewModel.getAddGeofenceResponse().observe(viewLifecycleOwner) {
            LogCalls_Debug.d(TAG, "res")
            when (it.statusApi) {
                ResultApi.StatusApi.SUCCESS -> {
                    hideProgressBar()
                    /*   fragments communication
                     Notify mostly previous fragment that data is changed. So previous fragment will be changed according to their need*/
                    passDataPreviousFragment()

                    closeFragment()
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


    /*   fragments communication
                     Notify mostly previous fragment that data is changed. So previous fragment will be changed according to their need*/
    private fun passDataPreviousFragment() {
        // Use the Kotlin extension in the fragment-ktx artifact.
        setFragmentResult(TrackerConstant.TRACKER_FRIEND_PLACES_LISTENER, bundleOf("isApiCall" to viewModel.isApiCall))
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d(Login_New.TAG, "onMapReady autoverse")
        mGoogleMap = googleMap
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL


        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //googleMap.isMyLocationEnabled = true
        }

        viewModel.isMapReady()


    }

    private fun moveCameraPosition(latLng: LatLng) {
        Log.d(Login_New.TAG, "moveCameraPosition $latLng")
        mGoogleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, mapZoom))
    }


    private fun setMapMarker(latLng: LatLng) {
        val markerViewBinding = CustommapiewMarkerBinding.inflate(LayoutInflater.from(context))

        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(markerViewBinding.root)!!))

        marker = mGoogleMap!!.addMarker(markerOptions)
    }


    private fun createDrawableFromView(view: View): Bitmap? {
        return BitmapMarker().createDrawableFromView(requireActivity(), view)
    }

    private fun drawCircleRadiusMap() {
        try {
            LogCalls_Debug.d(TAG, "lat lng"+ viewModel.getLatitude()+" lng "+ viewModel.getLongitude())
            mGoogleMap!!.clear()
            val latLng1 = LatLng(viewModel.getLatitude().value!!, viewModel.getLongitude().value!!)

            // Creating MarkerOptions
            val options = MarkerOptions()
            // Setting the position of the marker
            options.position(latLng1)
            options.icon(BitmapDescriptorFactory.fromResource(R.drawable.home_clicked))
            mGoogleMap!!.addMarker(options)

            //  googleMap.addMarker(new MarkerOptions().position(latLng1).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_icon)));
            mGoogleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng1, 17f))

            //  new GetCurrentLocation(context,comman_stuff_interface).removeLocation();
            circle = mGoogleMap!!.addCircle(
                CircleOptions()
                .center(LatLng(viewModel.getLatitude().value!!, viewModel.getLongitude().value!!)) // .fillColor(Color.RED)
                .strokeColor(Color.parseColor("#1e90ff")) //Color.parseColor("#2271cce7")
                .fillColor(0x220000FF)
                .radius(100.0))
        } catch (e: Exception) {
            e.printStackTrace()
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


    private fun setUpPlaceView() {
        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        val fields = listOf(Place.Field.ID, Place.Field.NAME)

        // Start the autocomplete intent.
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(requireActivity())
        //  startAutocomplete.launch(intent)



        // Initialize the AutocompleteSupportFragment.
        val autocompleteFragment = childFragmentManager.findFragmentById(R.id.autocomplete_fragment) as AutocompleteSupportFragment

        // Specify the types of place data to return.
        autocompleteFragment.setPlaceFields(listOf(Place.Field.ID, Place.Field.NAME,Place.Field.LAT_LNG))

        // Set up a PlaceSelectionListener to handle the response.
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: ${place.name}, ${place.id}")
                LogCalls_Debug.d(TAG,""+place.latLng )
                viewModel.setClickedLatLng(place.latLng!!)
                drawCircleRadiusMap()
            }

            override fun onError(status: Status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: $status")
            }
        })

    }
    private val startAutocomplete =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val intent = result.data
                    if (intent != null) {
                        val place = Autocomplete.getPlaceFromIntent(intent)
                        Log.i(
                                TAG, "Place: ${place.name}, ${place.id}"
                        )
                    }
                } else if (result.resultCode == Activity.RESULT_CANCELED) {
                    // The user canceled the operation.
                    Log.i(TAG, "User canceled autocomplete")
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }


}


