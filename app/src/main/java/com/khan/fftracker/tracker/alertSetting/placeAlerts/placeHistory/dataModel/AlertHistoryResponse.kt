package com.khan.fftracker.tracker.alertSetting.placeAlerts.placeHistory.dataModel

data class AlertHistoryResponse(
    val message: String,
    val notifications: List<Notification>,
    val success: Int
)