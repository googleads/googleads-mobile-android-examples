package com.google.example.dfp;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.doubleclick.AppEventListener;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.Toast;

/**
 * This class shows how to insert a DFP Banner into your application.
 *
 * There are three examples included:
 *   1. Standard DFP banner
 *   2. Multiple Ad Sizes
 *   3. App Events
 *
 * Change the SAMPLE_TO_RUN value to run a specific sample. See the {@link DfpSample} enumeration
 * for possible samples to run.
 */
public class BannerSamples extends Activity implements AppEventListener {
  /** The Sample to run. Change this value to run a different sample. */
  private static final DfpSample SAMPLE_TO_RUN = DfpSample.APP_EVENTS;

  /** The log tag. */
  public static final String LOG_TAG = "DfpBannerSample";

  /** The view to show the ad. */
  private PublisherAdView adView;

  /** Called when the activity is first created. */
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // The initialization depends on the type of example you wish to run. Change the value of
    // SAMPLE_TO_RUN to run a different sample.
    switch (SAMPLE_TO_RUN) {
      case STANDARD_BANNER:
        // Example 1. Using sample DFP banner.
        setContentView(R.layout.main);
        adView = new PublisherAdView(this);
        adView.setAdUnitId(DfpSample.STANDARD_BANNER.adunitId);
        adView.setAdSizes(AdSize.BANNER);
        break;
      case MULTIPLE_AD_SIZES:
        // Example 2. Using multiple ad sizes.
        // This ad unit supports the following sizes: (320x250, 300x250, 120x20)
        setContentView(R.layout.refresh);
        adView = new PublisherAdView(this);
        adView.setAdUnitId(DfpSample.MULTIPLE_AD_SIZES.adunitId);
        adView.setAdSizes(AdSize.BANNER, AdSize.MEDIUM_RECTANGLE, new AdSize(120, 20));
        break;
      case APP_EVENTS:
        // Example 3. Using app events.
        setContentView(R.layout.refresh);
        adView = new PublisherAdView(this);
        adView.setAdUnitId(DfpSample.APP_EVENTS.adunitId);
        adView.setAdSizes(AdSize.BANNER);
        adView.setAppEventListener(this);
        break;
    }

    /** The implementation below is the same for all examples. */
    // Set the AdListener to listen for standard ad events.
    adView.setAdListener(new LoggingAdListener());

    // Lookup your LinearLayout assuming it’s been given the attribute
    // android:id="@+id/mainLayout".
    LinearLayout layout = (LinearLayout) findViewById(R.id.mainLayout);

    // Add the adView to it.
    layout.addView(adView,
        new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

    // Initiate an request to load the AdView with an ad.
    adView.loadAd(new PublisherAdRequest.Builder().build());
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

  @Override
  public void onDestroy() {
    if (adView != null) {
      adView.destroy();
    }
    super.onDestroy();
  }

  /** Called when the refresh button is clicked. */
  public void refreshAd(View unusedView) {
    if (adView != null) {
      adView.loadAd(new PublisherAdRequest.Builder().build());
    }
  }

  /**
   * Called when a DFP creative invokes an app event.
   *
   * The app event creative is set up to send color=red when the ad is loaded, color=green when the
   * ad is clicked, and color=blue after 5 seconds. This example will listen for these events to
   * change the app's background color.
   */
  @Override
  public void onAppEvent(String name, String info) {
    String message = String.format("Received app event (%s, %s)", name, info);
    Log.d(LOG_TAG, message);
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    if ("color".equals(name)) {
      LinearLayout layout = (LinearLayout) findViewById(R.id.mainLayout);
      if ("red".equals(info)) {
        layout.setBackgroundColor(Color.RED);
      } else if ("green".equals(info)) {
        layout.setBackgroundColor(Color.GREEN);
      } else if ("blue".equals(info)) {
        layout.setBackgroundColor(Color.BLUE);
      }
    }
  }
}
