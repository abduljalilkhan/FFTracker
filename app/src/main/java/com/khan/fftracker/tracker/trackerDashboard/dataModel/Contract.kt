package com.khan.fftracker.tracker.trackerDashboard.dataModel

data class Contract(
    var ID: String = "",
    var ContractID: String = "",
    var ContractNo: String = "",
    var IsGuest: String = "",
    var CustomerFName: String = "",
    var CustomerImage: String = "",
    var CustomerLName: String = "",
    var LowBatteryNotification: String = "",
    var Make: String = "",
    var Model: String = "",
    var PhoneHome: String = "",
    var PrimaryEmail: String = "",
    var PrivacySetting: String = "",
    var VIN: String = "",
    var VehYear: String = "",
    var lat: String = "",
    var lng: String = "",
    var Battery: String? = "0",
    var AddedDate: String = "",
    var distance: String = "",
    var places_register: String = "",
    var IsGpsContract:String=""
)
{
    override fun toString(): String {
//        if(IsGpsContract.equals("1")) {
//            return "$Make $Model $VehYear"
//        }
//        else{
            return "$CustomerFName $CustomerLName"

      //  }
    }
}


