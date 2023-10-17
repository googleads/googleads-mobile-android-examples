package com.google.android.gms.example.appopenexample

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.MobileAds
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Number of milliseconds to count down before showing the app open ad. This simulates the time
 * needed to load the app.
 */
private const val COUNTER_TIME_MILLISECONDS = 5000L

private const val LOG_TAG = "SplashActivity"

/** Splash Activity that inflates splash activity xml. */
class SplashActivity : AppCompatActivity() {

  private val isMobileAdsInitializeCalled = AtomicBoolean(false)
  private lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager
  private var secondsRemaining: Long = 0L

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_splash)

    // Log the Mobile Ads SDK version.
    Log.d(LOG_TAG, "Google Mobile Ads SDK Version: " + MobileAds.getVersion())

    // Create a timer so the SplashActivity will be displayed for a fixed amount of time.
    createTimer(COUNTER_TIME_MILLISECONDS)

    googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(applicationContext)
    googleMobileAdsConsentManager.gatherConsent(this) { consentError ->
      if (consentError != null) {
        // Consent not obtained in current session.
        Log.w(LOG_TAG, String.format("%s: %s", consentError.errorCode, consentError.message))
      }

      if (googleMobileAdsConsentManager.canRequestAds) {
        initializeMobileAdsSdk()
      }

      if (secondsRemaining <= 0) {
        startMainActivity()
      }
    }

    // This sample attempts to load ads using consent obtained in the previous session.
    if (googleMobileAdsConsentManager.canRequestAds) {
      initializeMobileAdsSdk()
    }
  }

  /**
   * Create the countdown timer, which counts down to zero and show the app open ad.
   *
   * @param time the number of milliseconds that the timer counts down from
   */
  private fun createTimer(time: Long) {
    val counterTextView: TextView = findViewById(R.id.timer)
    val countDownTimer: CountDownTimer =
      object : CountDownTimer(time, 1000) {
        override fun onTick(millisUntilFinished: Long) {
          secondsRemaining = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + 1
          counterTextView.text = "App is done loading in: $secondsRemaining"
        }

        override fun onFinish() {
          secondsRemaining = 0
          counterTextView.text = "Done."

          (application as MyApplication).showAdIfAvailable(
            this@SplashActivity,
            object : MyApplication.OnShowAdCompleteListener {
              override fun onShowAdComplete() {
                // Check if the consent form is currently on screen before moving to the main
                // activity.
                if (googleMobileAdsConsentManager.canRequestAds) {
                  startMainActivity()
                }
              }
            }
          )
        }
      }
    countDownTimer.start()
  }

  private fun initializeMobileAdsSdk() {
    if (isMobileAdsInitializeCalled.getAndSet(true)) {
      return
    }

    // Initialize the Mobile Ads SDK.
    MobileAds.initialize(this) {}

    // Load an ad.
    (application as MyApplication).loadAd(this)
  }

  /** Start the MainActivity. */
  fun startMainActivity() {
    val intent = Intent(this, MainActivity::class.java)
    startActivity(intent)
  }
}
