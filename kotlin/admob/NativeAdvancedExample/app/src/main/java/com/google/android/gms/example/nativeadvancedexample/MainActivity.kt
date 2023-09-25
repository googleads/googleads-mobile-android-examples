/*
 * Copyright (C) 2017 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.gms.example.nativeadvancedexample

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.VideoController
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.gms.example.nativeadvancedexample.databinding.ActivityMainBinding
import com.google.android.gms.example.nativeadvancedexample.databinding.AdUnifiedBinding
import java.util.concurrent.atomic.AtomicBoolean

private const val TAG = "MainActivity"
const val ADMOB_AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110"

/** A simple activity class that displays native ad formats. */
class MainActivity : AppCompatActivity() {

  private val isMobileAdsInitializeCalled = AtomicBoolean(false)
  private lateinit var mainActivityBinding: ActivityMainBinding
  private lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager
  private var currentNativeAd: NativeAd? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(mainActivityBinding.root)

    // Log the Mobile Ads SDK version.
    Log.d(TAG, "Google Mobile Ads SDK Version: " + MobileAds.getVersion())

    googleMobileAdsConsentManager = GoogleMobileAdsConsentManager(this)
    googleMobileAdsConsentManager.gatherConsent { consentError ->
      if (consentError != null) {
        // Consent not obtained in current session.
        Log.w(TAG, "${consentError.errorCode}. ${consentError.message}")
      }

      if (googleMobileAdsConsentManager.canRequestAds) {
        mainActivityBinding.refreshButton.visibility = View.VISIBLE
        initializeMobileAdsSdk()
      }

      if (googleMobileAdsConsentManager.isPrivacyOptionsRequired) {
        // Regenerate the options menu to include a privacy setting.
        invalidateOptionsMenu()
      }
    }

    // This sample attempts to load ads using consent obtained in the previous session.
    if (googleMobileAdsConsentManager.canRequestAds) {
      initializeMobileAdsSdk()
    }

