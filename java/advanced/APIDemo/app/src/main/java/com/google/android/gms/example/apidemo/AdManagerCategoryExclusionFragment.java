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
package com.google.android.gms.example.apidemo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerAdView;

/**
 * The {@link AdManagerCategoryExclusionFragment} class demonstrates how to use category exclusions
 * with Ad Manager requests.
 */
public class AdManagerCategoryExclusionFragment extends Fragment {

  private AdManagerAdView noExclusionsAdView;
  private AdManagerAdView dogsExcludedAdView;
  private AdManagerAdView catsExcludedAdView;

  public AdManagerCategoryExclusionFragment() {
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_gam_category_exclusion, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    noExclusionsAdView = view.findViewById(R.id.no_exclusions_av);
    dogsExcludedAdView = view.findViewById(R.id.exclusions_av_dogsexcluded);
    catsExcludedAdView = view.findViewById(R.id.exclusions_av_catsexcluded);

    AdManagerAdRequest noExclusionsRequest = new AdManagerAdRequest.Builder().build();
    AdManagerAdRequest dogsExcludedRequest = new AdManagerAdRequest.Builder()
        .addCategoryExclusion(getString(R.string.categoryexclusion_dogscategoryname))
        .build();
    AdManagerAdRequest catsExcludedRequest = new AdManagerAdRequest.Builder()
        .addCategoryExclusion(getString(R.string.categoryexclusion_catscategoryname))
        .build();

    noExclusionsAdView.loadAd(noExclusionsRequest);
    dogsExcludedAdView.loadAd(dogsExcludedRequest);
    catsExcludedAdView.loadAd(catsExcludedRequest);
  }
}
