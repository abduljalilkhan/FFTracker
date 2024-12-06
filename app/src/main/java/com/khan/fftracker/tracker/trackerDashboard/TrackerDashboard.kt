package com.khan.fftracker.tracker.trackerDashboard

import android.Manifest
import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.BatteryManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.khan.fftracker.R
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityRecognitionClient
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.gms.maps.model.RoundCap
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.khan.fftracker.Autoverse.GetAddressFromLatLong
import com.khan.fftracker.Autoverse.Location.MarkerAnimation
import com.khan.fftracker.Item_Interface.CommonStuffInterface
import com.khan.fftracker.Location_FusedAPI.GetCurrentLocation
import com.khan.fftracker.logCalls.LogCalls_Debug
import com.khan.fftracker.Navigation_Drawer.Drawer
import com.khan.fftracker.Network_Volley.IsAdmin
import com.khan.fftracker.Permission_Granted.LocationPermission
import com.khan.fftracker.Permission_Granted.PermissionListener
import com.khan.fftracker.RecylerViewClicked.RecyclerViewItemListener
import com.khan.fftracker.autoverse_mvvm.Network_Stuff.ApiRetrofit
import com.khan.fftracker.autoverse_mvvm.networkApi.ResultApi
import com.khan.fftracker.autoverse_mvvm.utils.GenericResponse
import com.khan.fftracker.commanStuff.Bitmap_Stuff.BitmapMarker
import com.khan.fftracker.commanStuff.ShareDataIntent
import com.khan.fftracker.databinding.CustommapiewMarkerBinding
import com.khan.fftracker.databinding.TrackerDashboardBinding
import com.khan.fftracker.tracker.TrackerConstant
import com.khan.fftracker.tracker.aApiTracker.TrackerApi
import com.khan.fftracker.tracker.aApiTracker.TrackerRepo
import com.khan.fftracker.tracker.addFriendContactList.AddFriendContact
import com.khan.fftracker.tracker.customAlertDialogue.AlertDialogueTracker
import com.khan.fftracker.tracker.friendsDetail.FriendDetailsTracker
import com.khan.fftracker.tracker.trackerDashboard.adaptor.TrackerDashBoardAdaptor
import com.khan.fftracker.tracker.trackerDashboard.dataModel.Contract
import com.khan.fftracker.tracker.trackerDashboard.viewModel.TrackerDashboardVM
import com.khan.fftracker.tracker.trackerHistory.TrackerHistory
import com.khan.fftracker.tracker.userMenuSetting.UserMenu
import com.squareup.picasso.Picasso
import com.squareup.picasso.Picasso.LoadedFrom


class TrackerDashboard : Fragment(), OnMapReadyCallback, PermissionListener, CommonStuffInterface,
    RecyclerViewItemListener, SensorEventListener {

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var markerViewBinding: CustommapiewMarkerBinding
    private var adaptor: TrackerDashBoardAdaptor? = null
    private val TAG = TrackerDashboard::class.simpleName

    var binding: TrackerDashboardBinding? = null
    var mView: View? = null

    lateinit var viewModel: TrackerDashboardVM

    //run time permission fragment
    val TAG_LOC_PERMISSIONS_FRAGMENT = "locationPermissions"
    lateinit var locationPermissionFragment: LocationPermission

    var addressFromLatLong: GetAddressFromLatLong? = null

    /// IsAdmin: Class for using check whether customer or admin logged in
    var isAdmin: IsAdmin? = null


    lateinit var mFusedLocationClient: FusedLocationProviderClient

    private val hashMapMarker = HashMap<Int, Marker>()

    ///Google map
    var mGoogleMap: GoogleMap? = null
    var supportMapFragment: SupportMapFragment? = null
    var marker: Marker? = null
    var markerFriend: Marker? = null
    var markerRotateOrientation: Marker? = null
    private var mapZoom = 16.0f


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


    ////////////////////////////

    private var locationList: ArrayList<LatLng>? = null


    var handler: Handler? = null
    var runnable: Runnable? = null
    private val delayWebApiCall = (5000 * 3 //delay webservices call for 30 secs
            ).toLong()

    private lateinit var currentLocation: GetCurrentLocation

    lateinit var client: ActivityRecognitionClient
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.tracker_dashboard, container, false)

        mView = binding!!.root

        //backpressed
        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(), onBackPressedCallback
        )

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        batteryLevel()

        //setup viewmodel
        setupViewModel()
        /// Initialize class
        initializeAdminClass()

        initializeCurrentLocation()

        ///set permission fragment
        initializePermissionFragment()

        //Initializing map
        initMap()

        /* custom google map marker
         set custom layout to map marker*/
        initMarkerCustomView()

        //call background permission method in permission fragment
        setBackgroundLocPermission()

