/*
 * Copyright 2025 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.gms.snippets;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import com.google.android.gms.appset.AppSet;
import com.google.android.gms.appset.AppSetIdClient;
import com.google.android.ump.ConsentRequestParameters;

final class UMPSnippets {

  private void setTagForUnderAgeOfConsent() {
    // [START set_tag_for_under_age_of_consent]
    ConsentRequestParameters params =
        new ConsentRequestParameters.Builder()
            // Indicate the user is under age of consent.
            .setTagForUnderAgeOfConsent(true)
            .build();
    // [END set_tag_for_under_age_of_consent]
  }

  private void readConsentChoices(Context context) {
    // [START read_consent_choices]
    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    // Example value: "1111111111"
    String purposeConsents = sharedPref.getString("IABTCF_PurposeConsents", "");
    // Purposes are zero-indexed. Index 0 contains information about Purpose 1.
    if (!purposeConsents.isEmpty()) {
      String purposeOneString = String.valueOf(purposeConsents.charAt(0));
      boolean hasConsentForPurposeOne = purposeOneString.equals("1");
    }
    // [END read_consent_choices]
  }

  private void syncConsentIdentifier(@NonNull Activity activity) {
    // [START sync_consent_identifier]
    // Example fetching App Set ID to identify the user across apps.
    AppSetIdClient client = AppSet.getClient(activity);
    client
        .getAppSetIdInfo()
        .addOnSuccessListener(
            info -> {
              String appSetId = info.getId();
              ConsentRequestParameters params =
                  new ConsentRequestParameters.Builder().setConsentSyncId(appSetId).build();
            });
    // [END sync_consent_identifier]
  }
}
