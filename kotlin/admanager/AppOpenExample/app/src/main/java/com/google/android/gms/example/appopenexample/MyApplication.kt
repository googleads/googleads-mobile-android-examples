package com.google.android.gms.example.appopenexample

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.multidex.MultiDexApplication

/** Application class that initializes, loads and show ads when activities change states. */
// [START application_class]
class MyApplication : MultiDexApplication(), Application.ActivityLifecycleCallbacks {

  private lateinit var appOpenAdManager: AppOpenAdManager

  override fun onCreate() {
    super<MultiDexApplication>.onCreate()
    registerActivityLifecycleCallbacks(this)

    appOpenAdManager = AppOpenAdManager(this)
    appOpenAdManager.initialize()
  }

  // [END application_class]

  /** ActivityLifecycleCallback methods. */
  // [START activity_lifecycle_callbacks]
  override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

  override fun onActivityStarted(activity: Activity) {
    // An ad activity is started when an ad is showing, which could be AdActivity class from Google
    // SDK or another activity class implemented by a third party mediation partner. Updating the
    // currentActivity only when an ad is not showing will ensure it is not an ad activity, but the
    // one that shows the ad.
    if (!appOpenAdManager.isShowingAd) {
      appOpenAdManager.currentActivity = activity
    }
  }

  override fun onActivityResumed(activity: Activity) {}

  override fun onActivityPaused(activity: Activity) {}

  override fun onActivityStopped(activity: Activity) {}

  override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

  override fun onActivityDestroyed(activity: Activity) {}

  // [END activity_lifecycle_callbacks]

  /**
   * Shows an app open ad.
   *
   * @param activity the activity that shows the app open ad
   * @param onShowAdComplete the function to be notified when an app open ad is complete
   */
  fun showAdIfAvailable(activity: Activity, onShowAdComplete: () -> Unit) {
    // We wrap the showAdIfAvailable to enforce that other classes only interact with MyApplication
    // class.
    appOpenAdManager.showAdIfAvailable(activity, onShowAdComplete)
  }

  /**
   * Load an app open ad.
   *
   * @param activity the activity that shows the app open ad
   */
  fun loadAd(activity: Activity) {
    // We wrap the loadAd to enforce that other classes only interact with MyApplication
    // class.
    appOpenAdManager.loadAd(activity)
  }
}
