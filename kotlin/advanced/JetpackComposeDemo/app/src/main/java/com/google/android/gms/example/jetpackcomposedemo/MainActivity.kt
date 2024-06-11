/*
 * Copyright 2024 Google LLC
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

package com.google.android.gms.example.jetpackcomposedemo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.example.jetpackcomposedemo.R
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.example.jetpackcomposedemo.ui.TextButton
import com.google.android.gms.example.jetpackcomposedemo.ui.theme.ColorStateError
import com.google.android.gms.example.jetpackcomposedemo.ui.theme.ColorStateLoaded
import com.google.android.gms.example.jetpackcomposedemo.ui.theme.ColorStateUnloaded
import com.google.android.gms.example.jetpackcomposedemo.ui.theme.JetpackComposeDemoTheme
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentRequestParameters

class MainActivity : ComponentActivity() {

  // This instance manages the process initializing Google Mobile Ads.
  private val adsManager = GoogleMobileAdsManager()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    // Initialize the Google Mobile Ads SDK.
    if (adsManager.mobileAdsState.intValue != GoogleMobileAdsManager.MobileAdsState.INITIALIZED) {
      initMobileAds()
    }

    setContent {
      JetpackComposeDemoTheme {
        Surface(modifier = Modifier.fillMaxHeight(), color = MaterialTheme.colorScheme.background) {
          MainScreen()
        }
      }
    }
  }

  private fun initMobileAds() {
    // Always use test ads: https://developers.google.com/admob/android/test-ads#kotlin
    val testDeviceIds = listOf("33BE2250B43518CCDA7DE426D04EE231")

    // Configure RequestConfiguration.
    val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
    MobileAds.setRequestConfiguration(configuration)

    val debugSettings = ConsentDebugSettings.Builder(this)
    // For testing purposes, you can force a DebugGeography of EEA or NOT_EEA.
    // debugSettings.setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA);

    testDeviceIds.forEach { deviceId -> debugSettings.addTestDeviceHashedId(deviceId) }
    val consentRequestParameters =
      ConsentRequestParameters.Builder().setConsentDebugSettings(debugSettings.build()).build()

    adsManager.initializeWithConsent(this, consentRequestParameters)
  }

  @Composable
  @Preview
  fun MainScreenPreview() {
    JetpackComposeDemoTheme {
      Surface(modifier = Modifier.fillMaxHeight(), color = MaterialTheme.colorScheme.background) {
        MainScreen()
      }
    }
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  fun MainScreen() {
    val context = LocalContext.current
    val activity = this

    Column(
      modifier = Modifier.verticalScroll(rememberScrollState()),
      content = {
        // Render title.
        TopAppBar(title = { Text(resources.getString(R.string.main_title)) })
        // Render mobile ads status.
        Box(
          modifier =
            Modifier.fillMaxSize().background(adsManager.mobileAdsState.intValue.messageColor())
        ) {
          Text(
            text = adsManager.mobileAdsState.intValue.messageText(),
            style = MaterialTheme.typography.bodyLarge,
          )
        }
        // Show Consent Form.
        TextButton(
          name = resources.getString(R.string.consent_show),
          enabled = adsManager.isPrivacyOptionsRequired.value,
        ) {
          adsManager.showPrivacyOptionsForm(activity)
        }
        // Open Ad Inspector.
        TextButton(name = resources.getString(R.string.adinspector)) {
          adsManager.openAdInspector(context) { error ->
            if (error != null) {
              Toast.makeText(
                  context,
                  resources.getString(R.string.adinspector_error),
                  Toast.LENGTH_LONG,
                )
                .show()
              Log.e(
                TAG,
                String.format(resources.getString(R.string.adinspector_error), error.message),
              )
            }
          }
        }
        // Banner Sample.
        TextButton(name = "Banner", enabled = adsManager.canRequestAds.value) {
          val intent = Intent(context, BannerActivity::class.java)
          context.startActivity(intent)
        }
      },
    )
  }

  // Extend MobileAdsState with message color.
  private fun @GoogleMobileAdsManager.MobileAdsState.State Int.messageColor(): Color {
    return when (this) {
      GoogleMobileAdsManager.MobileAdsState.CONSENT_OBTAINED -> ColorStateUnloaded
      GoogleMobileAdsManager.MobileAdsState.CONSENT_REQUEST_ERROR -> ColorStateError
      GoogleMobileAdsManager.MobileAdsState.CONSENT_FORM_ERROR -> ColorStateError
      GoogleMobileAdsManager.MobileAdsState.INITIALIZED -> ColorStateLoaded
      else -> ColorStateUnloaded
    }
  }

  // Extend MobileAdsState with message text.
  private fun @GoogleMobileAdsManager.MobileAdsState.State Int.messageText(): String {
    return when (this) {
      GoogleMobileAdsManager.MobileAdsState.UNINITIALIZED ->
        resources.getString(R.string.mobileads_uninitialized)
      GoogleMobileAdsManager.MobileAdsState.CONSENT_REQUIRED ->
        resources.getString(R.string.mobileads_consentRequired)
      GoogleMobileAdsManager.MobileAdsState.CONSENT_OBTAINED ->
        resources.getString(R.string.mobileads_consentObtained)
      GoogleMobileAdsManager.MobileAdsState.CONSENT_REQUEST_ERROR ->
        resources.getString(R.string.mobileads_consentError)
      GoogleMobileAdsManager.MobileAdsState.CONSENT_FORM_ERROR ->
        resources.getString(R.string.mobileads_consentFormError)
      GoogleMobileAdsManager.MobileAdsState.INITIALIZED ->
        resources.getString(R.string.mobileads_initialized)
      else -> resources.getString(R.string.mobileads_uninitialized)
    }
  }

  companion object {
    const val TAG = "GoogleMobileAdsSample"
  }
}
