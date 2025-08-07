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

import android.content.Context
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
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

  private fun handleAdLoaded(adLoaderBuilder: AdLoader.Builder) {
    // [START handle_ad_loaded]
    adLoaderBuilder
      .forNativeAd { nativeAd ->
        // This callback is invoked when a native ad is successfully loaded.
      }
      .build()
    // [END handle_ad_loaded]
  }

  private fun destroyAd(nativeAd: NativeAd) {
    // [START destroy_ad]
    nativeAd.destroy()
    // [END destroy_ad]
  }

  private companion object {
    // Test ad unit IDs.
    // For more information,
    // see https://developers.google.com/admob/android/test-ads.
    // and https://developers.google.com/ad-manager/mobile-ads-sdk/android/test-ads.
    const val AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110"
    const val VIDEO_AD_UNIT_ID = "ca-app-pub-3940256099942544/1044960115"
    const val ADMANAGER_AD_UNIT_ID = "/21775744923/example/native"
    const val ADMANAGER_VIDEO_AD_UNIT_ID = "/21775744923/example/native-video"
  }
}
