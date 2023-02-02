package com.scorpio.funmob_sdk

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.funsol.funmob_sdk.FunMobAds
import com.funsol.funmob_sdk.interfaces.FunBannerCallback
import com.funsol.funmob_sdk.interfaces.FunInterstitialCallback
import com.funsol.funmob_sdk.interfaces.FunNativeCallback
import com.funsol.funmob_sdk.model.CampaignResponse
import com.google.firebase.FirebaseApp
import com.scorpio.funmob_sdk.Constants.bannerAdId
import com.scorpio.funmob_sdk.Constants.interstitialAdId
import com.scorpio.funmob_sdk.Constants.nativeAdId
import com.scorpio.funmob_sdk.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {

    private val funMobAds: FunMobAds = FunMobAds()

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private var type = "1a"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        funMobAds.funNativeCallback = funNativeCallback
        funMobAds.funInterstitialCallback = funInterstitialCallback

        FirebaseApp.initializeApp(this)

        binding.btnLoadInterstitial.setOnClickListener {
            funMobAds.loadFunInterstitialAd(Constants.authorization, interstitialAdId)
        }

        binding.btnLoadNative1.setOnClickListener {
            funMobAds.loadFunNativeAd(Constants.authorization, nativeAdId)
        }

        funMobAds.loadFunBannerAd(Constants.authorization, bannerAdId, this, binding.placeholderBanner, object : FunBannerCallback {
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

            funMobAds.showNativeAd(this@MainActivity, campaignResponse, binding.placeholderNative1, type)

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

    private val funInterstitialCallback: FunInterstitialCallback = object : FunInterstitialCallback {
        override fun onAdLoaded(campaignResponse: CampaignResponse) {
            Log.i(TAG, "InterstitialCallback -> onAdLoaded: $campaignResponse")
            Toast.makeText(this@MainActivity, "Interstital Ad Loaded", Toast.LENGTH_SHORT).show()
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

    companion object {
        const val TAG = "MyFunMobTag"
    }
}