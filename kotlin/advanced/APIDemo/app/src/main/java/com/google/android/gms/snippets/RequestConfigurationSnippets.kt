package com.google.android.gms.snippets

import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import java.util.Arrays

/** Kotlin code snippets for the developer guide. */
private class RequestConfigurationSnippets {

  private fun setTestDeviceIds() {
    // [START set_test_device_ids]
    val testDeviceIds = Arrays.asList("33BE2250B43518CCDA7DE426D04EE231")
    val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
    MobileAds.setRequestConfiguration(configuration)
    // [END set_test_device_ids]
  }
}
