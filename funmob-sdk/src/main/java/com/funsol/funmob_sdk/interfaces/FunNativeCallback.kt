package com.funsol.funmob_sdk.interfaces

import com.funsol.funmob_sdk.model.CampaignResponse

interface FunNativeCallback {
    fun onAdLoaded(campaignResponse: CampaignResponse)
    fun onAdFailed(adError: String)
    fun onAdShown()
    fun onAdClicked()
}