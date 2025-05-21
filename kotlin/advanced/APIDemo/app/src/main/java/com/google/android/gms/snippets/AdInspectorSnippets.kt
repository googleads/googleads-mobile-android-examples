// Copyright 2025 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.android.gms.snippets

import android.content.Context
import com.google.android.gms.ads.MobileAds

/** Kotlin code snippets for the developer guide. */
private class AdInspectorSnippets {

  private fun openAdInspector(context: Context) {
    // [START open_ad_inspector]
    MobileAds.openAdInspector(context) { error ->
      // Error will be non-null if ad inspector closed due to an error.
    }
    // [END open_ad_inspector]
  }
}
