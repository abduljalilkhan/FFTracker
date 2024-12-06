package com.khan.fftracker.tracker.friendsDetail

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.khan.fftracker.logCalls.LogCalls_Debug
import com.khan.fftracker.Navigation_Drawer.Drawer
import com.khan.fftracker.Network_Volley.IsAdmin
import com.khan.fftracker.R
import com.khan.fftracker.autoverse_mvvm.Network_Stuff.ApiRetrofit
import com.khan.fftracker.autoverse_mvvm.networkApi.ResultApi
import com.khan.fftracker.commanStuff.Bitmap_Stuff.BitmapMarker
import com.khan.fftracker.databinding.CustommapMarkerBatteryBinding
import com.khan.fftracker.databinding.CustommapiewMarkerBinding
import com.khan.fftracker.databinding.TrackerFriendDetailBinding
import com.khan.fftracker.login_Stuffs.Login_New
import com.khan.fftracker.tracker.TrackerConstant
import com.khan.fftracker.tracker.aApiTracker.TrackerApi
import com.khan.fftracker.tracker.aApiTracker.TrackerRepo
import com.khan.fftracker.tracker.chat.ChatsContentTracker
import com.khan.fftracker.tracker.customAlertDialogue.AlertDialogueTracker
import com.khan.fftracker.tracker.friendPlaces.FriendPlaces
import com.khan.fftracker.tracker.friendsDetail.viewModel.FriendDetailTrackerVM
import com.khan.fftracker.tracker.trackerDashboard.TrackerDashboard
import com.khan.fftracker.tracker.trackerDashboard.dataModel.Contract
import com.squareup.picasso.Picasso
import java.util.Locale


class FriendDetailsTracker : Fragment(), OnMapReadyCallback, SensorEventListener {

    private lateinit var markerViewBinding: CustommapMarkerBatteryBinding

    private val TAG = FriendDetailsTracker::class.simpleName

    var binding: TrackerFriendDetailBinding? = null
    var mView: View? = null

    lateinit var viewModel: FriendDetailTrackerVM

    var handler: Handler? = null
    var runnable: Runnable? = null
    private val delayWebApiCall = (10000 * 3 //delay webservices call for 30 secs
            ).toLong()

    /// IsAdmin: Class for using check whether customer or admin logged in
    var isAdmin: IsAdmin? = null


    ///Google map
    var mGoogleMap: GoogleMap? = null
    var supportMapFragment: SupportMapFragment? = null
    var marker: Marker? = null
    var markerFriend: Marker? = null
    var markerRotateOrientation: Marker? = null
    private var mapZoom = 16.0f

    lateinit var mFusedLocationClient: FusedLocationProviderClient

    private var locationList: ArrayList<LatLng>? = null
////////////////////////////////////////////////////////////////////


    var sensorManager: SensorManager? = null
    private var sensorAccelerometer: Sensor? = null
    private var sensorMagneticField: Sensor? = null


    private lateinit var valuesAccelerometer: FloatArray
    private lateinit var valuesMagneticField: FloatArray

