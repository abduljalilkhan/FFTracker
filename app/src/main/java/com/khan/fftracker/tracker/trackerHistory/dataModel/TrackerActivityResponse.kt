package com.khan.fftracker.tracker.trackerHistory.dataModel

data class TrackerActivityResponse(
    val coordinates: List<Coordinate>,
    val datetime: String,
    val duration: String,
    val enddatetime: String,
    val mapimage: String,
    val mode: String,
    val startdatetime: String,
    val totaldistance: String
)