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

import com.google.android.gms.ads.AdapterResponseInfo

/** Kotlin code snippets for the developer guide. */
class ResponseInfoSnippets {
  private fun getAdSourceName(loadedAdapterResponseInfo: AdapterResponseInfo): String {
    // [START get_ad_source_name]
    var adSourceName = loadedAdapterResponseInfo.adSourceName
    if (adSourceName == "Custom Event") {
      if (
        loadedAdapterResponseInfo.adapterClassName ==
          "com.google.ads.mediation.sample.customevent.SampleCustomEvent"
      ) {
        adSourceName = "Sample Ad Network (Custom Event)"
      }
    }
    // [END get_ad_source_name]
    return adSourceName
  }
}
