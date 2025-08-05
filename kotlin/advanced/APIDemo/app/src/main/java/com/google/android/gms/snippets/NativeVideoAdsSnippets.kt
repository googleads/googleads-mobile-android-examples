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

package com.google.android.gms.snippets

import android.util.Log
import com.google.android.gms.ads.VideoController
import com.google.android.gms.ads.nativead.NativeAd

/** Kotlin code snippets for the developer guide. */
class NativeVideoAdsSnippets {

  private fun getMediaContentInfo(nativeAd: NativeAd) {
    // [START get_media_content_info]
    nativeAd.mediaContent?.let { mediaContent ->
      val mediaAspectRatio: Float = mediaContent.aspectRatio
      if (mediaContent.hasVideoContent()) {
        val duration: Float = mediaContent.duration
      }
    }
    // [END get_media_content_info]
  }

  private fun setVideoLifecycleCallbacks(nativeAd: NativeAd) {
    // [START set_video_lifecycle_callbacks]
    val videoLifecycleCallbacks =
      object : VideoController.VideoLifecycleCallbacks() {
        override fun onVideoStart() {
          Log.d(TAG, "Video started.")
        }

        override fun onVideoPlay() {
          Log.d(TAG, "Video played.")
        }

        override fun onVideoPause() {
          Log.d(TAG, "Video paused.")
        }

        override fun onVideoEnd() {
          Log.d(TAG, "Video ended.")
        }

        override fun onVideoMute(isMuted: Boolean) {
          Log.d(TAG, "Video isMuted: $isMuted.")
        }
      }
    nativeAd.mediaContent?.videoController?.videoLifecycleCallbacks = videoLifecycleCallbacks
    // [END set_video_lifecycle_callbacks]
  }

  private companion object {
    const val TAG = "NativeVideoAdsSnippets"
  }
}
