/*
 * Copyright (C) 2019 Google LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.gms.example.adaptivebannerexample;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.FrameLayout;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import java.util.Arrays;

/** Main Activity. Inflates main activity xml and child fragments. */
public class MyActivity extends AppCompatActivity {
  static final String RESERVATION_IU = "/30497360/adaptive_banner_test_iu/reservation";
  static final String BACKFILL_IU = "/30497360/adaptive_banner_test_iu/backfill";

  private FrameLayout adContainerView;
  private PublisherAdView adView;
  private boolean initialLayoutComplete = false;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_my);

    Button btn = findViewById(R.id.btn);
    btn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            loadBanner(getAdSize());
          }
        });

    // Initialize the Mobile Ads SDK.
    MobileAds.initialize(this);

    // Set your test devices. Check your logcat output for the hashed device ID to
    // get test ads on a physical device. e.g.
    // "Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
    // to get test ads on this device."
    MobileAds.setRequestConfiguration(
        new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345")).build());

    adContainerView = findViewById(R.id.ad_view_container);

    // Since we're loading the banner based on the adContainerView size, we need to wait until this
    // view is laid out before we can get the width.
    adContainerView.getViewTreeObserver().addOnGlobalLayoutListener(
        new ViewTreeObserver.OnGlobalLayoutListener() {
      @Override
      public void onGlobalLayout() {
        if (!initialLayoutComplete) {
          initialLayoutComplete = true;
          loadBanner(getAdSize());
        }
      }
    });
  }

  /** Called when leaving the activity. */
  @Override
  public void onPause() {
    if (adView != null) {
      adView.pause();
    }
    super.onPause();
  }

  /** Called when returning to the activity */
  @Override
  public void onResume() {
    super.onResume();
    if (adView != null) {
      adView.resume();
    }
  }

  /** Called before the activity is destroyed */
  @Override
  public void onDestroy() {
    if (adView != null) {
      adView.destroy();
    }
    super.onDestroy();
  }

  private void loadBanner(AdSize adSize) {
    // Create an ad request.
    adView = new PublisherAdView(this);
    adView.setAdUnitId(BACKFILL_IU);
    adView.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
    adContainerView.removeAllViews();
    adContainerView.addView(adView);
    adView.setAdSizes(adSize);

    PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();

    // Start loading the ad in the background.
    adView.loadAd(adRequest);
  }

  private AdSize getAdSize() {
    // The conversion of dp units to screen pixels is simple: px = dp * (dpi / 160).
    // https://developer.android.com/training/multiscreen/screendensities#dips-pels
    float density = getResources().getConfiguration().densityDpi / (float) DisplayMetrics.DENSITY_DEFAULT;

    int adWidth = (int) (adContainerView.getWidth() / density);

    // If the ad hasn't been laid out, default to the full screen width.
    if (adWidth == 0) {
      adWidth = getResources().getConfiguration().screenWidthDp;
    }
    return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
  }
}
