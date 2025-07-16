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

package com.google.android.gms.snippets

import android.content.Context
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

/** Kotlin code snippets for the developer guide. */
class InlineAdaptiveBannerSnippets {

  // [START load_inline_adaptive_banner]
  private fun loadInlineAdaptiveBanner(context: Context) {

    // Step 1: Create an inline adaptive banner ad size using the activity context.
    val adSize = AdSize.getCurrentOrientationInlineAdaptiveBannerAdSize(context, 360)

    // Step 2: Create banner using activity context and set the inline ad size and
    // ad unit ID.
    val bannerView = AdView(context)
    bannerView.adUnitId = AD_UNIT_ID
    bannerView.setAdSize(adSize)

    // Step 3: Load an ad.
    val adRequest = AdRequest.Builder().build()
    bannerView.loadAd(adRequest)

    // TODO: Insert banner view in list view or scroll view, etc.
  }

  // [END load_inline_adaptive_banner]

  private companion object {
    const val AD_UNIT_ID = "ca-app-pub-3940256099942544/1039341195"
  }
}
