package com.google.android.gms.example.interstitialexample

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.android.synthetic.main.activity_main.*

const val GAME_LENGTH_MILLISECONDS = 3000L
const val AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"

class MainActivity : AppCompatActivity() {

  private var mInterstitialAd: InterstitialAd? = null
  private var mCountDownTimer: CountDownTimer? = null
  private var mGameIsInProgress = false
  private var mAdIsLoading: Boolean = false
  private var mTimerMilliseconds = 0L
  private var TAG = "MainActivity"

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    // Initialize the Mobile Ads SDK.
    MobileAds.initialize(this) {}

    // Set your test devices. Check your logcat output for the hashed device ID to
    // get test ads on a physical device. e.g.
    // "Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
    // to get test ads on this device."
    MobileAds.setRequestConfiguration(
      RequestConfiguration.Builder()
        .setTestDeviceIds(listOf("ABCDEF012345"))
        .build()
    )

    // Create the "retry" button, which triggers an interstitial between game plays.
    retry_button.visibility = View.INVISIBLE
    retry_button.setOnClickListener { showInterstitial() }

    // Kick off the first play of the "game."
    startGame()
  }

  private fun loadAd() {
    var adRequest = AdRequest.Builder().build()

    InterstitialAd.load(
      this, AD_UNIT_ID, adRequest,
      object : InterstitialAdLoadCallback() {
        override fun onAdFailedToLoad(adError: LoadAdError) {
          Log.d(TAG, adError?.message)
          mInterstitialAd = null
          mAdIsLoading = false
          val error = "domain: ${adError.domain}, code: ${adError.code}, " +
            "message: ${adError.message}"
          Toast.makeText(
            this@MainActivity,
            "onAdFailedToLoad() with error $error",
            Toast.LENGTH_SHORT
          ).show()
        }

        override fun onAdLoaded(interstitialAd: InterstitialAd) {
          Log.d(TAG, "Ad was loaded.")
          mInterstitialAd = interstitialAd
          mAdIsLoading = false
          Toast.makeText(this@MainActivity, "onAdLoaded()", Toast.LENGTH_SHORT).show()
        }
      }
    )
  }

  // Create the game timer, which counts down to the end of the level
  // and shows the "retry" button.
  private fun createTimer(milliseconds: Long) {
    mCountDownTimer?.cancel()

    mCountDownTimer = object : CountDownTimer(milliseconds, 50) {
      override fun onTick(millisUntilFinished: Long) {
        mTimerMilliseconds = millisUntilFinished
        timer.text = "seconds remaining: ${ millisUntilFinished / 1000 + 1 }"
      }

      override fun onFinish() {
        mGameIsInProgress = false
        timer.text = "done!"
        retry_button.visibility = View.VISIBLE
      }
    }
  }

  // Show the ad if it's ready. Otherwise toast and restart the game.
  private fun showInterstitial() {
    if (mInterstitialAd != null) {
      mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
        override fun onAdDismissedFullScreenContent() {
          Log.d(TAG, "Ad was dismissed.")
          // Don't forget to set the ad reference to null so you
          // don't show the ad a second time.
          mInterstitialAd = null
          loadAd()
        }

        override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
          Log.d(TAG, "Ad failed to show.")
          // Don't forget to set the ad reference to null so you
          // don't show the ad a second time.
          mInterstitialAd = null
        }

        override fun onAdShowedFullScreenContent() {
          Log.d(TAG, "Ad showed fullscreen content.")
          // Called when ad is dismissed.
        }
      }
      mInterstitialAd?.show(this)
    } else {
      Toast.makeText(this, "Ad wasn't loaded.", Toast.LENGTH_SHORT).show()
      startGame()
    }
  }

  // Request a new ad if one isn't already loaded, hide the button, and kick off the timer.
  private fun startGame() {
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

  // Resume the game if it's in progress.
  public override fun onResume() {
    super.onResume()

    if (mGameIsInProgress) {
      resumeGame(mTimerMilliseconds)
    }
  }

  // Cancel the timer if the game is paused.
  public override fun onPause() {
    mCountDownTimer?.cancel()
    super.onPause()
  }
}
