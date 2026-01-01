/*
 * Copyright 2024 Google LLC
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

package com.google.android.gms.example.jetpackcomposedemo

import android.app.Application

class GoogleMobileAdsApplication : Application() {
  companion object {
    const val TAG = "GoogleMobileAdsSample"

    const val BANNER_AD_UNIT_ID = "ca-app-pub-3940256099942544/9214589741"
    const val ADMANANGER_ADAPTIVE_BANNER_AD_UNIT_ID = "/21775744923/example/adaptive-banner"
    const val NATIVE_AD_UNIT_ID = "ca-app-pub-3940256099942544/1044960115"
  }
}
