package com.google.android.gms.example.jetpackcomposedemo.composables

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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

/**
 * A composable function to display a banner advertisement.
 *
 * @param bannerAdState The BannerState object containing ad configuration.
 * @param modifier The modifier to apply to the banner ad.
 */
@Composable
fun BannerAd(bannerAdState: BannerAdState, modifier: Modifier) {
  // Remember the adView so we can dispose of it later.
  var adView by remember { mutableStateOf<AdView?>(null) }

  if (LocalInspectionMode.current) {
    Box(
      modifier =
        Modifier.background(Color.Gray)
          .width(bannerAdState.adSize.width.dp)
          .height(bannerAdState.adSize.height.dp)
    ) {
      Text(text = "Google Mobile Ads preview banner.", modifier.align(Alignment.Center))
    }
    return
  }

  AndroidView(
    modifier = modifier.fillMaxWidth(),
    factory = { context ->
      AdView(context).apply {
        // Make sure we only run this code block once and in non-preview mode.
        if (adView != null) {
          return@apply
        }

        adView = this
        this.adUnitId = bannerAdState.adUnitId
        this.setAdSize(bannerAdState.adSize)
        this.adListener =
          object : AdListener() {
            override fun onAdLoaded() {
              bannerAdState.onAdLoaded?.invoke()
            }

            override fun onAdFailedToLoad(error: LoadAdError) {
              bannerAdState.onAdFailedToLoad?.invoke(error)
            }

            override fun onAdImpression() {
              bannerAdState.onAdImpression?.invoke()
            }

            override fun onAdClosed() {
              bannerAdState.onAdClosed?.invoke()
            }

            override fun onAdClicked() {
              bannerAdState.onAdClicked?.invoke()
            }

            override fun onAdOpened() {
              bannerAdState.onAdClicked?.invoke()
            }

            override fun onAdSwipeGestureClicked() {
              bannerAdState.onAdSwipeGestureClicked?.invoke()
            }
          }
        this.loadAd(bannerAdState.adRequest)
      }
    },
  )
  // Clean up the AdView after use.
  DisposableEffect(Unit) { onDispose { adView?.destroy() } }
}
