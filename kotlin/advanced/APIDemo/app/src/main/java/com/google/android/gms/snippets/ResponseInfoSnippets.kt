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
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.AdapterResponseInfo

/** Kotlin code snippets for the developer guide. */
class ResponseInfoSnippets {
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
