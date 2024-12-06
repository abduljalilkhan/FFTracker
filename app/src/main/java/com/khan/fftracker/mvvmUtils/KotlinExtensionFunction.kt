package com.khan.fftracker.mvvmUtils

import androidx.databinding.ObservableArrayList
import androidx.lifecycle.MutableLiveData

fun Array<MutableLiveData<String>>.isFormValid(
    formErrorsList: ObservableArrayList<FormValidationError>,
    emailField: MutableLiveData<String>,
    phoneField: MutableLiveData<String>,
    etEmptyError: MutableLiveData<String>,
    errorEmailValidation: MutableLiveData<String>,
    errorPhoneValidation: MutableLiveData<String>
): Boolean {
    formErrorsList.clear()

    if (!EditTextValidationRules.checkEmptyString(this, etEmptyError)) {
        formErrorsList.add(FormValidationError.ET_EMPTY)
    }

    if (!EditTextValidationRules.isValidEmail(emailField.value, errorEmailValidation)) {
        formErrorsList.add(FormValidationError.INVALID_EMAIL)
    }

    if (!EditTextValidationRules.isValidPhoneNumber(phoneField.value.toString(), errorPhoneValidation)) {
        formErrorsList.add(FormValidationError.INVALID_PHONE)
    }

    return formErrorsList.isEmpty()
}

