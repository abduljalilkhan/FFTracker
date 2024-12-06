package com.khan.fftracker.mvvmUtils

import android.text.TextUtils
import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import com.khan.fftracker.logCalls.LogCalls_Debug

import java.util.regex.Matcher
import java.util.regex.Pattern

object EditTextValidationRules {


    fun checkEmptyString(
        strArr: Array<MutableLiveData<String>>,
        etEmptyError: MutableLiveData<String>
    ): Boolean {
        for (a in strArr.indices) {
            LogCalls_Debug.d(LogCalls_Debug.TAG, "value" + strArr[a].value)
            if (strArr[a].value!!.isEmpty()) {

                etEmptyError.value = "Please type here"

                return false
            }
        }
        return true
    }

    fun isValidEmail(strEmail: String?, errorEmailValidation: MutableLiveData<String>): Boolean {
        if (TextUtils.isEmpty(strEmail) || !Patterns.EMAIL_ADDRESS.matcher(strEmail).matches()) {
            errorEmailValidation.value = "Please type correct email"
            return false
        }
        return true
        // return !TextUtils.isEmpty(strEmail) && Patterns.EMAIL_ADDRESS.matcher(strEmail).matches();
    }

    fun isValidPhoneNumber(strPhone: String, errorPhoneValidation: MutableLiveData<String>): Boolean {
        val strPhoneNo =
            strPhone.replace("(", "").replace(")", "").replace("-", "").replace(" ", "")

        return if (strPhoneNo.isEmpty() || strPhoneNo.length < 10) {
            errorPhoneValidation.value= "Please type valid Phone Number"
            false
        } else {
            Patterns.PHONE.matcher(strPhoneNo).matches()
        }
        // String validNumber = "^[+]?[0-9]{8,14}$";

    }

    fun isValidPassword(
        strPassword: String?,
        errorPasswordValidation: MutableLiveData<String>
    ): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{4,}$"
        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(strPassword)
        if (!matcher.matches()) {
            errorPasswordValidation.value = "Password format is incorrect"
            return false
        }
        if (strPassword!!.length < 7) {
            errorPasswordValidation.value = "Password must be atleast 8 characters"
            return false
        }
        return true
    }

    fun passwordConfirmation(
        strPassword: String?,
        strConfirmPassword: String?,
        errorConfirmPasswordValidation: MutableLiveData<String>
    ): Boolean {

        if (!strPassword.equals(strConfirmPassword, ignoreCase = true)) {
            errorConfirmPasswordValidation.value = "Confirm Password is incorrect"
            return false
        }
        return true
    }

    fun emailConfirmation(
        strEmail: String?,
        strConfirmEmail: String?,
        errorConfirmEmailValidation: MutableLiveData<String>
    ): Boolean {
        if (!strEmail.equals(strConfirmEmail, ignoreCase = true)) {
            errorConfirmEmailValidation.value = "Confirm Email is incorrect"
            return false
        }
        return true
    }

    fun isFormValid(arrEt :Array<MutableLiveData<String>>,
        emailField: MutableLiveData<String>,
        phoneField: MutableLiveData<String>,
        etEmptyError: MutableLiveData<String>,
        errorEmailValidation: MutableLiveData<String>,
        errorPhoneValidation: MutableLiveData<String>
    ): Boolean {
        val formErrorsList = mutableListOf<FormValidationError>()

        if (!checkEmptyString(arrEt, etEmptyError)) {
            formErrorsList.add(FormValidationError.ET_EMPTY)
        }

        if (!isValidEmail(emailField.value, errorEmailValidation)) {
            formErrorsList.add(FormValidationError.INVALID_EMAIL)
        }

        if (!isValidPhoneNumber(phoneField.value.toString(), errorPhoneValidation)) {
            formErrorsList.add(FormValidationError.INVALID_PHONE)
        }

        return formErrorsList.isEmpty()
    }
}

