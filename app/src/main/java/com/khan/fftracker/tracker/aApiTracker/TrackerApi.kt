package com.khan.fftracker.tracker.aApiTracker

import com.khan.fftracker.Network_Volley.Network_Stuffs
import com.khan.fftracker.utils.ChooseAddressResponse
import com.khan.fftracker.utils.GenericResponse
import com.khan.fftracker.tracker.addFriendContactList.dataModel.InviteURLResponse
import com.khan.fftracker.tracker.alertSetting.placeAlerts.placeHistory.dataModel.AlertHistoryResponse
import com.khan.fftracker.tracker.friendPlaces.dataModel.TrackerGeofenceListResponse
import com.khan.fftracker.tracker.friendsDetail.dataModel.FriendLatLngResponse
import com.khan.fftracker.tracker.landing.dataModel.SliderResponse
import com.khan.fftracker.tracker.trackerDashboard.dataModel.TrackerDashFriendResponse
import com.khan.fftracker.tracker.trackerHistory.dataModel.TrackerHistoryResponse
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.HeaderMap
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PartMap
import retrofit2.http.Url

interface TrackerApi {


    @FormUrlEncoded
    @POST
    suspend fun getAnyResponse(@HeaderMap headers: HashMap<String, String>, @Url url: String, @FieldMap hashMap: HashMap<String, String>): Response<GenericResponse>

    @Multipart
    @POST
    suspend fun uploadImage(@Url url:String, @PartMap params: MutableMap<String, RequestBody>, @PartMap files: MutableMap<String, RequestBody>): Response<GenericResponse>

    @FormUrlEncoded
    @POST("v/"+ Network_Stuffs.URL_VERSION +"/gps/checkserialno")
    suspend fun checkSerialNo(@FieldMap hashMap: HashMap<String, String>): Response<ChooseAddressResponse>

    @FormUrlEncoded
    @POST("v/"+ Network_Stuffs.URL_VERSION +"/familytracker/getstarted")
    suspend fun sliderResponse(@FieldMap hashMap: HashMap<String, String>): Response<SliderResponse>
    @FormUrlEncoded
    @POST("v/"+ Network_Stuffs.URL_VERSION +"/familytracker/findcontract")
    suspend fun inviteLinkResponse(@FieldMap hashMap: HashMap<String, String>): Response<InviteURLResponse>

    @FormUrlEncoded
    @POST("v/"+ Network_Stuffs.URL_VERSION +"/familytracker/avfriends")
    suspend fun friendListResponse(@FieldMap hashMap: HashMap<String, String>): Response<TrackerDashFriendResponse>

    @FormUrlEncoded
    @POST("v/"+ Network_Stuffs.URL_VERSION +"/familytracker/friendslatbaterydetails")
    suspend fun getFriendLatLng(@FieldMap hashMap: HashMap<String, String>): Response<FriendLatLngResponse>

    @FormUrlEncoded
    @POST("v/"+ Network_Stuffs.URL_VERSION +"/familytracker/savelatbatery")
    suspend fun saveFriendLatLng(@FieldMap hashMap: HashMap<String, String>): Response<GenericResponse>

    @FormUrlEncoded
    @POST("v/"+ Network_Stuffs.URL_VERSION +"/familytracker/geofence")
    suspend fun geofenceList(@FieldMap hashMap: HashMap<String, String>): Response<TrackerGeofenceListResponse>

    @FormUrlEncoded
    @POST("v/"+ Network_Stuffs.URL_VERSION +"/familytracker/deletegeofence")
    suspend fun deleteGeofence(@FieldMap hashMap: HashMap<String, String>): Response<GenericResponse>

    @FormUrlEncoded
    @POST("v/"+ Network_Stuffs.URL_VERSION +"/familytracker/notificationonoff")
    suspend fun notificationOnOff(@FieldMap hashMap: HashMap<String, String>): Response<GenericResponse>

    @FormUrlEncoded
    @POST("v/"+ Network_Stuffs.URL_VERSION +"/Familytracker/privacysetting")
    suspend fun friendPrivacySetting(@FieldMap hashMap: HashMap<String, String>): Response<GenericResponse>

    @FormUrlEncoded
    @POST("v/"+ Network_Stuffs.URL_VERSION +"/familytracker/alerthistory")
    suspend fun alertHistory(@FieldMap hashMap: HashMap<String, String>): Response<AlertHistoryResponse>

    @FormUrlEncoded
    @POST("v/"+ Network_Stuffs.URL_VERSION +"/familytracker/updatenearbyalert")
    suspend fun nearbyAlert(@FieldMap hashMap: HashMap<String, String>): Response<GenericResponse>

    @FormUrlEncoded
    @POST("v/"+ Network_Stuffs.URL_VERSION +"/familytracker/batteryalertononoff")
    suspend fun batteryAlertOnOff(@FieldMap hashMap: HashMap<String, String>): Response<GenericResponse>

    @FormUrlEncoded
    @POST("v/"+ Network_Stuffs.URL_VERSION +"/familytracker/deleteavfriend")
    suspend fun deleteFriend(@FieldMap hashMap: HashMap<String, String>): Response<GenericResponse>

    @FormUrlEncoded
    @POST("v/"+ Network_Stuffs.URL_VERSION +"/familytracker/gpsactivity")
    suspend fun trackerHistory(@FieldMap hashMap: HashMap<String, String>): Response<TrackerHistoryResponse>

}