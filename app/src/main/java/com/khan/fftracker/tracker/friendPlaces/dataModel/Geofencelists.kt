package com.khan.fftracker.tracker.friendPlaces.dataModel
data class Geofencelists(
        val GeoTitle: String,
        val GeofenctID: String,
        val Image: String,
        val IsHome: String,
        val IsSchool: String,
        val IsWork: String,
        var NotificationOn: String,
        val Radious: String,
        val isActive: String,
        val lat: String,
        val lng: String,
        val totalnotification: String
){
    override fun hashCode(): Int {
        return GeofenctID.toInt()
    }
}



