/*
 * Copyright 2024 Google LLC
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

package com.google.android.gms.example.apidemo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.example.apidemo.databinding.FragmentCollapsibleBannerBinding;

/** The [CollapsibleBannerFragment] class demonstrates how to use a collapsible banner ad. */
public class CollapsibleBannerFragment extends Fragment implements OnGlobalLayoutListener {

  // This is an ad unit ID for a test ad. Replace with your own banner ad unit ID.
  private static final String SAMPLE_AD_UNIT_ID = "ca-app-pub-3940256099942544/9214589741";
  private FragmentCollapsibleBannerBinding fragmentBinding;
  private AdView adView;

  @Override
  public View onCreateView(
      @NonNull LayoutInflater inflater,
      @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    fragmentBinding = FragmentCollapsibleBannerBinding.inflate(inflater);

    adView = new AdView(requireContext());
    fragmentBinding.adViewContainer.addView(adView);

    return fragmentBinding.getRoot();
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    view.getViewTreeObserver().addOnGlobalLayoutListener(this);
  }

  private void loadCollapsibleBanner() {
    // Create an extra parameter that aligns the bottom of the expanded ad to
    // the bottom of the bannerView.
    Bundle extras = new Bundle();
    extras.putString("collapsible", "bottom");

    // Create an ad request.
    AdRequest adRequest =
        new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, extras).build();

    // Listen to ad events.
    adView.setAdListener(
        new AdListener() {
          @Override
          public void onAdLoaded() {
            Log.i(
                MainActivity.LOG_TAG,
                String.format("Ad loaded. adView.isCollapsible() is %b.", adView.isCollapsible()));
          }
        });

    // Start loading a collapsible banner ad.
    adView.loadAd(adRequest);
  }

  @Override
  public void onGlobalLayout() {
    float density = requireContext().getResources().getDisplayMetrics().density;
    int adWidth = (int) (fragmentBinding.adViewContainer.getWidth() / density);

    // Step 1: Create an AdSize.
    AdSize adSize =
        AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(requireContext(), adWidth);

    // Step 2: Set ad unit ID and ad size to the banner ad view.
    adView.setAdUnitId(SAMPLE_AD_UNIT_ID);
    adView.setAdSize(adSize);

    // Step 3: Load a banner ad.
    loadCollapsibleBanner();

    requireView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
  }
}
