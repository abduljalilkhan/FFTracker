package com.khan.fftracker.tracker.alertSetting.batteryAlert.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.khan.fftracker.logCalls.LogCalls_Debug
import com.khan.fftracker.Network_Volley.Network_Stuffs
import com.khan.fftracker.autoverse_mvvm.networkApi.ResultApi
import com.khan.fftracker.autoverse_mvvm.utils.GenericResponse
import com.khan.fftracker.tracker.aApiTracker.TrackerRepo
import com.khan.fftracker.tracker.aApiTracker.TrackerRepoImpl
import com.khan.fftracker.tracker.trackerDashboard.dataModel.Contract
import com.khan.fftracker.tracker.trackerDashboard.dataModel.TrackerDashFriendResponse
import kotlinx.coroutines.launch

class BatteryAlertVM (private val apiCall: TrackerRepoImpl) : ViewModel() {
    ///response from web api
    private val _friendListResponse = MutableLiveData<ResultApi<TrackerDashFriendResponse>>()
    private val _notificationOnOff = MutableLiveData<ResultApi<GenericResponse>>()

    init {
        //call api and get response
        onFriendListApi()

    }
    //call api and get response
    private fun onFriendListApi() {
        _friendListResponse.value = ResultApi.loading(null)

        val map = HashMap<String, String>()
        map["GroupID"] = Network_Stuffs.GROUP_ID

        viewModelScope.launch {
            try {
                LogCalls_Debug.d(LogCalls_Debug.TAG, "onFriendListApi")
                //update observer in fragment
                _friendListResponse.value =apiCall.getFriendListResponse(map)

            } catch (exception: Exception) {
                exception.fillInStackTrace()
                LogCalls_Debug.d(LogCalls_Debug.TAG, exception.message)
            }
        }
    }

    //Live data observing in fragment. Purpose not to expose mutable (changeable) data in fragments
    fun getFriendListResponse(): LiveData<ResultApi<TrackerDashFriendResponse>> {
        return _friendListResponse
    }

    ///Api for Notification on/Off
    fun onNotificationOnOffApi(modelClass: Contract) {

        val map = HashMap<String, String>()

        map["LowBatteryNotification"] = modelClass.LowBatteryNotification
        map["FriendContractID"] = modelClass.ContractID
        map["FriendIsGuest"] = modelClass.IsGuest

        map["GroupID"] = Network_Stuffs.GROUP_ID


        viewModelScope.launch {
            try {
                LogCalls_Debug.d(LogCalls_Debug.TAG, "onNotificationOnOffApi")
              //  _notificationOnOff.value = ResultApi.loading(null)

                _notificationOnOff.value = apiCall.getBatteryAlertOnOffResponse(map)

            } catch (exception: Exception) {
                LogCalls_Debug.d(LogCalls_Debug.TAG, exception.message)
            }

        }
    }

    //observe for Api response
    fun getNotificationOnOffResponse(): LiveData<ResultApi<GenericResponse>> {
        return _notificationOnOff
    }

    class VMFactory(private val repositoryApi: TrackerRepo) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return BatteryAlertVM(TrackerRepoImpl(repositoryApi)) as T
        }
    }
}