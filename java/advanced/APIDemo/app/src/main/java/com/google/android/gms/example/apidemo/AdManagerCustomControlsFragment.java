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
import androidx.fragment.app.Fragment;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MediaContent;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.android.gms.ads.nativead.NativeCustomFormatAd;
import com.google.android.gms.ads.nativead.NativeCustomFormatAd.OnCustomClickListener;
import com.google.android.gms.ads.nativead.NativeCustomFormatAd.OnCustomFormatAdLoadedListener;
import java.util.Locale;

/**
 * The {@link AdManagerCustomControlsFragment} class demonstrates how to use custom controls with Ad
 * Manager custom ad format video ads.
 */
public class AdManagerCustomControlsFragment extends Fragment {

  private Button refresh;
  private CheckBox startVideoAdsMuted;
  private CheckBox customControlsCheckbox;
  private CheckBox nativeAdsCheckbox;
  private CheckBox customFormatAdsCheckbox;
  private CustomControlsView customControlsView;
  private NativeAd nativeAd;
  private NativeCustomFormatAd nativeCustomFormatAd;

  public AdManagerCustomControlsFragment() {
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_gam_customcontrols, container, false);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    refresh = getView().findViewById(R.id.btn_refresh);
    startVideoAdsMuted = getView().findViewById(R.id.cb_start_muted);
    customControlsCheckbox = getView().findViewById(R.id.cb_custom_controls);
    nativeAdsCheckbox = getView().findViewById(R.id.cb_native);
    customFormatAdsCheckbox = getView().findViewById(R.id.cb_custom_format);
    customControlsView = getView().findViewById(R.id.custom_controls);

