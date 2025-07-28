/*
 * Copyright 2025 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gms.snippets;

import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.gms.ads.AdValue;
import com.google.android.gms.ads.AdapterResponseInfo;
import com.google.android.gms.ads.OnPaidEventListener;
import com.google.android.gms.ads.rewarded.RewardedAd;

/** Java code snippets for the developer guide. */
public class ImpressionLevelAdRevenueSnippets {

  // [START on_paid_event_listener]
  private void setOnPaidEventListener(RewardedAd ad) {
    ad.setOnPaidEventListener(
        new OnPaidEventListener() {
          @Override
          public void onPaidEvent(@NonNull AdValue adValue) {
            // Extract the impression-level ad revenue data.
            long valueMicros = adValue.getValueMicros();
            String currencyCode = adValue.getCurrencyCode();
            int precision = adValue.getPrecisionType();

            // Get the ad unit ID.
            String adUnitId = ad.getAdUnitId();

            // Extract ad response information.
            AdapterResponseInfo loadedAdapterResponseInfo =
                ad.getResponseInfo().getLoadedAdapterResponseInfo();
            if (loadedAdapterResponseInfo != null) {
              String adSourceName = loadedAdapterResponseInfo.getAdSourceName();
              String adSourceId = loadedAdapterResponseInfo.getAdSourceId();
              String adSourceInstanceName = loadedAdapterResponseInfo.getAdSourceInstanceName();
              String adSourceInstanceId = loadedAdapterResponseInfo.getAdSourceInstanceId();

              Bundle extras = ad.getResponseInfo().getResponseExtras();
              String mediationGroupName = extras.getString("mediation_group_name");
              String mediationABTestName = extras.getString("mediation_ab_test_name");
              String mediationABTestVariant = extras.getString("mediation_ab_test_variant");
            }
          }
        });
  }
  // [END on_paid_event_listener]
}
