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

import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;

/**
 * The {@link DFPCategoryExclusionFragment} class demonstrates how to use category exclusions with
 * DFP requests
 */
public class DFPCategoryExclusionFragment extends Fragment {

    private PublisherAdView mNoExclusionsAdView;
    private PublisherAdView mDogsExcludedAdView;
    private PublisherAdView mCatsExcludedAdView;

    public DFPCategoryExclusionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dfp_category_exclusion, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mNoExclusionsAdView =
                (PublisherAdView) getView().findViewById(R.id.no_exclusions_av);
        mDogsExcludedAdView =
                (PublisherAdView) getView().findViewById(R.id.exclusions_av_dogsexcluded);
        mCatsExcludedAdView =
                (PublisherAdView) getView().findViewById(R.id.exclusions_av_catsexcluded);

        PublisherAdRequest noExclusionsRequest = new PublisherAdRequest.Builder().build();
        PublisherAdRequest dogsExcludedRequest = new PublisherAdRequest.Builder()
                .addCategoryExclusion(getString(R.string.categoryexclusion_dogscategoryname))
                .build();
        PublisherAdRequest catsExcludedRequest = new PublisherAdRequest.Builder()
                .addCategoryExclusion(getString(R.string.categoryexclusion_catscategoryname))
                .build();

        mNoExclusionsAdView.loadAd(noExclusionsRequest);
        mDogsExcludedAdView.loadAd(dogsExcludedRequest);
        mCatsExcludedAdView.loadAd(catsExcludedRequest);
    }
}
