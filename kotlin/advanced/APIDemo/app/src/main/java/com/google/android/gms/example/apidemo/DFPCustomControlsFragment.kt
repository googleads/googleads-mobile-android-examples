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
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.doubleclick.PublisherAdRequest
import com.google.android.gms.ads.formats.*
import kotlinx.android.synthetic.main.fragment_dfp_customcontrols.*

/**
 * The [DFPCustomControlsFragment] class demonstrates how to use custom controls with DFP
 * custom template video ads.
 */
class DFPCustomControlsFragment : Fragment() {

  var nativeCustomTemplateAd: NativeCustomTemplateAd? = null
  var nativeAppInstallAd: NativeAppInstallAd? = null
  var nativeContentAd: NativeContentAd? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_dfp_customcontrols, container, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    btn_refresh.setOnClickListener { refreshAd() }

    refreshAd()
  }

  override fun onDestroy() {
    nativeAppInstallAd?.destroy()
    nativeAppInstallAd = null
    nativeContentAd?.destroy()
    nativeContentAd = null
    nativeCustomTemplateAd?.destroy()
    nativeCustomTemplateAd = null
    super.onDestroy()
  }

  /**
   * Populates a [NativeAppInstallAdView] object with data from a given
   * [NativeAppInstallAd].
   *
   * @param nativeAppInstallAd the object containing the ad's assets
   * @param adView the view to be populated
   */
  private fun populateAppInstallAdView(
    nativeAppInstallAd: NativeAppInstallAd,
    adView: NativeAppInstallAdView
  ) {
    adView.headlineView = adView.findViewById(R.id.appinstall_headline)
    adView.bodyView = adView.findViewById(R.id.appinstall_body)
    adView.callToActionView = adView.findViewById(R.id.appinstall_call_to_action)
    adView.iconView = adView.findViewById(R.id.appinstall_app_icon)
    adView.priceView = adView.findViewById(R.id.appinstall_price)
    adView.starRatingView = adView.findViewById(R.id.appinstall_stars)
    adView.storeView = adView.findViewById(R.id.appinstall_store)

    // Some assets are guaranteed to be in every NativeAppInstallAd.
    (adView.headlineView as TextView).text = nativeAppInstallAd.headline
    (adView.bodyView as TextView).text = nativeAppInstallAd.body
    (adView.callToActionView as Button).text = nativeAppInstallAd.callToAction
    (adView.iconView as ImageView).setImageDrawable(nativeAppInstallAd.icon.drawable)

    // Get the video controller for the ad. One will always be provided, even if the ad doesn't
    // have a video asset.
    val videoController = nativeAppInstallAd.videoController

    val mediaView = adView.findViewById<MediaView>(R.id.appinstall_media)
    val mainImageView = adView.findViewById<ImageView>(R.id.appinstall_image)

    // Apps can check the VideoController's hasVideoContent property to determine if the
    // NativeAppInstallAd has a video asset.
    if (videoController.hasVideoContent()) {
      mainImageView.visibility = View.GONE
      adView.mediaView = mediaView
    } else {
      mediaView.visibility = View.GONE
      adView.imageView = mainImageView

      // At least one image is guaranteed.
      val images = nativeAppInstallAd.images
      mainImageView.setImageDrawable(images[0].drawable)
    }

    // These assets aren't guaranteed to be in every NativeAppInstallAd, so it's important to
    // check before trying to display them.
    if (nativeAppInstallAd.price == null) {
      adView.priceView.visibility = View.INVISIBLE
    } else {
      adView.priceView.visibility = View.VISIBLE
      (adView.priceView as TextView).text = nativeAppInstallAd.price
    }

    if (nativeAppInstallAd.store == null) {
      adView.storeView.visibility = View.INVISIBLE
    } else {
      adView.storeView.visibility = View.VISIBLE
      (adView.storeView as TextView).text = nativeAppInstallAd.store
    }

    if (nativeAppInstallAd.starRating == null) {
      adView.starRatingView.visibility = View.INVISIBLE
    } else {
      (adView.starRatingView as RatingBar).rating = nativeAppInstallAd.starRating!!.toFloat()
      adView.starRatingView.visibility = View.VISIBLE
    }

    // Assign native ad object to the native view.
    adView.setNativeAd(nativeAppInstallAd)

    custom_controls.setVideoController(videoController)

    btn_refresh.isEnabled = true
  }

  /**
   * Populates a [NativeContentAdView] object with data from a given
   * [NativeContentAd].
   *
   * @param nativeContentAd the object containing the ad's assets
   * @param adView the view to be populated
   */
  private fun populateContentAdView(
    nativeContentAd: NativeContentAd,
    adView: NativeContentAdView
  ) {
    adView.headlineView = adView.findViewById(R.id.contentad_headline)
    adView.bodyView = adView.findViewById(R.id.contentad_body)
    adView.callToActionView = adView.findViewById(R.id.contentad_call_to_action)
    adView.logoView = adView.findViewById(R.id.contentad_logo)
    adView.advertiserView = adView.findViewById(R.id.contentad_advertiser)

    // Some assets are guaranteed to be in every NativeContentAd.
    (adView.headlineView as TextView).text = nativeContentAd.headline
    (adView.bodyView as TextView).text = nativeContentAd.body
    (adView.callToActionView as TextView).text = nativeContentAd.callToAction
    (adView.advertiserView as TextView).text = nativeContentAd.advertiser

    // Get the video controller for the ad. One will always be provided, even if the ad doesn't
    // have a video asset.
    val videoController = nativeContentAd.videoController

    val mediaView = adView.findViewById<MediaView>(R.id.contentad_media)
    val mainImageView = adView.findViewById<ImageView>(R.id.contentad_image)

    // Apps can check the VideoController's hasVideoContent property to determine if the
    // NativeContentAd has a video asset.
    if (videoController.hasVideoContent()) {
      mainImageView.visibility = View.GONE
      adView.mediaView = mediaView
    } else {
      mediaView.visibility = View.GONE
      adView.imageView = mainImageView

      // At least one image is guaranteed.
      val images = nativeContentAd.images
      mainImageView.setImageDrawable(images[0].drawable)
    }

    // These assets aren't guaranteed to be in every NativeContentAd, so it's important to
    // check before trying to display them.
    val logoImage = nativeContentAd.logo

    if (logoImage == null) {
      adView.logoView.visibility = View.INVISIBLE
    } else {
      (adView.logoView as ImageView).setImageDrawable(logoImage.drawable)
      adView.logoView.visibility = View.VISIBLE
    }

    // Assign native ad object to the native view.
    adView.setNativeAd(nativeContentAd)
    custom_controls.setVideoController(videoController)

    btn_refresh.isEnabled = true
  }

  /**
   * Populates a [View] object with data from a [NativeCustomTemplateAd]. This method
   * handles a particular "simple" custom native ad format.
   *
   * @param nativeCustomTemplateAd the object containing the ad's assets
   * @param adView the view to be populated
   */
  private fun populateSimpleTemplateAdView(
    nativeCustomTemplateAd: NativeCustomTemplateAd,
    adView: View
  ) {
    val headline = adView.findViewById<TextView>(R.id.simplecustom_headline)
    val caption = adView.findViewById<TextView>(R.id.simplecustom_caption)

    headline.text = nativeCustomTemplateAd.getText("Headline")
    caption.text = nativeCustomTemplateAd.getText("Caption")

    headline.setOnClickListener { nativeCustomTemplateAd.performClick("Headline") }

    val mediaPlaceholder = adView.findViewById<FrameLayout>(R.id.simplecustom_media_placeholder)

    // Get the video controller for the ad. One will always be provided, even if the ad doesn't
    // have a video asset.
    val vc = nativeCustomTemplateAd.videoController

    // Apps can check the VideoController's hasVideoContent property to determine if the
    // NativeCustomTemplateAd has a video asset.
    if (vc.hasVideoContent()) {
      mediaPlaceholder.addView(nativeCustomTemplateAd.videoMediaView)
    } else {
      val mainImage = ImageView(activity)
      mainImage.adjustViewBounds = true
      mainImage.setImageDrawable(nativeCustomTemplateAd.getImage("MainImage").drawable)

      mainImage.setOnClickListener { nativeCustomTemplateAd.performClick("MainImage") }
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
      activity!!,
      activity!!.resources.getString(R.string.customcontrols_fragment_ad_unit_id)
    )

    if (cb_customtemplate.isChecked) {

      builder.forCustomTemplateAd(
        activity!!.resources.getString(R.string.customcontrols_fragment_template_id),
        { ad ->
          if (isDetached) {
            ad.destroy()
            return@forCustomTemplateAd
          }
          nativeCustomTemplateAd?.destroy()
          nativeCustomTemplateAd = ad
          val frameLayout = view!!.findViewById<FrameLayout>(R.id.fl_adplaceholder)
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

    if (cb_appinstall.isChecked) {
      builder.forAppInstallAd { ad ->
        if (isDetached) {
          ad.destroy()
          return@forAppInstallAd
        }
        nativeAppInstallAd?.destroy()
        nativeAppInstallAd = ad
        val frameLayout = view!!.findViewById<FrameLayout>(R.id.fl_adplaceholder)
        val adView = layoutInflater
          .inflate(R.layout.ad_app_install, null) as NativeAppInstallAdView
        populateAppInstallAdView(ad, adView)
        frameLayout.removeAllViews()
        frameLayout.addView(adView)
      }
    }

    if (cb_content.isChecked) {
      builder.forContentAd { ad ->
        if (isDetached) {
          ad.destroy()
          return@forContentAd
        }
        nativeContentAd?.destroy()
        nativeContentAd = ad
        val frameLayout = view!!.findViewById<FrameLayout>(R.id.fl_adplaceholder)
        val adView = layoutInflater
          .inflate(R.layout.ad_content, null) as NativeContentAdView
        populateContentAdView(ad, adView)
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
      override fun onAdFailedToLoad(errorCode: Int) {
        btn_refresh.isEnabled = true
        Toast.makeText(activity, "Failed to load native ad: " + errorCode, Toast.LENGTH_SHORT).show()
      }
    }).build()

    adLoader.loadAd(PublisherAdRequest.Builder().build())

    custom_controls.reset()
  }
}
