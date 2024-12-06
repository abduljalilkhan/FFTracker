package com.khan.fftracker.tracker.trackerHistory.adaptor

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.khan.fftracker.Location_FusedAPI.LocationUtils
import com.khan.fftracker.RecylerViewClicked.RecyclerViewItemListener
import com.khan.fftracker.databinding.TrackerhistoryItemBinding
import com.khan.fftracker.tracker.trackerHistory.dataModel.Coordinate
import com.khan.fftracker.tracker.trackerHistory.dataModel.TrackerActivityResponse


class TrackerHistoryAdaptor(activit: FragmentActivity, list: MutableList<TrackerActivityResponse>, itemListener: RecyclerViewItemListener) : RecyclerView.Adapter<TrackerHistoryAdaptor.MyViewHolder>() {
    private val activity: Activity
    private val list: List<TrackerActivityResponse>

    private val itemListener: RecyclerViewItemListener
    private var clickedId = 0
    private var pos = 0

    //This call has all location related setting i.e find distance between two point
    private lateinit var locationUtils: LocationUtils
    init {
        activity = activit
        this.list = list

        this.itemListener = itemListener

         locationUtils = LocationUtils()
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val binding = TrackerhistoryItemBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return MyViewHolder(binding)
    }

    // D0402066
    //022029
    override fun onBindViewHolder(holder: MyViewHolder, i: Int) {
        val list = list[i]

        holder.bind(list)
    }

    override fun getItemCount(): Int {
        return list.size
    }


    inner class MyViewHolder(var binding: TrackerhistoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mList: TrackerActivityResponse) {
            binding.list = mList
            binding.isShowHide=setVisibilityTrackerLayout(mList.coordinates)
        }

        init {
            binding.root.setOnClickListener { view ->

                pos = bindingAdapterPosition
                clickedId = view.id
                itemListener.onItemClickObject(clickedId, list[pos], pos)
            }
        }
    }

    fun setVisibilityTrackerLayout( latLngCoordinates: List<Coordinate>): Boolean {
        val polylinePoints: MutableList<LatLng> = ArrayList()

        for (latlng in 0 until latLngCoordinates.size) {
            val lat: Double = latLngCoordinates[latlng].lat.toDouble()
            val lng: Double = latLngCoordinates[latlng].lng.toDouble()

            if (polylinePoints.isNotEmpty()) {
                val distanceMeter= locationUtils.calculateDistanceTwoLatLong(
                    polylinePoints[polylinePoints.size-1],
                    LatLng(lat, lng)
                )
                if (distanceMeter>10){
                    return true
                }
            }
            else{
                polylinePoints.add(LatLng(lat, lng))
            }

        }
       return false
    }
}