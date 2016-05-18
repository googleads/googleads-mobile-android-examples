package com.google.example.gms.nativeads;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.formats.NativeAppInstallAd;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeCustomTemplateAd;
import com.google.android.gms.ads.formats.NativeCustomTemplateAd.OnCustomTemplateAdLoadedListener;

/**
 * {@link AppInstallAdFetcher} loads {@link NativeAppInstallAd}s, {@link NativeContentAd}s, and
 * {@link NativeCustomTemplateAd}s from a single ad unit, and invokes methods in the corresponding
 * view holder classes to display assets.
 */
public class MultiFormatAdFetcher {
    private final Object mSyncObject = new Object();
    private String mAdUnitId;
    private String mSimpleTemplateId;
    private String mExtendedTemplateId;
    private AdLoader mAdLoader;
    private NativeCustomTemplateAd mCustomTemplateAd;
    private NativeContentAd mContentAd;
    private NativeAppInstallAd mAppInstallAd;
    private MultiFormatAdPlacement mPlacement;

    /**
     * Creates a {@link MultiFormatAdFetcher}.
     *
     * @param adUnitId           The ad unit ID used to request ads.
     * @param simpleTemplateId   The template ID used to request simple custom template ads.
     * @param extendedTemplateId The template ID used to request extended custom template ads.
     */
    public MultiFormatAdFetcher(String adUnitId, String simpleTemplateId,
                                String extendedTemplateId) {
        this.mAdUnitId = adUnitId;
        this.mSimpleTemplateId = simpleTemplateId;
        this.mExtendedTemplateId = extendedTemplateId;
    }

    public void loadAd(Context context, MultiFormatAdPlacement placement) {
        synchronized (mSyncObject) {
            mPlacement = placement;

            if ((mAdLoader != null) && mAdLoader.isLoading()) {
                Log.d(MainActivity.LOG_TAG, "MultiFormatAdFetcher is already loading an ad.");
                return;
            }

            // If an ad previously loaded, reuse it instead of requesting a new one.
            if (mCustomTemplateAd != null) {
                if (mCustomTemplateAd.getCustomTemplateId().equals(mSimpleTemplateId)) {
                    SimpleCustomTemplateAdViewHolder holder =
                            mPlacement.getSimpleCustomTemplateAdViewHolder();
                    holder.populateView(mCustomTemplateAd);
                } else {
                    ExtendedCustomTemplateAdViewHolder holder =
                            mPlacement.getExtendedCustomTemplateAdViewHolder();
                    holder.populateView(mCustomTemplateAd);
                }
                return;
            } else if (mAppInstallAd != null) {
                AppInstallAdViewHolder holder = mPlacement.getAppInstallAdViewHolder();
                holder.populateView(mAppInstallAd);
                return;
            } else if (mContentAd != null) {
                ContentAdViewHolder holder = mPlacement.getContentAdViewHolder();
                holder.populateView(mContentAd);
                return;
            }

            OnCustomTemplateAdLoadedListener simpleCustomTemplateLoadedListener =
                    new NativeCustomTemplateAd.OnCustomTemplateAdLoadedListener() {
                        @Override
                        public void onCustomTemplateAdLoaded(NativeCustomTemplateAd ad) {
                            mCustomTemplateAd = ad;
                            SimpleCustomTemplateAdViewHolder holder = mPlacement
                                    .getSimpleCustomTemplateAdViewHolder();
                            holder.populateView(mCustomTemplateAd);
                        }
                    };

            OnCustomTemplateAdLoadedListener extendedCustomTemplateLoadedListener =
                    new NativeCustomTemplateAd.OnCustomTemplateAdLoadedListener() {
                        @Override
                        public void onCustomTemplateAdLoaded(NativeCustomTemplateAd ad) {
                            mCustomTemplateAd = ad;
                            ExtendedCustomTemplateAdViewHolder holder = mPlacement
                                    .getExtendedCustomTemplateAdViewHolder();
                            holder.populateView(mCustomTemplateAd);
                        }
                    };

            NativeCustomTemplateAd.OnCustomClickListener handleCustomTemplateClick =
                    new NativeCustomTemplateAd.OnCustomClickListener() {
                        @Override
                        public void onCustomClick(NativeCustomTemplateAd ad, String assetName) {
                            if (mCustomTemplateAd.getCustomTemplateId().equals(mSimpleTemplateId)) {
                                SimpleCustomTemplateAdViewHolder holder = mPlacement
                                        .getSimpleCustomTemplateAdViewHolder();
                                holder.processClick(ad, assetName);
                            } else {
                                ExtendedCustomTemplateAdViewHolder holder = mPlacement
                                        .getExtendedCustomTemplateAdViewHolder();
                                holder.processClick(ad, assetName);
                            }
                        }
                    };

            NativeContentAd.OnContentAdLoadedListener contentAdListener =
                    new NativeContentAd.OnContentAdLoadedListener() {
                        @Override
                        public void onContentAdLoaded(NativeContentAd ad) {
                            mContentAd = ad;
                            ContentAdViewHolder holder =
                                    mPlacement.getContentAdViewHolder();
                            holder.populateView(mContentAd);
                        }
                    };

            NativeAppInstallAd.OnAppInstallAdLoadedListener appInstallAdListener =
                    new NativeAppInstallAd.OnAppInstallAdLoadedListener() {
                        @Override
                        public void onAppInstallAdLoaded(NativeAppInstallAd ad) {
                            mAppInstallAd = ad;
                            AppInstallAdViewHolder holder =
                                    mPlacement.getAppInstallAdViewHolder();
                            holder.populateView(mAppInstallAd);
                        }
                    };

            if (mAdLoader == null) {
                mAdLoader = new AdLoader.Builder(context, mAdUnitId)
                        .forCustomTemplateAd(mSimpleTemplateId,
                                simpleCustomTemplateLoadedListener,
                                handleCustomTemplateClick)
                        .forCustomTemplateAd(mExtendedTemplateId,
                                extendedCustomTemplateLoadedListener,
                                handleCustomTemplateClick)
                        .forContentAd(contentAdListener)
                        .forAppInstallAd(appInstallAdListener)
                        .withAdListener(new AdListener() {
                            @Override
                            public void onAdFailedToLoad(int errorCode) {
                                if (mPlacement.getCurrentViewHolder() != null) {
                                    mPlacement.getCurrentViewHolder().hideView();
                                }
                                Log.e(MainActivity.LOG_TAG,
                                        "Multi-Format Ad Failed to load: " + errorCode);
                            }
                        }).build();
            }

            mAdLoader.loadAd(new PublisherAdRequest.Builder().build());
        }
    }
}
