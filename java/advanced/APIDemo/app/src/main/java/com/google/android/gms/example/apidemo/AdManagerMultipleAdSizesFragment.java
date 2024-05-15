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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerAdView;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@link AdManagerMultipleAdSizesFragment} class demonstrates how to set specific ad sizes for
 * a request.
 */
public class AdManagerMultipleAdSizesFragment extends Fragment {

  private Button loadButton;
  private AdManagerAdView adView;
  private CheckBox cb320x50;
  private CheckBox cb300x250;
  private CheckBox cb120x20;

  public AdManagerMultipleAdSizesFragment() {
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    // Inflate the layout for this fragment.
    return inflater.inflate(R.layout.fragment_gam_multiple_ad_sizes, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    loadButton = view.findViewById(R.id.adsizes_btn_loadad);
    cb120x20 = view.findViewById(R.id.adsizes_cb_120x20);
    cb320x50 = view.findViewById(R.id.adsizes_cb_320x50);
    cb300x250 = view.findViewById(R.id.adsizes_cb_300x250);
    adView = view.findViewById(R.id.adsizes_pav_main);

    adView.setAdListener(new AdListener() {
      @Override
      public void onAdLoaded() {
        adView.setVisibility(View.VISIBLE);
      }
    });

    loadButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        if (!cb120x20.isChecked() && !cb320x50.isChecked() && !cb300x250.isChecked()) {
          Toast.makeText(AdManagerMultipleAdSizesFragment.this.getActivity(),
              "At least one size is required.", Toast.LENGTH_SHORT).show();
        } else {
          List<AdSize> sizeList = new ArrayList<>();

          if (cb120x20.isChecked()) {
            sizeList.add(new AdSize(120, 20));
          }

          if (cb320x50.isChecked()) {
            sizeList.add(AdSize.BANNER);
          }

          if (cb300x250.isChecked()) {
            sizeList.add(AdSize.MEDIUM_RECTANGLE);
          }

          adView.setVisibility(View.INVISIBLE);
          adView.setAdSizes(sizeList.toArray(new AdSize[sizeList.size()]));
          adView.loadAd(new AdManagerAdRequest.Builder().build());
        }
      }
    });
  }
}
