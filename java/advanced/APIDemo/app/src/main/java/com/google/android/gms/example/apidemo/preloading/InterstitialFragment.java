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

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.example.apidemo.R;
import com.google.android.gms.example.apidemo.databinding.FragmentPreloadItemBinding;

/** A [Fragment] subclass that preloads an interstitial ad. */
public class InterstitialFragment extends PreloadItemFragment {

  // Replace this test ad unit ID with your own ad unit ID.
  public static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/1033173712";

  private FragmentPreloadItemBinding viewBinding;

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    viewBinding = FragmentPreloadItemBinding.inflate(inflater, container, false);

    // Initialize the UI.
    initializeUI();

    return viewBinding.getRoot();
  }

  private void initializeUI() {
    viewBinding.txtTitle.setText(getText(R.string.preload_interstitial));
    viewBinding.btnShow.setOnClickListener(
        view -> {
          pollAndShowAd();
          updateUI();
        });
    updateUI();
  }

  // [START pollAndShowAd]
  private void pollAndShowAd() {
    // [START isAdAvailable]
    // Verify that a preloaded ad is available before polling for an ad.
    if (!InterstitialAd.isAdAvailable(requireContext(), AD_UNIT_ID)) {
      Log.w(LOG_TAG, "Preloaded interstitial ad ${AD_UNIT_ID} is not available.");
      return;
    }
    // [END isAdAvailable]
    // Polling returns the next available ad and load another ad in the background.
    InterstitialAd ad = InterstitialAd.pollAd(requireContext(), AD_UNIT_ID);
    Activity activity = getActivity();
    if (activity != null && ad != null) {
      ad.show(activity);
    }
  }

  // [END pollAndShowAd]

  @Override
  public synchronized void updateUI() {
    if (InterstitialAd.isAdAvailable(requireContext(), AD_UNIT_ID)) {
      viewBinding.txtStatus.setText(getString(R.string.preload_available));
      viewBinding.btnShow.setEnabled(true);
    } else {
      viewBinding.txtStatus.setText(getString(R.string.preload_exhausted));
      viewBinding.btnShow.setEnabled(false);
    }
  }
}
