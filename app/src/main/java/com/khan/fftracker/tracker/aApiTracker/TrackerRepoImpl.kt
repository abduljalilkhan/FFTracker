package com.khan.fftracker.tracker.aApiTracker

import com.khan.fftracker.logCalls.LogCalls_Debug
import com.khan.fftracker.mvvmData.networkApi.BaseDataSource
import okhttp3.RequestBody
import javax.inject.Inject

class TrackerRepoImpl @Inject constructor(private val apiRepo: TrackerRepo): BaseDataSource() {

    suspend fun getAnyResponse(strUrl: String, hashMap: HashMap<String, String>)=safeApiCall {
        var map: HashMap<String, String> = hashMap
        map= getHashMap(map)

        LogCalls_Debug.d(LogCalls_Debug.TAG,map.toString())
        apiRepo.getAnyResponse(strUrl,map) }

    suspend fun getUploadImageResponse(strFunction: String, hashMap: MutableMap<String, RequestBody>, files: MutableMap<String, RequestBody>)=safeApiCall {

        LogCalls_Debug.d(LogCalls_Debug.TAG,hashMap.toString())
        apiRepo.getUploadImageResponse(strFunction,hashMap,files) }


    suspend fun getSliderResponse(hashMap: HashMap<String,String>)=safeApiCall {
        var map: HashMap<String, String> = hashMap
        map= getHashMap(map)

        LogCalls_Debug.d(LogCalls_Debug.TAG,map.toString())
        apiRepo.getSliderResponse(map) }

    suspend fun getInviteLinkResponse(hashMap: HashMap<String,String>)=safeApiCall {
        var map: HashMap<String, String> = hashMap
        map= getHashMap(map)

        LogCalls_Debug.d(LogCalls_Debug.TAG,map.toString())
        apiRepo.getInviteLinkResponse(map) }
    suspend fun getFriendListResponse(hashMap: HashMap<String,String>)=safeApiCall {
        var map: HashMap<String, String> = hashMap
        map= getHashMap(map)

        LogCalls_Debug.d(LogCalls_Debug.TAG,map.toString())
        apiRepo.getFriendListResponse(map) }


    suspend fun getFriendLatLngResponse(hashMap: HashMap<String,String>)=safeApiCall {
        var map: HashMap<String, String> = hashMap
        map= getHashMap(map)

        LogCalls_Debug.d(LogCalls_Debug.TAG,map.toString())
        apiRepo.getFriendLatLngResponse(map) }

    suspend fun saveFriendLatLngResponse(hashMap: HashMap<String,String>)=safeApiCall {
        var map: HashMap<String, String> = hashMap
        map= getHashMap(map)

        LogCalls_Debug.d(LogCalls_Debug.TAG,map.toString())
        apiRepo.saveFriendLatLngResponse(map) }


    suspend fun getGeofenceListResponse(hashMap: HashMap<String,String>)=safeApiCall {
        var map: HashMap<String, String> = hashMap
        map= getHashMap(map)

        LogCalls_Debug.d(LogCalls_Debug.TAG,map.toString())
        apiRepo.getGeofenceListResponse(map) }


    suspend fun getDeleteGeofenceResponse(hashMap: HashMap<String,String>)=safeApiCall {
        var map: HashMap<String, String> = hashMap
        map= getHashMap(map)

        LogCalls_Debug.d(LogCalls_Debug.TAG,map.toString())
        apiRepo.getDeleteGeofenceResponse(map) }
    suspend fun getNotificationOnOffResponse(hashMap: HashMap<String,String>)=safeApiCall {
        var map: HashMap<String, String> = hashMap
        map= getHashMap(map)

        LogCalls_Debug.d(LogCalls_Debug.TAG,map.toString())
        apiRepo.getNotificationOnOffResponse(map) }

    suspend fun getFriendPrivacySettingResponse(hashMap: HashMap<String,String>)=safeApiCall {
        var map: HashMap<String, String> = hashMap
        map= getHashMap(map)

        LogCalls_Debug.d(LogCalls_Debug.TAG,map.toString())
        apiRepo.getFriendPrivacySettingResponse(map) }
    suspend fun getAlertHistoryResponse(hashMap: HashMap<String,String>)=safeApiCall {
        var map: HashMap<String, String> = hashMap
        map= getHashMap(map)

        LogCalls_Debug.d(LogCalls_Debug.TAG,map.toString())
        apiRepo.getAlertHistoryResponse(map) }

    suspend fun getNearbyAlertResponse(hashMap: HashMap<String,String>)=safeApiCall {
        var map: HashMap<String, String> = hashMap
        map= getHashMap(map)

        LogCalls_Debug.d(LogCalls_Debug.TAG,map.toString())
        apiRepo.getNearbyAlertResponse(map) }


    suspend fun getBatteryAlertOnOffResponse(hashMap: HashMap<String,String>)=safeApiCall {
        var map: HashMap<String, String> = hashMap
        map= getHashMap(map)

        LogCalls_Debug.d(LogCalls_Debug.TAG,map.toString())
        apiRepo.getBatteryAlertOnOffResponse(map) }


    suspend fun getDeleteFriendResponse(hashMap: HashMap<String,String>)=safeApiCall {
        var map: HashMap<String, String> = hashMap
        map= getHashMap(map)

        LogCalls_Debug.d(LogCalls_Debug.TAG,map.toString())
        apiRepo.getDeleteFriendResponse(map) }

    suspend fun getTrackerHistoryResponse(hashMap: HashMap<String,String>)=safeApiCall {
        var map: HashMap<String, String> = hashMap
        map= getHashMap(map)

        LogCalls_Debug.d(LogCalls_Debug.TAG,map.toString())
        apiRepo.getTrackerHistoryResponse(map) }

}

