# FunMob-SDK

[![](https://jitpack.io/v/Funsol-Projects/FunMob-SDK.svg)](https://jitpack.io/#Funsol-Projects/FunMob-SDK)

FunMob-SDK is SDK for In House Advertisement Software uses to market inhouse apps.

## Getting Started

#### Dependencies

No extra dependencies required

## Step 1

Add maven repository in project level build.gradle or in latest project setting.gradle file

```kotlin 

    repositories {
        google()
        mavenCentral()
        maven { url "https://jitpack.io" }
    }
 
```  

## Step 2

Add Funsol Billing Helper dependencies in App level build.gradle.

```kotlin
    dependencies {
        implementation 'com.github.Funsol-Projects:FunMob-SDK:0.1.6-Beta'
    }
```  

## Step 3 (Initialize SDK)

Initialize FunMob-SDK by making its object
```kotlin
    val funMobAds: FunMobAds by lazy { FunMobAds() }
```

## Step 4 (Register Your App)

After Initializing, Register your app by calling this code.

```kotlin
    funMobAds.registerApp("AUTHORIZATION_TOKEN", "YOUR_FUN_APP_ID")
```

Call this in first stable activity

## How to use ads
### Fun Native Ads

To use FunNativeAds, First create callback object.

```kotlin
    funMobAds.funNativeCallback = object : FunNativeCallback {
        override fun onAdLoaded(campaignResponse: CampaignResponse) {
            Log.i(TAG, "NativeCallback -> onAdLoaded: $campaignResponse")
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
```

After Initializing callback, send load request for native ad.

```kotlin
    funMobAds.loadFunNativeAd("AUTHORIZATION_TOKEN", "YOUR_FUN_NATIVE_AD_ID")
```

### Fun Banner Ads

To use FunBannerAds, You can directly call loadFunBannerAd function by passing authorization, Ad ID, activity and callback object in parameters.

```kotlin
    funMobAds.loadFunBannerAd("AUTHORIZATION_TOKEN", "YOUR_FUN_NATIVE_AD_ID", "ACTIVITY", "FRAME_LAYOUT_ID", object : FunBannerCallback {
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
```

### Fun Interstitial Ads

To use FunInterstitialAds, First create callback object.
```kotlin
    funMobAds.funInterstitialCallback = object : FunInterstitialCallback {
        override fun onAdLoaded(campaignResponse: CampaignResponse) {
            Log.i(TAG, "InterstitialCallback -> onAdLoaded: $campaignResponse")
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
```

After Initializing callbacks, use these functions to load and show InterstitialAd.

```kotlin
    funMobAds.loadFunInterstitialAd("AUTHORIZATION_TOKEN", "YOUR_FUN_INTERSTITIAL_AD_ID")

    funMobAds.showInterstitialAd(this@MainActivity, campaignResponse)
```
### Fun AppOpen Ads

To use FunAppOpenAds for your own customize implementation, First  create callback objects.
```kotlin
   funMobAds.funAppOpenCallback = object : FunAppOpenCallback {
    override fun onAdLoaded(campaignResponse: CampaignResponse) {
        Log.i(TAG, "AppOpenCallback -> onAdLoaded:")
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
```

After Initializing callbacks, use these functions to load and show AppOpenAd.

```kotlin
   funMobAds.loadFunAppOpenAd("AUTHORIZATION_TOKEN","YOUR_FUN_APP_OPEN_AD_ID")

   funMobAds.showAppOpenAd(this@MainActivity, campaignResponse)
```

There is also FunAppOpenManager available, to use FunAppOpenManager initialize it in your application.

```kotlin
   FunAppOpenManager(this,funMobAds,"AUTHORIZATION_TOKEN","YOUR_FUN_APP_OPEN_AD_ID")
```

You can handle premium user and fun app open ad by using  adshow,isPremium

```kotlin
   FunAppOpenManager.adShow = true

   FunAppOpenManager.isPremium = false
```

## License

#### MIT License
#### Copyright (c) 2023 

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
associated documentation files (the "Software"), to deal in the Software without restriction,
including without limitation the rights to use, copy, modify, merge, publish, distribute,
sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial
portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

