 package com.google.android.gms.snippets;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import java.util.Arrays;
import java.util.List;

/** Java code snippets for the developer guide. */
final class RequestConfigurationSnippets {

  public static final String TEST_DEVICE_ID = "33BE2250B43518CCDA7DE426D04EE231";

  private void setTestDeviceIds() {
    // [START set_test_device_ids]
    List<String> testDeviceIds = Arrays.asList(TEST_DEVICE_ID);
    RequestConfiguration configuration =
        new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
    MobileAds.setRequestConfiguration(configuration);
    // [END set_test_device_ids]
  }

  private void setRequestConfiguration() {
    // [START set_request_configuration]
    RequestConfiguration requestConfiguration = MobileAds.getRequestConfiguration();
    MobileAds.setRequestConfiguration(requestConfiguration);
    // [END set_request_configuration]
  }

  private void setChildDirectedTreatment() {
    // [START set_child_directed_treatment]
    RequestConfiguration requestConfiguration =
        MobileAds.getRequestConfiguration().toBuilder()
            .setTagForChildDirectedTreatment(
                RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE)
            .build();
    MobileAds.setRequestConfiguration(requestConfiguration);
    // [END set_child_directed_treatment]
  }

  private void setUnderAgeOfConsent() {
    // [START set_under_age_of_consent]
    RequestConfiguration requestConfiguration =
        MobileAds.getRequestConfiguration().toBuilder()
            .setTagForUnderAgeOfConsent(RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_TRUE)
            .build();
    MobileAds.setRequestConfiguration(requestConfiguration);
    // [END set_under_age_of_consent]
  }

  private void setAdContentFiltering() {
    // [START set_ad_content_filtering]
    RequestConfiguration requestConfiguration =
        MobileAds.getRequestConfiguration().toBuilder()
            .setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G)
            .build();
    MobileAds.setRequestConfiguration(requestConfiguration);
    // [END set_ad_content_filtering]
  }

  private void setPublisherPrivacyTreatment() {
    // [START set_publisher_privacy_treatment]
    RequestConfiguration requestConfiguration =
        MobileAds.getRequestConfiguration().toBuilder()
            .setPublisherPrivacyPersonalizationState(
                RequestConfiguration.PublisherPrivacyPersonalizationState.DISABLED)
            .build();
    MobileAds.setRequestConfiguration(requestConfiguration);
    // [END set_publisher_privacy_treatment]
  }
}
