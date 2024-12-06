package com.khan.fftracker.tracker.alertSetting.placeAlerts.placeHistory.placeAdaptor

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.khan.fftracker.RecylerViewClicked.RecyclerViewItemListener
import com.khan.fftracker.databinding.TrackerAlertHistoryItemBinding
import com.khan.fftracker.tracker.alertSetting.placeAlerts.placeHistory.dataModel.Notification


class AlertHistoryTrackerAdaptor (activit: FragmentActivity, list: List<Notification>, itemListener: RecyclerViewItemListener) :
        RecyclerView.Adapter<AlertHistoryTrackerAdaptor.MyViewHolder>() {
    private val activity: Activity
    private val list: List<Notification>

    private val itemListener: RecyclerViewItemListener
    private var clickedId = 0
    private var pos = 0

    var clickedPos=-1
    var currentPos=-1
    var headerValue=""
    init {
        activity = activit
        this.list = list

        this.itemListener = itemListener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val binding = TrackerAlertHistoryItemBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, i: Int) {
        val list = list[i]

        holder.bind(list,i)
    }

    override fun getItemCount(): Int {
        return list.size
    }


    inner class MyViewHolder(var binding: TrackerAlertHistoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mList: Notification, i: Int) {
            binding.list=mList

           // if (i!=0) {
                headerValue = if (mList.Date == headerValue) "" else mList.Date
           // }
            binding.headerValue=headerValue


            currentPos=i
            binding.clickedPos=clickedPos
            binding.currentPos=currentPos

        }

        init {
            binding.root.setOnClickListener { view ->

                pos = bindingAdapterPosition
                clickedId = view.id
                itemListener.onItemClickObject(clickedId, list[pos], pos)
                clickedPos=pos
            }
        }
    }
}