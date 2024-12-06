package com.khan.fftracker.tracker.userMenuSetting.friendSettings.friendAdaptor

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.khan.fftracker.logCalls.LogCalls_Debug
import com.khan.fftracker.Navigation_Drawer.Get_Visible_Fragment
import com.khan.fftracker.RecylerViewClicked.RecyclerViewItemListener
import com.khan.fftracker.databinding.TrackerfriendlistSettingitemBinding
import com.khan.fftracker.tracker.alertSetting.batteryAlert.BatteryAlert
import com.khan.fftracker.tracker.trackerDashboard.dataModel.Contract


class FriendListSettingAdaptor (activit: FragmentActivity, list: List<Contract>, itemListener: RecyclerViewItemListener) :
    RecyclerView.Adapter<FriendListSettingAdaptor.MyViewHolder>() {
    private val activity: Activity
    private val list: List<Contract>

    private val itemListener: RecyclerViewItemListener
    private var clickedId = 0
    private var pos = 0

    var clickedPos=-1
    var currentPos=-1
    var lastFragment=""
    var fragment: Fragment? = null
    init {
        activity = activit
        this.list = list
        fragment = Get_Visible_Fragment(activity).visibleFragment
        this.itemListener = itemListener

        if (fragment is BatteryAlert) {
            lastFragment="BatteryAlert"
            LogCalls_Debug.d("json adaptor", "BatteryAlert")
            // Logic here...
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val binding = TrackerfriendlistSettingitemBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, i: Int) {
        val list = list[i]

        holder.bind(list,i)
    }

    override fun getItemCount(): Int {
        return list.size
    }
    fun updateListUploadImage(updateListUploadImage: String?) {
        list[clickedPos].CustomerImage=updateListUploadImage!!
        notifyItemChanged(clickedPos)
    }
    fun updatePlaceRegistered(updatePlaceRegistered: String?) {
        list[clickedPos].places_register=updatePlaceRegistered!!
        notifyItemChanged(clickedPos)
    }
    inner class MyViewHolder(var binding: TrackerfriendlistSettingitemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mList: Contract, i: Int) {
            binding.list=mList

            currentPos=i

        }

        init {
            binding.lastFragment=lastFragment
            binding.root.setOnClickListener { view ->

                pos = bindingAdapterPosition
                clickedId = view.id
                itemListener.onItemClickObject(clickedId, list[pos], pos)
                clickedPos=pos
            }

            ///switch check listener
            binding.switchNotification.setOnCheckedChangeListener { compoundButton, b ->

                if (compoundButton.isPressed) {
                    pos = bindingAdapterPosition

                    list[pos].LowBatteryNotification = if (b) "1" else "0"
                    notifyDataSetChanged()

                    clickedId = compoundButton.id
                    itemListener.onItemClickObject(clickedId, list[pos], pos)
                }

            }
        }
    }
}