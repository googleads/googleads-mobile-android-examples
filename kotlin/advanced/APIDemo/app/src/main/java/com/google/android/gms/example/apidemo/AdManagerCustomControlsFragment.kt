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
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.gms.ads.nativead.NativeCustomFormatAd
import com.google.android.gms.example.apidemo.databinding.FragmentGamCustomcontrolsBinding
import com.google.android.gms.example.apidemo.databinding.NativeAdBinding

/**
 * The [AdManagerCustomControlsFragment] class demonstrates how to use custom controls with Ad
 * Manager custom native video ads.
 */
class AdManagerCustomControlsFragment : Fragment() {

  private lateinit var fragmentBinding: FragmentGamCustomcontrolsBinding

  var nativeCustomFormatAd: NativeCustomFormatAd? = null
  var nativeAd: NativeAd? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    fragmentBinding = FragmentGamCustomcontrolsBinding.inflate(inflater)
    return fragmentBinding.root
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    fragmentBinding.btnRefresh.setOnClickListener { refreshAd() }

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
   * Populates a [NativeAdView] object with data from a given [NativeAd].
   *
   * @param nativeAd the object containing the ad's assets
   * @param nativeAdBinding the binding object of the layout that has NativeAdView as the root view
   */
  private fun populateNativeAdView(nativeAd: NativeAd, nativeAdBinding: NativeAdBinding) {
    val nativeAdView = nativeAdBinding.root

    // Set the ad assets.
    nativeAdView.mediaView = nativeAdBinding.adMedia
    nativeAdView.headlineView = nativeAdBinding.adHeadline
    nativeAdView.bodyView = nativeAdBinding.adBody
    nativeAdView.callToActionView = nativeAdBinding.adCallToAction
    nativeAdView.iconView = nativeAdBinding.adAppIcon
    nativeAdView.priceView = nativeAdBinding.adPrice
    nativeAdView.starRatingView = nativeAdBinding.adStars
    nativeAdView.storeView = nativeAdBinding.adStore

    // Some assets are guaranteed to be in every NativeAd.
    nativeAdBinding.adHeadline.text = nativeAd.headline
    nativeAdBinding.adBody.text = nativeAd.body
    nativeAdBinding.adCallToAction.text = nativeAd.callToAction
    nativeAdBinding.adAppIcon.setImageDrawable(nativeAd.icon?.drawable)

    nativeAd.mediaContent?.let { nativeAdBinding.adMedia.setMediaContent(it) }

    // These assets aren't guaranteed to be in every NativeAd, so it's important to
    // check before trying to display them.
    if (nativeAd.price == null) {
      nativeAdBinding.adPrice.visibility = View.INVISIBLE
    } else {
      nativeAdBinding.adPrice.visibility = View.VISIBLE
      nativeAdBinding.adPrice.text = nativeAd.price
    }

    if (nativeAd.store == null) {
      nativeAdBinding.adStore.visibility = View.INVISIBLE
    } else {
      nativeAdBinding.adStore.visibility = View.VISIBLE
      nativeAdBinding.adStore.text = nativeAd.store
    }

    if (nativeAd.starRating == null) {
      nativeAdBinding.adStars.visibility = View.INVISIBLE
    } else {
      nativeAdBinding.adStars.rating = nativeAd.starRating!!.toFloat()
      nativeAdBinding.adStars.visibility = View.VISIBLE
    }

    // Assign native ad object to the native view.
    nativeAdView.setNativeAd(nativeAd)

    val mediaContent: MediaContent? = nativeAd.mediaContent
    mediaContent?.let { fragmentBinding.customControls.setMediaContent(it) }

    fragmentBinding.btnRefresh.isEnabled = true
  }

  /**
   * Populates a [View] object with data from a [NativeCustomFormatAd]. This method handles a
   * particular "simple" custom native ad format.
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

    // Get the media content for the ad.
    val mediaContent = nativeCustomFormatAd.mediaContent

    // Apps can check the MediaContent's hasVideoContent property to
    // determine if the NativeCustomFormatAd has a video asset.
    if (mediaContent != null && mediaContent.hasVideoContent()) {
      val mediaView = MediaView(mediaPlaceholder.getContext())
      mediaView.mediaContent = mediaContent
    } else {
      val mainImage = ImageView(activity)
      mainImage.adjustViewBounds = true
      mainImage.setImageDrawable(nativeCustomFormatAd.getImage("MainImage")?.drawable)

      mainImage.setOnClickListener { nativeCustomFormatAd.performClick("MainImage") }
      mediaPlaceholder.addView(mainImage)
    }
    mediaContent?.let { fragmentBinding.customControls.setMediaContent(it) }

    fragmentBinding.btnRefresh.isEnabled = true
  }

  /**
   * Creates a request for a new native ad based on the boolean parameters and calls the
   * corresponding "populate" method when one is successfully returned.
   */
  private fun refreshAd() {
    fragmentBinding.btnRefresh.isEnabled = false

    val builder =
      AdLoader.Builder(
        requireActivity(),
        requireActivity().resources.getString(R.string.customcontrols_fragment_ad_unit_id)
      )

    if (fragmentBinding.cbCustomFormat.isChecked) {
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
          val adView = layoutInflater.inflate(R.layout.ad_simple_custom_template, null)
          populateSimpleTemplateAdView(ad, adView)
          frameLayout.removeAllViews()
          frameLayout.addView(adView)
        },
        { _, _ ->
          Toast.makeText(
              activity,
              "A custom click has occurred in the simple template",
              Toast.LENGTH_SHORT
            )
            .show()
        }
      )
    }

    if (fragmentBinding.cbNative.isChecked) {
      builder.forNativeAd { ad ->
        if (isDetached) {
          ad.destroy()
          return@forNativeAd
        }
        nativeAd?.destroy()
        nativeAd = ad
        val nativeAdBinding = NativeAdBinding.inflate(layoutInflater)
        populateNativeAdView(ad, nativeAdBinding)
        fragmentBinding.flAdplaceholder.removeAllViews()
        fragmentBinding.flAdplaceholder.addView(nativeAdBinding.root)
      }
    }

    val videoOptions =
      VideoOptions.Builder()
        .setStartMuted(fragmentBinding.cbStartMuted.isChecked)
        .setCustomControlsRequested(fragmentBinding.cbCustomControls.isChecked)
        .build()

    val adOptions = NativeAdOptions.Builder().setVideoOptions(videoOptions).build()

    builder.withNativeAdOptions(adOptions)

    val adLoader =
      builder
        .withAdListener(
          object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
              fragmentBinding.btnRefresh.isEnabled = true
              val error =
                "domain: ${loadAdError.domain}, code: ${loadAdError.code}, " +
                  "message: ${loadAdError.message}"
              Toast.makeText(
                  activity,
                  "Failed to load native ad with error $error",
                  Toast.LENGTH_SHORT
                )
                .show()
            }
          }
        )
        .build()

    adLoader.loadAd(AdManagerAdRequest.Builder().build())

    fragmentBinding.customControls.reset()
  }
}
