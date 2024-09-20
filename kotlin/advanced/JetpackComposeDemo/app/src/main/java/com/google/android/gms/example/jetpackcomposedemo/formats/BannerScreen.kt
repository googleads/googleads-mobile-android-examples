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

@Composable
fun BannerScreen(modifier: Modifier = Modifier) {
  val context = LocalContext.current
  val isPreviewMode = LocalInspectionMode.current
  val deviceWidth = LocalConfiguration.current.screenWidthDp
  var adView by remember { mutableStateOf<AdView?>(null) }

  LaunchedEffect(context, deviceWidth) {
    // Create a new AdView when size changes.
    adView?.destroy()
    adView = loadAdaptiveBannerAd(context, deviceWidth, isPreviewMode)
  }

  Column(modifier = modifier.fillMaxSize(), verticalArrangement = Arrangement.Bottom) {
    adView?.let { adView ->
      Box(modifier = Modifier.fillMaxWidth()) {
        BannerAd(adView, Modifier.align(Alignment.BottomCenter))
      }
    }
  }

  // Clean up the AdView after use.
  DisposableEffect(Unit) { onDispose { adView?.destroy() } }
}

private fun loadAdaptiveBannerAd(context: Context, width: Int, isPreviewMode: Boolean): AdView {
  val adView = AdView(context)

  // Do not load the AdView in preview mode.
  if (isPreviewMode) {
    return adView
  }

  adView.adUnitId = BANNER_AD_UNIT_ID
  val adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, width)
  adView.setAdSize(adSize)

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

  val adRequest = AdRequest.Builder().build()
  adView.loadAd(adRequest)
  return adView
}

@Preview
@Composable
private fun BannerScreenPreview() {
  JetpackComposeDemoTheme {
    Surface(color = MaterialTheme.colorScheme.background) { BannerScreen() }
  }
}
