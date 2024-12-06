package com.khan.fftracker.tracker.userMenuSetting.friendSettings.friendInfo.viewModel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.githubmvvm.Network_Stuff.CommanWebApiParams
import com.khan.fftracker.logCalls.LogCalls_Debug
import com.khan.fftracker.logCalls.LogCalls_Debug.TAG
import com.khan.fftracker.Network_Volley.Network_Stuffs
import com.khan.fftracker.Prefrences.Prefs_Operation
import com.khan.fftracker.Prefrences.Prefs_OperationKotlin
import com.khan.fftracker.autoverse_mvvm.networkApi.ResultApi
import com.khan.fftracker.autoverse_mvvm.utils.GenericResponse
import com.khan.fftracker.commanStuff.Bitmap_Stuff.Bitmap_Utils
import com.khan.fftracker.shoppingBossMVVM.EventLiveData
import com.khan.fftracker.tracker.TrackerConstant
import com.khan.fftracker.tracker.aApiTracker.TrackerRepo
import com.khan.fftracker.tracker.aApiTracker.TrackerRepoImpl
import com.khan.fftracker.tracker.trackerDashboard.dataModel.Contract
import kotlinx.coroutines.launch
import okhttp3.RequestBody
import java.io.File
import java.util.concurrent.Future

class FriendInfoVM(private val apiCall: TrackerRepoImpl) : ViewModel() {

    //boolean navigation for one time action perform
    private var _navigateCancel = MutableLiveData<EventLiveData<Boolean>>()

    private var _bottomSheet = MutableLiveData<EventLiveData<Boolean>>()
    private var _cameraLaunch = MutableLiveData<EventLiveData<Boolean>>()
    private var _galleryOpen = MutableLiveData<EventLiveData<Boolean>>()


    ///response from web api
    private val _friendPrivacySettingResponse = MutableLiveData<ResultApi<GenericResponse>>()
    private val _profileImageSavedResponse = MutableLiveData<ResultApi<GenericResponse>>()

    private var friendContractItemPref = MutableLiveData<Contract>()

    private var friendTrackerName = MutableLiveData("")
    private var friendImage: MutableLiveData<Bitmap>? = MutableLiveData()
    private var fileToServer: File? = null
    private var strPrivacySetting = MutableLiveData("3")
    private var strPrivacyText = MutableLiveData("Your friend can see your location on the map.")
    init {
        getFriendContractPref()

        getBitmapFromUrl()
    }

    private fun getFriendContractPref() {
        friendContractItemPref.value = Prefs_OperationKotlin.getModelPref(TrackerConstant.FRIEND_CONTRACT_ITEM_PREFS, Contract::class.java)

        friendTrackerName.value = friendContractItemPref.value!!.CustomerFName + " " + friendContractItemPref.value!!.CustomerLName

      //  strPrivacySetting.value=friendContractItemPref.value!!.PrivacySetting
        setPrivacySetting(friendContractItemPref.value!!.PrivacySetting)
        //friendImage.value=friendContractItemPref.value!!.CustomerImage

    }

    private fun getBitmapFromUrl() {
        //friendImage.value = Bitmap_Utils().bitmapFromUrl(friendContractItemPref.value!!.CustomerImage)
        val imgBitmap: Future<Bitmap>? = Bitmap_Utils().getBitmapFromUrl(friendContractItemPref.value!!.CustomerImage)
        friendImage!!.value=imgBitmap!!.get()

    }

    //call api and get response
    fun onSaveLocSettingApi() {

        val map = HashMap<String, String>()
        map["FriendContractID"] = friendContractItemPref.value!!.ContractID
        map["FriendIsGuest"] = friendContractItemPref.value!!.IsGuest
        map["GroupID"] = Network_Stuffs.GROUP_ID
        map["PrivacySetting"] = strPrivacySetting.value.toString()


        viewModelScope.launch {
            try {
                LogCalls_Debug.d(LogCalls_Debug.TAG, "onSaveLocSettingApi")
                _friendPrivacySettingResponse.value = ResultApi.loading(null)

                _friendPrivacySettingResponse.value = apiCall.getFriendPrivacySettingResponse(map)

            } catch (exception: Exception) {
                LogCalls_Debug.d(LogCalls_Debug.TAG, exception.message)
            }

        }
    }

