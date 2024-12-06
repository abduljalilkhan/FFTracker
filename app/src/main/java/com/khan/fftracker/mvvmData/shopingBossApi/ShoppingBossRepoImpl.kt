package com.khan.fftracker.mvvmData.shopingBossApi

import com.khan.fftracker.logCalls.LogCalls_Debug
import com.khan.fftracker.mvvmData.networkApi.BaseDataSource


class ShoppingBossRepoImpl(private val shoppingBossRepository: ShoppingBossRepo): BaseDataSource() {
    suspend fun getLoginResponse(hashMap: HashMap<String,String>)=safeApiCall {
        var map: HashMap<String, String> = hashMap
        map= getHashMap(map)
        LogCalls_Debug.d(LogCalls_Debug.TAG,map.toString())
        shoppingBossRepository.getLoginResponse(map) }

    suspend fun getSignUpResponse(hashMap: HashMap<String,String>)=safeApiCall {
        var map: HashMap<String, String> = hashMap
        map= getHashMap(map)

        LogCalls_Debug.d(LogCalls_Debug.TAG,map.toString())
        shoppingBossRepository.getSignUpResponse(map) }


    suspend fun getSmsResponse(hashMap: HashMap<String,String>)=safeApiCall {
        var map: HashMap<String, String> = hashMap
        map= getHashMap(map)

        LogCalls_Debug.d(LogCalls_Debug.TAG,map.toString())
        shoppingBossRepository.getSmsActivateResponse(map) }


    suspend fun getUserResponse(hashMap: HashMap<String,String>)=safeApiCall {
        var map: HashMap<String, String> = hashMap
        map= getHashMap(map)

        LogCalls_Debug.d(LogCalls_Debug.TAG,map.toString())
        shoppingBossRepository.getUserResponse(map) }


    suspend fun getMerchantDetailResponse(hashMap: HashMap<String,String>)=safeApiCall {
        var map: HashMap<String, String> = hashMap
        map= getHashMap(map)

        LogCalls_Debug.d(LogCalls_Debug.TAG,map.toString())
        shoppingBossRepository.getMerchantDetailResponse(map) }

    suspend fun getMerchantResponse(hashMap: HashMap<String,String>)=safeApiCall {
        var map: HashMap<String, String> = hashMap
        map= getHashMap(map)

        LogCalls_Debug.d(LogCalls_Debug.TAG,map.toString())
        shoppingBossRepository.getMerchantResponse(map) }

    suspend fun getAddFavoriteResponse(hashMap: HashMap<String,String>)=safeApiCall {
        var map: HashMap<String, String> = hashMap
        map= getHashMap(map)

        LogCalls_Debug.d(LogCalls_Debug.TAG,map.toString())
        shoppingBossRepository.getAddFavoriteResponse(map) }

    suspend fun getRemoveFavoriteResponse(hashMap: HashMap<String,String>)=safeApiCall {
        var map: HashMap<String, String> = hashMap
        map= getHashMap(map)

        LogCalls_Debug.d(LogCalls_Debug.TAG,map.toString())
        shoppingBossRepository.getRemoveFavoriteResponse(map) }


    suspend fun getShoppingBossCheckOutResponse(hashMap: HashMap<String,String>)=safeApiCall {
        var map: HashMap<String, String> = hashMap
        map= getHashMap(map)

        LogCalls_Debug.d(LogCalls_Debug.TAG,map.toString())
        shoppingBossRepository.getShoppingBossCheckOutResponse(map) }

    suspend fun getShoppingBossProcessOrderResponse(hashMap: HashMap<String,String>)=safeApiCall {
        var map: HashMap<String, String> = hashMap
        map= getHashMap(map)

        LogCalls_Debug.d(LogCalls_Debug.TAG,map.toString())
        shoppingBossRepository.getShoppingBossProcessOrderResponse(map) }


    suspend fun getShopBossWalletResponse(hashMap: HashMap<String,String>)=safeApiCall {
        var map: HashMap<String, String> = hashMap
        map= getHashMap(map)

        LogCalls_Debug.d(LogCalls_Debug.TAG,map.toString())
        shoppingBossRepository.getShopBossWalletResponse(map) }

    suspend fun getShopBossUpdateWalletStatusResponse(hashMap: HashMap<String,String>)=safeApiCall {
        var map: HashMap<String, String> = hashMap
        map= getHashMap(map)

        LogCalls_Debug.d(LogCalls_Debug.TAG,map.toString())
        shoppingBossRepository.getShopBossUpdateWalletStatusResponse(map) }

    suspend fun getShopBossWalletCheckBalanceResponse(hashMap: HashMap<String,String>)=safeApiCall {
        var map: HashMap<String, String> = hashMap
        map= getHashMap(map)

        LogCalls_Debug.d(LogCalls_Debug.TAG,map.toString())
        shoppingBossRepository.getShopBossWalletCheckBalanceResponse(map) }

    suspend fun getShopBossUpdateWalletNotesResponse(hashMap: HashMap<String,String>)=safeApiCall {
        var map: HashMap<String, String> = hashMap
        map= getHashMap(map)

        LogCalls_Debug.d(LogCalls_Debug.TAG,map.toString())
        shoppingBossRepository.getShopBossUpdateWalletNotesResponse(map) }
}


