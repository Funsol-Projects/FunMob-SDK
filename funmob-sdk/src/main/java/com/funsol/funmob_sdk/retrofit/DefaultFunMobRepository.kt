package com.funsol.funmob_sdk.retrofit

import com.funsol.funmob_sdk.api.FunMobApi
import com.funsol.funmob_sdk.model.CampaignResponse
import com.funsol.funmob_sdk.model.UserDeviceInfo
import com.funsol.funmob_sdk.utils.Constants
import com.funsol.funmob_sdk.utils.Resource
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

class DefaultFunMobRepository : FunMobRepository {

    private val api: FunMobApi = Retrofit.Builder()
        .baseUrl(Constants.ApiAddress)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(FunMobApi::class.java)


    private fun getTimeZone(): String {
        return TimeZone.getDefault().id
    }

    private fun getUserDeviceInfo(): UserDeviceInfo {
        return UserDeviceInfo(
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            "",
            ""
        )
    }

    override suspend fun loadFunAd(authorization: String, uuid: String): Resource<CampaignResponse> {
        return try {
            val response = api.loadFunAd(authorization, getTimeZone(), uuid)
            val result = response.body()
            if (response.isSuccessful && result != null) {
                Resource.Success(result)
            } else {
                Resource.Error(response.message())
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An error occurred")
        }
    }

    override suspend fun validateClick(authorization: String, uuid: String, camp_uuid: String) {
        try {
            api.validateClick(authorization, getUserDeviceInfo(), getTimeZone(), uuid, camp_uuid)
        } catch (_: Exception) {
        }
    }

    override suspend fun validateImpression(authorization: String, uuid: String, camp_uuid: String) {
        try {
            api.validateImpression(authorization, getUserDeviceInfo(), getTimeZone(), uuid, camp_uuid)
        } catch (_: Exception) {
        }
    }

    override suspend fun validateInstall(authorization: String, uuid: String, camp_uuid: String) {
        try {
            api.validateInstall(authorization, getUserDeviceInfo(), getTimeZone(), uuid, camp_uuid)
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