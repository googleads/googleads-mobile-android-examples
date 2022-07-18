package com.google.android.gms.example.rewardedinterstitialexample

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
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import kotlinx.android.synthetic.main.activity_main.coin_count_text
import kotlinx.android.synthetic.main.activity_main.retry_button
import kotlinx.android.synthetic.main.activity_main.timer

private const val AD_UNIT_ID = "ca-app-pub-3940256099942544/5354046379"
private const val GAME_COUNTER_TIME = 10L
private const val GAME_OVER_REWARD = 1
private const val MAIN_ACTIVITY_TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

  private var coinCount: Int = 0
  private var countDownTimer: CountDownTimer? = null
  private var gameOver = false
  private var gamePaused = false
  private var isLoadingAds = false
  private var rewardedInterstitialAd: RewardedInterstitialAd? = null
  private var timeRemaining: Long = 0L

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    MobileAds.initialize(this) { initializationStatus -> loadRewardedInterstitialAd() }

    // Create the "retry" button, which tries to show a rewarded video ad between game plays.
    retry_button.visibility = View.INVISIBLE
    retry_button.setOnClickListener { startGame() }

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
    countDownTimer?.cancel()
    gamePaused = true
  }

  private fun resumeGame() {
    createTimer(timeRemaining)
    gamePaused = false
  }

  private fun loadRewardedInterstitialAd() {
    if (rewardedInterstitialAd == null) {
      isLoadingAds = true
      val adRequest = AdRequest.Builder().build()

      // Load an ad.
      RewardedInterstitialAd.load(
        this,
        AD_UNIT_ID,
        adRequest,
        object : RewardedInterstitialAdLoadCallback() {
          override fun onAdFailedToLoad(adError: LoadAdError) {
            super.onAdFailedToLoad(adError)
            Log.d(MAIN_ACTIVITY_TAG, "onAdFailedToLoad: ${adError.message}")
            isLoadingAds = false
            rewardedInterstitialAd = null
          }

          override fun onAdLoaded(rewardedAd: RewardedInterstitialAd) {
            super.onAdLoaded(rewardedAd)
            Log.d(MAIN_ACTIVITY_TAG, "Ad was loaded.")

            rewardedInterstitialAd = rewardedAd
            isLoadingAds = false
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
    if (rewardedInterstitialAd == null && !isLoadingAds) {
      loadRewardedInterstitialAd()
    }
    createTimer(GAME_COUNTER_TIME)
    gamePaused = false
    gameOver = false
  }

  // Create the game timer, which counts down to the end of the level
  // and shows the "retry" button.
  private fun createTimer(time: Long) {
    countDownTimer?.cancel()

    countDownTimer =
      object : CountDownTimer(time * 1000, 50) {
        override fun onTick(millisUnitFinished: Long) {
          timeRemaining = millisUnitFinished / 1000 + 1
          timer.text = "seconds remaining: $timeRemaining"
        }

        override fun onFinish() {
          timer.text = "The game has ended!"
          addCoins(GAME_OVER_REWARD)
          retry_button.visibility = View.VISIBLE
          gameOver = true

          if (rewardedInterstitialAd == null) {
            Log.d(
              MAIN_ACTIVITY_TAG,
              "The game is over but the rewarded interstitial ad wasn't ready yet."
            )
            return
          }

          Log.d(MAIN_ACTIVITY_TAG, "The rewarded interstitial ad is ready.")
          val rewardAmount = rewardedInterstitialAd!!.rewardItem.amount
          val rewardType = rewardedInterstitialAd!!.rewardItem.type
          introduceVideoAd(rewardAmount, rewardType)
        }
      }

    countDownTimer?.start()
  }

  private fun introduceVideoAd(rewardAmount: Int, rewardType: String) {
    val dialog = AdDialogFragment.newInstance(rewardAmount, rewardType)
    dialog.setAdDialogInteractionListener(
      object : AdDialogFragment.AdDialogInteractionListener {
        override fun onShowAd() {
          Log.d(MAIN_ACTIVITY_TAG, "The rewarded interstitial ad is starting.")
          showRewardedVideo()
        }

        override fun onCancelAd() {
          Log.d(MAIN_ACTIVITY_TAG, "The rewarded interstitial ad was skipped before it starts.")
        }
      }
    )
    dialog.show(supportFragmentManager, "AdDialogFragment")
  }

  private fun showRewardedVideo() {
    if (rewardedInterstitialAd == null) {
      Log.d(MAIN_ACTIVITY_TAG, "The rewarded interstitial ad wasn't ready yet.")
      return
    }

    rewardedInterstitialAd!!.fullScreenContentCallback =
      object : FullScreenContentCallback() {
        override fun onAdDismissedFullScreenContent() {
          Log.d(MAIN_ACTIVITY_TAG, "Ad was dismissed.")

          // Don't forget to set the ad reference to null so you
          // don't show the ad a second time.
          rewardedInterstitialAd = null

          // Preload the next rewarded interstitial ad.
          loadRewardedInterstitialAd()
        }

        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
          Log.d(MAIN_ACTIVITY_TAG, "Ad failed to show.")

          // Don't forget to set the ad reference to null so you
          // don't show the ad a second time.
          rewardedInterstitialAd = null
        }

        override fun onAdShowedFullScreenContent() {
          Log.d(MAIN_ACTIVITY_TAG, "Ad showed fullscreen content.")
        }
      }

    rewardedInterstitialAd?.show(this) { rewardItem ->
      addCoins(rewardItem.amount)
      Log.d("TAG", "User earned the reward.")
    }
  }
}
