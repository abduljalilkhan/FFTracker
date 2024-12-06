package com.khan.fftracker.Prefrences

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


object Prefs_OperationKotlin {
    private lateinit var prefs:SharedPreferences
    private const val MY_PREFS="my_prefs"

    //user login key
    const val  IS_USER_LOGGED_IN = "IsUserLoggedIn"
    fun init(context: Context){
        prefs =context.getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE)
    }

    fun writeString(key: String, prefValue: String){
        val editor:SharedPreferences.Editor= prefs.edit()
        with(editor) {
            putString(key, prefValue).apply()
        }

    }

    fun writeInt(key: String, prefValue: Int){
        val editor:SharedPreferences.Editor= prefs.edit()
        with(editor) {
            putInt(key, prefValue).apply()
        }

    }
    fun writeBoolean(key: String, prefValue: Boolean){
        val editor:SharedPreferences.Editor= prefs.edit()
        with(editor) {
            putBoolean(key, prefValue).apply()
        }
    }

    fun readString(strKey: String, defaultValue: String): String? {
        return prefs.getString(strKey, defaultValue)
    }
    fun readBoolean(strKey: String, defaultValue: Boolean): Boolean {
        return prefs.getBoolean(strKey, defaultValue)
    }
    fun readInt(strKey: String, defaultValue: Int): Int? {
        return prefs.getInt(strKey, defaultValue)
    }

    // Get saved Model class
    fun <T> getModelPref(name: String, model: Class<T>) : T {
        val value = prefs.getString(name, "{}")
        return Gson().fromJson(value, model) as T

    }

    // Saving Model class
    fun setModelPref(name: String, model: Any) {
        val prefsEditor: SharedPreferences.Editor = prefs.edit()

        val gson = Gson().toJson(model)
        prefsEditor.putString(name, gson).apply()

    }


    //saving list
    fun <T> setListPref(name: String, list: List<T>) {
        val prefsEditor: SharedPreferences.Editor = prefs.edit()
        val gson = Gson().toJson(list)
        prefsEditor.putString(name, gson).apply()
    }


    // Get saved a List
    fun <T> getListPref(name: String, model: Class<T>): List<T> {
        val value = prefs.getString(name, "[]") // Default to an empty JSON array
        val type = TypeToken.getParameterized(List::class.java, model).type
        return Gson().fromJson(value, type) ?: emptyList() // Return an empty list if null
    }


    // Get saved Model class
    fun <T> getModelPrefA(name: String, model: Class<T>) : T? {
        val value = prefs.getString(name, null)
        return if (value != null) {
            Gson().fromJson(value, model)
        } else {
            null
        }
    }

    fun <T> getFromSharedPreferences(key: String, clazz: Class<T>, defaultVal: T?): T? {
        val json = prefs.getString(key, null)
        return if (json == null) defaultVal else Gson().fromJson(json, clazz)
    }



    /* Check for user login
    return true if user logged in else return false*/
    fun isUserLoggedIn(): Boolean {
        return prefs.getBoolean(IS_USER_LOGGED_IN, false)
    }

    fun clear() {
        val prefsEditor: SharedPreferences.Editor = prefs.edit()
        prefsEditor.clear()
        prefsEditor.apply()
    }



}