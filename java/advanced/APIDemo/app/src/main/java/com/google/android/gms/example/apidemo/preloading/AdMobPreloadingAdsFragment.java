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
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
    // Define the number of ads that can be preloaded for each ad unit.
    int bufferSize = 2;
    // Define a list of PreloadConfiguration objects, specifying the ad unit ID and ad format for
    // each ad unit to be preloaded.
    List<PreloadConfiguration> preloadConfigs =
        Arrays.asList(
            new PreloadConfiguration.Builder(InterstitialFragment.AD_UNIT_ID, AdFormat.INTERSTITIAL)
                .setBufferSize(bufferSize)
                .build(),
            new PreloadConfiguration.Builder(RewardedFragment.AD_UNIT_ID, AdFormat.REWARDED)
                .setBufferSize(bufferSize)
                .build(),
            new PreloadConfiguration.Builder(AppOpenFragment.AD_UNIT_ID, AdFormat.APP_OPEN_AD)
                .setBufferSize(bufferSize)
                .build());

    // Define a callback to receive preload availability events.
    PreloadCallback callback =
        new PreloadCallback() {
          @Override
          public void onAdsAvailable(@NonNull PreloadConfiguration preloadConfig) {
            Log.i(LOG_TAG, "Preload ad for " + preloadConfig.getAdFormat() + " is available.");
            updateUI();
          }

          @Override
          public void onAdsExhausted(@NonNull PreloadConfiguration preloadConfig) {
            Log.i(LOG_TAG, "Preload ad for " + preloadConfig.getAdFormat() + " is exhausted.");
            updateUI();
          }
        };

    // Start the preloading initialization process.
    MobileAds.startPreload(this.requireContext(), preloadConfigs, callback);
  }

  // [END start_preload]

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