    mainActivityBinding.refreshButton.setOnClickListener {
      if (googleMobileAdsConsentManager.canRequestAds) {
        refreshAd()
      }
    }
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.action_menu, menu)
    menu.findItem(R.id.action_more)?.apply {
      isVisible = googleMobileAdsConsentManager.isPrivacyOptionsRequired
    }
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val menuItemView = findViewById<View>(item.itemId)
    val activity = this
    PopupMenu(this, menuItemView).apply {
      menuInflater.inflate(R.menu.popup_menu, menu)
      show()
      setOnMenuItemClickListener { popupMenuItem ->
        when (popupMenuItem.itemId) {
          R.id.privacy_settings -> {
            // Handle changes to user consent.
            googleMobileAdsConsentManager.showPrivacyOptionsForm(activity) { formError ->
              if (formError != null) {
                Toast.makeText(this@MainActivity, formError.message, Toast.LENGTH_SHORT).show()
              }
            }
            true
          }
          else -> false
        }
      }
    }
    return super.onOptionsItemSelected(item)
  }

  /**
   * Populates a [NativeAdView] object with data from a given [NativeAd].
   *
   * @param nativeAd the object containing the ad's assets
   * @param unifiedAdBinding the binding object of the layout that has NativeAdView as the root view
   */
  private fun populateNativeAdView(nativeAd: NativeAd, unifiedAdBinding: AdUnifiedBinding) {
    val nativeAdView = unifiedAdBinding.root

    // Set the media view.
    nativeAdView.mediaView = unifiedAdBinding.adMedia

    // Set other ad assets.
    nativeAdView.headlineView = unifiedAdBinding.adHeadline
    nativeAdView.bodyView = unifiedAdBinding.adBody
    nativeAdView.callToActionView = unifiedAdBinding.adCallToAction
    nativeAdView.iconView = unifiedAdBinding.adAppIcon
    nativeAdView.priceView = unifiedAdBinding.adPrice
    nativeAdView.starRatingView = unifiedAdBinding.adStars
    nativeAdView.storeView = unifiedAdBinding.adStore
    nativeAdView.advertiserView = unifiedAdBinding.adAdvertiser

    // The headline and media content are guaranteed to be in every UnifiedNativeAd.
    unifiedAdBinding.adHeadline.text = nativeAd.headline
    nativeAd.mediaContent?.let { unifiedAdBinding.adMedia.setMediaContent(it) }

    // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
    // check before trying to display them.
    if (nativeAd.body == null) {
      unifiedAdBinding.adBody.visibility = View.INVISIBLE
    } else {
      unifiedAdBinding.adBody.visibility = View.VISIBLE
      unifiedAdBinding.adBody.text = nativeAd.body
    }

    if (nativeAd.callToAction == null) {
      unifiedAdBinding.adCallToAction.visibility = View.INVISIBLE
    } else {
      unifiedAdBinding.adCallToAction.visibility = View.VISIBLE
      unifiedAdBinding.adCallToAction.text = nativeAd.callToAction
    }

    if (nativeAd.icon == null) {
      unifiedAdBinding.adAppIcon.visibility = View.GONE
    } else {
      unifiedAdBinding.adAppIcon.setImageDrawable(nativeAd.icon?.drawable)
      unifiedAdBinding.adAppIcon.visibility = View.VISIBLE
    }

    if (nativeAd.price == null) {
      unifiedAdBinding.adPrice.visibility = View.INVISIBLE
    } else {
      unifiedAdBinding.adPrice.visibility = View.VISIBLE
      unifiedAdBinding.adPrice.text = nativeAd.price
    }

    if (nativeAd.store == null) {
      unifiedAdBinding.adStore.visibility = View.INVISIBLE
    } else {
      unifiedAdBinding.adStore.visibility = View.VISIBLE
      unifiedAdBinding.adStore.text = nativeAd.store
    }

    if (nativeAd.starRating == null) {
      unifiedAdBinding.adStars.visibility = View.INVISIBLE
    } else {
      unifiedAdBinding.adStars.rating = nativeAd.starRating!!.toFloat()
      unifiedAdBinding.adStars.visibility = View.VISIBLE
    }

    if (nativeAd.advertiser == null) {
      unifiedAdBinding.adAdvertiser.visibility = View.INVISIBLE
    } else {
      unifiedAdBinding.adAdvertiser.text = nativeAd.advertiser
      unifiedAdBinding.adAdvertiser.visibility = View.VISIBLE
    }

    // This method tells the Google Mobile Ads SDK that you have finished populating your
    // native ad view with this native ad.
    nativeAdView.setNativeAd(nativeAd)

    // Get the video controller for the ad. One will always be provided, even if the ad doesn't
    // have a video asset.
    val mediaContent = nativeAd.mediaContent
    val vc = mediaContent?.videoController

    // Updates the UI to say whether or not this ad has a video asset.
    if (vc != null && mediaContent.hasVideoContent()) {
      // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
      // VideoController will call methods on this object when events occur in the video
      // lifecycle.
      vc.videoLifecycleCallbacks =
        object : VideoController.VideoLifecycleCallbacks() {
          override fun onVideoEnd() {
            // Publishers should allow native ads to complete video playback before
            // refreshing or replacing them with another ad in the same UI location.
            mainActivityBinding.refreshButton.isEnabled = true
            mainActivityBinding.videostatusText.text = "Video status: Video playback has ended."
            super.onVideoEnd()
          }
        }
    } else {
      mainActivityBinding.videostatusText.text = "Video status: Ad does not contain a video asset."
      mainActivityBinding.refreshButton.isEnabled = true
    }
  }

  /**
   * Creates a request for a new native ad based on the boolean parameters and calls the
   * corresponding "populate" method when one is successfully returned.
   */
  private fun refreshAd() {
    mainActivityBinding.refreshButton.isEnabled = false

    val builder = AdLoader.Builder(this, ADMOB_AD_UNIT_ID)

    builder.forNativeAd { nativeAd ->
      // OnUnifiedNativeAdLoadedListener implementation.
      // If this callback occurs after the activity is destroyed, you must call
      // destroy and return or you may get a memory leak.
      var activityDestroyed = false
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        activityDestroyed = isDestroyed
      }
      if (activityDestroyed || isFinishing || isChangingConfigurations) {
        nativeAd.destroy()
        return@forNativeAd
      }
      // You must call destroy on old ads when you are done with them,
      // otherwise you will have a memory leak.
      currentNativeAd?.destroy()
      currentNativeAd = nativeAd
      val unifiedAdBinding = AdUnifiedBinding.inflate(layoutInflater)
      populateNativeAdView(nativeAd, unifiedAdBinding)
      mainActivityBinding.adFrame.removeAllViews()
      mainActivityBinding.adFrame.addView(unifiedAdBinding.root)
    }

    val videoOptions =
      VideoOptions.Builder().setStartMuted(mainActivityBinding.startMutedCheckbox.isChecked).build()

    val adOptions = NativeAdOptions.Builder().setVideoOptions(videoOptions).build()

    builder.withNativeAdOptions(adOptions)

    val adLoader =
      builder
        .withAdListener(
          object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
              val error =
                """
           domain: ${loadAdError.domain}, code: ${loadAdError.code}, message: ${loadAdError.message}
          """"
              mainActivityBinding.refreshButton.isEnabled = true
              Toast.makeText(
                  this@MainActivity,
                  "Failed to load native ad with error $error",
                  Toast.LENGTH_SHORT
                )
                .show()
            }
          }
        )
        .build()

    adLoader.loadAd(AdRequest.Builder().build())

    mainActivityBinding.videostatusText.text = ""
  }

  private fun initializeMobileAdsSdk() {
    if (isMobileAdsInitializeCalled.getAndSet(true)) {
      return
    }

    // Initialize the Mobile Ads SDK.
    MobileAds.initialize(this) { initializationStatus ->
      // Load an ad.
      refreshAd()
    }
  }

  override fun onDestroy() {
    currentNativeAd?.destroy()
    super.onDestroy()
  }
}
