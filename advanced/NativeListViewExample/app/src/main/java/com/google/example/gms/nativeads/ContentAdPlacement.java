package com.google.example.gms.nativeads;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.ads.formats.NativeContentAdView;

/**
 * The {@link ContentAdPlacement} class represents a single placement of a Content ad. It
 * returns a {@link View} that contains a previously loaded ad or will be filled with a new ad once
 * it completes loading.
 */
public class ContentAdPlacement implements AdPlacement {

    private ContentAdFetcher mFetcher;

    /**
     * Creates a new instance of {@link ContentAdPlacement} with the given fetcher
     *
     * @param fetcher the fetcher that will retrieve and cache native ads for this placement
     */
    public ContentAdPlacement(ContentAdFetcher fetcher) {
        mFetcher = fetcher;
    }

    /**
     * Creates or reuses a {@link FrameLayout} for this ad placement, filling it asynchronously
     * with the assets for its {@link com.google.android.gms.ads.formats.NativeContentAd}.
     *
     * @param convertView a {@link View} to reuse if possible
     * @param parent      The {@link ViewGroup} into which the {@link View} will be placed
     * @return a {@link FrameLayout} that contains or will contain the ad assets for this placement
     */
    @Override
    public View getView(View convertView, ViewGroup parent) {
        // For each native ad, a FrameLayout is created and returned to the ListView as
        // the item's root view. This allows the fetcher to asynchronously request an ad, which
        // will be converted into a set of Views and placed into the FrameLayout by the
        // ViewHolder object.
        FrameLayout frameLayout = (FrameLayout) convertView;

        ContentAdViewHolder holder
                = (frameLayout == null) ? null : (ContentAdViewHolder) frameLayout.getTag();

        // If the holder is null, either this is the first time the item has been displayed, or
        // convertView held a different type of item before. In either case, a new FrameLayout
        // and ViewHolder should be created and used.
        if (holder == null) {
            frameLayout = new FrameLayout(parent.getContext());

            LayoutInflater inflater = (LayoutInflater) parent.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            NativeContentAdView adView = (NativeContentAdView) inflater
                    .inflate(R.layout.item_content_ad, frameLayout, false);
            frameLayout.addView(adView);

            holder = new ContentAdViewHolder(adView);
            frameLayout.setTag(holder);
        }

        // This statement kicks off the ad loading process.  The ViewHolder has some interface
        // methods that will be invoked when the ad is ready to be displayed or fails to load.
        mFetcher.loadAd(frameLayout.getContext(), holder);

        // The FrameLayout is returned to the ListView so it will have something to show for the
        // item right now. The FrameLayout will be filled with native ad assets later, once the
        // ad has finished loading.
        return frameLayout;
    }
}