    refresh.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View unusedView) {
        refreshAd();
      }
    });

    refreshAd();
  }

  @Override
  public void onDestroy() {
    if (nativeAd != null) {
      nativeAd.destroy();
      nativeAd = null;
    }
    if (nativeCustomFormatAd != null) {
      nativeCustomFormatAd.destroy();
      nativeCustomFormatAd = null;
    }
    super.onDestroy();
  }

  /**
   * Populates a {@link NativeAdView} object with data from a given {@link NativeAd}.
   *
   * @param nativeAd the object containing the ad's assets
   * @param adView the view to be populated
   */
  private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
    adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
    adView.setBodyView(adView.findViewById(R.id.ad_body));
    adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
    adView.setIconView(adView.findViewById(R.id.ad_app_icon));
    adView.setPriceView(adView.findViewById(R.id.ad_price));
    adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
    adView.setStoreView(adView.findViewById(R.id.ad_store));

    // Some assets are guaranteed to be in every NativeAd.
    ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
    ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
    ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
    ((ImageView) adView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());

    // These assets aren't guaranteed to be in every NativeAd, so it's important to check
    // before trying to display them.
    if (nativeAd.getPrice() == null) {
      adView.getPriceView().setVisibility(View.INVISIBLE);
    } else {
      adView.getPriceView().setVisibility(View.VISIBLE);
      ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
    }

    if (nativeAd.getStore() == null) {
      adView.getStoreView().setVisibility(View.INVISIBLE);
    } else {
      adView.getStoreView().setVisibility(View.VISIBLE);
      ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
    }

    if (nativeAd.getStarRating() == null) {
      adView.getStarRatingView().setVisibility(View.INVISIBLE);
    } else {
      ((RatingBar) adView.getStarRatingView())
          .setRating(nativeAd.getStarRating().floatValue());
      adView.getStarRatingView().setVisibility(View.VISIBLE);
    }

    // Assign native ad object to the native view.
    adView.setNativeAd(nativeAd);

    // Set up the custom video controls functionality.
    MediaContent mediaContent = nativeAd.getMediaContent();
    if (mediaContent != null) {
      customControlsView.setMediaContent(mediaContent);
    }

    refresh.setEnabled(true);
  }

  /**
   * Populates a {@link View} object with data from a {@link NativeCustomFormatAd}. This method
   * handles a particular "simple" custom native ad format.
   *
   * @param nativeCustomFormatAd the object containing the ad's assets
   * @param adView the view to be populated
   */
  private void populateSimpleTemplateAdView(final NativeCustomFormatAd nativeCustomFormatAd,
      View adView) {
    TextView headline = adView.findViewById(R.id.simplecustom_headline);
    TextView caption = adView.findViewById(R.id.simplecustom_caption);

    headline.setText(nativeCustomFormatAd.getText("Headline"));
    caption.setText(nativeCustomFormatAd.getText("Caption"));

    headline.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View unusedView) {
        nativeCustomFormatAd.performClick("Headline");
      }
    });

    FrameLayout mediaPlaceholder = adView.findViewById(R.id.simplecustom_media_placeholder);

    // Apps can check the MediaContent's hasVideoContent property to determine if the
    // NativeCustomTemplateAd has a video asset.
    if (nativeCustomFormatAd.getMediaContent() != null
        && nativeCustomFormatAd.getMediaContent().hasVideoContent()) {
      MediaView mediaView = new MediaView(getActivity());
      mediaView.setMediaContent(nativeCustomFormatAd.getMediaContent());
      mediaPlaceholder.addView(mediaView);
    } else {
      ImageView mainImage = new ImageView(getActivity());
      mainImage.setAdjustViewBounds(true);
      mainImage.setImageDrawable(nativeCustomFormatAd.getImage("MainImage").getDrawable());

      mainImage.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View unusedView) {
          nativeCustomFormatAd.performClick("MainImage");
        }
      });
      mediaPlaceholder.addView(mainImage);
    }
    customControlsView.setMediaContent(nativeCustomFormatAd.getMediaContent());

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

    if (customFormatAdsCheckbox.isChecked()) {
      builder.forCustomFormatAd(
          resources.getString(R.string.customcontrols_fragment_template_id),
          new OnCustomFormatAdLoadedListener() {
            @Override
            public void onCustomFormatAdLoaded(NativeCustomFormatAd ad) {
              if (isDetached()) {
                ad.destroy();
                return;
              }
              if (nativeCustomFormatAd != null) {
                nativeCustomFormatAd.destroy();
              }
              nativeCustomFormatAd = ad;
              FrameLayout frameLayout = getView().findViewById(R.id.fl_adplaceholder);
              View adView =
                  getLayoutInflater()
                      .inflate(R.layout.ad_simple_custom_template, frameLayout, false);
              populateSimpleTemplateAdView(ad, adView);
              frameLayout.removeAllViews();
              frameLayout.addView(adView);
            }
          },
          new OnCustomClickListener() {
            @Override
            public void onCustomClick(NativeCustomFormatAd ad, String assetName) {
              Toast.makeText(
                      getActivity(),
                      "A custom click has occurred on asset: " + assetName,
                      Toast.LENGTH_SHORT)
                  .show();
            }
          });
    }

    if (nativeAdsCheckbox.isChecked()) {
      builder.forNativeAd(
          new NativeAd.OnNativeAdLoadedListener() {
            @Override
            public void onNativeAdLoaded(NativeAd ad) {
              if (isDetached()) {
                ad.destroy();
                return;
              }
              if (nativeAd != null) {
                nativeAd.destroy();
              }
              nativeAd = ad;
              FrameLayout frameLayout = getView().findViewById(R.id.fl_adplaceholder);
              NativeAdView adView =
                  (NativeAdView)
                      getLayoutInflater().inflate(R.layout.native_ad, frameLayout, false);
              populateNativeAdView(ad, adView);
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

    AdLoader adLoader =
        builder
            .withAdListener(
                new AdListener() {
                  @Override
                  public void onAdFailedToLoad(LoadAdError loadAdError) {
                    refresh.setEnabled(true);
                    String error =
                        String.format(
                            Locale.getDefault(),
                            "domain: %s, code: %d, message: %s",
                            loadAdError.getDomain(),
                            loadAdError.getCode(),
                            loadAdError.getMessage());
                    Toast.makeText(
                            getActivity(), "Failed to load native ad: " + error, Toast.LENGTH_SHORT)
                        .show();
                  }
                })
            .build();
    adLoader.loadAd(new AdManagerAdRequest.Builder().build());

    customControlsView.reset();
  }
}




