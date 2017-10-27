package com.google.example.gms.nativeads;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.formats.NativeAppInstallAd;

/**
 * {@link AppInstallAdFetcher} loads {@link NativeAppInstallAd}s and invokes methods in the
 * {@link AppInstallAdViewHolder} based on whether the load was successful.
 */
public class AppInstallAdFetcher {
    private final Object mSyncObject = new Object();
    private AdLoader mAdLoader;
    private String mAdUnitId;
    private NativeAppInstallAd mAppInstallAd;
    private AppInstallAdViewHolder mViewHolder;

    /**
     * Creates a {@link AppInstallAdFetcher}.
     *
     * @param adUnitId The ad unit ID used to request ads.
     */
    public AppInstallAdFetcher(String adUnitId) {
        this.mAdUnitId = adUnitId;
    }

    /**
     * Loads a {@link NativeAppInstallAd} and calls {@link AppInstallAdViewHolder} methods to
     * display its assets or handle failure by hiding the view.
     */
    public void loadAd(Context context,
                       AppInstallAdViewHolder viewHolder) {
        synchronized (mSyncObject) {
            mViewHolder = viewHolder;

            if ((mAdLoader != null) && mAdLoader.isLoading()) {
                Log.d(MainActivity.LOG_TAG, "AppInstallAdFetcher is already loading an ad.");
                return;
            }

            // If an ad previously loaded, reuse it instead of requesting a new one.
            if (mAppInstallAd != null) {
                mViewHolder.populateView(mAppInstallAd);
                return;
            }

            NativeAppInstallAd.OnAppInstallAdLoadedListener appInstallAdListener =
                    new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
                        public void onAppInstallAdLoaded(NativeAppInstallAd ad) {
                            mAppInstallAd = ad;
                            mViewHolder.populateView(mAppInstallAd);
                        }
                    };

            if (mAdLoader == null) {
                mAdLoader = new AdLoader.Builder(context, mAdUnitId)
                        .forAppInstallAd(appInstallAdListener)
                        .withAdListener(new AdListener() {
                            @Override
                            public void onAdFailedToLoad(int errorCode) {
                                mViewHolder.hideView();
                                Log.e(MainActivity.LOG_TAG,
                                        "App Install Ad Failed to load: " + errorCode);
                            }
                        }).build();
            }

            mAdLoader.loadAd(new PublisherAdRequest.Builder().build());
        }
    }
}
