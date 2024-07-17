package com.google.android.gms.example.jetpackcomposedemo

import android.app.Activity
import android.content.Context
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.OnAdInspectorClosedListener
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import com.google.android.ump.ConsentForm
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/** State holder for the Google Mobile Ads initialization process. */
class GoogleMobileAdsViewModel {

  private lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager
  private var isMobileAdsInitialized = false
  private val _uiState = MutableStateFlow(GoogleMobileAdsUIState())

  /** UIState for the ViewModel. */
  val uiState: StateFlow<GoogleMobileAdsUIState> = _uiState.asStateFlow()

  /** Sets initial UIState for the ViewModel. */
  fun init(context: Context) {
    googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(context)
  }

  /** Helper method to open the ad inspector. */
  fun openAdInspector(context: Context, listener: OnAdInspectorClosedListener) {
    MobileAds.openAdInspector(context, listener)
  }

  /** Helper method to call the UMP SDK method to show the privacy options form. */
  fun showPrivacyOptionsForm(
    activity: Activity,
    onConsentFormDismissedListener: ConsentForm.OnConsentFormDismissedListener,
  ) {
    googleMobileAdsConsentManager.showPrivacyOptionsForm(activity) { error ->
      updateUIState()
      onConsentFormDismissedListener.onConsentFormDismissed(error)
    }
  }

  /**
   * Helper method to call the UMP SDK methods to request consent information and load/show a
   * consent form if necessary.
   */
  fun gatherConsent(
    activity: Activity,
    onConsentGatheringCompleteListener:
      GoogleMobileAdsConsentManager.OnConsentGatheringCompleteListener,
  ) {
    googleMobileAdsConsentManager.gatherConsent(activity) { error ->
      updateUIState()
      onConsentGatheringCompleteListener.consentGatheringComplete(error)
    }
  }

  /** Helper method to call the MobileAds initialize. */
  fun initialize(context: Context, listener: OnInitializationCompleteListener) {
    MobileAds.initialize(context) { status ->
      isMobileAdsInitialized = true
      updateUIState()
      listener.onInitializationComplete(status)
    }
  }

  /** Helper method to update UIState. */
  private fun updateUIState() {
    _uiState.update {
      it.copy(
        canRequestAds = googleMobileAdsConsentManager.canRequestAds,
        isPrivacyOptionsRequired = googleMobileAdsConsentManager.isPrivacyOptionsRequired,
        isMobileAdsInitialized = isMobileAdsInitialized,
      )
    }
  }

  companion object {
    @Volatile private var instance: GoogleMobileAdsViewModel? = null

    fun getInstance() =
      instance
        ?: synchronized(this) { instance ?: GoogleMobileAdsViewModel().also { instance = it } }
  }
}
