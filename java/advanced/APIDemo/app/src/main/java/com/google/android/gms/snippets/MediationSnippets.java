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
import android.util.Log;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.AdapterStatus;
import java.util.Map;

/** Java code snippets for the developer guide. */
public class MediationSnippets {

  private static final String TAG = "MediationSnippets";

  private void checkMediationAdapterStatusOnInitialize(Context context) {
    // [START initialize_sdk]
    new Thread(
            () ->
                // Initialize the Google Mobile Ads SDK on a background thread.
                MobileAds.initialize(
                    context,
                    initializationStatus -> {
                      // Check each adapter's initialization status.
                      Map<String, AdapterStatus> statusMap =
                          initializationStatus.getAdapterStatusMap();
                      for (String adapterClass : statusMap.keySet()) {
                        AdapterStatus status = statusMap.get(adapterClass);
                        if (status != null) {
                          Log.d(
                              TAG,
                              String.format(
                                  "Adapter name: %s, Description: %s, Latency: %d",
                                  adapterClass, status.getDescription(), status.getLatency()));
                        }
                      }
                    }))
        .start();
    // [END initialize_sdk]
  }
}
