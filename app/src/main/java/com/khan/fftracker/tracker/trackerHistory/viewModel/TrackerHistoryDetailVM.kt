package com.khan.fftracker.tracker.trackerHistory.viewModel

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.khan.fftracker.Autoverse.Location.GMapMarkerUtils
import com.khan.fftracker.Autoverse.Location.PolylinePathDraw
import com.khan.fftracker.Location_FusedAPI.LocationUtils
import com.khan.fftracker.Prefrences.Prefs_OperationKotlin
import com.khan.fftracker.tracker.TrackerConstant
import com.khan.fftracker.tracker.trackerHistory.dataModel.TrackerActivityResponse

class TrackerHistoryDetailVM : ViewModel() {
    private lateinit var markerUtils: GMapMarkerUtils

    //This call has all location related setting i.e find distance between two point
    private lateinit var locationUtils: LocationUtils

    private var historyItemPref = MutableLiveData<TrackerActivityResponse>()

    private var polylinePoints: MutableList<LatLng>? = ArrayList()

    ///map take few secs to be ready. So call this boolean value let know map operations
    var isMapReady = MutableLiveData(false)

    //if polylinePoints size > 1 then true else false
    //check if list has only one value then don't show multiple views
    private var isListNotZero = MutableLiveData(false)

    //set initial lat lng
    private var initialLat = MutableLiveData(0.0)
    private var initialLng = MutableLiveData(0.0)

    //set final lat lng
    private var endLat = MutableLiveData(0.0)
    private var endLng = MutableLiveData(0.0)

    private var startEndDate = MutableLiveData("")

    init {
        initMapUtilClass()
        initLocationUtilClass()
        ////call api and get response
        getTrackerHistoryPref()

    }

    private fun initMapUtilClass() {
        markerUtils = GMapMarkerUtils()
    }

    private fun initLocationUtilClass() {
        locationUtils = LocationUtils()
    }

    private fun getTrackerHistoryPref() {
        //get model class form prefs
        historyItemPref.value = Prefs_OperationKotlin.getModelPref(
            TrackerConstant.TRACKER_HISTORY_ITEM_PREFS,
            TrackerActivityResponse::class.java
        )

        //set start and end date concatenate if polylinePoints<1
        setStartEndDate()

        //get lat lng array
        val latLngCoordinates = historyItemPref.value!!.coordinates


        val allPolylinePoints: MutableList<LatLng> = ArrayList()

        for (latlng in 0 until latLngCoordinates.size) {
            val lat: Double = latLngCoordinates[latlng].lat.toDouble()
            val lng: Double = latLngCoordinates[latlng].lng.toDouble()
            allPolylinePoints.add(LatLng(lat, lng))

        }
        //add item to arrayList containing locations 10 meters apart from each other
        polylinePoints!!.addAll(locationUtils.filterLocations(allPolylinePoints))

        if (polylinePoints!!.size>1){
            isListNotZero.value=true
        }
        setInitialFinalLatLng()
    }

    private fun setStartEndDate() {

        startEndDate.value =
            historyItemPref.value!!.startdatetime + "\n\n~" + historyItemPref.value!!.enddatetime

    }

    private fun setInitialFinalLatLng() {
        //set initial lat lng
        initialLat.value = polylinePoints!![0].latitude
        initialLng.value = polylinePoints!![0].longitude

        //set final lat lng
        endLat.value = polylinePoints!![polylinePoints!!.size - 1].latitude
        endLng.value = polylinePoints!![polylinePoints!!.size - 1].longitude
    }


    fun getTrackerHistoryResponse(): TrackerActivityResponse? {
        return historyItemPref.value
    }

    fun getLatLngFromResponse(): List<LatLng> {
        return polylinePoints!!
    }

    fun getInitialLatLng(): LatLng {
        return polylinePoints!![0]

    }


    //map take few secs to be ready. call this boolean value let know map operations
    fun isMapReady() {
        isMapReady.value = true
    }

    //observe for MapReady
    fun getIsMapReady(): MutableLiveData<Boolean> {
        return isMapReady
    }

    fun getListNotZero(): LiveData<Boolean> {
        return isListNotZero
    }

    fun getInitialLat(): LiveData<Double> {
        return initialLat
    }

    //observe for Longitude tag
    fun getInitialLng(): LiveData<Double> {
        return initialLng
    }

    fun getEndLat(): LiveData<Double> {
        return endLat
    }

    //observe for Longitude tag
    fun getEndLng(): LiveData<Double> {
        return endLng
    }

    fun getStartEndTime(): LiveData<String> {
        return startEndDate

    }

}