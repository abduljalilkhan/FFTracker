package com.khan.fftracker.mvvmData.networkApi

import com.google.gson.GsonBuilder
import com.khan.fftracker.Network_Volley.IsAdmin
import com.khan.fftracker.logCalls.LogCalls_Debug
import com.khan.fftracker.logCalls.LogCalls_Debug.TAG

import com.khan.fftracker.mvvmData.networkApi.ResultApi
import retrofit2.Response


abstract class BaseDataSource {


    suspend fun <T> safeApiCall(apiCall: suspend () -> Response<T>): ResultApi<T> {
        try {
            LogCalls_Debug.d(TAG,"safeApiCall")
            val response = apiCall()
            ResultApi.loading(null)
            if (response.isSuccessful) {

                LogCalls_Debug.d(TAG,"isSuccessful " + response.body().toString())
                LogCalls_Debug.d("json pretty printed gson => ", GsonBuilder().setPrettyPrinting().create().toJson(response.body()))


                val body = response.body()
                if (body != null) {
                    return ResultApi.success(body)
                }
            }

            return error("${response.code()} ${response.message()}")
        } catch (e: Exception) {
            return error(e.message ?: e.toString())
        }
    }

    private fun <T> error(message: String): ResultApi<T> {
        LogCalls_Debug.d(TAG, message)
       // return ResultApi.error("Network call has failed for a following reason: $message")

        return ResultApi.error("Some thing went wrong")

    }
     fun getHashMap(map: HashMap<String, String>): HashMap<String, String> {

        return IsAdmin().hashMap_Params(map)

    }
}
