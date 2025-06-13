package com.google.android.snippets;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import java.util.Arrays;

/** This snippet shows how to initialize test ads. */
final class AdmobTestAds {

  private AdmobTestAds() {}

  public static void initializeTestAds() {
    // [START initialize_test_ads]
    java.util.List<String> testDeviceIds = Arrays.asList("33BE2250B43518CCDA7DE426D04EE231");
    RequestConfiguration configuration =
        new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
    MobileAds.setRequestConfiguration(configuration);
    // [END initialize_test_ads]
  }
}
