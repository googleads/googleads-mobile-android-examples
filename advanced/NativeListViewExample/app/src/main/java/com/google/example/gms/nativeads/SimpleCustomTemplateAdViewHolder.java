package com.google.example.gms.nativeads;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.formats.NativeCustomTemplateAd;

/**
 * A class that creates, holds, and populates the view assets for a {@link NativeCustomTemplateAd}
 * with a specific DFP template.
 */
public class SimpleCustomTemplateAdViewHolder extends AdViewHolder {

    public static final String DFP_TEMPLATE_ID = "10063170";
    public static final String HEADLINE_TEMPLATE_FIELD_NAME = "Headline";
    public static final String CAPTION_TEMPLATE_FIELD_NAME = "Caption";
    public static final String MAINIMAGE_TEMPLATE_FIELD_NAME = "MainImage";

    private View mAdView;
    private ImageView mMainImage;
    private TextView mHeadline;
    private TextView mCaption;

    /**
     * Stores the View for a {@link NativeCustomTemplateAd} and locates specific {@link View}s used
     * to display assets for the "simple" custom template.
     *
     * @param adView the {@link View} used to display assets for a native ad.
     */
    public SimpleCustomTemplateAdViewHolder(View adView) {
        mAdView = adView;

        mMainImage = (ImageView) mAdView.findViewById(R.id.simplecustom_image);
        mHeadline = (TextView) mAdView.findViewById(R.id.simplecustom_headline);
        mCaption = (TextView) mAdView.findViewById(R.id.simplecustom_caption);
    }

    /**
     * Populates the asset {@link View}s with data
     * from the {@link NativeCustomTemplateAd} object. This method is invoked when an
     * {@link SimpleCustomTemplateAdFetcher} has successfully loaded a
     * {@link NativeCustomTemplateAd}.
     *
     * @param customTemplateAd the ad that is to be displayed
     */
    public void populateView(final NativeCustomTemplateAd customTemplateAd) {
        mHeadline.setText(customTemplateAd.getText(HEADLINE_TEMPLATE_FIELD_NAME));
        mCaption.setText(customTemplateAd.getText(CAPTION_TEMPLATE_FIELD_NAME));

        mMainImage.setImageDrawable(
                customTemplateAd.getImage(MAINIMAGE_TEMPLATE_FIELD_NAME).getDrawable());
        mMainImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customTemplateAd.performClick(MAINIMAGE_TEMPLATE_FIELD_NAME);
            }
        });

        mAdView.setVisibility(View.VISIBLE);
    }

    /**
     * Processes the custom click events from the {@link NativeCustomTemplateAd}. In this case, a
     * {@link Toast} is displayed, but other applications might change UI elements or enable/disable
     * inputs in response to these click events.
     *
     * @param customTemplateAd the ad object that's invoking the method
     * @param assetName        the name of the asset that was clicked by the user
     */
    public void processClick(NativeCustomTemplateAd customTemplateAd,
                             String assetName) {
        Context context = mAdView.getContext();
        String messageFormatString = context.getResources().getString(R.string.custom_click_toast);
        String message = String.format(messageFormatString, assetName,
                customTemplateAd.getCustomTemplateId());
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Hides the {@link View} used to display the native ad.
     */
    @Override
    public void hideView() {
        mAdView.setVisibility(View.GONE);
    }
}
