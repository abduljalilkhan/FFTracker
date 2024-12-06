package com.khan.fftracker.tracker.trackerHistory.dataModel

data class TrackerHistoryResponse(
    val activities: List<TrackerActivityResponse>,
    val message: String,
    val success: Int
)