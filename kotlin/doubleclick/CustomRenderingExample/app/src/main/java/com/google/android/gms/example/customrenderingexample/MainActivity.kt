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
            refreshAd(nativeads_checkbox.isChecked,
                    customtemplate_checkbox.isChecked)
        }

        refreshAd(nativeads_checkbox.isChecked,
                customtemplate_checkbox.isChecked)
    }

    /**
     * Populates a [UnifiedNativeAdView] object with data from a given
     * [UnifiedNativeAd].
     *
     * @param nativeAd the object containing the ad's assets
     * @param adView          the view to be populated
     */
    private fun populateUnifiedNativeAdView(nativeAd: UnifiedNativeAd, adView: UnifiedNativeAdView) {
        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        val vc = nativeAd.videoController

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

        val mediaView = adView.findViewById<MediaView>(R.id.ad_media)
        val mainImageView = adView.findViewById<ImageView>(R.id.ad_image)

        // Apps can check the VideoController's hasVideoContent property to determine if the
        // NativeAppInstallAd has a video asset.
        if (vc.hasVideoContent()) {
            adView.mediaView = mediaView
            mainImageView.visibility = View.GONE
            videostatus_text.text = String.format(Locale.getDefault(),
                    "Video status: Ad contains a %.2f:1 video asset.",
                    vc.aspectRatio)
        } else {
            adView.imageView = mainImageView
            mediaView.visibility = View.GONE

            val images = nativeAd.images
            if (images.size > 0) {
                mainImageView.setImageDrawable(images[0].drawable)
            }

            refresh_button.isEnabled = true
            videostatus_text.text = "Video status: Ad does not contain a video asset."
        }

        adView.headlineView = adView.findViewById(R.id.ad_headline)
        adView.bodyView = adView.findViewById(R.id.ad_body)
        adView.callToActionView = adView.findViewById(R.id.ad_call_to_action)
        adView.iconView = adView.findViewById(R.id.ad_app_icon)
        adView.priceView = adView.findViewById(R.id.ad_price)
        adView.starRatingView = adView.findViewById(R.id.ad_stars)
        adView.storeView = adView.findViewById(R.id.ad_store)
        adView.advertiserView = adView.findViewById(R.id.ad_advertiser)

        // Some assets are guaranteed to be in every UnifiedNativeAd.
        (adView.headlineView as TextView).text = nativeAd.headline
        (adView.bodyView as TextView).text = nativeAd.body
        (adView.callToActionView as Button).text = nativeAd.callToAction

        // These assets aren't guaranteed to be in every UnifiedNativeAd, so it's important to
        // check before trying to display them.
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

        if (nativeAd.advertiser == null) {
            adView.advertiserView.visibility = View.INVISIBLE
        } else {
            (adView.advertiserView as TextView).text = nativeAd.advertiser
            adView.advertiserView.visibility = View.VISIBLE
        }

        adView.setNativeAd(nativeAd)
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
     * @param requestUnifiedNativeAds  indicates whether unified native ads should be requested
     *
     * @param requestCustomTemplateAds indicates whether custom template ads should be requested
     */
    private fun refreshAd(requestUnifiedNativeAds: Boolean,
                          requestCustomTemplateAds: Boolean) {
        if (!requestUnifiedNativeAds && !requestCustomTemplateAds) {
            Toast.makeText(this, "At least one ad format must be checked to request an ad.",
                    Toast.LENGTH_SHORT).show()
            return
        }

        refresh_button.isEnabled = true

        val builder = AdLoader.Builder(this, DFP_AD_UNIT_ID)

        if (requestUnifiedNativeAds) {
            builder.forUnifiedNativeAd { unifiedNativeAd ->
                val adView = layoutInflater
                        .inflate(R.layout.ad_unified, null) as UnifiedNativeAdView
                populateUnifiedNativeAdView(unifiedNativeAd, adView)
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
