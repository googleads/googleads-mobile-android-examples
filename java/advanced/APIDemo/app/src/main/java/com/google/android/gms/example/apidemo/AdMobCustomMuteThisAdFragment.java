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
package com.google.android.gms.example.apidemo;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MediaContent;
import com.google.android.gms.ads.MuteThisAdListener;
import com.google.android.gms.ads.MuteThisAdReason;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@link AdMobCustomMuteThisAdFragment} class demonstrates how to use custom mute with AdMob
 * native ads.
 */
public class AdMobCustomMuteThisAdFragment extends Fragment {

  private Button refresh;
  private Button muteButton;
  private NativeAd nativeAd;
  private FrameLayout adContainer;

  public AdMobCustomMuteThisAdFragment() {
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_admob_custom_mute_this_ad, container, false);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    refresh = getView().findViewById(R.id.btn_refresh);
    muteButton = getView().findViewById(R.id.btn_mute_ad);
    adContainer = getView().findViewById(R.id.fl_adplaceholder);

    refresh.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View unusedView) {
        refreshAd();
      }
    });
    muteButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View unusedView) {
        showMuteReasonsDialog();
      }
    });

    refreshAd();
  }

  /**
   * Populates a {@link NativeAdView} object with data from a given {@link NativeAd}.
   *
   * @param nativeAd the object containing the ad's assets
   * @param adView the view to be populated
   */
  private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
    adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));

    adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
    adView.setBodyView(adView.findViewById(R.id.ad_body));
    adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
    adView.setIconView(adView.findViewById(R.id.ad_app_icon));
    adView.setPriceView(adView.findViewById(R.id.ad_price));
    adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
    adView.setStoreView(adView.findViewById(R.id.ad_store));
    adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

    ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
    adView.getMediaView().setMediaContent(nativeAd.getMediaContent());

    if (nativeAd.getBody() == null) {
      adView.getBodyView().setVisibility(View.INVISIBLE);
    } else {
      ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
      adView.getBodyView().setVisibility(View.VISIBLE);
    }

    if (nativeAd.getCallToAction() == null) {
      adView.getCallToActionView().setVisibility(View.INVISIBLE);
    } else {
      ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
      adView.getCallToActionView().setVisibility(View.VISIBLE);
    }

    if (nativeAd.getIcon() == null) {
      adView.getIconView().setVisibility(View.GONE);
    } else {
      ((ImageView) adView.getIconView()).setImageDrawable(
          nativeAd.getIcon().getDrawable());
      adView.getIconView().setVisibility(View.VISIBLE);
    }

    if (nativeAd.getPrice() == null) {
      adView.getPriceView().setVisibility(View.INVISIBLE);
    } else {
      ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
      adView.getPriceView().setVisibility(View.VISIBLE);
    }

    if (nativeAd.getStore() == null) {
      adView.getStoreView().setVisibility(View.INVISIBLE);
    } else {
      ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
      adView.getStoreView().setVisibility(View.VISIBLE);
    }

    if (nativeAd.getStarRating() == null) {
      adView.getStarRatingView().setVisibility(View.INVISIBLE);
    } else {
      ((RatingBar) adView.getStarRatingView())
          .setRating(nativeAd.getStarRating().floatValue());
      adView.getStarRatingView().setVisibility(View.VISIBLE);
    }

    if (nativeAd.getAdvertiser() == null) {
      adView.getAdvertiserView().setVisibility(View.INVISIBLE);
    } else {
      ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
      adView.getAdvertiserView().setVisibility(View.VISIBLE);
    }

    adView.setNativeAd(nativeAd);

    MediaContent mediaContent = nativeAd.getMediaContent();
    if (mediaContent != null && mediaContent.hasVideoContent()) {
      mediaContent.getVideoController().setVideoLifecycleCallbacks(
          new VideoController.VideoLifecycleCallbacks() {
            @Override
            public void onVideoEnd() {
              refresh.setEnabled(true);
              super.onVideoEnd();
            }
          });
    } else {
      refresh.setEnabled(true);
    }
  }

  /**
   * Creates a request for a new native ad based on the boolean parameters and calls the
   * "populateNativeAdView" method when one is successfully returned.
   */
  private void refreshAd() {
    refresh.setEnabled(false);
    muteButton.setEnabled(false);

    Resources resources = getActivity().getResources();
    AdLoader.Builder builder = new AdLoader.Builder(getActivity(),
        resources.getString(R.string.custommute_fragment_ad_unit_id));

    builder.forNativeAd(
        new NativeAd.OnNativeAdLoadedListener() {
          // OnNativeAdLoadedListener implementation.
          @Override
          public void onNativeAdLoaded(NativeAd nativeAd) {
            AdMobCustomMuteThisAdFragment.this.nativeAd = nativeAd;
            muteButton.setEnabled(nativeAd.isCustomMuteThisAdEnabled());
            nativeAd.setMuteThisAdListener(
                new MuteThisAdListener() {
                  @Override
                  public void onAdMuted() {
                    muteAd();
                    Toast.makeText(getActivity(), "Ad muted", Toast.LENGTH_SHORT).show();
                  }
                });

            NativeAdView adView =
                (NativeAdView) getLayoutInflater().inflate(R.layout.native_ad, adContainer, false);
            populateNativeAdView(nativeAd, adView);
            adContainer.removeAllViews();
            adContainer.addView(adView);
          }
        });

    NativeAdOptions adOptions = new NativeAdOptions.Builder()
        .setRequestCustomMuteThisAd(true)
        .build();

    builder.withNativeAdOptions(adOptions);

    AdLoader adLoader = builder.withAdListener(new AdListener() {
      @Override
      public void onAdFailedToLoad(LoadAdError error) {
        refresh.setEnabled(true);
        Toast.makeText(getActivity(), "Failed to load native ad: " + error,
            Toast.LENGTH_SHORT).show();
      }
    }).build();

    adLoader.loadAd(new AdRequest.Builder().build());
  }

  private void showMuteReasonsDialog() {
    class MuteThisAdReasonWrapper {

      MuteThisAdReason reason;

      MuteThisAdReasonWrapper(MuteThisAdReason reason) {
        this.reason = reason;
      }

      @Override
      public String toString() {
        return reason.getDescription();
      }
    }

    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    builder.setTitle("Select a reason");
    final List<MuteThisAdReason> reasons = nativeAd.getMuteThisAdReasons();
    final List<MuteThisAdReasonWrapper> wrappedReasons = new ArrayList<>();
    for (MuteThisAdReason reason : reasons) {
      wrappedReasons.add(new MuteThisAdReasonWrapper(reason));
    }

    builder.setAdapter(
        new ArrayAdapter<>(getActivity(),
            android.R.layout.simple_list_item_1, wrappedReasons),
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int which) {
            dialog.dismiss();
            muteAdDialogDidSelectReason(wrappedReasons.get(which).reason);
          }
        });

    builder.show();
  }

  private void muteAdDialogDidSelectReason(MuteThisAdReason reason) {
    // Report the mute action and reason to the ad.
    // The ad is actually muted (removed from UI) in the MuteThisAdListener callback.
    nativeAd.muteThisAd(reason);
  }

  private void muteAd() {
    // Disable mute button, remove ad.
    muteButton.setEnabled(false);
    adContainer.removeAllViews();
  }
}
