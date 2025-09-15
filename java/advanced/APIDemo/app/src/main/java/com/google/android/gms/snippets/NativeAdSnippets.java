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

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.example.apidemo.databinding.NativeAdBinding;

/** Java code snippets for the developer guide. */
final class NativeAdSnippets {

  // Test ad unit IDs.
  // For more information,
  // see https://developers.google.com/admob/android/test-ads.
  // and https://developers.google.com/ad-manager/mobile-ads-sdk/android/test-ads.
  private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110";

  private void createAdLoader(Context context) {
    // [START create_ad_loader]
    // It is recommended to call AdLoader.Builder on a background thread.
    new Thread(
            () -> {
              AdLoader adLoader =
                  new AdLoader.Builder(context, AD_UNIT_ID)
                      .forNativeAd(
                          new NativeAd.OnNativeAdLoadedListener() {
                            @Override
                            // The native ad loaded successfully. You can show the ad.
                            public void onNativeAdLoaded(@NonNull NativeAd nativeAd) {}
                          })
                      .withAdListener(
                          new AdListener() {
                            @Override
                            // The native ad load failed. Check the adError message for failure
                            // reasons.
                            public void onAdFailedToLoad(@NonNull LoadAdError adError) {}
                          })
                      // Use the NativeAdOptions.Builder class to specify individual options
                      // settings.
                      .withNativeAdOptions(new NativeAdOptions.Builder().build())
                      .build();
            })
        .start();
    // [END create_ad_loader]
  }

  private void setAdLoaderListener(AdLoader.Builder adLoaderBuilder) {
    // [START set_ad_listener]
    adLoaderBuilder.withAdListener(
        // Override AdListener callbacks here.
        new AdListener() {});
    // [END set_ad_listener]
  }

  private void loadAd(AdLoader adLoader) {
    // [START load_ad]
    adLoader.loadAd(new AdRequest.Builder().build());
    // [END load_ad]
  }

