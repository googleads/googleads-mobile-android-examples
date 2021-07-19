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

  private var mCoinCount: Int = 0
  private var mCountDownTimer: CountDownTimer? = null
  private var mGameOver = false
  private var mGamePaused = false
  private var mIsLoading = false
  private var mRewardedAd: RewardedAd? = null
  private var mTimeRemaining: Long = 0L

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    MobileAds.initialize(this) {}
    loadRewardedAd()

    // Create the "retry" button, which tries to show a rewarded video ad between game plays.
    retry_button.visibility = View.INVISIBLE
    retry_button.setOnClickListener { startGame() }

    // Create the "show" button, which shows a rewarded video if one is loaded.
    show_video_button.visibility = View.INVISIBLE
    show_video_button.setOnClickListener { showRewardedVideo() }

    // Display current coin count to user.
    coin_count_text.text = "Coins: $mCoinCount"

    startGame()
  }

  public override fun onPause() {
    super.onPause()
    pauseGame()
  }

  public override fun onResume() {
    super.onResume()
    if (!mGameOver && mGamePaused) {
      resumeGame()
    }
  }

  private fun pauseGame() {
    mCountDownTimer?.cancel()
    mGamePaused = true
  }

  private fun resumeGame() {
    createTimer(mTimeRemaining)
    mGamePaused = false
  }

  private fun loadRewardedAd() {
    if (mRewardedAd == null) {
      mIsLoading = true
      var adRequest = AdRequest.Builder().build()

      RewardedAd.load(
        this, AD_UNIT_ID, adRequest,
        object : RewardedAdLoadCallback() {
          override fun onAdFailedToLoad(adError: LoadAdError) {
            Log.d(TAG, adError?.message)
            mIsLoading = false
            mRewardedAd = null
          }

          override fun onAdLoaded(rewardedAd: RewardedAd) {
            Log.d(TAG, "Ad was loaded.")
            mRewardedAd = rewardedAd
            mIsLoading = false
          }
        }
      )
    }
  }

  private fun addCoins(coins: Int) {
    mCoinCount += coins
    coin_count_text.text = "Coins: $mCoinCount"
  }

  private fun startGame() {
    // Hide the retry button, load the ad, and start the timer.
    retry_button.visibility = View.INVISIBLE
    show_video_button.visibility = View.INVISIBLE
    if (mRewardedAd == null && !mIsLoading) {
      loadRewardedAd()
    }
    createTimer(COUNTER_TIME)
    mGamePaused = false
    mGameOver = false
  }

  // Create the game timer, which counts down to the end of the level
  // and shows the "retry" button.
  private fun createTimer(time: Long) {
    mCountDownTimer?.cancel()

    mCountDownTimer = object : CountDownTimer(time * 1000, 50) {
      override fun onTick(millisUnitFinished: Long) {
        mTimeRemaining = millisUnitFinished / 1000 + 1
        timer.text = "seconds remaining: $mTimeRemaining"
      }

      override fun onFinish() {
        show_video_button.visibility = View.VISIBLE
        timer.text = "The game has ended!"
        addCoins(GAME_OVER_REWARD)
        retry_button.visibility = View.VISIBLE
        mGameOver = true
      }
    }

    mCountDownTimer?.start()
  }

  private fun showRewardedVideo() {
    show_video_button.visibility = View.INVISIBLE
    if (mRewardedAd != null) {
      mRewardedAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
        override fun onAdDismissedFullScreenContent() {
          Log.d(TAG, "Ad was dismissed.")
          // Don't forget to set the ad reference to null so you
          // don't show the ad a second time.
          mRewardedAd = null
          loadRewardedAd()
        }

        override fun onAdFailedToShowFullScreenContent(adError: AdError?) {
          Log.d(TAG, "Ad failed to show.")
          // Don't forget to set the ad reference to null so you
          // don't show the ad a second time.
          mRewardedAd = null
        }

        override fun onAdShowedFullScreenContent() {
          Log.d(TAG, "Ad showed fullscreen content.")
          // Called when ad is dismissed.
        }
      }

      mRewardedAd?.show(
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
