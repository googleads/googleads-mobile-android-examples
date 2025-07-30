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

import android.app.Activity;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.appopen.AppOpenAd;

/** Java code snippets for the developer guide. */
public class AppOpenAdSnippets {
  private static final String TAG = "AppOpenAdSnippets";
  private AppOpenAd appOpenAd = null;
  private boolean isShowingAd = false;

  /** Interface definition for a callback to be invoked when an app open ad is complete. */
  public interface OnShowAdCompleteListener {
    void onShowAdComplete();
  }

  /**
   * Shows the ad if available.
   *
   * @param activity the activity that shows the ad
   */
  public void showAdIfAvailable(@NonNull final Activity activity) {
    showAdIfAvailable(
        activity,
        () -> {
          // Empty because the user will go back to the activity that shows the ad.
        });
  }

  /**
   * Shows the ad if available.
   *
   * @param activity The activity that shows the ad
   * @param onShowAdCompleteListener The listener to be notified when an app open ad is complete
   */
  // [START show_ad]
  public void showAdIfAvailable(
      @NonNull final Activity activity,
      @NonNull OnShowAdCompleteListener onShowAdCompleteListener) {
    // If the app open ad is already showing, do not show the ad again.
    if (isShowingAd) {
      Log.d(TAG, "The app open ad is already showing.");
      return;
    }

    // If the app open ad is not available yet, invoke the callback then load the ad.
    if (appOpenAd == null) {
      Log.d(TAG, "The app open ad is not ready yet.");
      onShowAdCompleteListener.onShowAdComplete();
      // Load an ad.
      return;
    }
    // [START_EXCLUDE silent]
    setFullScreenContentCallback(activity, onShowAdCompleteListener);
    // [END_EXCLUDE]

    isShowingAd = true;
    appOpenAd.show(activity);
  }

  // [END show_ad]

  private void setFullScreenContentCallback(
      Activity activity, @NonNull OnShowAdCompleteListener onShowAdCompleteListener) {
    // [START ad_events]
    appOpenAd.setFullScreenContentCallback(
        new FullScreenContentCallback() {
          @Override
          public void onAdDismissedFullScreenContent() {
            // Called when full screen content is dismissed.
            Log.d(TAG, "Ad dismissed fullscreen content.");
            // Don't forget to set the ad reference to null so you
            // don't show the ad a second time.
            appOpenAd = null;
            isShowingAd = false;

            onShowAdCompleteListener.onShowAdComplete();
            // Load an ad.
          }

          @Override
          public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
            // Called when full screen content failed to show.
            Log.d(TAG, adError.getMessage());
            appOpenAd = null;
            // Don't forget to set the ad reference to null so you
            // don't show the ad a second time.
            isShowingAd = false;

            onShowAdCompleteListener.onShowAdComplete();
            // Load an ad.
          }

          @Override
          public void onAdShowedFullScreenContent() {
            Log.d(TAG, "Ad showed fullscreen content.");
          }

          @Override
          public void onAdImpression() {
            // Called when an impression is recorded for an ad.
            Log.d(TAG, "The ad recorded an impression.");
          }

          @Override
          public void onAdClicked() {
            // Called when ad is clicked.
            Log.d(TAG, "The ad was clicked.");
          }
        });
    // [END ad_events]
  }
}
