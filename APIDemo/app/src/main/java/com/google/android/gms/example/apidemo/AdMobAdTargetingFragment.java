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
import android.app.DatePickerDialog;
import android.location.Location;
import android.location.LocationManager;
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
import android.widget.TextView;

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

    private AdView mAdView;
    private Button mDatePickButton;
    private Button mLoadAdButton;
    private TextView mLocationText;
    private EditText mBirthdayEdit;
    private RadioButton mMaleRadio;
    private RadioButton mFemaleRadio;
    private RadioButton mChildRadio;
    private RadioButton mNotChildRadio;
    private RadioButton mUnspecifiedRadio;
    private Location mLastLocation;
    private SimpleDateFormat mDateFormat = new SimpleDateFormat("M/d/yyyy");

    public AdMobAdTargetingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_admob_ad_targeting, container, false);

        mAdView = (AdView) rootView.findViewById(R.id.targeting_av_main);
        mLoadAdButton = (Button) rootView.findViewById(R.id.targeting_btn_loadad);
        mDatePickButton = (Button) rootView.findViewById(R.id.targeting_btn_datepick);
        mLocationText = (TextView) rootView.findViewById(R.id.targeting_tv_locationvalue);
        mBirthdayEdit = (EditText) rootView.findViewById(R.id.targeting_et_birthday);
        mMaleRadio = (RadioButton) rootView.findViewById(R.id.targeting_rb_male);
        mFemaleRadio = (RadioButton) rootView.findViewById(R.id.targeting_rb_female);
        mChildRadio = (RadioButton) rootView.findViewById(R.id.targeting_rb_child);
        mNotChildRadio = (RadioButton) rootView.findViewById(R.id.targeting_rb_notchild);
        mUnspecifiedRadio = (RadioButton) rootView.findViewById(R.id.targeting_rb_unspecified);

        if (mLastLocation != null) {
            mLocationText.setText(String.format("%.3f, %.3f",
                    mLastLocation.getLatitude(), mLastLocation.getLongitude()));
        }

        mDatePickButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment newFragment = new DatePickerFragment();
                newFragment.setOnDateSetListener(AdMobAdTargetingFragment.this);
                newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
            }
        });

        mLoadAdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdRequest.Builder builder = new AdRequest.Builder();

                if (mLastLocation != null) {
                    builder.setLocation(mLastLocation);
                }

                try {
                    String birthdayString = mBirthdayEdit.getText().toString();
                    Date birthday = mDateFormat.parse(birthdayString);
                    builder.setBirthday(birthday);
                } catch (ParseException ex) {
                    Log.d(MainActivity.LOG_TAG, "Failed to parse birthday");
                }

                if (mMaleRadio.isChecked()) {
                    builder.setGender(AdRequest.GENDER_MALE);
                } else if (mFemaleRadio.isChecked()) {
                    builder.setGender(AdRequest.GENDER_FEMALE);
                }

                if (mUnspecifiedRadio.isChecked()) {
                    // There's actually nothing to be done here. If you're unsure whether or not
                    // the user should receive child-directed treatment, simply avoid calling the
                    // tagForChildDirectedTreatment method. The ad request will not contain any
                    // indication one way or the other.
                } else if (mChildRadio.isChecked()) {
                    builder.tagForChildDirectedTreatment(true);
                } else if (mNotChildRadio.isChecked()) {
                    builder.tagForChildDirectedTreatment(false);
                }

                AdRequest request = builder.build();
                mAdView.loadAd(request);
            }
        });

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LocationManager mgr =
                (LocationManager) getActivity().getSystemService(Activity.LOCATION_SERVICE);
        mLastLocation = mgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, monthOfYear, dayOfMonth);
        mBirthdayEdit.setText(mDateFormat.format(calendar.getTime()));
    }
}
