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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * The {@link AdMobAdTargetingFragment} class demonstrates how to use ad targeting with AdMob.
 */
public class AdMobAdTargetingFragment extends Fragment
        implements DatePickerDialog.OnDateSetListener {

    private AdView adView;
    private Button datePickButton;
    private Button loadAdButton;
    private EditText birthdayEdit;
    private RadioButton maleRadio;
    private RadioButton femaleRadio;
    private RadioButton childRadio;
    private RadioButton notChildRadio;
    private RadioButton unspecifiedRadio;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/yyyy");

    public AdMobAdTargetingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_admob_ad_targeting, container, false);

        adView = rootView.findViewById(R.id.targeting_av_main);
        loadAdButton = rootView.findViewById(R.id.targeting_btn_loadad);
        datePickButton = rootView.findViewById(R.id.targeting_btn_datepick);
        birthdayEdit = rootView.findViewById(R.id.targeting_et_birthday);
        maleRadio = rootView.findViewById(R.id.targeting_rb_male);
        femaleRadio = rootView.findViewById(R.id.targeting_rb_female);
        childRadio = rootView.findViewById(R.id.targeting_rb_child);
        notChildRadio = rootView.findViewById(R.id.targeting_rb_notchild);
        unspecifiedRadio = rootView.findViewById(R.id.targeting_rb_unspecified);

        datePickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment newFragment = new DatePickerFragment();
                newFragment.setOnDateSetListener(AdMobAdTargetingFragment.this);
                newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
            }
        });

        loadAdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdRequest.Builder builder = new AdRequest.Builder();

                try {
                    String birthdayString = birthdayEdit.getText().toString();
                    Date birthday = dateFormat.parse(birthdayString);
                    builder.setBirthday(birthday);
                } catch (ParseException ex) {
                    Log.d(MainActivity.LOG_TAG, "Failed to parse birthday");
                }

                if (maleRadio.isChecked()) {
                    builder.setGender(AdRequest.GENDER_MALE);
                } else if (femaleRadio.isChecked()) {
                    builder.setGender(AdRequest.GENDER_FEMALE);
                }

                if (unspecifiedRadio.isChecked()) {
                    // There's actually nothing to be done here. If you're unsure whether or not
                    // the user should receive child-directed treatment, simply avoid calling the
                    // tagForChildDirectedTreatment method. The ad request will not contain any
                    // indication one way or the other.
                } else if (childRadio.isChecked()) {
                    builder.tagForChildDirectedTreatment(true);
                } else if (notChildRadio.isChecked()) {
                    builder.tagForChildDirectedTreatment(false);
                }

                AdRequest request = builder.build();
                adView.loadAd(request);
            }
        });

        return rootView;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);
        birthdayEdit.setText(dateFormat.format(calendar.getTime()));
    }
}
