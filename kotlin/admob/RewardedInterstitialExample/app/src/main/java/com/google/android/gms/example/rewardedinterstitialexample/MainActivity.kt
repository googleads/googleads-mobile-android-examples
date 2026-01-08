package com.google.android.gms.example.rewardedinterstitialexample

import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import com.google.android.gms.example.rewardedinterstitialexample.databinding.ActivityMainBinding
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

  private val isMobileAdsInitializeCalled = AtomicBoolean(false)
  private lateinit var binding: ActivityMainBinding
  private lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager
  private var coinCount: Int = 0
  private var countDownTimer: CountDownTimer? = null
  private var gameOver = false
  private var gamePaused = false
  private var isLoadingAds = false
  private var rewardedInterstitialAd: RewardedInterstitialAd? = null
  private var timeRemaining: Long = 0L

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)
    setSupportActionBar(binding.toolBar)

    ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
      val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
      view.setPadding(systemBars.left, 0, systemBars.right, systemBars.bottom)
      insets
    }

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

      if (
        rewardedInterstitialAd == null &&
          !isLoadingAds &&
          googleMobileAdsConsentManager.canRequestAds
      ) {
        loadRewardedInterstitialAd()
      }
    }

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
    countDownTimer?.cancel()
    gamePaused = true
  }

  private fun resumeGame() {
    if (gameOver || !gamePaused) {
      return
    }
    createTimer(timeRemaining)
    gamePaused = false
  }

  private fun loadRewardedInterstitialAd() {
    if (rewardedInterstitialAd == null) {
      isLoadingAds = true

      // [START load_ad]
      RewardedInterstitialAd.load(
        this,
        AD_UNIT_ID,
        AdRequest.Builder().build(),
        object : RewardedInterstitialAdLoadCallback() {
          override fun onAdLoaded(rewardedAd: RewardedInterstitialAd) {
            Log.d(TAG, "Ad was loaded.")
            rewardedInterstitialAd = rewardedAd
            isLoadingAds = false
          }

          override fun onAdFailedToLoad(adError: LoadAdError) {
            Log.d(TAG, "onAdFailedToLoad: ${adError.message}")
            rewardedInterstitialAd = null
            isLoadingAds = false
          }
        },
      )
      // [END load_ad]
    }
  }

  private fun addCoins(coins: Int) {
    coinCount += coins
    binding.coinCountText.text = "Coins: $coinCount"
  }

  private fun startGame() {
    // Hide the retry button, load the ad, and start the timer.
    binding.retryButton.visibility = View.INVISIBLE
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
          binding.timer.text = "seconds remaining: $timeRemaining"
        }

        override fun onFinish() {
          binding.timer.text = "The game has ended!"
          addCoins(GAME_OVER_REWARD)
          binding.retryButton.visibility = View.VISIBLE
          gameOver = true

          if (rewardedInterstitialAd == null) {
            Log.d(TAG, "The game is over but the rewarded interstitial ad wasn't ready yet.")
            return
          }

          Log.d(TAG, "The rewarded interstitial ad is ready.")
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
          Log.d(TAG, "The rewarded interstitial ad is starting.")
          showRewardedVideo()
        }

        override fun onCancelAd() {
          Log.d(TAG, "The rewarded interstitial ad was skipped before it starts.")
        }
      }
    )
    dialog.show(supportFragmentManager, "AdDialogFragment")
  }

  private fun showRewardedVideo() {
    if (rewardedInterstitialAd == null) {
      Log.d(TAG, "The rewarded interstitial ad wasn't ready yet.")
      return
    }

    // [START set_content_callback]
    rewardedInterstitialAd?.fullScreenContentCallback =
      object : FullScreenContentCallback() {
        override fun onAdDismissedFullScreenContent() {
          // Called when fullscreen content is dismissed.
          Log.d(TAG, "Ad was dismissed.")
          // Don't forget to set the ad reference to null so you
          // don't show the ad a second time.
          rewardedInterstitialAd = null
          // [START_EXCLUDE silent]
          if (googleMobileAdsConsentManager.canRequestAds) {
            loadRewardedInterstitialAd()
          }
          // [END_EXCLUDE]
        }

        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
          // Called when fullscreen content failed to show.
          Log.d(TAG, "Ad failed to show.")
          // Don't forget to set the ad reference to null so you
          // don't show the ad a second time.
          rewardedInterstitialAd = null
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
          // Called when an ad is clicked.
          Log.d(TAG, "Ad was clicked.")
        }
      }
    // [END set_content_callback]

    // [START show_ad]
    rewardedInterstitialAd?.show(this) { rewardItem ->
      Log.d(TAG, "User earned the reward.")
      // Handle the reward.
      val rewardAmount = rewardItem.amount
      val rewardType = rewardItem.type
      // [START_EXCLUDE silent]
      addCoins(rewardItem.amount)
      // [END_EXCLUDE]
    }
    // [END show_ad]
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
        loadRewardedInterstitialAd()
      }
    }
  }

  companion object {
    // This is an ad unit ID for a test ad. Replace with your own rewarded interstitial ad unit ID.
    private const val AD_UNIT_ID = "ca-app-pub-3940256099942544/5354046379"
    private const val GAME_COUNTER_TIME = 10L
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
