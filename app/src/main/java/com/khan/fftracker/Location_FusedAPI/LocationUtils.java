package com.khan.fftracker.Location_FusedAPI;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class LocationUtils {
    private static final double MIN_DISTANCE = 10.0; // Minimum distance in meters

    public LocationUtils(){

    }

    //calculate distance between two lat long
    public double calculateDistanceTwoLatLong(LatLng latLngStart, LatLng latLngEnd ) {
        Location startPoint = new Location("locationA");
        startPoint.setLatitude(latLngStart.latitude);
        startPoint.setLongitude(latLngStart.longitude);

        Location endPoint = new Location("locationA");
        endPoint.setLatitude(latLngEnd.latitude);
        endPoint.setLongitude(latLngEnd.longitude);

        double distanceMeter = startPoint.distanceTo(endPoint);
        double distance = startPoint.distanceTo(endPoint) * 0.00062137119;

       // LogCalls_Debug.d("current", distanceMeter + " miles " + distance);
        return distanceMeter;
    }


    //add item to arrayList containing locations 10 meters apart from each other
    public List<LatLng> filterLocations(List<LatLng> latLngs) {
        ArrayList<LatLng> filteredLocations = new ArrayList<>();

        // Add the first location to the filtered list
        filteredLocations.add(latLngs.get(0));

        // Iterate through the remaining locations
        for (int i = 1; i < latLngs.size(); i++) {
              double lat = latLngs.get(i).latitude;
              double lng = latLngs.get(i).longitude;


            double distanceMeter = calculateDistanceTwoLatLong(filteredLocations.get(filteredLocations.size()-1), new LatLng(lat, lng));
            if (distanceMeter >= 30){
                filteredLocations.add(new LatLng(lat, lng));
            }
        }

        return filteredLocations;
    }

    private void setLatLngFromResponse() {

//            for (latlng in 0 until latLngCoordinates.size) {
//                val lat: Double = latLngCoordinates[latlng].lat.toDouble()
//                val lng: Double = latLngCoordinates[latlng].lng.toDouble()
//
//                // latlng ++
//
//                if (polylinePoints!!.isNotEmpty()) {
//                    val distanceMeter= locationUtils.calculateDistanceTwoLatLong(polylinePoints!![polylinePoints!!.size-1],
//                    LatLng(lat, lng)
//                    )
//                    if (distanceMeter >= 10){
//                        polylinePoints!!.add(LatLng(lat, lng))
//                    }
//                }
//                else{
//                    polylinePoints!!.add(LatLng(lat, lng))
//                }
//
//            }

    }
}
