package com.funsol.funmob_sdk.interfaces

interface FunBannerCallback {
    fun onAdLoaded()
    fun onAdFailed(adError: String)
    fun onAdShown()
    fun onAdClicked()
}