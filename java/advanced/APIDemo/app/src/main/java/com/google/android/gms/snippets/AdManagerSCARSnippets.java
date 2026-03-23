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
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerAdView;
import com.google.android.gms.ads.formats.OnAdManagerAdViewLoadedListener;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.query.QueryInfo;
import com.google.android.gms.ads.query.QueryInfoGenerationCallback;

/** Java code snippets for the developer guide. */
public class AdManagerSCARSnippets {

  private static final String TAG = "AdManagerSCARSnippets";

  public void loadNative(Context context, String adUnitId) {
    // [START signal_request_native]
    // Specify the "query_info_type" as "signal_type_ad_manager_s2s" to
    // denote that the usage of QueryInfo is for Ad Manager S2S.
    Bundle extras = new Bundle();
    extras.putString("query_info_type", "signal_type_ad_manager_s2s");

    // Create an ad request for signal generation.
    AdManagerAdRequest adRequest =
        new AdManagerAdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, extras).build();

    // Generate signals with the ad request.
    QueryInfo.generate(
        context,
        AdFormat.NATIVE,
        adRequest,
        adUnitId,
        new QueryInfoGenerationCallback() {
          @Override
          public void onSuccess(@NonNull final QueryInfo queryInfo) {
            Log.d(TAG, "QueryInfo string: " + queryInfo.getQuery());
            // Fetch the ad response using your generated query info.
            String adResponseString = fetchAdResponseString(queryInfo);
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
    // Specify the "query_info_type" as "signal_type_ad_manager_s2s" to
    // denote that the usage of QueryInfo is for Ad Manager S2S.
    Bundle extras = new Bundle();
    extras.putString("query_info_type", "signal_type_ad_manager_s2s");

    // Set the adaptive banner size.
    // Refer to the AdSize class for available ad sizes.
    AdSize size = AdSize.getCurrentOrientationInlineAdaptiveBannerAdSize(context, 320);
    extras.putInt("adaptive_banner_w", size.getWidth());
    extras.putInt("adaptive_banner_h", size.getHeight());

    // Create an ad request for signal generation.
    AdManagerAdRequest adRequest =
        new AdManagerAdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, extras).build();

    // Generate signals with the ad request.
    QueryInfo.generate(
        context,
        AdFormat.BANNER,
        adRequest,
        adUnitId,
        new QueryInfoGenerationCallback() {
          @Override
          public void onSuccess(@NonNull final QueryInfo queryInfo) {
            Log.d(TAG, "QueryInfo string: " + queryInfo.getQuery());
            // Fetch the ad response using your generated query info.
            String adResponseString = fetchAdResponseString(queryInfo);
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

  public void loadNativePlusBanner(Context context, String adUnitId) {
    // [START signal_request_native_plus_banner]
    // Specify the "query_info_type" as "signal_type_ad_manager_s2s" to
    // denote that the usage of QueryInfo is for Ad Manager S2S.
    Bundle extras = new Bundle();
    extras.putString("query_info_type", "signal_type_ad_manager_s2s");

    // Set the adaptive banner size.
    // Refer to the AdSize class for available ad sizes.
    AdSize size = AdSize.getCurrentOrientationInlineAdaptiveBannerAdSize(context, 320);
    extras.putInt("adaptive_banner_w", size.getWidth());
    extras.putInt("adaptive_banner_h", size.getHeight());

    // Create an ad request for signal generation.
    AdManagerAdRequest adRequest =
        new AdManagerAdRequest.Builder()
            .addNetworkExtrasBundle(AdMobAdapter.class, extras)
            .setRequestAgent("REQUEST_AGENT")
            .build();

    // Generate signals with the ad request.
    QueryInfo.generate(
        context,
        AdFormat.NATIVE,
        adRequest,
        adUnitId,
        new QueryInfoGenerationCallback() {
          @Override
          public void onSuccess(@NonNull final QueryInfo queryInfo) {
            Log.d(TAG, "QueryInfo string: " + queryInfo.getQuery());
            // Fetch the ad response using your generated query info.
            String adResponseString = fetchAdResponseString(queryInfo);
            renderNativePlusBanner(context, adUnitId, adResponseString);
          }

          @Override
          public void onFailure(@NonNull String error) {
            Log.e(TAG, "QueryInfo failed with error: " + error);
            // TODO: Handle error.
          }
        });
    // [END signal_request_native_plus_banner]
  }

  public void loadNativeWithOptions(Context context, String adUnitId) {
    // [START native_ad_options]
    Bundle extras = new Bundle();
    // Specify the "query_info_type" as "signal_type_ad_manager_s2s" to
    // denote that the usage of QueryInfo is for Ad Manager S2S.
    extras.putString("query_info_type", "signal_type_ad_manager_s2s");
    // Provide native media aspect ratio through extras bundle.
    extras.putInt(
        "gad_native_media_aspect_ratio", NativeAdOptions.NATIVE_MEDIA_ASPECT_RATIO_LANDSCAPE);

    // Create an ad request for signal generation.
    AdManagerAdRequest adRequest =
        new AdManagerAdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, extras).build();

    // Generate signals with the ad request.
    QueryInfo.generate(
        context,
        AdFormat.NATIVE,
        adRequest,
        adUnitId,
        new QueryInfoGenerationCallback() {
          @Override
          public void onSuccess(@NonNull final QueryInfo queryInfo) {
            Log.d(TAG, "QueryInfo string: " + queryInfo.getQuery());
            // Fetch the ad response using your generated query info.
            String adResponseString = fetchAdResponseString(queryInfo);
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
    AdManagerAdRequest adRequest =
        new AdManagerAdRequest.Builder().setAdString(adResponseString).build();

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
    AdManagerAdRequest adRequest =
        new AdManagerAdRequest.Builder().setAdString(adResponseString).build();

    AdManagerAdView adView = new AdManagerAdView(context);
    adView.setAdUnitId(adUnitId);
    adView.setAdSizes(AdSize.BANNER);
    adView.loadAd(adRequest);
    // [END render_banner]
  }

  public void renderNativePlusBanner(Context context, String adUnitId, String adResponseString) {
    // [START render_native_plus_banner]
    AdManagerAdRequest adRequest =
        new AdManagerAdRequest.Builder().setAdString(adResponseString).build();

    AdLoader adLoader =
        new AdLoader.Builder(context, adUnitId)
            .forNativeAd(
                new NativeAd.OnNativeAdLoadedListener() {
                  @Override
                  public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                    Log.d(TAG, "Native ad rendered.");
                    // TODO: Show the ad.
                  }
                })
            .forAdManagerAdView(
                new OnAdManagerAdViewLoadedListener() {
                  @Override
                  public void onAdManagerAdViewLoaded(@NonNull AdManagerAdView adManagerAdView) {
                    Log.d(TAG, "Banner ad rendered.");
                    // TODO: Show the ad.
                  }
                },
                AdSize.BANNER)
            .build();
    adLoader.loadAd(adRequest);
    // [END render_native_plus_banner]
  }
}
