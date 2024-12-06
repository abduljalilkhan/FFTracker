package com.khan.fftracker.tracker.addFriendContactList.adaptor

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.khan.fftracker.logCalls.LogCalls_Debug

import java.util.Locale

class AddFriendContactAdaptor(activit: FragmentActivity, list: List<ContactList>, itemListener: RecyclerViewItemListener) : RecyclerView.Adapter<AddFriendContactAdaptor.MyViewHolder>() {
    private val activity: Activity
    private val list: MutableList<ContactList>
    private val listCopy = ArrayList<ContactList>()
    private val itemListener: RecyclerViewItemListener
    private var clickedId = 0
    private var pos = 0

    init {
        activity = activit
        this.list = list.toMutableList()

        //listCopy=this.list;
        listCopy.addAll(this.list)
        this.itemListener = itemListener
        LogCalls_Debug.d(LogCalls_Debug.TAG, "ShopB_ShowNow_Adaptor" + " " + listCopy.size)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val binding = AddfriendContactlistItemBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, i: Int) {
        val list = list[i]

        holder.bind(list)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun filter(text: String) {
        var text = text
        LogCalls_Debug.d(LogCalls_Debug.TAG, text + " " + listCopy.size)
        list.clear()
        if (text.isEmpty()) {
            list.addAll(listCopy)
        } else {
            text = text.lowercase(Locale.getDefault())
            for (item in listCopy) {
                if (item.name.lowercase(Locale.getDefault()).contains(text)) {
                    list.add(item)
                }
            }
        }
        notifyDataSetChanged()
    }

    fun setFilterList() {
        listCopy.clear()
        listCopy.addAll(list)
    }

    inner class MyViewHolder(var binding: AddfriendContactlistItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mList: ContactList) {
            binding.list=mList
        }

        init {
            binding.btnInvite.setOnClickListener { view ->

                pos = bindingAdapterPosition
                clickedId = view.id
                itemListener.onItemClickObject(clickedId, list[pos], pos)
            }
        }
    }
}