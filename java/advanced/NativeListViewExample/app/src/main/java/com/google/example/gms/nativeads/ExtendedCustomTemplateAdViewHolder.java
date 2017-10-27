package com.google.example.gms.nativeads;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.formats.NativeCustomTemplateAd;

/**
 * A class that creates, holds, and populates the view assets for a {@link NativeCustomTemplateAd}
 * with a particular DFP template.
 */
public class ExtendedCustomTemplateAdViewHolder extends AdViewHolder {

    public static final String DFP_TEMPLATE_ID = "10063530";
    public static final String HEADLINE_TEMPLATE_FIELD_NAME = "Headline";
    public static final String CAPTION_TEMPLATE_FIELD_NAME = "Caption";
    public static final String MAINIMAGE_TEMPLATE_FIELD_NAME = "MainImage";
    public static final String BODY_TEMPLATE_FIELD_NAME = "Body";
    public static final String CALLTOACTION_TEMPLATE_FIELD_NAME = "CallToAction";

    private View mAdView;
    private ImageView mMainImage;
    private TextView mHeadline;
    private TextView mCaption;
    private TextView mBody;
    private Button mCallToAction;

    /**
     * Stores the View for a {@link NativeCustomTemplateAd} and locates specific {@link View}s used
     * to display assets for the "extended" custom template.
     *
     * @param adView the {@link View} used to display assets for a native ad.
     */
    public ExtendedCustomTemplateAdViewHolder(View adView) {
        mAdView = adView;

        mMainImage = (ImageView) mAdView.findViewById(R.id.extendedcustom_image);
        mHeadline = (TextView) mAdView.findViewById(R.id.extendedcustom_headline);
        mCaption = (TextView) mAdView.findViewById(R.id.extendedcustom_caption);
        mBody = (TextView) mAdView.findViewById(R.id.extendedcustom_body);
        mCallToAction = (Button) mAdView.findViewById(R.id.extendedcustom_calltoaction);
    }

    /**
     * Populates the asset {@link View}s with data from the {@link NativeCustomTemplateAd} object.
     * This method is invoked when an {@link SimpleCustomTemplateAdFetcher} has successfully loaded
     * a {@link NativeCustomTemplateAd}.
     *
     * @param customTemplateAd the ad that is to be displayed
     */
    public void populateView(final NativeCustomTemplateAd customTemplateAd) {
        mHeadline.setText(customTemplateAd.getText(HEADLINE_TEMPLATE_FIELD_NAME));
        mCaption.setText(customTemplateAd.getText(CAPTION_TEMPLATE_FIELD_NAME));
        mBody.setText(customTemplateAd.getText(BODY_TEMPLATE_FIELD_NAME));
        mCallToAction.setText(customTemplateAd.getText(CALLTOACTION_TEMPLATE_FIELD_NAME));
        mMainImage.setImageDrawable(
                customTemplateAd.getImage(MAINIMAGE_TEMPLATE_FIELD_NAME).getDrawable());

        mCallToAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customTemplateAd.performClick(CALLTOACTION_TEMPLATE_FIELD_NAME);
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
