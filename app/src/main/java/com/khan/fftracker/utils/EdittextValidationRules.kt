package com.khan.fftracker.shoppingBossMVVM

import android.text.Editable
import android.text.TextUtils
import android.util.Patterns
import com.khan.fftracker.logCalls.LogCalls_Debug
import com.khan.fftracker.logCalls.LogCalls_Debug.TAG

import java.util.regex.Matcher
import java.util.regex.Pattern

object EdittextValidationRules {

    var NOT_EMPTY:StringValidationRules = object : StringValidationRules {
        override fun isValidate(strEdiTable: Editable): Boolean {
            return TextUtils.isEmpty(strEdiTable.toString())
        }
    }



    var EMAIL_VALID:StringValidationRules=object :StringValidationRules{
        override fun isValidate(strEdiTable: Editable): Boolean {
            val target = strEdiTable.toString()
            val istrue =
                !(!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches())
            LogCalls_Debug.d(TAG, istrue.toString())
           // return !(!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches())

            if (TextUtils.isEmpty(target) || !Patterns.EMAIL_ADDRESS.matcher(target).matches()) {
                return true
            }

            return false
        }

    }


    var PASSWORD_VALID:StringValidationRules=object :StringValidationRules{
        override fun isValidate(strEdiTable: Editable): Boolean {

            val strPassword=strEdiTable.toString()

            val pattern: Pattern
            val matcher: Matcher

            val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{4,}$"


            pattern = Pattern.compile(PASSWORD_PATTERN)
            matcher = pattern.matcher(strPassword)

            if (!matcher.matches()) {
                LogCalls_Debug.d(TAG,"PASSWORD_VALID matches")
                return true
            }

            if (strPassword.length < 7) {
                LogCalls_Debug.d(TAG, "PASSWORD_VALID length")
                return true
            }

            return false
        }

    }

    var PHONE_VALID:StringValidationRules=object:StringValidationRules{
        override fun isValidate(strEdiTable: Editable): Boolean {
            val phoneNumber = strEdiTable.toString()
            return !TextUtils.isEmpty(phoneNumber) && Patterns.PHONE.matcher(phoneNumber).matches()
        }

    }

    interface StringValidationRules{
        fun  isValidate(strEdiTable: Editable):Boolean
    }


}
