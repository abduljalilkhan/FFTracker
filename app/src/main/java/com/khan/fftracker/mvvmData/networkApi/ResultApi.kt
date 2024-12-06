package com.khan.fftracker.mvvmData.networkApi

data class ResultApi<out T>(val statusApi: StatusApi,
                            val data: T?,
                            val msg: String?) {

    enum class StatusApi {
        SUCCESS,
        ERROR,
        LOADING,
        FAILURE
    }

    companion object {
        fun <T> success(data: T): ResultApi<T> {
            return ResultApi(StatusApi.SUCCESS, data, null)
        }

        fun <T> error(message: String, data: T? = null): ResultApi<T> {
            return ResultApi(StatusApi.ERROR, data, message)
        }

        fun <T> loading(data: T?): ResultApi<T> {
            return ResultApi(StatusApi.LOADING, data, null)
        }

        fun <T> failed(message: String, data: T?): ResultApi<T> {
            return ResultApi(StatusApi.FAILURE, data, message)
        }
    }

}


