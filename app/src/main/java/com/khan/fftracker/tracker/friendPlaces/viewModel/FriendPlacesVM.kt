package com.khan.fftracker.tracker.friendPlaces.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.khan.fftracker.logCalls.LogCalls_Debug
import com.khan.fftracker.Network_Volley.Network_Stuffs
import com.khan.fftracker.Prefrences.Prefs_Operation
import com.khan.fftracker.Prefrences.Prefs_OperationKotlin
import com.khan.fftracker.autoverse_mvvm.networkApi.ResultApi
import com.khan.fftracker.autoverse_mvvm.utils.GenericResponse
import com.khan.fftracker.shoppingBossMVVM.EventLiveData
import com.khan.fftracker.tracker.TrackerConstant
import com.khan.fftracker.tracker.friendPlaces.dataModel.TrackerGeofenceListResponse
import com.khan.fftracker.tracker.friendPlaces.dataModel.FriendPlaceItem
import com.khan.fftracker.tracker.friendPlaces.dataModel.FriendPlacesData
import com.khan.fftracker.tracker.friendPlaces.dataModel.Geofencelists
import com.khan.fftracker.tracker.aApiTracker.TrackerRepo
import com.khan.fftracker.tracker.aApiTracker.TrackerRepoImpl
import com.khan.fftracker.tracker.trackerDashboard.dataModel.Contract
import kotlinx.coroutines.launch

class FriendPlacesVM(private val apiCall: TrackerRepoImpl) : ViewModel() {
   /* _geoFenceEdit value will 0 or 1
    0== for editing or deleting geofence
    1== for adding geofence*/
     private var _geoFenceEdit= MutableLiveData("")

    //boolean navigation for one time action perform
    private val _navigateToAddGeofence = MutableLiveData<EventLiveData<Boolean>>()

    ///response from web api
    private val _GeofenceListResponse = MutableLiveData<ResultApi<TrackerGeofenceListResponse>>()
    private val _notificationOnOff = MutableLiveData<ResultApi<GenericResponse>>()

    private var friendContractItemPref = MutableLiveData<Contract>()

    ///map take few secs to be ready. So call this boolean value let know map operations
    var isMapReady = MutableLiveData(false)

    var _friendPlaceResponse = MutableLiveData<ResultApi<FriendPlacesData>>()

    var listItem: MutableList<FriendPlaceItem> = ArrayList()

    var list= MutableLiveData<MutableList<Geofencelists>>()
    var listGeofence: MutableList<Geofencelists> = ArrayList()
    init {
        getFriendContractPref()

        //call api and get response
        onGeofenceListApi()
        list.value=listGeofence



    }
    private fun getFriendContractPref() {
        friendContractItemPref.value = Prefs_OperationKotlin.getModelPref(TrackerConstant.FRIEND_CONTRACT_ITEM_PREFS, Contract::class.java)

    }
     fun getGeofenceList():LiveData<MutableList<Geofencelists>> {
        return list
    }

    fun getRecylerViewList(): MutableList<Geofencelists> {
        return listGeofence
    }

    fun clearList(list: List<Geofencelists>) {
        //val myArrayList = ArrayList(LinkedHashSet<Geofencelists>(list))

//        this.list.value!!.clear()
//        this.list.value!!.addAll(myArrayList)

       // listGeofence=list.toMutableList()
       // this.list.value=listGeofence

        this.list.value!!.clear()
        this.list.value!!.addAll(list)


    }

    //map take few secs to be ready. call this boolean value let know map operations
    fun isMapReady() {
        isMapReady.value = true
    }

    //observe for MapReady
    fun getIsMapReady(): MutableLiveData<Boolean> {
        return isMapReady
    }

    //call api and get response
     fun onGeofenceListApi() {

        val map = HashMap<String, String>()
        map["FriendContractID"] = friendContractItemPref.value!!.ContractID
        map["FriendIsGuest"] = friendContractItemPref.value!!.IsGuest
        map["GroupID"] = Network_Stuffs.GROUP_ID



        viewModelScope.launch {
            try {
                LogCalls_Debug.d(LogCalls_Debug.TAG, "getCheckSerialNo")
                _GeofenceListResponse.value = ResultApi.loading(null)

                 _GeofenceListResponse.value = apiCall.getGeofenceListResponse(map)

            } catch (exception: Exception) {
                LogCalls_Debug.d(LogCalls_Debug.TAG, exception.message)
            }

        }
    }

