/*
 * Copyright 2025 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.example.gms.fullscreennativeexample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.VideoController
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.example.gms.fullscreennativeexample.databinding.FragmentNativeAdBinding
import android.util.Log

/** A fragment class that represents the native ad. */
class NativeAdFragment : Fragment(R.layout.fragment_native_ad) {

  private var _binding: FragmentNativeAdBinding? = null
  // This property is only valid between onCreateView and onDestroyView.
  private val binding get() = _binding!!

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = FragmentNativeAdBinding.inflate(inflater)
    return binding.root
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
    // Use safe cast and let for activity access
    (activity as MainActivity).supportActionBar?.show()
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    // Use safe cast and let for activity access
    (activity as MainActivity).let { mainActivity ->
      mainActivity.getNativeAd()?.let { nativeAd ->
        populateNativeAdView(nativeAd, binding.innerLayout.nativeAdView)
      }
      mainActivity.supportActionBar?.hide()
    }
  }

  private fun populateNativeAdView(nativeAd: NativeAd, adView: NativeAdView) {
    // Set the media view.
    adView.apply {
      mediaView = binding.innerLayout.adMedia

      // Set other ad assets.
      headlineView = binding.innerLayout.adHeadline
      bodyView = binding.innerLayout.adBody
      callToActionView = binding.innerLayout.adCallToAction
      iconView = binding.innerLayout.adAppIcon.apply { clipToOutline = true }
    }

    // The headline and mediaContent are guaranteed to be in every NativeAd.
    binding.innerLayout.adHeadline.text = nativeAd.headline
    // Removed conditional unwrap: trusting the SDK's guarantee and using '!!'
    // If nativeAd.mediaContent could truly be null, a safe call or a fallback should be used here.
    binding.innerLayout.adMedia.mediaContent = nativeAd.mediaContent!!

    // These assets aren't guaranteed to be in every NativeAd, so it's important to
    // check before trying to display them.

    // Removed conditional unwrap for body
    binding.innerLayout.adBody.apply {
      text = nativeAd.body
      visibility = if (nativeAd.body.isNullOrBlank()) View.INVISIBLE else View.VISIBLE
    }

    // Removed conditional unwrap for callToAction
    binding.innerLayout.adCallToAction.apply {
      text = nativeAd.callToAction
      visibility = if (nativeAd.callToAction.isNullOrBlank()) View.INVISIBLE else View.VISIBLE
    }

    // Removed conditional unwrap for icon
    nativeAd.icon?.drawable?.let { drawable ->
      binding.innerLayout.adAppIcon.setImageDrawable(drawable)
      binding.innerLayout.adAppIcon.visibility = View.VISIBLE
    } ?: run {
      binding.innerLayout.adAppIcon.visibility = View.GONE
    }

    // This method tells the Google Mobile Ads SDK that you have finished populating your
    // native ad view with this native ad.
    adView.setNativeAd(nativeAd)

    val videoController = nativeAd.mediaContent?.videoController

    // Updates the UI to say whether or not this ad has a video asset.
    // Removed conditional unwrap for videoController
    videoController?.takeIf { it.hasVideoContent() }?.let { controller ->
      // Create a new VideoLifecycleCallbacks object and pass it to the VideoController.
      // The VideoController will call methods on this object when events occur in the
      // video lifecycle.
      controller.videoLifecycleCallbacks = object : VideoController.VideoLifecycleCallbacks() {
        override fun onVideoEnd() {
          // Publishers should allow native ads to complete video playback before
          // refreshing or replacing them with another ad in the same UI location.
          super.onVideoEnd()
        }
      }
    }
  }

  companion object {
    private const val TAG = "NativeAdFragment"
  }
}
