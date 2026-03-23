// Copyright 2026 Google LLC
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
import android.os.Bundle
import android.util.Log
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdFormat
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.query.QueryInfo
import com.google.android.gms.ads.query.QueryInfoGenerationCallback

/** Kotlin code snippets for the developer guide. */
class AdMobSCARSnippets {

  fun loadNative(context: Context, adUnitID: String) {
    // [START signal_request_native]
    // Create a signal request for an ad.
    val signalRequest: AdRequest = AdRequest.Builder().build()

    // Generate and send the signal request.
    QueryInfo.generate(
      context,
      AdFormat.NATIVE,
      signalRequest,
      adUnitID,
      object : QueryInfoGenerationCallback() {
        override fun onSuccess(queryInfo: QueryInfo) {
          Log.d(TAG, "QueryInfo string: " + queryInfo.getQuery())
          // Fetch the ad response using your generated query info.
          val adResponseString = fetchAdResponseString(queryInfo)
          renderNative(context, adUnitID, adResponseString)
        }

        override fun onFailure(error: String) {
          Log.e(TAG, "QueryInfo failed with error: $error")
          // TODO: Handle error.
        }
      },
    )
    // [END signal_request_native]
  }

  fun loadBanner(context: Context, adUnitID: String) {
    // [START signal_request_banner]
    // Set the adaptive banner size.
    // Refer to the AdSize class for available ad sizes.
    val size: AdSize = AdSize.getCurrentOrientationInlineAdaptiveBannerAdSize(context, 320)
    val extras = Bundle()
    extras.putInt("adaptive_banner_w", size.width)
    extras.putInt("adaptive_banner_h", size.height)

    // Create a signal request for an ad.
    val signalRequest: AdRequest =
      AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter::class.java, extras).build()

    // Generate and send the signal request.
    QueryInfo.generate(
      context,
      AdFormat.BANNER,
      signalRequest,
      adUnitID,
      object : QueryInfoGenerationCallback() {
        override fun onSuccess(queryInfo: QueryInfo) {
          Log.d(TAG, "QueryInfo string: " + queryInfo.getQuery())
          // Fetch the ad response using your generated query info.
          val adResponseString = fetchAdResponseString(queryInfo)
          renderBanner(context, adUnitID, adResponseString)
        }

        override fun onFailure(error: String) {
          Log.e(TAG, "QueryInfo failed with error: $error")
          // TODO: Handle error.
        }
      },
    )
    // [END signal_request_banner]
  }

  fun loadNativeWithOptions(context: Context, adUnitID: String) {
    // [START native_ad_options]
    val videoOptions = VideoOptions.Builder().setStartMuted(true).build()
    val adOptions =
      NativeAdOptions.Builder()
        .setReturnUrlsForImageAssets(false)
        .setVideoOptions(videoOptions)
        .build()

    val extras = Bundle()
    // Provide native media aspect ratio through the network extras bundle.
    extras.putInt(
      "gad_native_media_aspect_ratio",
      NativeAdOptions.NATIVE_MEDIA_ASPECT_RATIO_LANDSCAPE,
    )

    // Create a signal request for an ad.
    val signalRequest: AdRequest =
      AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter::class.java, extras).build()

    // Generate and send the signal request.
    QueryInfo.generate(
      context,
      AdFormat.NATIVE,
      signalRequest,
      adUnitID,
      object : QueryInfoGenerationCallback() {
        override fun onSuccess(queryInfo: QueryInfo) {
          Log.d(TAG, "QueryInfo string: " + queryInfo.getQuery())
          // Fetch the ad response using your generated query info.
          val adResponseString = fetchAdResponseString(queryInfo)
          renderNative(context, adUnitID, adResponseString)
        }

        override fun onFailure(error: String) {
          Log.e(TAG, "QueryInfo failed with error: $error")
          // TODO: Handle error.
        }
      },
    )
    // [END native_ad_options]
  }

  // [START fetch_response]
  // Emulates a request to your ad server.
  private fun fetchAdResponseString(queryInfo: QueryInfo): String {
    // This value is generated dynamically at runtime from your ad server.
    return "Ad response"
  }

  // [END fetch_response]

  fun renderNative(context: Context, adUnitID: String, adResponseString: String) {
    // [START render_native]
    val adRequest = AdRequest.Builder().setAdString(adResponseString).build()

    val adLoader =
      AdLoader.Builder(context, adUnitID)
        .forNativeAd { nativeAd ->
          Log.d(TAG, "Native ad rendered.")
          // TODO: Show the ad.
        }
        .build()
    adLoader.loadAd(adRequest)
    // [END render_native]
  }

  fun renderBanner(context: Context, adUnitID: String, adResponseString: String) {
    // [START render_banner]
    val adRequest = AdRequest.Builder().setAdString(adResponseString).build()

    val adView = AdView(context)
    adView.adUnitId = adUnitID
    adView.setAdSize(AdSize.BANNER)
    adView.loadAd(adRequest)
    // [END render_banner]
  }

  companion object {
    private const val TAG = "AdMobSCARSnippets"
  }
}
