package com.funsol.funmob_sdk.interfaces

import com.funsol.funmob_sdk.model.CampaignResponse

interface FunAppOpenCallback {
    fun onAdLoaded(campaignResponse: CampaignResponse)
    fun onAdFailed(adError: String)
    fun onAdDismissed()
    fun onAdShown()
    fun onAdClicked()
}