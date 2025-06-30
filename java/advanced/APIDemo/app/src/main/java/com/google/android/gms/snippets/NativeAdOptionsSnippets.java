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

package com.google.android.gms.snippets;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.MediaContent;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.AdChoicesView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAd.Image;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import java.util.ArrayList;
import java.util.List;

/** Java code snippets for the developer guide. */
final class NativeAdOptionsSnippets {

  private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110";
  private static final String TAG = "NativeAdOptionsSnippets";

  private void setMediaAspectRatio(Context context) {
    // [START set_media_aspect_ratio]
    NativeAdOptions nativeAdOptions =
        new NativeAdOptions.Builder()
            .setMediaAspectRatio(NativeAdOptions.NATIVE_MEDIA_ASPECT_RATIO_LANDSCAPE)
            .build();

    AdLoader loader =
        new AdLoader.Builder(context, AD_UNIT_ID).withNativeAdOptions(nativeAdOptions).build();
    // [END set_media_aspect_ratio]
  }

  private void setImageDownloadOptions(Context context) {
    // [START set_image_download_options]
    NativeAdOptions nativeAdOptions =
        new NativeAdOptions.Builder().setReturnUrlsForImageAssets(true).build();

    AdLoader loader =
        new AdLoader.Builder(context, AD_UNIT_ID)
            .withNativeAdOptions(nativeAdOptions)
            .forNativeAd(
                nativeAd -> {
                  List<Uri> imageUris = new ArrayList<>();
                  for (Image image : nativeAd.getImages()) {
                    imageUris.add(image.getUri());
                  }
                })
            .build();
    // [END set_image_download_options]
  }

  private void setImagePayloadOptions(Context context) {
    // [START set_image_payload_options]
    NativeAdOptions nativeAdOptions =
        new NativeAdOptions.Builder().setRequestMultipleImages(true).build();

    AdLoader loader =
        new AdLoader.Builder(context, AD_UNIT_ID).withNativeAdOptions(nativeAdOptions).build();
    // [END set_image_payload_options]
  }

  private void setAdChoicesPlacement(Context context) {
    // [START set_ad_choices_placement]
    NativeAdOptions nativeAdOptions =
        new NativeAdOptions.Builder()
            .setAdChoicesPlacement(NativeAdOptions.ADCHOICES_BOTTOM_RIGHT)
            .build();

    AdLoader loader =
        new AdLoader.Builder(context, AD_UNIT_ID).withNativeAdOptions(nativeAdOptions).build();
    // [END set_ad_choices_placement]
  }

  private void customizeAdChoices(Context context) {
    // [START customize_ad_choices]
    NativeAdView nativeAdView = new NativeAdView(context);
    AdChoicesView adChoicesView = new AdChoicesView(context);
    nativeAdView.setAdChoicesView(adChoicesView);
    // [END customize_ad_choices]
  }

  private void setVideoOptions(Context context) {
    // [START set_video_options]
    VideoOptions videoOptions = new VideoOptions.Builder().setStartMuted(false).build();

    NativeAdOptions nativeAdOptions =
        new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();

    AdLoader loader =
        new AdLoader.Builder(context, AD_UNIT_ID).withNativeAdOptions(nativeAdOptions).build();
    // [END set_video_options]
  }

  private void setPlaybackBehavior(Context context) {
    // [START set_custom_controls]
    VideoOptions videoOptions = new VideoOptions.Builder().setCustomControlsRequested(true).build();

    NativeAdOptions nativeAdOptions =
        new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();

    AdLoader loader =
        new AdLoader.Builder(context, AD_UNIT_ID).withNativeAdOptions(nativeAdOptions).build();
    // [END set_custom_controls]
  }

  private void checkCustomControls(NativeAd nativeAd) {
    // [START check_custom_controls]
    MediaContent mediaContent = nativeAd.getMediaContent();
    if (mediaContent != null) {
      VideoController videoController = mediaContent.getVideoController();
      boolean canShowCustomControls = videoController.isCustomControlsEnabled();
    }
    // [END check_custom_controls]
  }

  private void setCustomClickGesture(Context context) {
    // [START set_custom_click_gesture]
    NativeAdOptions adOptions =
        new NativeAdOptions.Builder()
            .enableCustomClickGestureDirection(
                NativeAdOptions.SWIPE_GESTURE_DIRECTION_RIGHT, /* tapsAllowed= */ true)
            .build();

    // ca-app-pub-3940256099942544/2247696110 is a sample ad unit ID that has custom click
    // gestures enabled.
    AdLoader.Builder builder =
        new AdLoader.Builder(context, AD_UNIT_ID).withNativeAdOptions(adOptions);
    // [END set_custom_click_gesture]
  }

  private void setSwipeGesture(Context context) {
    // [START set_swipe_gesture]
    AdLoader adLoader =
        new AdLoader.Builder(context, AD_UNIT_ID)
            .withAdListener(
                new AdListener() {
                  // Called when a swipe gesture click is recorded.
                  @Override
                  public void onAdSwipeGestureClicked() {
                    // Called when a swipe gesture click is recorded.
                    Log.d(TAG, "A swipe gesture click has occurred.");
                  }

                  @Override
                  public void onAdClicked() {
                    // Called when a swipe gesture click or a tap click is recorded, as
                    // configured in NativeAdOptions.
                    Log.d(TAG, "A swipe gesture click or a tap click has occurred.");
                  }
                })
            .build();
    // [END set_swipe_gesture]
  }
}
