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
public class AdManagerSCARSnippets {

  private static final String TAG = "AdManagerSCARSnippets";

  public void loadNative(Context applicationContext, String adUnitID) {
    // [START signal_request_native]
    // Specify the "query_info_type" as "requester_type_8" to
    // denote that the usage of QueryInfo is for Ad Manager S2S.
    Bundle extras = new Bundle();
    extras.putString("query_info_type", "requester_type_8");

    // Create a signal request for an ad.
    AdManagerAdRequest signalRequest =
        new AdManagerAdRequest.Builder()
            .addNetworkExtrasBundle(AdMobAdapter.class, extras)
            .setRequestAgent("request_agent")
            .build();

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
    // Specify the "query_info_type" as "requester_type_8" to
    // denote that the usage of QueryInfo is for Ad Manager S2S.
    Bundle extras = new Bundle();
    extras.putString("query_info_type", "requester_type_8");

    // Set the adaptive banner size.
    AdSize size = AdSize.getCurrentOrientationInlineAdaptiveBannerAdSize(applicationContext, 320);
    extras.putInt("adaptive_banner_w", size.getWidth());
    extras.putInt("adaptive_banner_h", size.getHeight());

    // Create a signal request for an ad.
    AdManagerAdRequest signalRequest =
        new AdManagerAdRequest.Builder()
            .addNetworkExtrasBundle(AdMobAdapter.class, extras)
            .setRequestAgent("request_agent")
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

  public void loadNativePlusBanner(Context applicationContext, String adUnitID) {
    // [START signal_request_native_plus_banner]
    // Specify the "query_info_type" as "requester_type_8" to
    // denote that the usage of QueryInfo is for Ad Manager S2S.
    Bundle extras = new Bundle();
    extras.putString("query_info_type", "requester_type_8");

    // Set the adaptive banner size.
    AdSize size = AdSize.getCurrentOrientationInlineAdaptiveBannerAdSize(applicationContext, 320);
    extras.putInt("adaptive_banner_w", size.getWidth());
    extras.putInt("adaptive_banner_h", size.getHeight());

    // Create a signal request for an ad.
    AdManagerAdRequest signalRequest =
        new AdManagerAdRequest.Builder()
            .addNetworkExtrasBundle(AdMobAdapter.class, extras)
            .setRequestAgent("request_agent")
            .build();

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
    // [END signal_request_native_plus_banner]
  }
}
