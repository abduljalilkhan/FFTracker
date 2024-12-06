package com.khan.fftracker.tracker.landing.adapter


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.PagerAdapter
import com.khan.fftracker.R
import com.khan.fftracker.databinding.TrackerLandingPagerItemBinding
import com.khan.fftracker.tracker.landing.dataModel.Sliding
import java.security.AccessController.getContext


class TrackerLandingPagerAdaptor(private val mContext: Context, private val listSlider: List<Sliding>) : PagerAdapter() {
    private val mLayoutInflater: LayoutInflater

    init {
        mLayoutInflater = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getCount(): Int {
        return listSlider.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object` as ConstraintLayout
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView: View = mLayoutInflater.inflate(R.layout.tracker_landing_pager_item, container, false)

        val binding:TrackerLandingPagerItemBinding = DataBindingUtil.inflate(mLayoutInflater, R.layout.tracker_landing_pager_item, container, false)

        binding.root.tag=position
        binding.list=listSlider[position]
        container.addView(binding.root)

        return binding.getRoot();
/////////////////////////////////////////////////////////////////////////



//////////////////////////////////////////////////////////////////////////



//        val imgCenter: ImageView = itemView.findViewById<View>(R.id.imgLandingCenter) as ImageView
//        val tvTitle = itemView.findViewById<View>(R.id.tvLandingTitle) as TextView
//        val tvDesc = itemView.findViewById<View>(R.id.tvLandingDesc) as TextView
//
//
//        //set data to textview
//        tvTitle.text = listSlider[position].Title
//        tvDesc.text = listSlider[position].Description
//        // imgLogo = (ImageView) itemView.findViewById(R.id.imgLandingfst);
//
//        //show shimmer animation while image is loading
//        Glide.with(mContext).load(listSlider[position].Logo).into(imgCenter)
//
//        container.addView(itemView)
       // return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as ConstraintLayout)
    } //        @Override
    //        public CharSequence getPageTitle (int position) {
    //            return "Your static title";
    //        }
}