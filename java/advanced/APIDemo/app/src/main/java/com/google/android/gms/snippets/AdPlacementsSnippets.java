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

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;

/** Java code snippets for the developer guide. */
public class AdPlacementsSnippets {

  private static final long PLACEMENT_ID = 2500718471L;
  private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712";
  private static final String TAG = "AdPlacementsSnippets";

  // [START load_interstitial]
  public void loadInterstitial(Context context) {
    AdRequest adRequest = new AdRequest.Builder().setPlacementId(PLACEMENT_ID).build();
    InterstitialAd.load(
        context,
        AD_UNIT_ID,
        adRequest,
        new InterstitialAdLoadCallback() {
          @Override
          public void onAdLoaded(@NonNull InterstitialAd ad) {
            Log.d(TAG, "Placement ID: " + ad.getPlacementId());
          }
        });
  }

  // [END load_interstitial]

  // [START show_interstitial]
  private void showInterstitial(Activity activity, InterstitialAd ad) {
    ad.setPlacementId(PLACEMENT_ID);
    ad.show(activity);
  }

  // [END show_interstitial]

  // [START show_banner]
  public void showBannerView(AdView adView, ViewGroup parentView) {
    adView.setPlacementId(PLACEMENT_ID);
    parentView.addView(adView);
  }

  // [END show_banner]

  // [START show_native]
  private void showNativeAd(NativeAdView nativeAdView, NativeAd nativeAd) {
    nativeAd.setPlacementId(PLACEMENT_ID);
    nativeAdView.setNativeAd(nativeAd);
  }

  // [END show_native]
}
