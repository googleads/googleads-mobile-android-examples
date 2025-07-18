/*
 * Copyright 2025 Google LLC
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

package com.google.android.gms.snippets;

import android.view.View;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerAdView;

/** Java code snippets for the developer guide. */
final class NativeStylesSnippets {

  private void requestFluidNativeAd(View fluidAdContainer) {
    // [START request_fluid_native_ad]
    // fluidAdContainer is a ViewGroup that will be used to display the fluid native ad.
    AdManagerAdView adView = (AdManagerAdView) fluidAdContainer;
    AdManagerAdRequest request = new AdManagerAdRequest.Builder().build();
    adView.loadAd(request);
    // [END request_fluid_native_ad]
  }
}
