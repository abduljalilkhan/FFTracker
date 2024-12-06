package com.khan.fftracker.tracker.friendPlaces.dataModel

data class FriendPlacesData(
        val list:List<FriendPlaceItem>,
        val success: Int,
        val message: String)