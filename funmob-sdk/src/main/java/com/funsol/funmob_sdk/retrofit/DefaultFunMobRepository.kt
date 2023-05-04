package com.funsol.funmob_sdk.retrofit

import android.os.Build
import android.util.Log
import com.funsol.funmob_sdk.api.FunMobApi
import com.funsol.funmob_sdk.model.CampaignResponse
import com.funsol.funmob_sdk.model.UserDeviceInfo
import com.funsol.funmob_sdk.utils.Constants
import com.funsol.funmob_sdk.utils.Resource
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class DefaultFunMobRepository : FunMobRepository {

    private val client = OkHttpClient.Builder().build()

    private val api: FunMobApi = Retrofit.Builder()
        .baseUrl(Constants.ApiAddress)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(FunMobApi::class.java)

    private val mediaType = "application/json".toMediaTypeOrNull()

    private fun getTimeZone(): String {
        return TimeZone.getDefault().id
    }

    private fun getUserDeviceInfo(): UserDeviceInfo {
        return UserDeviceInfo(
            "",
            "",
            "",
            Build.BOARD,
            Build.BOOTLOADER,
            Build.BRAND,
            Build.DEVICE,
            Build.DISPLAY,
            Build.FINGERPRINT,
            Build.HOST,
            Build.HARDWARE,
            Build.ID,
            Build.MANUFACTURER,
            "",
            Build.PRODUCT,
            "",
            Build.getRadioVersion(),
            Build.getRadioVersion(),
            Build.TAGS,
            Build.TIME.toString(),
            Build.TYPE,
            Build.MODEL,
            StringBuilder().append(Build.BOARD).append(Build.BOOTLOADER).append(Build.FINGERPRINT).toString()
        )
    }

    override suspend fun loadFunAd(authorization: String, uuid: String, responseCallback: (Resource<CampaignResponse>) -> Unit) {
        try {
            val userDeviceInfo = getUserDeviceInfo()
            userDeviceInfo.time = getTimeZone()
            userDeviceInfo.uuid = uuid
            
            val requestBody = RequestBody.create(mediaType, Gson().toJson(userDeviceInfo).trimIndent())

            api.loadFunAd(authorization, requestBody).enqueue(object : Callback<CampaignResponse> {
                override fun onResponse(call: Call<CampaignResponse>, response: Response<CampaignResponse>) {
                    Log.i("FunMobTags", "onResponse: ")
                    val result = response.body()
                    if (response.isSuccessful && result != null) {
                        responseCallback(Resource.Success(result))
                    }
                }

                override fun onFailure(call: Call<CampaignResponse>, t: Throwable) {
                    Log.i("FunMobTags", "onFailure: ", t)
                    responseCallback(Resource.Error(t.message ?: "An error occurred"))
                }

            })
        } catch (e: Exception) {
            Log.i("FunMobTags", "onException: ", e)
            responseCallback(Resource.Error(e.message ?: "An error occurred"))
        }
    }

    override suspend fun validateClick(authorization: String, uuid: String, camp_uuid: String) {
        try {

            val userDeviceInfo = getUserDeviceInfo()
            userDeviceInfo.time = getTimeZone()
            userDeviceInfo.uuid = uuid
            userDeviceInfo.camp_uuid = camp_uuid

            val requestBody = RequestBody.create(mediaType, Gson().toJson(userDeviceInfo).trimIndent())

            api.validateClick(authorization, requestBody)
        } catch (_: Exception) {
        }
    }

    override suspend fun validateImpression(authorization: String, uuid: String, camp_uuid: String) {
        try {
            val userDeviceInfo = getUserDeviceInfo()
            userDeviceInfo.time = getTimeZone()
            userDeviceInfo.uuid = uuid
            userDeviceInfo.camp_uuid = camp_uuid

            val requestBody = RequestBody.create(mediaType, Gson().toJson(userDeviceInfo).trimIndent())

            val response = api.validateImpression(authorization, requestBody)
            Log.i("MyTesting", "validateImpression: $response")
        } catch (ex: Exception) {
            Log.i("MyTesting", "validateImpression: exceptio, ", ex)
        }
    }

    override suspend fun validateInstall(authorization: String, uuid: String, camp_uuid: String) {
        try {
            val userDeviceInfo = getUserDeviceInfo()
            userDeviceInfo.time = getTimeZone()
            userDeviceInfo.uuid = uuid
            userDeviceInfo.camp_uuid = camp_uuid

            val requestBody = RequestBody.create(mediaType, Gson().toJson(userDeviceInfo).trimIndent())

            api.validateInstall(authorization, requestBody)
        } catch (_: Exception) {
        }
    }

    override suspend fun registerApp(authorization: String, uuid: String) {
        try {
            api.registerApp(authorization, uuid)
        } catch (_: Exception) {
        }
    }

    override suspend fun unRegisterApp(authorization: String, uuid: String) {
        try {
            api.unRegisterApp(authorization, uuid)
        } catch (_: Exception) {
        }
    }
}