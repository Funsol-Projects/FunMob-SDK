package com.funsol.funmob_sdk

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.bumptech.glide.Glide
import com.funsol.funmob_sdk.databinding.ActivityAppOpenBinding
import com.funsol.funmob_sdk.databinding.ActivityInterstitialBinding
import com.funsol.funmob_sdk.interfaces.FunAppOpenCallback
import com.funsol.funmob_sdk.interfaces.FunBannerCallback
import com.funsol.funmob_sdk.interfaces.FunInterstitialCallback
import com.funsol.funmob_sdk.interfaces.FunNativeCallback
import com.funsol.funmob_sdk.model.CampaignResponse
import com.funsol.funmob_sdk.retrofit.DefaultFunMobRepository
import com.funsol.funmob_sdk.utils.NativeAdDesignUtils
import com.funsol.funmob_sdk.utils.NumberConverter
import com.funsol.funmob_sdk.utils.Resource
import com.tencent.mmkv.MMKV
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.AccessController.getContext


class FunMobAds {
    private var sharedPreferences: SharedPreferences? = null
    private var mmkv: MMKV? = null

    private val repository: DefaultFunMobRepository = DefaultFunMobRepository()

    var funNativeCallback: FunNativeCallback? = null
    var funInterstitialCallback: FunInterstitialCallback? = null
    var funAppOpenCallback: FunAppOpenCallback? = null

    private var authorization: String = ""

    fun loadFunNativeAd(authorization: String, native_ad_id: String) {
        CoroutineScope(IO).launch {
            this@FunMobAds.authorization = authorization
            if (native_ad_id.contains("native")) {
                repository.loadFunAd(authorization, native_ad_id) { campaignResponse ->
                    when (campaignResponse) {
                        is Resource.Error -> {
                            Log.i(TAG, "loadFunNativeAd error->->->->: ${campaignResponse.message}")
                            funNativeCallback?.onAdFailed(campaignResponse.message.toString())
                        }

                        is Resource.Success -> {
                            Log.i(TAG, "loadFunNativeAd success->->->->: ${campaignResponse.message}")
                            val campaign = campaignResponse.data
                            CoroutineScope(Dispatchers.Main).launch {
                                if (campaign != null) {
                                    funNativeCallback?.onAdLoaded(campaign)
                                } else {
                                    funNativeCallback?.onAdFailed("Unexpected Error")
                                }
                            }
                        }
                    }
                }
            } else {
                funNativeCallback?.onAdFailed("Invalid Advertisement Id…")
            }
        }
    }

