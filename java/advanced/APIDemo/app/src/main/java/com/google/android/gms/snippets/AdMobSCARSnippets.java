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

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdFormat;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.query.QueryInfo;
import com.google.android.gms.ads.query.QueryInfoGenerationCallback;

/** Java code snippets for the developer guide. */
public class AdMobSCARSnippets {

  private static final String TAG = "AdmobSCARSnippets";

  public void loadNative(Context applicationContext, String adUnitID) {
    // [START signal_request_native]
    // Create a signal request for an ad.
    AdManagerAdRequest signalRequest =
        new AdManagerAdRequest.Builder().setRequestAgent("REQUEST_AGENT").build();

    // Generate and send the signal request.
    QueryInfo.generate(
        applicationContext,
        AdFormat.NATIVE,
        signalRequest,
        adUnitID,
        new QueryInfoGenerationCallback() {
          @Override
          public void onSuccess(@NonNull final QueryInfo queryInfo) {
            Log.d(TAG, "QueryInfo string: " + queryInfo.getQuery());
            // TODO: Fetch the ad response using your generated query info.
          }

          @Override
          public void onFailure(@NonNull String error) {
            Log.d(TAG, "QueryInfo failed with error: " + error);
            // TODO: Handle error.
          }
        });
    // [END signal_request_native]
  }

  public void loadBanner(Context applicationContext, String adUnitID) {
    // [START signal_request_banner]
    // Set the adaptive banner size.
    // Refer to the AdSize class for available ad sizes.
    AdSize size = AdSize.getCurrentOrientationInlineAdaptiveBannerAdSize(applicationContext, 320);
    Bundle extras = new Bundle();
    extras.putInt("adaptive_banner_w", size.getWidth());
    extras.putInt("adaptive_banner_h", size.getHeight());

    // Create a signal request for an ad.
    AdManagerAdRequest signalRequest =
        new AdManagerAdRequest.Builder()
            .addNetworkExtrasBundle(AdMobAdapter.class, extras)
            .setRequestAgent("REQUEST_AGENT")
            .build();

    // Generate and send the signal request.
    QueryInfo.generate(
        applicationContext,
        AdFormat.BANNER,
        signalRequest,
        adUnitID,
        new QueryInfoGenerationCallback() {
          @Override
          public void onSuccess(@NonNull final QueryInfo queryInfo) {
            Log.d(TAG, "QueryInfo string: " + queryInfo.getQuery());
            // TODO: Fetch the ad response using your generated query info.
          }

          @Override
          public void onFailure(@NonNull String error) {
            Log.d(TAG, "QueryInfo failed with error: " + error);
            // TODO: Handle error.
          }
        });
    // [END signal_request_banner]
  }
}
