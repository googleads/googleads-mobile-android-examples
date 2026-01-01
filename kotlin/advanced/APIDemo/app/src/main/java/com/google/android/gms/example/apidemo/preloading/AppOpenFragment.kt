/*
 * Copyright 2025 Google LLC
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
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.OnPaidEventListener
import com.google.android.gms.ads.ResponseInfo
import com.google.android.gms.ads.appopen.AppOpenAdPreloader
import com.google.android.gms.ads.preload.PreloadCallbackV2
import com.google.android.gms.ads.preload.PreloadConfiguration
import com.google.android.gms.example.apidemo.MainActivity.Companion.LOG_TAG
import com.google.android.gms.example.apidemo.R
import com.google.android.gms.example.apidemo.databinding.FragmentPreloadItemBinding

/** A [Fragment] subclass that preloads an app open ad. */
class AppOpenFragment : Fragment() {

  private lateinit var viewBinding: FragmentPreloadItemBinding

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    viewBinding = FragmentPreloadItemBinding.inflate(inflater, container, false)

    // Define a PreloadConfiguration.
    val configuration = PreloadConfiguration.Builder(AD_UNIT_ID).build()

    // [Optional] Define a callback to receive preload events.
    val callback =
      object : PreloadCallbackV2() {
        override fun onAdPreloaded(preloadId: String, responseInfo: ResponseInfo?) {
          Log.i(LOG_TAG, "Preload ad for $preloadId is available.")
          updateUI()
        }

        override fun onAdsExhausted(preloadId: String) {
          Log.i(LOG_TAG, "Preload ad for $preloadId is exhausted.")
          updateUI()
        }

        override fun onAdFailedToPreload(preloadId: String, adError: AdError) {
          Log.i(LOG_TAG, "Preload ad $preloadId failed to load with error: ${adError.message}.")
        }
      }

    // Start the preloading with a given preload ID, preload configuration, and callback.
    AppOpenAdPreloader.start(AD_UNIT_ID, configuration, callback)

    // Initialize the UI.
    viewBinding.txtTitle.text = getText(R.string.preload_app_open)
    viewBinding.btnShow.setOnClickListener {
      pollAndShowAd()
      updateUI()
    }
    updateUI()

    return viewBinding.root
  }

  private fun pollAndShowAd() {
    // pollAd() returns the next available ad and loads another ad in the background.
    val ad = AppOpenAdPreloader.pollAd(AD_UNIT_ID)

    // [Optional] Interact with the ad as needed.
    ad?.onPaidEventListener = OnPaidEventListener {
      // [Optional] Send the impression-level ad revenue information to your preferred
      // analytics server directly within this callback.
    }

    // Show the ad immediately.
    activity?.let { activity -> ad?.show(activity) }
  }

  private fun updateUI() {
    if (AppOpenAdPreloader.isAdAvailable(AD_UNIT_ID)) {
      viewBinding.txtStatus.text = getString(R.string.preload_available)
      viewBinding.btnShow.isEnabled = true
    } else {
      viewBinding.txtStatus.text = getString(R.string.preload_exhausted)
      viewBinding.btnShow.isEnabled = false
    }
  }

  private companion object {
    // Sample app open ad unit ID.
    const val AD_UNIT_ID = "ca-app-pub-3940256099942544/9257395921"
  }
}
