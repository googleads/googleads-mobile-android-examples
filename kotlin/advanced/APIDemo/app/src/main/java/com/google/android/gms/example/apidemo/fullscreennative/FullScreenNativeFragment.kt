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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.gms.example.apidemo.MainActivity
import com.google.android.gms.example.apidemo.databinding.FragmentFullScreenNativeBinding

/** The [FullScreenNativeFragment] displays a full screen native ad. */
class FullScreenNativeFragment : Fragment() {

  private lateinit var binding: FragmentFullScreenNativeBinding
  private val nativeAdViewModel: NativeAdViewModel by activityViewModels()

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    binding = FragmentFullScreenNativeBinding.inflate(inflater)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    (requireActivity() as? MainActivity)?.supportActionBar?.hide()
    nativeAdViewModel.nativeAd.observe(viewLifecycleOwner) { nativeAd ->
      if (nativeAd != null) {
        populateNativeAdView(nativeAd, binding.nativeAdView)
      }
    }
  }

  private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
    // Set the media view.
    adView.apply {
      mediaView = binding.adMedia

      // Set other ad assets.
      headlineView = binding.adHeadline
      bodyView = binding.adBody
      callToActionView = binding.adCallToAction
      iconView = binding.adAppIcon.apply { clipToOutline = true }
    }

    // The headline is guaranteed to be in every NativeAd.
    binding.adHeadline.text = nativeAd.headline

    // These assets aren't guaranteed to be in every NativeAd, so it's important to
    // check before trying to display them.

    binding.adBody.apply {
      text = nativeAd.body
      visibility = if (nativeAd.body.isNullOrBlank()) View.INVISIBLE else View.VISIBLE
    }

    binding.adCallToAction.apply {
      text = nativeAd.callToAction
      visibility = if (nativeAd.callToAction.isNullOrBlank()) View.INVISIBLE else View.VISIBLE
    }

    val iconDrawable = nativeAd.icon?.drawable
    if (iconDrawable != null) {
      binding.adAppIcon.setImageDrawable(iconDrawable)
      binding.adAppIcon.visibility = View.VISIBLE
    } else {
      binding.adAppIcon.visibility = View.GONE
    }

    // This method tells the Google Mobile Ads SDK that you have finished populating your
    // native ad view with this native ad.
    adView.setNativeAd(nativeAd)
  }
}
