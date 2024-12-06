package com.khan.fftracker.mvvmData.shopingBossApi


import com.khan.fftracker.shoppingBossMVVM.dashBoard.model.DashBoardResponse
import com.khan.fftracker.shoppingBossMVVM.predefinedAmount.model.AddFavResponse
import com.khan.fftracker.shoppingBossMVVM.shopNow.ShopDetail.DataModel.ShowNowDetailResponse
import com.khan.fftracker.shoppingBossMVVM.shopNow.dataModel.ShopNowResponse
import com.khan.fftracker.shoppingBossMVVM.shopNowPurchase.model.ShopBossCheckOutResponse
import com.khan.fftracker.shoppingBossMVVM.shopNowPurchase.model.ShopBossProccessOrderResponse
import com.khan.fftracker.shoppingBossMVVM.signIn.Model.LoginResponse
import com.khan.fftracker.shoppingBossMVVM.signUp.accountDetail.model.SignUpResponse
import com.khan.fftracker.shoppingBossMVVM.signUp.activateAccount.model.SmsActivateResponse
import com.khan.fftracker.shoppingBossMVVM.wallet.walletDataModel.WalletCheckBalance
import com.khan.fftracker.shoppingBossMVVM.wallet.walletDataModel.WalletNotesUpdate
import com.khan.fftracker.shoppingBossMVVM.wallet.walletDataModel.WalletResponse
import com.khan.fftracker.shoppingBossMVVM.wallet.walletDataModel.WalletUpdateStatus
import retrofit2.Response
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ShoppingBossApi {
    @FormUrlEncoded
    @POST("shopping/boss/authenticate")
    suspend fun shoppingBossLogin(@FieldMap hashMap: HashMap<String, String>): Response<LoginResponse>

    @FormUrlEncoded
    @POST("shopping/boss/signup")
    suspend fun signUpResponse(@FieldMap hashMap: HashMap<String, String>): Response<SignUpResponse>

    @FormUrlEncoded
    @POST("shopping/boss/signup_smsactivate")
    suspend fun smsActivate(@FieldMap hashMap: HashMap<String, String>): Response<SmsActivateResponse>

    @FormUrlEncoded
    @POST("/shopping/boss/user")
    suspend fun userResponse(@FieldMap hashMap: HashMap<String, String>): Response<DashBoardResponse>

    @FormUrlEncoded
    @POST("shopping/boss/merchantdetail")
    suspend fun merchantDetailResponse(@FieldMap hashMap: HashMap<String, String>): Response<ShowNowDetailResponse>

    @FormUrlEncoded
    @POST("shopping/boss/merchant")
    suspend fun merchantResponse(@FieldMap hashMap: HashMap<String, String>): Response<ShopNowResponse>

    @FormUrlEncoded
    @POST("shopping/boss/addfavorite")
    suspend fun addFavorite(@FieldMap hashMap: HashMap<String, String>): Response<AddFavResponse>
    @FormUrlEncoded
    @POST("shopping/boss/removefavorite")
    suspend fun removeFavorite(@FieldMap hashMap: HashMap<String, String>): Response<AddFavResponse>

    @FormUrlEncoded
    @POST("shopping/boss/checkout")
    suspend fun shoppingBossCheckOut(@FieldMap hashMap: HashMap<String, String>): Response<ShopBossCheckOutResponse>

    @FormUrlEncoded
    @POST("shopping/boss/processorder")
    suspend fun shoppingBossProcessOrder(@FieldMap hashMap: HashMap<String, String>): Response<ShopBossProccessOrderResponse>

    @FormUrlEncoded
    @POST("shopping/boss/wallet/")
    suspend fun shoppingBossWallet(@FieldMap hashMap: HashMap<String, String>): Response<WalletResponse>

    @FormUrlEncoded
    @POST("shopping/boss/updatewalletstatus")
    suspend fun shopBossUpdateWalletStatus(@FieldMap hashMap: HashMap<String, String>): Response<WalletUpdateStatus>

    @FormUrlEncoded
    @POST("shopping/boss/checkbalance/")
    suspend fun shopBossWalletCheckBalance(@FieldMap hashMap: HashMap<String, String>): Response<WalletCheckBalance>

    @FormUrlEncoded
    @POST("shopping/boss/updatewalletnotes")
    suspend fun shopBossUpdateWalletNotes(@FieldMap hashMap: HashMap<String, String>): Response<WalletNotesUpdate>


}