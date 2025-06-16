/*
 * Copyright 2025 Google LLC
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

package com.google.android.gms.snippets

import android.app.Activity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.OnPaidEventListener
import com.google.android.gms.ads.ResponseInfo
import com.google.android.gms.ads.interstitial.InterstitialAdPreloader
import com.google.android.gms.ads.preload.PreloadCallbackV2
import com.google.android.gms.ads.preload.PreloadConfiguration

/** Kotlin code snippets for the developer guide. */
private class InterstitialAdPreloaderSnippets {

  private fun startPreload() {
    // [START start_preload]
    // Define a PreloadConfiguration.
    val configuration = PreloadConfiguration.Builder(AD_UNIT_ID).build()
    // Start the preloading with a given preload ID, preload configuration.
    InterstitialAdPreloader.start(AD_UNIT_ID, configuration)
    // [END start_preload]
  }

  private fun startPreloadWithCallback() {
    // [START start_preload_with_callback]
    // Define a PreloadConfiguration.
    val configuration = PreloadConfiguration.Builder(AD_UNIT_ID).build()

    // [START set_callback]
    // Define a callback to receive preload events.
    val callback =
      object : PreloadCallbackV2() {
        override fun onAdPreloaded(preloadId: String, responseInfo: ResponseInfo?) {
          // Called when preloaded ads are available.
        }

        override fun onAdsExhausted(preloadId: String) {
          // Called when no preloaded ads are available.
        }

        override fun onAdFailedToPreload(preloadId: String, adError: AdError) {
          // Called when preloaded ads failed to load.
        }
      }
    // [END set_callback]

    // Start the preloading with a given preload ID, preload configuration, and callback.
    InterstitialAdPreloader.start(AD_UNIT_ID, configuration, callback)
    // [END start_preload_with_callback]
  }

  private fun bufferSize() {
    // [START set_buffer_size]
    // Define a PreloadConfiguration and buffer up to 5 preloaded ads.
    val configuration = PreloadConfiguration.Builder(AD_UNIT_ID).setBufferSize(5).build()
    // [END set_buffer_size]
  }

  private fun pollAndShowAd(activity: Activity) {
    // [START pollAndShowAd]
    // pollAd() returns the next available ad and loads another ad in the background.
    val ad = InterstitialAdPreloader.pollAd(AD_UNIT_ID)

    // [Optional] Interact with the ad as needed.
    ad?.onPaidEventListener = OnPaidEventListener {
      // [Optional] Send the impression-level ad revenue information to your preferred
      // analytics server directly within this callback.
    }

    // Show the ad immediately.
    ad?.show(activity)
    // [END pollAndShowAd]
  }

  private fun isAdAvailable() {
    // [START isAdAvailable]
    // Verify that a preloaded ad is available.
    if (!InterstitialAdPreloader.isAdAvailable(AD_UNIT_ID)) {
      // No ads are available to show.
    }
    // [END isAdAvailable]
  }

  private fun numOfAdsAvailable() {
    // [START numOfAdsAvailable]
    // Check the number of ads available to show.
    val numberOfAds = InterstitialAdPreloader.getNumAdsAvailable(AD_UNIT_ID)
    // [END numOfAdsAvailable]
  }

  private fun getConfiguration() {
    // [START getConfiguration]
    val configuration = InterstitialAdPreloader.getConfiguration(AD_UNIT_ID)
    // [END getConfiguration]
  }

  private fun destroyAd() {
    // [START destroy_ad]
    // Stops the preloading and destroy preloaded ads.
    InterstitialAdPreloader.destroy(AD_UNIT_ID)
    // Stops the preloading and destroy all ads.
    InterstitialAdPreloader.destroyAll()
    // [END destroy_ad]
  }

  companion object {
    // Sample interstitial ad unit ID.
    const val AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
  }
}
