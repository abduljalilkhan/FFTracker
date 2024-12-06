package com.khan.fftracker.tracker.trackerHistory

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.Marker
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.khan.fftracker.AdminMyPCP.Admin_Chat.Convert_Date
import com.khan.fftracker.Ancillary_Coverages.DateDialogue
import com.khan.fftracker.DashBoard.Get_Dp
import com.khan.fftracker.Item_Interface.CommonStuffInterface
import com.khan.fftracker.logCalls.LogCalls_Debug
import com.khan.fftracker.Navigation_Drawer.Drawer
import com.khan.fftracker.Network_Volley.IsAdmin
import com.khan.fftracker.R
import com.khan.fftracker.RecylerViewClicked.RecyclerViewItemListener
import com.khan.fftracker.autoverse_mvvm.Network_Stuff.ApiRetrofit
import com.khan.fftracker.autoverse_mvvm.networkApi.ResultApi
import com.khan.fftracker.databinding.TrackerHistroyBinding
import com.khan.fftracker.googleMap.GoogleMapUtil
import com.khan.fftracker.login_Stuffs.Login_New
import com.khan.fftracker.tracker.aApiTracker.TrackerApi
import com.khan.fftracker.tracker.aApiTracker.TrackerRepo
import com.khan.fftracker.tracker.trackerHistory.adaptor.TrackerHistoryAdaptor
import com.khan.fftracker.tracker.trackerHistory.dataModel.TrackerActivityResponse
import com.khan.fftracker.tracker.trackerHistory.viewModel.TrackerHistoryVM


