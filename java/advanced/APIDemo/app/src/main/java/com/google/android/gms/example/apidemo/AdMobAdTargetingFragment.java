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
package com.google.android.gms.example.apidemo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import androidx.fragment.app.Fragment;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AgeRestrictedTreatment;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;

/**
 * The {@link AdMobAdTargetingFragment} class demonstrates how to use ad targeting with AdMob.
 */
public class AdMobAdTargetingFragment extends Fragment {

  private AdView adView;
  private Button loadAdButton;
  private RadioButton childRadio;
  private RadioButton teenRadio;
  private RadioButton unspecifiedRadio;
  private RadioButton contentRatingGRadio;
  private RadioButton contentRatingPGRadio;
  private RadioButton contentRatingTRadio;
  private RadioButton contentRatingMARadio;

  public AdMobAdTargetingFragment() {
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_admob_ad_targeting, container, false);

    adView = rootView.findViewById(R.id.targeting_av_main);
    loadAdButton = rootView.findViewById(R.id.targeting_btn_loadad);

    childRadio = rootView.findViewById(R.id.targeting_rb_child);
    teenRadio = rootView.findViewById(R.id.targeting_rb_teen);
    unspecifiedRadio = rootView.findViewById(R.id.targeting_rb_unspecified);
    contentRatingGRadio = rootView.findViewById(R.id.targeting_rb_rating_g);
    contentRatingPGRadio = rootView.findViewById(R.id.targeting_rb_rating_pg);
    contentRatingTRadio = rootView.findViewById(R.id.targeting_rb_rating_t);
    contentRatingMARadio = rootView.findViewById(R.id.targeting_rb_rating_ma);

    loadAdButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View v) {
            RequestConfiguration.Builder configurationBuilder =
                MobileAds.getRequestConfiguration().toBuilder();

            // Call setAgeRestrictedTreatment() based on radio button setting.
            if (childRadio.isChecked()) {
              configurationBuilder.setAgeRestrictedTreatment(AgeRestrictedTreatment.CHILD);
            } else if (teenRadio.isChecked()) {
              configurationBuilder.setAgeRestrictedTreatment(AgeRestrictedTreatment.TEEN);
            } else if (unspecifiedRadio.isChecked()) {
              configurationBuilder.setAgeRestrictedTreatment(AgeRestrictedTreatment.UNSPECIFIED);
            }

            // Call setMaxAdContentRating() based on radio button setting.
            if (contentRatingGRadio.isChecked()) {
              configurationBuilder.setMaxAdContentRating(
                  RequestConfiguration.MAX_AD_CONTENT_RATING_G);
            } else if (contentRatingPGRadio.isChecked()) {
              configurationBuilder.setMaxAdContentRating(
                  RequestConfiguration.MAX_AD_CONTENT_RATING_PG);
            } else if (contentRatingTRadio.isChecked()) {
              configurationBuilder.setMaxAdContentRating(
                  RequestConfiguration.MAX_AD_CONTENT_RATING_T);
            } else if (contentRatingMARadio.isChecked()) {
              configurationBuilder.setMaxAdContentRating(
                  RequestConfiguration.MAX_AD_CONTENT_RATING_MA);
            }

            // Build the new configuration and set it on the global MobileAds object.
            MobileAds.setRequestConfiguration(configurationBuilder.build());

            AdRequest request = new AdRequest.Builder().build();
            adView.loadAd(request);
          }
        });

    return rootView;
  }
}
