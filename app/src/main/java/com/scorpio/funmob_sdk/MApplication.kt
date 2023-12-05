package com.scorpio.funmob_sdk

import android.app.Application
import com.funsol.funmob_sdk.FunMobAds
import com.funsol.funmob_sdk.appopenad.FunAppOpenManager

class MApplication : Application(){
    private val funMobAds: FunMobAds by lazy { FunMobAds() }
    override fun onCreate() {
        super.onCreate()
        FunAppOpenManager(this,funMobAds,Constants.authorization,Constants.appOpenAdId)
    }
}