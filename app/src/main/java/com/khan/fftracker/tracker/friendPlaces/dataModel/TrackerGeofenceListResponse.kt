package com.khan.fftracker.tracker.friendPlaces.dataModel

data class TrackerGeofenceListResponse(
        val IsHome: Int,
        val IsSchool: Int,
        val IsWork: Int,
        val fucntion: String,
        val geofencelists: List<Geofencelists>,
        val success: Int,
        val places_register: Int
)