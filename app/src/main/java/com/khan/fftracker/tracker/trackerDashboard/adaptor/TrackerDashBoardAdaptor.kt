package com.khan.fftracker.tracker.trackerDashboard.adaptor

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.khan.fftracker.R
import com.khan.fftracker.RecylerViewClicked.RecyclerViewItemListener
import com.khan.fftracker.databinding.TrackerDashboardItemBinding
import com.khan.fftracker.tracker.trackerDashboard.dataModel.Contract

class TrackerDashBoardAdaptor(activit: FragmentActivity, list: List<Contract>, itemListener: RecyclerViewItemListener) : RecyclerView.Adapter<TrackerDashBoardAdaptor.MyViewHolder>() {
    private val activity: Activity
    private val list: List<Contract>

    private val itemListener: RecyclerViewItemListener
    private var clickedId = 0
    private var pos = 0

    private var strOrientation = "vertical"

    init {
        activity = activit
        this.list = list

        this.itemListener = itemListener
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
        val binding = TrackerDashboardItemBinding.inflate(LayoutInflater.from(viewGroup.context), viewGroup, false)
        return MyViewHolder(binding)
    }

    // D0402066
    //022029
    override fun onBindViewHolder(holder: MyViewHolder, i: Int) {
        val list = list[i]

        holder.bind(list)

        var sizeOfView = ConstraintLayout.LayoutParams.MATCH_PARENT
        holder.binding.layoutConstraint.visibility=View.VISIBLE

        if (strOrientation == "horizontal") {
            sizeOfView = ConstraintLayout.LayoutParams.WRAP_CONTENT
            holder.binding.layoutConstraint.visibility=View.GONE
        }
//        else{
//            sizeOfView = ConstraintLayout.LayoutParams.MATCH_PARENT
//            holder.binding.layoutConstraint.visibility=View.VISIBLE
//        }
        val params = ConstraintLayout.LayoutParams(sizeOfView, ConstraintLayout.LayoutParams.WRAP_CONTENT)
        holder.binding.cl.layoutParams = params
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setOrientation(s: String) {
        strOrientation = s

    }


    inner class MyViewHolder(var binding: TrackerDashboardItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(mList: Contract) {
            binding.list = mList
            binding.strOrientation = strOrientation
        }

        init {
            binding.root.setOnClickListener { view ->

                pos = bindingAdapterPosition
                clickedId = view.id
                itemListener.onItemClickObject(clickedId, list[pos], pos)
            }
        }
    }
}