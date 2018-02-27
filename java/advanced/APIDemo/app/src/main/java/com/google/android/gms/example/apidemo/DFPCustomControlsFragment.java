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
package com.google.android.gms.example.apidemo;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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


/**
 * The {@link DFPCustomControlsFragment} class demonstrates how to use custom controls with DFP
 * custom template video ads.
 */
public class DFPCustomControlsFragment extends Fragment {

    private Button refresh;
    private CheckBox startVideoAdsMuted;
    private CheckBox customControlsCheckbox;
    private CheckBox appInstallAdsCheckbox;
    private CheckBox contentAdsCheckbox;
    private CheckBox customTemplateAdsCheckbox;
    private CustomControlsView customControlsView;

    public DFPCustomControlsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dfp_customcontrols, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        refresh = getView().findViewById(R.id.btn_refresh);
        startVideoAdsMuted = getView().findViewById(R.id.cb_start_muted);
        customControlsCheckbox = getView().findViewById(R.id.cb_custom_controls);
        appInstallAdsCheckbox = getView().findViewById(R.id.cb_appinstall);
        contentAdsCheckbox = getView().findViewById(R.id.cb_content);
        customTemplateAdsCheckbox = getView().findViewById(R.id.cb_customtemplate);
        customControlsView = getView().findViewById(R.id.custom_controls);

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View unusedView) {
                refreshAd();
            }
        });

        refreshAd();
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
        VideoController videoController = nativeAppInstallAd.getVideoController();

        MediaView mediaView = adView.findViewById(R.id.appinstall_media);
        ImageView mainImageView = adView.findViewById(R.id.appinstall_image);

        // Apps can check the VideoController's hasVideoContent property to determine if the
        // NativeAppInstallAd has a video asset.
        if (videoController.hasVideoContent()) {
            mainImageView.setVisibility(View.GONE);
            adView.setMediaView(mediaView);
        } else {
            mediaView.setVisibility(View.GONE);
            adView.setImageView(mainImageView);

            // At least one image is guaranteed.
            List<NativeAd.Image> images = nativeAppInstallAd.getImages();
            mainImageView.setImageDrawable(images.get(0).getDrawable());
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

        customControlsView.setVideoController(videoController);

        refresh.setEnabled(true);
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
        VideoController videoController = nativeContentAd.getVideoController();

        MediaView mediaView = adView.findViewById(R.id.contentad_media);
        ImageView mainImageView = adView.findViewById(R.id.contentad_image);

        // Apps can check the VideoController's hasVideoContent property to determine if the
        // NativeContentAd has a video asset.
        if (videoController.hasVideoContent()) {
            mainImageView.setVisibility(View.GONE);
            adView.setMediaView(mediaView);
        } else {
            mediaView.setVisibility(View.GONE);
            adView.setImageView(mainImageView);

            // At least one image is guaranteed.
            List<NativeAd.Image> images = nativeContentAd.getImages();
            mainImageView.setImageDrawable(images.get(0).getDrawable());
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
        customControlsView.setVideoController(videoController);

        refresh.setEnabled(true);
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

        headline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View unusedView) {
                nativeCustomTemplateAd.performClick("Headline");
            }
        });

        FrameLayout mediaPlaceholder = adView.findViewById(R.id.simplecustom_media_placeholder);

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        VideoController vc = nativeCustomTemplateAd.getVideoController();

        // Apps can check the VideoController's hasVideoContent property to determine if the
        // NativeCustomTemplateAd has a video asset.
        if (vc.hasVideoContent()) {
            mediaPlaceholder.addView(nativeCustomTemplateAd.getVideoMediaView());
        } else {
            ImageView mainImage = new ImageView(getActivity());
            mainImage.setAdjustViewBounds(true);
            mainImage.setImageDrawable(nativeCustomTemplateAd.getImage("MainImage").getDrawable());

            mainImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View unusedView) {
                    nativeCustomTemplateAd.performClick("MainImage");
                }
            });
            mediaPlaceholder.addView(mainImage);
        }
        customControlsView.setVideoController(vc);

        refresh.setEnabled(true);
    }


    /**
     * Creates a request for a new native ad based on the boolean parameters and calls the
     * corresponding "populate" method when one is successfully returned.
     */
    private void refreshAd() {
        refresh.setEnabled(false);

        Resources resources = getActivity().getResources();
        AdLoader.Builder builder = new AdLoader.Builder(getActivity(),
                resources.getString(R.string.customcontrols_fragment_ad_unit_id));

        if (customTemplateAdsCheckbox.isChecked()) {
            builder.forCustomTemplateAd(
                    resources.getString(R.string.customcontrols_fragment_template_id),
                    new NativeCustomTemplateAd.OnCustomTemplateAdLoadedListener() {
                        @Override
                        public void onCustomTemplateAdLoaded(NativeCustomTemplateAd ad) {
                            FrameLayout frameLayout = getView().findViewById(R.id.fl_adplaceholder);
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
                            Toast.makeText(getActivity(),
                                    "A custom click has occurred in the simple template",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }

        if (appInstallAdsCheckbox.isChecked()) {
            builder.forAppInstallAd(new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
                @Override
                public void onAppInstallAdLoaded(NativeAppInstallAd ad) {
                    FrameLayout frameLayout = getView().findViewById(R.id.fl_adplaceholder);
                    NativeAppInstallAdView adView = (NativeAppInstallAdView) getLayoutInflater()
                            .inflate(R.layout.ad_app_install, null);
                    populateAppInstallAdView(ad, adView);
                    frameLayout.removeAllViews();
                    frameLayout.addView(adView);
                }
            });
        }

        if (contentAdsCheckbox.isChecked()) {
            builder.forContentAd(new NativeContentAd.OnContentAdLoadedListener() {
                @Override
                public void onContentAdLoaded(NativeContentAd ad) {
                    FrameLayout frameLayout = getView().findViewById(R.id.fl_adplaceholder);
                    NativeContentAdView adView = (NativeContentAdView) getLayoutInflater()
                            .inflate(R.layout.ad_content, null);
                    populateContentAdView(ad, adView);
                    frameLayout.removeAllViews();
                    frameLayout.addView(adView);
                }
            });
        }

        VideoOptions videoOptions = new VideoOptions.Builder()
                .setStartMuted(startVideoAdsMuted.isChecked())
                .setCustomControlsRequested(customControlsCheckbox.isChecked())
                .build();

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(videoOptions)
                .build();

        builder.withNativeAdOptions(adOptions);

        AdLoader adLoader = builder.withAdListener(new AdListener() {
            @Override
            public void onAdFailedToLoad(int errorCode) {
                refresh.setEnabled(true);
                Toast.makeText(getActivity(), "Failed to load native ad: "
                        + errorCode, Toast.LENGTH_SHORT).show();
            }
        }).build();

        adLoader.loadAd(new PublisherAdRequest.Builder().build());

        customControlsView.reset();
    }
}



