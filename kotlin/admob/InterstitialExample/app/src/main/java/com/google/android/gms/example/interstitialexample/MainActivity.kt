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
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.example.interstitialexample.databinding.ActivityMainBinding
import java.util.concurrent.atomic.AtomicBoolean

const val GAME_LENGTH_MILLISECONDS = 3000L
const val AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"

class MainActivity : AppCompatActivity() {

  private lateinit var binding: ActivityMainBinding
  private lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager
  private var isMobileAdsInitializeCalled = AtomicBoolean(false)
  private var interstitialAd: InterstitialAd? = null
  private var countdownTimer: CountDownTimer? = null
  private var gamePaused = false
  private var gameOver = false
  private var adIsLoading: Boolean = false
  private var timerMilliseconds = 0L

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    val view = binding.root
    setContentView(view)

    // Log the Mobile Ads SDK version.
    Log.d(TAG, "Google Mobile Ads SDK Version: " + MobileAds.getVersion())

    googleMobileAdsConsentManager = GoogleMobileAdsConsentManager(this)
    googleMobileAdsConsentManager.gatherConsent { consentError ->
      if (consentError != null) {
        // Consent not obtained in current session.
        Log.w(TAG, "${consentError.errorCode}: ${consentError.message}")
      }

      // Kick off the first play of the "game".
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

    // Set your test devices. Check your logcat output for the hashed device ID to
    // get test ads on a physical device. e.g.
    // "Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
    // to get test ads on this device."
    MobileAds.setRequestConfiguration(
      RequestConfiguration.Builder().setTestDeviceIds(listOf("ABCDEF012345")).build()
    )

    // Create the "retry" button, which triggers an interstitial between game plays.
    binding.retryButton.visibility = View.INVISIBLE
    binding.retryButton.setOnClickListener { showInterstitial() }
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.action_menu, menu)
    menu?.findItem(R.id.action_more)?.apply {
      isVisible = googleMobileAdsConsentManager.isPrivacyOptionsRequired
    }
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val menuItemView = findViewById<View>(item.itemId)
    val activity = this
    PopupMenu(this, menuItemView).apply {
      menuInflater.inflate(R.menu.popup_menu, menu)
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

    val adRequest = AdRequest.Builder().build()

    InterstitialAd.load(
      this,
      AD_UNIT_ID,
      adRequest,
      object : InterstitialAdLoadCallback() {
        override fun onAdFailedToLoad(adError: LoadAdError) {
          Log.d(TAG, adError.message)
          interstitialAd = null
          adIsLoading = false
          val error =
            "domain: ${adError.domain}, code: ${adError.code}, " + "message: ${adError.message}"
          Toast.makeText(
              this@MainActivity,
              "onAdFailedToLoad() with error $error",
              Toast.LENGTH_SHORT
            )
            .show()
        }

        override fun onAdLoaded(ad: InterstitialAd) {
          Log.d(TAG, "Ad was loaded.")
          interstitialAd = ad
          adIsLoading = false
          Toast.makeText(this@MainActivity, "onAdLoaded()", Toast.LENGTH_SHORT).show()
        }
      }
    )
  }

  // Create the game timer, which counts down to the end of the level
  // and shows the "retry" button.
  private fun createTimer(milliseconds: Long) {
    countdownTimer?.cancel()

    countdownTimer =
      object : CountDownTimer(milliseconds, 50) {
        override fun onTick(millisUntilFinished: Long) {
          timerMilliseconds = millisUntilFinished
          binding.timer.text = "seconds remaining: ${ millisUntilFinished / 1000 + 1 }"
        }

        override fun onFinish() {
          gameOver = true
          binding.timer.text = "done!"
          binding.retryButton.visibility = View.VISIBLE
        }
      }

    countdownTimer?.start()
  }

  // Show the ad if it's ready. Otherwise restart the game.
  private fun showInterstitial() {
    if (interstitialAd != null) {
      interstitialAd?.fullScreenContentCallback =
        object : FullScreenContentCallback() {
          override fun onAdDismissedFullScreenContent() {
            Log.d(TAG, "Ad was dismissed.")
            // Don't forget to set the ad reference to null so you
            // don't show the ad a second time.
            interstitialAd = null
          }

          override fun onAdFailedToShowFullScreenContent(adError: AdError) {
            Log.d(TAG, "Ad failed to show.")
            // Don't forget to set the ad reference to null so you
            // don't show the ad a second time.
            interstitialAd = null
          }

          override fun onAdShowedFullScreenContent() {
            Log.d(TAG, "Ad showed fullscreen content.")
            // Called when ad is dismissed.
          }
        }
      interstitialAd?.show(this)
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
    createTimer(timerMilliseconds)
    gamePaused = true
  }

  private fun initializeMobileAdsSdk() {
    if (isMobileAdsInitializeCalled.getAndSet(true)) {
      return
    }

    // Initialize the Mobile Ads SDK.
    MobileAds.initialize(this) { initializationStatus ->
      // Load an ad.
      loadAd()
    }
  }

  // Resume the game if it's in progress.
  public override fun onResume() {
    super.onResume()
    resumeGame()
  }

  public override fun onPause() {
    super.onPause()
    pauseGame()
  }

  private companion object {
    const val TAG = "MainActivity"
  }
}
