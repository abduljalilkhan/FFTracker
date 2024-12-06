package com.khan.fftracker.tracker.friendsDetail.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.map
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.khan.fftracker.DashBoard.Dashboard_Constants
import com.khan.fftracker.logCalls.LogCalls_Debug
import com.khan.fftracker.logCalls.LogCalls_Debug.TAG
import com.khan.fftracker.Network_Volley.Network_Stuffs
import com.khan.fftracker.Prefrences.Prefs_OperationKotlin
import com.khan.fftracker.autoverse_mvvm.networkApi.ResultApi
import com.khan.fftracker.shoppingBossMVVM.EventLiveData
import com.khan.fftracker.tracker.TrackerConstant
import com.khan.fftracker.tracker.friendsDetail.dataModel.FriendLatLngResponse
import com.khan.fftracker.tracker.aApiTracker.TrackerRepo
import com.khan.fftracker.tracker.aApiTracker.TrackerRepoImpl
import com.khan.fftracker.tracker.trackerDashboard.dataModel.Contract
import kotlinx.coroutines.launch

class FriendDetailTrackerVM(private val apiCall: TrackerRepoImpl) : ViewModel() {

    private lateinit var sd: Contract

    ///dataBinding views
    private var _addressFromLatlng = MutableLiveData("")

    //boolean navigation for one time action perform
    var _navigateFriendPlace = MutableLiveData<EventLiveData<Boolean>>()
    var _navigateMapDirection = MutableLiveData<EventLiveData<Boolean>>()
    var _navigatePrivacyDialogue = MutableLiveData<EventLiveData<Boolean>>()
    var _navigateChat = MutableLiveData<EventLiveData<Boolean>>()


    ///response from web api

    private var _locationUpdateResponse = MutableLiveData<ResultApi<FriendLatLngResponse?>>()
    private var _contractResponse = MutableLiveData<ResultApi<Contract?>>()

    ///map take few secs to be ready. So call this boolean value let know map operations
    var isMapReady = MutableLiveData(false)

    private var friendContractItemPref = MutableLiveData<Contract>()
    private var friendTrackerName = MutableLiveData("")
    private var batteryPercentage = MutableLiveData("")

    private var locationUpdateItemPref: MutableLiveData<FriendLatLngResponse>? = null

    //Double hold value
    private var lat = MutableLiveData(0.0)
    private var lng = MutableLiveData(0.0)

    private var myLat = MutableLiveData(0.0)
    private var myLng = MutableLiveData(0.0)
    private var myLatLng: MutableLiveData<LatLng> = MutableLiveData(LatLng(myLat.value!!, myLng.value!!))
    private var myImage = MutableLiveData("http//:")

    var strPrivacySetting = MutableLiveData("1")
    var text = MutableLiveData<String>()

    private var isDashboardNavigate = MutableLiveData(false)

    init {
        text.value = "dsakldj"
        //get customer lat,lng and image which is saved in dashboard response
        getCustomerData()

        getFriendContractPref()
        ////call api and get response
        onLocationUpdate()

        LogCalls_Debug.d(TAG, Prefs_OperationKotlin.readString("invite", "no invite ul"))

    }

    private fun getFriendContractPref() {
        friendContractItemPref.value = Prefs_OperationKotlin.getModelPref(TrackerConstant.FRIEND_CONTRACT_ITEM_PREFS, Contract::class.java)

        friendTrackerName.value = friendContractItemPref.value!!.CustomerFName + " " + friendContractItemPref.value!!.CustomerLName

        lat.value = friendContractItemPref.value!!.lat.toDouble()
        lng.value = friendContractItemPref.value!!.lng.toDouble()

        strPrivacySetting.value = friendContractItemPref.value!!.PrivacySetting

    }

    //get customer lat,lng and image which is saved in dashboard response
    private fun getCustomerData() {
        myImage.value = Prefs_OperationKotlin.readString(Dashboard_Constants.CUSTOMER_IMAGE, "http//:")

        myLat.value = Prefs_OperationKotlin.readString(TrackerConstant.TRACKER_LAT, "0.0")!!.toDouble()
        myLng.value = Prefs_OperationKotlin.readString(TrackerConstant.TRACKER_LNG, "0.0")!!.toDouble()

        myLatLng.value = LatLng(myLat.value!!, myLng.value!!)

    }


    fun getCustomerImageMap(): LiveData<String> {
        return myImage
    }

