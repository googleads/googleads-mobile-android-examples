package com.google.example.gms.fullscreennativeexample;

import android.app.Application;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoController.VideoLifecycleCallbacks;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.example.gms.fullscreennativeexample.databinding.ActivityNativeAdBinding;

/** An activity class that displays a native ad. */
public class NativeAdActivity extends AppCompatActivity {
  public NativeAd nativeAd;
  private NativeAdView nativeAdView;
  private ActivityNativeAdBinding binding;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityNativeAdBinding.inflate(getLayoutInflater());
    ViewGroup view = binding.getRoot();
    setContentView(view);

    // Populate the view on a Runnable.
    getWindow().getDecorView().post(() -> {
      Application application = getApplication();
      NativeAd nativeAd = ((MyApplication) application).getNativeAd();
      populateNativeAdView(nativeAd, binding.innerLayout.nativeAdView);
    });
  }

  private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
    MediaView mediaView = adView.findViewById(R.id.ad_media);
    // Set the media view.
    adView.setMediaView(mediaView);

    // Set other ad assets.
    adView.setHeadlineView(adView.findViewById(R.id.ad_headline));
    adView.setBodyView(adView.findViewById(R.id.ad_body));
    adView.setCallToActionView(adView.findViewById(R.id.ad_call_to_action));
    ImageView imageView = adView.findViewById(R.id.ad_app_icon);
    imageView.setClipToOutline(true);
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
