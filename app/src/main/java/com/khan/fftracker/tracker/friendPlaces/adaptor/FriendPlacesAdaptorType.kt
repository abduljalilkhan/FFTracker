package com.khan.fftracker.tracker.friendPlaces.adaptor

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.khan.fftracker.RecylerViewClicked.RecyclerViewItemListener
import com.khan.fftracker.databinding.TrackerfriendplacesItemBinding
import com.khan.fftracker.databinding.TrackerfrndplaceHeaderBinding
import com.khan.fftracker.tracker.friendPlaces.dataModel.Geofencelists

class FriendPlacesAdaptorType(activit: FragmentActivity, list: List<Geofencelists>, itemListener: RecyclerViewItemListener) : RecyclerView.Adapter<FriendPlacesAdaptorType.MyViewHolder?>() {
    private val activity: Activity
    private val list: List<Geofencelists>

    private val itemListener: RecyclerViewItemListener
    private var clickedId = 0
    private var pos = 0

    init {
        activity = activit
        this.list = list

        this.itemListener = itemListener

    }

    override fun getItemViewType(position: Int): Int {
        return if (position == list.size) {
            CELL_TYPE_HEADER
        } else {
            CELL_TYPE_REGULAR_ITEM
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): MyViewHolder {
        val binding: ViewDataBinding
        when (viewType) {
            CELL_TYPE_HEADER -> {
                binding = TrackerfrndplaceHeaderBinding.inflate(
                    LayoutInflater.from(viewGroup.context),
                    viewGroup,
                    false
                )
                return MyViewHolder(binding)
            }

            CELL_TYPE_REGULAR_ITEM -> {
                binding = TrackerfriendplacesItemBinding.inflate(
                    LayoutInflater.from(viewGroup.context),
                    viewGroup,
                    false
                )
                return MyViewHolder(
                    binding
                )
            }
        }
        binding = TrackerfriendplacesItemBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )
        return MyViewHolder(binding)
       // return null
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        //MyViewModel viewModel = new MyViewModel(getItem(position));
        when (holder.itemViewType) {
            CELL_TYPE_REGULAR_ITEM -> {
                val list = list[position]
                holder.bind(list)
            }
              //  var headerBinding: TrackerfrndplaceHeaderBinding?= holder.headerBinding
           // CELL_TYPE_HEADER ->
               // var regularItemBinding: TrackerfriendplacesItemBinding?= holder.regularItemBinding
        }
    }


    override fun getItemCount(): Int {
        return list.size+1
    }

    inner class MyViewHolder : RecyclerView.ViewHolder, View.OnClickListener {
        var headerBinding: TrackerfrndplaceHeaderBinding? = null
        var regularItemBinding: TrackerfriendplacesItemBinding? = null

        fun bind(mList: Geofencelists) {
            regularItemBinding!!.list=mList
        }
        constructor(binding: TrackerfrndplaceHeaderBinding) : super(binding.root) {
            headerBinding = binding
            headerBinding!!.root.setOnClickListener { view ->

                pos = bindingAdapterPosition
                clickedId = view.id
                itemListener.onItemClickObject(clickedId, "addnewGeofence", pos)
            }
        }

        constructor(binding: TrackerfriendplacesItemBinding) : super(binding.root) {
            regularItemBinding = binding
            binding.imgAlarm.setOnClickListener(this)
            binding.root.setOnClickListener(this)
//            regularItemBinding!!.root.setOnClickListener { view ->
//
//                pos = bindingAdapterPosition
//                clickedId = view.id
//                itemListener.onItemClickObject(clickedId, list[pos], pos)
//            }
        }

        override fun onClick(v: View?) {
            pos = bindingAdapterPosition
            clickedId = v!!.id
            itemListener.onItemClickObject(clickedId, list[pos], pos)
        }

    }

    companion object {
        private const val CELL_TYPE_HEADER = 0
        private const val CELL_TYPE_REGULAR_ITEM = 1
    }
}