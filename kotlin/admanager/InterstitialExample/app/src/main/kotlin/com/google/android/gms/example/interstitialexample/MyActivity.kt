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
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.doubleclick.PublisherAdRequest
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd
import kotlinx.android.synthetic.main.activity_my.*

const val GAME_LENGTH_MILLISECONDS: Long = 3000
const val AD_UNIT_ID = "/6499/example/interstitial"

/**
 * Main Activity. Inflates main activity xml.
 */
class MyActivity : AppCompatActivity() {

    private lateinit var mInterstitialAd: PublisherInterstitialAd
    private var mCountDownTimer: CountDownTimer? = null
    private var mGameIsInProgress: Boolean = false
    private var mAdIsLoading: Boolean = false
    private var mTimerMilliseconds: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my)

        // Create the InterstitialAd and set the adUnitId.
        mInterstitialAd = PublisherInterstitialAd(this)
        // Replace with your own ad unit id.
        mInterstitialAd.adUnitId = AD_UNIT_ID

        mInterstitialAd.adListener = object : AdListener() {
            override fun onAdClosed() {
                startGame()
            }

            override fun onAdLoaded() {
                mAdIsLoading = false
                Toast.makeText(this@MyActivity, "onAdLoaded()", Toast.LENGTH_SHORT).show()
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                mAdIsLoading = false
                Toast.makeText(
                    this@MyActivity,
                    "onAdFailedToLoad() with error code: $errorCode",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Create the "retry" button, which tries to show an interstitial between game plays.
        retry_button.visibility = View.INVISIBLE
        retry_button.setOnClickListener { showInterstitial() }

        startGame()
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
        if (mInterstitialAd.isLoaded) {
            mInterstitialAd.show()
        } else {
            Toast.makeText(this, "Ad did not load", Toast.LENGTH_SHORT).show()
            startGame()
        }
    }

    private fun startGame() {
        // Request a new ad if one isn't already loaded, hide the button, and kick off the timer.
        if (!mAdIsLoading && !mInterstitialAd.isLoaded) {
            mAdIsLoading = true
            val adRequest = PublisherAdRequest.Builder().build()
            mInterstitialAd.loadAd(adRequest)
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
