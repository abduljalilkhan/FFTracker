package com.khan.fftracker.tracker.trackerDashboard.dataModel

data class Pending(
    var ContractID: String="",
    var ContractNo: String="",
    var IsGuest:String="",
    var CustomerFName: String="",
    var CustomerImage: String="",
    var CustomerLName: String="",
    var LowBatteryNotification: String="",
    var Make: String="",
    var Model: String="",
    var PhoneHome: String="",
    var PrimaryEmail: String="",
    var PrivacySetting: String="",
    var VIN: String="",
    var VehYear: String="",
    var lat: String="",
    var lng: String="",
    var Battery: String? ="0",
    var AddedDate:String="",
    var distance:String="",
    var places_register:String=""
)