//        //show persistent bottom dialogue to display list of friend
//        showBottomSheetDialog()

        locationList = ArrayList()
        //getListOfLocations()

        //using to handler delay webservices call for 30 secs
        // timer()


        // The Activity Recognition Client returns a
        // list of activities that a user might be doing
        client = ActivityRecognition.getClient(requireActivity())
       // activityTransition();
        return mView
    }



    private fun showToast(message: String) {
        Toast.makeText(requireActivity(), message, Toast.LENGTH_LONG)
            .show()
    }



    private fun initializeCurrentLocation() {
        if (!this::currentLocation.isInitialized){

            LogCalls_Debug.d(TAG, " initializeCurrentLocation ")
            currentLocation = GetCurrentLocation(requireActivity(), this)
            currentLocation.getlocation()
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // markerViewBinding = CustommapiewMarkerBinding.inflate(LayoutInflater.from(context))

        addressFromLatLong = GetAddressFromLatLong(activity)


        //observe data changes in viewmodel
        setupObservers()

        // getlocation()
        setMarkerOrientation()
        //initialization recyclerview and set layout manager
        initRecyclerView()
        //show persistent bottom dialogue to display list of friend
        showBottomSheetDialog()

    }


    override fun onResume() {
        super.onResume()

        /*call friendlist to fetched all friend data
        called this api in resume method instead of init block of VM to fetch every time fresh data*/
        callFriendListApi()

        LogCalls_Debug.d(TAG, "onresume")
        supportMapFragment!!.onResume()
        // setBackGroundLocPermission()

        sensorManager!!.registerListener(
            this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL
        )
        sensorManager!!.registerListener(
            this, sensorMagneticField, SensorManager.SENSOR_DELAY_NORMAL
        )
    }

    private fun callFriendListApi() {
        viewModel.onFriendListApi()

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

    //initialization recyclerview and set layout manager
    private fun initRecyclerView() {
        LogCalls_Debug.d(TAG, "initRecyclerView")

        adaptor = TrackerDashBoardAdaptor(requireActivity(), viewModel.listFriendMarker, this)

        binding!!.includeBottomSheet.rv.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        binding!!.includeBottomSheet.rv.adapter = adaptor

        binding!!.includeBottomSheet.rvhorizontal.adapter = adaptor

    }

    //data set to rv

    private fun setRecyclerView(list: List<Contract>) {

        viewModel.setRecyclerViewVisibility()

        adaptor!!.notifyDataSetChanged()

        LogCalls_Debug.d(TAG, "adaptor recylerview " + adaptor!!.itemCount)
    }

    /* custom google map marker
     set custom layout to map marker*/
    private fun initMarkerCustomView() {
        //get view binding and assign data to view
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.custommapiew_marker, null)

        //bind: use for custom view without fragment or activity
        //bind: for databinding custom
        markerViewBinding = CustommapiewMarkerBinding.bind(view)

    }

    //setup viewmodel
    private fun setupViewModel() {
        //get retrofit instance
        val apiInterface = ApiRetrofit.getRetrofitInstance().create(TrackerApi::class.java)
        // View Model with factory
        val factory = TrackerDashboardVM.VMFactory(TrackerRepo(apiInterface))

        viewModel = ViewModelProvider(this, factory)[TrackerDashboardVM::class.java]
        ///////////////////////////////////////////////////

        binding!!.lifecycleOwner = this
        binding!!.viewModel = viewModel
    }

    private fun setupObservers() {

        viewModel.getContactPermissionGranted().observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { // Only proceed if the event has never been handled
                (activity as Drawer?)!!.getFragment(AddFriendContact(), -1)

            }
        }
        viewModel.getNavigateToMenuSetting().observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { // Only proceed if the event has never been handled
                (activity as Drawer?)!!.getFragment(UserMenu(), -1)

            }
        }

        viewModel.getNavigateTrackerHistory().observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { // Only proceed if the event has never been handled
                moveCameraPosition(viewModel.customerLatLng().value!!)
                (activity as Drawer?)!!.getFragment(TrackerHistory(), -1)

            }
        }

        //observe to show alert dialog
        viewModel.getAlertDialogue().observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { // Only proceed if the event has never been handled
                AlertDialogueTracker(requireActivity()).dialogThreeButton(
                    viewModel.getAlertListDialogue(),
                    this)

            }
        }
        ///observer: navigate to  ChooseAddress fragment
        viewModel.getNavigateFriendDetail().observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { // Only proceed if the event has never been handled
                if (viewModel.getPrivacySetting().value.equals("3")) {
                    moveCameraPosition(viewModel.getLatLng().value!!)
                }
                ///pass data to next fragment
                val bundle = Bundle()
                bundle.putString(TrackerConstant.DASHBOARD_NAVIGATE, "1")

                val fragment = FriendDetailsTracker()
                fragment.arguments = bundle
                (activity as Drawer?)!!.getFragment(fragment, -1)
            }
            LogCalls_Debug.d(TAG, " getNavigateFriendDetail ")
        }

        ///observer:   //map take few secs to be ready. call this boolean value let know map operations
        viewModel.getIsMapReady().observe(viewLifecycleOwner) {
            LogCalls_Debug.d(TAG, " getIsMapReady ")
            it.getContentIfNotHandled()?.let {
                /////// place marker on map getting current location lat and long coordinates
                // if (it) {
                //if (viewModel._fstTime.value == true) {
                //viewModel._fstTime.value = false

                moveCameraPosition(viewModel.getLatLng().value!!)
                viewModel.removeUserMarker()
                userMarkerSetting()
                // }
                // getlocation()
                //getListOfLocations()

            }
        }
        //Observe data changes when api called
        viewModel.getFriendListResponse().observe(viewLifecycleOwner) {

            when (it.statusApi) {
                ResultApi.StatusApi.SUCCESS -> {
                    LogCalls_Debug.d(TAG, "getFriendListResponse SUCCESS")
                    hideProgressBar()
                    viewModel.manageResponse(it.data)
                    setRecyclerView(it.data!!.contracts)

                    Handler(Looper.getMainLooper()).postDelayed(Runnable {
                        // marker!!.remove()
                        //set marker from url
                        setFriendsTrackLoc(it.data.contracts)



                    }, 1000)

                }

                ResultApi.StatusApi.ERROR -> {
                    Toast.makeText(activity, it.msg, Toast.LENGTH_SHORT).show()
                    LogCalls_Debug.d(TAG, "ERROR")
                    hideProgressBar()
                }

                ResultApi.StatusApi.LOADING -> {
                    //  Keyboard_Close(activity).keyboard_Close_Down()
                    // showProgressBar()
                    //initRecyclerView()
                }

                ResultApi.StatusApi.FAILURE -> {
                    Toast.makeText(activity, it.msg, Toast.LENGTH_SHORT).show()
                }
            }
        }

        //Observe data changes when api called
        viewModel.getInviteLinkResponse().observe(viewLifecycleOwner) {

            when (it.statusApi) {
                ResultApi.StatusApi.SUCCESS -> {
                    hideProgressBar()
                    viewModel.setInviteUrl(it.data!!.InviteURL)

                    ShareDataIntent(requireActivity()).sendTextMessage(viewModel.getPhoneNo().value, viewModel.getInviteUrl().value)
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

        //Observe data changes when api called
        viewModel.getDeleteFriendResponse().observe(viewLifecycleOwner) {
            when (it.statusApi) {
                ResultApi.StatusApi.SUCCESS -> {
                    hideProgressBar()
                    setDeleteFriendResponse(it.data)
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


    private fun setDeleteFriendResponse(data: GenericResponse?) {
        try {
            if (data!!.success==1) {
                viewModel.listFriendMarker.removeAt(viewModel.listPos.value!!)
                adaptor!!.notifyItemRemoved(viewModel.listPos.value!!)
            }
            else{
                Toast.makeText(activity, ""+data.message, Toast.LENGTH_SHORT).show()
            }
        }catch (e:Exception){
            LogCalls_Debug.d(TAG,e.message)
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        Log.d(TAG, "onMapReady")
        //assign googlemap
        mGoogleMap = googleMap

        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL


        if (ActivityCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // googleMap.isMyLocationEnabled = true
        }
        if (viewModel._fstTime.value == true) {
            //map take few secs to be ready. call this boolean value let know map operations
            viewModel.isMapReady()
        }
        //getlocation()

        adaptor!!.notifyDataSetChanged()

        //click marker on map will navigate to next fragment
        mapClickListener()

    }

    @SuppressLint("PotentialBehaviorOverride")
    private fun mapClickListener() {
        mGoogleMap!!.setOnMarkerClickListener {
            LogCalls_Debug.d(TAG, "tag== " + tag)

            /*gettag= to get position for list which is used in view model (friendlist)
      to return position from list and navigate to next screen
      when clicked on marker to get unique tag to identify which item of friend list will be saved for next fragment*/
            val tag = it.tag

            if (tag == -1) {
                viewModel.onNavigateTrackerHistory()

                false
            } else {
                if (tag != null) {
                    LogCalls_Debug.d(TAG, "tag==null " + tag)

                    val position = it.tag as Int/*    listFriendMarker==in List first item will be logged in user info
                    remaining item will be friend data fetched from web api
                    setMarkerClickPos(position+1)== add 1+(0 is user position in list) to get friend data */
                    viewModel.setMarkerClickPos(position + 1)
                    //Using position get Value from arraylist
                }
                false
            }
        }
    }


    //move camera position to specific latlng
    private fun moveCameraPosition(latLng: LatLng) {
        Log.d(TAG, "moveCameraPosition $latLng")
        mGoogleMap!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, mapZoom))

    }

    //get list from backend and do loop for all items in loop
    //set friend list data to map
    private fun setFriendsTrackLoc(list: List<Contract>) {
        //initCustomView()

//        var x = 0
//        while (x < locationList!!.size - 1) {
//            x += 2
//            setMapMarker(LatLng(locationList!![x].latitude, locationList!![x].longitude), x, list[0])
//        }

        //get list from backend and do loop for all items in loop
        for (a in list.indices) {
            //don't show marker which privacy setting is not equal to 3
            if (list[a].PrivacySetting.equals("3")) {/* set listdata to view using data binding
        Data set to views from xml*/
                markerViewBinding.list = list[a]
                markerViewBinding.executePendingBindings()/*setup google map marker with custom view
            marker image is set from url*
            Note : friend and user ar using same custom marker view*/
                setMapMarker(
                    LatLng(list[a].lat.toDouble(), list[a].lng.toDouble()), a, list[a].CustomerImage
                )
            }
        }
    }

    //set current user data to map which is logged to app (device location)
    private fun userMarkerSetting() {
        Log.d(TAG, "userMarkerSetting")
        ///rotate the marker direction according to the direction of the phone
        setMarkerDeviceOrientation(viewModel.customerLatLng().value!!)
        /// friend and user ar using same custom marker view
        setMapMarker(viewModel.customerLatLng().value!!, viewModel.getUserMarkerTag().value!!, viewModel.getCustomerImageMap().value)
    }

    /*setup google map marker with custom view
          marker image is set from url*/
    private fun setMapMarker(latLng: LatLng, a: Int, customerImage: String?) {
        //  markerViewBinding = CustommapiewMarkerBinding.inflate(LayoutInflater.from(context))

        addIconFromURL(latLng, customerImage!!, a)

    }

    /*
     add image to map form url
     if image loading is success then load image otherwise show textview with initial letter of customer name(eg Khan, k)
     Issue: if image is not loaded at the time of add custom view to marker then image will be not showing and textview will be showing with initial letter*/
    private fun addIconFromURL(latLng: LatLng, customerImage: String, a: Int) {

        //for image is loaded to marker or not
        var isbitmap = false
        Picasso.with(requireActivity()).load(customerImage).into(object : Target {
            override fun onBitmapLoaded(bitmap: Bitmap, from: LoadedFrom) {
                // loaded bitmap is here (bitmap)
                //hide initial letter textview hide. image is loading here
                markerViewBinding.ivImage.visibility = View.GONE
                markerViewBinding.imgUserMapPin.setImageBitmap(bitmap)

                //set true if image is loaded to marker as bitmap
                isbitmap = true

                //set marker info latlng,icon etc
                setMarkerInfo(latLng, a)

            }

            override fun onBitmapFailed(errorDrawable: Drawable) {
                Log.d(TAG, "onBitmapFailed: ")

            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {

            }

        })
        //set false if image is not loaded, Sow textview with initial letter
        if (!isbitmap) {
            //show initial letter textview hide. image is loading here
            markerViewBinding.ivImage.visibility = View.VISIBLE
            //set marker info latlng,icon etc
            setMarkerInfo(latLng, a)
        }

        LogCalls_Debug.d(TAG, "isbitmap " + isbitmap + " position " + a)
    }

    //set marker info latlng,icon etc
    private fun setMarkerInfo(latLng: LatLng, a: Int) {
        val offset: Double = a / 90.0
        val lat = latLng.latitude + offset
        val lng = latLng.longitude + offset

        // val latLng= LatLng(lat,lng)
        //create markeroption
        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        //set custom layout as bitmap to map
        markerOptions.icon(
            BitmapDescriptorFactory.fromBitmap(
                createDrawableFromView(
                    markerViewBinding.root
                )!!
            )
        )

        marker = mGoogleMap!!.addMarker(markerOptions)

        /*gettag= to get position for list which is used in view model (friendlist)
        to return position from list and navigate to next screen
        when clicked on marker to get unique tag to identify which item of friend list will be saved for next fragment*/
        marker!!.tag = a

        // hashMapMarker[a] = marker!!
        viewModel.addUserMarker(a, marker!!)

    }





    //return bitmap from custom layout to add marker on google map
    private fun createDrawableFromView(view: View): Bitmap? {
        return BitmapMarker().createDrawableFromView(requireActivity(), view)
    }

    /// rotate the marker direction according to the direction of the phone
    private fun setMarkerDeviceOrientation(latLng: LatLng) {
        if (markerRotateOrientation != null) {
            markerRotateOrientation!!.remove()
        }
        val height = 300
        val width = 220

        val bitmapdraw = ResourcesCompat.getDrawable(
            resources, R.drawable.map_direction_orientation1, null
        ) as BitmapDrawable
        val b = bitmapdraw.bitmap
        val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)

        val markerOptions = MarkerOptions()
        markerOptions.position(latLng)
        ////markerOptions.title(latLng.latitude + "," + latLng.longitude);
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
        markerOptions.flat(true)

        markerRotateOrientation = mGoogleMap!!.addMarker(markerOptions)
    }


    ///initialize permission fragment
    private fun initializePermissionFragment() {
        /// Runtime location permissions
        locationPermissionFragment = LocationPermission()

        this.requireActivity().supportFragmentManager.beginTransaction()
            .add(locationPermissionFragment, TAG_LOC_PERMISSIONS_FRAGMENT).commit()
        locationPermissionFragment.setPermissionGrantedListener(this)
    }


    //call background permission method in permission fragment
    private fun setBackgroundLocPermission() {
        //delay to 2miliseconds to let the parent fragment appear
        val handler = Handler(Looper.getMainLooper())

        handler.postDelayed({

            locationPermissionFragment.setLocationPermission()
        }, 200)
    }


    @RequiresApi(Build.VERSION_CODES.Q)
    override fun permissionGrantedListener(strResult: String) {
        LogCalls_Debug.d(TAG, "permissionGrantedListener " + strResult)
        when (strResult) {
            "accessFine" -> {
                LogCalls_Debug.d(TAG, "location granted")
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

            //,"NoEnableGps"
            "batteryOptimize" -> {
                locationPermissionFragment.setEnableGPS()
            }


            "enableGps" -> {
                Handler(Looper.getMainLooper()).postDelayed({
                    // getlocation()
                    getCurrentLocation()
                }, 1000)

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
            s.equals("btnDoneDialog") -> {
                viewModel.onDeleteFriendApi()
            }
            s.equals("btnOkDialog") -> {
                viewModel.onInviteLinkApi()
            }
            s!!.contains("loc") -> {
                val strarr = s.split("loc".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val lat = strarr[0].toDouble()
                val lng = strarr[1].toDouble()

                viewModel.onLocationUpdate(lat, lng)
               // viewModel.removeUserMarker()
              //  userMarkerSetting()

                moveUserLoggedInMarker()
            }

        }
    }

    private fun moveUserLoggedInMarker() {
        LogCalls_Debug.d(TAG, " moveUserLoggedInMarker")
        markerRotateOrientation!!.position=viewModel.customerLatLng().value!!

        if (viewModel.defaultUserMarker() != null){
            val marker=viewModel.defaultUserMarker()
            marker!!.position=viewModel.customerLatLng().value!!
            LogCalls_Debug.d(TAG, "  viewModel.defaultUserMarker()")
           // Toast.makeText(requireActivity(),"moveUserLoggedInMarker",Toast.LENGTH_SHORT).show()
        }
        /// friend and user ar using same custom marker view
     //   setMapMarker(viewModel.customerLatLng().value!!, viewModel.getUserMarkerTag().value!!, viewModel.getCustomerImageMap().value)
    }


    override fun onItemClick(get: HashMap<String, Any>, pos: Int) {}
    override fun onItemClickObject(id: Int, any: Any, pos: Int) {

        if (pos==0){
            viewModel.onNavigateTrackerHistory()

            return
        }
        if (any is Contract) {

//            Contract id=0
//            2 array is in this dashboard response
//            Contract and pending arrays
//            In pending array contract id will be 0 this means invited friend is still not accepted your request
            if (any.ContractID == "0") {
                //show alert dialog to to send invitation to friend
                viewModel.showAlertDialogue(any,pos)
            }
            else viewModel.onNavigateFriendDetail(any)
        }
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
            matrixR, matrixI, valuesAccelerometer, valuesMagneticField
        )
        if (success) {
            SensorManager.getOrientation(matrixR, matrixValues)
            azimuth = Math.toDegrees(matrixValues[0].toDouble())
            val pitch = Math.toDegrees(matrixValues[1].toDouble())
            val roll = Math.toDegrees(matrixValues[2].toDouble())

            //binding!!.heading.setText("Azimuth: $azimuth")
            // binding!!.includeBottomSheet.tvFriend.setText("Pitch: $pitch")
            // binding!!.includeBottomSheet.tvTrackerKM.setText("Roll: $roll")
            //   myCompass.update(matrixValues[0])
            // LogCalls_Debug.d(TAG, "bearing value " + matrixValues[0])
            if (markerRotateOrientation != null) markerRotateOrientation!!.rotation =
                azimuth.toFloat()
        }
    }


    private fun showBottomSheetDialog() {
//        if (adaptor != null) {
//            adaptor!!.notifyDataSetChanged()
//
//            LogCalls_Debug.d(TAG, "adaptor " + adaptor!!.itemCount)
//        }
        LogCalls_Debug.d(TAG, "showBottomSheetDialog")
        val bottomSheetLayout = binding!!.includeBottomSheet.layoutBottom

        val sheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)

        sheetBehavior.isFitToContents = false
        sheetBehavior.halfExpandedRatio = 0.5f

        //sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        sheetBehavior.state = viewModel.bottomSheetState.value!!


        if (viewModel.bottomSheetState.value == BottomSheetBehavior.STATE_COLLAPSED) {
            LogCalls_Debug.d(
                TAG,
                "viewModel.bottomSheetState.STATE_COLLAPSED " + viewModel.bottomSheetState.value
            )
            if (adaptor!!.itemCount > 1) {
                setRvLayoutDragging(LinearLayoutManager.HORIZONTAL, "horizontal", false)

            }
        }

        sheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                LogCalls_Debug.d(TAG, "onStateChanged newState" + newState)
                viewModel.bottomSheetState.value = newState
                if (adaptor!!.itemCount > 1) {
                    //val la = binding!!.includeBottomSheet.rv.layoutManager!! as
                    val layoutOrientation =
                        (binding!!.includeBottomSheet.rv.layoutManager as LinearLayoutManager).orientation

                    //if (sheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED || sheetBehavior.state == BottomSheetBehavior.STATE_DRAGGING) {
                    if (sheetBehavior.state == BottomSheetBehavior.STATE_DRAGGING) {
                        LogCalls_Debug.d(TAG, " expanded if")
                        if (LinearLayout.VERTICAL != layoutOrientation) {
                            //  binding!!.includeBottomSheet.rv.post {
                            //sheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
                            setRvLayoutDragging(LinearLayoutManager.VERTICAL, "vertical", true)

                        }
                        // }

                    }
                    if (sheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
                        if (LinearLayout.HORIZONTAL != layoutOrientation) {

                            setRvLayoutDragging(LinearLayoutManager.HORIZONTAL, "horizontal", false)
                        }
                    }

                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                //binding!!.includeBottomSheet.imgShowHide.rotation = slideOffset * 180
            }

        })


    }

    private fun setRvLayoutDragging(
        orientation: Int, strAdaptorOrientation: String, isRecyclerViewShow: Boolean
    ) {
        adaptor!!.setOrientation(strAdaptorOrientation)
        binding!!.includeBottomSheet.rv.postDelayed({
            layoutManager = LinearLayoutManager(activity, orientation, false)
            binding!!.includeBottomSheet.rv.layoutManager = layoutManager
            //  adaptor!!.setOrientation(strAdaptorOrientation)


            //viewModel.setRecyclerViewVisibility()
            //  adaptor!!.notifyDataSetChanged()
            // viewModel.isRecyclerViewShow.value = isRecyclerViewShow
            if (!isRecyclerViewShow) {
                //  adaptor!!.notifyDataSetChanged()
            }
        }, 0)
    }


    val batteryLevelReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            // context.unregisterReceiver(this)
            val rawlevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 0)
            viewModel.calculteBatteryPercentage(rawlevel, scale)
        }
    }

    private fun batteryLevel() {

        val batteryLevelFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        requireActivity().registerReceiver(batteryLevelReceiver, batteryLevelFilter)

    }

    override fun onDestroyView() {
        super.onDestroyView()
        //if (batteryLevelReceiver!=null) {
        requireActivity().unregisterReceiver(batteryLevelReceiver)
        //  }
        try {
            currentLocation.removeLocation()

            handler!!.removeCallbacks(runnable!!)
           // deregisterForUpdates()

            LogCalls_Debug.d(TAG, "ondestroy dashboardTracker")
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

    private fun getCurrentLocation() {
        LogCalls_Debug.d(TAG, " getCurrentLocation")
        if (ActivityCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireActivity(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        mFusedLocationClient.getCurrentLocation(LocationRequest.PRIORITY_HIGH_ACCURACY,
            object : CancellationToken() {

                override fun onCanceledRequested(onTokenCanceledListener: OnTokenCanceledListener): CancellationToken {
                    Log.d("CurrentLocation", "CancelRequest")
                    return CancellationTokenSource().token
                }

                override fun isCancellationRequested(): Boolean {
                    return false
                }
            }).addOnSuccessListener(OnSuccessListener<Location?> { location ->

            if (location != null) {

                LogCalls_Debug.d(TAG, " addOnSuccessListener " + location.toString())
                //call api
                viewModel.onLocationUpdate(location.latitude, location.longitude)
               // viewModel.removeUserMarker()
               // userMarkerSetting()
                moveUserLoggedInMarker()

                if (viewModel._locationGetFstTime.value == true) {
                    viewModel._locationGetFstTime.value = false
                    moveCameraPosition(viewModel.customerLatLng().value!!)
                }
                // viewModel.isMapReady()
            }

            viewModel.saveTrackerEnable()
            //getCurrentLocation.getlocation()
        })

    }

    ////////////// When on Back button pressed fragment navigate to Main DashBoard fragment
    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true /* Enabled by default */) {
            override fun handleOnBackPressed() {
                try {
                    // handler!!.removeCallbacks(runnable!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                // Handle the back button event
                LogCalls_Debug.d(TAG, "handleOnBackPressed false")

                closeFragment()

            }
        }

    private fun closeFragment() {

        requireActivity().supportFragmentManager.popBackStack()

    }


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


        val polyLineOptions = PolylineOptions()
        polyLineOptions.addAll(locationList!!)
        polyLineOptions.width(10f)
        polyLineOptions.color(Color.BLUE)
        polyLineOptions.geodesic(true)
        polyLineOptions.startCap(RoundCap())
        polyLineOptions.endCap(RoundCap())
        polyLineOptions.jointType(JointType.ROUND)
        mGoogleMap!!.addPolyline(polyLineOptions)



        val builder = LatLngBounds.Builder()
        builder.include(locationList!![0])
        builder.include(locationList!![locationList!!.size - 1])
        val bounds = builder.build()
        mGoogleMap!!.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 10))

        val markerOptions = MarkerOptions()
        markerOptions.position(locationList!![0])

        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.av_pin_location))

        markerFriend = mGoogleMap!!.addMarker(markerOptions)

        val handler1 = Handler()
        var markerAnimation=MarkerAnimation()
        val animationDuration=5000/locationList!!.size - 1

        LogCalls_Debug.d(TAG, " animationDur $animationDuration")
        Handler().postDelayed({
            markerAnimation.pulseCircleTest(mGoogleMap,locationList!![7])

            val height = 80
            val width = 80


            val bitmapDraw = ResourcesCompat.getDrawable(
                resources, R.drawable.marker_pulse, null
            ) as BitmapDrawable
            val b = bitmapDraw.bitmap
            val smallMarker = Bitmap.createScaledBitmap(b, width, height, false)
           // pulseMarker(smallMarker, markerFriend!!, 1000)
            MarkerAnimation().pulseMarker(smallMarker, markerFriend!!, 1000)

        for (a in 0..locationList!!.size - 1) {
            handler1.postDelayed({
                MarkerAnimation().animateMarkerNew(
                    locationList!![a],
                    markerFriend!!,
                    200
                )
                 // animateMarkerNew(locationList!![a])

            }, (1000 * a).toLong())

        }
        },5000)
    }


    ///////////////////////////////////////////////////////////////////////////////////////////////////


    private fun activityTransition() {
        LogCalls_Debug.d(TAG,"activityTransition")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf<String>(Manifest.permission.ACTIVITY_RECOGNITION),
                21
            )
        }

        if (ContextCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACTIVITY_RECOGNITION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            LogCalls_Debug.d(TAG,"ACTIVITY_RECOGNITION PERMISSION_GRANTED  ")
            requestForUpdates()
        }

    }

    // To register for changes we have to also supply the requestActivityTransitionUpdates() method
    // with the PendingIntent object that will contain an intent to the component
    // (i.e. IntentService, BroadcastReceiver etc.) that will receive and handle updates appropriately.
    private fun requestForUpdates() {
        LogCalls_Debug.d(TAG,"requestForUpdates")
        client
            .requestActivityTransitionUpdates(
                ActivityTransitionsUtil().getActivityTransitionRequest(),
                getPendingIntent()
            )
            .addOnSuccessListener {
                showToast("successful registration")
                LogCalls_Debug.d(TAG,"addOnSuccessListener")
            }
            .addOnFailureListener {
                showToast("Unsuccessful registration")
                LogCalls_Debug.d(TAG,"addOnFailureListener")
            }
    }

    // Deregistering from updates
    // call the removeActivityTransitionUpdates() method
    // of the ActivityRecognitionClient and pass
    // ourPendingIntent object as a parameter
    private fun deregisterForUpdates() {
        client
            .removeActivityTransitionUpdates(getPendingIntent())
            .addOnSuccessListener {
                getPendingIntent().cancel()
                showToast("successful deregistration")
            }
            .addOnFailureListener { e: Exception ->
                showToast("unsuccessful deregistration")
            }
    }

    // creates and returns the PendingIntent object which holds
    // an Intent to an BroadCastReceiver class
    private fun getPendingIntent(): PendingIntent {

        val intent = Intent(context, ActivityTransitionReceiver::class.java)
        return PendingIntent.getBroadcast(
            context,
            0,
            intent,
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
                PendingIntent.FLAG_CANCEL_CURRENT
            } else {
                PendingIntent.FLAG_MUTABLE
            }
        )
    }


    //using to handler delay webservices call for 30 secs
    private fun timer() {
        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {
                Log.d("Timer", "Timer")
                //Call webservices and sending,fetching data from webservice
                viewModel.onFriendListApi()
                handler!!.postDelayed(this, delayWebApiCall)
            }
        }
        handler!!.postDelayed(runnable!!, delayWebApiCall)
    }

}
//    STATE_DRAGGING1
//    STATE_SETTLING2
//    STATE_EXPANDED3
//    STATE_COLLAPSED4
//    STATE_HIDDEN5
//    STATE_HALF_EXPANDED6

