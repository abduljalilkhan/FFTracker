package com.khan.fftracker.tracker.trackerHistory

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.khan.fftracker.DashBoard.Get_Dp
import com.khan.fftracker.logCalls.LogCalls_Debug
import com.khan.fftracker.Network_Volley.IsAdmin
import com.khan.fftracker.R
import com.khan.fftracker.databinding.TrackerHistroyDetailBinding
import com.khan.fftracker.googleMap.GoogleMapUtil
import com.khan.fftracker.login_Stuffs.Login_New
import com.khan.fftracker.tracker.friendsDetail.FriendDetailsTracker
import com.khan.fftracker.tracker.trackerHistory.viewModel.TrackerHistoryDetailVM

class TrackerHistoryDetail : Fragment(), OnMapReadyCallback {

    private val TAG = TrackerHistoryDetail::class.simpleName

    var binding: TrackerHistroyDetailBinding? = null
    var mView: View? = null

    lateinit var viewModel: TrackerHistoryDetailVM

    /// IsAdmin: Class for using check whether customer or admin logged in
    var isAdmin: IsAdmin? = null

    ///Google map
    var mGoogleMap: GoogleMap? = null
    var supportMapFragment: SupportMapFragment? = null
    var marker: Marker? = null
    private var markerPulse: Marker? = null

    ////////////////////////////////////////////////////////


    private lateinit var googleMapUtil: GoogleMapUtil
    private lateinit var dpConverToPx: Get_Dp
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.tracker_histroy_detail, container, false)

        mView = binding!!.root

        setupViewModel()

        /// Initialize class
        initMapUtilClass()
        //Initializing map
        initMap()
        showBottomSheetDialog()

        requireActivity().onBackPressedDispatcher.addCallback(
            requireActivity(),
            onBackPressedCallback
        )
        /////////////////////////////////////////////

        return mView
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupObservers()
    }

    override fun onResume() {
        super.onResume()
        supportMapFragment!!.onResume()

    }

    //Intializing widgtes
    private fun initMap() {
        supportMapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        if (supportMapFragment != null) {
            supportMapFragment!!.getMapAsync(this)
        }
    }


    private fun initMapUtilClass() {
        if (!this::googleMapUtil.isInitialized)
            googleMapUtil = GoogleMapUtil(requireActivity())

        dpConverToPx = Get_Dp(requireActivity())

    }

    private fun setupViewModel() {

        viewModel = ViewModelProvider(this)[TrackerHistoryDetailVM::class.java]
        ///////////////////////////////////////////////////

        binding!!.lifecycleOwner = this
        binding!!.viewModel = viewModel
    }

    private fun setupObservers() {

        viewModel.getIsMapReady().observe(viewLifecycleOwner) {
            LogCalls_Debug.d(LogCalls_Debug.TAG, " getIsMapReady ")
            /////// place marker on map getting current location lat and long coordinates
            if (it) {
                drawPathMap()

            }
        }

    }


    override fun onMapReady(googleMap: GoogleMap) {
        Log.d(Login_New.TAG, "onMapReady autoverse")
        mGoogleMap = googleMap
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL


        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            //googleMap.isMyLocationEnabled = true
        }

        //set bottom and top padding to map to view polyline which is behind bottom sheet dialog
        setPaddingMap()

        viewModel.isMapReady()


    }

    private fun drawPathMap() {

        //addMarker()
        // Position==0, for starting point marker
        // Position==last Pos, For destination point
        val defaultMarkerIcon =
            ContextCompat.getDrawable(requireActivity(), R.drawable.marker_pulse)
        val startIcon = ContextCompat.getDrawable(requireActivity(), R.drawable.start_map)
        val endIcon = ContextCompat.getDrawable(requireActivity(), R.drawable.end_map)
        val polyLineColor = ContextCompat.getColor(requireActivity(), R.color.dodgerblue)
        val circleColor = ContextCompat.getColor(requireActivity(), R.color.circletranseblue)


        googleMapUtil.setDefaultMarker(defaultMarkerIcon, 100, 100)
        googleMapUtil.setPolylineAttributes(polyLineColor, 10f)
        googleMapUtil.setMapCircleColor(circleColor, 50)

        googleMapUtil.setMarkerData(mGoogleMap, markerPulse, viewModel.getLatLngFromResponse())


        if (viewModel.getLatLngFromResponse().size > 1) {

            googleMapUtil.addPolylineMap()

            googleMapUtil.setLatLngBound()
            googleMapUtil.setStartEndMarker(startIcon, endIcon, 100, 150)

            googleMapUtil.removeDefaultMarker()

        } else {
            googleMapUtil.addCircleGoogleMap(viewModel.getInitialLatLng())
            googleMapUtil.moveCameraMap(viewModel.getInitialLatLng())
        }

        //set bottom and top padding to map to view polyline which is behind bottom sheet dialog
        setPaddingMap()


    }

    //set bottom and top padding to map to view polyline which is behind bottom sheet dialog
    private fun setPaddingMap() {
        val dpTop = dpConverToPx.dpToPx(100)
        val dpBottom = dpConverToPx.dpToPx(230)

        mGoogleMap!!.setPadding(0, dpTop, 0, dpBottom)

    }

    private fun showBottomSheetDialog() {

        LogCalls_Debug.d(TAG, "showBottomSheetDialog")
        val bottomSheetLayout = binding!!.includeBottomSheet.layoutBottom

        val sheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)

        sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED



        sheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                LogCalls_Debug.d(TAG, "onStateChanged newState" + newState)
                if (newState == BottomSheetBehavior.STATE_DRAGGING) {
                    sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                }

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                //binding!!.includeBottomSheet.imgShowHide.rotation = slideOffset * 180
            }
        })
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


