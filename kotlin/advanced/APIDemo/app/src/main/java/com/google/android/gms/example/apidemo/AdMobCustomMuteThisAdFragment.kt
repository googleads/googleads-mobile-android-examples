/*
 * Copyright 2018 Google LLC
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
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MediaContent
import com.google.android.gms.ads.MuteThisAdReason
import com.google.android.gms.ads.VideoController
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.gms.example.apidemo.databinding.FragmentAdmobCustomMuteThisAdBinding
import com.google.android.gms.example.apidemo.databinding.NativeAdBinding

/**
 * The [AdMobCustomMuteThisAdFragment] class demonstrates how to use custom mute with AdMob native
 * ads.
 */
class AdMobCustomMuteThisAdFragment : Fragment() {

  private lateinit var fragmentBinding: FragmentAdmobCustomMuteThisAdBinding

  private var nativeAd: NativeAd? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View? {
    fragmentBinding = FragmentAdmobCustomMuteThisAdBinding.inflate(inflater)
    return fragmentBinding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    fragmentBinding.btnRefresh.setOnClickListener { refreshAd() }
    fragmentBinding.btnMuteAd.setOnClickListener { showMuteReasonsDialog() }

    refreshAd()
  }

  /**
   * Populates a [NativeAdView] object with data from a given [NativeAd].
   *
   * @param nativeAd the object containing the ad's assets
   * @param nativeAdBinding the binding object of the layout that has NativeAdView as the root view
   */
  private fun populateNativeAdView(nativeAd: NativeAd, nativeAdBinding: NativeAdBinding) {
    val nativeAdView = nativeAdBinding.root

    // Set the media view.
    nativeAdView.mediaView = nativeAdBinding.adMedia

    // Set other ad assets.
    nativeAdView.headlineView = nativeAdBinding.adHeadline
    nativeAdView.bodyView = nativeAdBinding.adBody
    nativeAdView.callToActionView = nativeAdBinding.adCallToAction
    nativeAdView.iconView = nativeAdBinding.adAppIcon
    nativeAdView.priceView = nativeAdBinding.adPrice
    nativeAdView.starRatingView = nativeAdBinding.adStars
    nativeAdView.storeView = nativeAdBinding.adStore
    nativeAdView.advertiserView = nativeAdBinding.adAdvertiser

    nativeAdBinding.adHeadline.text = nativeAd.headline
    nativeAd.mediaContent?.let { nativeAdBinding.adMedia.setMediaContent(it) }

    if (nativeAd.body == null) {
      nativeAdBinding.adBody.visibility = View.INVISIBLE
    } else {
      nativeAdBinding.adBody.text = nativeAd.body
      nativeAdBinding.adBody.visibility = View.VISIBLE
    }

    if (nativeAd.callToAction == null) {
      nativeAdBinding.adCallToAction.visibility = View.INVISIBLE
    } else {
      nativeAdBinding.adCallToAction.text = nativeAd.callToAction
      nativeAdBinding.adCallToAction.visibility = View.VISIBLE
    }

    if (nativeAd.icon == null) {
      nativeAdBinding.adAppIcon.visibility = View.GONE
    } else {
      nativeAdBinding.adAppIcon.setImageDrawable(nativeAd.icon?.drawable)
      nativeAdBinding.adAppIcon.visibility = View.VISIBLE
    }

    if (nativeAd.price == null) {
      nativeAdBinding.adPrice.visibility = View.INVISIBLE
    } else {
      nativeAdBinding.adPrice.text = nativeAd.price
      nativeAdBinding.adPrice.visibility = View.VISIBLE
    }

    if (nativeAd.store == null) {
      nativeAdBinding.adStore.visibility = View.INVISIBLE
    } else {
      nativeAdBinding.adStore.text = nativeAd.store
      nativeAdBinding.adStore.visibility = View.VISIBLE
    }

    if (nativeAd.starRating == null) {
      nativeAdBinding.adStars.visibility = View.INVISIBLE
    } else {
      nativeAdBinding.adStars.rating = nativeAd.starRating!!.toFloat()
      nativeAdBinding.adStars.visibility = View.VISIBLE
    }

    if (nativeAd.advertiser == null) {
      nativeAdBinding.adAdvertiser.visibility = View.INVISIBLE
    } else {
      nativeAdBinding.adAdvertiser.text = nativeAd.advertiser
      nativeAdBinding.adAdvertiser.visibility = View.VISIBLE
    }

    nativeAdView.setNativeAd(nativeAd)

    val mediaContent: MediaContent? = nativeAd.mediaContent

    if (mediaContent != null) {
      if (mediaContent.hasVideoContent()) {
        mediaContent.videoController.videoLifecycleCallbacks =
          object : VideoController.VideoLifecycleCallbacks() {
            override fun onVideoEnd() {
              fragmentBinding.btnRefresh.isEnabled = true
              super.onVideoEnd()
            }
          }
      } else {
        fragmentBinding.btnRefresh.isEnabled = true
      }
    }
  }

