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
import android.widget.FrameLayout;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import java.util.Arrays;

/** Main Activity. Inflates main activity xml and child fragments. */
public class MyActivity extends AppCompatActivity {

  // This is an ad unit ID for a test ad. Replace with your own banner ad unit ID.
  private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/9214589741";
  private FrameLayout adContainerView;
  private AdView adView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_my);

    // Initialize the Mobile Ads SDK.
    MobileAds.initialize(this, new OnInitializationCompleteListener() {
      @Override
      public void onInitializationComplete(InitializationStatus initializationStatus) {}
    });

    // Set your test devices. Check your logcat output for the hashed device ID to
    // get test ads on a physical device. e.g.
    // "Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
    // to get test ads on this device."
    MobileAds.setRequestConfiguration(
        new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345")).build());

    adContainerView = findViewById(R.id.ad_view_container);

    // Since we're loading the banner based on the adContainerView size, we need to wait until this
    // view is laid out before we can get the width.
    adContainerView.post(new Runnable() {
      @Override
      public void run() {
       loadBanner();
      }
    });
  }

  /** Called when leaving the activity */
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

  private void loadBanner() {
    // Create an ad request.
    adView = new AdView(this);
    adView.setAdUnitId(AD_UNIT_ID);
    adContainerView.removeAllViews();
    adContainerView.addView(adView);

    AdSize adSize = getAdSize();
    adView.setAdSize(adSize);

    AdRequest adRequest = new AdRequest.Builder().build();

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
