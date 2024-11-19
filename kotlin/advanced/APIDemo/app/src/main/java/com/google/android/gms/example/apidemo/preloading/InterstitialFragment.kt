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

package com.google.android.gms.example.apidemo.preloading

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.example.apidemo.MainActivity.Companion.LOG_TAG
import com.google.android.gms.example.apidemo.R
import com.google.android.gms.example.apidemo.databinding.FragmentPreloadItemBinding

/** A [Fragment] subclass that preloads an interstitial ad. */
class InterstitialFragment : PreloadItemFragment() {

  private lateinit var viewBinding: FragmentPreloadItemBinding

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    viewBinding = FragmentPreloadItemBinding.inflate(inflater, container, false)

    initializeUI()

    return viewBinding.root
  }

  private fun initializeUI() {
    viewBinding.txtTitle.text = getText(R.string.preload_interstitial)
    viewBinding.btnShow.setOnClickListener {
      pollAndShowAd()
      updateUI()
    }
    updateUI()
  }

  // [START pollAndShowAd]
  private fun pollAndShowAd() {
    // [START isAdAvailable]
    // Verify that a preloaded ad is available before polling for an ad.
    if (!InterstitialAd.isAdAvailable(requireContext(), AD_UNIT_ID)) {
      Log.w(LOG_TAG, "Preloaded interstitial ad ${AD_UNIT_ID} is not available.")
      return
    }
    // [END isAdAvailable]

    // Polling returns the next available ad and load another ad in the background.
    val ad = InterstitialAd.pollAd(requireContext(), AD_UNIT_ID)
    activity?.let { activity -> ad?.show(activity) }
  }

  // [END pollAndShowAd]

  @Synchronized
  override fun updateUI() {
    if (InterstitialAd.isAdAvailable(requireContext(), AD_UNIT_ID)) {
      viewBinding.txtStatus.text = getString(R.string.preload_available)
      viewBinding.btnShow.isEnabled = true
    } else {
      viewBinding.txtStatus.text = getString(R.string.preload_exhausted)
      viewBinding.btnShow.isEnabled = false
    }
  }

  companion object {
    const val AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712"
  }
}