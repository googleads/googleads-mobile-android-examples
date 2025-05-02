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
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.example.jetpackcomposedemo.R
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.compose_util.NativeAdAttribution
import com.google.android.gms.compose_util.NativeAdBodyView
import com.google.android.gms.compose_util.NativeAdButton
import com.google.android.gms.compose_util.NativeAdCallToActionView
import com.google.android.gms.compose_util.NativeAdHeadlineView
import com.google.android.gms.compose_util.NativeAdIconView
import com.google.android.gms.compose_util.NativeAdMediaView
import com.google.android.gms.compose_util.NativeAdPriceView
import com.google.android.gms.compose_util.NativeAdStarRatingView
import com.google.android.gms.compose_util.NativeAdStoreView
import com.google.android.gms.compose_util.NativeAdView
import com.google.android.gms.example.jetpackcomposedemo.GoogleMobileAdsApplication.Companion.NATIVE_AD_UNIT_ID
import com.google.android.gms.example.jetpackcomposedemo.GoogleMobileAdsApplication.Companion.TAG
import com.google.android.gms.example.jetpackcomposedemo.ui.theme.JetpackComposeDemoTheme

// [START ad_listener]
@Composable
/** Load and display a native ad. */
fun NativeScreen() {
  var nativeAd by remember { mutableStateOf<NativeAd?>(null) }
  val context = LocalContext.current
  var isDisposed by remember { mutableStateOf(false) }

  DisposableEffect(Unit) {
    // Load the native ad when we launch this screen
    loadNativeAd(
      context = context,
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
    // Destroy the native ad to prevent memory leaks when we dispose of this screen.
    onDispose {
      isDisposed = true
      nativeAd?.destroy()
      nativeAd = null
    }
  }

  // Display the native ad view with a user defined template.
  nativeAd?.let { adValue -> DisplayNativeAdView(adValue) }
}

fun loadNativeAd(context: Context, onAdLoaded: (NativeAd) -> Unit) {
  val adLoader =
    AdLoader.Builder(context, NATIVE_AD_UNIT_ID)
      .forNativeAd { nativeAd -> onAdLoaded(nativeAd) }
      .withAdListener(
        object : AdListener() {
          override fun onAdFailedToLoad(error: LoadAdError) {
            Log.e(TAG, "Native ad failed to load: ${error.message}")
          }

          override fun onAdLoaded() {
            Log.d(TAG, "Native ad was loaded.")
          }

          override fun onAdImpression() {
            Log.d(TAG, "Native ad recorded an impression.")
          }

          override fun onAdClicked() {
            Log.d(TAG, "Native ad was clicked.")
          }
        }
      )
      .build()
  adLoader.loadAd(AdRequest.Builder().build())
}

// [END ad_listener]

// [START display_native_ad]
@Composable
/** Display a native ad with a user defined template. */
fun DisplayNativeAdView(nativeAd: NativeAd) {
  val context = LocalContext.current
  Box(modifier = Modifier.padding(8.dp).wrapContentHeight(Alignment.Top)) {
    // Call the NativeAdView composable to display the native ad.
    NativeAdView {
      // Inside the NativeAdView composable, display the native ad assets.
      Column(Modifier.align(Alignment.TopStart).wrapContentHeight(Alignment.Top)) {
        // Display the ad attribution.
        NativeAdAttribution(text = context.getString(R.string.attribution))
        Row {
          // If available, display the icon asset.
          nativeAd.icon?.let { icon ->
            NativeAdIconView(Modifier.padding(5.dp)) {
              icon.drawable?.toBitmap()?.let { bitmap ->
                Image(bitmap = bitmap.asImageBitmap(), "Icon")
              }
            }
          }
          Column {
            // If available, display the headline asset.
            nativeAd.headline?.let {
              NativeAdHeadlineView {
                Text(text = it, style = MaterialTheme.typography.headlineLarge)
              }
            }
            // If available, display the star rating asset.
            nativeAd.starRating?.let {
              NativeAdStarRatingView {
                Text(text = "Rated $it", style = MaterialTheme.typography.labelMedium)
              }
            }
          }
        }

        // If available, display the body asset.
        nativeAd.body?.let { NativeAdBodyView { Text(text = it) } }
        // Display the media asset.
        NativeAdMediaView(Modifier.fillMaxWidth().height(500.dp).fillMaxHeight())

        Row(Modifier.align(Alignment.End).padding(5.dp)) {
          // If available, display the price asset.
          nativeAd.price?.let {
            NativeAdPriceView(Modifier.padding(5.dp).align(Alignment.CenterVertically)) {
              Text(text = it)
            }
          }
          // If available, display the store asset.
          nativeAd.store?.let {
            NativeAdStoreView(Modifier.padding(5.dp).align(Alignment.CenterVertically)) {
              Text(text = it)
            }
          }
          // If available, display the call to action asset.
          // Note: The Jetpack Compose button implements a click handler which overrides the native
          // ad click handler, causing issues. Use the NativeAdButton which does not implement a
          // click handler. To handle native ad clicks, use the NativeAd AdListener onAdClicked
          // callback.
          nativeAd.callToAction?.let { callToAction ->
            NativeAdCallToActionView(Modifier.padding(5.dp)) { NativeAdButton(text = callToAction) }
          }
        }
      }
    }
  }
}

// [END display_native_ad]

@Preview
@Composable
fun NativeLayoutScreenPreview() {
  JetpackComposeDemoTheme {
    Surface(color = MaterialTheme.colorScheme.background) { NativeScreen() }
  }
}
