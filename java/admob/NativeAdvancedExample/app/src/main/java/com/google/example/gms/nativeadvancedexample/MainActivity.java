/*
 * Copyright (C) 2015 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.example.gms.nativeadvancedexample;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoOptions;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdOptions;
import com.google.android.gms.ads.nativead.NativeAdView;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A simple activity class that displays native ad formats.
 */
@SuppressLint("SetTextI18n")
public class MainActivity extends AppCompatActivity {

    private static final String ADMOB_AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110";
    private static final String TAG = "MainActivity";

    private final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);
    private GoogleMobileAdsConsentManager googleMobileAdsConsentManager;
    private Button refresh;
    private CheckBox startVideoAdsMuted;
    private TextView videoStatus;
    private NativeAd nativeAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        refresh = findViewById(R.id.btn_refresh);
        startVideoAdsMuted = findViewById(R.id.cb_start_muted);
        videoStatus = findViewById(R.id.tv_video_status);

        // Log the Mobile Ads SDK version.
        Log.d(TAG, "Google Mobile Ads SDK Version: " + MobileAds.getVersion());

        googleMobileAdsConsentManager =
            GoogleMobileAdsConsentManager.getInstance(getApplicationContext());
        googleMobileAdsConsentManager.gatherConsent(
            this,
            consentError -> {
                if (consentError != null) {
                    // Consent not obtained in current session.
                    Log.w(
                        TAG,
                        String.format(
                            "%s: %s",
                            consentError.getErrorCode(),
                            consentError.getMessage()));
                }

                if (googleMobileAdsConsentManager.canRequestAds()) {
                    refresh.setVisibility(View.VISIBLE);
                    initializeMobileAdsSdk();
                }

                if (googleMobileAdsConsentManager.isPrivacyOptionsRequired()) {
                    // Regenerate the options menu to include a privacy setting.
                    invalidateOptionsMenu();
                }
            });

        // This sample attempts to load ads using consent obtained in the previous session.
        if (googleMobileAdsConsentManager.canRequestAds()) {
            initializeMobileAdsSdk();
        }
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View unusedView) {
                if (googleMobileAdsConsentManager.canRequestAds()) {
                    refreshAd();
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        MenuItem moreMenu = menu.findItem(R.id.action_more);
        moreMenu.setVisible(googleMobileAdsConsentManager.isPrivacyOptionsRequired());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        View menuItemView = findViewById(item.getItemId());
        PopupMenu popup = new PopupMenu(this, menuItemView);
        popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
        popup.show();
        popup.setOnMenuItemClickListener(
            popupMenuItem -> {
                if (popupMenuItem.getItemId() == R.id.privacy_settings) {
                    // Handle changes to user consent.
                    googleMobileAdsConsentManager.showPrivacyOptionsForm(
                    this,
                        formError -> {
                            if (formError != null) {
                                Toast.makeText(
                                    this,
                                    formError.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            }
                    });
                    return true;
                }
                return false;
            });
        return super.onOptionsItemSelected(item);
    }

  /**
   * Populates a {@link NativeAdView} object with data from a given {@link NativeAd}.
   *
   * @param nativeAd the object containing the ad's assets
   * @param adView the view to be populated
   */
  private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
        // Set the media view.
        adView.setMediaView((MediaView) adView.findViewById(R.id.ad_media));

        // Set other ad assets.
        adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
        adView.setBodyView(adView.findViewById(R.id.ad_body));
        adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
        adView.setIconView(adView.findViewById(R.id.ad_app_icon));
        adView.setPriceView(adView.findViewById(R.id.ad_price));
        adView.setStarRatingView(adView.findViewById(R.id.ad_stars));
        adView.setStoreView(adView.findViewById(R.id.ad_store));
        adView.setAdvertiserView(adView.findViewById(R.id.ad_advertiser));

    // The headline and mediaContent are guaranteed to be in every NativeAd.
    ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
        adView.getMediaView().setMediaContent(nativeAd.getMediaContent());

    // These assets aren't guaranteed to be in every NativeAd, so it's important to
    // check before trying to display them.
    if (nativeAd.getBody() == null) {
            adView.getBodyView().setVisibility(View.INVISIBLE);
        } else {
            adView.getBodyView().setVisibility(View.VISIBLE);
            ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
        }

        if (nativeAd.getCallToAction() == null) {
            adView.getCallToActionView().setVisibility(View.INVISIBLE);
        } else {
            adView.getCallToActionView().setVisibility(View.VISIBLE);
            ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
        }

        if (nativeAd.getIcon() == null) {
            adView.getIconView().setVisibility(View.GONE);
        } else {
            ((ImageView) adView.getIconView()).setImageDrawable(
                nativeAd.getIcon().getDrawable());
            adView.getIconView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getPrice() == null) {
            adView.getPriceView().setVisibility(View.INVISIBLE);
        } else {
            adView.getPriceView().setVisibility(View.VISIBLE);
            ((TextView) adView.getPriceView()).setText(nativeAd.getPrice());
        }

        if (nativeAd.getStore() == null) {
            adView.getStoreView().setVisibility(View.INVISIBLE);
        } else {
            adView.getStoreView().setVisibility(View.VISIBLE);
            ((TextView) adView.getStoreView()).setText(nativeAd.getStore());
        }

        if (nativeAd.getStarRating() == null) {
            adView.getStarRatingView().setVisibility(View.INVISIBLE);
        } else {
            ((RatingBar) adView.getStarRatingView())
                .setRating(nativeAd.getStarRating().floatValue());
            adView.getStarRatingView().setVisibility(View.VISIBLE);
        }

        if (nativeAd.getAdvertiser() == null) {
            adView.getAdvertiserView().setVisibility(View.INVISIBLE);
        } else {
            ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
            adView.getAdvertiserView().setVisibility(View.VISIBLE);
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        adView.setNativeAd(nativeAd);

    // Get the video controller for the ad. One will always be provided, even if the ad doesn't
    // have a video asset.
    VideoController vc = nativeAd.getMediaContent().getVideoController();

    // Updates the UI to say whether or not this ad has a video asset.
    if (nativeAd.getMediaContent() != null && nativeAd.getMediaContent().hasVideoContent()) {

            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            vc.setVideoLifecycleCallbacks(new VideoController.VideoLifecycleCallbacks() {
                @Override
                public void onVideoEnd() {
                    // Publishers should allow native ads to complete video playback before
                    // refreshing or replacing them with another ad in the same UI location.
                    refresh.setEnabled(true);
                    videoStatus.setText("Video status: Video playback has ended.");
                    super.onVideoEnd();
                }
            });
        } else {
            videoStatus.setText("Video status: Ad does not contain a video asset.");
            refresh.setEnabled(true);
        }
    }

    /**
     * Creates a request for a new native ad based on the boolean parameters and calls the
     * corresponding "populate" method when one is successfully returned.
     *
     */
    private void refreshAd() {
        refresh.setEnabled(false);

        AdLoader.Builder builder = new AdLoader.Builder(this, ADMOB_AD_UNIT_ID);

    builder.forNativeAd(
        new NativeAd.OnNativeAdLoadedListener() {
          // OnLoadedListener implementation.
          @Override
          public void onNativeAdLoaded(NativeAd nativeAd) {
            // If this callback occurs after the activity is destroyed, you must call
            // destroy and return or you may get a memory leak.
            boolean isDestroyed = false;
            refresh.setEnabled(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
              isDestroyed = isDestroyed();
            }
            if (isDestroyed || isFinishing() || isChangingConfigurations()) {
              nativeAd.destroy();
              return;
            }
            // You must call destroy on old ads when you are done with them,
            // otherwise you will have a memory leak.
            if (MainActivity.this.nativeAd != null) {
              MainActivity.this.nativeAd.destroy();
            }
            MainActivity.this.nativeAd = nativeAd;
            FrameLayout frameLayout = findViewById(R.id.fl_adplaceholder);
            NativeAdView adView =
                (NativeAdView) getLayoutInflater().inflate(R.layout.ad_unified, frameLayout, false);
            populateNativeAdView(nativeAd, adView);
            frameLayout.removeAllViews();
            frameLayout.addView(adView);
          }
        });

        VideoOptions videoOptions =
            new VideoOptions.Builder().setStartMuted(startVideoAdsMuted.isChecked()).build();

        NativeAdOptions adOptions =
            new NativeAdOptions.Builder().setVideoOptions(videoOptions).build();

        builder.withNativeAdOptions(adOptions);

    AdLoader adLoader =
        builder
            .withAdListener(
                new AdListener() {
                  @Override
                  public void onAdFailedToLoad(LoadAdError loadAdError) {
                    refresh.setEnabled(true);
                    String error =
                        String.format(
                            Locale.getDefault(),
                            "domain: %s, code: %d, message: %s",
                            loadAdError.getDomain(),
                            loadAdError.getCode(),
                            loadAdError.getMessage());
                    Toast.makeText(
                            MainActivity.this,
                            "Failed to load native ad with error " + error,
                            Toast.LENGTH_SHORT)
                        .show();
                  }
                })
            .build();

        adLoader.loadAd(new AdRequest.Builder().build());

        videoStatus.setText("");
    }

    private void initializeMobileAdsSdk() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) {
            return;
        }

        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(
        this,
            new OnInitializationCompleteListener() {
                @Override
                public void onInitializationComplete(InitializationStatus initializationStatus) {
                    // Load an ad.
                    refreshAd();
                }
            });
    }

    @Override
    protected void onDestroy() {
        if (nativeAd != null) {
            nativeAd.destroy();
        }
        super.onDestroy();
    }
}