    fun showNativeAd(activity: Activity, campaignResponse: CampaignResponse, parentView: FrameLayout, type: String, nativeAdDesignUtils: NativeAdDesignUtils? = null) {
        parentView.removeAllViews()

        val inflater = LayoutInflater.from(activity)
        val view = when (type) {
            "1a" -> {
                inflater.inflate(R.layout.funmob_native_1a, parentView, false)
            }
            "1b" -> {
                inflater.inflate(R.layout.funmob_native_1b, parentView, false)
            }
            "7a" -> {
                inflater.inflate(R.layout.funmob_native_7a, parentView, false)
            }
            "7b" -> {
                inflater.inflate(R.layout.funmob_native_7b, parentView, false)
            }
            "6a"->{
                inflater.inflate(R.layout.funmob_native_6a, parentView, false)
            }
            "6b"->{
                inflater.inflate(R.layout.funmob_native_6b, parentView, false)
            }
            else -> {
                inflater.inflate(R.layout.funmob_native_1a, parentView, false)
            }
        }

        view.findViewById<TextView>(R.id.ad_headline)?.let {
            nativeAdDesignUtils?.heading_color?.let { color ->
                if (color.isNotEmpty()) {
                    val mColor = color.replace("\"", "")
                    it.setTextColor(Color.parseColor(mColor))
                }
            }

            it.text = campaignResponse.title
            it.setOnClickListener {
                funNativeCallback?.onAdClicked()
                goToPlayStore(activity, campaignResponse)
            }
        }

        view.findViewById<TextView>(R.id.ad_body)?.let {

            nativeAdDesignUtils?.body_color?.let { color ->
                if (color.isNotEmpty()) {
                    val mColor = color.replace("\"", "")
                    it.setTextColor(Color.parseColor(mColor))
                }
            }

            it.text = campaignResponse.description
            it.setOnClickListener {
                funNativeCallback?.onAdClicked()
                goToPlayStore(activity, campaignResponse)
            }
        }

        view.findViewById<FrameLayout>(R.id.ifv_ad_call_to_action)?.let {
//            nativeAdDesignUtils?.cta_round?.let { round ->
//                it.roundPercent = round
//            }

            nativeAdDesignUtils?.cta_color?.let { color ->
                if (color.isNotEmpty()) {
                    val mColor = color.replace("\"", "")
                    it.setBackgroundColor(Color.parseColor(mColor))
                }
            }
        }

        view.findViewById<TextView>(R.id.ad_call_to_action)?.let {
            nativeAdDesignUtils?.cta_text_color?.let { color ->
                if (color.isNotEmpty()) {
                    val mColor = color.replace("\"", "")
                    it.setTextColor(Color.parseColor(mColor))
                }
            }

            it.setOnClickListener {
                funNativeCallback?.onAdClicked()
                goToPlayStore(activity, campaignResponse)
            }
        }

        view.findViewById<ImageView>(R.id.media_background)?.let {
            Glide.with(activity.applicationContext).load(campaignResponse.image).transform(BlurTransformation(25, 3)).into(it)
            it.setOnClickListener {
                funNativeCallback?.onAdClicked()
                goToPlayStore(activity, campaignResponse)
            }
        }

        view.findViewById<ImageView>(R.id.ad_media_img)?.let {
            Glide.with(activity.applicationContext).load(campaignResponse.image).into(it)
        }

        view.findViewById<ImageView>(R.id.ad_app_icon)?.let {
            Glide.with(activity.applicationContext).load(campaignResponse.icon).into(it)
            it.setOnClickListener {
                funNativeCallback?.onAdClicked()
                goToPlayStore(activity, campaignResponse)
            }
        }

        funNativeCallback?.onAdShown()
        parentView.addView(view)
        CoroutineScope(IO).launch { validateImpression(authorization, campaignResponse.ad_unit_id, campaignResponse.combination_id) }
    }

