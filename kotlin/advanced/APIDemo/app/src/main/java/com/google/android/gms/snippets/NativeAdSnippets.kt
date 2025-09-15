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
import android.content.Context
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
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.example.apidemo.databinding.NativeAdBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Kotlin code snippets for the developer guide. */
internal class NativeAdSnippets {

  private fun createAdLoader(context: Context) {
    // [START create_ad_loader]
    // It is recommended to call AdLoader.Builder on a background thread.
    CoroutineScope(Dispatchers.IO).launch {
      val adLoader =
        AdLoader.Builder(context, AD_UNIT_ID)
          .forNativeAd { nativeAd ->
            // The native ad loaded successfully. You can show the ad.
          }
          .withAdListener(
            object : AdListener() {
              override fun onAdFailedToLoad(adError: LoadAdError) {
                // The native ad load failed. Check the adError message for failure reasons.
              }
            }
          )
          // Use the NativeAdOptions.Builder class to specify individual options settings.
          .withNativeAdOptions(NativeAdOptions.Builder().build())
          .build()
    }
    // [END create_ad_loader]
  }

  private fun setAdLoaderListener(adLoaderBuilder: AdLoader.Builder) {
    // [START set_ad_listener]
    adLoaderBuilder.withAdListener(
      // Override AdListener callbacks here.
      object : AdListener() {}
    )
    // [END set_ad_listener]
  }

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
      displayNativeAd(nativeAd, nativeAdBinding)

      // Remove all old ad views and add the new native.
      frameLayout.removeAllViews()
      // Add the new native ad view to the view hierarchy.
      frameLayout.addView(adView)
    }
    // [END add_ad_view]
  }

  // [START display_native_ad]
  private fun displayNativeAd(nativeAd: NativeAd, nativeAdBinding: NativeAdBinding) {
    // [START populate_native_ad_view]
    // Populate all native ad view assets with the native ad.
    nativeAdBinding.adMedia.mediaContent = nativeAd.mediaContent
    nativeAdBinding.adAdvertiser.text = nativeAd.advertiser
    nativeAdBinding.adBody.text = nativeAd.body
    nativeAdBinding.adCallToAction.text = nativeAd.callToAction
    nativeAdBinding.adHeadline.text = nativeAd.headline
    nativeAdBinding.adAppIcon.setImageDrawable(nativeAd.icon?.drawable)
    nativeAdBinding.adPrice.text = nativeAd.price
    nativeAd.starRating?.toFloat().also { value ->
      if (value != null) {
        nativeAdBinding.adStars.rating = value
      }
    }
    nativeAdBinding.adStore.text = nativeAd.store
    // [END populate_native_ad_view]

    // [START hide_native_ad_view_assets]
    // Hide all native ad view assets that are not returned within the native ad.
    if (nativeAd.body == null) {
      nativeAdBinding.adBody.visibility = View.INVISIBLE
    } else {
      nativeAdBinding.adBody.text = nativeAd.body
      nativeAdBinding.adBody.visibility = View.VISIBLE
    }

    if (nativeAd.callToAction == null) {
      nativeAdBinding.adCallToAction.visibility = View.INVISIBLE
    } else {
      nativeAdBinding.adCallToAction.text = nativeAd.callToAction
      nativeAdBinding.adCallToAction.visibility = View.VISIBLE
    }

    if (nativeAd.icon == null) {
      nativeAdBinding.adAppIcon.visibility = View.GONE
    } else {
      nativeAdBinding.adAppIcon.setImageDrawable(nativeAd.icon?.drawable)
      nativeAdBinding.adAppIcon.visibility = View.VISIBLE
    }

    if (nativeAd.price == null) {
      nativeAdBinding.adPrice.visibility = View.INVISIBLE
    } else {
      nativeAdBinding.adPrice.text = nativeAd.price
      nativeAdBinding.adPrice.visibility = View.VISIBLE
    }

    if (nativeAd.store == null) {
      nativeAdBinding.adStore.visibility = View.INVISIBLE
    } else {
      nativeAdBinding.adStore.text = nativeAd.store
      nativeAdBinding.adStore.visibility = View.VISIBLE
    }

    if (nativeAd.starRating == null) {
      nativeAdBinding.adStars.visibility = View.INVISIBLE
    } else {
      nativeAdBinding.adStars.rating = nativeAd.starRating!!.toFloat()
      nativeAdBinding.adStars.visibility = View.VISIBLE
    }

    if (nativeAd.advertiser == null) {
      nativeAdBinding.adAdvertiser.visibility = View.INVISIBLE
    } else {
      nativeAdBinding.adAdvertiser.text = nativeAd.advertiser
      nativeAdBinding.adAdvertiser.visibility = View.VISIBLE
    }
    // [END hide_native_ad_view_assets]

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
          override fun onAdFailedToLoad(adError: LoadAdError) {
            // Handle the failure.
          }

          override fun onAdClicked() {
            // Log the click event or other custom behavior.
          }
        }
      )
      .build()
    // [END set_event_callback]
  }

  // [START set_image_scale_type_compose]
  private fun setImageScaleType(mediaView: MediaView) {
    // [START set_image_scale_type]
    mediaView.setImageScaleType(ImageView.ScaleType.CENTER_CROP)
    // [END set_image_scale_type]
  }

  // [END set_image_scale_type_compose]

  private companion object {
    // Test ad unit IDs.
    // For more information,
    // see https://developers.google.com/admob/android/test-ads.
    // and https://developers.google.com/ad-manager/mobile-ads-sdk/android/test-ads.
    const val AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110"
  }
}
