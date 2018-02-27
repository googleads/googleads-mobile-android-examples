/*
 * Copyright (C) 2015 Google, Inc.
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

package com.google.example.gms.customrenderingexample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;
import com.google.android.gms.ads.formats.NativeCustomTemplateAd;

import java.util.List;
import java.util.Locale;

/**
 * A simple activity class that displays native ad formats.
 */
public class MainActivity extends AppCompatActivity {

    private static final String DFP_AD_UNIT_ID = "/6499/example/native";
    private static final String SIMPLE_TEMPLATE_ID = "10104090";

    private Button refresh;
    private CheckBox requestAppInstallAds;
    private CheckBox requestContentAds;
    private CheckBox requestCustomTemplateAds;
    private CheckBox startVideoAdsMuted;
    private TextView videoStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        refresh = findViewById(R.id.btn_refresh);
        requestAppInstallAds = findViewById(R.id.cb_appinstall);
        requestContentAds = findViewById(R.id.cb_content);
        requestCustomTemplateAds = findViewById(R.id.cb_customtemplate);
        startVideoAdsMuted = findViewById(R.id.cb_start_muted);
        videoStatus = findViewById(R.id.tv_video_status);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshAd(requestAppInstallAds.isChecked(),
                        requestContentAds.isChecked(),
                        requestCustomTemplateAds.isChecked());
            }
        });

        refreshAd(true, true, true);
    }

    /**
     * Populates a {@link NativeAppInstallAdView} object with data from a given
     * {@link NativeAppInstallAd}.
     *
     * @param nativeAppInstallAd the object containing the ad's assets
     * @param adView             the view to be populated
     */
    private void populateAppInstallAdView(NativeAppInstallAd nativeAppInstallAd,
                                          NativeAppInstallAdView adView) {
        adView.setHeadlineView(adView.findViewById(R.id.appinstall_headline));
        adView.setBodyView(adView.findViewById(R.id.appinstall_body));
        adView.setCallToActionView(adView.findViewById(R.id.appinstall_call_to_action));
        adView.setIconView(adView.findViewById(R.id.appinstall_app_icon));
        adView.setPriceView(adView.findViewById(R.id.appinstall_price));
        adView.setStarRatingView(adView.findViewById(R.id.appinstall_stars));
        adView.setStoreView(adView.findViewById(R.id.appinstall_store));

        // Some assets are guaranteed to be in every NativeAppInstallAd.
        ((TextView) adView.getHeadlineView()).setText(nativeAppInstallAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeAppInstallAd.getBody());
        ((Button) adView.getCallToActionView()).setText(nativeAppInstallAd.getCallToAction());
        ((ImageView) adView.getIconView()).setImageDrawable(nativeAppInstallAd.getIcon()
                .getDrawable());

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = nativeAppInstallAd.getVideoController();

        MediaView mediaView = adView.findViewById(R.id.appinstall_media);
        ImageView mainImageView = adView.findViewById(R.id.appinstall_image);

        // Apps can check the VideoController's hasVideoContent property to determine if the
        // NativeAppInstallAd has a video asset.
        if (vc.hasVideoContent()) {
            mainImageView.setVisibility(View.GONE);
            adView.setMediaView(mediaView);

            videoStatus.setText(String.format(Locale.getDefault(),
                    "Video status: Ad contains a %.2f:1 video asset.",
                    vc.getAspectRatio()));

            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                public void onVideoEnd() {
                    // Publishers should allow native ads to complete video playback before
                    // refreshing or replacing them with another ad in the same UI location.
                    refresh.setEnabled(true);
                    videoStatus.setText("Video status: Video playback has ended.");
                    super.onVideoEnd();
                }
            });
        } else {
            mediaView.setVisibility(View.GONE);
            adView.setImageView(mainImageView);

            // At least one image is guaranteed.
            List<NativeAd.Image> images = nativeAppInstallAd.getImages();
            mainImageView.setImageDrawable(images.get(0).getDrawable());

            refresh.setEnabled(true);
            videoStatus.setText("Video status: Ad does not contain a video asset.");
        }

        // These assets aren't guaranteed to be in every NativeAppInstallAd, so it's important to
        // check before trying to display them.
        if (nativeAppInstallAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAppInstallAd.getPrice());
        }

        if (nativeAppInstallAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAppInstallAd.getStore());
        }

        if (nativeAppInstallAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                    .setRating(nativeAppInstallAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }


        // Assign native ad object to the native view.
        adView.setNativeAd(nativeAppInstallAd);
    }

    /**
     * Populates a {@link NativeContentAdView} object with data from a given
     * {@link NativeContentAd}.
     *
     * @param nativeContentAd the object containing the ad's assets
     * @param adView          the view to be populated
     */
    private void populateContentAdView(NativeContentAd nativeContentAd,
                                       NativeContentAdView adView) {
        adView.setHeadlineView(adView.findViewById(R.id.contentad_headline));
        adView.setBodyView(adView.findViewById(R.id.contentad_body));
        adView.setCallToActionView(adView.findViewById(R.id.contentad_call_to_action));
        adView.setLogoView(adView.findViewById(R.id.contentad_logo));
        adView.setAdvertiserView(adView.findViewById(R.id.contentad_advertiser));

        // Some assets are guaranteed to be in every NativeContentAd.
        ((TextView) adView.getHeadlineView()).setText(nativeContentAd.getHeadline());
        ((TextView) adView.getBodyView()).setText(nativeContentAd.getBody());
        ((TextView) adView.getCallToActionView()).setText(nativeContentAd.getCallToAction());
        ((TextView) adView.getAdvertiserView()).setText(nativeContentAd.getAdvertiser());

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = nativeContentAd.getVideoController();

        MediaView mediaView = adView.findViewById(R.id.contentad_media);
        ImageView mainImageView = adView.findViewById(R.id.contentad_image);

        // Apps can check the VideoController's hasVideoContent property to determine if the
        // NativeContentAd has a video asset.
        if (vc.hasVideoContent()) {
            mainImageView.setVisibility(View.GONE);
            adView.setMediaView(mediaView);

            videoStatus.setText(String.format(Locale.getDefault(),
                    "Video status: Ad contains a %.2f:1 video asset.",
                    vc.getAspectRatio()));

            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                public void onVideoEnd() {
                    // Publishers should allow native ads to complete video playback before
                    // refreshing or replacing them with another ad in the same UI location.
                    refresh.setEnabled(true);
                    videoStatus.setText("Video status: Video playback has ended.");
                    super.onVideoEnd();
                }
            });
        } else {
            mediaView.setVisibility(View.GONE);
            adView.setImageView(mainImageView);

            // At least one image is guaranteed.
            List<NativeAd.Image> images = nativeContentAd.getImages();
            mainImageView.setImageDrawable(images.get(0).getDrawable());

            refresh.setEnabled(true);
            videoStatus.setText("Video status: Ad does not contain a video asset.");
        }

        // These assets aren't guaranteed to be in every NativeContentAd, so it's important to
        // check before trying to display them.
        NativeAd.Image logoImage = nativeContentAd.getLogo();

        if (logoImage == null) {
            adView.getLogoView().setVisibility(View.INVISIBLE);
        } else {
            ((ImageView) adView.getLogoView()).setImageDrawable(logoImage.getDrawable());
            adView.getLogoView().setVisibility(View.VISIBLE);
        }

        // Assign native ad object to the native view.
        adView.setNativeAd(nativeContentAd);
    }

    /**
     * Populates a {@link View} object with data from a {@link NativeCustomTemplateAd}. This method
     * handles a particular "simple" custom native ad format.
     *
     * @param nativeCustomTemplateAd the object containing the ad's assets
     * @param adView                 the view to be populated
     */
    private void populateSimpleTemplateAdView(final NativeCustomTemplateAd nativeCustomTemplateAd,
                                              View adView) {
        TextView headline = adView.findViewById(R.id.simplecustom_headline);
        TextView caption = adView.findViewById(R.id.simplecustom_caption);

        headline.setText(nativeCustomTemplateAd.getText("Headline"));
        caption.setText(nativeCustomTemplateAd.getText("Caption"));

        FrameLayout mediaPlaceholder = adView.findViewById(R.id.simplecustom_media_placeholder);

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = nativeCustomTemplateAd.getVideoController();

        // Apps can check the VideoController's hasVideoContent property to determine if the
        // NativeCustomTemplateAd has a video asset.
        if (vc.hasVideoContent()) {
            mediaPlaceholder.addView(nativeCustomTemplateAd.getVideoMediaView());
            videoStatus.setText(String.format(Locale.getDefault(),
                    "Video status: Ad contains a %.2f:1 video asset.",
                    vc.getAspectRatio()));

            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                public void onVideoEnd() {
                    // Publishers should allow native ads to complete video playback before
                    // refreshing or replacing them with another ad in the same UI location.
                    refresh.setEnabled(true);
                    videoStatus.setText("Video status: Video playback has ended.");
                    super.onVideoEnd();
                }
            });
        } else {
            ImageView mainImage = new ImageView(this);
            mainImage.setAdjustViewBounds(true);
            mainImage.setImageDrawable(nativeCustomTemplateAd.getImage("MainImage").getDrawable());

            mainImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    nativeCustomTemplateAd.performClick("MainImage");
                }
            });
            mediaPlaceholder.addView(mainImage);
            refresh.setEnabled(true);
            videoStatus.setText("Video status: Ad does not contain a video asset.");
        }
    }

    /**
     * Creates a request for a new native ad based on the boolean parameters and calls the
     * corresponding "populate" method when one is successfully returned.
     *
     * @param requestAppInstallAds     indicates whether app install ads should be requested
     * @param requestContentAds        indicates whether content ads should be requested
     * @param requestCustomTemplateAds indicates whether custom template ads should be requested
     */
    private void refreshAd(boolean requestAppInstallAds, boolean requestContentAds,
                           boolean requestCustomTemplateAds) {
        if (!requestAppInstallAds && !requestContentAds && !requestCustomTemplateAds) {
            Toast.makeText(this, "At least one ad format must be checked to request an ad.",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        refresh.setEnabled(false);

        AdLoader.Builder builder = new AdLoader.Builder(this, DFP_AD_UNIT_ID);

        if (requestAppInstallAds) {
            builder.forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
                @Override
                public void onAppInstallAdLoaded(NativeAppInstallAd ad) {
                    FrameLayout frameLayout = findViewById(R.id.fl_adplaceholder);
                    NativeAppInstallAdView adView = (NativeAppInstallAdView) getLayoutInflater()
                            .inflate(R.layout.ad_app_install, null);
                    populateAppInstallAdView(ad, adView);
                    frameLayout.removeAllViews();
                    frameLayout.addView(adView);
                }
            });
        }

        if (requestContentAds) {
            builder.forContentAd(new NativeContentAd.OnContentAdLoadedListener() {
                @Override
                public void onContentAdLoaded(NativeContentAd ad) {
                    FrameLayout frameLayout = findViewById(R.id.fl_adplaceholder);
                    NativeContentAdView adView = (NativeContentAdView) getLayoutInflater()
                            .inflate(R.layout.ad_content, null);
                    populateContentAdView(ad, adView);
                    frameLayout.removeAllViews();
                    frameLayout.addView(adView);
                }
            });
        }

        if (requestCustomTemplateAds) {
            builder.forCustomTemplateAd(SIMPLE_TEMPLATE_ID,
                    new NativeCustomTemplateAd.OnCustomTemplateAdLoadedListener() {
                        @Override
                        public void onCustomTemplateAdLoaded(NativeCustomTemplateAd ad) {
                            FrameLayout frameLayout = findViewById(R.id.fl_adplaceholder);
                            View adView = getLayoutInflater()
                                    .inflate(R.layout.ad_simple_custom_template, null);
                            populateSimpleTemplateAdView(ad, adView);
                            frameLayout.removeAllViews();
                            frameLayout.addView(adView);
                        }
                    },
                    new NativeCustomTemplateAd.OnCustomClickListener() {
                        @Override
                        public void onCustomClick(NativeCustomTemplateAd ad, String s) {
                            Toast.makeText(MainActivity.this,
                                    "A custom click has occurred in the simple template",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(startVideoAdsMuted.isChecked())
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();

        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                refresh.setEnabled(true);
                Toast.makeText(MainActivity.this, "Failed to load native ad: "
                        + errorCode, Toast.LENGTH_SHORT).show();
            }
        }).build();

        adLoader.loadAd(new PublisherAdRequest.Builder().build());

        videoStatus.setText("");
    }
}
