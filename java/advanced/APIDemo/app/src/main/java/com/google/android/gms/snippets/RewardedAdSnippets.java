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
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.rewarded.ServerSideVerificationOptions;

/** Java code snippets for the developer guide. */
public class RewardedAdSnippets {

  private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917";
  private static final String SAMPLE_CUSTOM_DATA_STRING = "SAMPLE_CUSTOM_DATA_STRING";
  private RewardedAd rewardedAd;

  private void validateServerSideVerification(Context context) {
    // [START validate_server_side_verification]
    RewardedAd.load(
        context,
        AD_UNIT_ID,
        new AdRequest.Builder().build(),
        new RewardedAdLoadCallback() {
          @Override
          public void onAdLoaded(RewardedAd ad) {
            rewardedAd = ad;
            ServerSideVerificationOptions options =
                new ServerSideVerificationOptions.Builder()
                    .setCustomData(SAMPLE_CUSTOM_DATA_STRING)
                    .build();
            rewardedAd.setServerSideVerificationOptions(options);
          }
        });
    // [END validate_server_side_verification]
  }
}