  private void loadAdWithAdManagerAdRequest(AdLoader adLoader) {
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

  private void loadAdsWithAdManagerAdRequest(AdLoader adLoader) {
    // [START load_ads_ad_manager]
    // Load three native ads.
    adLoader.loadAds(new AdManagerAdRequest.Builder().build(), 3);
    // [END load_ads_ad_manager]
  }

  private void handleAdLoaded(AdLoader.Builder adLoaderBuilder) {
    // [START handle_ad_loaded]
    adLoaderBuilder
        .forNativeAd(
            nativeAd -> {
              // This callback is invoked when a native ad is successfully loaded.
            })
        .build();
    // [END handle_ad_loaded]
  }

  private void addNativeAdView(
      NativeAd nativeAd,
      Activity activity,
      LayoutInflater layoutInflater,
      FrameLayout frameLayout) {
    // [START add_ad_view]
    activity.runOnUiThread(
        () -> {
          // Inflate the native ad view and add it to the view hierarchy.
          NativeAdBinding nativeAdBinding = NativeAdBinding.inflate(layoutInflater);
          View adView = nativeAdBinding.getRoot();

          // Display and register the native ad asset views here.
          displayNativeAd(nativeAd, nativeAdBinding);

          // Remove all old ad views and add the new native.
          frameLayout.removeAllViews();
          // Add the new native ad view to the view hierarchy.
          frameLayout.addView(adView);
        });
    // [END add_ad_view]
  }

  // [START display_native_ad]
  private void displayNativeAd(NativeAd nativeAd, NativeAdBinding nativeAdBinding) {
    // [START populate_native_ad_view]
    // Populate all native ad view assets with the native ad.
    nativeAdBinding.adMedia.setMediaContent(nativeAd.getMediaContent());
    nativeAdBinding.adAdvertiser.setText(nativeAd.getAdvertiser());
    nativeAdBinding.adBody.setText(nativeAd.getBody());
    nativeAdBinding.adCallToAction.setText(nativeAd.getCallToAction());
    nativeAdBinding.adHeadline.setText(nativeAd.getHeadline());
    nativeAdBinding.adAppIcon.setImageDrawable(nativeAd.getIcon().getDrawable());
    nativeAdBinding.adPrice.setText(nativeAd.getPrice());
    Double starRating = nativeAd.getStarRating();
    if (starRating != null) {
      nativeAdBinding.adStars.setRating(starRating.floatValue());
    }
    nativeAdBinding.adStore.setText(nativeAd.getStore());
    // [END populate_native_ad_view]

    // [START hide_native_ad_view_assets]
    // Hide all native ad view assets that are not returned within the native ad.
    nativeAdBinding.adAdvertiser.setVisibility(
        (nativeAd.getAdvertiser() == null) ? View.GONE : View.VISIBLE);
    nativeAdBinding.adBody.setVisibility((nativeAd.getBody() == null) ? View.GONE : View.VISIBLE);
    nativeAdBinding.adCallToAction.setVisibility(
        (nativeAd.getCallToAction() == null) ? View.GONE : View.VISIBLE);
    nativeAdBinding.adHeadline.setVisibility(
        (nativeAd.getHeadline() == null) ? View.GONE : View.VISIBLE);
    nativeAdBinding.adAppIcon.setVisibility(
        (nativeAd.getIcon() == null) ? View.GONE : View.VISIBLE);
    nativeAdBinding.adPrice.setVisibility((nativeAd.getPrice() == null) ? View.GONE : View.VISIBLE);
    nativeAdBinding.adStars.setVisibility(
        (nativeAd.getStarRating() == null) ? View.GONE : View.VISIBLE);
    nativeAdBinding.adMedia.setVisibility(
        (nativeAd.getMediaContent() == null) ? View.GONE : View.VISIBLE);
    nativeAdBinding.adStore.setVisibility((nativeAd.getStore() == null) ? View.GONE : View.VISIBLE);
    // [END hide_native_ad_view_assets]

    // [START register_native_ad_assets]
    // Register all native ad assets with the native ad view.
    NativeAdView nativeAdView = nativeAdBinding.getRoot();
    nativeAdView.setAdvertiserView(nativeAdBinding.adAdvertiser);
    nativeAdView.setBodyView(nativeAdBinding.adBody);
    nativeAdView.setCallToActionView(nativeAdBinding.adCallToAction);
    nativeAdView.setHeadlineView(nativeAdBinding.adHeadline);
    nativeAdView.setIconView(nativeAdBinding.adAppIcon);
    nativeAdView.setPriceView(nativeAdBinding.adPrice);
    nativeAdView.setStarRatingView(nativeAdBinding.adStars);
    nativeAdView.setStoreView(nativeAdBinding.adStore);
    nativeAdView.setMediaView(nativeAdBinding.adMedia);
    // [END register_native_ad_assets]

    // [START set_native_ad]
    // This method tells the Google Mobile Ads SDK that you have finished populating your
    // native ad view with this native ad.
    nativeAdView.setNativeAd(nativeAd);
    // [END set_native_ad]
  }

  // [END display_native_ad]

  private void setEventCallback(AdLoader.Builder adLoader) {
    // [START set_event_callback]
    adLoader
        .withAdListener(
            new AdListener() {
              @Override
              public void onAdFailedToLoad(LoadAdError adError) {
                // Handle the failure.
              }

              @Override
              public void onAdClicked() {
                // Log the click event or other custom behavior.
              }
            })
        .build();
    // [END set_event_callback]
  }

  private void destroyAd(NativeAd nativeAd) {
    // [START destroy_ad]
    nativeAd.destroy();
    // [END destroy_ad]
  }

  private void setImageScaleType(MediaView mediaView) {
    // [START set_image_scale_type]
    mediaView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
    // [END set_image_scale_type]
  }
}