    //map take few secs to be ready. call this boolean value let know map operations
    fun isMapReady() {
        isMapReady.value = true

        /*google map is null if call this function in init block
        lets google map sync and then call this function to create marker*/
        getResponseFromPrefs()

    }

    /*google map is null if call this function in init block
            lets google map sync and then call this function to create marker*/
    private fun getResponseFromPrefs() {
        val contracts=friendContractItemPref.value

        val friendListreeposne= FriendLatLngResponse()
        friendListreeposne.contracts=contracts

        val resultApiResponse = ResultApi.success(friendListreeposne)

        _locationUpdateResponse.value=resultApiResponse

    }

    //observe for MapReady
    fun getIsMapReady(): MutableLiveData<Boolean> {
        return isMapReady
    }
    //observe for bundle edit
    fun getIsDashboardNavigate(): MutableLiveData<Boolean> {
        return isDashboardNavigate
    }
    fun setBundleData(string: String?) {
        isDashboardNavigate.value=true
    }

    fun getFriendListResponse(): LiveData<Contract> {
        return friendContractItemPref
    }


    //call vehiclestatus api and get lat,lng
    fun onLocationUpdate() {

        LogCalls_Debug.d(LogCalls_Debug.TAG, "onLocationUpdate")
        val map = HashMap<String, String>()
        map["FriendContractID"] = friendContractItemPref.value!!.ContractID
        map["FriendIsGuest"] = friendContractItemPref.value!!.IsGuest

        map["GroupID"] = Network_Stuffs.GROUP_ID
        viewModelScope.launch {
            try {
                LogCalls_Debug.d(LogCalls_Debug.TAG, "getCheckSerialNo")

                _locationUpdateResponse.value = apiCall.getFriendLatLngResponse(map)

            } catch (exception: Exception) {
                LogCalls_Debug.d(LogCalls_Debug.TAG, exception.message)
            }

        }
    }

    //observe for VehicleStatus() response
    fun getLocationUpdateResponse(): MutableLiveData<ResultApi<FriendLatLngResponse?>> {
        return _locationUpdateResponse
    }

    fun getNavigateFriendPlace(): LiveData<EventLiveData<Boolean>> {
        return _navigateFriendPlace
    }

    fun onNavigateFriendPlace() {
        _navigateFriendPlace.value = EventLiveData(true)

    }
    fun getNavigateChat(): LiveData<EventLiveData<Boolean>> {
        return _navigateChat
    }

    fun onNavigateChat() {
        _navigateChat.value = EventLiveData(true)

    }
    fun getNavigateMapDirection(): LiveData<EventLiveData<Boolean>> {
        return _navigateMapDirection
    }
    fun getNavigatePrivacyDialogue(): LiveData<EventLiveData<Boolean>> {
        return _navigatePrivacyDialogue
    }
    fun onNavigateMapDirection(){
        if (_locationUpdateResponse.value!!.data!!.contracts!!.PrivacySetting.equals("1")){
            _navigatePrivacyDialogue.value = EventLiveData(true)
        }
        else {
            _navigateMapDirection.value = EventLiveData(true)

        }
    }
    fun customerLatLng(): LiveData<LatLng> {
        return myLatLng
    }

    //observe for Longitude tag
    fun getLongitude(): LiveData<Double> {
        return lng
    }

    //observe for Latitude tag
    fun getLatitude(): LiveData<Double> {
        return lat
    }

    fun setClickedLatLng(latLang: LatLng) {
        lat.value = latLang.latitude
        lng.value = latLang.longitude


    }

    fun getAddress(): LiveData<String> {
        return _addressFromLatlng
    }

    fun setAddress(s: String?) {
        _addressFromLatlng.value = s
    }

    fun getFriendName(): LiveData<String> {
        return friendTrackerName
    }

//    fun getBatteryPercentage(): LiveData<String> {
//        return friendBatteryPercentage.value
//    }

    //get strCheckoutToken  to send in processorder api via transform live data
    var friendBatteryPercentage = _locationUpdateResponse.map {

        if (it.data != null) {
            it.data.contracts!!.Battery!!.toInt()
//            lat.value=it.data.details.Lat.toDouble()
//            lng.value=it.data.details.Lng.toDouble()
        }
    }


    private var currentName: MutableLiveData<String>? = null

    fun getCurrentName(): MutableLiveData<String>? {
        if (currentName == null) {
            currentName = MutableLiveData()
        }
        return currentName
    }


    class VMFactory(private val repositoryApi: TrackerRepo) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FriendDetailTrackerVM(TrackerRepoImpl(repositoryApi)) as T
        }
    }
}