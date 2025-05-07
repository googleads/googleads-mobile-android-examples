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
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback
import com.google.android.gms.example.interstitialexample.databinding.ActivityMyBinding
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Main Activity. Inflates main activity xml. */
class MyActivity : AppCompatActivity() {

  private val isMobileAdsInitializeCalled = AtomicBoolean(false)
  private lateinit var binding: ActivityMyBinding
  private lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager
  private var interstitialAd: AdManagerInterstitialAd? = null
  private var countDownTimer: CountDownTimer? = null
  private var gamePaused: Boolean = false
  private var gameOver: Boolean = false
  private var adIsLoading: Boolean = false
  private var timerMilliseconds: Long = 0

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMyBinding.inflate(layoutInflater)
    val view = binding.root
    setContentView(view)

    // Log the Mobile Ads SDK version.
    Log.d(TAG, "Google Mobile Ads SDK Version: " + MobileAds.getVersion())

    googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(applicationContext)
    googleMobileAdsConsentManager.gatherConsent(this) { consentError ->
      if (consentError != null) {
        // Consent not obtained in current session.
        Log.w(TAG, "${consentError.errorCode}: ${consentError.message}")
      }

      startGame()

      if (googleMobileAdsConsentManager.canRequestAds) {
        initializeMobileAdsSdk()
      }
      if (googleMobileAdsConsentManager.isPrivacyOptionsRequired) {
        // Regenerate the options menu to include a privacy setting.
        invalidateOptionsMenu()
      }
    }

    // This sample attempts to load ads using consent obtained in the previous session.
    if (googleMobileAdsConsentManager.canRequestAds) {
      initializeMobileAdsSdk()
    }

