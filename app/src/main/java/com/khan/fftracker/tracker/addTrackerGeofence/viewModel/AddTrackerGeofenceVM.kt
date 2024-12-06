package com.khan.fftracker.tracker.addTrackerGeofence.viewModel

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.khan.fftracker.logCalls.LogCalls_Debug
import com.khan.fftracker.logCalls.LogCalls_Debug.TAG
import com.khan.fftracker.mvvmUtils.FormValidationError
import com.khan.fftracker.Network_Volley.Network_Stuffs
import com.khan.fftracker.Prefrences.Prefs_OperationKotlin
import com.khan.fftracker.mvvmData.networkApi.ResultApi

import com.khan.fftracker.shoppingBossMVVM.EventLiveData
import com.khan.fftracker.tracker.TrackerConstant
import com.khan.fftracker.tracker.friendPlaces.dataModel.Geofencelists
import com.khan.fftracker.tracker.aApiTracker.TrackerRepo
import com.khan.fftracker.tracker.aApiTracker.TrackerRepoImpl
import com.khan.fftracker.tracker.trackerDashboard.dataModel.Contract
import com.khan.fftracker.utils.GenericResponse
import kotlinx.coroutines.launch

class AddTrackerGeofenceVM(private val apiCall: TrackerRepoImpl) : ViewModel() {

    var isApiCall: String = ""
    val formErrorsList = ObservableArrayList<FormValidationError>()

    ///error showing text for editext
    val etEmptyError = MutableLiveData("")

    var placeName = MutableLiveData("")

    private var strArrEt: Array<MutableLiveData<String>> = arrayOf(placeName)

    ///dataBinding views


    //boolean navigation for one time action perform
    var _navigateCancel = MutableLiveData<EventLiveData<Boolean>>()
    private val _radiusMap = MutableLiveData<EventLiveData<Boolean>>()

    private var mapZoom = MutableLiveData(19)
    ///response from web api

    private val _addUpdate_deleteGeofenceResponse = MutableLiveData<ResultApi<GenericResponse>>()

    ///map take few secs to be ready. So call this boolean value let know map operations
    var isMapReady = MutableLiveData(false)

    private var isEdit = MutableLiveData(false)
    var isDeleteBtnShow = MutableLiveData(false)

    private var friendContractItemPref = MutableLiveData<Contract>()

    private var geofenceItemPref = MutableLiveData<Geofencelists>()

    //Double hold value
    private var lat = MutableLiveData(0.0)
    private var lng = MutableLiveData(0.0)
    private var latLng = MutableLiveData(LatLng(lat.value!!, lng.value!!))

    private val minMapRadius = MutableLiveData(25)
    private val maxMapRadius = MutableLiveData(2000)
    var radiusMeter = MutableLiveData(25)
    var sBarRadiusProgress = MutableLiveData(25)

    init {
        getFriendContractPref()
    }

    fun setBundleData(strEdit: String?) {
        LogCalls_Debug.d(TAG, "setBundleData $strEdit")

        lat.value = friendContractItemPref.value!!.lat.toDouble()
        lng.value = friendContractItemPref.value!!.lng.toDouble()


        //strIEdit == "1" editing the geofence data
        //strIEdit == 0 new geofence will be added
        if (strEdit == "1") {
            getGeofenceItem()
            //if lat lng ==0  geofence is not exist
            if (checkLatLngEmpty()) {
                lat.value = geofenceItemPref.value!!.lat.toDouble()
                lng.value = geofenceItemPref.value!!.lng.toDouble()
            }
            placeName.value = geofenceItemPref.value!!.GeoTitle

            radiusMeter.value = 0

            onSeekBarRadiusProgress(geofenceItemPref.value!!.Radious.toInt())

            setDeleteBtnVisibility()

            isEdit.value = true
        } else {
            isEdit.value = false
            isDeleteBtnShow.value = true

        }

        latLng.value = LatLng(lat.value!!, lng.value!!)
    }

