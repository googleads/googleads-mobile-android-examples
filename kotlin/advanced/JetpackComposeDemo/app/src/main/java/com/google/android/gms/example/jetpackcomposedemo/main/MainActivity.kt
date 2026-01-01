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

package com.google.android.gms.example.jetpackcomposedemo.main

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.example.jetpackcomposedemo.GoogleMobileAdsApplication
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

  private lateinit var mainViewModel: MainViewModel

  override fun onCreate(savedInstanceState: Bundle?) {
    // Display content edge-to-edge.
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)

    // Log the Mobile Ads SDK version.
    Log.d(
      GoogleMobileAdsApplication.TAG,
      "Google Mobile Ads SDK Version: ${MobileAds.getVersion()}",
    )

    // Initialize the view model. This will gather consent and initialize Google Mobile Ads.
    mainViewModel = MainViewModel.getInstance()
    if (!mainViewModel.isInitCalled) {
      lifecycleScope.launch { mainViewModel.init(this@MainActivity) }
    }
    setContent { MainScreen(mainViewModel) }
  }
}
