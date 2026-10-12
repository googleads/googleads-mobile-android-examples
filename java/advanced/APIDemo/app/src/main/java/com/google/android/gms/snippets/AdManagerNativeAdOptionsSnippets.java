// Copyright 2026 Google LLC
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
import android.util.Log;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.nativead.NativeAdOptions;

/** Java code snippets for the developer guide. */
final class AdManagerNativeAdOptionsSnippets {

  private static final String AD_UNIT_ID = "/21775744923/example/native";
  private static final String TAG = "AdManagerNativeAdOptionsSnippets";

  private void setCustomClickGesture(Context context) {
    // [START set_custom_click_gesture]
    NativeAdOptions adOptions =
        new NativeAdOptions.Builder()
            .enableCustomClickGestureDirection(
                NativeAdOptions.SWIPE_GESTURE_DIRECTION_RIGHT, /* tapsAllowed= */ true)
            .build();

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
                    Log.d(TAG, "A swipe gesture click has occurred.");
                  }

                  @Override
                  public void onAdClicked() {
                    Log.d(TAG, "A swipe gesture click or a tap click has occurred.");
                  }
                })
            .build();
    // [END set_swipe_gesture]
  }
}
