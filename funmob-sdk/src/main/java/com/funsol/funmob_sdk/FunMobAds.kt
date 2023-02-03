package com.funsol.funmob_sdk

import android.app.Activity
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.funsol.funmob_sdk.databinding.ActivityInterstitialBinding
import com.funsol.funmob_sdk.interfaces.FunBannerCallback
import com.funsol.funmob_sdk.interfaces.FunInterstitialCallback
import com.funsol.funmob_sdk.interfaces.FunNativeCallback
import com.funsol.funmob_sdk.model.CampaignResponse
import com.funsol.funmob_sdk.retrofit.DefaultFunMobRepository
import com.funsol.funmob_sdk.utils.Constants
import com.funsol.funmob_sdk.utils.NativeAdDesignUtils
import com.funsol.funmob_sdk.utils.NumberConverter
import com.funsol.funmob_sdk.utils.Resource
import com.google.firebase.FirebaseApp
import com.google.firebase.dynamiclinks.PendingDynamicLinkData
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.tencent.mmkv.MMKV
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URLEncoder


class FunMobAds {
    private var sharedPreferences: SharedPreferences? = null
    private var mmkv: MMKV? = null

    private val repository: DefaultFunMobRepository = DefaultFunMobRepository()

    var funNativeCallback: FunNativeCallback? = null
    var funInterstitialCallback: FunInterstitialCallback? = null

    private var authorization: String = ""

    fun loadFunNativeAd(authorization: String, native_ad_id: String) {
        CoroutineScope(IO).launch {
            this@FunMobAds.authorization = authorization
            if (native_ad_id.contains("native")) {
                when (val campaignResponse = repository.loadFunAd(authorization, native_ad_id)) {
                    is Resource.Error -> {
                        Log.i(TAG, "loadFunNativeAd error->->->->: ${campaignResponse.data}\n ${campaignResponse.message}")
                        funNativeCallback?.onAdFailed(campaignResponse.message.toString())
                    }
                    is Resource.Success -> {
                        Log.i(TAG, "loadFunNativeAd success->->->->: ${campaignResponse.data}\n ${campaignResponse.message}")
                        val campaign = campaignResponse.data

                        withContext(Dispatchers.Main) {
                            if (campaign != null) {
                                funNativeCallback?.onAdLoaded(campaign)
                            } else {
                                funNativeCallback?.onAdFailed("Unexpected Error")
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
                when (val campaignResponse = repository.loadFunAd(authorization, interstitial_ad_id)) {
                    is Resource.Error -> funInterstitialCallback?.onAdFailed(campaignResponse.message.toString())
                    is Resource.Success -> {
                        val campaign = campaignResponse.data

                        withContext(Dispatchers.Main) {
                            if (campaign != null) {
                                funInterstitialCallback?.onAdLoaded(campaign)
                            } else {
                                funInterstitialCallback?.onAdFailed("Unexpected Error")
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
                when (val campaignResponse = repository.loadFunAd(authorization, banner_ad_id)) {
                    is Resource.Error -> funBannerCallback.onAdFailed(campaignResponse.message.toString())
                    is Resource.Success -> {
                        val campaign = campaignResponse.data

                        withContext(Dispatchers.Main) {
                            if (campaign != null) {
                                funBannerCallback.onAdLoaded()
                                showBannerAd(activity, campaign, parentView, funBannerCallback)
                            } else {
                                funBannerCallback.onAdFailed("Unexpected Error")
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

        var domain = campaignResponse.domain
        if (!domain.endsWith("/"))
            domain += "/"

        val link = "${campaignResponse.deeplink}?ad_unit_id=${campaignResponse.ad_unit_id}&combination_id=${campaignResponse.combination_id}"

        val encodedUrl = URLEncoder.encode(link, "UTF-8")

        val finalLink = "${campaignResponse.domain}?link=$encodedUrl&apn=${campaignResponse.package_name}".replace("?link=+", "?link=")

        sendLog("goToPlayStore: a$finalLink")
        openFinalLink(activity, finalLink)
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
        MMKV.initialize(activity)
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
            .addOnFailureListener(activity) { e -> sendLog("getDynamicLink:onFailure", e) }
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