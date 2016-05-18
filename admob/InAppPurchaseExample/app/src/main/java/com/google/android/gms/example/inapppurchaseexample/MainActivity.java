/*
 * Copyright (C) 2013 Google, Inc.
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

package com.google.android.gms.example.inapppurchaseexample;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.purchase.InAppPurchaseResult;
import com.google.android.gms.ads.purchase.PlayStorePurchaseListener;

import java.util.HashSet;
import java.util.Set;

/**
 * A class that demonstrates how to use AdMob in-app purchase ads.
 */
public class MainActivity extends AppCompatActivity implements PlayStorePurchaseListener {

    /**
     * The public key for your Android application, which can be found in the Android console.
     *
     * This parameter is optional, but it is recommended to include in a real application that uses
     * billing. Since your app defined on Android is for a different application package than this
     * sample, this parameter can be left as is for the purposes of running the sample.
     */
    private static final String PUBLIC_KEY = "<YOUR_PUBLIC_KEY>";

    /**
     * Your ad unit ID for in-app purchase ads.
     */
    private static final String AD_UNIT_ID = "/6499/example/iap";

    /**
     * Your AdMob Application ID.
     */
    private static final String APP_ID = "ca-app-pub-3940256099942544~3347511713";

    /**
     * The key for the billing response code in the Intent extras.
     */
    public static final String BILLING_RESPONSE_CODE_KEY = "RESPONSE_CODE";

    /**
     * The response code for a successful purchase.
     */
    public static final int BILLING_RESPONSE_RESULT_OK = 0;

    /**
     * The set of SKUs that this app can handle.
     */
    private Set<String> mValidSkus;

    /**
     * The interstitial used to load in-app purchase ads.
     */
    private InterstitialAd mInterstitial;

    /**
     * The amount of goods owned.
     */
    private int mGoods;

    /**
     * The view indicating the amount of goods owned.
     */
    private TextView mGoodsTextView;

    /**
     * The load interstitial button.
     */
    private Button mLoadInterstitialButton;

    /**
     * The show interstitial button.
     */
    private Button mShowInterstitialButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Initialize the Mobile Ads SDK.
        MobileAds.initialize(this, APP_ID);

        mGoods = 0;
        mGoodsTextView = (TextView) findViewById(R.id.tv_goods);
        updateGoods();

        // SKUs that your app supports.
        mValidSkus = new HashSet<String>();
        mValidSkus.add("android.test.purchased");

        mInterstitial = new InterstitialAd(this);
        mInterstitial.setAdUnitId(AD_UNIT_ID);
        mInterstitial.setPlayStorePurchaseParams(this, PUBLIC_KEY);
        mInterstitial.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                Toast.makeText(MainActivity.this, "Ad loaded", Toast.LENGTH_SHORT).show();
                mShowInterstitialButton.setEnabled(true);
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                Toast.makeText(MainActivity.this, "Ad failed to load", Toast.LENGTH_SHORT).show();
                mLoadInterstitialButton.setEnabled(true);
            }

            @Override
            public void onAdOpened() {
                mShowInterstitialButton.setEnabled(false);
            }

            @Override
            public void onAdClosed() {
                mLoadInterstitialButton.setEnabled(true);
            }
        });

        mLoadInterstitialButton = (Button) findViewById(R.id.btn_load_ad);
        mLoadInterstitialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View unusedView) {
                mLoadInterstitialButton.setEnabled(false);
                AdRequest request = new AdRequest.Builder().build();
                mInterstitial.loadAd(request);
            }
        });

        mShowInterstitialButton = (Button) findViewById(R.id.btn_show_ad);
        mShowInterstitialButton.setEnabled(false);
        mShowInterstitialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View unusedView) {
                if (mInterstitial.isLoaded()) {
                    mInterstitial.show();
                }
            }
        });
    }

    /**
     * Updates the number of goods owned.
     */
    private void updateGoods() {
        mGoodsTextView.setText(String.format("Goods: %d", mGoods));
    }

    @Override
    public boolean isValidPurchase(String sku) {
        boolean validPurchase = mValidSkus.contains(sku);
        Toast.makeText(this,
                String.format("isValidPurchase for product %s: %b", sku, validPurchase),
                Toast.LENGTH_SHORT).show();
        return validPurchase;
    }

    @Override
    public void onInAppPurchaseFinished(InAppPurchaseResult result) {
        String sku = result.getProductId();
        if (result.getResultCode() == Activity.RESULT_OK) {
            Toast.makeText(this,
                    String.format("Successful purchase for product %s", sku),
                    Toast.LENGTH_SHORT).show();
            // Reward user with goods.
            mGoods += 5;
            updateGoods();
            // Remember to call finishPurchase() to consume the purchase.
            result.finishPurchase();
        } else {
            Toast.makeText(this,
                    String.format("Failed to purchase product %s", sku),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
