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

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.doubleclick.AppEventListener;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;

/**
 * The {@link DFPAppEventsFragment} class demonstrates how to receive App Events from a DFP
 * creative.
 */
public class DFPAppEventsFragment extends Fragment {

    private PublisherAdView mAdView;
    private View mRootView;

    public DFPAppEventsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_dfp_app_events, container, false);
        return mRootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAdView = (PublisherAdView) getView().findViewById(R.id.appevents_av_main);

        mAdView.setAppEventListener(new AppEventListener() {
            @Override
            public void onAppEvent(String name, String data) {

                // The DFP ad that this fragment loads contains JavaScript code that sends App
                // Events to the host application. This AppEventListener receives those events,
                // and sets the background of the fragment to match the data that comes in.
                // The ad will send "red" when it loads, "blue" five seconds later, and "green"
                // if the user taps the ad.

                // This is just a demonstration, of course. Your apps can do much more interesting
                // things with App Events.

                if (name.equals("color")) {
                    switch (data) {
                        case "blue":
                            mRootView.setBackgroundColor(Color.rgb(0xD0, 0xD0, 0xFF));
                            break;
                        case "red":
                            mRootView.setBackgroundColor(Color.rgb(0xFF, 0xD0, 0xD0));
                            break;
                        case "green":
                            mRootView.setBackgroundColor(Color.rgb(0xD0, 0xFF, 0xD0));
                            break;
                    }
                }
            }
        });

        PublisherAdRequest adRequest = new PublisherAdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }
}
