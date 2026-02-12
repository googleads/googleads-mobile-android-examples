/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gms.example.bannerexample

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.example.bannerexample.databinding.ActivityMainBinding
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Main Activity. Inflates main activity xml and child fragments. */
class MainActivity : AppCompatActivity() {

  private val isMobileAdsInitializeCalled = AtomicBoolean(false)
  private var adView: AdView? = null
  private lateinit var binding: ActivityMainBinding
  private lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager

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
  }

  /** Called when leaving the activity. */
  public override fun onPause() {
    adView?.pause()
    super.onPause()
  }

  /** Called when returning to the activity. */
  public override fun onResume() {
    super.onResume()
    adView?.resume()
  }

  /** Called before the activity is destroyed. */
  public override fun onDestroy() {
    adView?.destroy()
    super.onDestroy()
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
            // Handle changes to user consent.
            googleMobileAdsConsentManager.showPrivacyOptionsForm(activity) { formError ->
              if (formError != null) {
                Toast.makeText(activity, formError.message, Toast.LENGTH_SHORT).show()
              }
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

  private fun loadBanner() {
    // [START create_ad_view]
    // Create a new ad view.
    val adView = AdView(this)
    adView.adUnitId = AD_UNIT_ID
    // [START set_ad_size]
    // Request a large anchored adaptive banner with a width of 360.
    adView.setAdSize(AdSize.getLargeAnchoredAdaptiveBannerAdSize(this, 360))
    // [END set_ad_size]
    this.adView = adView

    // Replace ad container with new ad view.
    binding.adViewContainer.removeAllViews()
    binding.adViewContainer.addView(adView)
    // [END create_ad_view]

    // Listen for ad events.
    adView.adListener =
      object : AdListener() {
        override fun onAdLoaded() {
          // Called when an ad is loaded.
          Log.d(TAG, "Ad loaded.")
        }

        override fun onAdFailedToLoad(error: LoadAdError) {
          // Called when an ad request failed.
          Log.i(TAG, "Ad failed to load: ${error.message}")
        }

        override fun onAdOpened() {
          // Called when an ad opens an overlay that covers the screen.
          Log.d(TAG, "Ad opened.")
        }

        override fun onAdClicked() {
          // Called when a click is recorded for an ad.
          Log.d(TAG, "Ad clicked.")
        }

        override fun onAdImpression() {
          // Called when an impression is recorded for an ad.
          Log.d(TAG, "Ad recorded an impression.")
        }

        override fun onAdClosed() {
          // Called when the user is about to return to the application
          // after tapping on an ad.
          Log.d(TAG, "Ad closed.")
        }
      }

    // [START load_ad]
    val adRequest = AdRequest.Builder().build()
    adView.loadAd(adRequest)
    // [END load_ad]
  }

  private fun initializeMobileAdsSdk() {
    if (isMobileAdsInitializeCalled.getAndSet(true)) {
      return
    }

    // Set your test devices.
    MobileAds.setRequestConfiguration(
      RequestConfiguration.Builder().setTestDeviceIds(listOf(TEST_DEVICE_HASHED_ID)).build()
    )

    // [START initialize_sdk]
    CoroutineScope(Dispatchers.IO).launch {
      // Initialize the Google Mobile Ads SDK on a background thread.
      MobileAds.initialize(this@MainActivity) {}
      // [START_EXCLUDE silent]
      runOnUiThread {
        // Load an ad on the main thread.
        loadBanner()
      }
      // [END_EXCLUDE]
    }
    // [END initialize_sdk]
  }

  companion object {
    // This is an ad unit ID for a test ad. Replace with your own banner ad unit ID.
    private const val AD_UNIT_ID = "ca-app-pub-3940256099942544/9214589741"
    private const val TAG = "MainActivity"

    // Check your logcat output for the test device hashed ID e.g.
    // "Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
    // to get test ads on this device" or
    // "Use new ConsentDebugSettings.Builder().addTestDeviceHashedId("ABCDEF012345") to set this as
    // a debug device".
    const val TEST_DEVICE_HASHED_ID = "ABCDEF012345"
  }
}
