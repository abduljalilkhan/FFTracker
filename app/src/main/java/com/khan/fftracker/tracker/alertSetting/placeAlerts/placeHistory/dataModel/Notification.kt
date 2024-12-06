package com.khan.fftracker.tracker.alertSetting.placeAlerts.placeHistory.dataModel

data class Notification(
    val ContractNo: String,
    val CustomerFName: String,
    val CustomerLName: String,
    val Date: String,
    val Message: String,
    val NotificationTime: String,
    val NotificationType: String
)