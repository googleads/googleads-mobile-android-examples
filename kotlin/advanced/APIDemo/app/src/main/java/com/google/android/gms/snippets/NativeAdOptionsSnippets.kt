// Copyright 2025 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.android.gms.snippets

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.AdChoicesView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView

/** Kotlin code snippets for the developer guide. */
class NativeAdOptionsSnippets {

  private fun setMediaAspectRatio(context: Context) {
    // [START set_media_aspect_ratio]
    val nativeAdOptions =
      NativeAdOptions.Builder()
        .setMediaAspectRatio(NativeAdOptions.NATIVE_MEDIA_ASPECT_RATIO_LANDSCAPE)
        .build()

    val loader = AdLoader.Builder(context, AD_UNIT_ID).withNativeAdOptions(nativeAdOptions).build()
    // [END set_media_aspect_ratio]
  }

  private fun setImageDownloadOptions(context: Context) {
    // [START set_image_download_options]
    val nativeAdOptions = NativeAdOptions.Builder().setReturnUrlsForImageAssets(true).build()

    val loader =
      AdLoader.Builder(context, AD_UNIT_ID)
        .withNativeAdOptions(nativeAdOptions)
        .forNativeAd { nativeAd ->
          val imageUris = nativeAd.images.mapNotNull { it.uri }
        }
        .build()
    // [END set_image_download_options]
  }

  private fun setImagePayloadOptions(context: Context) {
    // [START set_image_payload_options]
    val nativeAdOptions = NativeAdOptions.Builder().setRequestMultipleImages(true).build()

    val loader = AdLoader.Builder(context, AD_UNIT_ID).withNativeAdOptions(nativeAdOptions).build()
    // [END set_image_payload_options]
  }

  private fun setAdChoicesPlacement(context: Context) {
    // [START set_ad_choices_placement]
    val nativeAdOptions =
      NativeAdOptions.Builder()
        .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_BOTTOM_RIGHT)
        .build()

    val loader = AdLoader.Builder(context, AD_UNIT_ID).withNativeAdOptions(nativeAdOptions).build()
    // [END set_ad_choices_placement]
  }

  private fun customizeAdChoices(context: Context) {
    // [START customize_ad_choices]
    val nativeAdView = NativeAdView(context)
    val adChoicesView = AdChoicesView(context)
    nativeAdView.adChoicesView = adChoicesView
    // [END customize_ad_choices]
  }

  private fun setVideoOptions(context: Context) {
    // [START set_video_options]
    val videoOptions = VideoOptions.Builder().setStartMuted(false).build()

    val nativeAdOptions = NativeAdOptions.Builder().setVideoOptions(videoOptions).build()

    val loader = AdLoader.Builder(context, AD_UNIT_ID).withNativeAdOptions(nativeAdOptions).build()
    // [END set_video_options]
  }

  private fun setPlaybackBehavior(context: Context) {
    // [START set_custom_controls]
    val videoOptions = VideoOptions.Builder().setCustomControlsRequested(true).build()

    val nativeAdOptions = NativeAdOptions.Builder().setVideoOptions(videoOptions).build()

    val loader = AdLoader.Builder(context, AD_UNIT_ID).withNativeAdOptions(nativeAdOptions).build()
    // [END set_custom_controls]
  }

  private fun checkCustomControls(nativeAd: NativeAd) {
    // [START check_custom_controls]
    val mediaContent = nativeAd.mediaContent
    if (mediaContent != null) {
      val videoController = mediaContent.videoController
      val canShowCustomControls = videoController.isCustomControlsEnabled
    }
    // [END check_custom_controls]
  }

  private fun setCustomClickGesture(context: Context) {
    // [START set_custom_click_gesture]
    val adOptions =
      NativeAdOptions.Builder()
        .enableCustomClickGestureDirection(NativeAdOptions.SWIPE_GESTURE_DIRECTION_RIGHT, true)
        .build()

    val builder = AdLoader.Builder(context, AD_UNIT_ID).withNativeAdOptions(adOptions)
    // [END set_custom_click_gesture]
  }

  private fun setSwipeGesture(context: Context) {
    // [START set_swipe_gesture]
    val adLoader =
      AdLoader.Builder(context, AD_UNIT_ID)
        .withAdListener(
          object : AdListener() {
            override fun onAdSwipeGestureClicked() {
              // Called when a swipe gesture click is recorded.
              Log.d(TAG, "A swipe gesture click has occurred.")
            }

            override fun onAdClicked() {
              // Called when a swipe gesture click or a tap click is recorded, as
              // configured in NativeAdOptions.
              Log.d(TAG, "A swipe gesture click or a tap click has occurred.")
            }
          }
        )
        .build()
    // [END set_swipe_gesture]
  }

  private companion object {
    const val AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110"
    const val TAG = "NativeAdOptionsSnippets"
  }
}
