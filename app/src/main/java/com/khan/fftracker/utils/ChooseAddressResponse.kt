package com.khan.fftracker.utils

data class ChooseAddressResponse(
        val AlertOn: String? = null,
        val GeofenceOn: String? = null,
        val GpsBackgroup: String? = null,
        val GpsLogo: String? = null,
        val GpsTitle: String? = null,
        val HealthOn: String? = null,
        val LocationOn: String? = null,

        val StolenCarPhone: String,
        val `data`: Data,
        val fucntion: String,
        val message: String,
        val success: Int,
        val total: Int,
        val RecallCount: Int,
        val FactoryWarrantyCount:Int,
        var TheftGeofenceEnable:Int
)

data class Data(
        val LowVoltage: String="0",
        val LowVoltageOn: String,
        val MakeID: String,
        val MaxMileage: String,
        val Mileage: String="0",
        val MileageEmail: Any,
        val MileageOn: String,
        val MileagePhone: Any,
        val MinMileage: String,
        val ModelID: String,
        val Movement: String,
        val MovementEmail: Any,
        val MovementOn: String,
        val MovementPhone: Any,
        @JvmField val Speed: String,
        val SpeedEmail: Any,
        val SpeedOn: String,
        val SpeedPhone: Any,
        val TradeID: String,
        val VIN: String,
        val VehYear: String,
        val VoltageEmail: Any,
        val VoltagePhone: Any,
        val address: Any? = null,
        val currentmileage: String="0",
        val currentstatus: String,
        val date: String,
        val disabled: Any,
        val disableddate: Any,
        val distance: String,
        val heading: Any,
        var lastmovement: String,
        var lat: String,
        var lng: String,
        val make: String,
        val model: String,
        val name: Any,
        val serial: String,
        val speed: String="0",
        val typeId: String,
        val vin: String,
        val volts: String="0",
        val year: String
)