    fun loadFunInterstitialAd(authorization: String, interstitial_ad_id: String) {
        CoroutineScope(IO).launch {
            this@FunMobAds.authorization = authorization
            if (interstitial_ad_id.contains("interstitial")) {
                repository.loadFunAd(authorization, interstitial_ad_id) { campaignResponse ->
                    when (campaignResponse) {
                        is Resource.Error -> {
                            Log.i(TAG, "loadFunInterstitialAd error->->->->: ${campaignResponse.message}")
                            funInterstitialCallback?.onAdFailed(campaignResponse.message.toString())
                        }
                        is Resource.Success -> {
                            Log.i(TAG, "loadFunInterstitialAd success->->->->: ${campaignResponse.message}")
                            val campaign = campaignResponse.data
                            CoroutineScope(Dispatchers.Main).launch {
                                if (campaign != null) {
                                    funInterstitialCallback?.onAdLoaded(campaign)
                                } else {
                                    funInterstitialCallback?.onAdFailed("Unexpected Error")
                                }
                            }
                        }
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    funInterstitialCallback?.onAdFailed("Invalid Advertisement Id…")
                }
            }
        }
    }

    fun showInterstitialAd(activity: Activity, campaignResponse: CampaignResponse) {
        if (!activity.isFinishing && !activity.isDestroyed) {
            val interstitialDialog = Dialog(activity, R.style.full_screen_dialog)
            val interstitialDialogBinding = ActivityInterstitialBinding.inflate(activity.layoutInflater)

            interstitialDialog.setContentView(interstitialDialogBinding.root)
            interstitialDialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)

            with(interstitialDialogBinding) {

                fun onClick(view: View) {
                    view.setOnClickListener {
                        funInterstitialCallback?.onAdClicked()
                        goToPlayStore(activity, campaignResponse)
                        interstitialDialog.dismiss()
                    }
                }

                appName.let {
                    it.text = campaignResponse.title
                    onClick(it)
                }

                totalDownloads.let {
                    it.text = NumberConverter().format(campaignResponse.downloads)
                    onClick(it)
                }

                onClick(downloadsHeading)

                totalRating.let {
                    it.text = StringBuilder().append(campaignResponse.ratings)
                    onClick(it)
                }

                onClick(ratingHeading)

                onClick(appPrice)
                onClick(priceHeading)
                onClick(playIcon)
                onClick(textPlayStore)

                Glide.with(activity.applicationContext)
                    .load(campaignResponse.icon).into(appIcon)


                appIcon.setOnClickListener {
                    funInterstitialCallback?.onAdClicked()
                    goToPlayStore(activity, campaignResponse)
                    interstitialDialog.dismiss()
                }

                btnInstall.setOnClickListener {
                    funInterstitialCallback?.onAdClicked()
                    goToPlayStore(activity, campaignResponse)
                    interstitialDialog.dismiss()
                }

                btnClose.setOnClickListener {
                    funInterstitialCallback?.onAdDismissed()
                    interstitialDialog.dismiss()
                }
            }

            funInterstitialCallback?.onAdShown()
            CoroutineScope(IO).launch { validateImpression(authorization, campaignResponse.ad_unit_id, campaignResponse.combination_id) }
            interstitialDialog.show()
        }
    }

