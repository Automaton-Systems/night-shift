package com.systems.automaton.mindfullife.ads

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.systems.automaton.nightshift.R

private const val TAG: String = "AdManager"

class AdManager {

    private var mInterstitialAd: InterstitialAd? = null
    var isDisabled: Boolean = false; private set
    private var isInitialized: Boolean = false
    private var forceShowWhenReadyActivity: Activity? = null

    fun initialize(context: Context) {
        if (isInitialized) {
            throw IllegalStateException("Can't initialize an already initialized singleton.")
        }

        MobileAds.initialize(context) {}
        val requestConfiguration = RequestConfiguration.Builder()
            .setTestDeviceIds(listOf(AdRequest.DEVICE_ID_EMULATOR, context.getString(R.string.physical_device_id)))
            .build()
        MobileAds.setRequestConfiguration(requestConfiguration)

        prepareAd(context)
    }

    fun showAd(activity: Activity, forceShowWhenReady: Boolean = false) {

        if (forceShowWhenReady) {
            forceShowWhenReadyActivity = activity
        } else {
            forceShowWhenReadyActivity = null
        }

        if (isDisabled) {
            return
        }

        if (mInterstitialAd != null) {
            mInterstitialAd?.show(activity)
        } else {
            Log.d(TAG, "The interstitial ad wasn't ready yet.")
        }
    }

    private fun prepareAd(context: Context) {

        if (isDisabled) {
            return
        }

        val adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context, context.getString(R.string.ad_unit_id), adRequest, object : InterstitialAdLoadCallback() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
                Log.d(TAG, adError.message)
                mInterstitialAd = null
            }

            override fun onAdLoaded(interstitialAd: InterstitialAd) {
                Log.d(TAG, "Ad was loaded.")

                if (isDisabled) {
                    return
                }

                mInterstitialAd = interstitialAd

                interstitialAd.fullScreenContentCallback = object: FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        Log.d(TAG, "Ad was dismissed.")
                        prepareAd(context)
                    }

                    override fun onAdShowedFullScreenContent() {
                        Log.d(TAG, "Ad showed fullscreen content.")
                        mInterstitialAd = null
                    }
                }

                forceShowWhenReadyActivity?.let {
                    mInterstitialAd?.show(it)
                    forceShowWhenReadyActivity = null
                }
            }
        })
    }

    fun disableAds() {
        isDisabled = true
        mInterstitialAd = null
    }

    companion object {
        val instance = AdManager()
    }
}

@SuppressLint("StaticFieldLeak")
private var lastKnownActivity: Activity? = null
fun Context.getActivity(): Activity? {
    if (this is Activity) {
        lastKnownActivity = this
    } else if (this is ContextWrapper) {
        lastKnownActivity = baseContext.getActivity()
    }
    return lastKnownActivity
}

fun Context.showAd() = run {
    val activity = this.getActivity()
    activity?.let {
        AdManager.instance.showAd(it)
    }
}

fun Context.launchBuy() = run {
    val activity = this.getActivity()
    activity?.let {
        BillingManager.instance.buy(it)
    }
}