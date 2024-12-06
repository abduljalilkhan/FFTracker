package com.khan.fftracker.mvvmUtils.custom_binding_adaptor

import android.telephony.PhoneNumberFormattingTextWatcher
import android.text.InputFilter
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatEditText
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.khan.fftracker.LogCalls.LogCalls_Debug
import com.khan.fftracker.LogCalls.LogCalls_Debug.TAG
import com.khan.fftracker.Shopping_Boss.SignUp.TextCardWatcher
import com.khan.fftracker.Shopping_Boss.SignUp.YearValiditionWatcher
import com.khan.fftracker.Video_Create_Customer.InputFilterMinMax
import com.khan.fftracker.logCalls.LogCalls_Debug
import com.khan.fftracker.logCalls.LogCalls_Debug.TAG


@BindingAdapter("app:etcard_empty")
fun setEtCardMonth(
    editText: EditText,
    strError: String,
) {

    if (strError != "") {

        editText.error = strError

    } else {
        editText.error = null
    }

    //set maximum value range when user typing value in edittext
    val filter: InputFilterMinMax = object : InputFilterMinMax(
        "1",
        "12"
    ) {} //value should be 0 to 50000,not less then or greater then value is allowed
    editText.filters = arrayOf<InputFilter>(filter)


    editText.onFocusChangeListener =
        View.OnFocusChangeListener { view1: View?, hasFocus: Boolean ->
            if (!hasFocus) {
                if (editText.text.toString().matches("^([1-9]|1[012])$".toRegex())) {
                    // Toast.makeText(getActivity(), "matches", Toast.LENGTH_SHORT).show();
                    if (editText.text.toString().trim { it <= ' ' }.length == 1) {
                        editText.setText("0" + editText.text)
                    }
                } else {
                    // Toast.makeText(getActivity(), "un matches", Toast.LENGTH_SHORT).show();
                }
            }
        }

    LogCalls_Debug.d(TAG, "setEtCardMonth")

}

@BindingAdapter("app:etcard_number")
fun setEtCardNumber(
    editText: EditText,
    strError: String,
) {

    /////////////////////////// edit text detect ehen user is typing
    editText.addTextChangedListener(TextCardWatcher())

    if (strError != "") {
        if (editText.text.toString().isEmpty()) {
            editText.error = strError
        }

    } else {
        editText.error = null
    }
}


@BindingAdapter("app:etcard_year")
fun setEtCardYear(
    editText: EditText,
    strError: String,
) {

    editText.addTextChangedListener(
        YearValiditionWatcher(
            editText as AppCompatEditText?
        )
    )
    if (strError != "") {
        if (editText.text.toString().isEmpty()) {
            editText.error = strError
        }

    } else {
        editText.error = null
    }

}

@BindingAdapter("app:error_phone")
fun setEtPhone(
    editText: EditText,
    strError: String,
) {
    LogCalls_Debug.d(TAG, editText.text.toString() + " setEtErrorRules " + strError)
    editText.addTextChangedListener(PhoneNumberFormattingTextWatcher())

    if (strError != "") {
        //if ( editText.text.toString().isEmpty()){
        editText.error = strError
        // }

    } else {
        editText.error = null
    }
}

@BindingAdapter(value = ["app:etMaxRange", "app:etMinRange"], requireAll = true)
fun setEtAmountLimit(editText: EditText, maxRange: Double, minRange: Double) {
    editText.filters = arrayOf<InputFilter>(
        InputFilterMinMax.DigitsInputFilter(
            5,
            2,
            maxRange
        )
    )
    editText.onFocusChangeListener = InputFilterMinMax.OnFocusChangeListenerMin(minRange)


    editText.setOnEditorActionListener { textView, i, keyEvent ->
        if (i == EditorInfo.IME_ACTION_DONE) {
            editText.clearFocus()
        }
        false
    }


    LogCalls_Debug.d(TAG, "setEtAmountLimit")

}


@BindingAdapter("app:removeFocus")
fun setRemoveFocus(editText: EditText, isFocus: MutableLiveData<Boolean>) {

    editText.clearFocus()

    LogCalls_Debug.d(TAG, "setRemoveFocus")

}


@BindingAdapter(value = ["app:minValue", "app:errorValValidMsg","app:maxValueValid"], requireAll = true)
fun setEtLessGreaterValue(
    editText: EditText,
    strMinValue: String,
    strError:String,
    isMaxValueValid:LiveData<Boolean>
) {


    isMaxValueValid.observe((editText.context as LifecycleOwner)) {
        if (it != null && !it) {
            editText.error = strError
        } else {
            editText.error = null
        }
    }

    editText.onFocusChangeListener =
        View.OnFocusChangeListener { view1: View?, hasFocus: Boolean ->
            if (!hasFocus && editText.text.toString().trim().toInt() < strMinValue.toInt()) {
                editText.setText(strMinValue.toString())
                Toast.makeText(view1!!.context, "value set default to $strMinValue", Toast.LENGTH_SHORT).show();
            }
        }

    LogCalls_Debug.d(TAG, "setEtLessGreaterValue")

}