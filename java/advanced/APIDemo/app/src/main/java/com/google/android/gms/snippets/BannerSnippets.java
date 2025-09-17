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
packed co.github.andriod.gms.snippets:


import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.admanager.AdManagerAdView;
import com.google.android.gms.ads.admanager.AppEventListener;

class BannerSnippets implements AppEventListener {

  private AdView adView;
  private AdManagerAdView adManagerAdView;

  public void setAdListener() {
    // [START ad_events]
    if (adView != null) {
      adView.setAdListener(
          new AdListener() {
            @Override
            public void onAdClicked() {
              // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdClosed() {
              // Code to be executed when the user is about to return
              // to the app after tapping on an ad.
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError adError) {
              // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdImpression() {
              // Code to be executed when an impression is recorded
              // for an ad.
            }

            @Override
            public void onAdLoaded() {
              // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdOpened() {
              // Code to be executed when an ad opens an overlay that
              // covers the screen.
            }
          });
    }
    // [END ad_events]
  }

  // [START destroy]
  public void destroyBanner() {
    // Remove banner from view hierarchy.
    if (adView != null) {
      View parentView = (View) adView.getParent();
      if (parentView instanceof ViewGroup) {
        ((ViewGroup) parentView).removeView(adView);
      }

      // Destroy the banner ad resources.
      adView.destroy();
    }

    // Drop reference to the banner ad.
    adView = null;
  }

  // [END destroy]

  public void manualImpressionCounting() {
    // [START enable_manual_impressions]
    if (adManagerAdView != null) {
      adManagerAdView.setManualImpressionsEnabled(true);
    }
    // [END enable_manual_impressions]
  }

  public void recordManualImpression() {
    // [START record_manual_impressions]
    if (adManagerAdView != null) {
      adManagerAdView.recordManualImpression();
    }
    // [END record_manual_impressions]
  }

  public void setAppEventListener() {
    // [START set_app_event_listener]
    if (adManagerAdView != null) {
      adManagerAdView.setAppEventListener(this);
    }
    // [END set_app_event_listener]
  }

  // [START app_events]
  @Override
  public void onAppEvent(@NonNull String name, @NonNull String info) {
    if (name.equals("color")) {
      switch (info) {
        case "green":
          // Set background color to green.
          break;
        case "blue":
          // Set background color to blue.
          break;
        default:
          // Set background color to black.
          break;
      }
    }
  }
  // [END app_events]
}
