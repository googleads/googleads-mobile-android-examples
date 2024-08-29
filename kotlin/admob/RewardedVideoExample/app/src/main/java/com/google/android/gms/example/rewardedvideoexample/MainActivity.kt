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
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.google.android.gms.example.rewardedvideoexample.databinding.ActivityMainBinding
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

    googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(this)
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

    startGame()
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
      var adRequest = AdRequest.Builder().build()

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
        },
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
            // Don't forget to set the ad reference to null so you
            // don't show the ad a second time.
            rewardedAd = null
            if (googleMobileAdsConsentManager.canRequestAds) {
              loadRewardedAd()
            }
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
        },
      )
    }
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
      MobileAds.initialize(this@MainActivity) {}
      runOnUiThread {
        // Load an ad on the main thread.
        loadRewardedAd()
      }
    }
  }

  companion object {
    // This is an ad unit ID for a test ad. Replace with your own rewarded ad unit ID.
    private const val AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917"
    private const val COUNTER_TIME = 10L
    private const val GAME_OVER_REWARD = 1
    private const val TAG = "MainActivity"

    // Check your logcat output for the test device hashed ID e.g.
    // "Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
    // to get test ads on this device" or
    // "Use new ConsentDebugSettings.Builder().addTestDeviceHashedId("ABCDEF012345") to set this as
    // a debug device".
    const val TEST_DEVICE_HASHED_ID = "ABCDEF012345"
  }
}
