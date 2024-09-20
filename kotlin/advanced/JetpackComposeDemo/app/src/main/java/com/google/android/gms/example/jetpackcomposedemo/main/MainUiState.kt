package com.google.android.gms.example.jetpackcomposedemo.main

/** UiState for the MainViewModel. */
data class MainUiState(
  /** Represents current initialization states for the Google Mobile Ads SDK. */
  val isMobileAdsInitialized: Boolean = false,
  /** Indicates whether the app has completed the steps for gathering updated user consent. */
  val canRequestAds: Boolean = false,
  /** Indicates whether a privacy options form is required. */
  val isPrivacyOptionsRequired: Boolean = false,
)
