package com.khan.fftracker.mvvmUtils.custom_binding_adaptor

import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.github.hamzaahmedkhan.counteranimationtextview.CountAnimationTextView
import com.khan.fftracker.Animation_Utils.Animate_Views
import com.khan.fftracker.Chats_View.RelativeLayoutTouchListener

import com.khan.fftracker.R
import com.khan.fftracker.autoverse_mvvm.alert.viewModel.AlertVM
import java.text.DecimalFormat

@BindingAdapter("shakeAnim")
fun shakeAnimation(mView: ImageView, gifAnim: Int) {
    ///////////////////////
    Glide.with(mView.context).asGif().load(gifAnim).into(mView);

}

@BindingAdapter(value = ["app:anim_bg_drawable", "isShowHide", "carView"], requireAll = false)
fun hideShowCarStolenLayout(view: View, color: Int, isShowHide: Boolean, bgView: View) {

    if (view.isShown) {
        //binding.layoutStolenCarCall.startAnimation(new Animate_Views(getActivity()).outToLeftAnimation());
        val anim = Animate_Views().outToLeftAnimation()
        anim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                view.visibility = View.GONE
                //binding.layoutStolenCar.setBackground(getResources().getDrawable(R.drawable.circle_black_border));
                bgView.setBackgroundResource(R.color.semi_transparent)
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
        view.startAnimation(anim)
    } else {
        view.visibility = View.VISIBLE
        view.startAnimation(Animate_Views().inFromLeftAnimation())
        bgView.setBackgroundResource(color)
    }
}

@BindingAdapter(value = ["showHideView","isUpDown"], requireAll = false)
fun hideShowViewUpBottom(view: View, showHideView: View,isShowHide: Boolean) {
    val bottomUp = AnimationUtils.loadAnimation(view.context, R.anim.slideup)
    val bottomDown = AnimationUtils.loadAnimation(view.context, R.anim.slidedown)


    //// Dismiss ,Hide image while swiping
    view.setOnTouchListener(object : RelativeLayoutTouchListener(view.context as FragmentActivity?) {
        override fun onTopToBottomSwipe() {
            super.onTopToBottomSwipe()

            showHideView.startAnimation(bottomDown)
            showHideView.visibility = View.GONE
        }

        override fun onBottomToTopSwipe() {
            showHideView.startAnimation(bottomUp)
            showHideView.visibility = View.VISIBLE
        }
    })


}


@BindingAdapter("animCounbvter")
fun setAnimCounterA(view: CountAnimationTextView, value: String?) {
    if (view.isAttachedToWindow) {
        val viewModel = (view.context as? ViewModelProvider.Factory)?.create(AlertVM::class.java)
        if (viewModel?.isAnimationPlayed?.value == false) {
            //view.startAnimation(value.toString())
            viewModel?.setAnimationPlayed()
        }
    }
}

@BindingAdapter(value = ["animCounter"], requireAll = false)
fun setAnimCounter(tv: CountAnimationTextView, countText: String?) {
    if (countText != null) {
        //if showing count is not equal to current text(fetching from api) then animate views otherwise just don't animate just show old values in textviews
        if (tv.text.toString() != countText) {

            tv.setDecimalFormat(DecimalFormat("###,###,###"))
                .setAnimationDuration(2000).countAnimation(
                    converttoInt(tv.text.toString()), converttoInt(
                        countText
                    )
                )

            tv.setCountAnimationListener(object : CountAnimationTextView.CountAnimationListener {
                override fun onAnimationStart(o: Any?) {
                }

                override fun onAnimationEnd(o: Any?) {
                    tv.text = countText
                }
            })
        }
    }
}

private fun converttoInt(str: String): Int {
    val d = str.toDouble()
    val i = d.toInt()
    return i
}

//@BindingAdapter(value = ["app:showAnimLeftRight", "hideAnimLeftRight", "isShowHide"], requireAll = false)
//fun animLeftRight(view: View, hideView: View, isShowHide: Boolean) {
//
//    LogCalls_Debug.d(TAG, "animLeftRight: " + isShowHide)
//    if (view.isShown && isShowHide) {
//        //binding.layoutStolenCarCall.startAnimation(new Animate_Views(getActivity()).outToLeftAnimation());
//        val anim = Animate_Views().outToLeftAnimation()
//        anim.setAnimationListener(object : Animation.AnimationListener {
//            override fun onAnimationStart(animation: Animation) {}
//            override fun onAnimationEnd(animation: Animation) {
//                view.visibility = View.GONE
//                //binding.layoutStolenCar.setBackground(getResources().getDrawable(R.drawable.circle_black_border));
//            }
//
//            override fun onAnimationRepeat(animation: Animation) {}
//        })
//        view.startAnimation(anim)
//    } else {
//        val anim = Animate_Views().inFromLeftAnimation()
//
//        anim.setAnimationListener(object : Animation.AnimationListener {
//            override fun onAnimationStart(animation: Animation) {
//                view.visibility = View.VISIBLE
//            }
//            override fun onAnimationEnd(animation: Animation) {
//            }
//
//            override fun onAnimationRepeat(animation: Animation) {}
//        })
//        view.startAnimation(anim)
//    }
//}