    private lateinit var matrixR: FloatArray
    private lateinit var matrixI: FloatArray
    private lateinit var matrixValues: FloatArray
    private var azimuth: Double = 0.0
    ////////////////////////////////////////////////////////


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.tracker_friend_detail, container, false)

        mView = binding!!.root

        setupViewModel()
        /// Initialize class
        initializeAdminClass()

        getDataFromBundle()
        //Initializing map
        initMap()

        showBottomSheetDialog()

        //using to handler delay webservices call for 30 secs
        timer()
        locationList = ArrayList()
        getListOfLocations()

        requireActivity().onBackPressedDispatcher.addCallback(requireActivity(), onBackPressedCallback)
        //////////////////////////////////////////////


        return mView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initCustomView()

        setupObservers()

        getlocation()
        setMarkerOrientation()

    }

    override fun onResume() {
        super.onResume()
        supportMapFragment!!.onResume()

        sensorManager!!.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        sensorManager!!.registerListener(this, sensorMagneticField, SensorManager.SENSOR_DELAY_NORMAL)
    }


    override fun onDestroyView() {
        super.onDestroyView()
        LogCalls_Debug.d(LogCalls_Debug.TAG, " onDestroyView FriendDetailsTracker")
        try {
            handler!!.removeCallbacks(runnable!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPause() {
        sensorManager!!.unregisterListener(this, sensorAccelerometer)
        sensorManager!!.unregisterListener(this, sensorMagneticField)
        super.onPause()
    }


    //Intializing widgtes
    private fun initMap() {
        supportMapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        if (supportMapFragment != null) {
            supportMapFragment!!.getMapAsync(this)
        }
    }

    //get data from previous screen
    private fun getDataFromBundle() {

        //get data from previous fragment
        val bundle = this.arguments
        if (bundle != null) {

            viewModel.setBundleData(bundle.getString(TrackerConstant.DASHBOARD_NAVIGATE))

        }
    }

    private fun initCustomView() {
        //get view binding and assign data to view
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.custommap_marker_battery, null)

        //bind: use for custom view without fragment or activity
        markerViewBinding = CustommapMarkerBatteryBinding.bind(view)

    }

    private fun setupViewModel() {

        val apiInterface = ApiRetrofit.getRetrofitInstance().create(TrackerApi::class.java)
        // View Model with factory
        val factory = FriendDetailTrackerVM.VMFactory(TrackerRepo(apiInterface))

        viewModel = ViewModelProvider(this, factory)[FriendDetailTrackerVM::class.java]
        ///////////////////////////////////////////////////

        binding!!.lifecycleOwner = this
        binding!!.viewModel = viewModel
    }

    private fun setupObservers() {
        viewModel.getNavigateFriendPlace().observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { // Only proceed if the event has never been handled
                (activity as Drawer?)!!.getFragment(FriendPlaces(), -1)
            }
        }
        viewModel.getNavigateChat().observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { // Only proceed if the event has never been handled
                (activity as Drawer?)!!.getFragment(ChatsContentTracker(), -1)
            }
        }
        viewModel.getNavigatePrivacyDialogue().observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { // Only proceed if the event has never been handled
                AlertDialogueTracker(requireActivity()).showHiddenPrivacyDialogue()
            }
        }
        viewModel.getNavigateMapDirection().observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { // Only proceed if the event has never been handled
                val uri = String.format(Locale.ENGLISH, "http://maps.google.com/maps?saddr=%f,%f(%s)&daddr=%f,%f (%s)", viewModel.customerLatLng().value!!.latitude,
                        viewModel.customerLatLng().value!!.longitude, "Your Location", viewModel.getLatitude().value, viewModel.getLongitude().value,
                        "${viewModel.getFriendName().value}location")
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                intent.setPackage("com.google.android.apps.maps")
                requireActivity().startActivity(intent)
            }
        }
        ///observer: navigate to  dashboard fragment
        viewModel.getIsMapReady().observe(viewLifecycleOwner) {
            LogCalls_Debug.d(LogCalls_Debug.TAG, " getIsMapReady ")
            /////// place marker on map getting current location lat and long coordinates
            if (it) {
                // setMapMarkerRotation()
                moveCameraPosition(viewModel.customerLatLng().value!!)
                setMarkerDeviceOrientation(viewModel.customerLatLng().value!!)

                // setFriendMapMarker(viewModel.getFriendListResponse().value!!)

                setMapMarker(viewModel.customerLatLng().value!!)

                ///friend marker data get from fragment
                setFriendLatLngMarker(LatLng(viewModel.getLatitude().value!!, viewModel.getLatitude().value!!),
                        viewModel.getFriendListResponse().value!!)

            }
        }


        //Observe data changes when api called
        viewModel.getLocationUpdateResponse().observe(viewLifecycleOwner) {
            LogCalls_Debug.d(TAG, "res")
            when (it.statusApi) {
                ResultApi.StatusApi.SUCCESS -> {
                    hideProgressBar()
                    ///friend marker data get from fragment
                    setFriendLatLngMarker(LatLng(it.data!!.contracts!!.lat.toDouble(), it.data!!.contracts!!.lng.toDouble()),
                            viewModel.getLocationUpdateResponse().value!!.data!!.contracts!!)
                }

                ResultApi.StatusApi.ERROR -> {
                    Toast.makeText(activity, it.msg, Toast.LENGTH_SHORT).show()
                    LogCalls_Debug.d(LogCalls_Debug.TAG, "ERROR")
                    hideProgressBar()
                }

                ResultApi.StatusApi.LOADING -> {
                    //  Keyboard_Close(activity).keyboard_Close_Down()
                    // showProgressBar()
                }

                ResultApi.StatusApi.FAILURE -> {
                    Toast.makeText(activity, it.msg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setFriendLatLngMarker(latLng: LatLng, contracts: Contract) {
        viewModel.setClickedLatLng(latLng)
        setFriendMapMarker(contracts)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        Log.d(Login_New.TAG, "onMapReady autoverse")
        mGoogleMap = googleMap
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL


        if (ActivityCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            //googleMap.isMyLocationEnabled = true
        }

        viewModel.isMapReady()
//        moveCameraPosition(locationList!!.get(0))
//
//
//        val handler = Handler()
//        for (a in 0..locationList!!.size - 1) {
//            handler.postDelayed({
//                setMapMarker(locationList!![a],)
//                locationBound(locationList!![0])
//            }, (3000 * a).toLong())
//        }

    }


    private fun moveCameraPosition(latLng: LatLng) {
        LogCalls_Debug.d(Login_New.TAG, "moveCameraPosition $latLng")
        if (mGoogleMap == null) {
            LogCalls_Debug.d(Login_New.TAG, "moveCameraPosition mGoogleMap ==null")
        }
        mGoogleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, mapZoom))
    }

    private fun setMapMarker(latLng: LatLng) {

        //get view binding and assign data to view
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.custommapiew_marker, null)
        //bind: use for custom view without fragment or activity
        val markerViewBinding = CustommapiewMarkerBinding.bind(view)

        Picasso.with(requireActivity()).load(viewModel.getCustomerImageMap().value).into(markerViewBinding.imgUserMapPin)

        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(markerViewBinding.root)!!))

        if (marker == null)
            marker = mGoogleMap!!.addMarker(markerOptions)

    }


    private fun setFriendMapMarker(contract: Contract) {
        if (contract.PrivacySetting.equals("3")) {
            LogCalls_Debug.d(Login_New.TAG, "setFriendMapMarker" + LatLng(contract.lat.toDouble(), contract.lng.toDouble()))
            moveCameraPosition(LatLng(contract.lat.toDouble(), contract.lng.toDouble()))
            //val markerViewBinding = CustommapiewMarkerBinding.inflate(LayoutInflater.from(context))
            if (markerFriend != null) {
                markerFriend!!.remove()
            }

            /* set listdata to view using data binding
       Data set to views from xml*/
            LogCalls_Debug.d(TAG, "battery${contract.Battery}")
            markerViewBinding.list = contract
            markerViewBinding.executePendingBindings()
///////////////////////////////////////////////////////////////////

            addIconFromURL(LatLng(contract.lat.toDouble(), contract.lng.toDouble()), contract.CustomerImage)
        }
    }

    /*
     add image to map form url
     if image loading is success then load image otherwise show textview with initial letter of customer name(eg Khan, k)
     Issue: if image is not loaded at the time of add custom view to marker then image will be not showing and textview will be showing with initial letter*/
    private fun addIconFromURL(latLng: LatLng, customerImage: String) {

        //for image is loaded to marker or not
        var isbitmap = false
        Picasso.with(requireActivity()).load(customerImage).into(object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                // loaded bitmap is here (bitmap)
                //hide initial letter textview hide. image is loading here
                markerViewBinding.ivImage.visibility = View.GONE

                markerViewBinding.imgUserMapPin.setImageBitmap(bitmap)

                //set true if image is loaded to marker as bitmap
                isbitmap = true

                setMarkerInfo(latLng)
            }

            override fun onBitmapFailed(errorDrawable: Drawable) {
                Log.d(Login_New.TAG, "onBitmapFailed: ")

            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

            }

        })
        //set false if image is not loaded, Sow textview with initial letter
        if (!isbitmap) {
            //show initial letter textview hide. image is loading here
            markerViewBinding.ivImage.visibility = View.VISIBLE
            setMarkerInfo(latLng)
        }

        LogCalls_Debug.d(TAG, "isbitmap " + isbitmap + " position ")
    }


    private fun setMarkerInfo(latLng: LatLng) {
        //create markeroption
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(createDrawableFromView(markerViewBinding.root)!!))

        markerFriend = mGoogleMap!!.addMarker(markerOptions)

    }

    private fun createDrawableFromView(view: View): Bitmap? {
        return BitmapMarker().createDrawableFromView(requireActivity(), view)
    }

