package com.khan.fftracker.tracker.alertSetting.nearByAlert.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.khan.fftracker.logCalls.LogCalls_Debug
import com.khan.fftracker.Network_Volley.Network_Stuffs
import com.khan.fftracker.Prefrences.Prefs_OperationKotlin
import com.khan.fftracker.autoverse_mvvm.networkApi.ResultApi
import com.khan.fftracker.autoverse_mvvm.utils.GenericResponse
import com.khan.fftracker.shoppingBossMVVM.EventLiveData
import com.khan.fftracker.tracker.TrackerConstant
import com.khan.fftracker.tracker.aApiTracker.TrackerRepo
import com.khan.fftracker.tracker.aApiTracker.TrackerRepoImpl
import com.khan.fftracker.tracker.trackerDashboard.dataModel.Contract
import kotlinx.coroutines.launch

class NearByAlertVM (private val apiCall: TrackerRepoImpl) : ViewModel() {

    //boolean navigation for one time action perform
    private var _navigateCancel = MutableLiveData<EventLiveData<Boolean>>()

    ///response from web api
    private val _nearbyAlertResponse = MutableLiveData<ResultApi<GenericResponse>>()

    private var friendContractItemPref = MutableLiveData<Contract>()


    var enableNearbyAlert = MutableLiveData("")
    var sBarRadiusProgress = MutableLiveData(Prefs_OperationKotlin.readString(TrackerConstant.NEARBY_DISTANCE,"0")!!.toInt())



    private var switchNotify: Boolean = true
    init {
       // sBarRadiusProgress.value=10
        getFriendContractPref()
        LogCalls_Debug.d(LogCalls_Debug.TAG, "init")
    }

    private fun getFriendContractPref() {
        friendContractItemPref.value = Prefs_OperationKotlin.getModelPref(TrackerConstant.FRIEND_CONTRACT_ITEM_PREFS, Contract::class.java)

    }


    //call api and get response
    fun onNearbyAlertApi() {

        val map = HashMap<String, String>()
        map["FriendContractID"] = friendContractItemPref.value!!.ContractID
        map["FriendIsGuest"] = friendContractItemPref.value!!.IsGuest
        map["GroupID"] = Network_Stuffs.GROUP_ID

        map["NearbyDistance"] = sBarRadiusProgress.value.toString()
        map["EnableNearbyAlert"] =  if (switchNotify) "1" else "0"


        viewModelScope.launch {
            try {
                LogCalls_Debug.d(LogCalls_Debug.TAG, "onNearbyAlertApi")
                _nearbyAlertResponse.value = ResultApi.loading(null)

                _nearbyAlertResponse.value = apiCall.getNearbyAlertResponse(map)

            } catch (exception: Exception) {
                LogCalls_Debug.d(LogCalls_Debug.TAG, exception.message)
            }

        }
    }

    //observe for MapReady
    fun getNearbyAlertApiResponse(): LiveData<ResultApi<GenericResponse>> {
        return _nearbyAlertResponse
    }


    fun onSeekBarRadiusProgress(progress: Int) {

        LogCalls_Debug.d(LogCalls_Debug.TAG, "onSeekBarRadiusProgress")
        if (progress!=0 && !enableNearbyAlert.value.equals("")){
            enableNearbyAlert.value="1"
            LogCalls_Debug.d(LogCalls_Debug.TAG, "value1")
        }
        if (enableNearbyAlert.value.equals("")){
            LogCalls_Debug.d(LogCalls_Debug.TAG, "value0")
            enableNearbyAlert.value=Prefs_OperationKotlin.readString(TrackerConstant.NEARBY_ALERT,"0")
        }
    }
    fun getSeekBarRadiusProgress(): LiveData<Int> {
        return sBarRadiusProgress
    }
    fun getNavigateCancel(): LiveData<EventLiveData<Boolean>> {
        return _navigateCancel
    }

    fun onCancel() {
        _navigateCancel.value = EventLiveData(true)

    }
    fun onCheckedChanged(checked: Boolean) {
        switchNotify = checked
        if (checked){
            enableNearbyAlert.value="1"
        }
    }

    fun manageResponse() {
        Prefs_OperationKotlin.writeString(TrackerConstant.NEARBY_ALERT,if (switchNotify) "1" else "0")
        Prefs_OperationKotlin.writeString(TrackerConstant.NEARBY_DISTANCE,sBarRadiusProgress.value.toString())
    }


    class VMFactory(private val repositoryApi: TrackerRepo) :
            ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return NearByAlertVM(TrackerRepoImpl(repositoryApi)) as T
        }
    }
}


