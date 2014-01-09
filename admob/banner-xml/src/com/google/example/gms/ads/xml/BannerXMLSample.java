package com.google.example.gms.ads.xml;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import android.app.Activity;
import android.os.Bundle;

/**
 * A simple {@link Activity} which embeds an AdView in its layout XML.
 */
public class BannerXMLSample extends Activity {

  private static final String TEST_DEVICE_ID = "INSERT_YOUR_TEST_DEVICE_ID_HERE";

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    // The "loadAdOnCreate" and "testDevices" XML attributes no longer available.
    AdView adView = (AdView) this.findViewById(R.id.adView);
    AdRequest adRequest = new AdRequest.Builder()
        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
        .addTestDevice(TEST_DEVICE_ID)
        .build();
    adView.loadAd(adRequest);
  }
}
