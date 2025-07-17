// Copyright 2025 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.android.gms.snippets;

import android.content.Context;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.rewarded.ServerSideVerificationOptions;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;

/** Java code snippets for the developer guide. */
public class RewardedInterstitialAdSnippets {

  private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/5354046379";
  private static final String SAMPLE_CUSTOM_DATA_STRING = "SAMPLE_CUSTOM_DATA_STRING";
  private RewardedInterstitialAd rewardedInterstitialAd;

  private void validateServerSideVerification(Context context) {
    // [START validate_server_side_verification]
    RewardedInterstitialAd.load(
        context,
        AD_UNIT_ID,
        new AdRequest.Builder().build(),
        new RewardedInterstitialAdLoadCallback() {
          @Override
          public void onAdLoaded(RewardedInterstitialAd ad) {
            rewardedInterstitialAd = ad;
            ServerSideVerificationOptions options =
                new ServerSideVerificationOptions.Builder()
                    .setCustomData(SAMPLE_CUSTOM_DATA_STRING)
                    .build();
            rewardedInterstitialAd.setServerSideVerificationOptions(options);
          }
        });
    // [END validate_server_side_verification]
  }
}
