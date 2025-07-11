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

import android.app.Activity
import com.google.android.gms.appset.AppSet
import com.google.android.gms.appset.AppSetIdInfo
import com.google.android.ump.ConsentRequestParameters

private class UMPSnippets {

  private fun syncConsentIdentifier(activity: Activity) {
    // [START sync_consent_identifier]
    // Example fetching App Set ID to identify the user across apps.
    val client = AppSet.getClient(activity)
    client.appSetIdInfo.addOnSuccessListener { info: AppSetIdInfo ->
      val appSetId = info.id
      val params =
        ConsentRequestParameters.Builder()
          //  .setConsentSyncId(appSetId)
          .build()
    }
    // [END sync_consent_identifier]
  }
}
