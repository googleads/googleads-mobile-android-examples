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

package com.google.android.gms.example.nativeadsexample

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.VideoController
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.gms.ads.nativead.NativeCustomFormatAd
import com.google.android.gms.example.nativeadsexample.databinding.ActivityMainBinding
import com.google.android.gms.example.nativeadsexample.databinding.AdSimpleCustomTemplateBinding
import com.google.android.gms.example.nativeadsexample.databinding.AdUnifiedBinding
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

private const val TAG = "MainActivity"
const val AD_MANAGER_AD_UNIT_ID = "/6499/example/native"
const val SIMPLE_TEMPLATE_ID = "10104090"

/** A simple activity class that displays native ad formats. */
class MainActivity : AppCompatActivity() {

  private val isMobileAdsInitializeCalled = AtomicBoolean(false)
  private lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager
  private lateinit var customTemplateBinding: AdSimpleCustomTemplateBinding
  private lateinit var mainActivityBinding: ActivityMainBinding
  private var currentNativeAd: NativeAd? = null
  private var currentCustomFormatAd: NativeCustomFormatAd? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(mainActivityBinding.root)

    // Log the Mobile Ads SDK version.
    Log.d(TAG, "Google Mobile Ads SDK Version: " + MobileAds.getVersion())

    googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(applicationContext)
    googleMobileAdsConsentManager.gatherConsent(this) { consentError ->
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
        refreshAd(
          mainActivityBinding.nativeadsCheckbox.isChecked,
          mainActivityBinding.customtemplateCheckbox.isChecked
        )
      }
    }
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    menuInflater.inflate(R.menu.action_menu, menu)
    menu?.findItem(R.id.action_more)?.apply {
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

    // The headline and media content are guaranteed to be in every NativeAd.
    unifiedAdBinding.adHeadline.text = nativeAd.headline
    nativeAd.mediaContent?.let { unifiedAdBinding.adMedia.setMediaContent(it) }

    // These assets aren't guaranteed to be in every NativeAd, so it's important to
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

    val mediaContent = nativeAd.mediaContent

    // Updates the UI to say whether or not this ad has a video asset.
    if (mediaContent != null && mediaContent.hasVideoContent()) {
      val videoController = mediaContent.videoController
      mainActivityBinding.videostatusText.text =
        String.format(
          Locale.getDefault(),
          "Video status: Ad contains a %.2f:1 video asset.",
          mediaContent.aspectRatio
        )
      // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
      // VideoController will call methods on this object when events occur in the video
      // lifecycle.
      videoController?.videoLifecycleCallbacks =
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
   * Populates a [View] object with data from a [NativeCustomFormatAd]. This method handles a
   * particular "simple" custom native ad format.
   *
   * @param nativeCustomFormatAd the object containing the ad's assets
   * @param adView the view to be populated
   */
  private fun populateSimpleTemplateAdView(nativeCustomFormatAd: NativeCustomFormatAd) {
    customTemplateBinding.simplecustomHeadline.text = nativeCustomFormatAd.getText("Headline")
    customTemplateBinding.simplecustomCaption.text = nativeCustomFormatAd.getText("Caption")

    val videoController = nativeCustomFormatAd.mediaContent?.videoController

    // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
    // VideoController will call methods on this object when events occur in the video
    // lifecycle.
    if (videoController != null) {
      videoController.videoLifecycleCallbacks =
        object : VideoController.VideoLifecycleCallbacks() {
          override fun onVideoEnd() {
            // Publishers should allow native ads to complete video playback before refreshing
            // or replacing them with another ad in the same UI location.
            mainActivityBinding.refreshButton.isEnabled = true
            mainActivityBinding.videostatusText.text = "Video status: Video playback has ended."
            super.onVideoEnd()
          }
        }
    }

    val mediaContent = nativeCustomFormatAd.mediaContent

    // Apps can check the MediaContent's hasVideoContent property to determine if the
    // NativeCustomFormatAd has a video asset.
    if (mediaContent != null && mediaContent.hasVideoContent()) {
      val mediaView = MediaView(this)
      mediaView.mediaContent = mediaContent
      customTemplateBinding.simplecustomMediaPlaceholder.addView(mediaView)
      // Kotlin doesn't include decimal-place formatting in its string interpolation, but
      // good ol' String.format works fine.
      mainActivityBinding.videostatusText.text =
        String.format(Locale.getDefault(), "Video status: Ad contains a video asset.")
    } else {
      val mainImage = ImageView(this)
      mainImage.adjustViewBounds = true
      mainImage.setImageDrawable(nativeCustomFormatAd.getImage("MainImage")?.drawable)

      mainImage.setOnClickListener { nativeCustomFormatAd.performClick("MainImage") }
      customTemplateBinding.simplecustomMediaPlaceholder.addView(mainImage)
      mainActivityBinding.refreshButton.isEnabled = true
      mainActivityBinding.videostatusText.text = "Video status: Ad does not contain a video asset."
    }
  }

  /**
   * Creates a request for a new native ad based on the boolean parameters and calls the
   * corresponding "populate" method when one is successfully returned.
   *
   * @param requestNativeAds indicates whether native ads should be requested
   * @param requestCustomTemplateAds indicates whether custom template ads should be requested
   */
  private fun refreshAd(requestNativeAds: Boolean, requestCustomTemplateAds: Boolean) {
    if (!requestNativeAds && !requestCustomTemplateAds) {
      Toast.makeText(
          this,
          "At least one ad format must be checked to request an ad.",
          Toast.LENGTH_SHORT
        )
        .show()
      return
    }
    mainActivityBinding.refreshButton.isEnabled = true

    val builder = AdLoader.Builder(this, AD_MANAGER_AD_UNIT_ID)
    if (requestNativeAds) {
      builder.forNativeAd { nativeAd ->
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
    }

    if (requestCustomTemplateAds) {
      builder.forCustomFormatAd(
        SIMPLE_TEMPLATE_ID,
        { ad: NativeCustomFormatAd ->
          // If this callback occurs after the activity is destroyed, you must call
          // destroy and return or you may get a memory leak.
          var activityDestroyed = false
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            activityDestroyed = isDestroyed
          }
          if (activityDestroyed || isFinishing || isChangingConfigurations) {
            ad.destroy()
            return@forCustomFormatAd
          }
          // You must call destroy on old ads when you are done with them,
          // otherwise you will have a memory leak.
          currentCustomFormatAd?.destroy()
          currentCustomFormatAd = ad
          customTemplateBinding = AdSimpleCustomTemplateBinding.inflate(layoutInflater)
          populateSimpleTemplateAdView(ad)
          mainActivityBinding.adFrame.removeAllViews()
          mainActivityBinding.adFrame.addView(customTemplateBinding.root)
        },
        { ad: NativeCustomFormatAd, s: String ->
          Toast.makeText(
              this@MainActivity,
              "A custom click has occurred in the simple template",
              Toast.LENGTH_SHORT
            )
            .show()
        }
      )
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
              mainActivityBinding.refreshButton.isEnabled = true
              val error =
                """"
            domain: ${loadAdError.domain}, code: ${loadAdError.code}, message: ${loadAdError.message}
          """
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

    adLoader.loadAd(AdManagerAdRequest.Builder().build())

    mainActivityBinding.videostatusText.text = ""
  }

  private fun initializeMobileAdsSdk() {
    if (isMobileAdsInitializeCalled.getAndSet(true)) {
      return
    }

    // Initialize the Mobile Ads SDK.
    MobileAds.initialize(this) { initializationStatus ->
      // Load an ad.
      refreshAd(
        mainActivityBinding.nativeadsCheckbox.isChecked,
        mainActivityBinding.customtemplateCheckbox.isChecked
      )
    }
  }

  override fun onDestroy() {
    currentNativeAd?.destroy()
    currentCustomFormatAd?.destroy()
    super.onDestroy()
  }
}
