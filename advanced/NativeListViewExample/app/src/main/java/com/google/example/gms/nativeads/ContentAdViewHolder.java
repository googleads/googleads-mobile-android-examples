package com.google.example.gms.nativeads;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.formats.NativeContentAd;
import com.google.android.gms.ads.formats.NativeContentAdView;

import java.util.List;

/**
 * A class that creates, holds, and populates the view assets for a {@link NativeContentAd}.
 */
public class ContentAdViewHolder extends AdViewHolder {
    public NativeContentAdView mAdView;

    /**
     * Stores the View for a {@link NativeContentAd} and locates specific {@link View}s used
     * to display its assets.
     *
     * @param adView the {@link View} used to display assets for a native content ad.
     */
    public ContentAdViewHolder(NativeContentAdView adView) {
        mAdView = adView;

        mAdView.setHeadlineView(mAdView.findViewById(R.id.contentad_headline));
        mAdView.setImageView(mAdView.findViewById(R.id.contentad_image));
        mAdView.setBodyView(mAdView.findViewById(R.id.contentad_body));
        mAdView.setCallToActionView(mAdView.findViewById(R.id.contentad_call_to_action));
        mAdView.setLogoView(mAdView.findViewById(R.id.contentad_logo));
        mAdView.setAdvertiserView(mAdView.findViewById(R.id.contentad_advertiser));
    }

    /**
     * Populates the asset {@link View}s contained it the {@link NativeContentAdView} with data
     * from the {@link NativeContentAd} object. This method is invoked when an
     * {@link ContentAdFetcher} has successfully loaded a {@link NativeContentAd}.
     *
     * @param contentAd the ad that is to be displayed
     */
    public void populateView(NativeContentAd contentAd) {
        ((TextView) mAdView.getHeadlineView()).setText(contentAd.getHeadline());
        ((TextView) mAdView.getBodyView()).setText(contentAd.getBody());
        ((TextView) mAdView.getCallToActionView()).setText(contentAd.getCallToAction());
        ((TextView) mAdView.getAdvertiserView()).setText(contentAd.getAdvertiser());

        List<NativeAd.Image> images = contentAd.getImages();

        if (images != null && images.size() > 0) {
            ((ImageView) mAdView.getImageView())
                    .setImageDrawable(images.get(0).getDrawable());
        }

        NativeAd.Image logoImage = contentAd.getLogo();

        if (logoImage != null) {
            ((ImageView) mAdView.getLogoView())
                    .setImageDrawable(logoImage.getDrawable());
        }

        // assign native ad object to the native view and make visible
        mAdView.setNativeAd(contentAd);
        mAdView.setVisibility(View.VISIBLE);
    }

    /**
     * Hides the {@link NativeContentAdView} used to display the native ad.
     */
    @Override
    public void hideView() {
        mAdView.setVisibility(View.GONE);
    }
}
