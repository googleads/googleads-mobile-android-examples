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

import android.os.Bundle
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.admanager.AdManagerAdRequest

/** Kotlin code snippets for the developer guide. */
private class AdRequestSnippets {

  private fun addNetworkExtras(adView: AdView) {
    // [START add_network_extras]
    val extras = Bundle()
    extras.putString("collapsible", "bottom")
    val adRequest =
      AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter::class.java, extras).build()
    adView.loadAd(adRequest)
    // [END add_network_extras]
  }

  private fun addCustomTargeting() {
    // [START add_custom_targeting]
    // Example: Pass custom targeting "age=25".
    val newRequest = AdManagerAdRequest.Builder().addCustomTargeting("age", "25").build()
    // [END add_custom_targeting]
  }

  private fun addCustomTargetingAsList() {
    // Example: Pass custom targeting ["age=24", "age=25", "age=26"].
    val newRequest =
      AdManagerAdRequest.Builder()
        // [START add_custom_targeting_as_list]
        .addCustomTargeting("age", listOf("24", "25", "26"))
        // [END add_custom_targeting_as_list]
        .build()
  }

  private fun addCategoryExclusions() {
    // [START add_category_exclusions]
    // Example: Exclude "automobile" and "boat" categories.
    val newRequest =
      AdManagerAdRequest.Builder()
        .addCategoryExclusion("automobile")
        .addCategoryExclusion("boat")
        .build()
    // [END add_category_exclusions]
  }

  private fun setPublisherProvidedId() {
    // [START set_publisher_provided_id]
    val adRequest = AdManagerAdRequest.Builder().setPublisherProvidedId("AB123456789").build()
    // [END set_publisher_provided_id]
  }

  private fun setPublisherProvidedSignals() {
    // [START set_publisher_provided_signals]
    val extras = Bundle()
    // Set the demographic to an audience with an "Age Range" of 30-34 and an
    // interest in mergers and acquisitions.
    extras.putIntegerArrayList("IAB_AUDIENCE_1_1", arrayListOf(6, 284))
    // Set the content to sedan, station wagon and SUV automotive values.
    extras.putIntegerArrayList("IAB_CONTENT_2_2", arrayListOf(4, 5, 6))

    val request =
      AdManagerAdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter::class.java, extras).build()
    // [END set_publisher_provided_signals]
  }

  private fun setContentUrl() {
    // [START set_content_url]
    val builder = AdManagerAdRequest.Builder()
    builder.setContentUrl("https://www.example.com")
    val request = builder.build()
    // [END set_content_url]
  }

  private fun setNeighboringContentUrls() {
    // [START set_neighboring_content_urls]
    val urls =
      mutableListOf(
        "https://www.mycontenturl1.com",
        "https://www.mycontenturl2.com",
        "https://www.mycontenturl3.com",
        "https://www.mycontenturl4.com",
      )

    val requestWithContent = AdManagerAdRequest.Builder().setNeighboringContentUrls(urls).build()
    // [END set_neighboring_content_urls]
  }
}
