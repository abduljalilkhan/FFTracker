package com.khan.fftracker.tracker.trackerDashboard.dataModel

data class TrackerDashFriendResponse(
        val contracts: List<Contract>,
        val pending: List<Contract>,
        val InviteURL: String,
        val NearbyDistance: String,
        val EnableNearbyAlert: String,
        val success: Int,
        val message: String
)