package com.khan.fftracker.tracker.userMenuSetting.adaptor

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.khan.fftracker.RecylerViewClicked.RecyclerViewItemListener
import com.khan.fftracker.databinding.TrackerUsermenuItemBinding

class UserMenuAdaptor(activit: FragmentActivity, list: LiveData<MutableList<String>>, listImg: LiveData<MutableList<Int>>, itemListener: RecyclerViewItemListener) :
    RecyclerView.Adapter<UserMenuAdaptor.MyViewHolder>() {

    private val activity: Activity
    private val list: LiveData<MutableList<String>>
    private val listImg: LiveData<MutableList<Int>>

    private val itemListener: RecyclerViewItemListener
    private var clickedId = 0
    private var pos = 0

    init {
        activity = activit
        this.list = list
        this.listImg = listImg

        this.itemListener = itemListener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val binding = TrackerUsermenuItemBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, i: Int) {
        val list = list.value!![i]
        val listImg = listImg.value

        holder.bind(list, listImg!![i])
    }

    override fun getItemCount(): Int {
        return list.value!!.size
    }


    inner class MyViewHolder(var binding: TrackerUsermenuItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mList: String, listImg: Int) {
            binding.strTitle=mList
            binding.img=listImg
        }

        init {
            binding.root.setOnClickListener { view ->

                pos = bindingAdapterPosition
                clickedId = view.id
                itemListener.onItemClickObject(clickedId, list.value!![pos], pos)
            }
        }
    }
}