    fun loadFunBannerAd(authorization: String, banner_ad_id: String, activity: Activity, parentView: FrameLayout, funBannerCallback: FunBannerCallback) {
        CoroutineScope(IO).launch {
            this@FunMobAds.authorization = authorization
            if (banner_ad_id.contains("banner")) {
                repository.loadFunAd(authorization, banner_ad_id) { campaignResponse ->
                    when (campaignResponse) {
                        is Resource.Error -> {
                            Log.i(TAG, "loadFunBannerAd error->->->-> ${campaignResponse.message}")
                            funBannerCallback.onAdFailed(campaignResponse.message.toString())
                        }

                        is Resource.Success -> {
                            Log.i(TAG, "loadFunBannerAd success->->->->: ${campaignResponse.message}")
                            val campaign = campaignResponse.data

                            CoroutineScope(Dispatchers.Main).launch {
                                if (campaign != null) {
                                    funBannerCallback.onAdLoaded()
                                    showBannerAd(activity, campaign, parentView, funBannerCallback)
                                } else {
                                    funBannerCallback.onAdFailed("Unexpected Error")
                                }
                            }
                        }
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    funBannerCallback.onAdFailed("Invalid Advertisement Id…")
                }
            }
        }
    }

    private fun showBannerAd(activity: Activity, campaignResponse: CampaignResponse, parentView: FrameLayout, funBannerCallback: FunBannerCallback) {
        parentView.removeAllViews()

        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.funmob_banner, parentView, false)

        view.findViewById<TextView>(R.id.ad_headline)?.let {
            /*nativeAdDesignUtils?.heading_color?.let { color ->
                if (color.isNotEmpty()) {
                    val mColor = color.replace("\"", "")
                    it.setTextColor(Color.parseColor(mColor))
                }
            }*/

            it.text = campaignResponse.title
            it.setOnClickListener {
                funBannerCallback.onAdClicked()
                goToPlayStore(activity, campaignResponse)
            }
        }

        view.findViewById<TextView>(R.id.ad_body)?.let {
            /*nativeAdDesignUtils?.body_color?.let { color ->
                if (color.isNotEmpty()) {
                    val mColor = color.replace("\"", "")
                    it.setTextColor(Color.parseColor(mColor))
                }
            }*/

            it.text = campaignResponse.description
            it.setOnClickListener {
                funBannerCallback.onAdClicked()
                goToPlayStore(activity, campaignResponse)
            }
        }

        view.findViewById<TextView>(R.id.ad_call_to_action)?.let {
            /*nativeAdDesignUtils?.cta_text_color?.let { color ->
                if (color.isNotEmpty()) {
                    val mColor = color.replace("\"", "")
                    it.setTextColor(Color.parseColor(mColor))
                }
            }

            nativeAdDesignUtils?.cta_color?.let { color ->
                if (color.isNotEmpty()) {
                    val mColor = color.replace("\"", "")
                    it.setBackgroundColor(Color.parseColor(mColor))
                }
            }*/

            it.setOnClickListener {
                funBannerCallback.onAdClicked()
                goToPlayStore(activity, campaignResponse)
            }
        }

        view.findViewById<ImageView>(R.id.ad_app_icon)?.let {
            Glide.with(activity.applicationContext).load(campaignResponse.icon).into(it)
            it.setOnClickListener {
                funBannerCallback.onAdClicked()
                goToPlayStore(activity, campaignResponse)
            }
        }

        parentView.addView(view)
        funBannerCallback.onAdShown()
        CoroutineScope(IO).launch { validateImpression(authorization, campaignResponse.ad_unit_id, campaignResponse.combination_id) }
    }
    fun loadFunAppOpenAd(authorization: String, app_open_ad_id: String) {
        CoroutineScope(IO).launch {
            this@FunMobAds.authorization = authorization
            if (app_open_ad_id.contains("app-open")) {
                repository.loadFunAd(authorization, app_open_ad_id) { campaignResponse ->
                    when (campaignResponse) {
                        is Resource.Error -> {
                            Log.i(TAG, "loadFunAppOpenAd error->->->->: ${campaignResponse.message}")
                            funAppOpenCallback?.onAdFailed(campaignResponse.message.toString())
                        }
                        is Resource.Success -> {
                            Log.i(TAG, "loadFunAppOpenAd success->->->->: ${campaignResponse.message}")
                            val campaign = campaignResponse.data
                            CoroutineScope(Dispatchers.Main).launch {
                                if (campaign != null) {
                                    funAppOpenCallback?.onAdLoaded(campaign)
                                } else {
                                    funAppOpenCallback?.onAdFailed("Unexpected Error")
                                }
                            }
                        }
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    funAppOpenCallback?.onAdFailed("Invalid Advertisement Id…")
                }
            }
        }
    }
    fun showAppOpenAd(activity: Activity, campaignResponse: CampaignResponse){
        if (!activity.isFinishing && !activity.isDestroyed) {
            val appOpenDialog = Dialog(activity, R.style.full_screen_dialog)
            val appOpenDialogBinding = ActivityAppOpenBinding.inflate(activity.layoutInflater)

            appOpenDialog.setContentView(appOpenDialogBinding.root)
            appOpenDialog.window?.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
            appOpenDialog.setOnDismissListener {
                funAppOpenCallback?.onAdDismissed()
            }

            with(appOpenDialogBinding) {
                try {
                    val packageManager = activity.applicationContext.packageManager
                    val appInfo = packageManager.getApplicationInfo(activity.applicationContext.packageName, 0);
                    val icon: Drawable = packageManager.getApplicationIcon(activity.applicationContext.packageName)
                    appName.let {
                        if(appInfo != null){
                            it.text =packageManager.getApplicationLabel(appInfo)
                        }
                        else{
                            it.text = "Unknown"
                        }
                    }
                    Glide.with(activity.applicationContext)
                        .load(icon).into(parentAppIcon)
                }
                catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                }
                fun onClick(view: View) {
                    view.setOnClickListener {
                        funAppOpenCallback?.onAdClicked()
                        goToPlayStore(activity, campaignResponse)
//                        appOpenDialog.dismiss()
                    }
                }
                title.let {
                    it.text = campaignResponse.title
                    onClick(it)
                }
                adBody.let {
                    it.text = campaignResponse.description
                    onClick(it)
                }

             /*
                totalDownloads.let {
                    it.text = NumberConverter().format(campaignResponse.downloads)
                    onClick(it)
                }

                onClick(downloadsHeading)

                totalRating.let {
                    it.text = StringBuilder().append(campaignResponse.ratings)
                    onClick(it)
                }

                onClick(ratingHeading)

                onClick(appPrice)
                onClick(priceHeading)
                onClick(playIcon)
                onClick(textPlayStore)*/

                adMedia.let {
                    Glide.with(activity.applicationContext)
                        .load(campaignResponse.image).into(it)
                    onClick(it)
                }
                Glide.with(activity.applicationContext)
                    .load(campaignResponse.icon).into(appIcon)


               /* appIcon.setOnClickListener {
                    funInterstitialCallback?.onAdClicked()
                    goToPlayStore(activity, campaignResponse)
                    interstitialDialog.dismiss()
                }

                btnInstall.setOnClickListener {
                    funInterstitialCallback?.onAdClicked()
                    goToPlayStore(activity, campaignResponse)
                    interstitialDialog.dismiss()
                }*/

                continueToAppCard.setOnClickListener {
                    appOpenDialog.dismiss()
                }
            }

            funAppOpenCallback?.onAdShown()
            CoroutineScope(IO).launch { validateImpression(authorization, campaignResponse.ad_unit_id, campaignResponse.combination_id) }
            appOpenDialog.show()
        }
    }
    private suspend fun validateClick(authorization: String, uuid: String, camp_uuid: String) {
        repository.validateClick(authorization, uuid, camp_uuid)
    }

