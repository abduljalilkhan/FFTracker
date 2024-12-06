package com.khan.fftracker.tracker.alertSetting.placeAlerts.placeHistory.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.khan.fftracker.logCalls.LogCalls_Debug
import com.khan.fftracker.Network_Volley.Network_Stuffs
import com.khan.fftracker.autoverse_mvvm.networkApi.ResultApi
import com.khan.fftracker.shoppingBossMVVM.EventLiveData
import com.khan.fftracker.tracker.alertSetting.placeAlerts.placeHistory.dataModel.AlertHistoryResponse
import com.khan.fftracker.tracker.aApiTracker.TrackerRepo
import com.khan.fftracker.tracker.aApiTracker.TrackerRepoImpl
import kotlinx.coroutines.launch

class AlertHistoryTrackerVM (private val apiCall: TrackerRepoImpl) : ViewModel() {
    //boolean navigation for one time action perform
    private var _navigateFriendInfo = MutableLiveData<EventLiveData<Boolean>>()

    ///response from web api
    private val _alertHistoryResponse = MutableLiveData<ResultApi<AlertHistoryResponse>>()

    var userName = MutableLiveData("")

    init {
        //call api and get response
        onFriendListApi()

    }

    //call api and get response
    private fun onFriendListApi() {
        _alertHistoryResponse.value = ResultApi.loading(null)

        val map = HashMap<String, String>()
        map["GroupID"] = Network_Stuffs.GROUP_ID

        viewModelScope.launch {
            try {
                LogCalls_Debug.d(LogCalls_Debug.TAG, "onFriendListApi")
                //update observer in fragment
                _alertHistoryResponse.value =apiCall.getAlertHistoryResponse(map)

            } catch (exception: Exception) {
                exception.fillInStackTrace()
                LogCalls_Debug.d(LogCalls_Debug.TAG, exception.message)
            }
        }
    }

    //Live data observing in fragment. Purpose not to expose mutable (changeable) data in fragments
    fun getAlertHistoryResponse(): LiveData<ResultApi<AlertHistoryResponse>> {
        return _alertHistoryResponse
    }

    class VMFactory(private val repositoryApi: TrackerRepo) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AlertHistoryTrackerVM(TrackerRepoImpl(repositoryApi)) as T
        }
    }
}