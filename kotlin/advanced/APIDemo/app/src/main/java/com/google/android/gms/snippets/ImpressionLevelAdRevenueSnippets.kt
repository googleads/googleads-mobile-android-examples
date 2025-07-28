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

import com.google.android.gms.ads.OnPaidEventListener
import com.google.android.gms.ads.rewarded.RewardedAd

/** Kotlin code snippets for the developer guide. */
class ImpressionLevelAdRevenueSnippets {

  // [START on_paid_event_listener]
  private fun setOnPaidEventListener(ad: RewardedAd) {
    ad.onPaidEventListener = OnPaidEventListener { adValue ->
      // Extract the impression-level ad revenue data.
      val valueMicros = adValue.valueMicros
      val currencyCode = adValue.currencyCode
      val precision = adValue.precisionType

      // Get the ad unit ID.
      val adUnitId = ad.adUnitId

      // Extract ad response information.
      val loadedAdapterResponseInfo = ad.responseInfo.loadedAdapterResponseInfo
      val adSourceName = loadedAdapterResponseInfo?.adSourceName
      val adSourceId = loadedAdapterResponseInfo?.adSourceId
      val adSourceInstanceName = loadedAdapterResponseInfo?.adSourceInstanceName
      val adSourceInstanceId = loadedAdapterResponseInfo?.adSourceInstanceId
      val extras = ad.responseInfo.responseExtras
      val mediationGroupName = extras.getString("mediation_group_name")
      val mediationABTestName = extras.getString("mediation_ab_test_name")
      val mediationABTestVariant = extras.getString("mediation_ab_test_variant")
    }
  }
  // [END on_paid_event_listener]
}
