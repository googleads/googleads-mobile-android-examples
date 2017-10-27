package com.google.example.gms.nativeads;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.formats.NativeCustomTemplateAd;

/**
 * {@link SimpleCustomTemplateAdFetcher} loads {@link NativeCustomTemplateAd}s and invokes methods
 * in the {@link SimpleCustomTemplateAdViewHolder} interface based on whether the load was
 * successful.
 */
public class SimpleCustomTemplateAdFetcher {
    private final Object mSyncObject = new Object();
    private AdLoader mAdLoader;
    private String mAdUnitId;
    private String mTemplateId;
    private NativeCustomTemplateAd mCustomTemplateAd;
    private SimpleCustomTemplateAdViewHolder mViewHolder;

    /**
     * Creates a {@link SimpleCustomTemplateAdFetcher}.
     *
     * @param adUnitId   The ad unit ID used to request ads.
     * @param templateId The custom template ID used to request ads.
     */
    public SimpleCustomTemplateAdFetcher(String adUnitId, String templateId) {
        this.mAdUnitId = adUnitId;
        this.mTemplateId = templateId;
    }

    /**
     * Loads a {@link NativeCustomTemplateAd} and calls {@link SimpleCustomTemplateAdViewHolder}
     * methods to display its assets or handle failure.
     */
    public void loadAd(Context context, SimpleCustomTemplateAdViewHolder viewHolder) {
        synchronized (mSyncObject) {
            mViewHolder = viewHolder;

            if ((mAdLoader != null) && mAdLoader.isLoading()) {
                Log.d(MainActivity.LOG_TAG,
                        "SimpleCustomTemplateAdFetcher is already loading an ad.");
                return;
            }

            // If an ad previously loaded, reuse it instead of requesting a new one.
            if (mCustomTemplateAd != null) {
                mViewHolder.populateView(mCustomTemplateAd);
                return;
            }

            NativeCustomTemplateAd.OnCustomTemplateAdLoadedListener templateLoadedListener =
                    new NativeCustomTemplateAd.OnCustomTemplateAdLoadedListener() {
                        public void onCustomTemplateAdLoaded(NativeCustomTemplateAd ad) {
                            mCustomTemplateAd = ad;
                            mViewHolder.populateView(mCustomTemplateAd);
                        }
                    };

            NativeCustomTemplateAd.OnCustomClickListener handleCustomClick =
                    new NativeCustomTemplateAd.OnCustomClickListener() {
                        @Override
                        public void onCustomClick(NativeCustomTemplateAd ad, String assetName) {
                            mViewHolder.processClick(ad, assetName);
                        }
                    };

            if (mAdLoader == null) {
                mAdLoader = new AdLoader.Builder(context, mAdUnitId)
                        .forCustomTemplateAd(mTemplateId, templateLoadedListener, handleCustomClick)
                        .withAdListener(new AdListener() {
                            @Override
                            public void onAdFailedToLoad(int errorCode) {
                                mViewHolder.hideView();
                                Log.e(MainActivity.LOG_TAG,
                                        "Simple Custom Template Ad Failed to load: " + errorCode);
                            }
                        }).build();
            }

            mAdLoader.loadAd(new PublisherAdRequest.Builder().build());
        }
    }
}
