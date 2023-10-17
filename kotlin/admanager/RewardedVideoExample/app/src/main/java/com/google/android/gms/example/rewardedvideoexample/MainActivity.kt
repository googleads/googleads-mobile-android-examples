package com.google.android.gms.example.rewardedvideoexample

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
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.example.rewardedvideoexample.databinding.ActivityMainBinding
import java.util.concurrent.atomic.AtomicBoolean

const val AD_UNIT_ID = "/6499/example/rewarded-video"
const val COUNTER_TIME = 10L
const val GAME_OVER_REWARD = 1
const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

  private val isMobileAdsInitializeCalled = AtomicBoolean(false)
  private lateinit var binding: ActivityMainBinding
  private lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager
  private var coinCount: Int = 0
  private var countdownTimer: CountDownTimer? = null
  private var gameOver = false
  private var gamePaused = false
  private var isLoading = false
  private var rewardedAd: RewardedAd? = null
  private var timeRemaining: Long = 0L

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    // Log the Mobile Ads SDK version.
    Log.d(TAG, "Google Mobile Ads SDK Version: " + MobileAds.getVersion())

    googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(applicationContext)
    googleMobileAdsConsentManager.gatherConsent(this) { error ->
      if (error != null) {
        // Consent not obtained in current session.
        Log.d(TAG, "${error.errorCode}: ${error.message}")
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

    // Create the "retry" button, which tries to show a rewarded video ad between game plays.
    binding.retryButton.visibility = View.INVISIBLE
    binding.retryButton.setOnClickListener {
      startGame()
      if (rewardedAd == null && !isLoading && googleMobileAdsConsentManager.canRequestAds) {
        loadRewardedAd()
      }
    }

    // Create the "show" button, which shows a rewarded video if one is loaded.
    binding.showVideoButton.visibility = View.INVISIBLE
    binding.showVideoButton.setOnClickListener { showRewardedVideo() }

    // Display current coin count to user.
    binding.coinCountText.text = "Coins: $coinCount"
  }

  public override fun onPause() {
    super.onPause()
    pauseGame()
  }

  public override fun onResume() {
    super.onResume()
    resumeGame()
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.action_menu, menu)
    val moreMenu = menu?.findItem(R.id.action_more)
    moreMenu?.isVisible = googleMobileAdsConsentManager.isPrivacyOptionsRequired
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val menuItemView = findViewById<View>(item.itemId)
    val popup = PopupMenu(this, menuItemView)
    popup.menuInflater.inflate(R.menu.popup_menu, popup.menu)
    popup.show()
    popup.setOnMenuItemClickListener { popupMenuItem ->
      when (popupMenuItem.itemId) {
        R.id.privacy_settings -> {
          pauseGame()
          // Handle changes to user consent.
          googleMobileAdsConsentManager.showPrivacyOptionsForm(this) { formError ->
            if (formError != null) {
              Toast.makeText(this@MainActivity, formError.message, Toast.LENGTH_SHORT).show()
            }
            resumeGame()
          }
          true
        }
        else -> false
      }
    }
    return super.onOptionsItemSelected(item)
  }

  private fun pauseGame() {
    if (gameOver || gamePaused) {
      return
    }
    countdownTimer?.cancel()
    gamePaused = true
  }

  private fun resumeGame() {
    if (gameOver || !gamePaused) {
      return
    }
    createTimer(timeRemaining)
    gamePaused = false
  }

  private fun loadRewardedAd() {
    if (rewardedAd == null) {
      isLoading = true
      var adRequest = AdManagerAdRequest.Builder().build()

      RewardedAd.load(
        this,
        AD_UNIT_ID,
        adRequest,
        object : RewardedAdLoadCallback() {
          override fun onAdFailedToLoad(adError: LoadAdError) {
            Log.d(TAG, adError.message)
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
    binding.coinCountText.text = "Coins: $coinCount"
  }

  private fun startGame() {
    // Hide the retry button, load the ad, and start the timer.
    binding.retryButton.visibility = View.INVISIBLE
    binding.showVideoButton.visibility = View.INVISIBLE
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
          binding.timer.text = "seconds remaining: $timeRemaining"
        }

        override fun onFinish() {
          binding.showVideoButton.visibility = View.VISIBLE
          binding.timer.text = "The game has ended!"
          addCoins(GAME_OVER_REWARD)
          binding.retryButton.visibility = View.VISIBLE
          gameOver = true
        }
      }

    countdownTimer?.start()
  }

  private fun showRewardedVideo() {
    binding.showVideoButton.visibility = View.INVISIBLE
    if (rewardedAd != null) {
      rewardedAd?.fullScreenContentCallback =
        object : FullScreenContentCallback() {
          override fun onAdDismissedFullScreenContent() {
            Log.d(TAG, "Ad was dismissed.")
            if (googleMobileAdsConsentManager.canRequestAds) {
              loadRewardedAd()
            }
            // Don't forget to set the ad reference to null so you
            // don't show the ad a second time.
            rewardedAd = null
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
        OnUserEarnedRewardListener { rewardItem ->
          // Handle the reward.
          val rewardAmount = rewardItem.amount
          val rewardType = rewardItem.type
          addCoins(rewardAmount)
          Log.d("TAG", "User earned the reward.")
        }
      )
    }
  }

  private fun initializeMobileAdsSdk() {
    if (isMobileAdsInitializeCalled.getAndSet(true)) {
      return
    }

    // Initialize the Mobile Ads SDK.
    MobileAds.initialize(this) {}
    // Load an ad.
    loadRewardedAd()
  }
}
