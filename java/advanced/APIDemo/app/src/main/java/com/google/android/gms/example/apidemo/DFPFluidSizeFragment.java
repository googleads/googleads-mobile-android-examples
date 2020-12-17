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
import androidx.fragment.app.Fragment;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;

import java.util.Locale;

/**
 * The {@link DFPFluidSizeFragment} demonstrates the use of the {@code AdSize.FLUID} ad size.
 */
public class DFPFluidSizeFragment extends Fragment {

    private PublisherAdView publisherAdView;
    private Button changeAdViewWidthButton;
    private TextView currentWidthTextView;
    private final int[] adViewWidths = new int[]{200, 250, 320};
    private int currentIndex = 0;

    public DFPFluidSizeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dfp_fluid_size, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // The size for this PublisherAdView is defined in the XML layout as AdSize.FLUID. It could
        // also be set here by calling publisherAdView.setAdSizes(AdSize.FLUID).
        //
        // An ad with fluid size will automatically stretch or shrink to fit the height of its
        // content, which can help layout designers cut down on excess whitespace.
        publisherAdView = getView().findViewById(R.id.fluid_av_main);

        PublisherAdRequest publisherAdRequest = new PublisherAdRequest.Builder().build();
        publisherAdView.loadAd(publisherAdRequest);

        changeAdViewWidthButton = getView().findViewById(R.id.fluid_btn_change_width);
        changeAdViewWidthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int newWidth = adViewWidths[currentIndex % adViewWidths.length];
                currentIndex += 1;
                // Change the PublisherAdView's width.
                ViewGroup.LayoutParams layoutParams = publisherAdView.getLayoutParams();
                final float scale = getResources().getConfiguration().densityDpi / (float) DisplayMetrics.DENSITY_DEFAULT;
                layoutParams.width = (int) (newWidth * scale + 0.5f);
                publisherAdView.setLayoutParams(layoutParams);
                // Update the TextView with the new width.
                currentWidthTextView = getView().findViewById(R.id.fluid_tv_current_width);
                currentWidthTextView.setText(
                        String.format(Locale.getDefault(), "%d dp", newWidth));
            }
        });
    }
}
