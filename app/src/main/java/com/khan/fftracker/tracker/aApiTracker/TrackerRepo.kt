package com.khan.fftracker.tracker.aApiTracker

import okhttp3.RequestBody
import javax.inject.Inject

class TrackerRepo @Inject constructor(private val apiInterface: TrackerApi) {
    suspend fun getAnyResponse(strUrl: String, hashMap: HashMap<String, String>)= apiInterface.getAnyResponse(hashMap,strUrl,hashMap)

    suspend fun getUploadImageResponse(strFunction: String, hashMap: MutableMap<String, RequestBody>, files: MutableMap<String, RequestBody>)=
            apiInterface.uploadImage(strFunction,hashMap,files)

    suspend fun getSliderResponse(hashMap:HashMap<String,String>)= apiInterface.sliderResponse(hashMap)

    suspend fun getInviteLinkResponse(hashMap:HashMap<String,String>)= apiInterface.inviteLinkResponse(hashMap)

    suspend fun getFriendListResponse(hashMap:HashMap<String,String>)= apiInterface.friendListResponse(hashMap)

    suspend fun getFriendLatLngResponse(hashMap:HashMap<String,String>)= apiInterface.getFriendLatLng(hashMap)

    suspend fun saveFriendLatLngResponse(hashMap:HashMap<String,String>)= apiInterface.saveFriendLatLng(hashMap)
    suspend fun getGeofenceListResponse(hashMap:HashMap<String,String>)= apiInterface.geofenceList(hashMap)

    suspend fun getDeleteGeofenceResponse(hashMap:HashMap<String,String>)= apiInterface.deleteGeofence(hashMap)

    suspend fun getNotificationOnOffResponse(hashMap:HashMap<String,String>)= apiInterface.notificationOnOff(hashMap)

    suspend fun getFriendPrivacySettingResponse(hashMap:HashMap<String,String>)= apiInterface.friendPrivacySetting(hashMap)

    suspend fun getAlertHistoryResponse(hashMap:HashMap<String,String>)= apiInterface.alertHistory(hashMap)

    suspend fun getNearbyAlertResponse(hashMap:HashMap<String,String>)= apiInterface.nearbyAlert(hashMap)

    suspend fun getBatteryAlertOnOffResponse(hashMap:HashMap<String,String>)= apiInterface.batteryAlertOnOff(hashMap)

    suspend fun getDeleteFriendResponse(hashMap:HashMap<String,String>)= apiInterface.deleteFriend(hashMap)

    suspend fun getTrackerHistoryResponse(hashMap:HashMap<String,String>)= apiInterface.trackerHistory(hashMap)

}