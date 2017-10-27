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

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;

/**
 * the {@link DFPCustomTargetingFragment} class demonstrates how to add custom targeting
 * information to a request.
 */
public class DFPCustomTargetingFragment extends Fragment {

    private Spinner mSportsSpinner;
    private Button mLoadButton;
    private PublisherAdView mAdView;

    public DFPCustomTargetingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dfp_custom_targeting, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mSportsSpinner = (Spinner) getView().findViewById(R.id.customtargeting_spn_sport);
        mLoadButton = (Button) getView().findViewById(R.id.customtargeting_btn_loadad);
        mAdView = (PublisherAdView) getView().findViewById(R.id.customtargeting_av_main);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getView().getContext(),
                R.array.customtargeting_sports, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSportsSpinner.setAdapter(adapter);

        mLoadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PublisherAdRequest adRequest = new PublisherAdRequest.Builder()
                        .addCustomTargeting(getString(R.string.customtargeting_key),
                                (String) mSportsSpinner.getSelectedItem())
                        .build();

                mAdView.loadAd(adRequest);
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
}
