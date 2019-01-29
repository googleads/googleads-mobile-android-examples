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
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.VideoController
import com.google.android.gms.ads.formats.MediaView
import com.google.android.gms.ads.formats.NativeAdOptions
import com.google.android.gms.ads.formats.UnifiedNativeAd
import com.google.android.gms.ads.formats.UnifiedNativeAdView
import kotlinx.android.synthetic.main.fragment_admob_custom_mute_this_ad.*

/**
 * The [AdMobCustomMuteThisAdFragment] class demonstrates how to use custom mute
 * with AdMob native ads.
 */
class AdMobCustomMuteThisAdFragment : Fragment() {

    private var nativeAd: UnifiedNativeAd? = null

    override fun onCreateView(
      inflater: LayoutInflater?,
      container: ViewGroup?,
      savedInstanceState: Bundle?
    ): View? {
        return inflater!!.inflate(
                R.layout.fragment_admob_custom_mute_this_ad, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        btn_refresh.setOnClickListener { refreshAd() }
        btn_mute_ad.setOnClickListener { showMuteReasonsDialog() }

        refreshAd()
    }

    /**
     * Populates a [UnifiedNativeAdView] object with data from a given
     * [UnifiedNativeAd].
     *
     * @param nativeAd the object containing the ad's assets
     * @param adView the view to be populated
     */
    private fun populateUnifiedNativeAdView(
      nativeAd: UnifiedNativeAd,
      adView: UnifiedNativeAdView
    ) {
        adView.mediaView = adView.findViewById<MediaView>(R.id.ad_media)

        // Set other ad assets.
        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        adView.priceView = adView.findViewById(R.id.ad_price)
        adView.starRatingView = adView.findViewById(R.id.ad_stars)
        adView.storeView = adView.findViewById(R.id.ad_store)
        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

        (adView.headlineView as TextView).text = nativeAd.headline

        if (nativeAd.body == null) {
            adView.bodyView.visibility = View.INVISIBLE
        } else {
            (adView.bodyView as TextView).text = nativeAd.body
            adView.bodyView.visibility = View.VISIBLE
        }

        if (nativeAd.callToAction == null) {
            adView.callToActionView.visibility = View.INVISIBLE
        } else {
            (adView.callToActionView as Button).text = nativeAd.callToAction
            adView.callToActionView.visibility = View.VISIBLE
        }

        if (nativeAd.icon == null) {
            adView.iconView.visibility = View.GONE
        } else {
            (adView.iconView as ImageView).setImageDrawable(
                    nativeAd.icon.drawable)
            adView.iconView.visibility = View.VISIBLE
        }

        if (nativeAd.price == null) {
            adView.priceView.visibility = View.INVISIBLE
        } else {
            (adView.priceView as TextView).text = nativeAd.price
            adView.priceView.visibility = View.VISIBLE
        }

        if (nativeAd.store == null) {
            adView.storeView.visibility = View.INVISIBLE
        } else {
            (adView.storeView as TextView).text = nativeAd.store
            adView.storeView.visibility = View.VISIBLE
        }

        if (nativeAd.starRating == null) {
            adView.starRatingView.visibility = View.INVISIBLE
        } else {
            (adView.starRatingView as RatingBar).rating = nativeAd.starRating!!.toFloat()
            adView.starRatingView.visibility = View.VISIBLE
        }

        if (nativeAd.advertiser == null) {
            adView.advertiserView.visibility = View.INVISIBLE
        } else {
            (adView.advertiserView as TextView).text = nativeAd.advertiser
            adView.advertiserView.visibility = View.VISIBLE
        }

        adView.setNativeAd(nativeAd)

        val vc = nativeAd.videoController

        if (vc.hasVideoContent()) {
            vc.videoLifecycleCallbacks = object : VideoController.VideoLifecycleCallbacks() {
                override fun onVideoEnd() {
                    btn_refresh.isEnabled = true
                    super.onVideoEnd()
                }
            }
        } else {
            btn_refresh.isEnabled = true
        }
    }

    /**
     * Creates a request for a new unified native ad based on the boolean parameters and calls the
     * "populateUnifiedNativeAdView" method when one is successfully returned.
     */
    private fun refreshAd() {
        btn_refresh.isEnabled = false
        btn_mute_ad.isEnabled = false

        val resources = activity.resources
        val builder = AdLoader.Builder(activity,
                resources.getString(R.string.custommute_fragment_ad_unit_id))

        builder.forUnifiedNativeAd { unifiedNativeAd ->
            // OnUnifiedNativeAdLoadedListener implementation.
            this@AdMobCustomMuteThisAdFragment.nativeAd = unifiedNativeAd
            btn_mute_ad.isEnabled = unifiedNativeAd.isCustomMuteThisAdEnabled
            nativeAd?.setMuteThisAdListener {
                    muteAd()
                    Toast.makeText(activity, "Ad muted", Toast.LENGTH_SHORT).show()
                }

            val adView = layoutInflater
                    .inflate(R.layout.ad_unified, null) as UnifiedNativeAdView
            populateUnifiedNativeAdView(unifiedNativeAd, adView)
            ad_container.removeAllViews()
            ad_container.addView(adView)
        }

        val adOptions = NativeAdOptions.Builder()
                .setRequestCustomMuteThisAd(true)
                .build()

        builder.withNativeAdOptions(adOptions)

        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(errorCode: Int) {
                btn_refresh.isEnabled = true
                Toast.makeText(
                        activity, "Failed to load native ad: $errorCode", Toast.LENGTH_SHORT).show()
            }
        }).build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun showMuteReasonsDialog() {
        class MuteThisAdReasonWrapper(var reason: MuteThisAdReason) {

            override fun toString(): String {
                return reason.getDescription()
            }
        }

        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Select a reason")
        val reasons = nativeAd?.muteThisAdReasons

        val wrappedReasons = reasons!!.map { r -> MuteThisAdReasonWrapper(r) }

        builder.setAdapter(
                ArrayAdapter<MuteThisAdReasonWrapper>(activity,
                        android.R.layout.simple_list_item_1, wrappedReasons)
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
        btn_mute_ad.isEnabled = false
        ad_container.removeAllViews()
    }
}
