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

import android.content.Context;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

/** Java code snippets for the developer guide. */
public class InlineAdaptiveBannerSnippets {

  private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/1039341195";

  // [START load_inline_adaptive_banner]
  private void loadInlineAdaptiveBanner(Context context) {

    // Step 1: Create an inline adaptive banner ad size using the activity context.
    AdSize adSize = AdSize.getCurrentOrientationInlineAdaptiveBannerAdSize(context, 360);

    // Step 2: Create banner using activity context and set the inline ad size and
    // ad unit ID.
    AdView bannerView = new AdView(context);
    bannerView.setAdUnitId(AD_UNIT_ID);
    bannerView.setAdSize(adSize);

    // Step 3: Load an ad.
    AdRequest adRequest = new AdRequest.Builder().build();
    bannerView.loadAd(adRequest);

    // TODO: Insert banner view in list view or scroll view, etc.
  }
  // [END load_inline_adaptive_banner]
}
