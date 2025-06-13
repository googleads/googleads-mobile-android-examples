package com.google.android.gms.example.apidemo

import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import java.util.Arrays

/** This snippet shows how to initialize test ads. */
class AdmobTestAds {

  fun initializeTestAds() {
    // [START initialize_test_ads]
    val testDeviceIds = Arrays.asList("33BE2250B43518CCDA7DE426D04EE231")
    val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
    MobileAds.setRequestConfiguration(configuration)
    // [END initialize_test_ads]
  }
}