    private suspend fun validateImpression(authorization: String, uuid: String, camp_uuid: String) {
        repository.validateImpression(authorization, uuid, camp_uuid)
    }

    private fun validateInstall(authorization: String, ad_unit_id: String, combination_id: String) {
        CoroutineScope(IO).launch {
            repository.validateInstall(authorization, ad_unit_id, combination_id)
        }
    }

    fun registerApp(authorization: String, uuid: String) {
        CoroutineScope(IO).launch {
            repository.registerApp(authorization, uuid)
        }
    }

    fun unRegisterApp(authorization: String, uuid: String) {
        CoroutineScope(IO).launch {
            repository.unRegisterApp(authorization, uuid)
        }
    }

    private fun goToPlayStore(activity: Activity, campaignResponse: CampaignResponse) {
        CoroutineScope(IO).launch { validateClick(authorization, campaignResponse.ad_unit_id, campaignResponse.combination_id) }

        val playStoreUrl = StringBuilder().append("https://play.google.com/store/apps/details?id=")
            .append(campaignResponse.package_name)
            .append("&")
            .append("referrer=utm_source")
            .append("%3D")
            .append("referral")
            .append("%26")
            .append("utm_medium")
            .append("%3D")
            .append("ad_click")
            .append("%26")
            .append("utm_campaign")
            .append("%3D")
            .append("friend_referral")
            .append("%26")
            .append("referral_code")
            .append("%3D")
            .append("ABC123").toString()

        openFinalLink(activity, playStoreUrl)
    }

