package com.funsol.funmob_sdk.api

import com.funsol.funmob_sdk.model.CampaignResponse
import com.funsol.funmob_sdk.model.UserDeviceInfo
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface FunMobApi {

    @POST("api/campaign/")
    fun loadFunAd(
        @Header("Authorization") authorization: String,
        @Body body: RequestBody,
        @Header("Content-Type") contentType: String = "application/json"
    ): Call<CampaignResponse>

    @POST("api/clicks/")
    suspend fun validateClick(
        @Header("Authorization") authorization: String,
        @Body body: RequestBody,
        @Header("Content-Type") contentType: String = "application/json"
    )

    @POST("api/impressions/")
    suspend fun validateImpression(
        @Header("Authorization") authorization: String,
        @Body body: RequestBody,
        @Header("Content-Type") contentType: String = "application/json"
    ): Response<String>


    @POST("api/installs/")
    suspend fun validateInstall(
        @Header("Authorization") authorization: String,
        @Body body: RequestBody,
        @Header("Content-Type") contentType: String = "application/json"
    )

    @GET("api/register/{uuid}")
    suspend fun registerApp(
        @Header("Authorization") authorization: String,
        @Path(value = "uuid", encoded = true) uuid: String
    )

    @GET("api/unregister/{uuid}")
    suspend fun unRegisterApp(
        @Header("Authorization") authorization: String,
        @Path(value = "uuid", encoded = true) uuid: String
    )
}