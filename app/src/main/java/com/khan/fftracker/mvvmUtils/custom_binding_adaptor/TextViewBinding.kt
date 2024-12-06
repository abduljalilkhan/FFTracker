package com.khan.fftracker.mvvmUtils.custom_binding_adaptor

import android.app.Activity
import android.location.Address
import android.location.Geocoder
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.khan.fftracker.logCalls.LogCalls_Debug

import java.util.Locale
import java.util.concurrent.Executors


@BindingAdapter(value = ["app:clickableText", "app:unClickableText", "app:clickableColor", "app:unClickableColor"], requireAll = true)
fun singleTVPrivacy(tv: TextView, clickableText: String, unClickableText: String, clickableColor: Int, unClickableColor: Int) {
    LogCalls_Debug.d(LogCalls_Debug.TAG, "singleTVPrivacy")
    val mContext = tv.context as Activity
    Terms_Policy(mContext).singleTVPrivacy(
            tv,
            unClickableText,
            clickableText,
            unClickableColor,
            clickableColor
    )

}

/*return value with two decimal points
eg 20.00, .00 is actually "%.2f"*/
@BindingAdapter("app:textDecimal")
fun setTwoDecimalDollarTv(tv: TextView, tvText: Double) {

    val mContext = tv.context as Activity
    val commaSeparatedString = Comma_Separated_String(mContext)
    tv.text = String.format("$%s", commaSeparatedString.twoValueFormat(tvText))
}

/*return value with two decimal points
eg 20.00, .00 is actually "%.2f"*/
@BindingAdapter(value = ["app:tvDateFormat","app:inputDateFormat", "app:outputDateFormat"], requireAll = true)
fun setDateFormatTv(tv: TextView, tvText: String, tvInputDate: String, tvOutputDate: String) {

    tv.text= Convert_Date().parseFormatConvert(
        tvText,
        tvInputDate,
        tvOutputDate
    )
}

@Suppress("DEPRECATION")
fun Geocoder.getAddress(latitude: Double, longitude: Double, address: (Address?) -> Unit) {


    val executor = arrayOf(Executors.newSingleThreadExecutor())
    val handler = Handler(Looper.getMainLooper())

    executor[0].execute {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            getFromLocation(latitude, longitude, 1) { address(it.firstOrNull()) }

            return@execute
        }
        try {
            address(getFromLocation(latitude, longitude, 1)?.firstOrNull())
        } catch (e: Exception) {
            //will catch if there is an internet problem
            address(null)
        }

    }
}


//UI Thread work here// Here 1 represent max location result to returned, by documents it recommended 1 to 5

// If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
////Get address from lat lng
@BindingAdapter(value = ["app:txtLat", "app:txtLng", "app:addressName", "app:otherText"], requireAll = false)
fun setLocationAddress(
        tv: TextView,
        latitude: String,
        longitude: String,
        addressNameFromLatLng: String?,
        otherText: String?

) {
    if (latitude.isNotBlank() && longitude.isNotBlank()) {
        val handler = Handler(Looper.getMainLooper())
        Geocoder(tv.context, Locale.getDefault()).getAddress(
            latitude.toDouble(),
            longitude.toDouble()
        ) { address: Address? ->
            if (address != null) {
                //do your logic
                handler.post {
                    tv.text = address.getAddressLine(0).replace(address.postalCode+",","").replace(address.countryName,"")


                    if (addressNameFromLatLng != null) {
                        if (addressNameFromLatLng == "subLocal") {
                            LogCalls_Debug.d(TAG, "subLocal " + address.locality)
                            tv.text = address.locality.toString()
                        }
                        if (addressNameFromLatLng == "fullAddress"){
                           // tv.text = address.getAddressLine(0)
                        }
                    }
                    if (otherText != null) {
                        tv.text = otherText
                    }
                }

                LogCalls_Debug.d(TAG, address.toString() + " onGeocode size ")
            }
        }
    }


//      val geocoder = Geocoder(tv.context, Locale.getDefault())
//
//    val executor = arrayOf(Executors.newSingleThreadExecutor())
//    //val handler = Handler(Looper.getMainLooper())
//
//    val address = arrayOfNulls<String>(1)
//    val listAddress: ArrayList<Address> = ArrayList() // = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
//
//
//    executor[0].execute {
//        try {
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//                geocoder.getFromLocation(latitude.toDouble(), longitude.toDouble(), 1, object : GeocodeListener {
//                    override fun onGeocode(addresse: List<Address>) {
//                        listAddress.addAll(addresse)
//                        //LogCalls_Debug.d(TAG, listAddress.size.toString() + " onGeocode size ")
//                    }
//
//                    override fun onError(errorMessage: String?) {
//                        super<GeocodeListener>.onError(errorMessage)
//                    }
//                })
//            }
//            else{
//                var addresses = geocoder.getFromLocation(latitude.toDouble(), longitude.toDouble(), 1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
//                address[0] = addresses!![0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//
//            }
//
//            val addresses = geocoder.getFromLocation(latitude.toDouble(), longitude.toDouble(), 1) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
//            address[0] = addresses!![0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
//            //addresses.addAll(addresses)
//            listAddress.addAll(geocoder.getFromLocation(33.673293, 73.077647, 1)!!)
//
//        } catch (e: java.lang.Exception) {
//            e.printStackTrace()
//        }
//        handler.post {
//            //UI Thread work here
//            // binding.tvAddress.setText(address[0]);
//            tv.text = address[0]
//
//////            LogCalls_Debug.d(TAG, listAddress.size.toString() + " size ")
//            LogCalls_Debug.d(TAG, listAddress[0].getAddressLine(0).toString() + " getAddressLine ")
//            LogCalls_Debug.d(TAG, listAddress[0].subLocality.toString() + " subLocality ")
//        }
 //   }
    ///////////////////////////////////////////////////////////////////////////////////////////


}




