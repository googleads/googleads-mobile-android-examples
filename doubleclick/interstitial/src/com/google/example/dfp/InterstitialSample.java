package com.google.example.dfp;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherInterstitialAd;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * An {@link Activity} that requests and displays a {@link PublisherInterstitialAd}.
 */
public class InterstitialSample extends Activity {
  /** The log tag. */
  private static final String LOG_TAG = "DfpInterstitialSample";

  /** The interstitial ad unit id. Replace with your ad unit to test your interstitials.  */
  private static final String AD_UNIT_ID = "/6253334/dfp_example_ad/interstitial";

  /** Button text values for the interstitial show button. */
  private static final String TEXT_NOT_READY = "Interstitial Not Ready";
  private static final String TEXT_LOADING = "Loading Interstitial...";
  private static final String TEXT_SHOW = "Show Interstitial";
  private static final String TEXT_FAILED_TO_LOAD = "Ad Failed to Load";

  /** The interstitial ad. */
  private PublisherInterstitialAd interstitialAd;

  /** The "Show Interstitial" button. */
  private Button showButton;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    // Create an ad.
    interstitialAd = new PublisherInterstitialAd(this);
    interstitialAd.setAdUnitId(AD_UNIT_ID);

    // Set the AdListener.
    interstitialAd.setAdListener(new InterstitialAdListener());

    showButton = (Button) findViewById(R.id.showButton);
    showButton.setText(TEXT_NOT_READY);
    showButton.setEnabled(false);
  }

  /** Called when the "Load Interstitial" button is clicked. */
  public void loadInterstitial(View unusedview) {
    // Disable the show button until the new ad is loaded.
    showButton.setText(TEXT_LOADING);
    showButton.setEnabled(false);

    // Load the interstitial ad.
    interstitialAd.loadAd(new PublisherAdRequest.Builder().build());
  }

  /** Called when the "Show Interstitial" button is clicked. */
  public void showInterstitial(View unusedview) {
    // Show the interstitial if it's loaded.
    if (interstitialAd.isLoaded()) {
      interstitialAd.show();
    } else {
      Log.d(LOG_TAG, "Interstitial ad was not ready to be shown.");
    }

    // Disable the show button until another interstitial is loaded.
    showButton.setText(TEXT_NOT_READY);
    showButton.setEnabled(false);
  }

  /**
   * An ad listener that logs and toasts ad events, and enables/disables the "Show Interstitial"
   * button when appropriate.
   */
  private class InterstitialAdListener extends AdListener {
    /** Called when an ad is loaded. */
    @Override
    public void onAdLoaded() {
      Log.d(InterstitialSample.LOG_TAG, "onAdLoaded");
      Toast.makeText(InterstitialSample.this, "onAdLoaded", Toast.LENGTH_SHORT).show();

      // Change the button text and enable the show button.
      showButton.setText(TEXT_SHOW);
      showButton.setEnabled(true);
    }

    /** Called when an ad failed to load. */
    @Override
    public void onAdFailedToLoad(int errorCode) {
      String message = String.format("onAdFailedToLoad (%s)", getErrorReason(errorCode));
      Log.d(InterstitialSample.LOG_TAG, message);
      Toast.makeText(InterstitialSample.this, message, Toast.LENGTH_SHORT).show();

      // Change the button text and disable the show button.
      showButton.setText(TEXT_FAILED_TO_LOAD);
      showButton.setEnabled(false);
    }

    /**
     * Called when an Activity is created in front of the app (e.g. an interstitial is shown, or an
     * ad is clicked and launches a new Activity).
     */
    @Override
    public void onAdOpened() {
      Log.d(InterstitialSample.LOG_TAG, "onAdOpened");
      Toast.makeText(InterstitialSample.this, "onAdOpened", Toast.LENGTH_SHORT).show();
    }

    /** Called when an ad is closed and about to return to the application. */
    @Override
    public void onAdClosed() {
      Log.d(InterstitialSample.LOG_TAG, "onAdClosed");
      Toast.makeText(InterstitialSample.this, "onAdClosed", Toast.LENGTH_SHORT).show();
    }

    /**
     * Called when an ad is clicked and going to start a new Activity that will leave the
     * application (e.g. breaking out to the Browser or Maps application).
     */
    @Override
    public void onAdLeftApplication() {
      Log.d(InterstitialSample.LOG_TAG, "onAdLeftApplication");
      Toast.makeText(InterstitialSample.this, "onAdLeftApplication", Toast.LENGTH_SHORT).show();
    }

    /** Gets a string error reason from an error code. */
    private String getErrorReason(int errorCode) {
      switch(errorCode) {
        case AdRequest.ERROR_CODE_INTERNAL_ERROR:
          return "Internal error";
        case AdRequest.ERROR_CODE_INVALID_REQUEST:
          return "Invalid request";
        case AdRequest.ERROR_CODE_NETWORK_ERROR:
          return "Network Error";
        case AdRequest.ERROR_CODE_NO_FILL:
          return "No fill";
        default:
          return "Unknown error";
      }
    }
  }
}
