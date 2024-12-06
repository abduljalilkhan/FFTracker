package com.khan.fftracker.tracker.friendsDetail.dataModel

import com.khan.fftracker.tracker.trackerDashboard.dataModel.Contract

data class FriendLatLngResponse(
        var contracts: Contract?=null,
        var message: String="",
        var success: Int=0
)

