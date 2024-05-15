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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerAdView;

/**
 * The {@link AdManagerCustomTargetingFragment} class demonstrates how to add custom targeting
 * information to a request.
 */
public class AdManagerCustomTargetingFragment extends Fragment {

  private Spinner sportsSpinner;
  private AdManagerAdView adView;

  public AdManagerCustomTargetingFragment() {
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_gam_custom_targeting, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    sportsSpinner = view.findViewById(R.id.customtargeting_spn_sport);
    Button loadButton = view.findViewById(R.id.customtargeting_btn_loadad);
    adView = view.findViewById(R.id.customtargeting_av_main);

    ArrayAdapter<CharSequence> adapter =
        ArrayAdapter.createFromResource(
            view.getContext(),
            R.array.customtargeting_sports,
            android.R.layout.simple_spinner_item);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    sportsSpinner.setAdapter(adapter);

    loadButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder()
            .addCustomTargeting(getString(R.string.customtargeting_key),
                (String) sportsSpinner.getSelectedItem())
            .build();

        adView.loadAd(adRequest);
      }
    });
  }
}
