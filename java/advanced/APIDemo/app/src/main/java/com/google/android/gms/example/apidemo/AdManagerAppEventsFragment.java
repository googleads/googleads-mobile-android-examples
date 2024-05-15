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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerAdView;
import com.google.android.gms.ads.admanager.AppEventListener;

/**
 * The {@link AdManagerAppEventsFragment} class demonstrates how to receive app events from an Ad
 * Manager creative.
 */
public class AdManagerAppEventsFragment extends Fragment {

  private AdManagerAdView adView;
  private View rootView;

  public AdManagerAppEventsFragment() {
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
      Bundle savedInstanceState) {
    rootView = inflater.inflate(R.layout.fragment_gam_app_events, container, false);
    return rootView;
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    adView = view.findViewById(R.id.appevents_av_main);

    adView.setAppEventListener(
        new AppEventListener() {
          @Override
          public void onAppEvent(@NonNull String name, @NonNull String data) {

            // The Ad Manager ad this fragment loads contains JavaScript code that sends App
            // Events to this host application. This AppEventListener receives those events,
            // and sets the background of the fragment to match the value sent in the app event.
            // The ad will send "red" when it loads, "blue" five seconds later, and "green"
            // if the user taps the ad.

            // This is just a demonstration, of course. Your apps can do much more interesting
            // things with app events.

            if (name.equals("color")) {
              switch (data) {
                case "blue":
                  rootView.setBackgroundColor(Color.rgb(0xD0, 0xD0, 0xFF));
                  break;
                case "red":
                  rootView.setBackgroundColor(Color.rgb(0xFF, 0xD0, 0xD0));
                  break;
                case "green":
                  rootView.setBackgroundColor(Color.rgb(0xD0, 0xFF, 0xD0));
                  break;
              }
            }
          }
        });

    AdManagerAdRequest adRequest = new AdManagerAdRequest.Builder().build();
    adView.loadAd(adRequest);
  }
}
