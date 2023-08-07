package com.google.android.gms.example.bannerexample;

import android.app.Activity;
import androidx.annotation.NonNull;
import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentForm.OnConsentFormDismissedListener;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentInformation.PrivacyOptionsRequirementStatus;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.FormError;
import com.google.android.ump.UserMessagingPlatform;

/**
 * The Google Mobile Ads SDK provides the User Messaging Platform (Google's
 * IAB Certified consent management platform) as one solution to capture
 * consent for users in GDPR impacted countries. This is an example and
 * you can choose another consent management platform to capture consent.
 */
public class GoogleMobileAdsConsentManager {
  private final Activity activity;
  private final ConsentInformation consentInformation;

  /** Interface definition for a callback to be invoked when consent gathering is complete. */
  public interface OnConsentGatheringCompleteListener {
    void consentGatheringComplete(FormError error);
  }

  /** Constructor */
  public GoogleMobileAdsConsentManager(@NonNull Activity activity) {
    this.activity = activity;
    this.consentInformation = UserMessagingPlatform.getConsentInformation(activity);
  }

  /** Helper variable to determine if the app can request ads. */
  public boolean canRequestAds() {
    return consentInformation.canRequestAds();
  }

  /** Helper variable to determine if the privacy options form is required. */
  public boolean isPrivacyOptionsRequired() {
    return consentInformation.getPrivacyOptionsRequirementStatus()
        == PrivacyOptionsRequirementStatus.REQUIRED;
  }

  /** Helper method to call the UMP SDK methods to request consent information and load/present a
   * consent form if necessary. */
  public void gatherConsent(
      OnConsentGatheringCompleteListener onConsentGatheringCompleteListener) {
    // For testing purposes, you can force a DebugGeography of EEA or NOT_EEA.
    ConsentDebugSettings debugSettings = new ConsentDebugSettings.Builder(activity)
        // .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
        // Check your logcat output for the hashed device ID e.g.
        // "Use new ConsentDebugSettings.Builder().addTestDeviceHashedId("ABCDEF012345")" to use
        // the debug functionality.
        .addTestDeviceHashedId("TEST-DEVICE-HASHED-ID")
        .build();

    ConsentRequestParameters params = new ConsentRequestParameters.Builder()
        .setConsentDebugSettings(debugSettings)
        .build();

    // Requesting an update to consent information should be called on every app launch.
    consentInformation.requestConsentInfoUpdate(
        activity,
        params,
        () ->
            UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                activity,
                formError -> {
                  // Consent has been gathered.
                  onConsentGatheringCompleteListener.consentGatheringComplete(formError);
                }),
        requestConsentError ->
            onConsentGatheringCompleteListener.consentGatheringComplete(requestConsentError)
    );
  }

  /** Helper method to call the UMP SDK method to present the privacy options form. */
  public void showPrivacyOptionsForm(
      Activity activity,
      OnConsentFormDismissedListener onConsentFormDismissedListener) {
    UserMessagingPlatform.showPrivacyOptionsForm(activity, onConsentFormDismissedListener);
  }
}
