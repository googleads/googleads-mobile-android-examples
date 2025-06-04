/*
 * Copyright 2024 Google LLC
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

package com.google.android.gms.example.apidemo.preloading;

import static com.google.android.gms.example.apidemo.MainActivity.LOG_TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.google.android.gms.ads.AdFormat;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.preload.PreloadCallback;
import com.google.android.gms.ads.preload.PreloadConfiguration;
import com.google.android.gms.example.apidemo.R;
import com.google.android.gms.example.apidemo.databinding.FragmentPreloadItemBinding;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Demonstrates how to preload ads. */
public class AdMobPreloadingAdsFragment extends PreloadItemFragment {

  private final List<PreloadItemFragment> fragmentList = new ArrayList<>();

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // Inflate the layout.
    FragmentPreloadItemBinding viewBinding =
        FragmentPreloadItemBinding.inflate(getLayoutInflater());

    // Initialize the fragments UI.
    addFragmentsUI();

    // Start preloading ads.
    startPreload();

    return viewBinding.getRoot();
  }

  // [START start_preload]
  private void startPreload() {
    // Define a list of PreloadConfiguration objects, specifying the ad unit ID and ad format.
    List<PreloadConfiguration> preloadConfigs =
        Arrays.asList(
            new PreloadConfiguration.Builder(InterstitialFragment.AD_UNIT_ID, AdFormat.INTERSTITIAL)
                .build(),
            new PreloadConfiguration.Builder(RewardedFragment.AD_UNIT_ID, AdFormat.REWARDED)
                .build(),
            new PreloadConfiguration.Builder(AppOpenFragment.AD_UNIT_ID, AdFormat.APP_OPEN_AD)
                .build());

    // Start the preloading initialization process.
    // Be sure to pass an Activity context when calling startPreload() if you use mediation, as
    // some mediation partners require an Activity context to load ads.
    MobileAds.startPreload(this.requireActivity(), preloadConfigs, getPreloadCallback());
  }

  // [END start_preload]

  // [START set_callback]
  private PreloadCallback getPreloadCallback() {
    // Define a callback to receive preload availability events.
    return new PreloadCallback() {
      @Override
      public void onAdsAvailable(@NonNull PreloadConfiguration preloadConfig) {
        // This callback indicates that an ad is available for the specified configuration.
        // No action is required here, but updating the UI can be useful in some cases.
        Log.i(
            LOG_TAG,
            "Preload ad for configuration with ad unit ID "
                + preloadConfig.getAdUnitId()
                + " and ad format "
                + preloadConfig.getAdFormat()
                + " is available.");
        // [START_EXCLUDE]
        updateUI();
        // [END_EXCLUDE]
      }

      @Override
      public void onAdsExhausted(@NonNull PreloadConfiguration preloadConfig) {
        // This callback indicates that all the ads for the specified configuration have been
        // consumed and no ads are available to show. No action is required here, but updating
        // the UI can be useful in some cases.
        Log.i(
            LOG_TAG,
            "Preload ad for configuration " + preloadConfig.getAdUnitId() + " is exhausted.");
        // [START_EXCLUDE]
        updateUI();
        // [END_EXCLUDE]
      }
    };
  }

  // [END set_callback]

  private void addFragmentsUI() {
    addFragmentUI(new AppOpenFragment());
    addFragmentUI(new InterstitialFragment());
    addFragmentUI(new RewardedFragment());
  }

  private void addFragmentUI(PreloadItemFragment fragment) {
    getParentFragmentManager().beginTransaction().add(R.id.list_formats, fragment).commit();
    fragmentList.add(fragment);
  }

  public void updateUI() {
    for (PreloadItemFragment fragment : fragmentList) {
      fragment.updateUI();
    }
  }
}
