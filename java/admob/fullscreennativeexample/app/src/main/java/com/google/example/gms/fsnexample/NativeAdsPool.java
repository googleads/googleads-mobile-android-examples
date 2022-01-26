package com.google.example.gms.fsnexample;

import android.content.Context;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MediaAspectRatio;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A simple implementation of cache for preloaded native ads, this is not supposed to be used in
 * any production use case
 */
public class NativeAdsPool {
    private static final String ADMOB_AD_UNIT_ID = "ca-app-pub-3940256099942544/7342230711";

    private final Context ctx;
    private LinkedBlockingQueue<NativeAd> mPool = null;
    private AdLoader mAdLoader;
    /**
     *  Listen to the refresh event from pool to handle new feeds
     */
    public interface OnPoolRefreshedListener {
        public void onPoolRefreshed();
    }

    public NativeAdsPool(Context ctx) {
        this.ctx = ctx;
        mPool = new LinkedBlockingQueue<>();
    }

    public void init(final OnPoolRefreshedListener cb) {
        MobileAds.initialize(this.ctx);
        AdLoader.Builder builder = new AdLoader.Builder(this.ctx, ADMOB_AD_UNIT_ID);
        VideoOptions videoOptions =
                new VideoOptions.Builder()
                        .setStartMuted(false)
                        .setCustomControlsRequested(true)
                        .build();
        NativeAdOptions adOptions =
                new NativeAdOptions.Builder()
                        .setMediaAspectRatio(MediaAspectRatio.ANY)
                        .setVideoOptions(videoOptions).build();
        builder.withNativeAdOptions(adOptions);
        builder.forNativeAd(
                new NativeAd.OnNativeAdLoadedListener() {
                    @Override
                    public void onNativeAdLoaded(final NativeAd nativeAd) {
                        push(nativeAd);
                        cb.onPoolRefreshed();
                    }
                });
        mAdLoader = builder.build();
    }

    public void push(NativeAd ad) {
        mPool.add(ad);
    }

    public NativeAd pop() {
        return mPool.poll();
    }

    public void refresh(int n) {
        this.mAdLoader.loadAds(new AdRequest.Builder().build(), n);
    }
}
