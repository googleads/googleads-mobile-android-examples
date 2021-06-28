/*
 * Copyright (C) 2018 Google, Inc.
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
package com.google.android.gms.example.apidemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MediaContent
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.gms.ads.nativead.NativeCustomFormatAd
import kotlinx.android.synthetic.main.fragment_gam_customcontrols.btn_refresh
import kotlinx.android.synthetic.main.fragment_gam_customcontrols.cb_custom_controls
import kotlinx.android.synthetic.main.fragment_gam_customcontrols.cb_custom_format
import kotlinx.android.synthetic.main.fragment_gam_customcontrols.cb_native
import kotlinx.android.synthetic.main.fragment_gam_customcontrols.cb_start_muted
import kotlinx.android.synthetic.main.fragment_gam_customcontrols.custom_controls

/**
 * The [AdManagerCustomControlsFragment] class demonstrates how to use custom controls with Ad
 * Manager custom native video ads.
 */
class AdManagerCustomControlsFragment : Fragment() {

  var nativeCustomFormatAd: NativeCustomFormatAd? = null
  var nativeAd: NativeAd? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_gam_customcontrols, container, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    btn_refresh.setOnClickListener { refreshAd() }

    refreshAd()
  }

  override fun onDestroy() {
    nativeAd?.destroy()
    nativeAd = null
    nativeCustomFormatAd?.destroy()
    nativeCustomFormatAd = null
    super.onDestroy()
  }

  /**
   * Populates a [NativeAdView] object with data from a given
   * [NativeAd].
   *
   * @param nativeAd the object containing the ad's assets
   * @param adView the view to be populated
   */
  private fun populateNativeAdView(
    nativeAd: NativeAd,
    adView: NativeAdView
  ) {
    adView.headlineView = adView.findViewById(R.id.ad_headline)
    adView.bodyView = adView.findViewById(R.id.ad_body)
    adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
    adView.iconView = adView.findViewById(R.id.ad_app_icon)
    adView.priceView = adView.findViewById(R.id.ad_price)
    adView.starRatingView = adView.findViewById(R.id.ad_stars)
    adView.storeView = adView.findViewById(R.id.ad_store)

    // Some assets are guaranteed to be in every NativeAd.
    (adView.headlineView as TextView).text = nativeAd.headline
    (adView.bodyView as TextView).text = nativeAd.body
    (adView.callToActionView as Button).text = nativeAd.callToAction
    (adView.iconView as ImageView).setImageDrawable(nativeAd.icon.drawable)

    // These assets aren't guaranteed to be in every NativeAd, so it's important to
    // check before trying to display them.
    if (nativeAd.price == null) {
      adView.priceView.visibility = View.INVISIBLE
    } else {
      adView.priceView.visibility = View.VISIBLE
      (adView.priceView as TextView).text = nativeAd.price
    }

    if (nativeAd.store == null) {
      adView.storeView.visibility = View.INVISIBLE
    } else {
      adView.storeView.visibility = View.VISIBLE
      (adView.storeView as TextView).text = nativeAd.store
    }

    if (nativeAd.starRating == null) {
      adView.starRatingView.visibility = View.INVISIBLE
    } else {
      (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
      adView.starRatingView.visibility = View.VISIBLE
    }

    // Assign native ad object to the native view.
    adView.setNativeAd(nativeAd)

    val mediaContent: MediaContent = nativeAd.mediaContent
    custom_controls.setVideoController(mediaContent.videoController)

    btn_refresh.isEnabled = true
  }

  /**
   * Populates a [View] object with data from a [NativeCustomFormatAd]. This method
   * handles a particular "simple" custom native ad format.
   *
   * @param nativeCustomFormatAd the object containing the ad's assets
   * @param adView the view to be populated
   */
  private fun populateSimpleTemplateAdView(
    nativeCustomFormatAd: NativeCustomFormatAd,
    adView: View
  ) {
    val headline = adView.findViewById<TextView>(R.id.simplecustom_headline)
    val caption = adView.findViewById<TextView>(R.id.simplecustom_caption)

    headline.text = nativeCustomFormatAd.getText("Headline")
    caption.text = nativeCustomFormatAd.getText("Caption")

    headline.setOnClickListener { nativeCustomFormatAd.performClick("Headline") }

    val mediaPlaceholder = adView.findViewById<FrameLayout>(R.id.simplecustom_media_placeholder)

    // Get the video controller for the ad. One will always be provided, even if the ad doesn't
    // have a video asset.
    val vc = nativeCustomFormatAd.videoController

    // Apps can check the VideoController's hasVideoContent property to determine if the
    // NativeCustomFormatAd has a video asset.
    if (vc.hasVideoContent()) {
      mediaPlaceholder.addView(nativeCustomFormatAd.videoMediaView)
    } else {
      val mainImage = ImageView(activity)
      mainImage.adjustViewBounds = true
      mainImage.setImageDrawable(nativeCustomFormatAd.getImage("MainImage").drawable)

      mainImage.setOnClickListener { nativeCustomFormatAd.performClick("MainImage") }
      mediaPlaceholder.addView(mainImage)
    }
    custom_controls.setVideoController(vc)

    btn_refresh.isEnabled = true
  }

  /**
   * Creates a request for a new native ad based on the boolean parameters and calls the
   * corresponding "populate" method when one is successfully returned.
   */
  private fun refreshAd() {
    btn_refresh.isEnabled = false

    val builder = AdLoader.Builder(
      requireActivity(),
      requireActivity().resources.getString(R.string.customcontrols_fragment_ad_unit_id)
    )

    if (cb_custom_format.isChecked) {
      builder.forCustomFormatAd(
        requireActivity().resources.getString(R.string.customcontrols_fragment_template_id),
        { ad ->
          if (isDetached) {
            ad.destroy()
            return@forCustomFormatAd
          }
          nativeCustomFormatAd?.destroy()
          nativeCustomFormatAd = ad
          val frameLayout = requireView().findViewById<FrameLayout>(R.id.fl_adplaceholder)
          val adView = layoutInflater
            .inflate(R.layout.ad_simple_custom_template, null)
          populateSimpleTemplateAdView(ad, adView)
          frameLayout.removeAllViews()
          frameLayout.addView(adView)
        },
        { _, _ ->
          Toast.makeText(
            activity,
            "A custom click has occurred in the simple template",
            Toast.LENGTH_SHORT
          ).show()
        }
      )
    }

    if (cb_native.isChecked) {
      builder.forNativeAd { ad ->
        if (isDetached) {
          ad.destroy()
          return@forNativeAd
        }
        nativeAd?.destroy()
        nativeAd = ad
        val frameLayout = requireView().findViewById<FrameLayout>(R.id.fl_adplaceholder)
        val adView = layoutInflater
          .inflate(R.layout.native_ad, null) as NativeAdView
        populateNativeAdView(ad, adView)
        frameLayout.removeAllViews()
        frameLayout.addView(adView)
      }
    }

    val videoOptions = VideoOptions.Builder()
      .setStartMuted(cb_start_muted.isChecked)
      .setCustomControlsRequested(cb_custom_controls.isChecked)
      .build()

    val adOptions = NativeAdOptions.Builder()
      .setVideoOptions(videoOptions)
      .build()

    builder.withNativeAdOptions(adOptions)

    val adLoader = builder.withAdListener(object : AdListener() {
      override fun onAdFailedToLoad(loadAdError: LoadAdError) {
        btn_refresh.isEnabled = true
        val error = "domain: ${loadAdError.domain}, code: ${loadAdError.code}, " +
          "message: ${loadAdError.message}"
        Toast.makeText(
          activity, "Failed to load native ad with error $error",
          Toast.LENGTH_SHORT
        ).show()
      }
    }).build()

    adLoader.loadAd(AdManagerAdRequest.Builder().build())

    custom_controls.reset()
  }
}
