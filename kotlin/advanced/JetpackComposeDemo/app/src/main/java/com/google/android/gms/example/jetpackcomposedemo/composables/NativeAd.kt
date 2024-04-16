/*
 * Copyright 2024 Google LLC
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

package com.google.android.gms.example.jetpackcomposedemo.composables

import android.view.LayoutInflater
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.viewinterop.AndroidView
import com.example.jetpackcomposedemo.databinding.NativeadBinding
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

/**
 * A composable function to display a native ad with a native ad view layout defined in xml.
 *
 * @param nativeAdState The NativeAdState object containing ad configuration.
 * @param layoutID The layout resource Id to use for the native ad view.
 * @param modifier The modifier to apply to the banner ad.
 */
@Composable
fun NativeAd(nativeAdState: NativeAdState, layoutID: Int, modifier: Modifier) {
  // Do not load the ad in preview mode
  if (LocalInspectionMode.current) {
    Box(modifier = Modifier.background(Color.Gray)) {
      Text(text = "Native Ad Preview.", modifier.align(Alignment.Center))
    }
    return
  }

  var currentNativeAdView: NativeAdView? = null
  var currentNativeAd: NativeAd? = null

  val adLoader = AdLoader.Builder(LocalContext.current, nativeAdState.adUnitId)
  if (nativeAdState.nativeAdOptions != null) {
    adLoader.withNativeAdOptions(nativeAdState.nativeAdOptions)
  }
  adLoader.withAdListener(
    object : AdListener() {
      override fun onAdFailedToLoad(error: LoadAdError) {
        nativeAdState.onAdFailedToLoad?.invoke(error)
      }

      override fun onAdLoaded() {
        nativeAdState.onAdLoaded?.invoke()
      }

      override fun onAdClicked() {
        nativeAdState.onAdClicked?.invoke()
      }

      override fun onAdClosed() {
        nativeAdState.onAdClosed?.invoke()
      }

      override fun onAdImpression() {
        nativeAdState.onAdImpression?.invoke()
      }

      override fun onAdOpened() {
        nativeAdState.onAdOpened?.invoke()
      }

      override fun onAdSwipeGestureClicked() {
        nativeAdState.onAdSwipeGestureClicked?.invoke()
      }
    }
  )
  adLoader.forNativeAd { nativeAd ->

    // Destroy old native ad assets to prevent memory leaks.
    currentNativeAd?.destroy()
    currentNativeAd = null
    currentNativeAd = nativeAd

    if (currentNativeAdView == null) {
      return@forNativeAd
    }

    // Bind our native ad view with the native ad assets.
    // This file is generated from /res/layouts/nativead
    currentNativeAdView?.let { adView ->
      val binding = NativeadBinding.bind(adView)
      binding.adHeadline.text = nativeAd.headline
      binding.adBody.text = nativeAd.body
      binding.adCallToAction.text = nativeAd.callToAction
      binding.adPrice.text = nativeAd.price
      binding.adStore.text = nativeAd.store
      binding.adStars.rating = nativeAd.starRating?.toFloat() ?: 0.toFloat()
      binding.adAdvertiser.text = nativeAd.advertiser
      binding.adAppIcon.setImageDrawable(nativeAd.icon?.drawable)

      // Hide unused native ad view elements.
      binding.adBody.visibility = nativeAd.body?.let { View.VISIBLE } ?: View.GONE
      binding.adCallToAction.visibility = nativeAd.callToAction?.let { View.VISIBLE } ?: View.GONE
      binding.adPrice.visibility = nativeAd.price?.let { View.VISIBLE } ?: View.GONE
      binding.adStore.visibility = nativeAd.store?.let { View.VISIBLE } ?: View.GONE
      binding.adStars.visibility = nativeAd.starRating?.let { View.VISIBLE } ?: View.GONE
      binding.adAdvertiser.visibility = nativeAd.advertiser?.let { View.VISIBLE } ?: View.GONE
      binding.adAppIcon.visibility = nativeAd.icon?.let { View.VISIBLE } ?: View.GONE
      binding.adMedia.visibility = nativeAd.mediaContent?.let { View.VISIBLE } ?: View.GONE

      // Set the mediaView just before calling setNativeAd.
      adView.mediaView = binding.adMedia

      // This method tells the Google Mobile Ads SDK that you have finished populating your
      // native ad view with this native ad.
      adView.setNativeAd(nativeAd)

      // TODO: Remove after androidx.compose.ui:ui:1.7.0-beta04
      adView.viewTreeObserver?.dispatchOnGlobalLayout()
    }
  }

  AndroidView(
    modifier = modifier,
    factory = { context ->
      LayoutInflater.from(context).inflate(layoutID, null, false) as NativeAdView
    },
  ) { nativeAdView ->
    currentNativeAdView = nativeAdView
    return@AndroidView
  }

  LaunchedEffect(Unit) {
    // Load the native ad.
    adLoader.build().loadAd(nativeAdState.adRequest)
  }

  // Clean up the native ad view after use.
  DisposableEffect(Unit) {
    onDispose {
      // Destroy old native ad assets to prevent memory leaks.
      currentNativeAd?.destroy()
      currentNativeAd = null
    }
  }
}
