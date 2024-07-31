package com.google.example.gms.fullscreennativeexample;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import androidx.annotation.NonNull;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.nativead.NativeAd;

/** Application class that loads a native ad.. */
public class MyApplication extends Application {
  private NativeAdManager nativeAdManager;

  @Override
  public void onCreate() {
    super.onCreate();

    nativeAdManager = new NativeAdManager();
  }

  public NativeAd getNativeAd() {
    return nativeAdManager.getNativeAd();
  }

  /**
   * Load an app open ad.
   *
   * @param activity the activity that shows the app open ad
   */
  public void loadAd(
      @NonNull Activity activity,
      @NonNull NativeAdEventListener nativeAdEventListener) {
    // We wrap the loadAd to enforce that other classes only interact with MyApplication
    // class.
    nativeAdManager.loadAd(activity, nativeAdEventListener);
  }

  /**
   * Interface definition for a callback to be invoked when an app open ad is complete (i.e.
   * dismissed or fails to show).
   */
  public interface NativeAdEventListener {
    void onNativeAdLoaded(boolean success);
  }

  /** Inner class that loads and shows native ads. */
  private class NativeAdManager {
    private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110";
    private NativeAd nativeAd;

    public NativeAd getNativeAd() {
      return nativeAd;
    }

    /** Constructor. */
    public NativeAdManager() {}

    /**
     * Creates a request for a new native ad based on the boolean parameters and calls the
     * corresponding "populate" method when one is successfully returned.
     */
    public void loadAd(Context context, @NonNull NativeAdEventListener nativeAdEventListener) {
      AdLoader adLoader =
          new AdLoader.Builder(context, AD_UNIT_ID)
              .forNativeAd(nativeAd -> {
                this.nativeAd = nativeAd;
                nativeAdEventListener.onNativeAdLoaded(true);
              })
              .withAdListener(new AdListener() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                  nativeAdEventListener.onNativeAdLoaded(false);
                }
              })
              .build();

      adLoader.loadAd(new AdRequest.Builder().build());
    }
  }
}
