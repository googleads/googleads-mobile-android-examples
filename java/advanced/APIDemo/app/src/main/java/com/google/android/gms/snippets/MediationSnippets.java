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
import com.google.android.gms.ads.initialization.InitializationStatus;
import java.util.Map;

/** Java code snippets for the developer guide. */
public class MediationSnippets {

  private static final String TAG = "MediationSnippets";

  // [START initialize_sdk_with_callback_method]
  public void initialize(Context context) {
    new Thread(
            () ->
                // Initialize the Google Mobile Ads SDK on a background thread.
                MobileAds.initialize(context, this::logAdapterStatus))
        .start();
  }

  private void logAdapterStatus(InitializationStatus initializationStatus) {
    // Check each adapter's initialization status.
    Map<String, AdapterStatus> statusMap = initializationStatus.getAdapterStatusMap();
    for (Map.Entry<String, AdapterStatus> entry : statusMap.entrySet()) {
      String adapterClass = entry.getKey();
      AdapterStatus status = entry.getValue();
      Log.d(
          TAG,
          String.format(
              "Adapter name: %s, Description: %s, Latency: %d",
              adapterClass, status.getDescription(), status.getLatency()));
    }
  }
  // [END initialize_sdk_with_callback_method]
}
