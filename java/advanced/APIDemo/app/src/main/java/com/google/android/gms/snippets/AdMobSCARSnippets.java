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

package com.google.android.gms.snippets;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdFormat;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.query.QueryInfo;
import com.google.android.gms.ads.query.QueryInfoGenerationCallback;

/** Java code snippets for the developer guide. */
public class AdMobSCARSnippets {

  private static final String TAG = "AdMobSCARSnippets";

  public void loadNative(Context context, String adUnitId) {
    // [START signal_request_native]
    // Create a signal request for an ad.
    AdRequest signalRequest = new AdRequest.Builder().build();

    // Generate and send the signal request.
    QueryInfo.generate(
        context,
        AdFormat.NATIVE,
        signalRequest,
        adUnitId,
        new QueryInfoGenerationCallback() {
          @Override
          public void onSuccess(@NonNull final QueryInfo queryInfo) {
            Log.d(TAG, "QueryInfo string: " + queryInfo.getQuery());
            // Fetch the ad response using your generated query info.
            String adResponseString = fetchAdResponseString(queryInfo);
            // Render the ad.
            renderNative(context, adUnitId, adResponseString);
          }

          @Override
          public void onFailure(@NonNull String error) {
            Log.e(TAG, "QueryInfo failed with error: " + error);
            // TODO: Handle error.
          }
        });
    // [END signal_request_native]
  }

  public void loadBanner(Context context, String adUnitId) {
    // [START signal_request_banner]
    // Set the adaptive banner size.
    // Refer to the AdSize class for available ad sizes.
    AdSize size = AdSize.getCurrentOrientationInlineAdaptiveBannerAdSize(context, 320);
    Bundle extras = new Bundle();
    extras.putInt("adaptive_banner_w", size.getWidth());
    extras.putInt("adaptive_banner_h", size.getHeight());

    // Create a signal request for an ad.
    AdRequest signalRequest =
        new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, extras).build();

    // Generate and send the signal request.
    QueryInfo.generate(
        context,
        AdFormat.BANNER,
        signalRequest,
        adUnitId,
        new QueryInfoGenerationCallback() {
          @Override
          public void onSuccess(@NonNull final QueryInfo queryInfo) {
            Log.d(TAG, "QueryInfo string: " + queryInfo.getQuery());
            // Fetch the ad response using your generated query info.
            String adResponseString = fetchAdResponseString(queryInfo);
            // Render the ad.
            renderBanner(context, adUnitId, adResponseString);
          }

          @Override
          public void onFailure(@NonNull String error) {
            Log.e(TAG, "QueryInfo failed with error: " + error);
            // TODO: Handle error.
          }
        });
    // [END signal_request_banner]
  }

  public void loadNativeWithOptions(Context context, String adUnitId) {
    // [START native_ad_options]
    VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(true).build();
    NativeAdOptions adOptions =
        new NativeAdOptions.Builder()
            .setReturnUrlsForImageAssets(false)
            .setVideoOptions(videoOptions)
            .build();

    Bundle extras = new Bundle();
    // Provide native media aspect ratio through the network extras bundle.
    extras.putInt(
        "gad_native_media_aspect_ratio", NativeAdOptions.NATIVE_MEDIA_ASPECT_RATIO_LANDSCAPE);

    // Create a signal request for an ad.
    AdRequest signalRequest =
        new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, extras).build();

    // Generate and send the signal request.
    QueryInfo.generate(
        context,
        AdFormat.NATIVE,
        signalRequest,
        adUnitId,
        new QueryInfoGenerationCallback() {
          @Override
          public void onSuccess(@NonNull final QueryInfo queryInfo) {
            Log.d(TAG, "QueryInfo string: " + queryInfo.getQuery());
            // Fetch the ad response using your generated query info.
            String adResponseString = fetchAdResponseString(queryInfo);
            // Render the ad.
            renderNative(context, adUnitId, adResponseString);
          }

          @Override
          public void onFailure(@NonNull String error) {
            Log.e(TAG, "QueryInfo failed with error: " + error);
            // TODO: Handle error.
          }
        });
    // [END native_ad_options]
  }

  // [START fetch_response]
  // Emulates a request to your ad server.
  private String fetchAdResponseString(QueryInfo queryInfo) {
    // This value is generated dynamically at runtime from your ad server.
    return "Ad response";
  }

  // [END fetch_response]

  public void renderNative(Context context, String adUnitId, String adResponseString) {
    // [START render_native]
    AdRequest adRequest = new AdRequest.Builder().setAdString(adResponseString).build();

    AdLoader adLoader =
        new AdLoader.Builder(context, adUnitId)
            .forNativeAd(
                new NativeAd.OnNativeAdLoadedListener() {
                  @Override
                  public void onNativeAdLoaded(NativeAd nativeAd) {
                    Log.d(TAG, "Native ad rendered.");
                    // TODO: Show the ad.
                  }
                })
            .build();
    adLoader.loadAd(adRequest);
    // [END render_native]
  }

  public void renderBanner(Context context, String adUnitId, String adResponseString) {
    // [START render_banner]
    AdRequest adRequest = new AdRequest.Builder().setAdString(adResponseString).build();

    AdView adView = new AdView(context);
    adView.setAdUnitId(adUnitId);
    adView.setAdSize(AdSize.BANNER);
    adView.loadAd(adRequest);
    // [END render_banner]
  }
}
