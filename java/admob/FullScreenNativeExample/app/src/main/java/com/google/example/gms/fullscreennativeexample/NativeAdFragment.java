package com.google.example.gms.fullscreennativeexample;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.gms.ads.VideoController;
import com.google.android.gms.ads.VideoController.VideoLifecycleCallbacks;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;
import com.google.example.gms.fullscreennativeexample.databinding.FragmentNativeAdBinding;

/** A fragment class that represents the native ad. */
public class NativeAdFragment extends Fragment {

  private FragmentNativeAdBinding binding;

  public NativeAdFragment() {
    super(R.layout.fragment_native_ad);
  }

  @Nullable
  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    binding = FragmentNativeAdBinding.inflate(getLayoutInflater());
    return binding.getRoot();
  }

  @Override
  public void onDestroyView() {
    binding = null;
    MainActivity mainActivity = ((MainActivity) getActivity());
    if (mainActivity != null && mainActivity.getSupportActionBar() != null) {
      mainActivity.getSupportActionBar().show();
    }
    super.onDestroyView();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    MainActivity mainActivity = ((MainActivity) getActivity());
    if (mainActivity != null) {
      populateNativeAdView(mainActivity.getNativeAd(), binding.innerLayout.nativeAdView);

      if (mainActivity.getSupportActionBar() != null) {
        // Display the native ad in full-screen.
        mainActivity.getSupportActionBar().hide();
      }
    }
  }

  private void populateNativeAdView(NativeAd nativeAd, NativeAdView adView) {
    // Set the media view.
    adView.setMediaView(binding.innerLayout.adMedia);

    // Set other ad assets.
    adView.setHeadlineView(binding.innerLayout.adHeadline);
    adView.setBodyView(binding.innerLayout.adBody);
    adView.setCallToActionView(binding.innerLayout.adCallToAction);
    ImageView imageView = binding.innerLayout.adAppIcon;
    imageView.setClipToOutline(true);
    adView.setIconView(imageView);

    // The headline and mediaContent are guaranteed to be in every NativeAd.
    binding.innerLayout.adHeadline.setText(nativeAd.getHeadline());
    binding.innerLayout.adMedia.setMediaContent(nativeAd.getMediaContent());

    // These assets aren't guaranteed to be in every NativeAd, so it's important to
    // check before trying to display them.
    if (nativeAd.getBody() == null) {
      binding.innerLayout.adBody.setVisibility(View.INVISIBLE);
      binding.innerLayout.adBody.setText("");
    } else {
      binding.innerLayout.adBody.setVisibility(View.VISIBLE);
      binding.innerLayout.adBody.setText(nativeAd.getBody());
    }

    if (nativeAd.getCallToAction() == null) {
      binding.innerLayout.adCallToAction.setVisibility(View.INVISIBLE);
      binding.innerLayout.adBody.setText("");
    } else {
      binding.innerLayout.adCallToAction.setVisibility(View.VISIBLE);
      binding.innerLayout.adCallToAction.setText(nativeAd.getCallToAction());
    }

    if (nativeAd.getIcon() == null) {
      binding.innerLayout.adAppIcon.setVisibility(View.GONE);
    } else {
      binding.innerLayout.adAppIcon.setVisibility(View.VISIBLE);
      binding.innerLayout.adAppIcon.setImageDrawable(nativeAd.getIcon().getDrawable());
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
