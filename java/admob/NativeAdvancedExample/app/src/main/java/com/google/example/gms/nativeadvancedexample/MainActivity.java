/*
 * Copyright (C) 2026 Google, Inc.
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

package com.google.example.gms.nativeadvancedexample;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.example.gms.nativeadvancedexample.databinding.ActivityMainBinding;
import com.google.example.gms.nativeadvancedexample.databinding.AdUnifiedBinding;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

/** A simple activity class that displays native ad formats. */
@SuppressLint("SetTextI18n")
public class MainActivity extends AppCompatActivity {

  // Check your logcat output for the test device hashed ID e.g.
  // "Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
  // to get test ads on this device" or
  // "Use new ConsentDebugSettings.Builder().addTestDeviceHashedId("ABCDEF012345") to set this as
  // a debug device".
  public static final String TEST_DEVICE_HASHED_ID = "ABCDEF012345";

  private static final String ADMOB_AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110";
  private static final String TAG = "MainActivity";

  private final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);
  private GoogleMobileAdsConsentManager googleMobileAdsConsentManager;
  private ActivityMainBinding binding;
  private NativeAd nativeAd;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityMainBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());
    setSupportActionBar(binding.toolBar);

    // Log the Mobile Ads SDK version.
    Log.d(TAG, "Google Mobile Ads SDK Version: " + MobileAds.getVersion());

    googleMobileAdsConsentManager =
        GoogleMobileAdsConsentManager.getInstance(getApplicationContext());
    googleMobileAdsConsentManager.gatherConsent(
        this,
        consentError -> {
          if (consentError != null) {
            // Consent not obtained in current session.
            Log.w(
                TAG,
                String.format("%s: %s", consentError.getErrorCode(), consentError.getMessage()));
          }

          if (googleMobileAdsConsentManager.canRequestAds()) {
            binding.refreshButton.setVisibility(View.VISIBLE);
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
    binding.refreshButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View unusedView) {
            if (googleMobileAdsConsentManager.canRequestAds()) {
              refreshAd();
            }
          }
        });
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.action_menu, menu);
    MenuItem privacyItem = menu.findItem(R.id.privacy_settings);
    if (privacyItem != null) {
      privacyItem.setVisible(googleMobileAdsConsentManager.isPrivacyOptionsRequired());
    }
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId() == R.id.privacy_settings) {
      // Handle changes to user consent.
      googleMobileAdsConsentManager.showPrivacyOptionsForm(
          this,
          formError -> {
            if (formError != null) {
              Toast.makeText(this, formError.getMessage(), Toast.LENGTH_SHORT).show();
            }
          });
      return true;
    } else if (item.getItemId() == R.id.ad_inspector) {
      MobileAds.openAdInspector(
          this,
          error -> {
            // Error will be non-null if ad inspector closed due to an error.
            if (error != null) {
              Toast.makeText(this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
          });
      return true;
    }
    return super.onOptionsItemSelected(item);
  }

  /**
   * Populates a {@link NativeAdView} object with data from a given {@link NativeAd}.
   *
   * @param nativeAd the object containing the ad's assets
   * @param adBinding the binding object of the ad view
   */
  private void populateNativeAdView(NativeAd nativeAd, AdUnifiedBinding adBinding) {
    NativeAdView adView = (NativeAdView) adBinding.getRoot();

    // Set the media view.
    adView.setMediaView(adBinding.adMedia);

    // Set other ad assets.
    adView.setHeadlineView(adBinding.adHeadline);
    adView.setBodyView(adBinding.adBody);
    adView.setCallToActionView(adBinding.adCallToAction);
    adView.setIconView(adBinding.adAppIcon);
    adView.setPriceView(adBinding.adPrice);
    adView.setStarRatingView(adBinding.adStars);
    adView.setStoreView(adBinding.adStore);
    adView.setAdvertiserView(adBinding.adAdvertiser);

    // The headline and mediaContent are guaranteed to be in every NativeAd.
    adBinding.adHeadline.setText(nativeAd.getHeadline());
    adBinding.adMedia.setMediaContent(nativeAd.getMediaContent());

    // These assets aren't guaranteed to be in every NativeAd, so it's important to
    // check before trying to display them.
    if (nativeAd.getBody() == null) {
      adBinding.adBody.setVisibility(View.INVISIBLE);
    } else {
      adBinding.adBody.setVisibility(View.VISIBLE);
      adBinding.adBody.setText(nativeAd.getBody());
    }

    if (nativeAd.getCallToAction() == null) {
      adBinding.adCallToAction.setVisibility(View.INVISIBLE);
    } else {
      adBinding.adCallToAction.setVisibility(View.VISIBLE);
      adBinding.adCallToAction.setText(nativeAd.getCallToAction());
    }

    if (nativeAd.getIcon() == null) {
      adBinding.adAppIcon.setVisibility(View.GONE);
    } else {
      adBinding.adAppIcon.setImageDrawable(nativeAd.getIcon().getDrawable());
      adBinding.adAppIcon.setVisibility(View.VISIBLE);
    }

    if (nativeAd.getPrice() == null) {
      adBinding.adPrice.setVisibility(View.INVISIBLE);
    } else {
      adBinding.adPrice.setVisibility(View.VISIBLE);
      adBinding.adPrice.setText(nativeAd.getPrice());
    }

    if (nativeAd.getStore() == null) {
      adBinding.adStore.setVisibility(View.INVISIBLE);
    } else {
      adBinding.adStore.setVisibility(View.VISIBLE);
      adBinding.adStore.setText(nativeAd.getStore());
    }

    if (nativeAd.getStarRating() == null) {
      adBinding.adStars.setVisibility(View.INVISIBLE);
    } else {
      adBinding.adStars.setRating(nativeAd.getStarRating().floatValue());
      adBinding.adStars.setVisibility(View.VISIBLE);
    }

    if (nativeAd.getAdvertiser() == null) {
      adBinding.adAdvertiser.setVisibility(View.INVISIBLE);
    } else {
      adBinding.adAdvertiser.setText(nativeAd.getAdvertiser());
      adBinding.adAdvertiser.setVisibility(View.VISIBLE);
    }

    // This method tells the Google Mobile Ads SDK that you have finished populating your
    // native ad view with this native ad.
    adView.setNativeAd(nativeAd);

    // Get the video controller for the ad. One will always be provided, even if the ad doesn't
    // have a video asset.
    VideoController vc = nativeAd.getMediaContent().getVideoController();

    // Updates the UI to say whether or not this ad has a video asset.
    if (nativeAd.getMediaContent() != null && nativeAd.getMediaContent().hasVideoContent()) {

      // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
      // VideoController will call methods on this object when events occur in the video
      // lifecycle.
      vc.setVideoLifecycleCallbacks(
          new VideoController.VideoLifecycleCallbacks() {
            @Override
            public void onVideoEnd() {
              // Publishers should allow native ads to complete video playback before
              // refreshing or replacing them with another ad in the same UI location.
              binding.refreshButton.setEnabled(true);
              binding.videoStatusText.setText("Video status: Video playback has ended.");
              super.onVideoEnd();
            }
          });
    } else {
      binding.videoStatusText.setText("Video status: Ad does not contain a video asset.");
      binding.refreshButton.setEnabled(true);
    }
  }

  /**
   * Creates a request for a new native ad based on the boolean parameters and calls the
   * corresponding "populate" method when one is successfully returned.
   */
  private void refreshAd() {
    binding.refreshButton.setEnabled(false);
    binding.videoStatusText.setText("");

    boolean startMuted = binding.startMutedCheckbox.isChecked();

    // It is recommended to call AdLoader.Builder on a background thread.
    new Thread(
            () -> {
              AdLoader.Builder builder = new AdLoader.Builder(this, ADMOB_AD_UNIT_ID);

              builder.forNativeAd(
                  new NativeAd.OnNativeAdLoadedListener() {
                    // OnLoadedListener implementation.
                    @Override
                    public void onNativeAdLoaded(NativeAd nativeAd) {
                      // If this callback occurs after the activity is destroyed, you must call
                      // destroy and return or you may get a memory leak.
                      boolean isDestroyed = false;
                      binding.refreshButton.setEnabled(true);
                      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        isDestroyed = isDestroyed();
                      }
                      if (isDestroyed || isFinishing() || isChangingConfigurations()) {
                        nativeAd.destroy();
                        return;
                      }
                      // You must call destroy on old ads when you are done with them,
                      // otherwise you will have a memory leak.
                      if (MainActivity.this.nativeAd != null) {
                        MainActivity.this.nativeAd.destroy();
                      }
                      MainActivity.this.nativeAd = nativeAd;
                      AdUnifiedBinding adBinding =
                          AdUnifiedBinding.inflate(getLayoutInflater(), binding.adFrame, false);
                      populateNativeAdView(nativeAd, adBinding);
                      binding.adFrame.removeAllViews();
                      binding.adFrame.addView(adBinding.getRoot());
                    }
                  });

              VideoOptions videoOptions =
                  new VideoOptions.Builder().setStartMuted(startMuted).build();

              NativeAdOptions adOptions =
                  new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();

              builder.withNativeAdOptions(adOptions);

              AdLoader adLoader =
                  builder
                      .withAdListener(
                          new AdListener() {
                            @Override
                            public void onAdFailedToLoad(LoadAdError loadAdError) {
                              binding.refreshButton.setEnabled(true);
                              String error =
                                  String.format(
                                      Locale.getDefault(),
                                      "domain: %s, code: %d, message: %s",
                                      loadAdError.getDomain(),
                                      loadAdError.getCode(),
                                      loadAdError.getMessage());
                              Toast.makeText(
                                      MainActivity.this,
                                      "Failed to load native ad with error " + error,
                                      Toast.LENGTH_SHORT)
                                  .show();
                            }
                          })
                      .build();

              adLoader.loadAd(new AdRequest.Builder().build());
            })
        .start();
  }

  private void initializeMobileAdsSdk() {
    if (isMobileAdsInitializeCalled.getAndSet(true)) {
      return;
    }

    // Set your test devices.
    MobileAds.setRequestConfiguration(
        new RequestConfiguration.Builder()
            .setTestDeviceIds(Arrays.asList(TEST_DEVICE_HASHED_ID))
            .build());

    new Thread(
            () -> {
              // Initialize the Google Mobile Ads SDK on a background thread.
              MobileAds.initialize(this, initializationStatus -> {});

              // Load an ad on the main thread.
              runOnUiThread(() -> refreshAd());
            })
        .start();
  }

  @Override
  protected void onDestroy() {
    if (nativeAd != null) {
      nativeAd.destroy();
    }
    super.onDestroy();
  }
}
