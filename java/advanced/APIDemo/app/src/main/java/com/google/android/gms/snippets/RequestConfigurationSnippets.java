package com.google.android.gms.snippets;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import java.util.Arrays;
import java.util.List;

/** Java code snippets for the developer guide. */
final class RequestConfigurationSnippets {

  public static final String TEST_DEVICE_ID = "33BE2250B43518CCDA7DE426D04EE231";

  private void setTestDeviceIds() {
    // [START set_test_device_ids]
    List<String> testDeviceIds = Arrays.asList(TEST_DEVICE_ID);
    RequestConfiguration configuration =
        new RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build();
    MobileAds.setRequestConfiguration(configuration);
    // [END set_test_device_ids]
  }
}
