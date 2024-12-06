package com.khan.fftracker.mvvmData.shopingBossApi

class ShoppingBossRepo (private val apiInterface: ShoppingBossApi){
    suspend fun getLoginResponse(hashMap:HashMap<String,String>)= apiInterface.shoppingBossLogin(hashMap)

    suspend fun getSignUpResponse(hashMap:HashMap<String,String>)= apiInterface.signUpResponse(hashMap)

    suspend fun getSmsActivateResponse(hashMap:HashMap<String,String>)= apiInterface.smsActivate(hashMap)

    suspend fun getUserResponse(hashMap:HashMap<String,String>)= apiInterface.userResponse(hashMap)

    suspend fun getMerchantDetailResponse(hashMap:HashMap<String,String>)= apiInterface.merchantDetailResponse(hashMap)

    suspend fun getMerchantResponse(hashMap:HashMap<String,String>)= apiInterface.merchantResponse(hashMap)

    suspend fun getAddFavoriteResponse(hashMap:HashMap<String,String>)= apiInterface.addFavorite(hashMap)
    suspend fun getRemoveFavoriteResponse(hashMap:HashMap<String,String>)= apiInterface.removeFavorite(hashMap)

    suspend fun getShoppingBossCheckOutResponse(hashMap:HashMap<String,String>)= apiInterface.shoppingBossCheckOut(hashMap)

    suspend fun getShoppingBossProcessOrderResponse(hashMap:HashMap<String,String>)= apiInterface.shoppingBossProcessOrder(hashMap)

    suspend fun getShopBossWalletResponse(hashMap:HashMap<String,String>)= apiInterface.shoppingBossWallet(hashMap)

    suspend fun getShopBossUpdateWalletStatusResponse(hashMap:HashMap<String,String>)= apiInterface.shopBossUpdateWalletStatus(hashMap)

    suspend fun getShopBossWalletCheckBalanceResponse(hashMap:HashMap<String,String>)= apiInterface.shopBossWalletCheckBalance(hashMap)

    suspend fun getShopBossUpdateWalletNotesResponse(hashMap:HashMap<String,String>)= apiInterface.shopBossUpdateWalletNotes(hashMap)
}