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

import android.view.ViewGroup
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdView
import com.google.android.gms.ads.admanager.AppEventListener

private class BannerSnippets : AppEventListener {

  private var adView: AdView? = null
  private var adManagerAdView: AdManagerAdView? = null

  fun setAdListener() {
    // [START ad_events]
    adView?.adListener =
      object : AdListener() {
        override fun onAdClicked() {
          // Code to be executed when the user clicks on an ad.
        }

        override fun onAdClosed() {
          // Code to be executed when the user is about to return
          // to the app after tapping on an ad.
        }

        override fun onAdFailedToLoad(adError: LoadAdError) {
          // Code to be executed when an ad request fails.
        }

        override fun onAdImpression() {
          // Code to be executed when an impression is recorded
          // for an ad.
        }

        override fun onAdLoaded() {
          // Code to be executed when an ad finishes loading.
        }

        override fun onAdOpened() {
          // Code to be executed when an ad opens an overlay that
          // covers the screen.
        }
      }
    // [END ad_events]
  }

  // [START destroy]
  fun destroyBanner() {
    // Remove banner from view hierarchy.
    val parentView = adView?.parent
    if (parentView is ViewGroup) {
      parentView.removeView(adView)
    }

    // Destroy the banner ad resources.
    adView?.destroy()

    // Drop reference to the banner ad.
    adView = null
  }

  // [END destroy]

  fun manualImpressionCounting() {
    // [START enable_manual_impressions]
    adManagerAdView?.setManualImpressionsEnabled(true)
    // [END enable_manual_impressions]
  }

  fun recordManualImpression() {
    // [START record_manual_impressions]
    adManagerAdView?.recordManualImpression()
    // [END record_manual_impressions]
  }

  fun setAppEventListener() {
    // [START set_app_event_listener]
    adManagerAdView?.appEventListener = this
    // [END set_app_event_listener]
  }

  // [START app_events]
  override fun onAppEvent(name: String, info: String) {
    if (name == "color") {
      when (info) {
        "green" -> {
          // Set background color to green.
        }
        "blue" -> {
          // Set background color to blue.
        }
        else -> {
          // Set background color to black.
        }
      }
    }
  }
  // [END app_events]
}
