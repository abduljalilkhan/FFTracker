package com.khan.fftracker.Prefrences;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;


/**
 * Created by Abdul Jalil Khan on 11/2/2017.
 */
public class Prefs_Operation {
    public static final String PREFS="my_prefs";
    static SharedPreferences sharedPreferences;
    //user login key
    static String  IS_USER_LOGGED_IN = "IsUserLoggedIn";

//    public static void saveString(MainActivity mainActivity, String key, String value) {
//        SharedPreferences sharedPreferences=mainActivity.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor=sharedPreferences.edit();
//        editor.putString(key,value);
//    }

    public static SharedPreferences init_SingleTon(Context applicationContext) {
        if (sharedPreferences==null){
            sharedPreferences=applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        }
        return sharedPreferences;
    }

    //////////// save string in shared prefrences
    public static void writePrefs(String string, String name) {
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(string,name).commit();

    }
/////////////////////////save int in shared prefrences
    public static void writePrefs(String string, Integer age) {
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt(string,age).commit();

    }
    ////////////////// save boolean in shared prefrences
    public static void writePrefs(String aBoolean, boolean trueFalse) {
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putBoolean(aBoolean,trueFalse).commit();
    }

    ////////////////// save boolean in shared prefrences

    public static String readPrefs(String string, String strNull) {
        try {
            return sharedPreferences.getString(string, strNull);
        }catch (Exception e){
            e.printStackTrace();
        }
        return strNull;
    }

    public static Integer readPrefs(String string, int intdefaultValue) {
        return sharedPreferences.getInt(string,intdefaultValue);

    }

    public static boolean readPrefs(String aBoolean, boolean defaultValue) {
        return sharedPreferences.getBoolean(aBoolean,defaultValue);
    }

    public static void clear() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    public static void remove(String strRemove) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(strRemove);
        editor.commit();
    }

    // Get saved Model class
    public  static <T> T getModelPref(String name, Class<T> model) {
        String value = sharedPreferences.getString(name, "");
        return new Gson().fromJson(value, model);
    }

    // Saving Model class
    public static  <T> void setModelPref(String name, Class<T> model) {
        SharedPreferences.Editor editor = sharedPreferences.edit();

        String gson = new Gson().toJson(model);
        editor.putString(name, gson).apply();

//        val gson = Gson()
//        val favData = gson.toJson(body)
//        Log.d(LogCalls_Debug.TAG, "savePrefsForNextScreen: fav data"+favData)
//        SharedPrefs.writeString(LoginConstant.LOGIN_RESPONSE,favData)

    }

    /* Check for user login
    return true if user logged in else return false*/
    public Boolean isUserLoggedIn()  {
        return sharedPreferences.getBoolean(IS_USER_LOGGED_IN, false);
    }

}
