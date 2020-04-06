/*
 * Copyright (C) 2019 Google LLC.
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
package com.google.android.gms.example.adaptivebannerexample

import android.os.Bundle
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.doubleclick.PublisherAdRequest
import com.google.android.gms.ads.doubleclick.PublisherAdView
import java.util.Arrays
import kotlinx.android.synthetic.main.activity_my.*

/** Main Activity. Inflates main activity xml and child fragments.  */
class MyActivity : AppCompatActivity() {

  private lateinit var adView: PublisherAdView
  private var initialLayoutComplete = false

  // Determine the screen width (less decorations) to use for the ad width.
  // If the ad width isn't known, default to the full screen width.
  val adSize: AdSize
    get() {
      val display = windowManager.defaultDisplay
      val outMetrics = DisplayMetrics()
      display.getMetrics(outMetrics)

      val density = outMetrics.density

      var adWidthPixels = ad_view_container.width.toFloat()
      if (adWidthPixels == 0f) {
        adWidthPixels = outMetrics.widthPixels.toFloat()
      }

      val adWidth = (adWidthPixels / density).toInt()
      return AdSize.getCurrentOrientationBannerAdSizeWithWidth(this, adWidth)
    }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_my)
    load_button.setOnClickListener { loadBanner(adSize) }

    // Initialize the Mobile Ads SDK.
    MobileAds.initialize(this)

    // Set your test devices. Check your logcat output for the hashed device ID to
    // get test ads on a physical device. e.g.
    // "Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
    // to get test ads on this device."
    MobileAds.setRequestConfiguration(
      RequestConfiguration.Builder()
        .setTestDeviceIds(Arrays.asList("ABCDEF012345"))
        .build()
    )

    adView = PublisherAdView(this)
    ad_view_container.addView(adView)
    // Since we're loading the banner based on the adContainerView size, we need to wait until this
    // view is laid out before we can get the width.
    ad_view_container.viewTreeObserver.addOnGlobalLayoutListener {
      if (!initialLayoutComplete) {
        initialLayoutComplete = true
        loadBanner(adSize)
      }
    }
  }

  /** Called when leaving the activity.  */
  public override fun onPause() {
    adView.pause()
    super.onPause()
  }

  /** Called when returning to the activity  */
  public override fun onResume() {
    super.onResume()
    adView.resume()
  }

  /** Called before the activity is destroyed  */
  public override fun onDestroy() {
    adView.destroy()
    super.onDestroy()
  }

  private fun loadBanner(adSize: AdSize) {
    adView.adUnitId = BACKFILL_AD_UNIT_ID
    adView.setAdSizes(adSize)

    // Create an ad request.
    val adRequest = PublisherAdRequest.Builder().build()

    // Start loading the ad in the background.
    adView.loadAd(adRequest)
  }

  companion object {
    internal val BACKFILL_AD_UNIT_ID = "/30497360/adaptive_banner_test_iu/backfill"
  }
}