    //observe for MapReady
    fun getSaveLocSettingResponse(): LiveData<ResultApi<GenericResponse>> {
        return _friendPrivacySettingResponse
    }

    fun onProfileImageSaved() {

        _profileImageSavedResponse.value = ResultApi.loading(null)
        viewModelScope.launch {
            val commanWebApiParams = CommanWebApiParams()
            var params: MutableMap<String, RequestBody> = HashMap()
            params["FriendContractID"] = commanWebApiParams.createRequestBody(friendContractItemPref.value!!.ContractID)
            params["FriendIsGuest"] = commanWebApiParams.createRequestBody(friendContractItemPref.value!!.IsGuest)
            params["GroupID"] = commanWebApiParams.createRequestBody(Network_Stuffs.GROUP_ID)


            params = CommanWebApiParams().commanParams(params)


            val files: MutableMap<String, RequestBody> = HashMap()
            if (fileToServer != null) {
                files["uploadimg\"; filename=\"file_avatar.png\" "] = commanWebApiParams.createRequestBody(fileToServer!!)

            }
            LogCalls_Debug.d(TAG, params.toString())

            val strFunction = "v/${Network_Stuffs.URL_VERSION}/familytracker/addfriendphoto"

            try {
                _profileImageSavedResponse.value = apiCall.getUploadImageResponse(strFunction, params, files)

            } catch (exception: Exception) {
                LogCalls_Debug.d(LogCalls_Debug.TAG, exception.message)
            }
        }
    }

    //observe for MapReady
    fun getProfileImageSavedResponse(): LiveData<ResultApi<GenericResponse>> {
        return _profileImageSavedResponse
    }

    fun getNavigateCancel(): LiveData<EventLiveData<Boolean>> {
        return _navigateCancel
    }

    fun onCancel() {
        _navigateCancel.value = EventLiveData(true)

    }

    fun getShowBottomSheetDialog(): LiveData<EventLiveData<Boolean>> {
        return _bottomSheet
    }

    fun onShowBottomSheetDialog() {
        _bottomSheet.value = EventLiveData(true)
    }

    fun onCameraLaunch() {
        _cameraLaunch.value = EventLiveData(true)
    }

    fun getCameraLaunch(): LiveData<EventLiveData<Boolean>> {
        return _cameraLaunch
    }

    fun onGalleryOpen() {
        _galleryOpen.value = EventLiveData(true)
    }

    fun getGalleryOpen(): LiveData<EventLiveData<Boolean>> {
        return _galleryOpen
    }
    //observe
    fun getPrivacySetting(): LiveData<String> {
        return strPrivacySetting
    }
    fun setPrivacySetting(privacySetting: String) {
        strPrivacySetting.value = privacySetting
        LogCalls_Debug.d(TAG, "strPrivacySetting ${strPrivacySetting.value}")
        setPrivacyText(privacySetting)
    }

    private fun setPrivacyText(privacySetting: String) {
        when(privacySetting){
            "1"->
                strPrivacyText.value="Your friend cannot see your location."
            "2"->
                strPrivacyText.value="Your friend cannot see your location but can see the distance between you and them."
            "3"->
                strPrivacyText.value="Your friend can see your location on the map."

        }
    }

    fun getPrivacyText(): LiveData<String> {
        return strPrivacyText
    }
    fun getFriendName(): LiveData<String> {
        return friendTrackerName
    }

    fun getFriendImage(): LiveData<Bitmap> {
        return friendImage!!
    }

    fun setFriendImage(bitmap: Bitmap?, file: File) {
        friendImage!!.value = bitmap!!
        fileToServer = file
        onProfileImageSaved()
    }


    fun saveFriendImage(url: String) {

        Prefs_Operation.writePrefs(TrackerConstant.FRIEND_IMG,url)
    }


    class VMFactory(private val repositoryApi: TrackerRepo) :
            ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FriendInfoVM(TrackerRepoImpl(repositoryApi)) as T
        }
    }
}


