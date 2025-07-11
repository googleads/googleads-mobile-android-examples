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

package com.google.android.gms.example.apidemo.fullscreennative

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.ads.nativead.NativeAd

/** A [ViewModel] that holds the [NativeAd]. */
class NativeAdViewModel : ViewModel() {

  private val _nativeAd = MutableLiveData<NativeAd>()

  val nativeAd: LiveData<NativeAd>
    get() = _nativeAd

  fun setNativeAd(ad: NativeAd) {
    _nativeAd.value = ad
  }
}
