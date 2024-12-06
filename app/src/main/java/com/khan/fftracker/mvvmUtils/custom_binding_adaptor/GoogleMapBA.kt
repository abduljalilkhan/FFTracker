package com.khan.fftracker.mvvmUtils.custom_binding_adaptor

import android.view.MotionEvent
import android.view.View
import androidx.core.widget.NestedScrollView
import androidx.databinding.BindingAdapter

@BindingAdapter("app:disableTouch")
fun setMapDisableTouch(
        viewTouch: View,
        scrollView: NestedScrollView,
) {

    viewTouch.setOnTouchListener(View.OnTouchListener { v, event ->
        val action = event.action
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                // Disallow ScrollView to intercept touch events.
                scrollView.requestDisallowInterceptTouchEvent(true)
                // Disable touch on transparent view
                false
            }

            MotionEvent.ACTION_UP -> {
                // Allow ScrollView to intercept touch events.
                scrollView.requestDisallowInterceptTouchEvent(false)
                true
            }

            MotionEvent.ACTION_MOVE -> {
                scrollView.requestDisallowInterceptTouchEvent(true)
                false
            }

            else -> true
        }
    })

    //googleMap.setListener { scrollView.requestDisallowInterceptTouchEvent(true) }
}