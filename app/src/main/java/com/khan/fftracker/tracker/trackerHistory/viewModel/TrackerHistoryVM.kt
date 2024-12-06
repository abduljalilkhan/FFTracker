package com.khan.fftracker.tracker.trackerHistory.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.khan.fftracker.Autoverse.Location.GMapMarkerUtils
import com.khan.fftracker.Location_FusedAPI.LocationUtils
import com.khan.fftracker.logCalls.LogCalls_Debug
import com.khan.fftracker.Network_Volley.Network_Stuffs
import com.khan.fftracker.Prefrences.Prefs_OperationKotlin
import com.khan.fftracker.autoverse_mvvm.networkApi.ResultApi
import com.khan.fftracker.shoppingBossMVVM.EventLiveData
import com.khan.fftracker.tracker.TrackerConstant
import com.khan.fftracker.tracker.aApiTracker.TrackerRepo
import com.khan.fftracker.tracker.aApiTracker.TrackerRepoImpl
import com.khan.fftracker.tracker.trackerHistory.dataModel.TrackerActivityResponse
import com.khan.fftracker.tracker.trackerHistory.dataModel.TrackerHistoryResponse
import kotlinx.coroutines.launch

class TrackerHistoryVM(private val apiCall: TrackerRepoImpl, private val strCurrentDate: String) :
    ViewModel() {
    //to determine that this fragment called already
    // actually this value is used for when user back from another fragment. So one time call values will be not changed
    val _fstTimeLaunch = MutableLiveData(false)

    //This call has all marker setting
    private lateinit var markerUtils: GMapMarkerUtils

    //This call has all location related setting i.e find distance between two point
    private lateinit var locationUtils: LocationUtils

    //boolean navigation for one time action perform
    var showAlertDialog = MutableLiveData<EventLiveData<Boolean>>()
    var _navigateToTrackerDetail = MutableLiveData<EventLiveData<Boolean>>()

    ///response from web api

    private var _trackeHistoryResponse = MutableLiveData<ResultApi<TrackerHistoryResponse>>()
    private var listMapMarkerData: MutableList<TrackerActivityResponse> = ArrayList()

    private var polylinePoints: MutableList<LatLng>? = ArrayList()

    ///map take few secs to be ready. So call this boolean value let know map operations
    var isMapReady = MutableLiveData(false)


    var bottomSheetState = MutableLiveData(4)
    private var strDate = MutableLiveData("")


    init {

        setFirstLaunch(true)

        initMapUtilClass()
        initLocationUtilClass()

        assignCurrentDate()
        ////call api and get response
        onHistoryApiCall()

    }

    fun setFirstLaunch(value: Boolean) {
        _fstTimeLaunch.value = value
    }

    //observer
    fun getFirstTime(): LiveData<Boolean> {

        return _fstTimeLaunch
    }

    private fun assignCurrentDate() {
        strDate.value = strCurrentDate
    }

    private fun initMapUtilClass() {
        markerUtils = GMapMarkerUtils()
    }

    private fun initLocationUtilClass() {
        locationUtils = LocationUtils()
    }

    //map take few secs to be ready. call this boolean value let know map operations
    fun isMapReady() {
        //setFirstLaunch(false)

        isMapReady.value = true
    }

    //observe for MapReady
    fun getIsMapReady(): LiveData<Boolean> {

        return isMapReady
    }


    //call vehiclestatus api and get lat,lng
    fun onHistoryApiCall() {

        _trackeHistoryResponse.value = ResultApi.loading(null)

        val map = HashMap<String, String>()
        //2024-01-31
        map["ActivityDate"] = strDate.value.toString()
        map["GroupID"] = Network_Stuffs.GROUP_ID
        viewModelScope.launch {
            try {
                LogCalls_Debug.d(LogCalls_Debug.TAG, "onHistoryApiCall")

                _trackeHistoryResponse.value = apiCall.getTrackerHistoryResponse(map)

            } catch (exception: Exception) {
                LogCalls_Debug.d(LogCalls_Debug.TAG, exception.message)
            }
        }
    }

    //observe for  response
    fun getTrackerHistoryResponse(): LiveData<ResultApi<TrackerHistoryResponse>> {
        return _trackeHistoryResponse
    }


    fun manageResponse(response: TrackerHistoryResponse?) {
        //list
        listMapMarkerData.clear()
        listMapMarkerData.addAll(response!!.activities)

        polylinePoints!!.clear()
        setLatLngFromResponse()

    }

    fun clearList() {
        //list clear when api is calling
        listMapMarkerData.clear()
        polylinePoints!!.clear()

    }

    //observe for  response
    fun getListMapData(): MutableList<TrackerActivityResponse> {
        return listMapMarkerData
    }

    private fun setLatLngFromResponse() {
        val allPolylinePoints: MutableList<LatLng> = ArrayList()
        for (a in 0 until listMapMarkerData.size) {

            val latLngCoordinates = listMapMarkerData[a].coordinates

            for (latlng in 0 until latLngCoordinates.size) {
                val lat: Double = latLngCoordinates[latlng].lat.toDouble()
                val lng: Double = latLngCoordinates[latlng].lng.toDouble()
                allPolylinePoints.add(LatLng(lat, lng))

            }
        }
        polylinePoints!!.addAll(locationUtils.filterLocations(allPolylinePoints))
    }

    //  i have list of latlng and i want only add to arraylist  lat lng 10 meter difference android Haversine
    fun getLatLngFromResponse(): List<LatLng> {
        return polylinePoints!!
    }


    fun getInitialLatLng(): LatLng {
        return polylinePoints!![0]

    }

    fun setDate(strDate: String) {
        this.strDate.value = strDate

    }

    fun getDate(): LiveData<String> {
        return strDate
    }

    //observe to show alert dialog
    fun getDateAlertDialogue(): LiveData<EventLiveData<Boolean>> {

        return showAlertDialog

    }

    //show alert dialog
    fun showAlertDialogue() {

        showAlertDialog.value = EventLiveData(true)

    }

    //observe navigate to fragment
    fun getNavigateTrackerDetail(): LiveData<EventLiveData<Boolean>> {
        return _navigateToTrackerDetail
    }

    //navigate to fragment
    fun onNavigateTrackerDetail(modelClass: TrackerActivityResponse) {
        //save model class in prefs
        Prefs_OperationKotlin.setModelPref(TrackerConstant.TRACKER_HISTORY_ITEM_PREFS, modelClass)
        _navigateToTrackerDetail.value = EventLiveData(true)
    }

    class VMFactory(private val repositoryApi: TrackerRepo, private val strCurrentDate: String) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TrackerHistoryVM(TrackerRepoImpl(repositoryApi), strCurrentDate) as T
        }
    }
}