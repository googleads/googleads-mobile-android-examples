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
package com.google.android.gms.example.bannerexample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.doubleclick.PublisherAdRequest
import java.util.Arrays
import kotlinx.android.synthetic.main.activity_my.*

/**
 * Main Activity. Inflates main activity xml and child fragments.
 */
class MyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my)

        // Set your test devices. Check your logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
        // to get test ads on this device."
        MobileAds.setRequestConfiguration(
          RequestConfiguration.Builder()
            .setTestDeviceIds(Arrays.asList("ABCDEF012345"))
            .build()
        )

        // Create an ad request.
        val adRequest = PublisherAdRequest.Builder().build()

        // Start loading the ad in the background.
        ad_view.loadAd(adRequest)
    }

    /** Called when leaving the activity  */
    public override fun onPause() {
        ad_view.pause()
        super.onPause()
    }

    /** Called when returning to the activity  */
    public override fun onResume() {
        super.onResume()
        ad_view.resume()
    }

    /** Called before the activity is destroyed  */
    public override fun onDestroy() {
        ad_view.destroy()
        super.onDestroy()
    }
}