    // Create the "retry" button, which tries to show an interstitial between game plays.
    binding.retryButton.visibility = View.INVISIBLE
    binding.retryButton.setOnClickListener { showInterstitial() }
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.action_menu, menu)
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val menuItemView = findViewById<View>(item.itemId)
    val activity = this
    PopupMenu(this, menuItemView).apply {
      menuInflater.inflate(R.menu.popup_menu, menu)
      menu
        .findItem(R.id.privacy_settings)
        .setVisible(googleMobileAdsConsentManager.isPrivacyOptionsRequired)
      show()
      setOnMenuItemClickListener { popupMenuItem ->
        when (popupMenuItem.itemId) {
          R.id.privacy_settings -> {
            pauseGame()
            // Handle changes to user consent.
            googleMobileAdsConsentManager.showPrivacyOptionsForm(activity) { formError ->
              if (formError != null) {
                Toast.makeText(activity, formError.message, Toast.LENGTH_SHORT).show()
              }
              resumeGame()
            }
            true
          }
          R.id.ad_inspector -> {
            MobileAds.openAdInspector(activity) { error ->
              // Error will be non-null if ad inspector closed due to an error.
              error?.let { Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show() }
            }
            true
          }
          // Handle other branches here.
          else -> false
        }
      }
      return super.onOptionsItemSelected(item)
    }
  }

  private fun loadAd() {
    // Request a new ad if one isn't already loaded.
    if (adIsLoading || interstitialAd != null) {
      return
    }
    adIsLoading = true

    // [START load_ad]
    AdManagerInterstitialAd.load(
      this,
      AD_UNIT_ID,
      AdManagerAdRequest.Builder().build(),
      object : AdManagerInterstitialAdLoadCallback() {
        override fun onAdLoaded(interstitialAd: AdManagerInterstitialAd) {
          Log.d(TAG, "Ad was loaded.")
          this@MyActivity.interstitialAd = interstitialAd
          // [START_EXCLUDE silent]
          adIsLoading = false
          Toast.makeText(this@MyActivity, "onAdLoaded()", Toast.LENGTH_SHORT).show()
          // [END_EXCLUDE]
        }

        override fun onAdFailedToLoad(adError: LoadAdError) {
          Log.d(TAG, adError.message)
          interstitialAd = null
          // [START_EXCLUDE silent]
          adIsLoading = false
          val error =
            "domain: ${adError.domain}, code: ${adError.code}, " + "message: ${adError.message}"
          Toast.makeText(
              this@MyActivity,
              "onAdFailedToLoad() with error $error",
              Toast.LENGTH_SHORT,
            )
            .show()
          // [END_EXCLUDE]
        }
      },
    )
    // [END load_ad]
  }

  private fun createTimer(milliseconds: Long) {
    // Create the game timer, which counts down to the end of the level
    // and shows the "retry" button.
    countDownTimer?.cancel()

    countDownTimer =
      object : CountDownTimer(milliseconds, 50) {
        override fun onTick(millisUntilFinished: Long) {
          timerMilliseconds = millisUntilFinished
          binding.timer.text =
            getString(R.string.seconds_remaining, (millisUntilFinished / 1000 + 1))
        }

        override fun onFinish() {
          gameOver = true
          binding.timer.setText(R.string.done)
          binding.retryButton.visibility = View.VISIBLE
        }
      }

    countDownTimer?.start()
  }

  public override fun onResume() {
    // Start or resume the game.
    super.onResume()
    resumeGame()
  }

  public override fun onPause() {
    super.onPause()
    pauseGame()
  }

  private fun showInterstitial() {
    // Show the ad if it's ready. Otherwise restart the game.
    if (interstitialAd != null) {
      // [START set_fullscreen_callback]
      interstitialAd?.fullScreenContentCallback =
        object : FullScreenContentCallback() {
          override fun onAdDismissedFullScreenContent() {
            // Called when fullscreen content is dismissed.
            Log.d(TAG, "Ad was dismissed.")
            // Don't forget to set the ad reference to null so you
            // don't show the ad a second time.
            interstitialAd = null
          }

          override fun onAdFailedToShowFullScreenContent(adError: AdError) {
            // Called when fullscreen content failed to show.
            Log.d(TAG, "Ad failed to show.")
            // Don't forget to set the ad reference to null so you
            // don't show the ad a second time.
            interstitialAd = null
          }

          override fun onAdShowedFullScreenContent() {
            // Called when fullscreen content is shown.
            Log.d(TAG, "Ad showed fullscreen content.")
          }

          override fun onAdImpression() {
            // Called when an impression is recorded for an ad.
            Log.d(TAG, "Ad recorded an impression.")
          }

          override fun onAdClicked() {
            // Called when ad is clicked.
            Log.d(TAG, "Ad was clicked.")
          }
        }
      // [END set_fullscreen_callback]

      // [START show_ad]
      interstitialAd?.show(this)
      // [END show_ad]
    } else {
      startGame()
      if (googleMobileAdsConsentManager.canRequestAds) {
        loadAd()
      }
    }
  }

  // Hide the button, and kick off the timer.
  private fun startGame() {
    binding.retryButton.visibility = View.INVISIBLE
    createTimer(GAME_LENGTH_MILLISECONDS)
    gamePaused = false
    gameOver = false
  }

  private fun resumeGame() {
    if (gameOver || !gamePaused) {
      return
    }
    // Create a new timer for the correct length.
    gamePaused = false
    createTimer(timerMilliseconds)
  }

  private fun pauseGame() {
    if (gameOver || gamePaused) {
      return
    }
    countDownTimer?.cancel()
    gamePaused = true
  }

  private fun initializeMobileAdsSdk() {
    if (isMobileAdsInitializeCalled.getAndSet(true)) {
      return
    }

    // Set your test devices.
    MobileAds.setRequestConfiguration(
      RequestConfiguration.Builder().setTestDeviceIds(listOf(TEST_DEVICE_HASHED_ID)).build()
    )

    CoroutineScope(Dispatchers.IO).launch {
      // Initialize the Google Mobile Ads SDK on a background thread.
      MobileAds.initialize(this@MyActivity) {}

      runOnUiThread {
        // Load an ad on the main thread.
        loadAd()
      }
    }
  }

  companion object {
    // This is an ad unit ID for a test ad. Replace with your own interstitial ad unit ID.
    private const val AD_UNIT_ID = "/21775744923/example/interstitial"
    private const val GAME_LENGTH_MILLISECONDS: Long = 3000
    private const val TAG = "MyActivity"

    // Check your logcat output for the test device hashed ID e.g.
    // "Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
    // to get test ads on this device" or
    // "Use new ConsentDebugSettings.Builder().addTestDeviceHashedId("ABCDEF012345") to set this as
    // a debug device".
    const val TEST_DEVICE_HASHED_ID = "ABCDEF012345"
  }
}
