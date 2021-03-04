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
package com.google.android.gms.example.interstitialexample

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback
import kotlinx.android.synthetic.main.activity_my.*

const val GAME_LENGTH_MILLISECONDS: Long = 3000
const val AD_UNIT_ID = "/6499/example/interstitial"

/**
 * Main Activity. Inflates main activity xml.
 */
class MyActivity : AppCompatActivity() {

  private var mInterstitialAd: AdManagerInterstitialAd? = null
  private var mCountDownTimer: CountDownTimer? = null
  private var mGameIsInProgress: Boolean = false
  private var mAdIsLoading: Boolean = false
  private var mTimerMilliseconds: Long = 0
  private var TAG = "MainActivity"

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_my)

    // Initialize the Mobile Ads SDK with an empty completion listener.
    MobileAds.initialize(this) {}

    loadAd()

    // Create the "retry" button, which tries to show an interstitial between game plays.
    retry_button.visibility = View.INVISIBLE
    retry_button.setOnClickListener { showInterstitial() }

    startGame()
  }

  private fun loadAd() {
    var adRequest = AdManagerAdRequest.Builder().build()

    AdManagerInterstitialAd.load(
      this, AD_UNIT_ID, adRequest,
      object : AdManagerInterstitialAdLoadCallback() {
        override fun onAdFailedToLoad(adError: LoadAdError) {
          Log.d(TAG, adError?.message)
          mInterstitialAd = null
          mAdIsLoading = false
          val error = "domain: ${adError.domain}, code: ${adError.code}, " +
            "message: ${adError.message}"
          Toast.makeText(
            this@MyActivity,
            "onAdFailedToLoad() with error $error",
            Toast.LENGTH_SHORT
          ).show()
        }

        override fun onAdLoaded(interstitialAd: AdManagerInterstitialAd) {
          Log.d(TAG, "Ad was loaded.")
          mInterstitialAd = interstitialAd
          mAdIsLoading = false
          Toast.makeText(this@MyActivity, "onAdLoaded()", Toast.LENGTH_SHORT).show()
        }
      }
    )
  }

  private fun createTimer(milliseconds: Long) {
    // Create the game timer, which counts down to the end of the level
    // and shows the "retry" button.
    mCountDownTimer?.cancel()

    mCountDownTimer = object : CountDownTimer(milliseconds, 50) {
      override fun onTick(millisUntilFinished: Long) {
        mTimerMilliseconds = millisUntilFinished
        timer.text = getString(R.string.seconds_remaining, (millisUntilFinished / 1000 + 1))
      }

      override fun onFinish() {
        mGameIsInProgress = false
        timer.setText(R.string.done)
        retry_button.visibility = View.VISIBLE
      }
    }
  }

  public override fun onResume() {
    // Start or resume the game.
    super.onResume()

    if (mGameIsInProgress) {
      resumeGame(mTimerMilliseconds)
    }
  }

  public override fun onPause() {
    // Cancel the timer if the game is paused.
    mCountDownTimer?.cancel()
    super.onPause()
  }

  private fun showInterstitial() {
    // Show the ad if it's ready. Otherwise toast and restart the game.
    if (mInterstitialAd != null) {
      mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
        override fun onAdDismissedFullScreenContent() {
          mInterstitialAd = null
          Log.d(TAG, "Ad was dismissed.")
        }

        override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
          mInterstitialAd = null
          Log.d(TAG, "Ad failed to show.")
        }

        override fun onAdShowedFullScreenContent() {
          Log.d(TAG, "Ad showed fullscreen content.")
          startGame()
        }
      }

      mInterstitialAd?.show(this)
    } else {
      Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show()
      startGame()
    }
  }

  private fun startGame() {
    // Request a new ad if one isn't already loaded, hide the button, and kick off the timer.
    if (!mAdIsLoading && mInterstitialAd == null) {
      mAdIsLoading = true
      loadAd()
    }

    retry_button.visibility = View.INVISIBLE
    resumeGame(GAME_LENGTH_MILLISECONDS)
  }

  private fun resumeGame(milliseconds: Long) {
    // Create a new timer for the correct length and start it.
    mGameIsInProgress = true
    mTimerMilliseconds = milliseconds
    createTimer(milliseconds)
    mCountDownTimer?.start()
  }
}
