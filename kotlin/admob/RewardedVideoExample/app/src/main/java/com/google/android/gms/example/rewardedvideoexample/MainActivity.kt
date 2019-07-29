package com.google.android.gms.example.rewardedvideoexample

import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.reward.RewardItem
import com.google.android.gms.ads.reward.RewardedVideoAd
import com.google.android.gms.ads.reward.RewardedVideoAdListener
import kotlinx.android.synthetic.main.activity_main.*

const val AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
const val COUNTER_TIME = 10L
const val GAME_OVER_REWARD = 1

class MainActivity : AppCompatActivity(), RewardedVideoAdListener {

    private var mCoinCount: Int = 0
    private var mCountDownTimer: CountDownTimer? = null
    private var mGameOver = false
    private var mGamePaused = false
    private lateinit var mRewardedVideoAd: RewardedVideoAd
    private var mTimeRemaining: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this) {}

        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this)
        mRewardedVideoAd.rewardedVideoAdListener = this
        loadRewardedVideoAd()

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
        mRewardedVideoAd.pause(this)
    }

    public override fun onResume() {
        super.onResume()
        if (!mGameOver && mGamePaused) {
            resumeGame()
        }
        mRewardedVideoAd.resume(this)
    }

    private fun pauseGame() {
        mCountDownTimer?.cancel()
        mGamePaused = true
    }

    private fun resumeGame() {
        createTimer(mTimeRemaining)
        mGamePaused = false
    }

    private fun loadRewardedVideoAd() {
        if (!mRewardedVideoAd.isLoaded) {
            mRewardedVideoAd.loadAd(AD_UNIT_ID, AdRequest.Builder().build())
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
        loadRewardedVideoAd()
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
                if (mRewardedVideoAd.isLoaded) {
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
        if (mRewardedVideoAd.isLoaded) {
            mRewardedVideoAd.show()
        }
    }

    override fun onRewardedVideoAdLeftApplication() {
        Toast.makeText(this, "onRewardedVideoAdLeftApplication", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoAdClosed() {
        // Preload the next video ad.
        loadRewardedVideoAd()
        Toast.makeText(this, "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoAdFailedToLoad(errorCode: Int) {
        Toast.makeText(this, "onRewardedVideoAdFailedToLoad", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoAdLoaded() {
        Toast.makeText(this, "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoAdOpened() {
        Toast.makeText(this, "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show()
    }

    override fun onRewarded(reward: RewardItem) {
        Toast.makeText(
            this, "onRewarded! currency: ${reward.type} amount: ${reward.amount}",
            Toast.LENGTH_SHORT
        ).show()
        addCoins(reward.amount)
    }

    override fun onRewardedVideoStarted() {
        Toast.makeText(this, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show()
    }

    override fun onRewardedVideoCompleted() {
        Toast.makeText(this, "onRewardedVideoCompleted", Toast.LENGTH_SHORT).show()
    }
}
