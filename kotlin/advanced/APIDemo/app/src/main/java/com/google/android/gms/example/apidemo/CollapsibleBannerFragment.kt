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

package com.google.android.gms.example.apidemo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.example.apidemo.databinding.FragmentCollapsibleBannerBinding

/** The [CollapsibleBannerFragment] class demonstrates how to use a collapsible banner ad. */
class CollapsibleBannerFragment : Fragment(), ViewTreeObserver.OnGlobalLayoutListener {

  private lateinit var fragmentBinding: FragmentCollapsibleBannerBinding
  private lateinit var adView: AdView

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    fragmentBinding = FragmentCollapsibleBannerBinding.inflate(inflater)

    adView = AdView(requireContext())
    fragmentBinding.adViewContainer.addView(adView)

    return fragmentBinding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    view.viewTreeObserver.addOnGlobalLayoutListener(this)
  }

  private fun loadCollapsibleBanner() {
    // Create an extra parameter that aligns the bottom of the expanded ad to
    // the bottom of the bannerView.
    val extras = Bundle()
    extras.putString("collapsible", "bottom")

    // Create an ad request.
    val adRequest =
      AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter::class.java, extras).build()

    // Listen to ad events.
    adView.adListener =
      object : AdListener() {
        override fun onAdLoaded() {
          Log.i(
            MainActivity.LOG_TAG,
            "Ad loaded. adView.isCollapsible() is ${adView.isCollapsible}.",
          )
        }
      }

    // Start loading a collapsible banner ad.
    adView.loadAd(adRequest)
  }

  // Use the fragment width as the ad width when layout is finished.
  override fun onGlobalLayout() {
    val screenPixelDensity = requireContext().resources.displayMetrics.density
    val adWidth = (fragmentBinding.adViewContainer.width / screenPixelDensity).toInt()

    // Step 1: Create an AdSize.
    val adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(requireContext(), adWidth)

    // Step 2: Set ad unit ID and ad size to the banner ad view.
    adView.adUnitId = SAMPLE_AD_UNIT_ID
    adView.setAdSize(adSize)

    // Step 3: Load a banner ad.
    loadCollapsibleBanner()

    view?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
  }

  companion object {
    // This is an ad unit ID for a test ad. Replace with your own banner ad unit ID.
    private const val SAMPLE_AD_UNIT_ID = "ca-app-pub-3940256099942544/9214589741"
  }
}
