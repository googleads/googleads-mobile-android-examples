//
//  Copyright (C) 2023 Google LLC
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//

package com.google.example.gms.fullscreennativeexample;

import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoController.VideoLifecycleCallbacks;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.example.gms.fullscreennativeexample.FeedAdapter.AdFeedItem;

/** A ViewHolder representing a native ad view. */
public class AdsViewHolder extends FeedViewHolder {

  public AdsViewHolder(View itemView) {
    super(itemView);
  }

  public void bind(AdFeedItem adFeedItem, int position) {
    populateNativeAdView(adFeedItem.getNativeAd(), itemView.findViewById(R.id.native_ad_view));
  }

  @Override
  public void attach() {}

  @Override
  public void detach() {}

  public static void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
    MediaView mediaView = adView.findViewById(R.id.ad_media);
    // Set the media view.
    adView.setMediaView(mediaView);

    // Set other ad assets.
    adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
    adView.setBodyView(adView.findViewById(R.id.ad_body));
    adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
    ImageView imageView = adView.findViewById(R.id.ad_app_icon);
    if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
      imageView.setClipToOutline(true);
    }
    adView.setIconView(imageView);

    // The headline and mediaContent are guaranteed to be in every NativeAd.
    ((TextView) adView.getHeadlineView()).setText(nativeAd.getHeadline());
    mediaView.setMediaContent(nativeAd.getMediaContent());

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
      ((ImageView) adView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
      adView.getIconView().setVisibility(View.VISIBLE);
    }

    // This method tells the Google Mobile Ads SDK that you have finished populating your
    // native ad view with this native ad.
    adView.setNativeAd(nativeAd);

    // Get the video controller for the ad. One will always be provided,
    // even if the ad doesn't have a video asset.
    VideoController videoController = nativeAd.getMediaContent().getVideoController();

    // Updates the UI to say whether or not this ad has a video asset.
    if (videoController.hasVideoContent()) {

      // Create a new VideoLifecycleCallbacks object and pass it to the VideoController.
      // The VideoController will call methods on this object when events occur in the
      // video lifecycle.
      videoController.setVideoLifecycleCallbacks(
          new VideoLifecycleCallbacks() {
            @Override
            public void onVideoEnd() {
              // Publishers should allow native ads to complete video playback before
              // refreshing or replacing them with another ad in the same UI location.
              super.onVideoEnd();
            }
          });
    }
  }
}
