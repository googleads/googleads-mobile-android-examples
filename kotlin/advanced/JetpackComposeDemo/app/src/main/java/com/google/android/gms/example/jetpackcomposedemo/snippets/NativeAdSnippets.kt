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

package com.google.android.gms.example.jetpackcomposedemo.snippets

import android.content.Context
import android.widget.ImageView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.jetpackcomposedemo.R
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.compose_util.NativeAdAttribution
import com.google.android.gms.compose_util.NativeAdHeadlineView
import com.google.android.gms.compose_util.NativeAdMediaView
import com.google.android.gms.compose_util.NativeAdView

/** Kotlin code snippets for the developer guide. */
internal class NativeAdSnippets {

  // [START define_native_ad_view]
  @Composable
  /** Display a native ad with a user defined template. */
  fun DisplayNativeAdView(nativeAd: NativeAd) {
    val context = LocalContext.current
    Box(modifier = Modifier.padding(8.dp).wrapContentHeight(Alignment.Top)) {
      // Call the NativeAdView composable to display the native ad.
      NativeAdView {
        // Inside the NativeAdView composable, display the native ad assets.
        Column(Modifier.align(Alignment.TopStart).wrapContentHeight(Alignment.Top)) {
          // Display the ad attribution. This is required.
          NativeAdAttribution(text = context.getString(R.string.attribution))
          // Display the headline asset. This is required.
          nativeAd.headline?.let {
            NativeAdHeadlineView { Text(text = it, style = MaterialTheme.typography.headlineLarge) }
          }
          // Display the media asset. This is required.
          NativeAdMediaView(Modifier.fillMaxWidth().height(500.dp).fillMaxHeight())
        }
      }
    }
  }

  // [END define_native_ad_view]

  // [START display_native_ad_screen]
  @Composable
  fun DisplayNativeAdScreen(adunitId: String) {
    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }
    val context = LocalContext.current
    var isDisposed by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
      // Load the native ad when we launch this screen
      loadNativeAd(
        context = context,
        adunitId,
        onAdLoaded = { ad ->
          // Handle the native ad being loaded.
          if (!isDisposed) {
            nativeAd = ad
          } else {
            // Destroy the native ad if loaded after the screen is disposed.
            ad.destroy()
          }
        },
      )
      // [START destroy_native_ad]
      // Destroy the native ad to prevent memory leaks when we dispose of this screen.
      onDispose {
        isDisposed = true
        nativeAd?.destroy()
        nativeAd = null
      }
      // [END destroy_native_ad]
    }

    // Display the native ad view with a user defined template here.
    nativeAd?.let { adValue -> DisplayNativeAdView(adValue) }
  }

  // [END display_native_ad_screen]

  // [START load_native_ad_compose]
  fun loadNativeAd(context: Context, adUnitId: String, onAdLoaded: (NativeAd) -> Unit) {
    val adLoader =
      AdLoader.Builder(context, adUnitId)
        .forNativeAd { nativeAd -> onAdLoaded(nativeAd) }
        .withAdListener(
          object : AdListener() {
            override fun onAdFailedToLoad(error: LoadAdError) {
              // Native ad failed to load.
            }

            override fun onAdLoaded() {
              // Native ad was loaded.
            }

            override fun onAdImpression() {
              // Native ad recorded an impression.
            }

            override fun onAdClicked() {
              // Native ad was clicked.
            }
          }
        )
        .build()
    adLoader.loadAd(AdRequest.Builder().build())
  }

  // [END load_native_ad_compose]

  @Composable
  fun SetImageScaleType() {
    // [START set_image_scale_type_compose]
    NativeAdMediaView(Modifier.fillMaxWidth(), scaleType = ImageView.ScaleType.CENTER_CROP)
    // [END set_image_scale_type_compose]
  }
}
