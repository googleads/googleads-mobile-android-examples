package com.google.android.gms.example.jetpackcomposedemo

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.annotation.IntDef
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.*
import java.util.concurrent.atomic.AtomicBoolean

/** This class manages the process of obtaining consent for and initializing Google Mobile Ads. */
class GoogleMobileAdsManager {

  /** Represents initialization states for the Google Mobile Ads SDK. */
  object MobileAdsState {
    /** Initial start state. */
    const val UNINITIALIZED = 0

    /** User consent required but not yet obtained. */
    const val CONSENT_REQUIRED = 2

    /** User consent obtained. Personalized vs non-personalized undefined. */
    const val CONSENT_OBTAINED = 3

    /** Google Mobile Ads SDK initialized successfully. */
    const val INITIALIZED = 100

    /** An error occurred when requesting consent. */
    const val CONSENT_REQUEST_ERROR = -1

    /** An error occurred when showing the privacy options form. */
    const val CONSENT_FORM_ERROR = -2

    @Target(AnnotationTarget.TYPE)
    @IntDef(
      UNINITIALIZED,
      INITIALIZED,
      CONSENT_REQUIRED,
      CONSENT_OBTAINED,
      CONSENT_REQUEST_ERROR,
      CONSENT_FORM_ERROR,
    )
    @Retention(AnnotationRetention.SOURCE)
    annotation class State
  }

  /** Represents current initialization states for the Google Mobile Ads SDK. */
  var mobileAdsState = mutableIntStateOf(MobileAdsState.UNINITIALIZED)

  /** Indicates whether the app has completed the steps for gathering updated user consent. */
  var canRequestAds = mutableStateOf(false)

  /** Helper variable to determine if the privacy options form is required. */
  var isPrivacyOptionsRequired = mutableStateOf(false)

  private var isMobileAdsInitializeCalled = AtomicBoolean(false)

  private lateinit var consentInformation: ConsentInformation

  /**
   * Initiates the consent process and initializes the Google Mobile Ads SDK if the SDK is
   * UNINITIALIZED.
   *
   * @param activity Activity responsible for initializing the Google Mobile Ads SDK.
   * @param consentRequestParameters Parameters for the consent request form.
   */
  fun initializeWithConsent(
    activity: Activity,
    consentRequestParameters: ConsentRequestParameters,
  ) {

    if (isMobileAdsInitializeCalled.getAndSet(true)) {
      return
    }

    consentInformation = UserMessagingPlatform.getConsentInformation(activity)

    consentInformation.requestConsentInfoUpdate(
      activity,
      consentRequestParameters,
      {
        // Success callback.
        showConsentFormIfRequired(activity) { error ->
          if (error != null) {
            Log.w(TAG, "Consent form error: ${error.errorCode} - ${error.message}")
            mobileAdsState.intValue = MobileAdsState.CONSENT_FORM_ERROR
          } else {
            mobileAdsState.intValue = MobileAdsState.CONSENT_OBTAINED
          }
          canRequestAds.value = consentInformation.canRequestAds()
          isPrivacyOptionsRequired.value =
            consentInformation.privacyOptionsRequirementStatus ==
              ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED
          if (consentInformation.canRequestAds()) {
            initializeMobileAdsSdk(activity)
          }
        }
      },
      { formError ->
        // Failure callback.
        Log.w(TAG, "Consent info update error: ${formError.errorCode} - ${formError.message}")
        mobileAdsState.intValue = MobileAdsState.CONSENT_REQUEST_ERROR
      },
    )

    // This sample attempts to load ads using consent obtained from the previous session.
    if (consentInformation.canRequestAds()) {
      initializeMobileAdsSdk(activity)
    }
  }

  /** Shows the update consent form. */
  fun showPrivacyOptionsForm(activity: Activity) {
    UserMessagingPlatform.showPrivacyOptionsForm(activity) { error ->
      if (error != null) {
        mobileAdsState.intValue = MobileAdsState.CONSENT_FORM_ERROR
      }
    }
  }

  /**
   * Initializes Mobile Ads SDK.
   *
   * @param activity Activity responsible for initializing the Google Mobile Ads SDK.
   */
  fun initializeMobileAdsSdk(activity: Activity) {
    MobileAds.initialize(activity) {
      Log.d(TAG, "Mobile Ads SDK initialized")
      mobileAdsState.intValue = MobileAdsState.INITIALIZED
    }
  }

  /**
   * Opens the Ad Inspector UI.
   *
   * @param context The application or activity context required for launching the inspector.
   * @param onAdInspectorResult A callback to handle the result of opening the Ad Inspector.
   */
  fun openAdInspector(context: Context, onAdInspectorResult: (AdError?) -> Unit) {
    MobileAds.openAdInspector(context) { error -> onAdInspectorResult(error) }
  }

  private fun showConsentFormIfRequired(activity: Activity, onFormResult: (FormError?) -> Unit) {
    UserMessagingPlatform.loadAndShowConsentFormIfRequired(activity, onFormResult)
  }

  companion object {
    const val TAG = "GoogleMobileAdsSample"
  }
}
