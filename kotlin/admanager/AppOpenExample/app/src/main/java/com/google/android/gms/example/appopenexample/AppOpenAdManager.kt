package com.google.android.gms.example.appopenexample

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdManagerAdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback
import java.util.Date

/** Class that loads and shows app open ads. */
// [START app_open_ad_manager]
class AppOpenAdManager(context: Context) : DefaultLifecycleObserver {
  private var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager =
    GoogleMobileAdsConsentManager.getInstance(context)
  private var appOpenAd: AppOpenAd? = null
  /** Keep track of the time an app open ad is loaded to ensure you don't show an expired ad. */
  private var loadTime: Long = 0
  private var isLoadingAd = false
  var isShowingAd = false
  var currentActivity: Activity? = null

  // [END app_open_ad_manager]

  fun initialize() {
    ProcessLifecycleOwner.get().lifecycle.addObserver(this)
  }

  /**
   * DefaultLifecycleObserver method that shows the app open ad when the app moves to foreground.
   */
  // [START start_lifecycle_observer]
  override fun onStart(owner: LifecycleOwner) {
    super.onStart(owner)
    currentActivity?.let {
      // Show the ad (if available) when the app moves to foreground.
      showAdIfAvailable(it)
    }
  }

  /** DefaultLifecycleObserver method that is called when the manager is destroyed. */
  override fun onDestroy(owner: LifecycleOwner) {
    super.onDestroy(owner)
    ProcessLifecycleOwner.get().lifecycle.removeObserver(this)
  }

  // [END start_lifecycle_observer]

  /**
   * Load an ad.
   *
   * @param context the context of the activity that loads the ad
   */
  // [START load_ad]
  fun loadAd(context: Context) {
    // Do not load ad if there is an unused ad or one is already loading.
    if (isLoadingAd || isAdAvailable()) {
      return
    }
    isLoadingAd = true

    AppOpenAd.load(
      context,
      AD_UNIT_ID,
      AdManagerAdRequest.Builder().build(),
      object : AppOpenAdLoadCallback() {
        override fun onAdLoaded(ad: AppOpenAd) {
          Log.d(TAG, "Ad was loaded.")
          Toast.makeText(context, "Ad was loaded.", Toast.LENGTH_SHORT).show()
          appOpenAd = ad
          isLoadingAd = false
          loadTime = Date().time
        }

        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
          Log.d(TAG, "Ad failed to load with error: " + loadAdError.message)
          Toast.makeText(context, "Ad failed to load.", Toast.LENGTH_SHORT).show()
          isLoadingAd = false
        }
      },
    )
  }

  // [END load_ad]

  /** Check if ad was loaded more than n hours ago. */
  // [START ad_expiration]
  private fun wasLoadTimeLessThanNHoursAgo(): Boolean {
    val dateDifference: Long = Date().time - loadTime
    val numMilliSecondsPerHour: Long = 3600000
    return dateDifference < numMilliSecondsPerHour * TIME_INTERVAL
  }

  /** Check if ad exists and can be shown. */
  private fun isAdAvailable(): Boolean {
    return appOpenAd != null && wasLoadTimeLessThanNHoursAgo()
  }

  // [END ad_expiration]

  /**
   * Show the ad if one isn't already showing.
   *
   * @param activity the activity that shows the app open ad.
   * @param onShowAdComplete the function to be executed when an app open ad is complete.
   */
  // [START show_ad]
  fun showAdIfAvailable(activity: Activity, onShowAdComplete: () -> Unit = {}) {
    // If the app open ad is already showing, do not show the ad again.
    if (isShowingAd) {
      Log.d(TAG, "The app open ad is already showing.")
      return
    }

    // If the app open ad is not available yet but is supposed to show, load
    // a new ad.
    if (!isAdAvailable()) {
      Log.d(TAG, "The app open ad is not ready yet.")
      onShowAdComplete()
      if (googleMobileAdsConsentManager.canRequestAds) {
        loadAd(activity)
      }
      return
    }
    // [START_EXCLUDE silent]
    setFullScreenContentCallback(activity, onShowAdComplete)
    // [END_EXCLUDE]

    isShowingAd = true
    appOpenAd?.show(activity)
  }

  // [END show_ad]

  private fun setFullScreenContentCallback(activity: Activity, onShowAdComplete: () -> Unit) {
    // [START ad_events]
    appOpenAd?.fullScreenContentCallback =
      object : FullScreenContentCallback() {
        override fun onAdDismissedFullScreenContent() {
          // Called when full screen content is dismissed.
          Log.d(TAG, "App open ad was dismissed.")
          Toast.makeText(activity, "App open ad was dismissed.", Toast.LENGTH_SHORT).show()
          appOpenAd = null
          isShowingAd = false

          onShowAdComplete()
          if (googleMobileAdsConsentManager.canRequestAds) {
            loadAd(activity)
          }
        }

        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
          // Called when fullscreen content failed to show.
          Log.d(TAG, "Ad failed to show full screen content with error: " + adError.message)
          Toast.makeText(activity, "Ad failed to show.", Toast.LENGTH_SHORT).show()
          appOpenAd = null
          isShowingAd = false

          onShowAdComplete()
          if (googleMobileAdsConsentManager.canRequestAds) {
            loadAd(activity)
          }
        }

        override fun onAdShowedFullScreenContent() {
          // Called when fullscreen content is shown.
          Log.d(TAG, "Ad showed full screen content.")
          Toast.makeText(activity, "Ad showed full screen content.", Toast.LENGTH_SHORT).show()
        }
      }
    // [END ad_events]
  }

  companion object {
    // This is an ad unit ID for a test ad. Replace with your own app open ad unit ID.
    private const val AD_UNIT_ID = "/21775744923/example/app-open"
    private const val TAG = "MyApplication"
    // For time interval details, see: https://support.google.com/admanager/answer/9351867
    private const val TIME_INTERVAL = 4L
  }
}
