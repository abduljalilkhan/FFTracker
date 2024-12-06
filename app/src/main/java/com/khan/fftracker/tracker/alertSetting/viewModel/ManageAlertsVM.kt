package com.khan.fftracker.tracker.alertSetting.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.khan.fftracker.shoppingBossMVVM.EventLiveData

class ManageAlertsVM : ViewModel() {
    //boolean navigation for one time action perform
    private var _navigatePlaceAlert = MutableLiveData<EventLiveData<Boolean>>()
    private var _navigateNearAlert = MutableLiveData<EventLiveData<Boolean>>()
    private var _navigateBatteryAlert = MutableLiveData<EventLiveData<Boolean>>()


    //Place Alert
    //observe navigate to fragment
    fun getNavigatePlaceAlert(): LiveData<EventLiveData<Boolean>> {
        return _navigatePlaceAlert
    }

    //navigate to fragment
    fun onNavigatePlaceAlert() {
        _navigatePlaceAlert.value = EventLiveData(true)
    }



    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    //Nearby Alert
    //observe navigate to fragment
    fun getNavigateNearAlert(): LiveData<EventLiveData<Boolean>> {
        return _navigateNearAlert
    }

    //navigate to fragment
    fun onNavigateNearAlert() {
        _navigateNearAlert.value = EventLiveData(true)
    }



    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    //Battery Alert
    //observe navigate to fragment
    fun getNavigateBatteryAlert(): LiveData<EventLiveData<Boolean>> {
        return _navigateBatteryAlert
    }

    //navigate to fragment
    fun onNavigateBatteryAlert() {
        _navigateBatteryAlert.value = EventLiveData(true)
    }

}