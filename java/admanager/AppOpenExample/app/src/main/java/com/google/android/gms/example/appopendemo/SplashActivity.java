/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.gms.example.appopendemo;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.example.appopendemo.MyApplication.OnShowAdCompleteListener;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/** Splash Activity that inflates splash activity xml. */
public class SplashActivity extends AppCompatActivity {

  private static final String TAG = "SplashActivity";
  private final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);
  private final AtomicBoolean gatherConsentFinished = new AtomicBoolean(false);
  private GoogleMobileAdsConsentManager googleMobileAdsConsentManager;

  /**
   * Number of milliseconds to count down before showing the app open ad. This simulates the time
   * needed to load the app.
   */
  private static final long COUNTER_TIME_MILLISECONDS = 5000;

  private long secondsRemaining;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);

    // Create a timer so the SplashActivity will be displayed for a fixed amount of time.
    createTimer();

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

          gatherConsentFinished.set(true);

          if (googleMobileAdsConsentManager.canRequestAds()) {
            initializeMobileAdsSdk();
          }

          if (secondsRemaining <= 0) {
            startMainActivity();
          }
        });

    // This sample attempts to load ads using consent obtained in the previous session.
    if (googleMobileAdsConsentManager.canRequestAds()) {
      initializeMobileAdsSdk();
    }
  }

  /** Create the countdown timer, which counts down to zero and show the app open ad. */
  private void createTimer() {
    final TextView counterTextView = findViewById(R.id.timer);

    CountDownTimer countDownTimer =
        new CountDownTimer(SplashActivity.COUNTER_TIME_MILLISECONDS, 1000) {
          @SuppressLint("SetTextI18n")
          @Override
          public void onTick(long millisUntilFinished) {
            secondsRemaining = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + 1;
            counterTextView.setText("App is done loading in: " + secondsRemaining);
          }

          @SuppressLint("SetTextI18n")
          @Override
          public void onFinish() {
            secondsRemaining = 0;
            counterTextView.setText("Done.");

            Application application = getApplication();
            ((MyApplication) application)
                .showAdIfAvailable(
                    SplashActivity.this,
                    new OnShowAdCompleteListener() {
                      @Override
                      public void onShowAdComplete() {
                        // Check if the consent form is currently on screen before moving to the
                        // main activity.
                        if (gatherConsentFinished.get()) {
                          startMainActivity();
                        }
                      }
                    });
          }
        };
    countDownTimer.start();
  }

  private void initializeMobileAdsSdk() {
    if (isMobileAdsInitializeCalled.getAndSet(true)) {
      return;
    }

    // Set your test devices.
    MobileAds.setRequestConfiguration(
        new RequestConfiguration.Builder()
            .setTestDeviceIds(Arrays.asList(MyApplication.TEST_DEVICE_HASHED_ID))
            .build());

    new Thread(
            () -> {
              // Initialize the Google Mobile Ads SDK on a background thread.
              MobileAds.initialize(this, initializationStatus -> {});

              // Load an ad on the main thread.
              runOnUiThread(
                  () -> {
                    Application application = getApplication();
                    ((MyApplication) application).loadAd(this);
                  });
            })
        .start();
  }

  /** Start the MainActivity. */
  public void startMainActivity() {
    Intent intent = new Intent(this, MainActivity.class);
    this.startActivity(intent);
  }
}
