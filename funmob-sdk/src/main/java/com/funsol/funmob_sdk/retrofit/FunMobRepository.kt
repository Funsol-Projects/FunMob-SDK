package com.funsol.funmob_sdk.retrofit

import com.funsol.funmob_sdk.model.CampaignResponse
import com.funsol.funmob_sdk.model.UserDeviceInfo
import com.funsol.funmob_sdk.utils.Resource

interface FunMobRepository {
    suspend fun loadFunAd(
        authorization: String, uuid: String, responseCallback: (Resource<CampaignResponse>) -> Unit
    )

    suspend fun validateClick(
        authorization: String, uuid: String, camp_uuid: String
    )

    suspend fun validateImpression(
        authorization: String, uuid: String, camp_uuid: String
    )

    suspend fun validateInstall(
        authorization: String, uuid: String, camp_uuid: String
    )

    suspend fun registerApp(
        authorization: String, uuid: String
    )

    suspend fun unRegisterApp(
        authorization: String, uuid: String
    )
}