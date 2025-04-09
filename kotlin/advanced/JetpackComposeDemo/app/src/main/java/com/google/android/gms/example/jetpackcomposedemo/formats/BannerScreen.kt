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

package com.google.android.gms.example.jetpackcomposedemo.formats

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.compose_util.BannerAd
import com.google.android.gms.example.jetpackcomposedemo.GoogleMobileAdsApplication.Companion.BANNER_AD_UNIT_ID
import com.google.android.gms.example.jetpackcomposedemo.GoogleMobileAdsApplication.Companion.TAG
import com.google.android.gms.example.jetpackcomposedemo.ui.theme.JetpackComposeDemoTheme

// [START banner_screen]
@Composable
fun BannerScreen(modifier: Modifier = Modifier) {

  // Initialize required variables.
  val context = LocalContext.current
  val isPreviewMode = LocalInspectionMode.current
  // Get the current screen width in density-independent pixels (dp).
  // This is used to determine the appropriate banner ad size.
  val deviceWidth = LocalConfiguration.current.screenWidthDp
  // Create a mutable state to hold the AdView instance.
  // It's nullable because it might not be initialized yet or might be destroyed.
  var adView by remember { mutableStateOf<AdView?>(null) }

  // LaunchedEffect is a composable that runs a side effect in a coroutine.
  // It will be re-launched when any of its keys change.
  // In this case, it will re-run if the context or deviceWidth changes.
  LaunchedEffect(context, deviceWidth) {
    adView?.destroy()
    adView = loadAdaptiveBannerAd(context, deviceWidth, isPreviewMode)
  }

  // Place the ad view at the bottom of the screen.
  Column(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {
    adView?.let { adView ->
      Box(modifier = Modifier.fillMaxWidth()) {
        BannerAd(adView, Modifier.align(Alignment.BottomCenter))
      }
    }
  }

  // DisposableEffect runs code when the composable appears and disappears.
  // 'Unit' means it runs only once when the screen is created.
  DisposableEffect(Unit) {
    // Destroy the AdView to release its resources and prevent memory leaks.
    onDispose { adView?.destroy() }
  }
}

// [END banner_screen]

// [START create_ad_view]
private fun loadAdaptiveBannerAd(context: Context, width: Int, isPreviewMode: Boolean): AdView {

  val adView = AdView(context)

  // Prevent loading the AdView if the app is in preview mode.
  if (isPreviewMode) {
    return adView
  }

  // Setup and load the adview.
  // [START_EXCLUDE]
  // Set the unique ID for this specific ad unit.
  adView.adUnitId = BANNER_AD_UNIT_ID

  // [START set_ad_size]
  // Set the adaptive banner ad size based on the current screen orientation.
  val adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, width)
  adView.setAdSize(adSize)
  // [END set_ad_size]

  // [START add_listener]
  // [Optional] Set an AdListener to receive callbacks for various ad events.
  adView.adListener =
    object : AdListener() {
      override fun onAdLoaded() {
        Log.d(TAG, "Banner ad was loaded.")
      }

      override fun onAdFailedToLoad(error: LoadAdError) {
        Log.e(TAG, "Banner ad failed to load: ${error.message}")
      }

      override fun onAdImpression() {
        Log.d(TAG, "Banner ad recorded an impression.")
      }

      override fun onAdClicked() {
        Log.d(TAG, "Banner ad was clicked.")
      }
    }
  // [END add_listener]

  // [START load_ad]
  // Create an AdRequest and load the ad.
  val adRequest = AdRequest.Builder().build()
  adView.loadAd(adRequest)
  // [END load_ad]
  // [END_EXCLUDE]

  // Return the configured AdView instance.
  return adView
}

// [END create_ad_view]

@Preview
@Composable
private fun BannerScreenPreview() {
  JetpackComposeDemoTheme {
    Surface(color = MaterialTheme.colorScheme.background) { BannerScreen() }
  }
}
