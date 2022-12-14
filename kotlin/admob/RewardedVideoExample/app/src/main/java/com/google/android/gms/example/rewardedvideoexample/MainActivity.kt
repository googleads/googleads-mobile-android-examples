package com.google.android.gms.example.rewardedvideoexample

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.android.synthetic.main.activity_main.*

const val AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
const val COUNTER_TIME = 10L
const val GAME_OVER_REWARD = 1
const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

  private var coinCount: Int = 0
  private var countdownTimer: CountDownTimer? = null
  private var gameOver = false
  private var gamePaused = false
  private var isLoading = false
  private var rewardedAd: RewardedAd? = null
  private var timeRemaining: Long = 0L

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    // Log the Mobile Ads SDK version.
    Log.d(TAG, "Google Mobile Ads SDK Version: " + MobileAds.getVersion())

    MobileAds.initialize(this) {}
    loadRewardedAd()

    // Create the "retry" button, which tries to show a rewarded video ad between game plays.
    retry_button.visibility = View.INVISIBLE
    retry_button.setOnClickListener { startGame() }

    // Create the "show" button, which shows a rewarded video if one is loaded.
    show_video_button.visibility = View.INVISIBLE
    show_video_button.setOnClickListener { showRewardedVideo() }

    // Display current coin count to user.
    coin_count_text.text = "Coins: $coinCount"

    startGame()
  }

  public override fun onPause() {
    super.onPause()
    pauseGame()
  }

  public override fun onResume() {
    super.onResume()
    if (!gameOver && gamePaused) {
      resumeGame()
    }
  }

  private fun pauseGame() {
    countdownTimer?.cancel()
    gamePaused = true
  }

  private fun resumeGame() {
    createTimer(timeRemaining)
    gamePaused = false
  }

  private fun loadRewardedAd() {
    if (rewardedAd == null) {
      isLoading = true
      var adRequest = AdRequest.Builder().build()

      RewardedAd.load(
        this,
        AD_UNIT_ID,
        adRequest,
        object : RewardedAdLoadCallback() {
          override fun onAdFailedToLoad(adError: LoadAdError) {
            Log.d(TAG, adError?.message)
            isLoading = false
            rewardedAd = null
          }

          override fun onAdLoaded(ad: RewardedAd) {
            Log.d(TAG, "Ad was loaded.")
            rewardedAd = ad
            isLoading = false
          }
        }
      )
    }
  }

  private fun addCoins(coins: Int) {
    coinCount += coins
    coin_count_text.text = "Coins: $coinCount"
  }

  private fun startGame() {
    // Hide the retry button, load the ad, and start the timer.
    retry_button.visibility = View.INVISIBLE
    show_video_button.visibility = View.INVISIBLE
    if (rewardedAd == null && !isLoading) {
      loadRewardedAd()
    }
    createTimer(COUNTER_TIME)
    gamePaused = false
    gameOver = false
  }

  // Create the game timer, which counts down to the end of the level
  // and shows the "retry" button.
  private fun createTimer(time: Long) {
    countdownTimer?.cancel()

    countdownTimer =
      object : CountDownTimer(time * 1000, 50) {
        override fun onTick(millisUnitFinished: Long) {
          timeRemaining = millisUnitFinished / 1000 + 1
          timer.text = "seconds remaining: $timeRemaining"
        }

        override fun onFinish() {
          show_video_button.visibility = View.VISIBLE
          timer.text = "The game has ended!"
          addCoins(GAME_OVER_REWARD)
          retry_button.visibility = View.VISIBLE
          gameOver = true
        }
      }

    countdownTimer?.start()
  }

  private fun showRewardedVideo() {
    show_video_button.visibility = View.INVISIBLE
    if (rewardedAd != null) {
      rewardedAd?.fullScreenContentCallback =
        object : FullScreenContentCallback() {
          override fun onAdDismissedFullScreenContent() {
            Log.d(TAG, "Ad was dismissed.")
            // Don't forget to set the ad reference to null so you
            // don't show the ad a second time.
            rewardedAd = null
            loadRewardedAd()
          }

          override fun onAdFailedToShowFullScreenContent(adError: AdError) {
            Log.d(TAG, "Ad failed to show.")
            // Don't forget to set the ad reference to null so you
            // don't show the ad a second time.
            rewardedAd = null
          }

          override fun onAdShowedFullScreenContent() {
            Log.d(TAG, "Ad showed fullscreen content.")
            // Called when ad is dismissed.
          }
        }

      rewardedAd?.show(
        this,
        OnUserEarnedRewardListener() {
          fun onUserEarnedReward(rewardItem: RewardItem) {
            var rewardAmount = rewardItem.amount
            addCoins(rewardAmount)
            Log.d("TAG", "User earned the reward.")
          }
        }
      )
    }
  }
}
