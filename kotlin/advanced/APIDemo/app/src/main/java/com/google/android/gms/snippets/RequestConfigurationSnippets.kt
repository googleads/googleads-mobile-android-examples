/*
 * Copyright 2025 Google LLC
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

package com.google.android.gms.snippets

import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration

/** Kotlin code snippets for the developer guide. */
private class RequestConfigurationSnippets {

  private fun setTestDeviceIds() {
    // [START set_test_device_ids]
    val testDeviceIds = listOf(TEST_DEVICE_ID)
    val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
    MobileAds.setRequestConfiguration(configuration)
    // [END set_test_device_ids]
  }

  companion object {
    const val TEST_DEVICE_ID = "33BE2250B43518CCDA7DE426D04EE231"
  }
}
