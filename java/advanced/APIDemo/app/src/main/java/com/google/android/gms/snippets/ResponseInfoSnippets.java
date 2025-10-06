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

package com.google.android.gms.snippets;

import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdapterResponseInfo;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.ResponseInfo;
import java.util.List;

/** Java code snippets for the developer guide. */
public class ResponseInfoSnippets {

  private static final String TAG = "ResponseInfoSnippets";

  private void logResponseInfo(AdView adView) {
    adView.setAdListener(
        new AdListener() {
          // [START log_response_info]
          @Override
          public void onAdLoaded() {
            ResponseInfo responseInfo = adView.getResponseInfo();
            Log.d(TAG, responseInfo.toString());
          }

          @Override
          public void onAdFailedToLoad(LoadAdError adError) {
            ResponseInfo responseInfo = adError.getResponseInfo();
            Log.d(TAG, responseInfo.toString());
          }
          // [END log_response_info]
        });
  }

  private void examineResponseInfo(AdView adView) {
    adView.setAdListener(
        new AdListener() {
          // [START get_response_info]
          @Override
          public void onAdLoaded() {
            ResponseInfo responseInfo = adView.getResponseInfo();

            String responseId = responseInfo.getResponseId();
            String mediationAdapterClassName = responseInfo.getMediationAdapterClassName();
            List<AdapterResponseInfo> adapterResponses = responseInfo.getAdapterResponses();
            AdapterResponseInfo loadedAdapterResponseInfo =
                responseInfo.getLoadedAdapterResponseInfo();
            Bundle extras = responseInfo.getResponseExtras();
            String mediationGroupName = extras.getString("mediation_group_name");
            String mediationABTestName = extras.getString("mediation_ab_test_name");
            String mediationABTestVariant = extras.getString("mediation_ab_test_variant");
          }
          // [END get_response_info]
        });
  }

  private void examineAdapterResponseInfo(AdView adView) {
    adView.setAdListener(
        new AdListener() {
          // [START get_adapter_response_info]
          @Override
          public void onAdLoaded() {
            AdapterResponseInfo loadedAdapterResponseInfo =
                adView.getResponseInfo().getLoadedAdapterResponseInfo();

            AdError adError = loadedAdapterResponseInfo.getAdError();
            String adSourceId = loadedAdapterResponseInfo.getAdSourceId();
            String adSourceInstanceId = loadedAdapterResponseInfo.getAdSourceInstanceId();
            String adSourceInstanceName = loadedAdapterResponseInfo.getAdSourceInstanceName();
            String adSourceName = loadedAdapterResponseInfo.getAdSourceName();
            String adapterClassName = loadedAdapterResponseInfo.getAdapterClassName();
            Bundle credentials = loadedAdapterResponseInfo.getCredentials();
            long latencyMillis = loadedAdapterResponseInfo.getLatencyMillis();
          }
          // [END get_adapter_response_info]
        });
  }

  // [START get_ad_source_name]
  private String getUniqueAdSourceName(@NonNull AdapterResponseInfo loadedAdapterResponseInfo) {

    String adSourceName = loadedAdapterResponseInfo.getAdSourceName();
    if (adSourceName.equals("Custom Event")) {
      if (loadedAdapterResponseInfo
          .getAdapterClassName()
          .equals("com.google.ads.mediation.sample.customevent.SampleCustomEvent")) {
        adSourceName = "Sample Ad Network (Custom Event)";
      }
    }
    return adSourceName;
  }

  // [END get_ad_source_name]

  private void logMediationAdapterClassNameFromAd(AdView ad) {
    // [START get_adapter_class_name]
    ResponseInfo responseInfo = ad.getResponseInfo();
    String adapterClassName = null;
    if (responseInfo != null) {
      adapterClassName = responseInfo.getMediationAdapterClassName();
    }
    Log.d(TAG, "Adapter class name: " + adapterClassName);
    // [END get_adapter_class_name]
  }
}
