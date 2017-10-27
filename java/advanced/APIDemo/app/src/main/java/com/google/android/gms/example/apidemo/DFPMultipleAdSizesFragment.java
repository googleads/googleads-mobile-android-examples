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
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@link DFPMultipleAdSizesFragment} class demonstrates how to set specific ad sizes for a
 * request.
 */
public class DFPMultipleAdSizesFragment extends Fragment {

    private Button mLoadButton;
    private PublisherAdView mPublisherAdView;
    private CheckBox m320x50CheckBox;
    private CheckBox m300x250CheckBox;
    private CheckBox m120x20CheckBox;

    public DFPMultipleAdSizesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dfp_multiple_ad_sizes, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mLoadButton = (Button) getView().findViewById(R.id.adsizes_btn_loadad);
        m120x20CheckBox = (CheckBox) getView().findViewById(R.id.adsizes_cb_120x20);
        m320x50CheckBox = (CheckBox) getView().findViewById(R.id.adsizes_cb_320x50);
        m300x250CheckBox = (CheckBox) getView().findViewById(R.id.adsizes_cb_300x250);
        mPublisherAdView = (PublisherAdView) getView().findViewById(R.id.adsizes_pav_main);

        mPublisherAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                mPublisherAdView.setVisibility(View.VISIBLE);
            }
        });

        mLoadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!m120x20CheckBox.isChecked()
                        && !m320x50CheckBox.isChecked()
                        && !m300x250CheckBox.isChecked()) {
                    Toast.makeText(DFPMultipleAdSizesFragment.this.getActivity(),
                            "At least one size is required.", Toast.LENGTH_SHORT).show();
                } else {
                    List<AdSize> sizeList = new ArrayList<AdSize>();

                    if (m120x20CheckBox.isChecked()) {
                        sizeList.add(new AdSize(120, 20));
                    }

                    if (m320x50CheckBox.isChecked()) {
                        sizeList.add(AdSize.BANNER);
                    }

                    if (m300x250CheckBox.isChecked()) {
                        sizeList.add(AdSize.MEDIUM_RECTANGLE);
                    }

                    mPublisherAdView.setVisibility(View.INVISIBLE);
                    mPublisherAdView.setAdSizes(sizeList.toArray(new AdSize[sizeList.size()]));
                    mPublisherAdView.loadAd(new PublisherAdRequest.Builder().build());
                }
            }
        });
    }
}
