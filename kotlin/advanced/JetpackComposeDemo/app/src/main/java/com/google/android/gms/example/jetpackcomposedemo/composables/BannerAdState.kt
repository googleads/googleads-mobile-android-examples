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

package com.google.android.gms.example.jetpackcomposedemo.composables

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.LoadAdError

/**
 * Represents the configuration of a banner advertisement.
 *
 * @param adUnitId The ID of the ad unit to load the banner into.
 * @param adRequest The AdRequest object used to configure ad targeting and loading behavior.
 * @param adSize The desired size of the banner ad (default is AdSize.BANNER).
 * @param onAdClicked Function invoked when the ad is clicked.
 * @param onAdImpression Function invoked when an ad impression is recorded.
 * @param onAdFailedToLoad Function invoked when the ad fails to load, includes the LoadAdError.
 * @param onAdLoaded Function invoked when the ad is successfully loaded.
 * @param onAdOpened Function invoked when the ad is opened (e.g., expands to a fullscreen).
 * @param onAdClosed Function invoked when the ad is closed.
 * @param onAdSwipeGestureClicked Function invoked when user performs a swipe gesture on the ad.
 */
data class BannerAdState(
  val adUnitId: String,
  val adRequest: AdRequest,
  val adSize: AdSize = AdSize.BANNER,
  val onAdClicked: (() -> Unit)? = null,
  val onAdImpression: (() -> Unit)? = null,
  val onAdFailedToLoad: ((LoadAdError) -> Unit)? = null,
  val onAdLoaded: (() -> Unit)? = null,
  val onAdOpened: (() -> Unit)? = null,
  val onAdClosed: (() -> Unit)? = null,
  val onAdSwipeGestureClicked: (() -> Unit)? = null,
)
