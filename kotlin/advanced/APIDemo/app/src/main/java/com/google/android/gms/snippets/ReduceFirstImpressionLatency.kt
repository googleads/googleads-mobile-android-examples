/*
 * Copyright 2025 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gms.snippets

import android.content.Context
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** Kotlin code snippets for reducing first impression latency. */
private class ReduceFirstImpressionLatency {

  // [START reduce_first_impression_latency]

  // Set up a CONFLATED channel that only buffers the latest event.
  private val adLoadChannel = Channel<Unit>(Channel.CONFLATED)

  fun initialize(context: Context) {
    // Coroutine to handle the 5-second timeout.
    CoroutineScope(Dispatchers.Default).launch {
      delay(5000L)

      // 5 second timer elapsed
      adLoadChannel.trySend(Unit)
    }

    // Coroutine to call MobileAds.initialize() on a background thread.
    CoroutineScope(Dispatchers.IO).launch {
      MobileAds.initialize(context) {
        // Mobile Ads SDK initialization finished.
        adLoadChannel.trySend(Unit)
      }
    }

    // Coroutine to listen for init complete or timer, whichever comes first.
    CoroutineScope(Dispatchers.Main).launch {
      // Wait on the first event.
      adLoadChannel.receive()
      loadAd()
      // Close the channel after receiving the first event.
      adLoadChannel.close()
    }
  }

  private fun loadAd() {
    // TODO: Load an ad.
  }

  // [END reduce_first_impression_latency]
}
