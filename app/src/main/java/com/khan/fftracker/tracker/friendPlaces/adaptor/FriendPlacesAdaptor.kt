package com.khan.fftracker.tracker.friendPlaces.adaptor

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.khan.fftracker.RecylerViewClicked.RecyclerViewItemListener
import com.khan.fftracker.databinding.TrackerfriendplacesItemBinding
import com.khan.fftracker.tracker.friendPlaces.dataModel.FriendPlaceItem

class FriendPlacesAdaptor(activit: FragmentActivity, list: List<FriendPlaceItem>, itemListener: RecyclerViewItemListener) : RecyclerView.Adapter<FriendPlacesAdaptor.MyViewHolder>() {
    private val activity: Activity
    private val list: MutableList<FriendPlaceItem>

    private val itemListener: RecyclerViewItemListener
    private var clickedId = 0
    private var pos = 0

    init {
        activity = activit
        this.list = list.toMutableList()

        this.itemListener = itemListener

    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val binding = TrackerfriendplacesItemBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, i: Int) {
        val list = list[i]

        holder.bind(list)
    }

    override fun getItemCount(): Int {
        return list.size
    }


    inner class MyViewHolder(var binding: TrackerfriendplacesItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mList: FriendPlaceItem) {
          //  binding.list=mList
        }

        init {
            binding.imgAlarm.setOnClickListener { view ->

                pos = bindingAdapterPosition
                clickedId = view.id
                itemListener.onItemClickObject(clickedId, list[pos], pos)
            }
        }
    }
}