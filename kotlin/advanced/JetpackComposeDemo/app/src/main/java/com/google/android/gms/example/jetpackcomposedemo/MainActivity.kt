/*
 * Copyright 2024 Google LLC
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

package com.google.android.gms.example.jetpackcomposedemo

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.example.jetpackcomposedemo.R
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.example.jetpackcomposedemo.GoogleMobileAdsApplication.Companion.TAG
import com.google.android.gms.example.jetpackcomposedemo.GoogleMobileAdsApplication.Companion.TEST_DEVICE_HASHED_ID
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

  private val isMobileAdsInitializeCalled = AtomicBoolean(false)
  private lateinit var googleMobileAdsViewModel: GoogleMobileAdsViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    // Display content edge-to-edge.
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)

    // Handle systemWindowInsets.
    val window = window
    val rootView = window.decorView.findViewById<View>(android.R.id.content)
    ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, windowInsets ->
      val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
      v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
        leftMargin = insets.left
        bottomMargin = insets.bottom
        rightMargin = insets.right
        topMargin = insets.top
      }
      WindowInsetsCompat.CONSUMED
    }

    // Initialize Google Mobile Ads.
    initializeConsentManager()

    setContent { MainScreen(googleMobileAdsViewModel) }
  }

  private fun initializeConsentManager() {
    // Log the Mobile Ads SDK version.
    Log.d(TAG, getString(R.string.version_format, MobileAds.getVersion()))

    googleMobileAdsViewModel = GoogleMobileAdsViewModel.getInstance()
    googleMobileAdsViewModel.init(this)
    googleMobileAdsViewModel.gatherConsent(this) { error ->
      if (error != null) {
        // Consent not obtained in current session.
        Log.d(TAG, "${error.errorCode}: ${error.message}")
      }

      if (googleMobileAdsViewModel.uiState.value.canRequestAds) {
        initializeMobileAdsSdk()
      }
    }

    // This sample attempts to load ads using consent obtained in the previous session.
    if (googleMobileAdsViewModel.uiState.value.canRequestAds) {
      initializeMobileAdsSdk()
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

    // Initialize the Google Mobile Ads SDK on a background thread.
    CoroutineScope(Dispatchers.IO).launch {
      googleMobileAdsViewModel.initialize(applicationContext) {}
    }
  }
}
