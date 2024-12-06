package com.khan.fftracker.tracker.alertSetting.placeAlerts.viewModel

import android.graphics.Bitmap
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
import com.khan.fftracker.shoppingBossMVVM.EventLiveData
import com.khan.fftracker.tracker.TrackerConstant
import com.khan.fftracker.tracker.aApiTracker.TrackerRepo
import com.khan.fftracker.tracker.aApiTracker.TrackerRepoImpl
import com.khan.fftracker.tracker.trackerDashboard.dataModel.Contract
import com.khan.fftracker.tracker.trackerDashboard.dataModel.TrackerDashFriendResponse
import kotlinx.coroutines.launch

class PlaceAlertFriendListVM (private val apiCall: TrackerRepoImpl) : ViewModel() {
    //boolean navigation for one time action perform
    private var _navigateFriendInfo = MutableLiveData<EventLiveData<Boolean>>()
    private var _navigateAlertHistory = MutableLiveData<EventLiveData<Boolean>>()

    ///response from web api
    private val _friendListResponse = MutableLiveData<ResultApi<TrackerDashFriendResponse>>()

    var userName = MutableLiveData("")

    var bitmap: Bitmap? = null
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

    //observe navigate to ChooseAddress fragment
    fun getNavigateFriendInfo(): LiveData<EventLiveData<Boolean>> {
        return _navigateFriendInfo
    }

    //navigate to fragment
    fun onNavigateFriendInfo(modelClass: Contract) {
        //save model class in prefs
        Prefs_OperationKotlin.setModelPref(TrackerConstant.FRIEND_CONTRACT_ITEM_PREFS, modelClass)
        _navigateFriendInfo.value = EventLiveData(true)
    }
    //Place Alert
    //observe navigate to fragment
    fun getNavigateAlertHistory(): LiveData<EventLiveData<Boolean>> {
        return _navigateAlertHistory
    }

    //navigate to fragment
    fun onNavigateAlertHistory() {
        _navigateAlertHistory.value = EventLiveData(true)
    }
    fun updateListUploadImage(): String? {
        return Prefs_Operation.readPrefs(TrackerConstant.FRIEND_IMG,"https://")
    }
    fun updatePlaceRegistered(): String? {
        return Prefs_Operation.readPrefs(TrackerConstant.PLACE_REGISTERED,"0")
    }
    class VMFactory(private val repositoryApi: TrackerRepo) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PlaceAlertFriendListVM(TrackerRepoImpl(repositoryApi)) as T
        }
    }
}