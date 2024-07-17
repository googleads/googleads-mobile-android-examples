package com.google.android.gms.example.jetpackcomposedemo

/** State for the Google Mobile Ads initialization process. */
data class GoogleMobileAdsUIState(
  /** Represents current initialization states for the Google Mobile Ads SDK. */
  val isMobileAdsInitialized: Boolean = false,
  /** Indicates whether the app has completed the steps for gathering updated user consent. */
  val canRequestAds: Boolean = false,
  /** Helper variable to determine if the privacy options form is required. */
  val isPrivacyOptionsRequired: Boolean = false,
)