//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//UI Thread work here// Here 1 represent max location result to returned, by documents it recommended 1 to 5

// If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
////Get address from lat lng
//    private fun addressFromLatLng(location: Location) {
//
//        try {
//            viewModel.setLatLng(location.latitude,location.longitude)
//            viewModel.setLongitude(location.longitude)
//
//        } catch (e: Exception) {
//            LogCalls_Debug.d(TAG, e.localizedMessage)
//        }
//    }


////////////////////////////////////////////////

//    private fun setFriendMapMarker(latLng: LatLng, a: Int, customerImage: String?) {
//        //  markerViewBinding = CustommapiewMarkerBinding.inflate(LayoutInflater.from(context))
//
//        addIconFromURL(latLng, customerImage!!, a)
//
//    }
////////////////////////////////////////////////
// val one = "https://cdn.thewirecutter.com/wp-content/media/2023/03/androidphones-2048px-s23front.jpg"
//val two = "https://m-cdn.phonearena.com/images/hub/290-wide-two_1200/Android4-release-date-supported-devices-and-must-know-features.jpg"

//        if (a !=2) {
//            list.CustomerImage = fg
//        }
//
//        if (a == 4) {
//            list.CustomerImage = one
//        }
//        if (a == 6) {
//            list.CustomerImage = two
//        }


//////////////////////////////////////////////////////////////


//val markerView: View = (requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(R.layout.custommapiew_marker, null)
//        if (marker != null) {
//            marker!!.remove()
//        }

/////////////////////////////////////////////////////////////