    //observe for Api response
    fun getGeofenceListResponse(): LiveData<ResultApi<TrackerGeofenceListResponse>> {
        return _GeofenceListResponse
    }

    ///Api for Notification on/Off
    fun onNotificationOnOffApi(modelClass: Geofencelists, pos: Int) {
        val strNotification=if(modelClass.NotificationOn == "0")"1" else "0"
        list.value!![pos].NotificationOn =strNotification

        val map = HashMap<String, String>()
        map["GeofenctID"] = modelClass.GeofenctID
        map["NotificationOn"] = strNotification

        map["FriendContractID"] = friendContractItemPref.value!!.ContractID
        map["FriendIsGuest"] = friendContractItemPref.value!!.IsGuest
        map["GroupID"] = Network_Stuffs.GROUP_ID


        viewModelScope.launch {
            try {
                LogCalls_Debug.d(LogCalls_Debug.TAG, "onNotificationOnOffApi")
                _notificationOnOff.value = ResultApi.loading(null)

                _notificationOnOff.value = apiCall.getNotificationOnOffResponse(map)

            } catch (exception: Exception) {
                LogCalls_Debug.d(LogCalls_Debug.TAG, exception.message)
            }

        }
    }

    //observe for Api response
    fun getNotificationOnOffResponse(): LiveData<ResultApi<GenericResponse>> {
        return _notificationOnOff
    }
    //observe for AddRemoveFav  response
    //Live data observing in fragment. Purpose not to expose mutable (changeable) data in fragments
    fun getNavigateAddGeofence(): LiveData<EventLiveData<Boolean>> {
        return _navigateToAddGeofence
    }

    ///save response class in prefs and navigate to AutoVerseGeofenceRadius fragment
    fun onSaveGeoFenceResponse(modelClass: Geofencelists) {
        //save model class in prefs
        Prefs_OperationKotlin.setModelPref(TrackerConstant.GEOFENCE_ITEM_PREFS, modelClass)
        onNavigateAddGeofence("1")
    }
    fun onNavigateAddGeofence(strEdit: String) {
        _geoFenceEdit.value=strEdit
        _navigateToAddGeofence.value = EventLiveData(true)
    }


     /* _geoFenceEdit value will 0 or 1
    0== for editing or deleting geofence
    1== for adding geofence*/
    //Live data observing in fragment. Purpose not to expose mutable (changeable) data in fragments
    fun getGeoFenceEdit(): LiveData<String> {
        return _geoFenceEdit
    }

    fun savePlaceRegistered(placesRegister: Int) {
        Prefs_Operation.writePrefs(TrackerConstant.PLACE_REGISTERED,placesRegister.toString())
    }


    class VMFactory(private val repositoryApi: TrackerRepo) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FriendPlacesVM(TrackerRepoImpl(repositoryApi)) as T
        }
    }
}

//private fun setRecyclerViewList() {
////        val listOne = FriendPlacesData("", "Home", "22415 US-98, Fairhope,Al", "", 1, "")
////        val listTwo = FriendPlacesData("", "School", "22415 US-98, Fairhope,Al", "", 1, "")
////        val listThree = FriendPlacesData("", "Work", "Magnolia Ave, Fairhope,Al", "", 1, "")
//
//    listItem.add(FriendPlaceItem("","Home","22415 US-98, Fairhope,Al",""))
//    listItem.add(FriendPlaceItem("","School","22415 US-98, Fairhope,Al",""))
//    listItem.add(FriendPlaceItem("","Work","Magnolia Ave, Fairhope,Al",""))
//
//    val listOne1 = FriendPlacesData(listItem, 1, "")
//
//    val resultApiReponse = ResultApi.success(listOne1)
//    _friendPlaceResponse.value = resultApiReponse
//
////        val resultApiReponseTwo = ResultApi.success(listTwo)
////        _friendPlaceResponse.value = resultApiReponseTwo
////
////        val resultApiReponseThree = ResultApi.success(listThree)
////        _friendPlaceResponse.value = resultApiReponseThree
//
//
//}
