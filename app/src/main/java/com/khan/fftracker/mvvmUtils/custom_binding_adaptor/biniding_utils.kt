package com.khan.fftracker.mvvmUtils.custom_binding_adaptor

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.view.isVisible
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import com.bumptech.glide.Glide
import com.kofigyan.stateprogressbar.StateProgressBar

import com.khan.fftracker.logCalls.LogCalls_Debug
import com.khan.fftracker.logCalls.LogCalls_Debug.TAG

import com.squareup.picasso.Picasso


@BindingAdapter("android:visibility")
fun setVisibility(view: View, value: Boolean) {
    view.visibility = if (value) View.VISIBLE else View.GONE
}

@BindingAdapter("android:visibility")
fun setVisibility(view: View, value: String) {
    view.visibility = if (value == "1") View.VISIBLE else View.GONE
}


@BindingAdapter("android:visibility")
fun View.bindVisibility(visible: Boolean?) {
    isVisible = visible == true
    // visibility = if (visible == true) View.VISIBLE else View.GONE
}

@BindingAdapter("src")
fun setImageUrl(imageView: ImageView, imageUrl: String) {
    LogCalls_Debug.d(TAG, "$imageUrl image")
    if (imageUrl != "null") {
        Glide.with(imageView.context).load(imageUrl).into(imageView)
    }
}

@BindingAdapter("imageUrl")
fun loadImage(imageView: ImageView, imageUrl: String?) {
    LogCalls_Debug.d(TAG, "$imageUrl image")
    if (imageUrl != "") {
        Glide.with(imageView.context).load(imageUrl).into(imageView)
    }

}

@BindingAdapter("imageUrlPicasso")
fun loadImagePicasso(imageView: ImageView, imageUrl: String?) {
    LogCalls_Debug.d(TAG, "$imageUrl image")
    if (imageUrl != "") {
        Picasso.with(imageView.context).load(imageUrl).into(imageView)
    }

}

@BindingAdapter("setImgResource")
fun setImgResource(imageView: ImageView, imgRes: Int) {
    LogCalls_Debug.d(TAG, "$imgRes image")
    imageView.setImageResource(imgRes!!)
}

@BindingAdapter("setImgBitmap")
fun setImgBitmap(imageView: ImageView, imgRes: Bitmap?) {
    LogCalls_Debug.d(TAG, "$imgRes image")
    imageView.setImageBitmap(imgRes)
}

@BindingAdapter("setBgResource")
fun setBgResource(btn: View, bgRes: Int) {
    LogCalls_Debug.d(TAG, "$bgRes image")
    btn.setBackgroundResource(bgRes)
}

@BindingAdapter(value = ["background_circle"]) //customise your name here
fun setBackGroundColor(view: View, color: Int) {
    view.background = CircleDrawable(color)

}

@BindingAdapter("stateText")
fun stateProgressBarText(stateProgressBar: StateProgressBar, descriptionData: Array<String>) {
    stateProgressBar.setStateDescriptionData(descriptionData)

}

@BindingAdapter(value = ["app:validation", "app:errormsg", "app:empty"], requireAll = true)
fun setEtErrorRulesValidation(
        editText: EditText,
        strValidationRules: EdittextValidationRules.StringValidationRules,
        strError: String,
        isEtEmpty: Boolean
) {

    // if (isEtEmpty) {
    if (strError != "") {
        if (strValidationRules.isValidate(editText.text)) {
            editText.error = strError
        } else {
            editText.error = null
        }
    }


    LogCalls_Debug.d(TAG, "setEtErrorRules")
    editText.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
        override fun afterTextChanged(editable: Editable) {
//            if (isEtEmpty) {
//                if (strValidationRules.isValidate(editText.text)) {
//                    editText.error = strError
//                } else {
//                    editText.error = null
//                }
//            }
        }
    })
}


@BindingAdapter(value = ["app:errormsg", "isempty"], requireAll = false)
fun setEtErrorRules(
        editText: EditText,
        strError: String,
        isEtEmpty: Boolean
) {
    LogCalls_Debug.d(TAG, editText.text.toString() + " setEtErrorRules " + strError)

    if (strError != "") {
        if (isEtEmpty && editText.text.toString().isEmpty()) {
            editText.error = strError
        } else if (!isEtEmpty) {
            editText.error = strError
        }


    } else {
        editText.error = null
    }
}

//@BindingAdapter("entries")
//fun entries(recyclerView: RecyclerView, array: Array<String?>?) {
//    recyclerView.adapter = SimpleArrayAdapter(array)
//}

@BindingAdapter(value = ["spinnerTextColor", "selectedItemPos"], requireAll = false)
fun spinnerSelectedColor(spinner: Spinner, strColor: String, pos: Int) {
    if (pos != -1) {
        // (spinner.getChildAt(0) as TextView).setTextColor(Color.parseColor(strColor))
        (spinner.selectedView as TextView).setTextColor(Color.parseColor(strColor)) //<----

    }
}

@BindingAdapter(value = ["selectedValue", "selectedValueAttrChanged"], requireAll = false)
fun bindSpinnerData(
        pAppCompatSpinner: AppCompatSpinner,
        newSelectedValue: ProfileState?,
        newTextAttrChanged: InverseBindingListener
) {
    pAppCompatSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
            newTextAttrChanged.onChange()
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {}
    }
    if (newSelectedValue != null) {
        // val pos = (pAppCompatSpinner.adapter as ArrayAdapter<String?>).getPosition(newSelectedValue)
        val pos = pAppCompatSpinner.selectedItemPosition

        pAppCompatSpinner.setSelection(pos, true)
    }
    LogCalls_Debug.d(TAG + " bindSpinnerData", newSelectedValue.toString())
}

@InverseBindingAdapter(attribute = "selectedValue", event = "selectedValueAttrChanged")
fun captureSelectedValue(pAppCompatSpinner: AppCompatSpinner): ProfileState? {
    LogCalls_Debug.d(TAG + " captureSelectedValue", (pAppCompatSpinner.selectedItem as ProfileState).toString())
    return pAppCompatSpinner.selectedItem as ProfileState
}

fun requestFocus(view: View) {
    val mContext = view.context as Activity
    if (view.requestFocus()) {
        mContext.getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
    }


}