    private fun openFinalLink(activity: Activity, path: String) {
        val uri = Uri.parse(path)
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        try {
            activity.startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            activity.startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
    }

    fun setupAppForInstall(authorization: String, activity: Activity, intent: Intent) {
        val referrerClient = InstallReferrerClient.newBuilder(activity).build()
        referrerClient.startConnection(object : InstallReferrerStateListener {
            override fun onInstallReferrerSetupFinished(responseCode: Int) {
                when (responseCode) {
                    InstallReferrerClient.InstallReferrerResponse.OK -> {
                        // The connection was successful.
                        // Get the referrer URL using the getInstallReferrer() method.
                        val response = referrerClient.installReferrer
                        val referrerUrl = response.installReferrer

                        Log.i("TAG", "onInstallReferrerSetupFinished: ${response.installBeginTimestampSeconds}")
                        Log.i("TAG", "onInstallReferrerSetupFinished: ${response.installVersion}")
                        Log.i("TAG", "onInstallReferrerSetupFinished: ${response.googlePlayInstantParam}")
                        Log.i("TAG", "onInstallReferrerSetupFinished: ${response.referrerClickTimestampSeconds}")
                        Log.i("TAG", "onInstallReferrerSetupFinished: ${response.referrerClickTimestampServerSeconds}")
                        Log.i("TAG", "onInstallReferrerSetupFinished: $referrerUrl")

                        val source = referrerUrl.substringBefore("&").substringAfter("=")
                        val medium = referrerUrl.substringAfter("&").substringAfter("=")

                        if (source == "referral" && medium == "ad_click") {
//                            validateInstall(authorization, adUnitId, combinationId)
                        }
                    }

                    InstallReferrerClient.InstallReferrerResponse.FEATURE_NOT_SUPPORTED -> {}
                    InstallReferrerClient.InstallReferrerResponse.SERVICE_UNAVAILABLE -> {}
                    else -> {}
                }
                // Disconnect from the InstallReferrer service.
                referrerClient.endConnection()
            }

            override fun onInstallReferrerServiceDisconnected() {
                // Retry connecting to the InstallReferrer service.
            }
        })


        /*MMKV.initialize(activity)
        mmkv = MMKV.defaultMMKV()
        sharedPreferences = activity.getSharedPreferences(Constants.FunMobPrefKey, Context.MODE_PRIVATE)

        FirebaseApp.initializeApp(activity)

        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(activity) { pendingDynamicLinkData: PendingDynamicLinkData? ->
                // Get deep link from result (may be null if no link is found)
                sendLog("getDynamicLink:onSuccess")

                val deepLink: Uri?
                if (pendingDynamicLinkData != null) {
                    deepLink = pendingDynamicLinkData.link

                    sendLog("getDynamicLink:onSuccess1 ${pendingDynamicLinkData.link}")
                    sendLog("getDynamicLink:onSuccess5 -> ${deepLink?.queryParameterNames}")

                    if (deepLink?.queryParameterNames?.contains("ad_unit_id") == true)
                        sendLog("getDynamicLinks: found ad_unit_id -> ${deepLink.getQueryParameter("ad_unit_id")}")

                    if (deepLink?.queryParameterNames?.contains("combination_id") == true)
                        sendLog("getDynamicLinks: found combination_id -> ${deepLink.getQueryParameter("combination_id")}")

                    val adUnitId = deepLink?.getQueryParameter("ad_unit_id") ?: ""
                    val combinationId = deepLink?.getQueryParameter("combination_id") ?: ""

                    if (getDynamicFirstTime() && adUnitId != "" && combinationId != "") {
                        validateInstall(authorization, adUnitId, combinationId)
                        sendLog("getDynamicLink: Install Validated $adUnitId, $combinationId")
                    }

                }
                setDynamicFirstTime(false)
            }
            .addOnFailureListener(activity) { e -> sendLog("getDynamicLink:onFailure", e) }*/
    }

    private fun setDynamicFirstTime(value: Boolean) {
        mmkv?.encode("DynamicFirstTime", value)
    }

    private fun getDynamicFirstTime(): Boolean {
        return mmkv?.decodeBool("DynamicFirstTime") ?: true
    }

    private fun sendLog(message: String, throwable: Throwable? = null) {
        if (BuildConfig.DEBUG) {
            if (throwable != null)
                Log.d(TAG, "sendLog: $message", throwable)
            else
                Log.d(TAG, "sendLog: $message")
        }
    }

    companion object {
        private const val TAG = "FunMobTags"
    }
}