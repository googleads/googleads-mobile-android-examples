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
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.PopupMenu;
import android.widget.Toast;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerAdView;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

/** Main Activity. Inflates main activity xml and child fragments. */
public class MyActivity extends AppCompatActivity {
  static final String RESERVATION_IU = "/30497360/adaptive_banner_test_iu/reservation";
  static final String BACKFILL_IU = "/30497360/adaptive_banner_test_iu/backfill";
  private final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);
  private GoogleMobileAdsConsentManager googleMobileAdsConsentManager;
  private FrameLayout adContainerView;
  private AdManagerAdView adView;
  private final AtomicBoolean initialLayoutComplete = new AtomicBoolean(false);
  private static final String TAG = "MyActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_my);
    adContainerView = findViewById(R.id.ad_view_container);

    Button btn = findViewById(R.id.btn);
    btn.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            if (googleMobileAdsConsentManager.canRequestAds()) {
              loadBanner(getAdSize());
            }
          }
        });

    // Log the Mobile Ads SDK version.
    Log.d(TAG, "Google Mobile Ads SDK Version: " + MobileAds.getVersion());

    googleMobileAdsConsentManager = new GoogleMobileAdsConsentManager(this);
    googleMobileAdsConsentManager.gatherConsent(
        consentError -> {
          if (consentError != null) {
            // Consent not obtained in current session.
            Log.w(
                TAG,
                String.format("%s: %s", consentError.getErrorCode(), consentError.getMessage()));
          }

          if (googleMobileAdsConsentManager.canRequestAds()) {
            initializeMobileAdsSdk();
          }

          if (googleMobileAdsConsentManager.isPrivacyOptionsRequired()) {
            // Regenerate the options menu to include a privacy setting.
            invalidateOptionsMenu();
          }
        });

    // This sample attempts to load ads using consent obtained in the previous session.
    if (googleMobileAdsConsentManager.canRequestAds()) {
      initializeMobileAdsSdk();
    }

    // Since we're loading the banner based on the adContainerView size, we need to wait until this
    // view is laid out before we can get the width.
    adContainerView
        .getViewTreeObserver()
        .addOnGlobalLayoutListener(
            () -> {
              if (!initialLayoutComplete.getAndSet(true)
                  && googleMobileAdsConsentManager.canRequestAds()) {
                loadBanner(getAdSize());
              }
            });

    // Set your test devices. Check your logcat output for the hashed device ID to
    // get test ads on a physical device. e.g.
    // "Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
    // to get test ads on this device."
    MobileAds.setRequestConfiguration(
        new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345")).build());
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

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.action_menu, menu);
    MenuItem moreMenu = menu.findItem(R.id.action_more);
    moreMenu.setVisible(googleMobileAdsConsentManager.isPrivacyOptionsRequired());
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    View menuItemView = findViewById(item.getItemId());
    PopupMenu popup = new PopupMenu(this, menuItemView);
    popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
    popup.show();
    popup.setOnMenuItemClickListener(
        popupMenuItem -> {
          if (popupMenuItem.getItemId() == R.id.privacy_settings) {
            // Handle changes to user consent.
            googleMobileAdsConsentManager.showPrivacyOptionsForm(
                this,
                formError -> {
                  if (formError != null) {
                    Toast.makeText(this, formError.getMessage(), Toast.LENGTH_SHORT).show();
                  }
                });
            return true;
          }
          return false;
        });
    return super.onOptionsItemSelected(item);
  }

  private void loadBanner(AdSize adSize) {
    // Create an ad request.
    adView = new AdManagerAdView(this);
    adView.setAdUnitId(BACKFILL_IU);
    adView.setBackgroundColor(getResources().getColor(android.R.color.holo_blue_light));
    adContainerView.removeAllViews();
    adContainerView.addView(adView);
    adView.setAdSizes(adSize);

    AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();

    // Start loading the ad in the background.
    adView.loadAd(adRequest);
  }

  private void initializeMobileAdsSdk() {
    if (isMobileAdsInitializeCalled.getAndSet(true)) {
      return;
    }

    // Initialize the Mobile Ads SDK.
    MobileAds.initialize(
        this,
        new OnInitializationCompleteListener() {
          @Override
          public void onInitializationComplete(InitializationStatus status) {}
        });

    // Load an ad.
    if (initialLayoutComplete.get()) {
      loadBanner(getAdSize());
    }
  }

  private AdSize getAdSize() {
    // Determine the screen width (less decorations) to use for the ad width.
    Display display = getWindowManager().getDefaultDisplay();
    DisplayMetrics outMetrics = new DisplayMetrics();
    display.getMetrics(outMetrics);

    float density = outMetrics.density;

    float adWidthPixels = adContainerView.getWidth();

    // If the ad width isn't known, default to the full screen width.
    if (adWidthPixels == 0) {
      adWidthPixels = outMetrics.widthPixels;
    }

    int adWidth = (int) (adWidthPixels / density);
    return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, adWidth);
  }
}
