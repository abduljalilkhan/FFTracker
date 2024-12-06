package com.khan.fftracker.tracker.trackerDashboard.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.khan.fftracker.DashBoard.Dashboard_Constants
import com.khan.fftracker.logCalls.LogCalls_Debug
import com.khan.fftracker.Network_Volley.Network_Stuffs
import com.khan.fftracker.Prefrences.Prefs_Operation
import com.khan.fftracker.Prefrences.Prefs_OperationKotlin
import com.khan.fftracker.autoverse_mvvm.networkApi.ResultApi
import com.khan.fftracker.autoverse_mvvm.utils.GenericResponse
import com.khan.fftracker.login_Stuffs.Login_Contstant
import com.khan.fftracker.shoppingBossMVVM.EventLiveData
import com.khan.fftracker.tracker.TrackerConstant
import com.khan.fftracker.tracker.TrackerConstant.INVITE_URL
import com.khan.fftracker.tracker.TrackerConstant.TRACKER_BACKGROUND
import com.khan.fftracker.tracker.aApiTracker.TrackerRepo
import com.khan.fftracker.tracker.aApiTracker.TrackerRepoImpl
import com.khan.fftracker.tracker.addFriendContactList.dataModel.InviteURLResponse
import com.khan.fftracker.tracker.trackerDashboard.dataModel.Contract
import com.khan.fftracker.tracker.trackerDashboard.dataModel.TrackerDashFriendResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class TrackerDashboardVM(private val apiCall: TrackerRepoImpl) : ViewModel() {
    //boolean navigation for one time action perform
    private val _navigateToFriendDetail = MutableLiveData<EventLiveData<Boolean>>()
    private val _navigateToTrackerHistory = MutableLiveData<EventLiveData<Boolean>>()

    ///map take few secs to be ready. So call this boolean value let know map operations
    var isMapReady = MutableLiveData<EventLiveData<Boolean>>()
    var contactPermission = MutableLiveData<EventLiveData<Boolean>>()
    var _navigateMenuSetting = MutableLiveData<EventLiveData<Boolean>>()
    var showAlertDialog = MutableLiveData<EventLiveData<Boolean>>()

    val _fstTime = MutableLiveData(false)
    val _locationGetFstTime = MutableLiveData(false)

    ///response from web api
    private val _friendListResponse = MutableLiveData<ResultApi<TrackerDashFriendResponse>>()
    private val _saveFriendLatLngResponse = MutableLiveData<ResultApi<GenericResponse>>()
    private val _inviteLinkResponse = MutableLiveData<ResultApi<InviteURLResponse>>()
    private val _onDeleteResponse = MutableLiveData<ResultApi<GenericResponse>>()


    var listFriendMarker: MutableList<Contract> = ArrayList()

    ///map take few secs to be ready. So call this boolean value let know map operations
    // var isMapReady = MutableLiveData(false)
    var isRecyclerViewShow = MutableLiveData(false)

    //Double hold value

    private var customerlat = MutableLiveData(0.0)
    private var customerlng = MutableLiveData(0.0)
    private var customerLatLng: MutableLiveData<LatLng> =
        MutableLiveData(LatLng(customerlat.value!!, customerlng.value!!))

    private var customerImage = MutableLiveData("http//:")

    private var latLng = MutableLiveData(LatLng(0.0, 0.0))

    var userName = MutableLiveData("")
    var batteryPercentage = MutableLiveData("0")
    var strPrivacySetting = MutableLiveData("3")

    /*used for friend pending array
    to show in alert dialog and for apis*/
    var strPhoneNo = MutableLiveData("")
    var strFriendName = MutableLiveData("")
    var strInviteURl = MutableLiveData("")

    lateinit var contract: Contract
    var listPos = MutableLiveData(0)
    private val userMarkerTag = MutableLiveData(-1)

    var bottomSheetState = MutableLiveData(6)

    private val hashMapMarker = MutableLiveData(HashMap<Int, Marker>())


    val strTitle = "Awaiting Reply"
    val strDesc = "Are you sure you to want delete?"
    val strDoneBtn = "Delete"
    val strOkBtn = "Yes"
    val strNoBtn = "No"

    //list that include dialog detail
    private var alertList = mutableListOf(strTitle, strDesc, strDoneBtn, strOkBtn, strNoBtn)

    init {
        //call api and get response
        //    onFriendListApi()
        //get customer lat,lng and image which is saved in dashboard response
        getCustomerData()
        getUserDataList()
    }

    //get customer lat,lng and image which is saved in dashboard response
    private fun getCustomerData() {
        userName.value = Prefs_Operation.readPrefs(Login_Contstant.CUSTOMER_NAME, "")

        customerImage.value =
            Prefs_OperationKotlin.readString(Dashboard_Constants.CUSTOMER_IMAGE, "http//:")

        val lat: Double =
            Prefs_OperationKotlin.readString(TrackerConstant.TRACKER_LAT, "0.0")!!.toDouble()
        val lng: Double =
            Prefs_OperationKotlin.readString(TrackerConstant.TRACKER_LNG, "0.0")!!.toDouble()

        latLng.value = LatLng(lat, lng)

        setClickedLatLng(LatLng(lat, lng))
        _fstTime.value = true
        _locationGetFstTime.value = true

    }

    private fun getUserDataList() {
        val contract = Contract()
        contract.CustomerFName = userName.value.toString()
        contract.CustomerImage = customerImage.value.toString()
        contract.lat = latLng.value!!.latitude.toString()
        contract.lng = latLng.value!!.longitude.toString()
        contract.Battery = batteryPercentage.value.toString()

        listFriendMarker.add(0, contract)
    }

    fun getCustomerImageMap(): LiveData<String> {
        return customerImage
    }


    //map take few secs to be ready. call this boolean value let know map operations
    fun isMapReady() {
        _fstTime.value = false
        // isMapReady.value = true
        isMapReady.value = EventLiveData(true)
    }

    //observe for MapReady
    fun getIsMapReady(): LiveData<EventLiveData<Boolean>> {
        return isMapReady
    }

    fun setRecyclerViewVisibility() {
        isRecyclerViewShow.value = isRecyclerViewShow.value != true
    }

    /*  ///////////////////get friend list///////////////// */

    //call api and get response
    fun onFriendListApi() {
        _friendListResponse.value = ResultApi.loading(null)

        val map = HashMap<String, String>()
        map["GroupID"] = Network_Stuffs.GROUP_ID

        viewModelScope.launch {
            try {
                LogCalls_Debug.d(LogCalls_Debug.TAG, "onFriendListApi")

                ///call api,get result and wait until response is saved in prefs
                val deferredResult = async {
                    apiCall.getFriendListResponse(map)
                }
                //wait until savePrefs method called
                val apiResponse = deferredResult.await()

                saveInviteURl(apiResponse.data!!.InviteURL)
                //update observer in fragment
                _friendListResponse.value = apiResponse

            } catch (exception: Exception) {
                exception.fillInStackTrace()
                LogCalls_Debug.d(LogCalls_Debug.TAG, exception.message)
            }
        }
    }

    //save invite url and send to friends for joining av tracker
    //Invite URl: You can send invite url to any of your friend in contact list in "AddFriend Contact list" fragment
    private fun saveInviteURl(data: String) {
        Prefs_OperationKotlin.writeString(INVITE_URL, data)
    }

    //observe for AddRemoveFav  response
    //Live data observing in fragment. Purpose not to expose mutable (changeable) data in fragments
    fun getFriendListResponse(): LiveData<ResultApi<TrackerDashFriendResponse>> {
        return _friendListResponse
    }


    /*/////////////Invite friend api/////////////// */
    fun onInviteLinkApi() {
        _inviteLinkResponse.value = ResultApi.loading(null)

        val map = HashMap<String, String>()
        map["GroupID"] = Network_Stuffs.GROUP_ID
        map["Phone"] = strPhoneNo.value.toString()
        map["CustomerFName"] = strFriendName.value.toString()


        viewModelScope.launch {
            try {
                LogCalls_Debug.d(LogCalls_Debug.TAG, "onInviteLinkApi")

                _inviteLinkResponse.value = apiCall.getInviteLinkResponse(map)

            } catch (exception: Exception) {
                LogCalls_Debug.d(LogCalls_Debug.TAG, exception.message)
            }

        }
    }

    //observe for   response
    //Live data observing in fragment. Purpose not to expose mutable (changeable) data in fragments
    fun getInviteLinkResponse(): LiveData<ResultApi<InviteURLResponse>> {
        return _inviteLinkResponse
    }

    fun getInviteUrl(): MutableLiveData<String> {
        return strInviteURl
    }

    fun setInviteUrl(inviteURL: String) {
        strInviteURl.value =
            "I invite you to AV tracker. Click the link below to add me. $inviteURL"
    }

    fun getPhoneNo(): MutableLiveData<String> {
        return strPhoneNo
    }


    /*/////////////Delete friend /////////////// */
    fun onDeleteFriendApi() {
        _onDeleteResponse.value = ResultApi.loading(null)

        val map = HashMap<String, String>()
        map["GroupID"] = Network_Stuffs.GROUP_ID
        map["ID"] = contract.ID


        viewModelScope.launch {
            try {
                LogCalls_Debug.d(LogCalls_Debug.TAG, "onDeleteFriendApi")

                _onDeleteResponse.value = apiCall.getDeleteFriendResponse(map)

            } catch (exception: Exception) {
                LogCalls_Debug.d(LogCalls_Debug.TAG, exception.message)
            }

        }

    }

    //observe for   response
    //Live data observing in fragment. Purpose not to expose mutable (changeable) data in fragments
    fun getDeleteFriendResponse(): LiveData<ResultApi<GenericResponse>> {
        return _onDeleteResponse
    }

    //call vehiclestatus api and get lat,lng
    fun onLocationUpdate(latitude: Double, longitude: Double) {
        setClickedLatLng(LatLng(latitude, longitude))
        LogCalls_Debug.d(LogCalls_Debug.TAG, "onLocationUpdate")
        // _locationUpdateResponse.value = ResultApi.loading(null)

        val map = HashMap<String, String>()
        map["Lat"] = latitude.toString()
        map["Lng"] = longitude.toString()
        map["Battery"] = batteryPercentage.value.toString()

        map["GroupID"] = Network_Stuffs.GROUP_ID
        viewModelScope.launch {
            try {
                LogCalls_Debug.d(LogCalls_Debug.TAG, "onLocationUpdate")

                _saveFriendLatLngResponse.value = apiCall.saveFriendLatLngResponse(map)

            } catch (exception: Exception) {
                LogCalls_Debug.d(LogCalls_Debug.TAG, exception.message)
            }

        }
    }



    //observe
    fun getPrivacySetting(): LiveData<String> {
        return strPrivacySetting
    }

    //observe navigate to fragment
    fun getNavigateFriendDetail(): LiveData<EventLiveData<Boolean>> {
        return _navigateToFriendDetail
    }
    //navigate to fragment
    fun onNavigateFriendDetail(modelClass: Contract) {
        //save model class in prefs
        Prefs_OperationKotlin.setModelPref(TrackerConstant.FRIEND_CONTRACT_ITEM_PREFS, modelClass)
        latLng.value = LatLng(modelClass.lat.toDouble(), modelClass.lng.toDouble())
        strPrivacySetting.value = modelClass.PrivacySetting
        _navigateToFriendDetail.value = EventLiveData(true)
    }


    //observe navigate to fragment
    fun getNavigateTrackerHistory(): LiveData<EventLiveData<Boolean>> {
        return _navigateToTrackerHistory
    }
    fun onNavigateTrackerHistory() {

        _navigateToTrackerHistory.value = EventLiveData(true)
    }

    //observe for Latitude tag
    fun getLatLng(): MutableLiveData<LatLng> {
        return latLng
    }

    fun getContactPermissionGranted(): LiveData<EventLiveData<Boolean>> {
        return contactPermission
    }

    fun setContactPermissionGranted() {
        contactPermission.value = EventLiveData(true)

    }

    fun getNavigateToMenuSetting(): LiveData<EventLiveData<Boolean>> {
        return _navigateMenuSetting
    }

    fun onNavigateToMenuSetting() {
        _navigateMenuSetting.value = EventLiveData(true)

    }

    //show alert dialog to to send invitation to friend
    fun showAlertDialogue(contractClass: Contract, pos: Int) {
        //list position
        listPos.value = pos
        contract = Contract()
        contract = contractClass

        strPhoneNo.value = contract.PhoneHome
        strFriendName.value = contract.CustomerFName
        //update friend phone in list
        alertList[1] =
            contract.CustomerFName + "(" + contract.PhoneHome + ")" + " has not connected to tracker. Would you like resend your invitation?"

        showAlertDialog.value = EventLiveData(true)

    }

    //observe to show alert dialog
    fun getAlertDialogue(): LiveData<EventLiveData<Boolean>> {

        return showAlertDialog

    }

    //list that include dialog detail
    fun getAlertListDialogue(): List<String> {
        return alertList

    }

    fun customerLatLng(): LiveData<LatLng> {
        return customerLatLng
    }

    fun setClickedLatLng(latLang: LatLng) {
        customerlat.value = latLang.latitude
        customerlng.value = latLang.longitude


        LogCalls_Debug.d(
            LogCalls_Debug.TAG,
            "${customerlat.value.toString()} lngDash ${customerlng.value.toString()}"
        )

        customerLatLng.value = LatLng(customerlat.value!!, customerlng.value!!)

        Prefs_Operation.writePrefs(TrackerConstant.TRACKER_LAT, customerlat.value.toString())
        Prefs_Operation.writePrefs(TrackerConstant.TRACKER_LNG, customerlng.value.toString())

    }

    fun getCustomerLat(): LiveData<Double> {
        return customerlat
    }

    //observe for Longitude tag
    fun getCustomerLng(): LiveData<Double> {
        return customerlng
    }

    fun manageResponse(response: TrackerDashFriendResponse?) {
        Prefs_OperationKotlin.writeString(
            TrackerConstant.NEARBY_ALERT,
            response!!.EnableNearbyAlert
        )
        Prefs_OperationKotlin.writeString(
            TrackerConstant.NEARBY_DISTANCE,
            response!!.NearbyDistance
        )

        //list
        listFriendMarker.clear()
        getUserDataList()
        listFriendMarker.addAll(response!!.contracts)
        listFriendMarker.addAll(response!!.pending)
    }


    /* set tracker value set to 1
     1= background location will be set to tracker
    0= default: its means that user is not navigate to tracker screen yet . So bg location will be not set for tracker
    TRACKER_BACKGROUND tag is using in LocationCurrent*/
    fun saveTrackerEnable() {
        Prefs_OperationKotlin.writeString(TRACKER_BACKGROUND, "1")
    }


    fun setMarkerClickPos(position: Int) {

        ///////// Arraylist for selected bank item selection in spinner
        if (position != userMarkerTag.value && position != 0) {
            for (a in listFriendMarker.indices) {
                LogCalls_Debug.d(LogCalls_Debug.TAG, listFriendMarker[position].toString())
                if (position == a) {
                    onNavigateFriendDetail(listFriendMarker[a])
                    return
                }
            }
        }
    }

    fun getUserMarkerTag(): LiveData<Int> {
        return userMarkerTag
    }
    fun removeUserMarker() {
        val marker = hashMapMarker.value!![userMarkerTag.value]
        if (marker != null) {

            marker!!.remove()
            hashMapMarker.value!!.remove(userMarkerTag.value)

        }
    }
    fun defaultUserMarker(): Marker? {
        val marker = hashMapMarker.value!![userMarkerTag.value]
        if (marker != null) {
            return marker
        }
        return null
    }

    fun addUserMarker(a: Int, marker: Marker) {
        hashMapMarker.value!![a] = marker
    }

    fun calculteBatteryPercentage(rawlevel: Int, scale: Int) {
        var level = -1
        if (rawlevel >= 0 && scale > 0) {
            level = rawlevel * 100 / scale
        }
        if (level == -1) level=0
        batteryPercentage.value = level.toString()
    }




    class VMFactory(private val repositoryApi: TrackerRepo) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TrackerDashboardVM(TrackerRepoImpl(repositoryApi)) as T
        }
    }
}