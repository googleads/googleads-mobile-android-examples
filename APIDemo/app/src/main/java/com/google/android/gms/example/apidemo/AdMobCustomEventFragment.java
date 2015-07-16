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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

/**
 * The {@link AdMobCustomEventFragment} class demonstrates how to use an AdMob custom event. The
 * ad unit used below returns a mediation stack that references {@link SampleCustomEvent}, so an
 * instance of that custom event class will be loaded.
 */
public class AdMobCustomEventFragment extends Fragment {

    private AdView mAdView;

    public AdMobCustomEventFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admob_custom_event, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Note that when running on an emulator, AdMob will only serve test ads. That's fine for
        // the other demonstrations, but it will prevent the custom event from being used in this
        // one. In order to properly experience this demo, you'll need to run it on a hardware
        // device.

        mAdView = (AdView) getView().findViewById(R.id.customevent_av_main);
        mAdView.loadAd(new AdRequest.Builder().build());
    }
}
