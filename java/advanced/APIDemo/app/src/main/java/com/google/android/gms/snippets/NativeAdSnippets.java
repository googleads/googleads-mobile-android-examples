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
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;

/** Java code snippets for the developer guide. */
final class NativeAdSnippets {

  // Test ad unit IDs.
  // For more information,
  // see https://developers.google.com/admob/android/test-ads.
  // and https://developers.google.com/ad-manager/mobile-ads-sdk/android/test-ads.
  private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110";
  private static final String VIDEO_AD_UNIT_ID = "ca-app-pub-3940256099942544/1044960115";
  private static final String ADMANAGER_AD_UNIT_ID = "/21775744923/example/native";
  private static final String ADMANAGER_VIDEO_AD_UNIT_ID = "/21775744923/example/native-video";

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

  private void addNativeAdView(lLayoutInflater layoutInflater, FrameLayout frameLayout) {
    // [START add_ad_view]
    activity.runOnUiThread(
        () -> {
          // Inflate the native ad view and add it to the view hierarchy.
          NativeAdBinding nativeAdBinding = NativeAdBinding.inflate(layoutInflater);
          View adView = nativeAdBinding.getRoot();

          // Display and register the native ad asset views here.
          populateNativeAdView(nativeAd, nativeAdBinding);
          registerNativeAdAssets(nativeAd, nativeAdBinding);

          // Remove all old ad views and add the new native.
          frameLayout.removeAllViews();
          // Add the new native ad view to the view hierarchy.
          frameLayout.addView(adView);
        });
    // [END add_ad_view]
  }

  // [START register_native_ad_assets]
  private void registerNativeAdAssets(NativeAd nativeAd, NativeAdBinding nativeAdBinding) {
    // Set the native ad view elements.
    NativeAdView nativeAdView = nativeAdBinding.getRoot();
    nativeAdView.setAdvertiserView(nativeAdBinding.adAdvertiser);
    nativeAdView.setBodyView(nativeAdBinding.adBody);
    nativeAdView.setCallToActionView(nativeAdBinding.adCallToAction);
    nativeAdView.setHeadlineView(nativeAdBinding.adHeadline);
    nativeAdView.setIconView(nativeAdBinding.adAppIcon);
    nativeAdView.setPriceView(nativeAdBinding.adPrice);
    nativeAdView.setStarRatingView(nativeAdBinding.adStars);
    nativeAdView.setStoreView(nativeAdBinding.adStore);

    // Set the media view.
    nativeAdView.setMediaView(nativeAdBinding.adMedia);

    // Add other ad assets as defined by your native ad view class.

    // [START set_native_ad]
    // This method tells the Google Mobile Ads SDK that you have finished populating your
    // native ad view with this native ad.
    nativeAdView.setNativeAd(nativeAd);
    // [END set_native_ad]
  }

  // [END register_native_ad_assets]

  // [START populate_native_ad_view]
  private void populateNativeAdView(NativeAd nativeAd, NativeAdBinding nativeAdBinding) {
    // Set the view element with the native ad assets.
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
  }

  // [END populate_native_ad_view]

  private void setEventCallback(NativeAd nativeAd) {
    // [START set_event_callback]
    nativeAd.setAdEventCallback(
        new NativeAdEventCallback() {
          @Override
          public void onAdClicked() {
            Log.d(Constant.TAG, "Native ad recorded a click.");
          }
        });
    // [END set_event_callback]
  }

  private void destroyAd(NativeAd nativeAd) {
    // [START destroy_ad]
    nativeAd.destroy();
    // [END destroy_ad]
  }

  private void setImageScaleType(MediaView mediaView, ImageView.ScaleType imageScaleType) {
    // [START set_image_scale_type]
    mediaView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
    // [END set_image_scale_type]
  }
}
