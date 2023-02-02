package com.funsol.funmob_sdk.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.funsol.funmob_sdk.databinding.ActivityInterstitialBinding
import com.funsol.funmob_sdk.interfaces.FunInterstitialCallback
import com.funsol.funmob_sdk.model.CampaignResponse

class InterstitialActivity : AppCompatActivity() {

    private val binding: ActivityInterstitialBinding by lazy { ActivityInterstitialBinding.inflate(layoutInflater) }
    private var campaignResponse: CampaignResponse? = null

    private var funInterstitialCallback: FunInterstitialCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        @Suppress("Deprecation")
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setContentView(binding.root)

        campaignResponse = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("campaignResponse", CampaignResponse::class.java)
        } else {
            intent.getParcelableExtra("campaignResponse")
        }

        try {
            funInterstitialCallback = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra("funInterstitialCallback", FunInterstitialCallback::class.java)
            } else {
                intent.getParcelableExtra("funInterstitialCallback")
            }
        } catch (e: java.lang.Exception) {
            Log.i("InterstitialCallback", "onError:", e)
        }

        init()
        initListeners()

        funInterstitialCallback?.onAdShown()
    }

    private fun init() {
        if (campaignResponse == null) {
            funInterstitialCallback?.onAdFailed("No Response")
            finish()
        }
        /*with(binding) {
            campaignResponse?.let { data ->
                appName.text = data.name
                totalDownloads.text = NumberConverter().format(data.installs?.toLong() ?: 0)
                totalRating.text = StringBuilder().append(data.rating)

                Glide.with(this@InterstitialActivity)
                    .load(data.image_URL).into(appIcon)

                appIcon.setOnClickListener {
                    goToPlayStore(data.playstore_link ?: "", data.package_name ?: "")
                }

                btnInstall.setOnClickListener {
                    goToPlayStore(data.playstore_link ?: "", data.package_name ?: "")
                }
            }
        }*/
    }

    private fun initListeners() {
        with(binding) {
            btnClose.setOnClickListener {
                funInterstitialCallback?.onAdDismissed()
                finish()
            }
        }
    }

    fun funInterstitialCallback(funInterstitialCallback: FunInterstitialCallback) {
        this.funInterstitialCallback = funInterstitialCallback
    }

    private fun goToPlayStore(link: String, packageName: String) {
        val uri = Uri.parse("market://details?id=$packageName")
        val goToMarket = Intent(Intent.ACTION_VIEW, uri)
        try {
            startActivity(goToMarket)
        } catch (e: ActivityNotFoundException) {
            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(link)
                )
            )
        }
    }
}