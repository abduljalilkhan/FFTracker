package com.khan.fftracker.tracker.locationPermission.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.khan.fftracker.R
import com.khan.fftracker.shoppingBossMVVM.EventLiveData
import com.khan.fftracker.tracker.aApiTracker.TrackerRepo
import com.khan.fftracker.tracker.aApiTracker.TrackerRepoImpl

class GetLocPermissionVM(private val apiCall:TrackerRepoImpl): ViewModel() {

    val leftAnim=MutableLiveData(R.raw.leftdouble_arrow)
    val rightAnim=MutableLiveData(R.raw.rightdouble_arrow)
    //boolean navigation for one time action perform
    private val _navigateToDashBoard = MutableLiveData<EventLiveData<Boolean>>()
    private val _navigateToLocPermission = MutableLiveData<EventLiveData<Boolean>>()

    //Live data observing in fragment. Purpose not to expose mutable (changeable) data in fragments
    fun getNavigatePermission(): LiveData<EventLiveData<Boolean>> {
        return _navigateToLocPermission
    }

    //navigate to fragment
    fun onLocationPermission(){
        _navigateToLocPermission.value = EventLiveData(true)
    }

    //Live data observing in fragment. Purpose not to expose mutable (changeable) data in fragments
    fun getTrackerDashBoard(): LiveData<EventLiveData<Boolean>> {
        return _navigateToDashBoard
    }
    //navigate to fragment
    fun onTrackerDashBoard(){
        _navigateToDashBoard.value = EventLiveData(true)
    }

    class VMFactory(private val repositoryApi: TrackerRepo) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return GetLocPermissionVM(TrackerRepoImpl(repositoryApi)) as T
        }
    }

}