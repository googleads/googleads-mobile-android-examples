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

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.mediation.MediationAdRequest;
import com.google.android.gms.ads.mediation.customevent.CustomEventBanner;
import com.google.android.gms.ads.mediation.customevent.CustomEventBannerListener;

/**
 * The {@link SampleCustomEvent} class is a simple custom event example that returns a TextView
 * with a fake advertisement.
 */
public class SampleCustomEvent implements CustomEventBanner {

    private TextView mSampleTextView;
    private CustomEventBannerListener mListener;
    private Context mContext;

    @Override
    public void requestBannerAd(Context context,
                                final CustomEventBannerListener customEventBannerListener,
                                String parameter,
                                AdSize adSize,
                                MediationAdRequest mediationAdRequest,
                                Bundle bundle) {
        mContext = context;
        mListener = customEventBannerListener;

        // In this example we're using a simple TextView to stand in for a mediated AdView or
        // some other form of advertisement. In your own custom events you can use third-party
        // ad views or build layouts on the fly.

        mSampleTextView = new TextView(context);
        mSampleTextView.setBackgroundColor(Color.WHITE);
        mSampleTextView.setGravity(Gravity.CENTER);
        mSampleTextView.setHeight(adSize.getHeightInPixels(context));
        mSampleTextView.setWidth(adSize.getWidthInPixels(context));

        // When setting up a custom event in an ad unit, you have the option to specify a string
        // parameter that will be passed into the custom event requestBannerAd method. For this
        // example, it's just being displayed, but you can use it to pass ad units for other
        // networks, or whatever extra information your custom event might require.

        String bannerText =
                String.format(context.getResources().getString(R.string.customevent_bannertext),
                        parameter);
        mSampleTextView.setText(bannerText);

        mSampleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View unusedView) {
                mListener.onAdClicked();
                mListener.onAdOpened();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com"));
                mContext.startActivity(intent);
                mListener.onAdLeftApplication();
            }
        });

        mListener.onAdLoaded(mSampleTextView);
    }

    @Override
    public void onDestroy() {
        // Because this example uses a simple TextView to stand in for an ad, there is no work to be
        // done here.
    }

    @Override
    public void onPause() {
        // Because this example uses a simple TextView to stand in for an ad, there is no work to be
        // done here.
    }

    @Override
    public void onResume() {
        // Because this example uses a simple TextView to stand in for an ad, there is no work to be
        // done here.
    }
}
