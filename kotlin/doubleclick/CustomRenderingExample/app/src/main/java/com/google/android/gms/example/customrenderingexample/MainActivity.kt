/*
 * Copyright (C) 2017 Google, Inc.
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

package com.google.android.gms.example.customrenderingexample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.*
import com.google.android.gms.ads.*
import com.google.android.gms.ads.doubleclick.PublisherAdRequest
import com.google.android.gms.ads.formats.*
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

const val DFP_AD_UNIT_ID = "/6499/example/native"
const val SIMPLE_TEMPLATE_ID = "10104090"

/**
 * A simple activity class that displays native ad formats.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        refresh_button.setOnClickListener {
            refreshAd(appinstall_checkbox.isChecked,
                    content_checkbox.isChecked,
                    customtemplate_checkbox.isChecked)
        }

        refreshAd(appinstall_checkbox.isChecked,
                content_checkbox.isChecked,
                customtemplate_checkbox.isChecked)
    }

    /**
     * Populates a [NativeAppInstallAdView] object with data from a given
     * [NativeAppInstallAd].
     *
     * @param nativeAppInstallAd the object containing the ad's assets
     *
     * @param adView             the view to be populated
     */
    private fun populateAppInstallAdView(nativeAppInstallAd: NativeAppInstallAd,
                                         adView: NativeAppInstallAdView) {
        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        val vc = nativeAppInstallAd.videoController

        // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
        // VideoController will call methods on this object when events occur in the video
        // lifecycle.
        vc.videoLifecycleCallbacks = object : VideoController.VideoLifecycleCallbacks() {
            override fun onVideoEnd() {
                // Publishers should allow native ads to complete video playback before refreshing
                // or replacing them with another ad in the same UI location.
                refresh_button.isEnabled = true
                videostatus_text.text = "Video status: Video playback has ended."
                super.onVideoEnd()
            }
        }

        adView.headlineView = adView.findViewById(R.id.appinstall_headline)
        adView.bodyView = adView.findViewById(R.id.appinstall_body)
        adView.callToActionView = adView.findViewById(R.id.appinstall_call_to_action)
        adView.iconView = adView.findViewById(R.id.appinstall_app_icon)
        adView.priceView = adView.findViewById(R.id.appinstall_price)
        adView.starRatingView = adView.findViewById(R.id.appinstall_stars)
        adView.storeView = adView.findViewById(R.id.appinstall_store)

        // The MediaView will display a video asset if one is present in the ad, and the first image
        // asset otherwise.
        adView.mediaView = adView.findViewById(R.id.appinstall_media)

        // Some assets are guaranteed to be in every NativeAppInstallAd.
        (adView.headlineView as TextView).text = nativeAppInstallAd.headline
        (adView.bodyView as TextView).text = nativeAppInstallAd.body
        (adView.callToActionView as Button).text = nativeAppInstallAd.callToAction
        (adView.iconView as ImageView).setImageDrawable(nativeAppInstallAd.icon.drawable)

        // Apps can check the VideoController's hasVideoContent property to determine if the
        // NativeAppInstallAd has a video asset.
        if (vc.hasVideoContent()) {
            // Kotlin doesn't include decimal-place formatting in its string interpolation, but
            // good ol' String.format works fine.
            videostatus_text.text = String.format(Locale.getDefault(),
                    "Video status: Ad contains a %.2f:1 video asset.",
                    vc.aspectRatio)
        } else {
            refresh_button.isEnabled = true
            videostatus_text.text = "Video status: Ad does not contain a video asset."
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
    }

    /**
     * Populates a [NativeContentAdView] object with data from a given
     * [NativeContentAd].
     *
     * @param nativeContentAd the object containing the ad's assets
     *
     * @param adView          the view to be populated
     */
    private fun populateContentAdView(nativeContentAd: NativeContentAd,
                                      adView: NativeContentAdView) {
        videostatus_text.text = "Video status: Ad does not contain a video asset."
        refresh_button.isEnabled = true

        adView.headlineView = adView.findViewById(R.id.contentad_headline)
        adView.imageView = adView.findViewById(R.id.contentad_image)
        adView.bodyView = adView.findViewById(R.id.contentad_body)
        adView.callToActionView = adView.findViewById(R.id.contentad_call_to_action)
        adView.logoView = adView.findViewById(R.id.contentad_logo)
        adView.advertiserView = adView.findViewById(R.id.contentad_advertiser)

        // Some assets are guaranteed to be in every NativeContentAd.
        (adView.headlineView as TextView).text = nativeContentAd.headline
        (adView.bodyView as TextView).text = nativeContentAd.body
        (adView.callToActionView as TextView).text = nativeContentAd.callToAction
        (adView.advertiserView as TextView).text = nativeContentAd.advertiser

        val images = nativeContentAd.images

        if (images.size > 0) {
            (adView.imageView as ImageView).setImageDrawable(images[0].drawable)
        }

        // Some aren't guaranteed, however, and should be checked.
        val logoImage = nativeContentAd.logo

        if (logoImage == null) {
            adView.logoView.visibility = View.INVISIBLE
        } else {
            (adView.logoView as ImageView).setImageDrawable(logoImage.drawable)
            adView.logoView.visibility = View.VISIBLE
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeContentAd)
    }

    /**
     * Populates a [View] object with data from a [NativeCustomTemplateAd]. This method
     * handles a particular "simple" custom native ad format.
     *
     * @param nativeCustomTemplateAd the object containing the ad's assets
     *
     * @param adView                 the view to be populated
     */
    private fun populateSimpleTemplateAdView(nativeCustomTemplateAd: NativeCustomTemplateAd,
                                             adView: View) {
        val headlineView = adView.findViewById<TextView>(R.id.simplecustom_headline)
        val captionView = adView.findViewById<TextView>(R.id.simplecustom_caption)

        headlineView.text = nativeCustomTemplateAd.getText("Headline")
        captionView.text = nativeCustomTemplateAd.getText("Caption")

        val mediaPlaceholder = adView.findViewById<FrameLayout>(R.id.simplecustom_media_placeholder)

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        val vc = nativeCustomTemplateAd.videoController

        // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
        // VideoController will call methods on this object when events occur in the video
        // lifecycle.
        vc.videoLifecycleCallbacks = object : VideoController.VideoLifecycleCallbacks() {
            override fun onVideoEnd() {
                // Publishers should allow native ads to complete video playback before refreshing
                // or replacing them with another ad in the same UI location.
                refresh_button.isEnabled = true
                videostatus_text.text = "Video status: Video playback has ended."
                super.onVideoEnd()
            }
        }

        // Apps can check the VideoController's hasVideoContent property to determine if the
        // NativeCustomTemplateAd has a video asset.
        if (vc.hasVideoContent()) {
            // Kotlin doesn't include decimal-place formatting in its string interpolation, but
            // good ol' String.format works fine.
            videostatus_text.text = String.format(Locale.getDefault(),
                    "Video status: Ad contains a %.2f:1 video asset.",
                    vc.aspectRatio)
        } else {
            val mainImage = ImageView(this)
            mainImage.adjustViewBounds = true
            mainImage.setImageDrawable(nativeCustomTemplateAd.getImage("MainImage").drawable)

            mainImage.setOnClickListener { nativeCustomTemplateAd.performClick("MainImage") }
            mediaPlaceholder.addView(mainImage)
            refresh_button.isEnabled = true
            videostatus_text.text = "Video status: Ad does not contain a video asset."
        }
    }

    /**
     * Creates a request for a new native ad based on the boolean parameters and calls the
     * corresponding "populate" method when one is successfully returned.
     *
     * @param requestAppInstallAds     indicates whether app install ads should be requested
     *
     * @param requestContentAds        indicates whether content ads should be requested
     *
     * @param requestCustomTemplateAds indicates whether custom template ads should be requested
     */
    private fun refreshAd(requestAppInstallAds: Boolean, requestContentAds: Boolean,
                          requestCustomTemplateAds: Boolean) {
        if (!requestAppInstallAds && !requestContentAds && !requestCustomTemplateAds) {
            Toast.makeText(this, "At least one ad format must be checked to request an ad.",
                    Toast.LENGTH_SHORT).show()
            return
        }

        refresh_button.isEnabled = true

        val builder = AdLoader.Builder(this, DFP_AD_UNIT_ID)

        if (requestAppInstallAds) {
            builder.forAppInstallAd { ad: NativeAppInstallAd ->
                val adView = layoutInflater
                        .inflate(R.layout.ad_app_install, ad_frame, false) as NativeAppInstallAdView
                populateAppInstallAdView(ad, adView)
                ad_frame.removeAllViews()
                ad_frame.addView(adView)
            }
        }

        if (requestContentAds) {
            builder.forContentAd { ad: NativeContentAd ->
                val adView = layoutInflater
                        .inflate(R.layout.ad_content, ad_frame, false) as NativeContentAdView
                populateContentAdView(ad, adView)
                ad_frame.removeAllViews()
                ad_frame.addView(adView)
            }
        }

        if (requestCustomTemplateAds) {
            builder.forCustomTemplateAd(SIMPLE_TEMPLATE_ID,
                    {
                        ad: NativeCustomTemplateAd ->
                        val frameLayout = findViewById<FrameLayout>(R.id.ad_frame)
                        val adView = layoutInflater
                                .inflate(R.layout.ad_simple_custom_template, null)
                        populateSimpleTemplateAdView(ad, adView)
                        frameLayout.removeAllViews()
                        frameLayout.addView(adView)
                    },
                    {
                        ad: NativeCustomTemplateAd, s: String ->
                        Toast.makeText(this@MainActivity,
                                "A custom click has occurred in the simple template",
                                Toast.LENGTH_SHORT).show();

                    })
        }

        val videoOptions = VideoOptions.Builder()
                .setStartMuted(start_muted_checkbox.isChecked)
                .build()

        val adOptions = NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build()

        builder.withNativeAdOptions(adOptions)

        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(errorCode: Int) {
                refresh_button.isEnabled = true
                Toast.makeText(this@MainActivity, "Failed to load native ad: $errorCode",
                        Toast.LENGTH_SHORT).show()
            }
        }).build()

        adLoader.loadAd(PublisherAdRequest.Builder().build())

        videostatus_text.text = ""
    }
}
