package com.google.example.gms.nativeads;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Holds the views for a listing.
 */
public class ListingViewHolder {
    public ImageView mImage;
    public TextView mPrice;
    public TextView mHeadline;
    public TextView mSubtitle;
    public TextView mBody;

    /**
     * Populates a {@link Listing} list item.
     *
     * @param listing The listing data.
     */
    public void populateView(Listing listing) {
        mImage.setImageResource(listing.getImageResource());
        mPrice.setText(listing.getPrice());
        mHeadline.setText(listing.getHeadline());
        mSubtitle.setText(listing.getSubtitle());
        mBody.setText(listing.getBody());
    }

    /**
     * Creates and returns a {@code ListingViewHolder} to hold the listing views.
     */
    public static ListingViewHolder create(View listingLayout) {
        ListingViewHolder viewHolder = new ListingViewHolder();
        viewHolder.mImage = (ImageView) listingLayout.findViewById(R.id.image);
        viewHolder.mPrice = (TextView) listingLayout.findViewById(R.id.price);
        viewHolder.mHeadline = (TextView) listingLayout.findViewById(R.id.headline);
        viewHolder.mSubtitle = (TextView) listingLayout.findViewById(R.id.subtitle);
        viewHolder.mBody = (TextView) listingLayout.findViewById(R.id.body);
        return viewHolder;
    }
}
