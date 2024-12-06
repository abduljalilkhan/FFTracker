package com.khan.fftracker.mvvmUtils.custom_binding_adaptor

import android.view.View
import androidx.databinding.BindingAdapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.khan.fftracker.logCalls.LogCalls_Debug
import com.khan.fftracker.logCalls.LogCalls_Debug.TAG


@BindingAdapter("bottomSheetBehaviorState")
fun setState(v: View, showHideView: View) {
    val viewBottomSheetBehavior: BottomSheetBehavior<View> = BottomSheetBehavior.from(v)
    //viewBottomSheetBehavior.setState(bottomSheetBehaviorState)

    viewBottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            LogCalls_Debug.d(TAG, "onStateChanged newState" + newState)
            if (viewBottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
                LogCalls_Debug.d(TAG, " expanded if")

            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            showHideView.rotation = slideOffset * 180
        }

    })
}