  /**
   * Creates a request for a new native ad based on the boolean parameters and calls the
   * "populateNativeAdView" method when one is successfully returned.
   */
  private fun refreshAd() {
    fragmentBinding.btnRefresh.isEnabled = false
    fragmentBinding.btnMuteAd.isEnabled = false

    val resources = requireActivity().resources
    val builder =
      requireActivity().let {
        AdLoader.Builder(it, resources.getString(R.string.custommute_fragment_ad_unit_id))
      }

    builder?.forNativeAd { nativeAd ->
      // OnNativeAdLoadedListener implementation.
      this@AdMobCustomMuteThisAdFragment.nativeAd = nativeAd
      fragmentBinding.btnMuteAd.isEnabled = nativeAd.isCustomMuteThisAdEnabled
      nativeAd?.setMuteThisAdListener {
        muteAd()
        Toast.makeText(activity, "Ad muted", Toast.LENGTH_SHORT).show()
      }

      val nativeAdBinding = NativeAdBinding.inflate(layoutInflater)
      populateNativeAdView(nativeAd, nativeAdBinding)
      fragmentBinding.adContainer.removeAllViews()
      fragmentBinding.adContainer.addView(nativeAdBinding.root)
    }

    val adOptions = NativeAdOptions.Builder().setRequestCustomMuteThisAd(true).build()

    builder?.withNativeAdOptions(adOptions)

    val adLoader =
      builder
        ?.withAdListener(
          object : AdListener() {
            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
              fragmentBinding.btnRefresh.isEnabled = true
              val error =
                "domain: ${loadAdError.domain}, code: ${loadAdError.code}, " +
                  "message: ${loadAdError.message}"
              Toast.makeText(
                  activity,
                  "Failed to load native ad with error $error",
                  Toast.LENGTH_SHORT,
                )
                .show()
            }
          }
        )
        ?.build()
    adLoader?.loadAd(AdRequest.Builder().build())
  }

  private fun showMuteReasonsDialog() {
    class MuteThisAdReasonWrapper(var reason: MuteThisAdReason) {

      override fun toString(): String {
        return reason.getDescription()
      }
    }

    val builder = AlertDialog.Builder(requireActivity())
    builder.setTitle("Select a reason")
    val reasons = nativeAd?.muteThisAdReasons

    val wrappedReasons = reasons!!.map { r -> MuteThisAdReasonWrapper(r) }

    builder.setAdapter(
      ArrayAdapter<MuteThisAdReasonWrapper>(
        requireActivity(),
        android.R.layout.simple_list_item_1,
        wrappedReasons,
      )
    ) { dialog, which ->
      dialog.dismiss()
      muteAdDialogDidSelectReason(wrappedReasons[which].reason)
    }

    builder.show()
  }

  private fun muteAdDialogDidSelectReason(reason: MuteThisAdReason) {
    // Report the mute action and reason to the ad.
    // The ad is actually muted (removed from UI) in the MuteThisAdListener callback.
    nativeAd?.muteThisAd(reason)
  }

  private fun muteAd() {
    // Disable mute button, remove ad.
    fragmentBinding.btnMuteAd.isEnabled = false
    fragmentBinding.adContainer.removeAllViews()
  }
}
