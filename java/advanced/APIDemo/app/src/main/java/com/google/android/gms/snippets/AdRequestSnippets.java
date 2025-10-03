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

import android.os.Bundle;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Java code snippets for the developer guide. */
final class AdRequestSnippets {

  private void addNetworkExtras(AdView adView) {
    // [START add_network_extras]
    Bundle extras = new Bundle();
    extras.putString("collapsible", "bottom");
    AdRequest adRequest =
        new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, extras).build();
    adView.loadAd(adRequest);
    // [END add_network_extras]
  }

  private void addCustomTargeting() {
    // [START add_custom_targeting]
    // Example: Pass custom targeting "age=25".
    AdManagerAdRequest newRequest =
        new AdManagerAdRequest.Builder().addCustomTargeting("age", "25").build();
    // [END add_custom_targeting]
  }

  private void addCustomTargetingAsList() {
    // Example: Pass custom targeting ["age=24", "age=25", "age=26"].
    AdManagerAdRequest newRequest =
        new AdManagerAdRequest.Builder()
            // [START add_custom_targeting_as_list]
            .addCustomTargeting("age", Arrays.asList("24", "25", "26"))
            // [END add_custom_targeting_as_list]
            .build();
  }

  private void addCategoryExclusions() {
    // [START add_category_exclusions]
    // Example: Exclude "automobile" and "boat" categories.
    AdManagerAdRequest newRequest =
        new AdManagerAdRequest.Builder()
            .addCategoryExclusion("automobile")
            .addCategoryExclusion("boat")
            .build();
    // [END add_category_exclusions]
  }

  private void setPublisherProvidedId() {
    // [START set_publisher_provided_id]
    AdManagerAdRequest adRequest =
        new AdManagerAdRequest.Builder().setPublisherProvidedId("AB123456789").build();
    // [END set_publisher_provided_id]
  }

  private void setPublisherProvidedSignals() {
    // [START set_publisher_provided_signals]
    Bundle extras = new Bundle();
    // Set the demographic to an audience with an "Age Range" of 30-34 and an
    // interest in mergers and acquisitions.
    extras.putIntegerArrayList("IAB_AUDIENCE_1_1", new ArrayList<>(Arrays.asList(6, 284)));
    // Set the content to sedan, station wagon and SUV automotive values.
    extras.putIntegerArrayList("IAB_CONTENT_2_2", new ArrayList<>(Arrays.asList(4, 5, 6)));

    AdManagerAdRequest request =
        new AdManagerAdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, extras).build();
    // [END set_publisher_provided_signals]
  }

  private void setContentUrl() {
    // [START set_content_url]
    AdManagerAdRequest.Builder builder = new AdManagerAdRequest.Builder();
    builder.setContentUrl("https://www.example.com");
    AdManagerAdRequest request = builder.build();
    // [END set_content_url]
  }

  private void setNeighboringContentUrls() {
    // [START set_neighboring_content_urls]
    List<String> urls =
        Arrays.asList(
            "https://www.mycontenturl1.com",
            "https://www.mycontenturl2.com",
            "https://www.mycontenturl3.com",
            "https://www.mycontenturl4.com");

    AdManagerAdRequest requestWithContent =
        new AdManagerAdRequest.Builder().setNeighboringContentUrls(urls).build();
    // [END set_neighboring_content_urls]
  }
}
