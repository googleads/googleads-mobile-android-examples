package com.google.android.gms.example.bannerexample

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.example.bannerexample.databinding.ActivityMainBinding
import java.util.concurrent.atomic.AtomicBoolean

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

  private lateinit var binding: ActivityMainBinding
  private lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager
  private var isMobileAdsInitializeCalled = AtomicBoolean(false)

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(binding.root)

    // Log the Mobile Ads SDK version.
    Log.d(TAG, "Google Mobile Ads SDK Version: " + MobileAds.getVersion())

    // Set your test devices. Check your logcat output for the hashed device ID to
    // get test ads on a physical device. e.g.
    // "Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
    // to get test ads on this device."
    MobileAds.setRequestConfiguration(
      RequestConfiguration.Builder().setTestDeviceIds(listOf("ABCDEF012345")).build()
    )

    googleMobileAdsConsentManager = GoogleMobileAdsConsentManager(this)

    googleMobileAdsConsentManager.gatherConsent { error ->
      error?.let {
        // Consent not obtained in current session. This sample loads ads using
        // consent obtained in the previous session.
        Log.d(TAG, "${it.errorCode}: ${it.message}")
      }

      if (googleMobileAdsConsentManager.canRequestAds) {
        initializeMobileAdsSdk()
      }

      if (googleMobileAdsConsentManager.isFormAvailable) {
        // Regenerate the options menu to include a privacy setting.
        invalidateOptionsMenu()
      }
    }

    if (googleMobileAdsConsentManager.canRequestAds) {
      initializeMobileAdsSdk()
    }
  }

  // Called when leaving the activity
  public override fun onPause() {
    binding.adView.pause()
    super.onPause()
  }

  // Called when returning to the activity
  public override fun onResume() {
    super.onResume()
    binding.adView.resume()
  }

  // Called before the activity is destroyed
  public override fun onDestroy() {
    binding.adView.destroy()
    super.onDestroy()
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.action_menu, menu)
    val moreMenu = menu?.findItem(R.id.action_more)
    moreMenu?.isVisible = googleMobileAdsConsentManager.isFormAvailable
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
          // Handle changes to user consent.
          googleMobileAdsConsentManager.presentPrivacyOptionsForm(this) { formError ->
            formError?.let {
              Toast.makeText(this@MainActivity, it.message, Toast.LENGTH_SHORT).show()
            }
          }
          true
        }
        else -> false
      }
    }
    return super.onOptionsItemSelected(item)
  }

  private fun initializeMobileAdsSdk() {
    if (isMobileAdsInitializeCalled.get()) {
      return
    }
    isMobileAdsInitializeCalled.set(true)

    // Initialize the Google Mobile Ads SDK.
    MobileAds.initialize(this)

    // Request an ad.
    val adRequest = AdRequest.Builder().build()
    binding.adView.loadAd(adRequest)
  }
}