    private fun checkLatLngEmpty(): Boolean {
        if (geofenceItemPref.value!!.lat != "0") {
            return true
        }
        return false

    }

    private fun getGeofenceItem() {
        geofenceItemPref.value = Prefs_OperationKotlin.getModelPref(TrackerConstant.GEOFENCE_ITEM_PREFS, Geofencelists::class.java)
    }

    //show delete button if geofence is edited
    //hide delete button if new geofence is added
    private fun setDeleteBtnVisibility() {
        if (!checkLatLngEmpty()) {
           isDeleteBtnShow.value = true
        }
    }

    //observe for bundle edit
    fun getIsEdit(): MutableLiveData<Boolean> {
        return isEdit
    }

    //map take few secs to be ready. call this boolean value let know map operations
    fun isMapReady() {
        isMapReady.value = true
    }

    //observe for MapReady
    fun getIsMapReady(): MutableLiveData<Boolean> {
        return isMapReady
    }

    private fun getFriendContractPref() {
        friendContractItemPref.value = Prefs_OperationKotlin.getModelPref(TrackerConstant.FRIEND_CONTRACT_ITEM_PREFS, Contract::class.java)


//        lat.value= friendContractItemPref.value!!.lat.toDouble()
//        lng.value= friendContractItemPref.value!!.lng.toDouble()
//
//        latLng.value=LatLng(lat.value!!,lng.value!!)

        LogCalls_Debug.d(TAG, "conract id" + friendContractItemPref.value!!.ContractID)
    }

    //call vehiclestatus api and get lat,lng
    fun onSaveGeofenceApi() {
        if (isFormValid()) {
            LogCalls_Debug.d(TAG, "onLocationUpdate")
            _addUpdate_deleteGeofenceResponse.value = ResultApi.loading(null)
            isApiCall = "yes"

            val map = HashMap<String, String>()
            map["FriendContractID"] = friendContractItemPref.value!!.ContractID
            map["FriendIsGuest"] = friendContractItemPref.value!!.IsGuest

            map["Name"] = placeName.value.toString()
            map["lat"] = lat.value.toString()
            map["lng"] = lng.value.toString()
            map["Radiou"] = sBarRadiusProgress.value.toString()
            map["NotificationOn"] = "0"

            map["GroupID"] = Network_Stuffs.GROUP_ID


            //Two APi calling for theft.Check theft is enable or disable
            var strFunction = "v/" + Network_Stuffs.URL_VERSION + "/familytracker/addgeofence"

            if (isEdit.value!!) {
                strFunction = "v/" + Network_Stuffs.URL_VERSION + "/familytracker/updategeofence"
                map["GeofenctID"] = geofenceItemPref.value!!.GeofenctID
            }

            viewModelScope.launch {
                try {
                    LogCalls_Debug.d(LogCalls_Debug.TAG, "onSaveGeofenceApi")
                    _addUpdate_deleteGeofenceResponse.value = apiCall.getAnyResponse(strFunction, map)
                    // _addGeofenceResponse.value = apiCall.getAddGeofenceResponse(map)

                } catch (exception: Exception) {
                    LogCalls_Debug.d(LogCalls_Debug.TAG, exception.message)
                }

            }
        }
    }

    //observe for VehicleStatus() response
    fun getAddGeofenceResponse(): LiveData<ResultApi<GenericResponse>> {
        return _addUpdate_deleteGeofenceResponse
    }


    fun onDeleteGeofenceApi() {

        LogCalls_Debug.d(LogCalls_Debug.TAG, "onDeleteGeofenceApi")
        _addUpdate_deleteGeofenceResponse.value = ResultApi.loading(null)
        isApiCall = "yes"

        val map = HashMap<String, String>()
        map["FriendContractID"] = friendContractItemPref.value!!.ContractID
        map["FriendIsGuest"] = friendContractItemPref.value!!.IsGuest
        map["GeofenctID"] = geofenceItemPref.value!!.GeofenctID

        map["GroupID"] = Network_Stuffs.GROUP_ID

        viewModelScope.launch {
            try {
                LogCalls_Debug.d(LogCalls_Debug.TAG, "onDeleteGeofenceApi")

                _addUpdate_deleteGeofenceResponse.value = apiCall.getDeleteGeofenceResponse(map)

            } catch (exception: Exception) {
                LogCalls_Debug.d(LogCalls_Debug.TAG, exception.message)
            }

        }

    }


