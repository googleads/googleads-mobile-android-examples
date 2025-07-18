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
import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

/** Kotlin code snippets for the developer guide. */
class AdPlacementsSnippets {
  private val loadCallback = object : InterstitialAdLoadCallback() {}

  // [START load_interstitial]
  private fun loadInterstitial(context: Context, interstitial: InterstitialAd) {
    val adRequest = AdRequest.Builder().setPlacementId(PLACEMENT_ID).build()
    InterstitialAd.load(context, AD_UNIT_ID, adRequest, loadCallback)
  }

  // [END load_interstitial]

  // [START show_interstitial]
  private fun showAd(activity: Activity, ad: InterstitialAd) {
    ad.placementId = PLACEMENT_ID
    ad.show(activity)
  }

  // [END show_interstitial]

  // [START load_banner]
  private fun loadBannerView(adView: AdView) {
    adView.placementId = PLACEMENT_ID
    adView.loadAd(AdRequest.Builder().build())
  }

  // [END load_banner]

  // [START show_native]
  private fun showNativeAd(nativeAdView: NativeAdView, nativeAd: NativeAd) {
    nativeAd.placementId = PLACEMENT_ID
    nativeAdView.setNativeAd(nativeAd)
  }

  // [END show_native]

  private companion object {
    const val PLACEMENT_ID = 2500718471
    const val AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
  }
}
