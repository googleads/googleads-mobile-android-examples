/*
 * Copyright (C) 2013 Google, Inc.
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
package com.google.android.gms.example.interstitialexample;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd;
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Main Activity. Inflates main activity xml.
 */
@SuppressLint("SetTextI18n")
public class MyActivity extends AppCompatActivity {

    private static final long GAME_LENGTH_MILLISECONDS = 3000;
    private static final String AD_UNIT_ID = "/6499/example/interstitial";
    private static final String TAG = "MyActivity";

    private final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);
    private GoogleMobileAdsConsentManager googleMobileAdsConsentManager;
    private AdManagerInterstitialAd interstitialAd;
    private CountDownTimer countDownTimer;
    private Button retryButton;
    private boolean gamePaused;
    private boolean gameOver;
    private boolean adIsLoading;
    private long timerMilliseconds;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

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
                        String.format(
                            "%s: %s",
                            consentError.getErrorCode(),
                            consentError.getMessage()));
                }

                startGame();

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

        // Create the "retry" button, which tries to show an interstitial between game plays.
        retryButton = findViewById(R.id.retry_button);
        retryButton.setVisibility(View.INVISIBLE);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInterstitial();
            }
        });
    }

    private void createTimer(final long milliseconds) {
        // Create the game timer, which counts down to the end of the level
        // and shows the "retry" button.
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        final TextView textView = findViewById(R.id.timer);

        countDownTimer = new CountDownTimer(milliseconds, 50) {
            @Override
            public void onTick(long millisUnitFinished) {
                timerMilliseconds = millisUnitFinished;
                textView.setText("seconds remaining: " + ((millisUnitFinished / 1000) + 1));
            }

            @Override
            public void onFinish() {
                gameOver = true;
                textView.setText("done!");
                retryButton.setVisibility(View.VISIBLE);
            }
        };

        countDownTimer.start();
    }

    @Override
    public void onResume() {
        // Start or resume the game.
        super.onResume();
        resumeGame();
    }

    @Override
    public void onPause() {
        super.onPause();
        pauseGame();
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
                    pauseGame();
                    // Handle changes to user consent.
                    googleMobileAdsConsentManager.showPrivacyOptionsForm(
                        this,
                        formError -> {
                            if (formError != null) {
                                Toast.makeText(
                                    this,
                                    formError.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            }
                            resumeGame();
                        });
                    return true;
                }
                return false;
            });
        return super.onOptionsItemSelected(item);
    }

  public void loadAd() {
    // Request a new ad if one isn't already loaded.
    if (adIsLoading || interstitialAd != null) {
      return;
    }
    adIsLoading = true;
    AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
    AdManagerInterstitialAd.load(
        this,
        AD_UNIT_ID,
        adRequest,
        new AdManagerInterstitialAdLoadCallback() {
          @Override
          public void onAdLoaded(@NonNull AdManagerInterstitialAd interstitialAd) {
            // The mInterstitialAd reference will be null until
            // an ad is loaded.
            MyActivity.this.interstitialAd = interstitialAd;
            adIsLoading = false;
            Log.i(TAG, "onAdLoaded");
            Toast.makeText(MyActivity.this, "onAdLoaded()", Toast.LENGTH_SHORT).show();
            interstitialAd.setFullScreenContentCallback(
                new FullScreenContentCallback() {
                  @Override
                  public void onAdDismissedFullScreenContent() {
                    // Called when fullscreen content is dismissed.
                    Log.d("TAG", "The ad was dismissed.");
                    // Make sure to set your reference to null so you don't
                    // show it a second time.
                    MyActivity.this.interstitialAd = null;
                  }

                  @Override
                  public void onAdFailedToShowFullScreenContent(AdError adError) {
                    // Called when fullscreen content failed to show.
                    Log.d("TAG", "The ad failed to show.");
                    // Make sure to set your reference to null so you don't
                    // show it a second time.
                    MyActivity.this.interstitialAd = null;
                  }

                  @Override
                  public void onAdShowedFullScreenContent() {
                    // Called when fullscreen content is shown.
                    Log.d("TAG", "The ad was shown.");
                  }
                });
          }

          @Override
          public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
            // Handle the error
            Log.i(TAG, loadAdError.getMessage());
            interstitialAd = null;
            adIsLoading = false;
            String error =
                String.format(
                    java.util.Locale.US,
                    "domain: %s, code: %d, message: %s",
                    loadAdError.getDomain(),
                    loadAdError.getCode(),
                    loadAdError.getMessage());
            Toast.makeText(
                    MyActivity.this, "onAdFailedToLoad() with error: " + error, Toast.LENGTH_SHORT)
                .show();
          }
        });
  }

    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise restart the game.
        if (interstitialAd != null) {
            interstitialAd.show(this);
        } else {
            startGame();
            if (googleMobileAdsConsentManager.canRequestAds()) {
                loadAd();
            }
        }
    }

    private void startGame() {
        // Hide the button, and kick off the timer.
        retryButton.setVisibility(View.INVISIBLE);
        createTimer(GAME_LENGTH_MILLISECONDS);
        gamePaused = false;
        gameOver = false;
    }

    private void resumeGame() {
        if (gameOver || !gamePaused) {
          return;
        }
        // Create a new timer for the correct length.
        gamePaused = false;
        createTimer(timerMilliseconds);
    }

    private void pauseGame() {
        if (gameOver || gamePaused) {
            return;
        }
        countDownTimer.cancel();
        gamePaused = true;
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
                  public void onInitializationComplete(InitializationStatus initializationStatus) {
                      // Load an ad.
                      loadAd();
                  }
              });
    }
}
