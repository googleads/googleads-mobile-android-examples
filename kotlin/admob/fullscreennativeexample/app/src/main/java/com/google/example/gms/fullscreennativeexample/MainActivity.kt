/*
 * Copyright 2025 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.example.gms.fullscreennativeexample

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.nativead.NativeAd
import com.google.example.gms.fullscreennativeexample.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Collections
import java.util.concurrent.atomic.AtomicBoolean

/** An activity class that loads a native ad. */
@SuppressLint("SetTextI18n")
class MainActivity : AppCompatActivity() {

    // Check your logcat output for the test device hashed ID e.g.
    // "Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
    // to get test ads on this device" or
    // "Use new ConsentDebugSettings.Builder().addTestDeviceHashedId("ABCDEF012345") to set this as
    // a debug device".
    companion object {
        const val TEST_DEVICE_HASHED_ID = "ABCDEF012345"
        private const val AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110"
        private const val TAG = "MainActivity"
    }

    private lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager

    private val isMobileAdsInitializeCalled = AtomicBoolean(false)
    private val fragmentManager: FragmentManager = supportFragmentManager
    private var nativeAd: NativeAd? = null

    fun getNativeAd(): NativeAd? {
        return nativeAd
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "Google Mobile Ads SDK Version: ${MobileAds.getVersion()}")

        googleMobileAdsConsentManager =
            GoogleMobileAdsConsentManager.getInstance(applicationContext)
        googleMobileAdsConsentManager.gatherConsent(
            this
        ) { consentError ->
            consentError?.let {
                // Consent not obtained in current session.
                Log.w(TAG, "Gather consent failed with code: ${it.errorCode}, message: ${it.message}")
            }

            if (googleMobileAdsConsentManager.canRequestAds()) {
                (fragmentManager.findFragmentById(R.id.fragment_container_view) as? MainFragment)?.binding?.loadAdButton?.visibility = View.VISIBLE
                initializeMobileAdsSdk()
            }

            if (googleMobileAdsConsentManager.isPrivacyOptionsRequired()) {
                // Regenerate the options menu to include a privacy setting.
                invalidateOptionsMenu()
            }
        }

        // This sample attempts to load ads using consent obtained in the previous session.
        if (googleMobileAdsConsentManager.canRequestAds()) {
            initializeMobileAdsSdk()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.action_menu, menu)
        val moreMenu = menu.findItem(R.id.action_more)
        moreMenu.isVisible = googleMobileAdsConsentManager.isPrivacyOptionsRequired()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val menuItemView = findViewById<View>(item.itemId)
        val popup = PopupMenu(this, menuItemView)
        popup.menuInflater.inflate(R.menu.popup_menu, popup.menu)
        popup.show()
        popup.setOnMenuItemClickListener { popupMenuItem ->
            if (popupMenuItem.itemId != R.id.privacy_settings) {
                false
            } else {
                // Handle changes to user consent.
                googleMobileAdsConsentManager.showPrivacyOptionsForm(
                    this
                ) { formError ->
                    formError?.let {
                        Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                    }
                }
                true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return
        }

        // Set your test devices.
        MobileAds.setRequestConfiguration(
            RequestConfiguration.Builder()
                .setTestDeviceIds(Collections.singletonList(TEST_DEVICE_HASHED_ID))
                .build()
        )
        CoroutineScope(Dispatchers.IO).launch {
            // Initialize the Google Mobile Ads SDK on a background thread.
            MobileAds.initialize(this@MainActivity) {}
            // Load an ad on the main thread.
            runOnUiThread {
                loadAd()
            }
        }
    }

    fun loadAd() {
        (fragmentManager.findFragmentById(R.id.fragment_container_view) as? MainFragment)?.binding?.loadAdButton?.isEnabled = false

        val adLoader =
            AdLoader.Builder(this, AD_UNIT_ID)
                .forNativeAd { nativeAd ->
                    this.nativeAd = nativeAd
                    (fragmentManager.findFragmentById(R.id.fragment_container_view) as? MainFragment)?.binding?.showAdButton?.isEnabled = true
                }
                .withAdListener(
                    object : AdListener() {
                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            (fragmentManager.findFragmentById(R.id.fragment_container_view) as? MainFragment)?.binding?.loadAdButton?.isEnabled = true
                            Log.d(
                                TAG,
                                "${adError.code} Native ad failed to load : $adError"
                            )
                            Toast.makeText(
                                this@MainActivity,
                                adError.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                )
                .build()

        adLoader.loadAd(AdRequest.Builder().build())
    }
}
