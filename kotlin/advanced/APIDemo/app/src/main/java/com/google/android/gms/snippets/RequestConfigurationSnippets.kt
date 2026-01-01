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

import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration

/** Kotlin code snippets for the developer guide. */
private class RequestConfigurationSnippets {

  private fun setTestDeviceIds() {
    // [START set_test_device_ids]
    val testDeviceIds = listOf(TEST_DEVICE_ID)
    val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
    MobileAds.setRequestConfiguration(configuration)
    // [END set_test_device_ids]
  }

  private fun setRequestConfiguration() {
    // [START set_request_configuration]
    val requestConfiguration = MobileAds.getRequestConfiguration()
    MobileAds.setRequestConfiguration(requestConfiguration)
    // [END set_request_configuration]
  }

  private fun setChildDirectedTreatment() {
    // [START set_child_directed_treatment]
    val requestConfiguration =
      MobileAds.getRequestConfiguration()
        .toBuilder()
        .setTagForChildDirectedTreatment(RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE)
        .build()
    MobileAds.setRequestConfiguration(requestConfiguration)
    // [END set_child_directed_treatment]
  }

  private fun setUnderAgeOfConsent() {
    // [START set_under_age_of_consent]
    val requestConfiguration =
      MobileAds.getRequestConfiguration()
        .toBuilder()
        .setTagForUnderAgeOfConsent(RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_TRUE)
        .build()
    MobileAds.setRequestConfiguration(requestConfiguration)
    // [END set_under_age_of_consent]
  }

  private fun setAdContentFiltering() {
    // [START set_ad_content_filtering]
    val requestConfiguration =
      MobileAds.getRequestConfiguration()
        .toBuilder()
        .setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G)
        .build()
    MobileAds.setRequestConfiguration(requestConfiguration)
    // [END set_ad_content_filtering]
  }

  private fun setPublisherPrivacyTreatment() {
    // [START set_publisher_privacy_treatment]
    val requestConfiguration =
      MobileAds.getRequestConfiguration()
        .toBuilder()
        .setPublisherPrivacyPersonalizationState(
          RequestConfiguration.PublisherPrivacyPersonalizationState.DISABLED
        )
        .build()
    MobileAds.setRequestConfiguration(requestConfiguration)
    // [END set_publisher_privacy_treatment]
  }

  companion object {
    const val TEST_DEVICE_ID = "33BE2250B43518CCDA7DE426D04EE231"
  }
}