class TrackerHistory : Fragment(), OnMapReadyCallback, RecyclerViewItemListener,
    CommonStuffInterface {

    private val TAG = TrackerHistory::class.simpleName

    var handler: Handler? = null
    var runnable: Runnable? = null

    private var adaptor: TrackerHistoryAdaptor? = null

    var binding: TrackerHistroyBinding? = null
    var mView: View? = null

    lateinit var viewModel: TrackerHistoryVM

    /// IsAdmin: Class for using check whether customer or admin logged in
    var isAdmin: IsAdmin? = null

    ///Google map
    var mGoogleMap: GoogleMap? = null
    var supportMapFragment: SupportMapFragment? = null
    var marker: Marker? = null
    var markerPulse: Marker? = null

    private var mapZoom = 16.0f

    ////////////////////////////////////////////////////////


    //    Date dialogue
    private lateinit var dateDialogue: DateDialogue


    private lateinit var googleMapUtil: GoogleMapUtil
    private lateinit var dpConverToPx: Get_Dp
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.tracker_histroy, container, false)

        mView = binding!!.root

        ///init date dialog
        initDateDialog()

        setupViewModel()
        /// Initialize class
        initializeAdminClass()
        /// Initialize class
        initMapUtilClass()

        //Initializing map
        initMap()

        //initialization recyclerview and set layout manager
        initRecyclerView()
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

    //Initializing widgets
    private fun initMap() {
        supportMapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        if (supportMapFragment != null) {
            supportMapFragment!!.getMapAsync(this)
        }
    }

    private fun initMapUtilClass() {
        if (!this::googleMapUtil.isInitialized){
            googleMapUtil = GoogleMapUtil(requireActivity())
}
        //Get_Dp:: convert dp to px
        if (!this::dpConverToPx.isInitialized) {
            dpConverToPx = Get_Dp(requireActivity())
        }
    }

    ///init date dialog
    private fun initDateDialog() {
        if (!this::dateDialogue.isInitialized) {
            dateDialogue = DateDialogue(requireActivity())
        }
    }


    //initialization recyclerview and set layout manager
    private fun initRecyclerView() {
        LogCalls_Debug.d(TAG, "initRecyclerView")

        adaptor = TrackerHistoryAdaptor(requireActivity(), viewModel.getListMapData(), this)

        binding!!.includeBottomSheet.rv.layoutManager =
            LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)

        binding!!.includeBottomSheet.rv.adapter = adaptor

    }


    private fun setupViewModel() {

        ///get current date and send to api
        val strDate =
            "${dateDialogue.currentMonth}/${dateDialogue.currentDay}/${dateDialogue.currentYear}"

        val apiInterface = ApiRetrofit.getRetrofitInstance().create(TrackerApi::class.java)
        // View Model with factory
        val factory = TrackerHistoryVM.VMFactory(TrackerRepo(apiInterface), strDate)

        viewModel = ViewModelProvider(this, factory)[TrackerHistoryVM::class.java]
        ///////////////////////////////////////////////////


        binding!!.lifecycleOwner = this
        binding!!.viewModel = viewModel
    }

    private fun setupObservers() {

        viewModel.getDateAlertDialogue().observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { // Only proceed if the event has never been handled

                dateDialogue.showDate()
            }
        }

        ///observer: navigate to  ChooseAddress fragment
        viewModel.getNavigateTrackerDetail().observe(viewLifecycleOwner) {
            it.getContentIfNotHandled()?.let { // Only proceed if the event has never been handled
                viewModel.setFirstLaunch(false)
                (activity as Drawer?)!!.getFragment(TrackerHistoryDetail(), -1)
            }
            LogCalls_Debug.d(TAG, " TrackerHistoryDetail ")
        }

        ///observer: navigate to  dashboard fragment
        viewModel.getIsMapReady().observe(viewLifecycleOwner) {
            LogCalls_Debug.d(LogCalls_Debug.TAG, " getIsMapReady ")
            /////// place marker on map getting current location lat and long coordinates
            if (it) {
                if (viewModel.getFirstTime().value == true) {
                    //initDateDialog()
                    dateDialogue.datePickerDialog(this)
                }
            }
        }

        //Observe data changes when api called
        viewModel.getTrackerHistoryResponse().observe(viewLifecycleOwner) {
            LogCalls_Debug.d(TAG, "res")
            when (it.statusApi) {
                ResultApi.StatusApi.SUCCESS -> {
                    hideProgressBar()
                    LogCalls_Debug.d(TAG, "SUCCESS")
                    if (viewModel.getFirstTime().value == true && it.data!!.success != 0) {
                        //if (it.data!!.success!=0){
                        viewModel.manageResponse(it.data)
                        setRecyclerView()
                        drawPathMap()
                        //addMarker()
                        viewModel.setFirstLaunch(false)
                    }
                }

                ResultApi.StatusApi.ERROR -> {
                    Toast.makeText(activity, it.msg, Toast.LENGTH_SHORT).show()
                    LogCalls_Debug.d(LogCalls_Debug.TAG, "ERROR")
                    hideProgressBar()
                }

                ResultApi.StatusApi.LOADING -> {
                    //  Keyboard_Close(activity).keyboard_Close_Down()
                    viewModel.setFirstLaunch(true)
                    showProgressBar()
                    viewModel.clearList()
                    setRecyclerView()
                    clearMap()

                }

                ResultApi.StatusApi.FAILURE -> {
                    Toast.makeText(activity, it.msg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun clearMap() {
        if (mGoogleMap != null) {
            mGoogleMap!!.clear()
        }
    }

    private fun setRecyclerView() {

        adaptor!!.notifyDataSetChanged()

        LogCalls_Debug.d(TAG, "adaptor recylerview " + adaptor!!.itemCount)
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
        if (viewModel.getFirstTime().value == true) {
            viewModel.isMapReady()
        }
        //set bottom and top padding to map to view polyline which is behind bottom sheet dialog
        setPaddingMap()

    }

    //set bottom and top padding to map to view polyline which is behind bottom sheet dialog
    private fun setPaddingMap() {
        val dpTop = dpConverToPx.dpToPx(100)
        val dpBottom = dpConverToPx.dpToPx(200)

        mGoogleMap!!.setPadding(0, dpTop, 0, dpBottom)

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
        googleMapUtil.setAnimationDuration(5000)

        googleMapUtil.setMarkerData(mGoogleMap, markerPulse, viewModel.getLatLngFromResponse())


        if (viewModel.getLatLngFromResponse().size > 1) {

            googleMapUtil.addPolylineMap()

            googleMapUtil.setLatLngBound()
            googleMapUtil.setStartEndMarker(startIcon, endIcon, 100, 150)

            googleMapUtil.animPolylineWithMarker()


        } else {
            googleMapUtil.addCircleGoogleMap(viewModel.getInitialLatLng())
            googleMapUtil.moveCameraMap(viewModel.getInitialLatLng())
        }

        //set bottom and top padding to map to view polyline which is behind bottom sheet dialog
        setPaddingMap()

    }


    override fun onDestroyView() {
        super.onDestroyView()

        try {
            handler!!.removeCallbacks(runnable!!)
            googleMapUtil.removeDefaultMarker()
            LogCalls_Debug.d(TAG, "ondestroy trackerhistorytracker")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showBottomSheetDialog() {

        LogCalls_Debug.d(TAG, "showBottomSheetDialog")
        val bottomSheetLayout = binding!!.includeBottomSheet.layoutBottom

        val sheetBehavior = BottomSheetBehavior.from(bottomSheetLayout)

        sheetBehavior.isFitToContents = false
        sheetBehavior.halfExpandedRatio = 0.5f


        //sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        sheetBehavior.state = viewModel.bottomSheetState.value!!

        sheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                LogCalls_Debug.d(TAG, "onStateChanged newState" + newState)
                viewModel.bottomSheetState.value = newState

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                //binding!!.includeBottomSheet.imgShowHide.rotation = slideOffset * 180
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

    override fun onItemClick(get: HashMap<String, Any>, pos: Int) {}
    override fun onItemClickObject(id: Int, any: Any, pos: Int) {

        if (any is TrackerActivityResponse) {

            viewModel.onNavigateTrackerDetail(any)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun commonStuffListener(s: String?) {

        viewModel.setDate(s!!)
        viewModel.onHistoryApiCall()
        val datefr = Convert_Date(activity).parseFormatConvert(s!!, "MM/dd/yyyy", "EEE, MMM dd")
        LogCalls_Debug.d(TAG, datefr)

    }

    ////////////// When on Back button pressed fragment navigate to prevoius fragment
    private val onBackPressedCallback: OnBackPressedCallback =
        object : OnBackPressedCallback(true /* Enabled by default */) {
            override fun handleOnBackPressed() {

                // Handle the back button event
                LogCalls_Debug.d(
                    LogCalls_Debug.TAG + TrackerHistory::class.java.name,
                    "handleOnBackPressed false"
                )

                closeFragment()

            }
        }

    private fun closeFragment() {

        requireActivity().supportFragmentManager.popBackStack()

    }


}


