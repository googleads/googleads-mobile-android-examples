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

import android.util.Log
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.AdapterResponseInfo
import com.google.android.gms.ads.LoadAdError

/** Kotlin code snippets for the developer guide. */
class ResponseInfoSnippets {

  private fun logResponseInfo(adView: AdView) {
    adView.adListener =
      object : AdListener() {
        // [START log_response_info]
        override fun onAdLoaded() {
          val responseInfo = adView.responseInfo
          Log.d(TAG, responseInfo.toString())
        }

        override fun onAdFailedToLoad(adError: LoadAdError) {
          val responseInfo = adError.responseInfo
          Log.d(TAG, responseInfo.toString())
        }
        // [END log_response_info]
      }
  }

  private fun useResponseInfo(adView: AdView) {
    adView.adListener =
      object : AdListener() {
        // [START get_response_info]
        override fun onAdLoaded() {
          val responseInfo = adView.responseInfo

          val responseId = responseInfo?.responseId
          val mediationAdapterClassName = responseInfo?.mediationAdapterClassName
          val adapterResponses = responseInfo?.adapterResponses
          val loadedAdapterResponseInfo = responseInfo?.loadedAdapterResponseInfo
          val extras = responseInfo?.responseExtras
          val mediationGroupName = extras?.getString("mediation_group_name")
          val mediationABTestName = extras?.getString("mediation_ab_test_name")
          val mediationABTestVariant = extras?.getString("mediation_ab_test_variant")
        }
        // [END get_response_info]
      }
  }

  private fun useAdapterResponseInfo(adView: AdView) {
    adView.adListener =
      object : AdListener() {
        // [START get_adapter_response_info]
        override fun onAdLoaded() {
          val loadedAdapterResponseInfo = adView.responseInfo?.loadedAdapterResponseInfo

          val adError = loadedAdapterResponseInfo?.adError
          val adSourceId = loadedAdapterResponseInfo?.adSourceId
          val adSourceInstanceId = loadedAdapterResponseInfo?.adSourceInstanceId
          val adSourceInstanceName = loadedAdapterResponseInfo?.adSourceInstanceName
          val adSourceName = loadedAdapterResponseInfo?.adSourceName
          val adapterClassName = loadedAdapterResponseInfo?.adapterClassName
          val credentials = loadedAdapterResponseInfo?.credentials
          val latencyMillis = loadedAdapterResponseInfo?.latencyMillis
        }
        // [END get_adapter_response_info]
      }
  }

  // [START get_ad_source_name]
  private fun getUniqueAdSourceName(loadedAdapterResponseInfo: AdapterResponseInfo): String {

    var adSourceName = loadedAdapterResponseInfo.adSourceName
    if (adSourceName == "Custom Event") {
      if (
        loadedAdapterResponseInfo.adapterClassName ==
          "com.google.ads.mediation.sample.customevent.SampleCustomEvent"
      ) {
        adSourceName = "Sample Ad Network (Custom Event)"
      }
    }
    return adSourceName
  }

  // [END get_ad_source_name]

  private fun getMediationAdapterClassNameFromAd(ad: AdView) {
    // [START get_adapter_class_name]
    Log.d(TAG, "Adapter class name:" + ad.responseInfo?.mediationAdapterClassName)
    // [END get_adapter_class_name]
  }

  private companion object {
    const val TAG = "ResponseInfoSnippets"
  }
}
