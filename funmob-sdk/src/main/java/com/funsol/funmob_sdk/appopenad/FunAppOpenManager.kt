package com.funsol.funmob_sdk.appopenad

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.funsol.funmob_sdk.FunMobAds
import com.funsol.funmob_sdk.interfaces.FunAppOpenCallback
import com.funsol.funmob_sdk.model.CampaignResponse
import com.funsol.funmob_sdk.ui.AppOpenActivity
import java.util.*
import java.util.concurrent.TimeUnit

//*Prefetches App Open Ads.
open class FunAppOpenManager(private val myApplication: Application, private val funMobAds: FunMobAds, private val authorization: String, private val app_open_ad_id: String) :
    LifecycleObserver, ActivityLifecycleCallbacks {

    private var currentActivity: Activity? = null
    private var loadTime: Long = 0 // to keep track of time because ad expires 4 hours after loading
    private var alreadyShown = false

    //*LifecycleObserver methods
    private val funAppOpenCallback: FunAppOpenCallback = object : FunAppOpenCallback {
        override fun onAdLoaded(campaignResponse: CampaignResponse) {
            Log.i(TAG, "AppOpenCallback -> onAdLoaded:")
            appOpenAd = campaignResponse
        }
        override fun onAdFailed(adError: String) {
            Log.i(TAG, "AppOpenCallback -> onAdFailed: $adError")
        }
        override fun onAdDismissed() {
            Log.i(TAG, "AppOpenCallback -> onAdDismissed:")
            alreadyShown = false
        }
        override fun onAdShown() {
            alreadyShown = true
            Log.i(TAG, "AppOpenCallback -> onAdShown:")
        }
        override fun onAdClicked() {
            Log.i(TAG, "AppOpenCallback -> onAdClicked:")
        }
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onStart() {
        if (!isPremium) {
            showAdIfAvailable()
        }
        Log.d(TAG, "FunAppOpen onStart")
    }
    //*Request an ad
    private fun fetchAd() {
        if (!isPremium && appOpenAd ==null) {
            funMobAds.loadFunAppOpenAd(authorization, app_open_ad_id)
        }
    }
    private fun isAdAvailable():Boolean{
        return  appOpenAd !=null
    }
    //*Utility method to check if ad was loaded more than n hours ago.
    private fun showAdIfAvailable() {
        if (
            !isPremium
            /*&& timeDifference(appOpenTimeElapsed) > appOpenCapTime
            && timeDifference(interstitialTimeElapsed) > appOpenCapTime*/
        ) {
            // Only show ad if there is not already an app open ad currently showing
            // and an ad is available.
            if (isAdAvailable()){
                if (!isShowingAd && adShow) {
                if(!alreadyShown){
                    currentActivity?.let { appOpenAd?.let { it1 -> funMobAds.showAppOpenAd(it, it1) } }
                }
                else {
                    Log.d(TAG, "FunAppOpen already show")
                }
            }
                else{
                Log.d(TAG, "FunAppOpen adShow -> $adShow and isShowingAd -> $isShowingAd")
                }
            }
            else {
                Log.d(TAG, "FunAppOpen not available")
                fetchAd()
            }
        }
        else{
            Log.i(TAG, "FunAppOpen premium user")
        }
    }
    private fun wasLoadTimeLessThanNHoursAgo(numHours: Long): Boolean {
        val dateDifference = Date().time - loadTime
        val numMilliSecondsPerHour: Long = 3600000
        return dateDifference < numMilliSecondsPerHour * numHours
    }

    //*Utility method that checks if ad exists and can be shown.
    /*val isAdAvailable: Boolean
        get() = appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4)*/
    //*Shows the ad if one isn't already showing.

    private fun timeDifference(millis: Long): Int {
        val current = Calendar.getInstance().timeInMillis
        val elapsedTime = current - millis

        return TimeUnit.MILLISECONDS.toSeconds(elapsedTime).toInt()
    }
    //*Creates and returns ad request .
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {
        if (activity !is AppOpenActivity) currentActivity = activity
    }
    override fun onActivityResumed(activity: Activity) {
        if (activity !is AppOpenActivity) currentActivity = activity
    }
    override fun onActivityPaused(activity: Activity) {
        if (activity !is AppOpenActivity) currentActivity = activity
    }
    override fun onActivityStopped(activity: Activity) {
        if (activity !is AppOpenActivity) currentActivity = activity
    }
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {
        if (activity !is AppOpenActivity) currentActivity = activity
    }
    override fun onActivityDestroyed(activity: Activity) {
        currentActivity = null
    }
    companion object {
        private var appOpenAd: CampaignResponse? = null
        private const val TAG = "FunMobTags"
        private const val appOpenCapTime = 10
        private var appOpenTimeElapsed = 0L
        var isShowingAd = false
        var adShow = true
        var isPremium = false
    }
    init {
        myApplication.registerActivityLifecycleCallbacks(this) //register interface for current activity to listen to all current activity events.
        ProcessLifecycleOwner.get().lifecycle.addObserver(this) //listen for foregrounding events in your
        funMobAds.funAppOpenCallback = funAppOpenCallback
        fetchAd()
    }
}