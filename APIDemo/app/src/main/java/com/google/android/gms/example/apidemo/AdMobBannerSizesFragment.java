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

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

/**
 * The {@link AdMobBannerSizesFragment} class demonstrates how to set a desired banner size
 * prior to loading an ad.
 */
public class AdMobBannerSizesFragment extends Fragment {

    private AdView mAdView;
    private Button mLoadButton;
    private FrameLayout mAdFrameLayout;
    private Spinner mSizesSpinner;

    public AdMobBannerSizesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_admob_banner_sizes, container, false);

        mSizesSpinner = (Spinner) rootView.findViewById(R.id.bannersizes_spn_size);
        mLoadButton = (Button) rootView.findViewById(R.id.bannersizes_btn_loadad);
        mAdFrameLayout = (FrameLayout) rootView.findViewById(R.id.bannersizes_fl_adframe);

        String[] sizesArray;

        // It is a Mobile Ads SDK policy that only the banner, large banner, and smart banner ad
        // sizes are shown on phones, and that the full banner, leaderboard, and medium rectangle
        // sizes are reserved for use on tablets.  The conditional below checks the screen size
        // and retrieves the correct list.

        int screenSize = getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK;

        if ((screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE)
                || (screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE)) {
            sizesArray = getResources().getStringArray(R.array.bannersizes_largesizes);
        } else {
            sizesArray = getResources().getStringArray(R.array.bannersizes_smallsizes);
        }

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(rootView.getContext(),
                android.R.layout.simple_spinner_dropdown_item, sizesArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSizesSpinner.setAdapter(adapter);

        mLoadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdView != null) {
                    mAdFrameLayout.removeView(mAdView);
                    mAdView.destroy();
                }

                mAdView = new AdView(getActivity());
                mAdView.setAdUnitId(getString(R.string.admob_banner_ad_unit_id));
                mAdFrameLayout.addView(mAdView);

                switch (mSizesSpinner.getSelectedItemPosition()) {
                    case 0:
                        mAdView.setAdSize(AdSize.BANNER);
                        break;
                    case 1:
                        mAdView.setAdSize(AdSize.LARGE_BANNER);
                        break;
                    case 2:
                        mAdView.setAdSize(AdSize.SMART_BANNER);
                        break;
                    case 3:
                        mAdView.setAdSize(AdSize.FULL_BANNER);
                        break;
                    case 4:
                        mAdView.setAdSize(AdSize.MEDIUM_RECTANGLE);
                        break;
                    case 5:
                        mAdView.setAdSize(AdSize.LEADERBOARD);
                        break;
                }

                mAdView.loadAd(new AdRequest.Builder().build());
            }
        });

        return rootView;
    }
}
