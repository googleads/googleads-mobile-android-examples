package com.google.android.gms.example.appopenexample

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Splash Activity that inflates splash activity xml. */
class SplashActivity : AppCompatActivity() {

  private lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager
  private val isMobileAdsInitializeCalled = AtomicBoolean(false)
  private val gatherConsentFinished = AtomicBoolean(false)
  private var secondsRemaining: Long = 0L

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_splash)

    // Log the Mobile Ads SDK version.
    Log.d(TAG, "Google Mobile Ads SDK Version: " + MobileAds.getVersion())

    // Create a timer so the SplashActivity will be displayed for a fixed amount of time.
    createTimer()

    googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(applicationContext)
    googleMobileAdsConsentManager.gatherConsent(this) { consentError ->
      if (consentError != null) {
        // Consent not obtained in current session.
        Log.w(TAG, String.format("%s: %s", consentError.errorCode, consentError.message))
      }

      gatherConsentFinished.set(true)

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

  /** Create the countdown timer, which counts down to zero and show the app open ad. */
  private fun createTimer() {
    val counterTextView: TextView = findViewById(R.id.timer)
    val countDownTimer: CountDownTimer =
      object : CountDownTimer(COUNTER_TIME_MILLISECONDS, 1000) {
        override fun onTick(millisUntilFinished: Long) {
          secondsRemaining = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) + 1
          counterTextView.text = "App is done loading in: $secondsRemaining"
        }

        override fun onFinish() {
          secondsRemaining = 0
          counterTextView.text = "Done."

          (application as MyApplication).showAdIfAvailable(this@SplashActivity) {
            // Check if the consent form is currently on screen before moving to the main
            // activity.
            if (gatherConsentFinished.get()) {
              startMainActivity()
            }
          }
        }
      }
    countDownTimer.start()
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
      MobileAds.initialize(this@SplashActivity) {}
      runOnUiThread {
        // Load an ad on the main thread.
        (application as MyApplication).loadAd(this@SplashActivity)
      }
    }
  }

  /** Start the MainActivity. */
  fun startMainActivity() {
    val intent = Intent(this, MainActivity::class.java)
    startActivity(intent)
  }

  companion object {
    // Number of milliseconds to count down before showing the app open ad. This simulates the time
    // needed to load the app.
    private const val COUNTER_TIME_MILLISECONDS = 5000L
    private const val TAG = "SplashActivity"
    // Check your logcat output for the test device hashed ID e.g.
    // "Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
    // to get test ads on this device" or
    // "Use new ConsentDebugSettings.Builder().addTestDeviceHashedId("ABCDEF012345") to set this as
    // a debug device".
    const val TEST_DEVICE_HASHED_ID = "ABCDEF012345"
  }
}
