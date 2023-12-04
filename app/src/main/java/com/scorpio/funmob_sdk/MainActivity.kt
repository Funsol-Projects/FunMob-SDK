package com.scorpio.funmob_sdk

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.funsol.funmob_sdk.FunMobAds
import com.funsol.funmob_sdk.interfaces.FunAppOpenCallback
import com.funsol.funmob_sdk.interfaces.FunBannerCallback
import com.funsol.funmob_sdk.interfaces.FunInterstitialCallback
import com.funsol.funmob_sdk.interfaces.FunNativeCallback
import com.funsol.funmob_sdk.model.CampaignResponse
import com.google.firebase.FirebaseApp
import com.scorpio.funmob_sdk.Constants.authorization
import com.scorpio.funmob_sdk.Constants.bannerAdId2
import com.scorpio.funmob_sdk.Constants.interstitialAdId
import com.scorpio.funmob_sdk.Constants.nativeAdId
import com.scorpio.funmob_sdk.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private val funMobAds: FunMobAds by lazy { FunMobAds() }

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private var type = "1a"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        funMobAds.funNativeCallback = funNativeCallback
        funMobAds.funInterstitialCallback = funInterstitialCallback
        funMobAds.funAppOpenCallback = funAppOpenCallback
        FirebaseApp.initializeApp(this)

        binding.btnLoadInterstitial.setOnClickListener {
            funMobAds.loadFunInterstitialAd(authorization, interstitialAdId)
        }

        binding.btnLoadNative1.setOnClickListener {
            funMobAds.loadFunNativeAd(authorization, nativeAdId)
        }
        binding.btnLoadAppOpen.setOnClickListener {
            funMobAds.loadFunAppOpenAd(authorization, Constants.appOpenAdId)
        }

        funMobAds.loadFunBannerAd(
            authorization,
            bannerAdId2,
            this,
            binding.placeholderBanner,
            object : FunBannerCallback {
                override fun onAdLoaded() {
                    Log.i(TAG, "MyBannerTag -> onAdLoaded: ")
                }

                override fun onAdFailed(adError: String) {
                    Log.i(TAG, "MyBannerTag -> onAdFailed: ")
                }

                override fun onAdShown() {
                    Log.i(TAG, "MyBannerTag -> onAdShown: ")

                }

                override fun onAdClicked() {
                    Log.i(TAG, "MyBannerTag -> onAdClicked: ")
                }
            })
    }

    private val funNativeCallback: FunNativeCallback = object : FunNativeCallback {
        override fun onAdLoaded(campaignResponse: CampaignResponse) {
            Log.i(TAG, "NativeCallback -> onAdLoaded: $campaignResponse")

            funMobAds.showNativeAd(
                this@MainActivity,
                campaignResponse,
                binding.placeholderNative1,
                type
            )

            type = when (type) {
                "1a" -> {
                    binding.btnLoadNative1.text = "Load Native 1b"
                    "1b"
                }

                "1b" -> {
                    binding.btnLoadNative1.text = "Load Native 7a"
                    "7a"
                }

                "7a" -> {
                    binding.btnLoadNative1.text = "Load Native 7b"
                    "7b"
                }

                "7b" -> {
                    binding.btnLoadNative1.text = "Load Native 6a"
                    "6a"
                }

                "6a" -> {
                    binding.btnLoadNative1.text = "Load Native 6b"
                    "6b"
                }

                "6b" -> {
                    binding.btnLoadNative1.text = "Load Native 1a"
                    "1a"
                }

                else -> {
                    binding.btnLoadNative1.text = "Load Native 1a"
                    "1a"
                }
            }
        }

        override fun onAdFailed(adError: String) {
            Log.i(TAG, "NativeCallback -> onAdFailed: $adError")
        }

        override fun onAdShown() {
            Log.i(TAG, "NativeCallback -> onAdShown:")
        }

        override fun onAdClicked() {
            Log.i(TAG, "NativeCallback -> onAdClicked:")
        }
    }

    private val funInterstitialCallback: FunInterstitialCallback =
        object : FunInterstitialCallback {
            override fun onAdLoaded(campaignResponse: CampaignResponse) {
                Log.i(TAG, "InterstitialCallback -> onAdLoaded:")
                Toast.makeText(this@MainActivity, "Interstital Ad Loaded", Toast.LENGTH_SHORT)
                    .show()
                binding.btnShowInterstitial.setOnClickListener { _ ->
                    funMobAds.showInterstitialAd(this@MainActivity, campaignResponse)
                }
            }

            override fun onAdFailed(adError: String) {
                Log.i(TAG, "InterstitialCallback -> onAdFailed: $adError")
            }

            override fun onAdDismissed() {
                Log.i(TAG, "InterstitialCallback -> onAdDismissed:")
            }

            override fun onAdShown() {
                Log.i(TAG, "InterstitialCallback -> onAdShown:")
            }

            override fun onAdClicked() {
                Log.i(TAG, "InterstitialCallback -> onAdClicked:")
            }
        }

    private val funAppOpenCallback: FunAppOpenCallback = object : FunAppOpenCallback {
        override fun onAdLoaded(campaignResponse: CampaignResponse) {
            Log.i(TAG, "AppOpenCallback -> onAdLoaded:")
            Toast.makeText(this@MainActivity, "App Open Ad Loaded", Toast.LENGTH_SHORT).show()
            binding.btnShowAppOpen.setOnClickListener {
                funMobAds.showAppOpenAd(this@MainActivity, campaignResponse)
            }
        }

        override fun onAdFailed(adError: String) {
            Log.i(TAG, "AppOpenCallback -> onAdFailed: $adError")
        }

        override fun onAdDismissed() {
            Log.i(TAG, "AppOpenCallback -> onAdDismissed:")
        }

        override fun onAdShown() {
            Log.i(TAG, "AppOpenCallback -> onAdShown:")
        }

        override fun onAdClicked() {
            Log.i(TAG, "AppOpenCallback -> onAdClicked:")
        }

    }

    companion object {
        const val TAG = "FunMobTags"
    }
}