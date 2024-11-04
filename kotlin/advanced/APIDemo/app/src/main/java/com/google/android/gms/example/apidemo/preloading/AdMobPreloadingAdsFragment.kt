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
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdFormat
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.preload.PreloadCallback
import com.google.android.gms.ads.preload.PreloadConfiguration
import com.google.android.gms.example.apidemo.MainActivity.Companion.LOG_TAG
import com.google.android.gms.example.apidemo.R
import com.google.android.gms.example.apidemo.databinding.FragmentPreloadMainBinding

/** Demonstrates how to preload ads. */
class AdMobPreloadingAdsFragment : Fragment() {
  private lateinit var viewBinding: FragmentPreloadMainBinding
  private val fragmentList = mutableListOf<PreloadItemFragment>()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    viewBinding = FragmentPreloadMainBinding.inflate(inflater, container, false)

    // Initialize the fragment UI.
    addFragments()

    // Start preloading ads.
    startPreload()

    return viewBinding.root
  }

  // [START start_preload]
  private fun startPreload() {
    // Define the number of ads that can be preloaded for each ad unit.
    val bufferSize = 2
    // Define a list of PreloadConfiguration objects, specifying the ad unit ID and ad format for
    // each ad unit to be preloaded.
    val preloadConfigs =
      listOf(
        PreloadConfiguration.Builder(InterstitialFragment.AD_UNIT_ID, AdFormat.INTERSTITIAL)
          .setBufferSize(bufferSize)
          .build(),
        PreloadConfiguration.Builder(RewardedFragment.AD_UNIT_ID, AdFormat.REWARDED)
          .setBufferSize(bufferSize)
          .build(),
        PreloadConfiguration.Builder(AppOpenFragment.AD_UNIT_ID, AdFormat.APP_OPEN_AD)
          .setBufferSize(bufferSize)
          .build(),
      )

    // Define a callback to receive preload availability events.
    val callback =
      object : PreloadCallback {
        override fun onAdsAvailable(preloadConfig: PreloadConfiguration) {
          Log.i(LOG_TAG, "Preload ad for ${ preloadConfig.adFormat } is available.")
          updateUI()
        }

        override fun onAdsExhausted(preloadConfig: PreloadConfiguration) {
          Log.i(LOG_TAG, "Preload ad for configuration ${ preloadConfig.adFormat } is exhausted.")
          updateUI()
        }
      }

    // Start the preloading initialization process.
    MobileAds.startPreload(this.requireContext(), preloadConfigs, callback)
  }

  // [END start_preload]

  private fun addFragments() {
    addFragment(AppOpenFragment())
    addFragment(InterstitialFragment())
    addFragment(RewardedFragment())
  }

  private fun <T : PreloadItemFragment> addFragment(fragment: T) {
    val fragmentManager = getParentFragmentManager()
    val fragmentTransaction = fragmentManager.beginTransaction()
    fragmentTransaction.add(R.id.list_formats, fragment)
    fragmentTransaction.commit()
    fragmentList.add(fragment)
  }

  private fun updateUI() {
    fragmentList.forEach { it.updateUI() }
  }
}
