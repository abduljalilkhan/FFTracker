package com.khan.fftracker.mvvmUtils.custom_binding_adaptor

import android.widget.Spinner
import androidx.databinding.BindingAdapter
import com.khan.fftracker.Adaptor_MYPCP.ContractListCustomAdapter
import com.khan.fftracker.DashBoard.MultipleContract

@BindingAdapter(value = ["app:multipleContractEntries","app:rvItemColor"], requireAll = true)
fun Spinner.setCustomEntries(entries: ArrayList<MultipleContract>?, color: Int) {
    entries?.let {
        // we pass our item list and context to our Adapter.
        val adapter = ContractListCustomAdapter(context, it,color)
        this.adapter = adapter
    }
}