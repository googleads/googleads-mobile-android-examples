/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.gms.example.appopendemo;

import android.app.Activity;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback;
import java.util.Date;

/** Application class that initializes, loads and show ads when activities change states. */
// [START application_class]
public class MyApplication extends Application
    implements ActivityLifecycleCallbacks, DefaultLifecycleObserver {

  private AppOpenAdManager appOpenAdManager;
  private Activity currentActivity;

  @Override
  public void onCreate() {
    super.onCreate();
    this.registerActivityLifecycleCallbacks(this);

    ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
    appOpenAdManager = new AppOpenAdManager();
  }

  // [END application_class]

  /** LifecycleObserver method that shows the app open ad when the app moves to foreground. */
  // [START lifecycle_observer_events]
  @Override
  public void onStart(@NonNull LifecycleOwner owner) {
    DefaultLifecycleObserver.super.onStart(owner);
    appOpenAdManager.showAdIfAvailable(currentActivity);
  }

  // [END lifecycle_observer_events]

  /** ActivityLifecycleCallback methods. */
  // [START activity_lifecycle_callbacks]
  @Override
  public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {}

  @Override
  public void onActivityStarted(@NonNull Activity activity) {
    // An ad activity is started when an ad is showing, which could be AdActivity class from Google
    // SDK or another activity class implemented by a third party mediation partner. Updating the
    // currentActivity only when an ad is not showing will ensure it is not an ad activity, but the
    // one that shows the ad.
    if (!appOpenAdManager.isShowingAd) {
      currentActivity = activity;
    }
  }

  @Override
  public void onActivityResumed(@NonNull Activity activity) {}

  @Override
  public void onActivityPaused(@NonNull Activity activity) {}

  @Override
  public void onActivityStopped(@NonNull Activity activity) {}

  @Override
  public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {}

  @Override
  public void onActivityDestroyed(@NonNull Activity activity) {}

  // [END activity_lifecycle_callbacks]

  /**
   * Load an app open ad.
   *
   * @param activity the activity that shows the app open ad
   */
  public void loadAd(@NonNull Activity activity) {
    // We wrap the loadAd to enforce that other classes only interact with MyApplication
    // class.
    appOpenAdManager.loadAd(activity);
  }

  /**
   * Shows an app open ad.
   *
   * @param activity the activity that shows the app open ad
   * @param onShowAdCompleteListener the listener to be notified when an app open ad is complete
   */
  public void showAdIfAvailable(
      @NonNull Activity activity, @NonNull OnShowAdCompleteListener onShowAdCompleteListener) {
    // We wrap the showAdIfAvailable to enforce that other classes only interact with MyApplication
    // class.
    appOpenAdManager.showAdIfAvailable(activity, onShowAdCompleteListener);
  }

  /**
   * Interface definition for a callback to be invoked when an app open ad is complete
   * (i.e. dismissed or fails to show).
   */
  public interface OnShowAdCompleteListener {
    void onShowAdComplete();
  }

  /** Inner class that loads and shows app open ads. */
  // [START manager_class]
  private class AppOpenAdManager {

    private static final String LOG_TAG = "AppOpenAdManager";
    private static final String AD_UNIT_ID = "/21775744923/example/app-open";

    private final GoogleMobileAdsConsentManager googleMobileAdsConsentManager =
        GoogleMobileAdsConsentManager.getInstance(getApplicationContext());
    private AppOpenAd appOpenAd = null;
    private boolean isLoadingAd = false;
    private boolean isShowingAd = false;

    /** Keep track of the time an app open ad is loaded to ensure you don't show an expired ad. */
    private long loadTime = 0;

    /** Constructor. */
    public AppOpenAdManager() {}

    // [END manager_class]

    /**
     * Load an ad.
     *
     * @param context the context of the activity that loads the ad
     */
    private void loadAd(Context context) {
      // Do not load ad if there is an unused ad or one is already loading.
      if (isLoadingAd || isAdAvailable()) {
        return;
      }
      isLoadingAd = true;
      // [START load_ad]
      AppOpenAd.load(
          context,
          AD_UNIT_ID,
          new AdManagerAdRequest.Builder().build(),
          new AppOpenAdLoadCallback() {
            @Override
            public void onAdLoaded(AppOpenAd ad) {
              // Called when an app open ad has loaded.
              Log.d(LOG_TAG, "App open ad loaded.");
              Toast.makeText(context, "Ad was loaded.", Toast.LENGTH_SHORT).show();

              appOpenAd = ad;
              isLoadingAd = false;
              loadTime = (new Date()).getTime();
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
              // Called when an app open ad has failed to load.
              Log.d(LOG_TAG, "App open ad failed to load with error: " + loadAdError.getMessage());
              Toast.makeText(context, "Ad failed to load.", Toast.LENGTH_SHORT).show();

              isLoadingAd = false;
            }
          });
      // [END load_ad]
    }

    // [START ad_expiration]
    /** Check if ad was loaded more than n hours ago. */
    private boolean wasLoadTimeLessThanNHoursAgo(long numHours) {
      long dateDifference = (new Date()).getTime() - loadTime;
      long numMilliSecondsPerHour = 3600000;
      return (dateDifference < (numMilliSecondsPerHour * numHours));
    }

    /** Check if ad exists and can be shown. */
    private boolean isAdAvailable() {
      // For time interval details, see: https://support.google.com/admob/answer/9341964
      return appOpenAd != null && wasLoadTimeLessThanNHoursAgo(4);
    }

    // [END ad_expiration]

    /**
     * Show the ad if one isn't already showing.
     *
     * @param activity the activity that shows the app open ad
     */
    private void showAdIfAvailable(@NonNull final Activity activity) {
      showAdIfAvailable(
          activity,
          new OnShowAdCompleteListener() {
            @Override
            public void onShowAdComplete() {
              // Empty because the user will go back to the activity that shows the ad.
            }
          });
    }

    /**
     * Show the ad if one isn't already showing.
     *
     * @param activity the activity that shows the app open ad
     * @param onShowAdCompleteListener the listener to be notified when an app open ad is complete
     */
    private void showAdIfAvailable(
        @NonNull final Activity activity,
        @NonNull OnShowAdCompleteListener onShowAdCompleteListener) {
      // If the app open ad is already showing, do not show the ad again.
      if (isShowingAd) {
        Log.d(LOG_TAG, "The app open ad is already showing.");
        return;
      }

      // If the app open ad is not available yet, invoke the callback then load the ad.
      if (!isAdAvailable()) {
        Log.d(LOG_TAG, "The app open ad is not ready yet.");
        onShowAdCompleteListener.onShowAdComplete();
        if (googleMobileAdsConsentManager.canRequestAds()) {
          loadAd(activity);
        }
        return;
      }

      Log.d(LOG_TAG, "Will show ad.");

      appOpenAd.setFullScreenContentCallback(
          new FullScreenContentCallback() {
            /** Called when full screen content is dismissed. */
            @Override
            public void onAdDismissedFullScreenContent() {
              // Set the reference to null so isAdAvailable() returns false.
              appOpenAd = null;
              isShowingAd = false;

              Log.d(LOG_TAG, "onAdDismissedFullScreenContent.");
              Toast.makeText(activity, "onAdDismissedFullScreenContent", Toast.LENGTH_SHORT).show();

              onShowAdCompleteListener.onShowAdComplete();
              if (googleMobileAdsConsentManager.canRequestAds()) {
                loadAd(activity);
              }
            }

            /** Called when fullscreen content failed to show. */
            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
              appOpenAd = null;
              isShowingAd = false;

              Log.d(LOG_TAG, "onAdFailedToShowFullScreenContent: " + adError.getMessage());
              Toast.makeText(activity, "onAdFailedToShowFullScreenContent", Toast.LENGTH_SHORT)
                  .show();

              onShowAdCompleteListener.onShowAdComplete();
              if (googleMobileAdsConsentManager.canRequestAds()) {
                loadAd(activity);
              }
            }

            /** Called when fullscreen content is shown. */
            @Override
            public void onAdShowedFullScreenContent() {
              Log.d(LOG_TAG, "onAdShowedFullScreenContent.");
              Toast.makeText(activity, "onAdShowedFullScreenContent", Toast.LENGTH_SHORT).show();
            }
          });

      isShowingAd = true;
      appOpenAd.show(activity);
    }
  }
}
