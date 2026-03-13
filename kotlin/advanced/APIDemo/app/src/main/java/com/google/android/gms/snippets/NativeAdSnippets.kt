// Copyright 2025 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.android.gms.snippets

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.example.apidemo.databinding.NativeAdBinding

/** Kotlin code snippets for the developer guide. */
internal class NativeAdSnippets {

  private fun loadAd(adLoader: AdLoader) {
    // [START load_ad]
    adLoader.loadAd(AdRequest.Builder().build())
    // [END load_ad]
  }

  private fun loadAdWithAdManagerAdRequest(adLoader: AdLoader) {
    // [START load_ad_ad_manager]
    adLoader.loadAd(AdManagerAdRequest.Builder().build())
    // [END load_ad_ad_manager]
  }

  private fun loadAds(adLoader: AdLoader) {
    // [START load_ads]
    // Load three native ads.
    adLoader.loadAds(AdRequest.Builder().build(), 3)
    // [END load_ads]
  }

  private fun loadAdsWithAdManagerAdRequest(adLoader: AdLoader) {
    // [START load_ads_ad_manager]
    // Load three native ads.
    adLoader.loadAds(AdManagerAdRequest.Builder().build(), 3)
    // [END load_ads_ad_manager]
  }

  private fun handleAdLoaded(adLoaderBuilder: AdLoader.Builder) {
    // [START handle_ad_loaded]
    adLoaderBuilder
      .forNativeAd { nativeAd ->
        // This callback is invoked when a native ad is successfully loaded.
      }
      .build()
    // [END handle_ad_loaded]
  }

  private fun addNativeAdView(
    activity: Activity,
    nativeAd: NativeAd,
    layoutInflater: LayoutInflater,
    frameLayout: FrameLayout,
  ) {
    // [START add_ad_view]
    activity.runOnUiThread {
      // Inflate the native ad view and add it to the view hierarchy.
      val nativeAdBinding = NativeAdBinding.inflate(layoutInflater)
      val adView = nativeAdBinding.root

      // Display and register the native ad asset views here.
      displayAndRegisterNativeAd(nativeAd, nativeAdBinding)

      // Remove all old ad views and add the new native.
      frameLayout.removeAllViews()
      // Add the new native ad view to the view hierarchy.
      frameLayout.addView(adView)
    }
    // [END add_ad_view]
  }

  // [START display_native_ad]
  private fun displayAndRegisterNativeAd(nativeAd: NativeAd, nativeAdBinding: NativeAdBinding) {
    // [START populate_native_ad_view]
    // Populate all native ad view assets with the native ad.
    nativeAdBinding.adMedia.mediaContent = nativeAd.mediaContent
    nativeAdBinding.adHeadline.text = nativeAd.headline

    // Hide all native ad view assets that are not returned within the native ad.
    nativeAd.body?.let { body ->
      nativeAdBinding.adBody.text = body
      nativeAdBinding.adBody.visibility = View.VISIBLE
    } ?: run { nativeAdBinding.adBody.visibility = View.INVISIBLE }

    nativeAd.callToAction?.let { callToAction ->
      nativeAdBinding.adCallToAction.text = callToAction
      nativeAdBinding.adCallToAction.visibility = View.VISIBLE
    } ?: run { nativeAdBinding.adCallToAction.visibility = View.INVISIBLE }

    nativeAd.icon?.let { icon ->
      nativeAdBinding.adAppIcon.setImageDrawable(icon.drawable)
      nativeAdBinding.adAppIcon.visibility = View.VISIBLE
    } ?: run { nativeAdBinding.adAppIcon.visibility = View.GONE }

    nativeAd.price?.let { price ->
      nativeAdBinding.adPrice.text = price
      nativeAdBinding.adPrice.visibility = View.VISIBLE
    } ?: run { nativeAdBinding.adPrice.visibility = View.INVISIBLE }

    nativeAd.store?.let { store ->
      nativeAdBinding.adStore.text = store
      nativeAdBinding.adStore.visibility = View.VISIBLE
    } ?: run { nativeAdBinding.adStore.visibility = View.INVISIBLE }

    nativeAd.starRating?.let { rating ->
      nativeAdBinding.adStars.rating = rating.toFloat()
      nativeAdBinding.adStars.visibility = View.VISIBLE
    } ?: run { nativeAdBinding.adStars.visibility = View.INVISIBLE }

    nativeAd.advertiser?.let { advertiser ->
      nativeAdBinding.adAdvertiser.text = advertiser
      nativeAdBinding.adAdvertiser.visibility = View.VISIBLE
    } ?: run { nativeAdBinding.adAdvertiser.visibility = View.INVISIBLE }
    // [END populate_native_ad_view]

    // [START register_native_ad_assets]
    // Register all native ad assets with the native ad view.
    val nativeAdView = nativeAdBinding.root
    nativeAdView.advertiserView = nativeAdBinding.adAdvertiser
    nativeAdView.bodyView = nativeAdBinding.adBody
    nativeAdView.callToActionView = nativeAdBinding.adCallToAction
    nativeAdView.headlineView = nativeAdBinding.adHeadline
    nativeAdView.iconView = nativeAdBinding.adAppIcon
    nativeAdView.priceView = nativeAdBinding.adPrice
    nativeAdView.starRatingView = nativeAdBinding.adStars
    nativeAdView.storeView = nativeAdBinding.adStore
    nativeAd.mediaContent?.let { nativeAdBinding.adMedia.setMediaContent(it) }
    nativeAdView.mediaView = nativeAdBinding.adMedia
    // [END register_native_ad_assets]

    // [START set_native_ad]
    // This method tells the Google Mobile Ads SDK that you have finished populating your
    // native ad view with this native ad.
    nativeAdView.setNativeAd(nativeAd)
    // [END set_native_ad]
  }

  // [END display_native_ad]

  private fun destroyAd(nativeAd: NativeAd) {
    // [START destroy_ad]
    nativeAd.destroy()
    // [END destroy_ad]
  }

  private fun setEventCallback(adLoader: AdLoader.Builder) {
    // [START set_event_callback]
    adLoader
      .withAdListener(
        object : AdListener() {
          override fun onAdClosed() {
            // Called when the ad is closed.
          }

          override fun onAdFailedToLoad(adError: LoadAdError) {
            // Called when an ad fails to load.
          }

          override fun onAdOpened() {
            // Called when an ad opens full screen.
          }

          override fun onAdLoaded() {
            // Called when an ad has loaded.
          }

          override fun onAdClicked() {
            // Called when a click is recorded for an ad.
          }

          override fun onAdImpression() {
            // Called when an impression is recorded for an ad.
          }

          override fun onAdSwipeGestureClicked() {
            // Called when a swipe gesture is recorded for an ad.
          }
        }
      )
      .build()
    // [END set_event_callback]
  }

  private fun setImageScaleType(mediaView: MediaView) {
    // [START set_image_scale_type]
    mediaView.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
    // [END set_image_scale_type]
  }
}
