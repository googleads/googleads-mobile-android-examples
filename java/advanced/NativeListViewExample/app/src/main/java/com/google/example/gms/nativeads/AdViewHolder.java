package com.google.example.gms.nativeads;

/**
 * A base class for the other ad view holders. It defines a single method, {@code hideView} that's
 * used to hide the {@link android.view.View} (typically a
 * {@link com.google.android.gms.ads.formats.NativeAdView}) in each native ad view holder type.
 */
public abstract class AdViewHolder {
    /**
     * Hides the {@link android.view.View} used to display native ad assets.
     */
    public abstract void hideView();
}