//    private fun locationBound(latLng: LatLng) {
//        // LatLng fromPosition =locationList.get(0);
//        mapZoom = mGoogleMap!!.cameraPosition.zoom
//        LogCalls_Debug.d(Login_New.TAG, " Zoom  $mapZoom")
//        val bounds = mGoogleMap!!.projection.visibleRegion.latLngBounds
//        if (!bounds.contains(LatLng(latLng.latitude, latLng.longitude))) {
//            setFriendMapMarker(latLng)
//            moveCameraPosition(latLng)
//        }
//    }

    private fun showBottomSheetDialog() {

        val bottomSheetLayout = binding!!.includeBottomSheet.layoutBottom

        val sheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)

        sheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                LogCalls_Debug.d(TAG, "onStateChanged newState" + newState)
                if (sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                    LogCalls_Debug.d(TAG, " expanded if")

                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding!!.includeBottomSheet.imgShowHide.setRotation(slideOffset * 180)
            }
        })

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

    //using to handler delay webservices call for 30 secs
    private fun timer() {
        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {
                Log.d("Timer", "Timer")
                //Call webservices and sending,fetching data from webservice
                viewModel.onLocationUpdate()
                handler!!.postDelayed(this, delayWebApiCall)
            }
        }
        handler!!.postDelayed(runnable!!, delayWebApiCall)
    }

    private fun setMarkerDeviceOrientation(latLng: LatLng) {
        if (markerRotateOrientation != null) {
            markerRotateOrientation!!.remove()
        }

        val height = 300
        val width = 220

        val bitmapdraw = ResourcesCompat.getDrawable(resources, R.drawable.map_direction_orientation1, null) as BitmapDrawable
        val b = bitmapdraw.bitmap
        val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)

        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        ////markerOptions.title(latLng.latitude + "," + latLng.longitude);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
        markerOptions.flat(true)

        markerRotateOrientation = mGoogleMap!!.addMarker(markerOptions)
    }

    private fun setMarkerOrientation() {

        sensorManager = requireActivity().getSystemService(Context.SENSOR_SERVICE) as SensorManager?

        sensorAccelerometer = sensorManager!!.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorMagneticField = sensorManager!!.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)


        valuesAccelerometer = FloatArray(3)
        valuesMagneticField = FloatArray(3)
        matrixR = FloatArray(9)
        matrixI = FloatArray(9)
        matrixValues = FloatArray(3)
    }

    override fun onAccuracyChanged(arg0: Sensor?, arg1: Int) {

    }

    override fun onSensorChanged(event: SensorEvent) {

        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                var i = 0
                while (i < 3) {
                    valuesAccelerometer[i] = event.values[i]
                    i++
                }
            }

            Sensor.TYPE_MAGNETIC_FIELD -> {
                var i = 0
                while (i < 3) {
                    valuesMagneticField[i] = event.values[i]
                    i++
                }
            }
        }
        val success = SensorManager.getRotationMatrix(
                matrixR,
                matrixI,
                valuesAccelerometer,
                valuesMagneticField)
        if (success) {
            SensorManager.getOrientation(matrixR, matrixValues)
            azimuth = Math.toDegrees(matrixValues[0].toDouble())
            val pitch = Math.toDegrees(matrixValues[1].toDouble())
            val roll = Math.toDegrees(matrixValues[2].toDouble())

            //binding!!.heading.setText("Azimuth: $azimuth")
            // binding!!.includeBottomSheet.tvFriend.setText("Pitch: $pitch")
            // binding!!.includeBottomSheet.tvTrackerKM.setText("Roll: $roll")
            //   myCompass.update(matrixValues[0])
            //LogCalls_Debug.d(TAG, "bearing value " + matrixValues[0])
            if (markerRotateOrientation != null)
                markerRotateOrientation!!.rotation = azimuth.toFloat()
        }
    }

    ////////////// When on Back button pressed fragment navigate to prevoius fragment
    private val onBackPressedCallback: OnBackPressedCallback = object : OnBackPressedCallback(true /* Enabled by default */) {
        override fun handleOnBackPressed() {
            try {
                handler!!.removeCallbacks(runnable!!)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            // Handle the back button event
            LogCalls_Debug.d(LogCalls_Debug.TAG + FriendDetailsTracker::class.java.name, "handleOnBackPressed false")

            if (viewModel.getIsDashboardNavigate().value != true) {
                LogCalls_Debug.d(LogCalls_Debug.TAG + FriendDetailsTracker::class.java.name, "handleOnBackPressed TrackerDashboard")
                requireActivity().supportFragmentManager.popBackStack()
                (activity as Drawer?)!!.getFragment(TrackerDashboard(), -1)
            } else {
                LogCalls_Debug.d(LogCalls_Debug.TAG + FriendDetailsTracker::class.java.name, "handleOnBackPressed else")
                closeFragment()
            }

        }
    }

    private fun closeFragment() {

        requireActivity().supportFragmentManager.popBackStack()

    }

    fun getlocation() {

//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                mFusedLocationClient.getLastLocation().addOnSuccessListener(requireActivity()) { location ->
//                    // Got last known location. In some rare situations this can be null.
//                    if (location != null) {
//                        // moveCameraPosition(LatLng(location.latitude, location.longitude))
//                        setMarkerDeviceOrientation(LatLng(location.latitude, location.longitude))
//                        setMapMarker(LatLng(location.latitude, location.longitude))
//
//                    }
//                }
//            }
//        }

    }


    ////////////////////////////////////////////////////


    fun getListOfLocations() {


///////////////////////////////////////////////////////////////////////////////////
        locationList!!.add(LatLng(33.591953620228566, 73.05396141140102))
        locationList!!.add(LatLng(33.59224474594921, 73.05430463578708))
        locationList!!.add(LatLng(33.59237344878411, 73.05440986034866))
        locationList!!.add(LatLng(33.59268951143851, 73.05472172903931))
        locationList!!.add(LatLng(33.59293685395624, 73.05441863591209))
        locationList!!.add(LatLng(33.59313038527008, 73.0541740469459))
        locationList!!.add(LatLng(33.59330161942487, 73.05394936474869))
        locationList!!.add(LatLng(33.59369561917209, 73.05344704695945))
        locationList!!.add(LatLng(33.593918926254624, 73.05293481741238))
        locationList!!.add(LatLng(33.5938451967951, 73.05283021096324))
        locationList!!.add(LatLng(33.59315010574684, 73.05214209336107))
        locationList!!.add(LatLng(33.59310502518372, 73.0519998604808))
        locationList!!.add(LatLng(33.59338493861627, 73.05144619526268))
        locationList!!.add(LatLng(33.59388261964929, 73.05082576717844))
        locationList!!.add(LatLng(33.59408188965285, 73.05054507598507))
        locationList!!.add(LatLng(33.594227357726496, 73.0502769197996))
        locationList!!.add(LatLng(33.59396260226414, 73.050479426831))
    }


}


