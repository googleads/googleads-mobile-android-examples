package com.google.example.gms.ads.purchase;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.purchase.InAppPurchase;
import com.google.android.gms.ads.purchase.InAppPurchaseListener;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * An {@link Activity} that requests and can display an InterstitialAd.
 */
public class IAPSample extends Activity implements InAppPurchaseListener {
  /** The log tag. */
  private static final String LOG_TAG = "IAPSample";

  /** Your ad unit id. Replace with your actual ad unit id. */
  private static final String AD_UNIT_ID = "INSERT_YOUR_AD_UNIT_ID_HERE";

  /** The interstitial ad. */
  private InterstitialAd interstitialAd;

  /** The button that show the interstitial. */
  private Button showButton;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    // Create an ad.
    interstitialAd = new InterstitialAd(this);
    interstitialAd.setAdUnitId(AD_UNIT_ID);
    
    //Set the IAP Listener.
    interstitialAd.setInAppPurchaseListener(this);


    // Set the AdListener.
    interstitialAd.setAdListener(new AdListener() {
      @Override
      public void onAdLoaded() {
        Log.d(LOG_TAG, "onAdLoaded");
        Toast.makeText(IAPSample.this, "onAdLoaded", Toast.LENGTH_SHORT).show();

        // Change the button text and enable the button.
        showButton.setText("Show Interstitial");
        showButton.setEnabled(true);
      }

      @Override
      public void onAdFailedToLoad(int errorCode) {
        String message = String.format("onAdFailedToLoad (%s)", getErrorReason(errorCode));
        Log.d(LOG_TAG, message);
        Toast.makeText(IAPSample.this, message, Toast.LENGTH_SHORT).show();

        // Change the button text and disable the button.
        showButton.setText("Ad Failed to Load");
        showButton.setEnabled(false);
      }
    });

    showButton = (Button) findViewById(R.id.showButton);
    showButton.setEnabled(false);
  }

  /** Called when the Load Interstitial button is clicked. */
  public void loadInterstitial(View unusedView) {
    // Disable the show button until the new ad is loaded.
    showButton.setText("Loading Interstitial...");
    showButton.setEnabled(false);

    // Check the logcat output for your hashed device ID to get test ads on a physical device.
    AdRequest adRequest = new AdRequest.Builder()
         // You will probably want to remove test devices
         // in order to make sure you get back your ad.
        .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
        .addTestDevice("INSERT_YOUR_HASHED_DEVICE_ID_HERE")
        .build();

    // Load the interstitial ad.
    interstitialAd.loadAd(adRequest);
  }

  /** Called when the Show Interstitial button is clicked. */
  public void showInterstitial(View unusedView) {
    // Disable the show button until another interstitial is loaded.
    showButton.setText("Interstitial Not Ready");
    showButton.setEnabled(false);

    if (interstitialAd.isLoaded()) {
      interstitialAd.show();
    } else {
      Log.d(LOG_TAG, "Interstitial ad was not ready to be shown.");
    }
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

  
  // Implement method for InAppPurchaseListener.
  @Override
  public void onInAppPurchase(InAppPurchase inAppPurchase) {
    String message = String.format("onInAppPurchase. Product Id: %s", inAppPurchase.getProductId());
    Log.d(LOG_TAG, message);
    Toast.makeText(IAPSample.this, message, Toast.LENGTH_SHORT).show();
    
    // At this point, you can use the purchase information to launch an in-app purchase request to
    // the user. When the transaction is complete, make sure to call recordResolution to record
    // the result. In practice, this will be called asychonronously when the purchase is complete.
    
    inAppPurchase.recordResolution(0);
    
  }
}
