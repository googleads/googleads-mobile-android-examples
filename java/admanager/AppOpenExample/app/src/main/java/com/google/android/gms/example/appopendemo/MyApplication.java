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
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/** Application class that initializes, loads and show ads when activities change states. */
// [START application_class]
public class MyApplication extends Application implements ActivityLifecycleCallbacks {
  // [START_EXCLUDE silent]
  // Check your logcat output for the test device hashed ID e.g.
  // "Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
  // to get test ads on this device" or
  // "Use new ConsentDebugSettings.Builder().addTestDeviceHashedId("ABCDEF012345") to set this as
  // a debug device".
  public static final String TEST_DEVICE_HASHED_ID = "ABCDEF012345";
  // [END_EXCLUDE]

  private AppOpenAdManager appOpenAdManager;

  @Override
  public void onCreate() {
    super.onCreate();
    this.registerActivityLifecycleCallbacks(this);

    appOpenAdManager = new AppOpenAdManager(this);
    appOpenAdManager.initialize();
  }

  // [END application_class]

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
    appOpenAdManager.setActivity(activity);
  }

  @Override
  public void onActivityResumed(@NonNull Activity activity) {}

  @Override
  public void onActivityPaused(@NonNull Activity activity) {}

  @Override
  public void onActivityStopped(@NonNull Activity activity) {
    // Ensure resources are released appropriately.
    appOpenAdManager.clearActivity(activity);
  }

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
   * Interface definition for a callback to be invoked when an app open ad is complete (i.e.
   * dismissed or fails to show).
   */
  public interface OnShowAdCompleteListener {
    void onShowAdComplete();
  }
}
