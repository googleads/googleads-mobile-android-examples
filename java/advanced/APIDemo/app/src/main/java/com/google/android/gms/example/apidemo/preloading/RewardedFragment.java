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
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.example.apidemo.R;
import com.google.android.gms.example.apidemo.databinding.FragmentPreloadItemBinding;

/** A [Fragment] subclass that preloads an rewarded ad. */
public class RewardedFragment extends PreloadItemFragment {

  // Replace this test ad unit ID with your own ad unit ID.
  public static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917";

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
    viewBinding.txtTitle.setText(getText(R.string.preload_rewarded));
    viewBinding.btnShow.setOnClickListener(
        view -> {
          pollAndShowAd();
          updateUI();
        });
    updateUI();
  }

  private void pollAndShowAd() {
    // Verify that a preloaded ad is available before polling for an ad.
    if (!RewardedAd.isAdAvailable(requireContext(), AD_UNIT_ID)) {
      Log.w(LOG_TAG, "Preloaded rewarded ad ${AD_UNIT_ID} is not available.");
      return;
    }
    // Polling returns the next available ad and load another ad in the background.
    RewardedAd ad = RewardedAd.pollAd(requireContext(), AD_UNIT_ID);
    Activity activity = getActivity();
    if (activity != null && ad != null) {
      ad.show(
          activity,
          new OnUserEarnedRewardListener() {
            @Override
            public void onUserEarnedReward(@NonNull RewardItem reward) {
              Log.w(LOG_TAG, "User was rewarded " + reward.getAmount() + " " + reward.getType());
            }
          });
    }
  }

  @Override
  public synchronized void updateUI() {
    if (RewardedAd.isAdAvailable(requireContext(), AD_UNIT_ID)) {
      viewBinding.txtStatus.setText(getString(R.string.preload_available));
      viewBinding.btnShow.setEnabled(true);
    } else {
      viewBinding.txtStatus.setText(getString(R.string.preload_exhausted));
      viewBinding.btnShow.setEnabled(false);
    }
  }
}
