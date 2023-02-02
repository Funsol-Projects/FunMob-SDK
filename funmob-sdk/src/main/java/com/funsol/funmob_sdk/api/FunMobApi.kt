package com.funsol.funmob_sdk.api

import com.funsol.funmob_sdk.model.CampaignResponse
import com.funsol.funmob_sdk.model.UserDeviceInfo
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface FunMobApi {
    //    @GET("api/campaign/?time=Asia%2FKabul&{uuid}")
//    @GET("api/campaign/?time=Asia%2FKabul&uuid=fun-mob-ad-unit-d89be49f-6a34-43ea-877b-007649f1f434")
    @GET("api/campaign/")
    suspend fun loadFunAd(
        @Header("Authorization") authorization: String,
//        @Body userDeviceInfo: UserDeviceInfo,
        @Query(value = "time", encoded = true) time: String,
        @Query(value = "uuid", encoded = true) uuid: String
    ): Response<CampaignResponse>

    @GET("api/clicks/{time}&{uuid}&{camp_uuid}")
    suspend fun validateClick(
        @Header("Authorization") authorization: String,
        @Body userDeviceInfo: UserDeviceInfo,
        @Path(value = "time", encoded = true) time: String,
        @Path(value = "uuid", encoded = true) uuid: String,
        @Path(value = "camp_uuid", encoded = true) camp_uuid: String
    )

    @GET("api/impressions/{time}&{uuid}&{camp_uuid}")
    suspend fun validateImpression(
        @Header("Authorization") authorization: String,
        @Body userDeviceInfo: UserDeviceInfo,
        @Path(value = "time", encoded = true) time: String,
        @Path(value = "uuid", encoded = true) uuid: String,
        @Path(value = "camp_uuid", encoded = true) camp_uuid: String
    )

    @GET("api/installs/{time}&{uuid}&{camp_uuid}")
    suspend fun validateInstall(
        @Header("Authorization") authorization: String,
        @Body userDeviceInfo: UserDeviceInfo,
        @Path(value = "time", encoded = true) time: String,
        @Path(value = "uuid", encoded = true) uuid: String,
        @Path(value = "camp_uuid", encoded = true) camp_uuid: String
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