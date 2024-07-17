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
import com.google.android.gms.example.jetpackcomposedemo.GoogleMobileAdsApplication.Companion.TAG

class MainActivity : ComponentActivity() {

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

    setContent { MainScreen() }
  }

  private fun initializeConsentManager() {
    // Log the Mobile Ads SDK version.
    Log.d(TAG, getString(R.string.version_format, MobileAds.getVersion()))
  }
}
