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

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import com.google.android.gms.example.appopendemo.MyApplication.OnShowAdCompleteListener;

/** Splash Activity that inflates splash activity xml. */
public class SplashActivity extends AppCompatActivity {
  private static final String LOG_TAG = "SplashActivity";

  /**
   * Number of seconds to count down before showing the app open ad. This simulates the time needed
   * to load the app.
   */
  private static final long COUNTER_TIME = 5;

  private long secondsRemaining;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_splash);

    // Create a timer so the SplashActivity will be displayed for a fixed amount of time.
    createTimer(COUNTER_TIME);
  }

  /**
   * Create the countdown timer, which counts down to zero and show the app open ad.
   *
   * @param seconds the number of seconds that the timer counts down from
   */
  private void createTimer(long seconds) {
    final TextView counterTextView = findViewById(R.id.timer);

    CountDownTimer countDownTimer =
        new CountDownTimer(seconds * 1000, 1000) {
          @Override
          public void onTick(long millisUntilFinished) {
            secondsRemaining = ((millisUntilFinished / 1000) + 1);
            counterTextView.setText("App is done loading in: " + secondsRemaining);
          }

          @Override
          public void onFinish() {
            secondsRemaining = 0;
            counterTextView.setText("Done.");

            Application application = getApplication();

            // If the application is not an instance of MyApplication, log an error message and
            // start the MainActivity without showing the app open ad.
            if (!(application instanceof MyApplication)) {
              Log.e(LOG_TAG, "Failed to cast application to MyApplication.");
              startMainActivity();
              return;
            }

            // Show the app open ad.
            ((MyApplication) application)
                .showAdIfAvailable(
                    SplashActivity.this,
                    new OnShowAdCompleteListener() {
                      @Override
                      public void onShowAdComplete() {
                        startMainActivity();
                      }
                    });
          }
        };
    countDownTimer.start();
  }

  /** Start the MainActivity. */
  public void startMainActivity() {
    Intent intent = new Intent(this, MainActivity.class);
    this.startActivity(intent);
  }
}
