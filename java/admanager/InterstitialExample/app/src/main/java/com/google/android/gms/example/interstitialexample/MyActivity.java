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

import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;

/**
 * Main Activity. Inflates main activity xml.
 */
public class MyActivity extends AppCompatActivity {

    private static final long GAME_LENGTH_MILLISECONDS = 3000;

    private PublisherInterstitialAd interstitialAd;
    private CountDownTimer countDownTimer;
    private Button retryButton;
    private boolean gameIsInProgress;
    private boolean adIsLoading;
    private long timerMilliseconds;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        // Create the InterstitialAd and set the adUnitId.
        interstitialAd = new PublisherInterstitialAd(this);
        // Defined in res/values/strings.xml
        interstitialAd.setAdUnitId(getString(R.string.ad_unit_id));

        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                startGame();
            }

            @Override
            public void onAdLoaded() {
                adIsLoading = false;
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                adIsLoading = false;
            }
        });

        // Create the "retry" button, which tries to show an interstitial between game plays.
        retryButton = findViewById(R.id.retry_button);
        retryButton.setVisibility(View.INVISIBLE);
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInterstitial();
            }
        });

        startGame();
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
                gameIsInProgress = false;
                textView.setText("done!");
                retryButton.setVisibility(View.VISIBLE);
            }
        };
    }

    @Override
    public void onResume() {
        // Start or resume the game.
        super.onResume();

        if (gameIsInProgress) {
            resumeGame(timerMilliseconds);
        }
    }

    @Override
    public void onPause() {
        // Cancel the timer if the game is paused.
        countDownTimer.cancel();
        super.onPause();
    }

    private void showInterstitial() {
        // Show the ad if it's ready. Otherwise toast and restart the game.
        if (interstitialAd != null && interstitialAd.isLoaded()) {
            interstitialAd.show();
        } else {
            Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show();
            startGame();
        }
    }

    private void startGame() {
        // Request a new ad if one isn't already loaded, hide the button, and kick off the timer.
        if (!adIsLoading && !interstitialAd.isLoaded()) {
            adIsLoading = true;
            PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
            interstitialAd.loadAd(adRequest);
        }

        retryButton.setVisibility(View.INVISIBLE);
        resumeGame(GAME_LENGTH_MILLISECONDS);
    }

    private void resumeGame(long milliseconds) {
        // Create a new timer for the correct length and start it.
        gameIsInProgress = true;
        timerMilliseconds = milliseconds;
        createTimer(milliseconds);
        countDownTimer.start();
    }
}
