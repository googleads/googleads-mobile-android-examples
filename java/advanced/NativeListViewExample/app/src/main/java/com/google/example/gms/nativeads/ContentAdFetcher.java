package com.google.example.gms.nativeads;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.formats.NativeContentAd;

/**
 * {@link ContentAdFetcher} loads {@link NativeContentAd}s and invokes methods in the
 * {@link ContentAdViewHolder} based on whether the load was successful.
 */
public class ContentAdFetcher {
    private final Object mSyncObject = new Object();
    private AdLoader mAdLoader;
    private String mAdUnitId;
    private NativeContentAd mContentAd;
    private ContentAdViewHolder mViewHolder;

    /**
     * Creates a {@link ContentAdFetcher}.
     *
     * @param adUnitId The ad unit ID used to request ads.
     */
    public ContentAdFetcher(String adUnitId) {
        this.mAdUnitId = adUnitId;
    }

    /**
     * Loads a {@link NativeContentAd} and calls {@link ContentAdViewHolder} methods to
     * display its assets or handle failure by hiding the view.
     */
    public void loadAd(Context context, ContentAdViewHolder viewHolder) {
        synchronized (mSyncObject) {
            mViewHolder = viewHolder;

            if ((mAdLoader != null) && mAdLoader.isLoading()) {
                Log.d(MainActivity.LOG_TAG, "ContentAdFetcher is already loading an ad.");
                return;
            }

            // If an ad previously loaded, reuse it instead of requesting a new one.
            if (mContentAd != null) {
                mViewHolder.populateView(mContentAd);
                return;
            }

            NativeContentAd.OnContentAdLoadedListener contentAdListener =
                    new NativeContentAd.OnContentAdLoadedListener() {
                        public void onContentAdLoaded(NativeContentAd ad) {
                            mContentAd = ad;
                            mViewHolder.populateView(mContentAd);
                        }
                    };

            if (mAdLoader == null) {
                mAdLoader = new AdLoader.Builder(context, mAdUnitId)
                        .forContentAd(contentAdListener)
                        .withAdListener(new AdListener() {
                            @Override
                            public void onAdFailedToLoad(int errorCode) {
                                mViewHolder.hideView();
                                Log.e(MainActivity.LOG_TAG,
                                        "Content Ad Failed to load: " + errorCode);
                            }
                        }).build();
            }

            mAdLoader.loadAd(new PublisherAdRequest.Builder().build());
        }
    }

}
