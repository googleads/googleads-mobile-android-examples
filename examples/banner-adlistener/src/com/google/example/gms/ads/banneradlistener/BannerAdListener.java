package com.google.example.gms.ads.banneradlistener;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * A more advanced {@link Activity} that embeds an AdView and implements its
 * listener and sets an AdListener.
 */
public class BannerAdListener extends Activity {
  /** Your ad unit id*/
  private static final String AD_UNIT_ID = "INSERT_YOUR_AD_UNIT_ID_HERE";
  
  /** The log tag. */
  private static final String LOG_TAG = "BannerAdListener";

  /** The view to show the ad. */
  private AdView adView;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    // Create an ad.
    adView = new AdView(this);
    adView.setAdSize(AdSize.BANNER);
    adView.setAdUnitId(AD_UNIT_ID);

    // Set the AdListener.
    adView.setAdListener(new AdListener() {
      /** Called when an ad is clicked and about to return to the application. */
      @Override
      public void onAdClosed() {
        Log.d(LOG_TAG, "onAdClosed");
        Toast.makeText(BannerAdListener.this, "onAdClosed", Toast.LENGTH_SHORT).show();
      }

      /** Called when an ad failed to load. */
      @Override
      public void onAdFailedToLoad(int error) {
        String message = "onAdFailedToLoad: " + getErrorReason(error);
        Log.d(LOG_TAG, message);
        Toast.makeText(BannerAdListener.this, message, Toast.LENGTH_SHORT).show();
      }

      /**
       * Called when an ad is clicked and going to start a new Activity that will
       * leave the application (e.g. breaking out to the Browser or Maps
       * application).
       */
      @Override
      public void onAdLeftApplication() {
        Log.d(LOG_TAG, "onAdLeftApplication");
        Toast.makeText(BannerAdListener.this, "onAdLeftApplication", Toast.LENGTH_SHORT).show();
      }

      /**
       * Called when an Activity is created in front of the app (e.g. an
       * interstitial is shown, or an ad is clicked and launches a new Activity).
       */
      @Override
      public void onAdOpened() {
        Log.d(LOG_TAG, "onAdOpened");
        Toast.makeText(BannerAdListener.this, "onAdOpened", Toast.LENGTH_SHORT).show();
      }

      /** Called when an ad is loaded. */
      @Override
      public void onAdLoaded() {
        Log.d(LOG_TAG, "onAdLoaded");
        Toast.makeText(BannerAdListener.this, "onAdLoaded", Toast.LENGTH_SHORT).show();
      }
    });    
    // Add the AdView to the view hierarchy.
    LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayout);
    layout.addView(adView);

    // Create an ad request. Check logcat output for the hashed device ID to
    // get test ads on a physical device.
    AdRequest adRequest = new AdRequest.Builder()
        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
        .addTestDevice("INSERT_YOUR_HASHED_DEVICE_ID_HERE")
        .build();

    // Start loading the ad in the background.
    adView.loadAd(adRequest);
  }

  @Override
  public void onResume() {
    super.onResume();
    if (adView != null) {
      adView.resume();
    }
  }

  @Override
  public void onPause() {
    if (adView != null) {
      adView.pause();
    }
    super.onPause();
  }
  
  /** Called before the activity is destroyed. */
  @Override
  public void onDestroy() {
    if (adView != null) {
      // Destroy the AdView.
      adView.destroy();
    }
    super.onDestroy();
  }
  
  /** Gets a string error reason from an error code. */
  private String getErrorReason(int errorCode) {
    String errorReason = "";
    switch(errorCode) {
      case AdRequest.ERROR_CODE_INTERNAL_ERROR:
        errorReason = "Internal error";
        break;
      case AdRequest.ERROR_CODE_INVALID_REQUEST:
        errorReason = "Invalid request";
        break;
      case AdRequest.ERROR_CODE_NETWORK_ERROR:
        errorReason = "Network Error";
        break;
      case AdRequest.ERROR_CODE_NO_FILL:
        errorReason = "No fill";
        break;
    }
    return errorReason;
  }

}
