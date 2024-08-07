//
//  Copyright (C) 2023 Google LLC
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//

package com.google.example.gms.fullscreennativeexample;

import android.content.Context;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MediaAspectRatio;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import java.util.Arrays;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A simple implementation of cache for preloaded native ads, this is not supposed to be used in any
 * production use case.
 */
public class NativeAdsPool {

  // Check your logcat output for the test device hashed ID e.g.
  // "Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
  // to get test ads on this device" or
  // "Use new ConsentDebugSettings.Builder().addTestDeviceHashedId("ABCDEF012345") to set this as
  // a debug device".
  public static final String TEST_DEVICE_HASHED_ID = "ABCDEF012345";

  private static final String ADMOB_AD_UNIT_ID = "ca-app-pub-3940256099942544/7342230711";

  private final Context context;
  private LinkedBlockingQueue<NativeAd> nativeAdsPool = null;
  private AdLoader adLoader;

  /** Listen to the refresh event from pool to handle new feeds. */
  public interface OnPoolRefreshedListener {
    public void onPoolRefreshed();
  }

  public NativeAdsPool(Context context) {
    this.context = context;
    nativeAdsPool = new LinkedBlockingQueue<>();
  }

  public void init(final OnPoolRefreshedListener listener) {
    // Set your test devices.
    MobileAds.setRequestConfiguration(
        new RequestConfiguration.Builder()
            .setTestDeviceIds(Arrays.asList(TEST_DEVICE_HASHED_ID))
            .build());

    new Thread(
            () -> {
              // Initialize the Google Mobile Ads SDK on a background thread.
              MobileAds.initialize(this.context, initializationStatus -> {});
            })
        .start();

    AdLoader.Builder builder = new AdLoader.Builder(this.context, ADMOB_AD_UNIT_ID);
    VideoOptions videoOptions =
        new VideoOptions.Builder().setStartMuted(false).setCustomControlsRequested(true).build();
    NativeAdOptions adOptions =
        new NativeAdOptions.Builder()
            .setMediaAspectRatio(MediaAspectRatio.PORTRAIT)
            .setVideoOptions(videoOptions)
            .build();
    builder.withNativeAdOptions(adOptions);
    builder.forNativeAd(
        nativeAd -> {
          push(nativeAd);
          listener.onPoolRefreshed();
        });
    builder.withAdListener(
        new AdListener() {
          @Override
          public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
            Log.d("XXX", loadAdError.toString());
          }
        });
    adLoader = builder.build();
  }

  public void push(NativeAd ad) {
    nativeAdsPool.add(ad);
  }

  public NativeAd pop() {
    return nativeAdsPool.poll();
  }

  public void refresh(int numberOfAds) {
    this.adLoader.loadAds(new AdRequest.Builder().build(), numberOfAds);
  }
}
