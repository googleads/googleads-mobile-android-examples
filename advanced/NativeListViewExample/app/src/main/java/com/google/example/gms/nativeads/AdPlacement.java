package com.google.example.gms.nativeads;

import android.view.View;
import android.view.ViewGroup;

/**
 * The {@link AdPlacement} interface is used by the {@link AdsListingAdapter} to retrieve
 * {@link View}s from the AdPlacement classes ({@link AppInstallAdPlacement},
 * {@link ContentAdPlacement}, etc.).
 */
public interface AdPlacement {

    /**
     * Returns a {@link View} suitable for display in a list adapter.
     *
     * @param convertView a {@link View} to reuse if possible
     * @param parent      The {@link ViewGroup} into which the {@link View} will be placed
     * @return a new or recycled {@link View} that will (eventually) contain the ad placement
     */
    View getView(View convertView, ViewGroup parent);
}
