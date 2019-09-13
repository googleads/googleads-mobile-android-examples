package com.google.android.gms.example.rewardedvideoexample

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.doubleclick.PublisherAdRequest
import com.google.android.gms.ads.rewarded.RewardItem
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdCallback
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import kotlinx.android.synthetic.main.activity_main.*

const val AD_UNIT_ID = "/6499/example/rewarded-video"
const val COUNTER_TIME = 10L
const val GAME_OVER_REWARD = 1

class MainActivity : AppCompatActivity() {

  private var mCoinCount: Int = 0
  private var mCountDownTimer: CountDownTimer? = null
  private var mGameOver = false
  private var mGamePaused = false
  private var mIsLoading = false
  private lateinit var mRewardedAd: RewardedAd
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
    if (!(::mRewardedAd.isInitialized) || !mRewardedAd.isLoaded) {
      mIsLoading = true
      mRewardedAd = RewardedAd(this, AD_UNIT_ID)
      mRewardedAd.loadAd(
        PublisherAdRequest.Builder().build(),
        object : RewardedAdLoadCallback() {
          override fun onRewardedAdLoaded() {
            mIsLoading = false
            Toast.makeText(this@MainActivity, "onRewardedAdLoaded", Toast.LENGTH_LONG).show()
          }

          override fun onRewardedAdFailedToLoad(errorCode: Int) {
            mIsLoading = false
            Toast.makeText(this@MainActivity, "onRewardedAdFailedToLoad", Toast.LENGTH_LONG).show()
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
    if (!mRewardedAd.isLoaded && !mIsLoading) {
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
        if (mRewardedAd.isLoaded) {
          show_video_button.visibility = View.VISIBLE
        }
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
    if (mRewardedAd.isLoaded) {
      mRewardedAd.show(
        this,
        object : RewardedAdCallback() {
          override fun onUserEarnedReward(rewardItem: RewardItem) {
            Toast.makeText(this@MainActivity, "onUserEarnedReward", Toast.LENGTH_LONG).show()
            addCoins(rewardItem.amount)
          }

          override fun onRewardedAdClosed() {
            Toast.makeText(this@MainActivity, "onRewardedAdClosed", Toast.LENGTH_LONG).show()
            loadRewardedAd()
          }

          override fun onRewardedAdFailedToShow(errorCode: Int) {
            Toast.makeText(this@MainActivity, "onRewardedAdFailedToShow", Toast.LENGTH_LONG).show()
          }

          override fun onRewardedAdOpened() {
            Toast.makeText(this@MainActivity, "onRewardedAdOpened", Toast.LENGTH_LONG).show()
          }
        }
      )
    }
  }
}
