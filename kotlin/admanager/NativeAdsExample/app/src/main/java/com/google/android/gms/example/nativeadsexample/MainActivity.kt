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
import java.util.*
import kotlinx.android.synthetic.main.activity_main.*

const val AD_MANAGER_AD_UNIT_ID = "/6499/example/native"
const val SIMPLE_TEMPLATE_ID = "10104090"

var currentNativeAd: NativeAd? = null
var currentCustomFormatAd: NativeCustomFormatAd? = null

/**
 * A simple activity class that displays native ad formats.
 */
class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    // Initialize the Mobile Ads SDK with an empty completion listener.
    MobileAds.initialize(this) {}

    refresh_button.setOnClickListener {
      refreshAd(nativeads_checkbox.isChecked, customtemplate_checkbox.isChecked)
    }

    refreshAd(nativeads_checkbox.isChecked, customtemplate_checkbox.isChecked)
  }

  /**
   * Populates a [NativeAdView] object with data from a given
   * [NativeAd].
   *
   * @param nativeAd the object containing the ad's assets
   * @param adView the view to be populated
   */
  private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
    // Set the media view.
    adView.mediaView = adView.findViewById<MediaView>(R.id.ad_media)

    // Set other ad assets.
    adView.headlineView = adView.findViewById(R.id.ad_headline)
    adView.bodyView = adView.findViewById(R.id.ad_body)
    adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
    adView.iconView = adView.findViewById(R.id.ad_app_icon)
    adView.priceView = adView.findViewById(R.id.ad_price)
    adView.starRatingView = adView.findViewById(R.id.ad_stars)
    adView.storeView = adView.findViewById(R.id.ad_store)
    adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

    // The headline and media content are guaranteed to be in every NativeAd.
    (adView.headlineView as TextView).text = nativeAd.headline
    adView.mediaView.setMediaContent(nativeAd.mediaContent)

    // These assets aren't guaranteed to be in every NativeAd, so it's important to
    // check before trying to display them.
    if (nativeAd.body == null) {
      adView.bodyView.visibility = View.INVISIBLE
    } else {
      adView.bodyView.visibility = View.VISIBLE
      (adView.bodyView as TextView).text = nativeAd.body
    }

    if (nativeAd.callToAction == null) {
      adView.callToActionView.visibility = View.INVISIBLE
    } else {
      adView.callToActionView.visibility = View.VISIBLE
      (adView.callToActionView as Button).text = nativeAd.callToAction
    }

    if (nativeAd.icon == null) {
      adView.iconView.visibility = View.GONE
    } else {
      (adView.iconView as ImageView).setImageDrawable(
        nativeAd.icon.drawable
      )
      adView.iconView.visibility = View.VISIBLE
    }

    if (nativeAd.price == null) {
      adView.priceView.visibility = View.INVISIBLE
    } else {
      adView.priceView.visibility = View.VISIBLE
      (adView.priceView as TextView).text = nativeAd.price
    }

    if (nativeAd.store == null) {
      adView.storeView.visibility = View.INVISIBLE
    } else {
      adView.storeView.visibility = View.VISIBLE
      (adView.storeView as TextView).text = nativeAd.store
    }

    if (nativeAd.starRating == null) {
      adView.starRatingView.visibility = View.INVISIBLE
    } else {
      (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
      adView.starRatingView.visibility = View.VISIBLE
    }

    if (nativeAd.advertiser == null) {
      adView.advertiserView.visibility = View.INVISIBLE
    } else {
      (adView.advertiserView as TextView).text = nativeAd.advertiser
      adView.advertiserView.visibility = View.VISIBLE
    }

    // This method tells the Google Mobile Ads SDK that you have finished populating your
    // native ad view with this native ad.
    adView.setNativeAd(nativeAd)

    // Get the video controller for the ad. One will always be provided, even if the ad doesn't
    // have a video asset.
    val vc = nativeAd.mediaContent.videoController

    // Updates the UI to say whether or not this ad has a video asset.
    if (vc.hasVideoContent()) {
      videostatus_text.text = String.format(
        Locale.getDefault(),
        "Video status: Ad contains a %.2f:1 video asset.",
        nativeAd.mediaContent.aspectRatio
      )
      // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
      // VideoController will call methods on this object when events occur in the video
      // lifecycle.
      vc.videoLifecycleCallbacks = object : VideoController.VideoLifecycleCallbacks() {
        override fun onVideoEnd() {
          // Publishers should allow native ads to complete video playback before
          // refreshing or replacing them with another ad in the same UI location.
          refresh_button.isEnabled = true
          videostatus_text.text = "Video status: Video playback has ended."
          super.onVideoEnd()
        }
      }
    } else {
      videostatus_text.text = "Video status: Ad does not contain a video asset."
      refresh_button.isEnabled = true
    }
  }

  /**
   * Populates a [View] object with data from a [NativeCustomFormatAd]. This method
   * handles a particular "simple" custom native ad format.
   *
   * @param nativeCustomFormatAd the object containing the ad's assets
   *
   * @param adView the view to be populated
   */
  private fun populateSimpleTemplateAdView(
    nativeCustomFormatAd: NativeCustomFormatAd,
    adView: View
  ) {
    val headlineView = adView.findViewById<TextView>(R.id.simplecustom_headline)
    val captionView = adView.findViewById<TextView>(R.id.simplecustom_caption)

    headlineView.text = nativeCustomFormatAd.getText("Headline")
    captionView.text = nativeCustomFormatAd.getText("Caption")

    val mediaPlaceholder = adView.findViewById<FrameLayout>(R.id.simplecustom_media_placeholder)

    // Get the video controller for the ad. One will always be provided, even if the ad doesn't
    // have a video asset.
    val vc = nativeCustomFormatAd.videoController

    // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
    // VideoController will call methods on this object when events occur in the video
    // lifecycle.
    vc.videoLifecycleCallbacks = object : VideoController.VideoLifecycleCallbacks() {
      override fun onVideoEnd() {
        // Publishers should allow native ads to complete video playback before refreshing
        // or replacing them with another ad in the same UI location.
        refresh_button.isEnabled = true
        videostatus_text.text = "Video status: Video playback has ended."
        super.onVideoEnd()
      }
    }

    // Apps can check the VideoController's hasVideoContent property to determine if the
    // NativeCustomFormatAd has a video asset.
    if (vc.hasVideoContent()) {
      mediaPlaceholder.addView(nativeCustomFormatAd.getVideoMediaView())
      // Kotlin doesn't include decimal-place formatting in its string interpolation, but
      // good ol' String.format works fine.
      videostatus_text.text = String.format(
        Locale.getDefault(),
        "Video status: Ad contains a video asset."
      )
    } else {
      val mainImage = ImageView(this)
      mainImage.adjustViewBounds = true
      mainImage.setImageDrawable(nativeCustomFormatAd.getImage("MainImage").drawable)

      mainImage.setOnClickListener { nativeCustomFormatAd.performClick("MainImage") }
      mediaPlaceholder.addView(mainImage)
      refresh_button.isEnabled = true
      videostatus_text.text = "Video status: Ad does not contain a video asset."
    }
  }

  /**
   * Creates a request for a new native ad based on the boolean parameters and calls the
   * corresponding "populate" method when one is successfully returned.
   *
   * @param requestNativeAds indicates whether native ads should be requested
   *
   * @param requestCustomTemplateAds indicates whether custom template ads should be requested
   */
  private fun refreshAd(
    requestNativeAds: Boolean,
    requestCustomTemplateAds: Boolean
  ) {
    if (!requestNativeAds && !requestCustomTemplateAds) {
      Toast.makeText(
        this, "At least one ad format must be checked to request an ad.",
        Toast.LENGTH_SHORT
      ).show()
      return
    }
    refresh_button.isEnabled = true

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
        val adView = layoutInflater
          .inflate(R.layout.ad_unified, null) as NativeAdView
        populateNativeAdView(nativeAd, adView)
        ad_frame.removeAllViews()
        ad_frame.addView(adView)
      }
    }

    if (requestCustomTemplateAds) {
      builder.forCustomFormatAd(
        SIMPLE_TEMPLATE_ID,
        {
          ad: NativeCustomFormatAd ->
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
          val frameLayout = findViewById<FrameLayout>(R.id.ad_frame)
          val adView = layoutInflater
            .inflate(R.layout.ad_simple_custom_template, null)
          populateSimpleTemplateAdView(ad, adView)
          frameLayout.removeAllViews()
          frameLayout.addView(adView)
        },
        {
          ad: NativeCustomFormatAd, s: String ->
          Toast.makeText(
            this@MainActivity,
            "A custom click has occurred in the simple template",
            Toast.LENGTH_SHORT
          ).show()
        }
      )
    }

    val videoOptions = VideoOptions.Builder()
      .setStartMuted(start_muted_checkbox.isChecked)
      .build()

    val adOptions = NativeAdOptions.Builder()
      .setVideoOptions(videoOptions)
      .build()

    builder.withNativeAdOptions(adOptions)

    val adLoader = builder.withAdListener(object : AdListener() {
      override fun onAdFailedToLoad(loadAdError: LoadAdError) {
        refresh_button.isEnabled = true
        val error =
          """"
            domain: ${loadAdError.domain}, code: ${loadAdError.code}, message: ${loadAdError.message}
          """
        Toast.makeText(
          this@MainActivity, "Failed to load native ad with error $error",
          Toast.LENGTH_SHORT
        ).show()
      }
    }).build()

    adLoader.loadAd(AdManagerAdRequest.Builder().build())

    videostatus_text.text = ""
  }

  override fun onDestroy() {
    currentNativeAd?.destroy()
    currentCustomFormatAd?.destroy()
    super.onDestroy()
  }
}
