/*
 * Copyright 2023 Google LLC
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

package com.google.android.gms.example.bannerexample;

import android.app.Activity;
import android.content.Context;
import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentForm.OnConsentFormDismissedListener;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentInformation.PrivacyOptionsRequirementStatus;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.FormError;
import com.google.android.ump.UserMessagingPlatform;

/**
 * The Google Mobile Ads SDK provides the User Messaging Platform (Google's IAB Certified consent
 * management platform) as one solution to capture consent for users in GDPR impacted countries.
 * This is an example and you can choose another consent management platform to capture consent.
 */
public class GoogleMobileAdsConsentManager {
  private static GoogleMobileAdsConsentManager instance;
  private final ConsentInformation consentInformation;

  /** Private constructor */
  private GoogleMobileAdsConsentManager(Context context) {
    this.consentInformation = UserMessagingPlatform.getConsentInformation(context);
  }

  /** Public constructor */
  public static GoogleMobileAdsConsentManager getInstance(Context context) {
    if (instance == null) {
      instance = new GoogleMobileAdsConsentManager(context);
    }

    return instance;
  }

  /** Interface definition for a callback to be invoked when consent gathering is complete. */
  public interface OnConsentGatheringCompleteListener {
    void consentGatheringComplete(FormError error);
  }

  /** Helper variable to determine if the app can request ads. */
  public boolean canRequestAds() {
    return consentInformation.canRequestAds();
  }

  // [START is_privacy_options_required]
  /** Helper variable to determine if the privacy options form is required. */
  public boolean isPrivacyOptionsRequired() {
    return consentInformation.getPrivacyOptionsRequirementStatus()
        == PrivacyOptionsRequirementStatus.REQUIRED;
  }

  // [END is_privacy_options_required]

  /**
   * Helper method to call the UMP SDK methods to request consent information and load/present a
   * consent form if necessary.
   */
  public void gatherConsent(
      Activity activity, OnConsentGatheringCompleteListener onConsentGatheringCompleteListener) {
    // For testing purposes, you can force a DebugGeography of EEA or NOT_EEA.
    ConsentDebugSettings debugSettings =
        new ConsentDebugSettings.Builder(activity)
            // .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
            .addTestDeviceHashedId(MyActivity.TEST_DEVICE_HASHED_ID)
            .build();

    ConsentRequestParameters params =
        new ConsentRequestParameters.Builder().setConsentDebugSettings(debugSettings).build();

    // [START request_consent_info_update]
    // Requesting an update to consent information should be called on every app launch.
    consentInformation.requestConsentInfoUpdate(
        activity,
        params,
        () -> // Called when consent information is successfully updated.
            // [START_EXCLUDE silent]
            loadAndShowConsentFormIfRequired(activity, onConsentGatheringCompleteListener),
        // [END_EXCLUDE]
        requestConsentError -> // Called when there's an error updating consent information.
            // [START_EXCLUDE silent]
            onConsentGatheringCompleteListener.consentGatheringComplete(requestConsentError));
    // [END_EXCLUDE]
    // [END request_consent_info_update]
  }

  private void loadAndShowConsentFormIfRequired(
      Activity activity, OnConsentGatheringCompleteListener onConsentGatheringCompleteListener) {
    // [START load_and_show_consent_form]
    UserMessagingPlatform.loadAndShowConsentFormIfRequired(
        activity,
        formError -> {
          // Consent gathering process is complete.
          // [START_EXCLUDE silent]
          onConsentGatheringCompleteListener.consentGatheringComplete(formError);
          // [END_EXCLUDE]
        });
    // [END load_and_show_consent_form]
  }

  /** Helper method to call the UMP SDK method to present the privacy options form. */
  public void showPrivacyOptionsForm(
      Activity activity, OnConsentFormDismissedListener onConsentFormDismissedListener) {
    // [START present_privacy_options_form]
    UserMessagingPlatform.showPrivacyOptionsForm(activity, onConsentFormDismissedListener);
    // [END present_privacy_options_form]
  }
}
