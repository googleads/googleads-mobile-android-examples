package com.google.android.gms.example.bannerexample;

import android.app.Activity;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.ump.ConsentDebugSettings;
import com.google.android.ump.ConsentForm;
import com.google.android.ump.ConsentForm.OnConsentFormDismissedListener;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentInformation.ConsentStatus;
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
  private static final String TAG = "MobileAdsConsentManager";
  private final Activity activity;
  private final ConsentInformation consentInformation;

  // The UMP SDK consent form.
  private ConsentForm consentForm = null;

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
    return consentInformation.getConsentStatus() == ConsentStatus.OBTAINED
        || consentInformation.getConsentStatus() == ConsentStatus.NOT_REQUIRED;
  }

  /** Helper variable to determine if the consent form is available. */
  public boolean isFormAvailable() {
    return consentInformation.isConsentFormAvailable();
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
            loadAndPresentConsentFormIfRequired(
                activity,
                formError -> {
                  // Consent has been gathered.
                  onConsentGatheringCompleteListener.consentGatheringComplete(formError);

                  // Your app needs to allow the user to change their consent status at any time.
                  // Load another form and store it so it's ready to be displayed immediately after
                  // the user clicks your app's privacy settings button.
                  loadPrivacyOptionsFormIfRequired();
                }),
        requestConsentError ->
            onConsentGatheringCompleteListener.consentGatheringComplete(requestConsentError)
    );
  }

  private void loadAndPresentConsentFormIfRequired(
      Activity activity,
      OnConsentFormDismissedListener onConsentFormDismissedListener
  ) {
    // Determine the consent-related action to take based on the ConsentStatus.
    if (
        consentInformation.getConsentStatus() == ConsentStatus.OBTAINED
            || consentInformation.getConsentStatus() == ConsentStatus.NOT_REQUIRED
    ) {
      // Consent has already been gathered or not required.
      onConsentFormDismissedListener.onConsentFormDismissed(null);
      return;
    }

    UserMessagingPlatform.loadConsentForm(
        activity,
        form -> consentForm.show(activity, onConsentFormDismissedListener),
        loadError -> onConsentFormDismissedListener.onConsentFormDismissed(loadError)
    );
  }

  private void loadPrivacyOptionsFormIfRequired() {
    // No privacy options form needed if consent form is not available.
    if (consentInformation.isConsentFormAvailable()) {
      UserMessagingPlatform.loadConsentForm(
          activity,
          consentForm -> this.consentForm = consentForm,
          formError ->
              // See FormError for more info.
              Log.d(
                  TAG, String.format("%s: %s", formError.getErrorCode(), formError.getMessage()))
      );
    }
  }

  /** Helper method to call the UMP SDK method to present the privacy options form. */
  public void presentPrivacyOptionsFormIfRequired(
      Activity activity,
      OnConsentFormDismissedListener onConsentFormDismissedListener) {
      if (consentForm == null) {
        onConsentFormDismissedListener.onConsentFormDismissed(
            new FormError(0, "No form available. Please try again later."));

        // Your app needs to allow the user to change their consent status at any time. Load
        // another form and store it so it's ready to be displayed immediately after the user
        // clicks your app's privacy settings button.
        loadPrivacyOptionsFormIfRequired();
        return;
      }

      consentForm.show(
          activity,
          formError -> {
            onConsentFormDismissedListener.onConsentFormDismissed(formError);

            // Your app needs to allow the user to change their consent status at any time. Load
            // another form and store it so it's ready to be displayed immediately after the user
            // clicks your app's privacy settings button.
            loadPrivacyOptionsFormIfRequired();
          }
      );
  }
}
