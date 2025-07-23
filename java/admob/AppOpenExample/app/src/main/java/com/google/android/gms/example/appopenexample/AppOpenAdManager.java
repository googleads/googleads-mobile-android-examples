package com.google.android.gms.example.appopenexample;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ProcessLifecycleOwner;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.appopen.AppOpenAd.AppOpenAdLoadCallback;
import com.google.android.gms.example.appopenexample.MyApplication.OnShowAdCompleteListener;
import java.util.Date;

/** Class that loads and shows app open ads. */
// [START app_open_ad_manager]
public class AppOpenAdManager implements DefaultLifecycleObserver {
  private static final String TAG = "AppOpenAdManager";
  private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/9257395921";
  // For time interval details, see: https://support.google.com/admob/answer/9341964
  private static final Integer TIME_INTERVAL = 4;
  private final GoogleMobileAdsConsentManager googleMobileAdsConsentManager;
  private AppOpenAd appOpenAd = null;
  private boolean isShowingAd = false;
  private boolean isLoadingAd = false;
  private Activity currentActivity;

  /** Keep track of the time an app open ad is loaded to ensure you don't show an expired ad. */
  private long loadTime = 0;

  // [END app_open_ad_manager]

  /** Constructor. */
  public AppOpenAdManager(Context context) {
    this.googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(context);
  }

  public void initialize() {
    ProcessLifecycleOwner.get().getLifecycle().addObserver(this);
  }

  /**
   * Updates the current activity reference. This should be called from the {@link
   * android.app.Application.ActivityLifecycleCallbacks#onActivityStarted(Activity)} method to
   * ensure the manager always has a reference to the foreground activity.
   *
   * @param activity The activity that has just started and is now visible to the user.
   */
  public void setActivity(Activity activity) {
    // Only update if an ad is not currently showing.
    if (!isShowingAd) {
      this.currentActivity = activity;
    }
  }

  /**
   * Clears the current activity reference to prevent memory leaks. This should be called from the
   * {@link android.app.Application.ActivityLifecycleCallbacks#onActivityStopped(Activity)} method.
   *
   * <p>
   *
   * @param activity The activity that has just stopped and is no longer visible.
   */
  public void clearActivity(Activity activity) {
    // Clear the reference if it matches the activity being stopped.
    if (this.currentActivity == activity) {
      this.currentActivity = null;
    }
  }

  /**
   * DefaultLifecycleObserver method that shows the app open ad when the app moves to foreground.
   */
  // [START start_lifecycle_observer]
  @Override
  public void onStart(@NonNull LifecycleOwner owner) {
    DefaultLifecycleObserver.super.onStart(owner);
    if (currentActivity != null) {
      showAdIfAvailable(currentActivity);
    }
  }

  /** DefaultLifecycleObserver method that is called when the manager is destroyed. */
  @Override
  public void onDestroy(@NonNull LifecycleOwner owner) {
    DefaultLifecycleObserver.super.onDestroy(owner);
    ProcessLifecycleOwner.get().getLifecycle().removeObserver(this);
  }

  // [END start_lifecycle_observer]

  /**
   * Load an ad.
   *
   * @param context the context of the activity that loads the ad
   */
  // [START load_ad]
  public void loadAd(Context context) {
    // Do not load ad if there is an unused ad or one is already loading.
    if (isLoadingAd || isAdAvailable()) {
      return;
    }
    isLoadingAd = true;

    AppOpenAd.load(
        context,
        AD_UNIT_ID,
        new AdRequest.Builder().build(),
        new AppOpenAdLoadCallback() {
          @Override
          public void onAdLoaded(@NonNull AppOpenAd ad) {
            Log.d(TAG, "Ad was loaded.");
            Toast.makeText(context, "Ad was loaded.", Toast.LENGTH_SHORT).show();
            appOpenAd = ad;
            isLoadingAd = false;
            loadTime = (new Date()).getTime();
          }

          @Override
          public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
            Log.d(TAG, "Ad failed to load with error: " + loadAdError.getMessage());
            Toast.makeText(context, "Ad failed to load.", Toast.LENGTH_SHORT).show();
            isLoadingAd = false;
          }
        });
  }

  // [END load_ad]

  /** Check if ad was loaded more than n hours ago. */
  // [START ad_expiration]
  private boolean wasLoadTimeLessThanNHoursAgo() {
    long dateDifference = (new Date()).getTime() - loadTime;
    long numMilliSecondsPerHour = 3600000;
    return (dateDifference < (numMilliSecondsPerHour * TIME_INTERVAL));
  }

  /** Check if ad exists and can be shown. */
  private boolean isAdAvailable() {
    return appOpenAd != null && wasLoadTimeLessThanNHoursAgo();
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
        () -> {
          // Empty because the user will go back to the activity that shows the ad.
        });
  }

  /**
   * Show the ad if one isn't already showing.
   *
   * @param activity the activity that shows the app open ad.
   * @param onShowAdCompleteListener the listener to be notified when an app open ad is complete.
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
    if (!isAdAvailable()) {
      Log.d(TAG, "The app open ad is not ready yet.");
      onShowAdCompleteListener.onShowAdComplete();
      if (googleMobileAdsConsentManager.canRequestAds()) {
        loadAd(currentActivity);
      }
      return;
    }
    // [START_EXCLUDE silent]
    setFullScreenContentCallback(activity, onShowAdCompleteListener);
    // [END_EXCLUDE]

    isShowingAd = true;
    appOpenAd.show(activity);
  }

  // [END show_ad]

  /**
   * Set the full screen content callback for the app open ad.
   *
   * @param activity the activity needed for the app open ad.
   * @param onShowAdCompleteListener the listener to be notified when an app open ad is complete.
   */
  private void setFullScreenContentCallback(
      Activity activity, OnShowAdCompleteListener onShowAdCompleteListener) {
    // [START ad_events]
    appOpenAd.setFullScreenContentCallback(
        new FullScreenContentCallback() {
          @Override
          public void onAdDismissedFullScreenContent() {
            // Called when full screen content is dismissed.
            appOpenAd = null;
            isShowingAd = false;
            Log.d(TAG, "Ad dismissed full screen content.");
            Toast.makeText(activity, "Ad dismissed full screen content", Toast.LENGTH_SHORT).show();
            onShowAdCompleteListener.onShowAdComplete();
            if (googleMobileAdsConsentManager.canRequestAds()) {
              loadAd(activity);
            }
          }

          @Override
          public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
            // Called when fullscreen content failed to show.
            Log.d(TAG, "Ad failed to show full screen content with error: " + adError.getMessage());
            Toast.makeText(activity, "Ad failed to show full screen content.", Toast.LENGTH_SHORT)
                .show();
            appOpenAd = null;
            isShowingAd = false;
            onShowAdCompleteListener.onShowAdComplete();
            if (googleMobileAdsConsentManager.canRequestAds()) {
              loadAd(activity);
            }
          }

          @Override
          public void onAdShowedFullScreenContent() {
            // Called when fullscreen content is shown
            Log.d(TAG, "Ad showed full screen content.");
            Toast.makeText(activity, "Ad showed full screen content.", Toast.LENGTH_SHORT).show();
          }
        });
    // [END ad_events]
  }
}
