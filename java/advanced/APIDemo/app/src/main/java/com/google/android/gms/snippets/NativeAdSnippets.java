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
import androidx.annotation.NonNull;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;

/** Java code snippets for the developer guide. */
final class NativeAdSnippets {

  private static final String AD_UNIT_ID = "/21775744923/example/native";

  private void createAdLoader(Context context) {
    // [START create_ad_loader]
    AdLoader adLoader =
        new AdLoader.Builder(context, AD_UNIT_ID)
            .forNativeAd(
                new NativeAd.OnNativeAdLoadedListener() {
                  @Override
                  public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {
                    // The native ad loaded successfully. You can show the ad.
                  }
                })
            .withAdListener(
                new AdListener() {
                  @Override
                  public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                    // The native ad load failed. Check the adError message for failure reasons.
                  }
                })
            // Use the NativeAdOptions.Builder class to specify individual options settings.
            .withNativeAdOptions(new NativeAdOptions.Builder().build())
            .build();
    // [END create_ad_loader]
  }

  private void setAdLoaderListener(AdLoader.Builder adLoaderBuilder) {
    // [START set_ad_listener]
    adLoaderBuilder.withAdListener(
        new AdListener() {
          // Set the AdListener to listen to ad events.
        });
    // [END set_ad_listener]
  }

  private void loadAd(AdLoader adLoader) {
    // [START load_ad]
    adLoader.loadAd(new AdRequest.Builder().build());
    // [END load_ad]
  }

  private void loadAdAdManager(AdLoader adLoader) {
    // [START load_ad_ad_manager]
    adLoader.loadAd(new AdManagerAdRequest.Builder().build());
    // [END load_ad_ad_manager]
  }

  private void loadAds(AdLoader adLoader) {
    // [START load_ads]
    // Load three native ads.
    adLoader.loadAds(new AdRequest.Builder().build(), 3);
    // [END load_ads]
  }

  private void handleAdLoaded(AdLoader.Builder adLoaderBuilder) {
    // [START handle_ad_loaded]
    adLoaderBuilder
        .forNativeAd(
            nativeAd -> {
              // This callback will be invoked when a native ad is successfully loaded.
            })
        .build();
    // [END handle_ad_loaded]
  }

  private void destroyAd(NativeAd nativeAd) {
    // [START destroy_ad]
    nativeAd.destroy();
    // [END destroy_ad]
  }
}
