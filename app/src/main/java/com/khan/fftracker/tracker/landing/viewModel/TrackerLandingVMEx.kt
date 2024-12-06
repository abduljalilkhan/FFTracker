package com.khan.fftracker.tracker.landing.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.khan.fftracker.logCalls.LogCalls_Debug
import com.khan.fftracker.Network_Volley.Network_Stuffs
import com.khan.fftracker.autoverse_mvvm.networkApi.ResultApi
import com.khan.fftracker.shoppingBossMVVM.EventLiveData
import com.khan.fftracker.tracker.aApiTracker.TrackerRepo
import com.khan.fftracker.tracker.aApiTracker.TrackerRepoImpl
import com.khan.fftracker.tracker.landing.dataModel.SliderResponse
import kotlinx.coroutines.launch

class TrackerLandingVMEx (private val apiCall: TrackerRepoImpl): ViewModel() {

    //boolean navigation for one time action perform
    private val _navigateToPermission = MutableLiveData<EventLiveData<Boolean>>()

    ///response from web api
    private val _SliderResponse = MutableLiveData<ResultApi<SliderResponse>>()

    init {
        //call api and get response
        onSliderApi()

    }

    //call api and get response
    private fun onSliderApi() {
        _SliderResponse.value = ResultApi.loading(null)

        val map = HashMap<String, String>()
        map["GroupID"] = Network_Stuffs.GROUP_ID

        viewModelScope.launch {
            try {
                LogCalls_Debug.d(LogCalls_Debug.TAG, "onSliderApi")

                _SliderResponse.value = apiCall.getSliderResponse(map)

            } catch (exception: Exception) {
                LogCalls_Debug.d(LogCalls_Debug.TAG, exception.message)
            }

        }
    }


    //observe for AddRemoveFav  response
    //Live data observing in fragment. Purpose not to expose mutable (changeable) data in fragments
    fun getSliderResponse(): LiveData<ResultApi<SliderResponse>> {
        return _SliderResponse
    }

    //observe for AddRemoveFav  response
    //Live data observing in fragment. Purpose not to expose mutable (changeable) data in fragments
    fun getNavigatePermission(): LiveData<EventLiveData<Boolean>> {
        return _navigateToPermission
    }
    //navigate to fragment
    fun onNavigatePermission() {
        _navigateToPermission.value = EventLiveData(true)
    }

    @Suppress("UNCHECKED_CAST")
    class VMFactory(private val repositoryApi: TrackerRepo) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return TrackerLandingVMEx(TrackerRepoImpl(repositoryApi)) as T
        }
    }

}