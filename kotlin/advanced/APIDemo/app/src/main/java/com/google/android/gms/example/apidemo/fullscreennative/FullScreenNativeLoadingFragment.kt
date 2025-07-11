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

package com.google.android.gms.example.apidemo.fullscreennative

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.example.apidemo.R
import com.google.android.gms.example.apidemo.databinding.FragmentFullScreenNativeLoadingBinding

/** A fragment class that provides the UI to load/show a native ad. */
class FullScreenNativeLoadingFragment : Fragment() {

  lateinit var binding: FragmentFullScreenNativeLoadingBinding
  private val nativeAdViewModel: NativeAdViewModel by activityViewModels()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    binding = FragmentFullScreenNativeLoadingBinding.inflate(layoutInflater)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    // Configure "Load ad" button.
    binding.loadAdButton.setOnClickListener {
      binding.loadAdButton.isEnabled = false
      binding.showAdButton.isEnabled = false
      loadAd()
    }

    // Configure "Show ad" button.
    binding.showAdButton.setOnClickListener {
      binding.loadAdButton.isEnabled = true
      binding.showAdButton.isEnabled = false

      // Display the full-screen native ad.
      val fragmentTransaction = parentFragmentManager.beginTransaction()
      val nativeAdFragment = FullScreenNativeFragment()
      fragmentTransaction.replace(R.id.container, nativeAdFragment)
      fragmentTransaction.addToBackStack(null)
      fragmentTransaction.commit()
    }
  }

  override fun onResume() {
    super.onResume()
    binding.loadAdButton.isEnabled = true
    binding.showAdButton.isEnabled = false
  }

  fun loadAd() {
    val adLoader =
      AdLoader.Builder(requireContext(), AD_UNIT_ID)
        .forNativeAd { nativeAd ->
          nativeAdViewModel.setNativeAd(nativeAd)
          binding.showAdButton.isEnabled = true
        }
        .withAdListener(
          object : AdListener() {
            override fun onAdFailedToLoad(adError: LoadAdError) {
              binding.loadAdButton.isEnabled = true
              Log.d(TAG, "Native ad failed to load with code: ${adError.code}, error: $adError")
            }
          }
        )
        .build()

    adLoader.loadAd(AdRequest.Builder().build())
  }

  private companion object {
    const val TAG = "FullScreenNativeAd"
    const val AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110"
  }
}
