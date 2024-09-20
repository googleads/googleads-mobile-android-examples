package com.google.android.gms.example.jetpackcomposedemo.main

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.OnAdInspectorClosedListener
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.example.jetpackcomposedemo.GoogleMobileAdsApplication
import com.google.android.gms.example.jetpackcomposedemo.GoogleMobileAdsConsentManager
import com.google.android.ump.ConsentForm
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext

/** State holder for the Google Mobile Ads initialization process. */
class MainViewModel {

  private val _isInitCalled = AtomicBoolean(false)
  private val _isMobileAdsInitializeCalled = AtomicBoolean(false)
  private val _uiState = MutableStateFlow(MainUiState())
  private lateinit var _googleMobileAdsConsentManager: GoogleMobileAdsConsentManager

  /** UIState for the ViewModel. */
  val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()

  /** Initialization state for the ViewModel. */
  val isInitCalled: Boolean
    get() = _isInitCalled.get()

  /** Sets initial UIState for the ViewModel. */
  suspend fun init(activity: Activity) {
    _isInitCalled.set(true)
    _googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(activity)

    // Initializes the consent manager and calls the UMP SDK methods to request consent information
    // and load/show a consent form if necessary.
    gatherConsent(activity) { error ->
      if (error != null) {
        // Consent not obtained in current session.
        Log.d(GoogleMobileAdsApplication.TAG, "${error.errorCode}: ${error.message}")
      }
      // canRequestAds can be updated when gatherConsent is completed.
      _uiState.update { it.copy(canRequestAds = _googleMobileAdsConsentManager.canRequestAds) }
    }
    // canRequestAds can be updated when gatherConsent is called.
    _uiState.update { it.copy(canRequestAds = _googleMobileAdsConsentManager.canRequestAds) }

    // when canRequestAds is true initializeMobileAdsSdk
    uiState.collect { state ->
      if (state.canRequestAds) {
        initializeMobileAdsSdk(activity)
      }
    }
  }

  /** Opens the ad inspector. */
  fun openAdInspector(context: Context, listener: OnAdInspectorClosedListener?) {
    MobileAds.openAdInspector(context) { error ->
      if (error != null) {
        val errorMessage = error.message
        Log.e(GoogleMobileAdsApplication.TAG, errorMessage)
        Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
      }
      // Notify listener of ad inspector closed.
      listener?.onAdInspectorClosed(error)
    }
  }

  /** Calls the UMP SDK method to show the privacy options form. */
  fun showPrivacyOptionsForm(
    activity: Activity,
    onConsentFormDismissedListener: ConsentForm.OnConsentFormDismissedListener?,
  ) {
    _googleMobileAdsConsentManager.showPrivacyOptionsForm(activity) { error ->
      if (error != null) {
        val errorMessage = error.message
        Log.e(GoogleMobileAdsApplication.TAG, errorMessage)
        Toast.makeText(activity, errorMessage, Toast.LENGTH_SHORT).show()
      }
      // Notify listener of consent form dismissal.
      onConsentFormDismissedListener?.onConsentFormDismissed(error)
    }
  }

  /** Calls the UMP SDK methods to gatherConsent. */
  private fun gatherConsent(
    activity: Activity,
    onConsentGatheringCompleteListener:
      GoogleMobileAdsConsentManager.OnConsentGatheringCompleteListener,
  ) {
    _googleMobileAdsConsentManager.gatherConsent(activity) { error ->
      // Update UIState and notify listener of updated consent status.
      _uiState.update {
        it.copy(
          canRequestAds = _googleMobileAdsConsentManager.canRequestAds,
          isPrivacyOptionsRequired = _googleMobileAdsConsentManager.isPrivacyOptionsRequired,
        )
      }
      onConsentGatheringCompleteListener.consentGatheringComplete(error)
    }
    // Update UIState based on consent obtained in the previous session.
    _uiState.update {
      it.copy(
        canRequestAds = _googleMobileAdsConsentManager.canRequestAds,
        isPrivacyOptionsRequired = _googleMobileAdsConsentManager.isPrivacyOptionsRequired,
      )
    }
  }

  /** Initializes the Mobile Ads SDK. */
  private suspend fun initializeMobileAdsSdk(context: Context) {

    // Ensure that MobileAdsInitialize is called only once.
    if (_isMobileAdsInitializeCalled.getAndSet(true)) {
      return
    }

    // Set your test devices.
    MobileAds.setRequestConfiguration(
      RequestConfiguration.Builder().setTestDeviceIds(listOf(TEST_DEVICE_HASHED_ID)).build()
    )

    // Initialize the Google Mobile Ads SDK on a background thread.
    withContext(Dispatchers.IO) {
      MobileAds.initialize(context) { _uiState.update { it.copy(isMobileAdsInitialized = true) } }
    }
  }

  companion object {
    // Check your logcat output for the test device hashed ID e.g.
    // "Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
    // to get test ads on this device" or
    // "Use new ConsentDebugSettings.Builder().addTestDeviceHashedId("ABCDEF012345") to set this as
    // a debug device".
    const val TEST_DEVICE_HASHED_ID = "ABCDEF012345"

    @Volatile private var instance: MainViewModel? = null

    fun getInstance() =
      instance ?: synchronized(this) { instance ?: MainViewModel().also { instance = it } }
  }
}
