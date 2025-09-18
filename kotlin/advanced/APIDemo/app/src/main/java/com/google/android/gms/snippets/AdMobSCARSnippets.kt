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
import android.os.Bundle
import android.util.Log
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdFormat
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.query.QueryInfo
import com.google.android.gms.ads.query.QueryInfoGenerationCallback

/** Kotlin code snippets for the developer guide. */
class AdMobSCARSnippets {

  fun loadNative(applicationContext: Context, adUnitID: String) {
    // [START signal_request_native]
    // Create a signal request for an ad.
    val signalRequest: AdManagerAdRequest =
      AdManagerAdRequest.Builder().setRequestAgent("REQUEST_AGENT").build()

    // Generate and send the signal request.
    QueryInfo.generate(
      applicationContext,
      AdFormat.NATIVE,
      signalRequest,
      adUnitID,
      object : QueryInfoGenerationCallback() {
        override fun onSuccess(queryInfo: QueryInfo) {
          Log.d(TAG, "QueryInfo string: " + queryInfo.getQuery())
          // TODO: Fetch the ad response using your generated query info.
        }

        override fun onFailure(error: String) {
          Log.d(TAG, "QueryInfo failed with error: $error")
          // TODO: Handle error.
        }
      },
    )
    // [END signal_request_native]
  }

  fun loadBanner(applicationContext: Context, adUnitID: String) {
    // [START signal_request_banner]
    // Set the adaptive banner size.
    // Refer to the AdSize class for available ad sizes.
    val size: AdSize =
      AdSize.getCurrentOrientationInlineAdaptiveBannerAdSize(applicationContext, 320)
    val extras = Bundle()
    extras.putInt("adaptive_banner_w", size.width)
    extras.putInt("adaptive_banner_h", size.height)

    // Create a signal request for an ad.
    val signalRequest: AdManagerAdRequest =
      AdManagerAdRequest.Builder()
        .setRequestAgent("REQUEST_AGENT")
        .addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
        .build()

    // Generate and send the signal request.
    QueryInfo.generate(
      applicationContext,
      AdFormat.BANNER,
      signalRequest,
      adUnitID,
      object : QueryInfoGenerationCallback() {
        override fun onSuccess(queryInfo: QueryInfo) {
          Log.d(TAG, "QueryInfo string: " + queryInfo.getQuery())
          // TODO: Fetch the ad response using your generated query info.
        }

        override fun onFailure(error: String) {
          Log.d(TAG, "QueryInfo failed with error: $error")
          // TODO: Handle error.
        }
      },
    )
    // [END signal_request_banner]
  }

  companion object {
    private const val TAG = "AdmobSCARSnippets"
  }
}
