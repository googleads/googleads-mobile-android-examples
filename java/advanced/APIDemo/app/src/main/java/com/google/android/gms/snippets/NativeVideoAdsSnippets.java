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

package com.google.android.gms.snippets;

import android.util.Log;
import com.google.android.gms.ads.MediaContent;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.nativead.NativeAd;

/** Java code snippets for the developer guide. */
final class NativeVideoAdsSnippets {

  public void getMediaContentInfo(NativeAd nativeAd) {
    // [START get_media_content_info]
    if (nativeAd.getMediaContent() != null) {
      MediaContent mediaContent = nativeAd.getMediaContent();
      float mediaAspectRatio = mediaContent.getAspectRatio();
      if (mediaContent.hasVideoContent()) {
        float duration = mediaContent.getDuration();
      }
    }
    // [END get_media_content_info]
  }

  private void setVideoLifecycleCallbacks(NativeAd nativeAd) {
    // [START set_video_lifecycle_callbacks]
    if (nativeAd.getMediaContent() != null) {
      VideoController videoController = nativeAd.getMediaContent().getVideoController();
      if (videoController != null) {
        videoController.setVideoLifecycleCallbacks(
            new VideoController.VideoLifecycleCallbacks() {
              @Override
              public void onVideoStart() {
                Log.d("MyApp", "Video Started");
              }

              @Override
              public void onVideoPlay() {
                Log.d("MyApp", "Video Played");
              }

              @Override
              public void onVideoPause() {
                Log.d("MyApp", "Video Paused");
              }

              @Override
              public void onVideoEnd() {
                Log.d("MyApp", "Video Ended");
              }

              @Override
              public void onVideoMute(boolean isMuted) {
                Log.d("MyApp", "Video Muted");
              }
            });
      }
    }
    // [END set_video_lifecycle_callbacks]
  }
}
