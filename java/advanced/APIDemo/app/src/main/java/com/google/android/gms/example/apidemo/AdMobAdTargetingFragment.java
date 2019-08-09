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

import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.google.android.gms.ads.RequestConfiguration.MAX_AD_CONTENT_RATING_G;
import static com.google.android.gms.ads.RequestConfiguration.MAX_AD_CONTENT_RATING_MA;
import static com.google.android.gms.ads.RequestConfiguration.MAX_AD_CONTENT_RATING_PG;
import static com.google.android.gms.ads.RequestConfiguration.MAX_AD_CONTENT_RATING_T;

/**
 * The {@link AdMobAdTargetingFragment} class demonstrates how to use ad targeting with AdMob.
 */
public class AdMobAdTargetingFragment extends Fragment {

    private AdView adView;
    private Button loadAdButton;
    private RadioGroup childRadio;
    private RadioGroup uacRadio;

    public AdMobAdTargetingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_admob_ad_targeting, container, false);

        adView = rootView.findViewById(R.id.targeting_ad_view);
        loadAdButton = rootView.findViewById(R.id.targeting_btn_loadad);
        childRadio = rootView.findViewById(R.id.targeting_rg_child);
        childRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int setting = RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_UNSPECIFIED;
                switch (checkedId) {
                    case R.id.targeting_rb_child:
                        setting = RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE;
                        break;
                    case R.id.targeting_rb_notchild:
                        setting = RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_FALSE;
                        break;
                    case R.id.targeting_rb_unspecified:
                    default:
                        break;
                }
                RequestConfiguration requestConfiguration = MobileAds.getRequestConfiguration().toBuilder()
                            .setTagForChildDirectedTreatment(setting)
                            .build();
                MobileAds.setRequestConfiguration(requestConfiguration);
            }
        });
        uacRadio = rootView.findViewById(R.id.targeting_rg_uac);
        uacRadio.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int setting = RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_UNSPECIFIED;
                switch (checkedId) {
                    case R.id.targeting_rb_uac:
                        setting = RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_TRUE;
                        break;
                    case R.id.targeting_rb_notuac:
                        setting = RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_FALSE;
                        break;
                    case R.id.targeting_rb_unspecified:
                    default:
                        break;
                }
                RequestConfiguration requestConfiguration = MobileAds.getRequestConfiguration().toBuilder()
                    .setTagForUnderAgeOfConsent(setting)
                    .build();
                MobileAds.setRequestConfiguration(requestConfiguration);
            }
        });

        final
        String[] ratingsArray = getResources().getStringArray(R.array.targeting_ratings);

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(
            getContext(),
            android.R.layout.simple_spinner_dropdown_item, ratingsArray
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = rootView.findViewById(R.id.targeting_content_rating_spinner);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String rating;
                switch (ratingsArray[position]) {
                    case "G":
                    default:
                        rating = MAX_AD_CONTENT_RATING_G;
                        break;
                    case "PG":
                        rating = MAX_AD_CONTENT_RATING_PG;
                        break;
                    case "T":
                        rating = MAX_AD_CONTENT_RATING_T;
                        break;
                    case "MA":
                        rating = MAX_AD_CONTENT_RATING_MA;
                        break;
                }
                RequestConfiguration requestConfiguration = MobileAds.getRequestConfiguration().toBuilder()
                    .setMaxAdContentRating(rating).build();
                MobileAds.setRequestConfiguration(requestConfiguration);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        loadAdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdRequest request = new AdRequest.Builder().build();
                adView.loadAd(request);
            }
        });

        return rootView;
    }
}