    fun getNavigateCancel(): LiveData<EventLiveData<Boolean>> {
        return _navigateCancel
    }

    fun onCancel() {
        _navigateCancel.value = EventLiveData(true)

    }

    //observe for Longitude tag
    fun getLongitude(): LiveData<Double> {
        return lng
    }

    //observe for Latitude tag
    fun getLatitude(): LiveData<Double> {
        return lat
    }

    //observe for Latitude tag
    fun getLatLng(): MutableLiveData<LatLng> {
        return latLng
    }


    //observe for Longitude tag
    fun getMinMapRadius(): LiveData<Int> {
        return minMapRadius
    }

    //observe for Latitude tag
    fun getMaxMapRadius(): LiveData<Int> {
        return maxMapRadius
    }

    fun onSeekBarRadiusProgress(progress: Int) {
        sBarRadiusProgress.value = progress
    }

    fun getSeekBarRadiusProgress(): LiveData<Int> {
        return sBarRadiusProgress
    }

    fun setRadiusMeter(progress: Int) {
        radiusMeter.value = progress
    }

    fun setClickedLatLng(latLang: LatLng) {
        lat.value = latLang.latitude
        lng.value = latLang.longitude

        latLng.value = latLang

    }

    fun onRadiusMap() {
        val sbProgress = getMinMapRadius().value!!

        LogCalls_Debug.d(Login_New.TAG, "$sbProgress progress initial")

        val finalProgress = getMinMaxProgress(sBarRadiusProgress.value!!, sbProgress)
        // if (progress>100&&progress<1950){
        if (finalProgress) {

            LogCalls_Debug.d(Login_New.TAG, (sbProgress).toString() + " exist ")

            // onSeekBarRadiusProgress(sbProgress + radius)

            zoomMapForRadius(sBarRadiusProgress.value!!)

            setRadiusMeter(sBarRadiusProgress.value!!)

            _radiusMap.value = EventLiveData(true)

        }
        LogCalls_Debug.d(Login_New.TAG, "$sbProgress progress later $sBarRadiusProgress.value!!")
    }

    private fun getMinMaxProgress(radius: Int, sbProgress: Int): Boolean {
        var progress = sbProgress
        progress += radius

        return progress in minMapRadius.value!!..maxMapRadius.value!!
    }

    //observe navigate to AutoVerseGeofenceRadius fragment
    fun getRadiusMap(): LiveData<EventLiveData<Boolean>> {
        return _radiusMap
    }

    fun zoomMapForRadius(progress: Int) {
        when {
            progress < 50 -> {
                mapZoom.value = 19
            }

            progress in 51..111 -> {
                mapZoom.value = 18
            }

            progress in 113..199 -> {
                mapZoom.value = 17
            }

            progress in 201..359 -> {
                mapZoom.value = 16
            }

            progress in 361..759 -> {
                mapZoom.value = 15
            }

            progress in 761..1499 -> {
                mapZoom.value = 14
            }

            progress in 1501..1999 -> {
                mapZoom.value = 13
            }
        }
    }

    //observe for MapReady
    fun getZoomMapForRadius(): LiveData<Int> {
        return mapZoom
    }

    private fun isFormValid(): Boolean {

        formErrorsList.clear()
        if (!EditTextValidationRules.checkEmptyString(strArrEt, etEmptyError)) {

            formErrorsList.add(FormValidationError.ET_EMPTY)
        }

        return formErrorsList.isEmpty()
    }

    class VMFactory(private val repositoryApi: TrackerRepo) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AddTrackerGeofenceVM(TrackerRepoImpl(repositoryApi)) as T
        }
    }
}