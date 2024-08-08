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

package com.google.android.gms.example.jetpackcomposedemo

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import com.example.jetpackcomposedemo.R
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.compose_util.BannerAd
import com.google.android.gms.example.jetpackcomposedemo.GoogleMobileAdsApplication.Companion.BANNER_ADUNIT_ID
import com.google.android.gms.example.jetpackcomposedemo.GoogleMobileAdsApplication.Companion.TAG
import com.google.android.gms.example.jetpackcomposedemo.ui.theme.JetpackComposeDemoTheme

@Preview
@Composable
private fun BannerScreenPreview() {
  JetpackComposeDemoTheme {
    Surface(color = MaterialTheme.colorScheme.background) { BannerScreen() }
  }
}

@Composable
fun BannerScreen(modifier: Modifier = Modifier) {
  val context = LocalContext.current
  val isPreviewMode = LocalInspectionMode.current
  val deviceWidth = LocalConfiguration.current.screenWidthDp
  var adView by remember { mutableStateOf<AdView?>(null) }

  LaunchedEffect(context, deviceWidth) {
    // Create a new adview when size changes.
    adView?.destroy()
    adView = loadAdaptiveBannerAd(context, deviceWidth, isPreviewMode)
  }

  Column(modifier) {
    // Reload the adview when the button is clicked.
    Button(
      {
        adView?.destroy()
        adView = loadAdaptiveBannerAd(context, deviceWidth, isPreviewMode)
      },
      modifier = Modifier.fillMaxWidth(),
    ) {
      Text(text = context.getString(R.string.text_reload))
    }

    adView?.let { BannerAd(it, Modifier.fillMaxWidth()) }
  }

  // Clean up the AdView after use.
  DisposableEffect(Unit) { onDispose { adView?.destroy() } }
}

private fun loadAdaptiveBannerAd(context: Context, width: Int, isPreviewMode: Boolean): AdView {
  val adView = AdView(context)

  // Do not load the ad in preview mode.
  if (isPreviewMode) {
    return adView
  }

  adView.adUnitId = BANNER_ADUNIT_ID
  val adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, width)
  adView.setAdSize(adSize)

  adView.adListener =
    object : AdListener() {
      override fun onAdLoaded() {
        super.onAdLoaded()
        Log.i(TAG, context.getString(R.string.banner_loaded))
      }

      override fun onAdFailedToLoad(error: LoadAdError) {
        Log.e(TAG, context.getString(R.string.banner_failedToLoad))
      }

      override fun onAdImpression() {
        super.onAdImpression()
        Log.i(TAG, context.getString(R.string.banner_impression))
      }

      override fun onAdClicked() {
        super.onAdClicked()
        Log.i(TAG, context.getString(R.string.banner_clicked))
      }
    }

  val adRequest = AdRequest.Builder().build()
  adView.loadAd(adRequest)
  return adView
}
