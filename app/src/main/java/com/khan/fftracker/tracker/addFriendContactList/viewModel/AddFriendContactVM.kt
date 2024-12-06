package com.khan.fftracker.tracker.addFriendContactList.viewModel

import android.content.pm.PackageInfo
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.khan.fftracker.logCalls.LogCalls_Debug
import com.khan.fftracker.Network_Volley.Network_Stuffs
import com.khan.fftracker.Prefrences.Prefs_OperationKotlin
import com.khan.fftracker.mvvmData.networkApi.ResultApi

import com.khan.fftracker.shoppingBossMVVM.EventLiveData
import com.khan.fftracker.tracker.TrackerConstant
import com.khan.fftracker.tracker.addFriendContactList.dataModel.InviteURLResponse
import com.khan.fftracker.tracker.aApiTracker.TrackerRepo
import com.khan.fftracker.tracker.aApiTracker.TrackerRepoImpl
import kotlinx.coroutines.launch

class AddFriendContactVM(private val apiCall: TrackerRepoImpl) : ViewModel() {

    //boolean navigation for one time action perform
    private val _navigateCancel = MutableLiveData<EventLiveData<Boolean>>()
    var contactPermission = MutableLiveData<EventLiveData<Boolean>>()
    var _shareLink = MutableLiveData<EventLiveData<Boolean>>()
    var _copyLink = MutableLiveData<EventLiveData<Boolean>>()
    var _whatsAppShareLink = MutableLiveData<EventLiveData<Boolean>>()
    var _instagramShareLink = MutableLiveData<EventLiveData<Boolean>>()


    ///response from web api
    private val _inviteLinkResponse = MutableLiveData<ResultApi<InviteURLResponse>>()

    var mContactList: MutableList<ContactList> = ArrayList()

    var strInviteURl = MutableLiveData("")
    var strUserInviteURl = MutableLiveData("")
    var strPackageName = MutableLiveData("")

    var strPhoneNo = MutableLiveData("")


    //query for recyclerview adaptor
    var strAdaptorFilter = MutableLiveData("")

    // for show hide view
    var isRecyclerViewShow = MutableLiveData(false)
    var hideShowWhatsAppImg = MutableLiveData(false)
    var hideShowInstagramImg = MutableLiveData(false)

    init {
        //call api and get response
        //  onGeofenceApi()
        userInviteUrlPrefs()
    }

    private fun userInviteUrlPrefs() {
        strUserInviteURl.value=Prefs_OperationKotlin.readString(TrackerConstant.INVITE_URL,"")
    }

    //navigate to fragment
    fun getNavigateCancel(): LiveData<EventLiveData<Boolean>> {
        return _navigateCancel
    }

    //navigate to fragment
    fun onCancel() {
        _navigateCancel.value = EventLiveData(true)

    }


    fun getContactList(): List<ContactList> {
        return mContactList
    }


    fun setContactList(name: List<ContactList>) {
        mContactList.addAll(name)
    }

    fun getContactPermissionGranted(): LiveData<EventLiveData<Boolean>> {
        return contactPermission
    }

    fun setContactPermissionGranted() {
        contactPermission.value = EventLiveData(true)

    }

    fun setShareLink() {
        _shareLink.value = EventLiveData(true)
    }

    fun getShareLink(): LiveData<EventLiveData<Boolean>> {
        return _shareLink
    }

    fun getCopyLink(): LiveData<EventLiveData<Boolean>> {
        return _copyLink
    }

    fun setCopyLink() {
        if (strInviteURl.value.equals("")) {
            strInviteURl.value= "I invite you to AV tracker. Click the link below to add me."+strUserInviteURl.value
        }
        _copyLink.value = EventLiveData(true)
    }


    fun setWhatsAppShare() {
        strPackageName.value="com.whatsapp"
        _whatsAppShareLink.value = EventLiveData(true)
    }

    fun getWhatsAppShare(): LiveData<EventLiveData<Boolean>> {
        return _whatsAppShareLink
    }

    fun setInstagramShare() {
        strPackageName.value="com.instagram.android"
        _whatsAppShareLink.value = EventLiveData(true)
       //  _instagramShareLink.value = EventLiveData(true)
    }

    fun getInstagramShare(): LiveData<EventLiveData<Boolean>> {
        return _instagramShareLink
    }

    fun setRecyclerViewVisibility() {
        isRecyclerViewShow.value = true
    }

    fun getAdaptorFilter(): MutableLiveData<String> {
        return strAdaptorFilter
    }
    fun getPhoneNo(): MutableLiveData<String> {
        return strPhoneNo
    }
    fun getPackageName(): MutableLiveData<String> {
        return strPackageName
    }
    fun getUserInviteUrl(): MutableLiveData<String> {
        return strUserInviteURl
    }
    fun getInviteUrl(): MutableLiveData<String> {
        return strInviteURl
    }
    fun setInviteUrl(inviteURL: String) {
        strInviteURl.value= "I invite you to AV tracker. Click the link below to add me. $inviteURL"
    }

    fun onInviteLinkApi(phoneNumber: String, name: String) {
        strPhoneNo.value = phoneNumber
        _inviteLinkResponse.value = ResultApi.loading(null)

        val map = HashMap<String, String>()
        map["GroupID"] = Network_Stuffs.GROUP_ID
        map["Phone"] = phoneNumber
        map["CustomerFName"] = name


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

    fun setIsWhatsAppInstalled(appInfo: PackageInfo?) {
        if (appInfo != null) {
            hideShowWhatsAppImg.value = true
        }
    }

    fun setIsInstagramInstalled(appInfo: PackageInfo?) {
        if (appInfo != null){
            hideShowInstagramImg.value=true
        }
    }




    class VMFactory(private val repositoryApi: TrackerRepo) :
            ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return AddFriendContactVM(TrackerRepoImpl(repositoryApi)) as T
        }
    }
}