package com.google.example.gms.nativeads;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.ads.formats.NativeAppInstallAdView;
import com.google.android.gms.ads.formats.NativeContentAdView;

/**
 * The {@link MultiFormatAdPlacement} class represents a single placement of a Multi-format ad. It
 * returns a {@link View} that contains a previously loaded ad or will be filled with a new ad once
 * it completes loading.
 */
public class MultiFormatAdPlacement
        implements AdPlacement {

    private MultiFormatAdFetcher mFetcher;
    private FrameLayout mFrame;

    /**
     * Creates a new instance of {@link MultiFormatAdPlacement} with the given fetcher
     *
     * @param fetcher the fetcher that will retrieve and cache native ads for this placement
     */
    public MultiFormatAdPlacement(MultiFormatAdFetcher fetcher) {
        mFetcher = fetcher;
    }

    /**
     * Creates or reuses a {@link FrameLayout} for this ad placement, filling it asynchronously
     * with the assets for its ad.
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
        mFrame = (FrameLayout) convertView;

        if (convertView == null) {
            mFrame = new FrameLayout(parent.getContext());
        }

        // This statement kicks off the ad loading process.  The ViewHolder has some interface
        // methods that will be invoked when the ad is ready to be displayed or fails to load.
        mFetcher.loadAd(mFrame.getContext(), this);

        // The FrameLayout is returned to the ListView so it will have something to show for the
        // item right now. The FrameLayout will be filled with native ad assets later, once the
        // ad has finished loading.
        return mFrame;
    }

    public AppInstallAdViewHolder getAppInstallAdViewHolder() {
        // There is no instance in which this will be called before getView(), so there is
        // no reason getTag() should ever return a null value.
        AdViewHolder holder = (AdViewHolder) mFrame.getTag();

        if (holder instanceof AppInstallAdViewHolder) {
            return (AppInstallAdViewHolder) holder;
        } else {
            mFrame.removeAllViews();

            LayoutInflater inflater = (LayoutInflater) mFrame.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            NativeAppInstallAdView adView = (NativeAppInstallAdView) inflater
                    .inflate(R.layout.item_app_install_ad, mFrame, false);
            mFrame.addView(adView);

            AppInstallAdViewHolder appInstallAdViewHolder = new AppInstallAdViewHolder(adView);
            mFrame.setTag(appInstallAdViewHolder);

            return appInstallAdViewHolder;
        }
    }

    public ContentAdViewHolder getContentAdViewHolder() {
        // There is no instance in which this will be called before getView(), so there is
        // no reason getTag() should ever return a null value.
        AdViewHolder holder = (AdViewHolder) mFrame.getTag();

        if (holder instanceof ContentAdViewHolder) {
            return (ContentAdViewHolder) holder;
        } else {
            mFrame.removeAllViews();

            LayoutInflater inflater = (LayoutInflater) mFrame.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            NativeContentAdView adView = (NativeContentAdView) inflater
                    .inflate(R.layout.item_content_ad, mFrame, false);
            mFrame.addView(adView);

            ContentAdViewHolder contentAdViewHolder = new ContentAdViewHolder(adView);
            mFrame.setTag(contentAdViewHolder);

            return contentAdViewHolder;
        }
    }

    public SimpleCustomTemplateAdViewHolder getSimpleCustomTemplateAdViewHolder() {
        // There is no instance in which this will be called before getView(), so there is
        // no reason getTag() should ever return a null value.
        AdViewHolder holder = (AdViewHolder) mFrame.getTag();

        if (holder instanceof SimpleCustomTemplateAdViewHolder) {
            return (SimpleCustomTemplateAdViewHolder) holder;
        } else {
            mFrame.removeAllViews();

            LayoutInflater inflater = (LayoutInflater) mFrame.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View adView = inflater.inflate(R.layout.item_simple_custom_template_ad, mFrame, false);
            mFrame.addView(adView);

            SimpleCustomTemplateAdViewHolder simpleCustomTemplateAdViewHolder =
                    new SimpleCustomTemplateAdViewHolder(adView);
            mFrame.setTag(simpleCustomTemplateAdViewHolder);

            return simpleCustomTemplateAdViewHolder;
        }
    }

    public ExtendedCustomTemplateAdViewHolder getExtendedCustomTemplateAdViewHolder() {
        // There is no instance in which this will be called before getView(), so there is
        // no reason getTag() should ever return a null value.
        AdViewHolder holder = (AdViewHolder) mFrame.getTag();

        if (holder instanceof ExtendedCustomTemplateAdViewHolder) {
            return (ExtendedCustomTemplateAdViewHolder) holder;
        } else {
            mFrame.removeAllViews();

            LayoutInflater inflater = (LayoutInflater) mFrame.getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View adView = inflater.inflate(R.layout.item_extended_custom_template_ad, mFrame,
                    false);
            mFrame.addView(adView);

            ExtendedCustomTemplateAdViewHolder extendedCustomTemplateAdViewHolder =
                    new ExtendedCustomTemplateAdViewHolder(adView);
            mFrame.setTag(extendedCustomTemplateAdViewHolder);

            return extendedCustomTemplateAdViewHolder;
        }
    }

    public AdViewHolder getCurrentViewHolder() {
        return (AdViewHolder) mFrame.getTag();
    }
}
