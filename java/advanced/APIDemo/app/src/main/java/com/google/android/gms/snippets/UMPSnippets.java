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
import androidx.annotation.NonNull;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;

final class UMPSnippets {

  // Example identifier.
  private static final String CONSENT_SYNC_IDENTIFIER = "12JD92JD8078S8J29SDOAKC0EF230337";

  private final ConsentInformation consentInformation;

  public UMPSnippets(@NonNull Context context) {
    this.consentInformation = UserMessagingPlatform.getConsentInformation(context);
  }

  private void syncConsentIdentifier(@NonNull Activity activity) {
    // [START sync_consent_identifier]
    ConsentRequestParameters parameters = new ConsentRequestParameters.Builder().build();
    // Sets the consent sync ID to sync the user consent status collected with the same ID.
    // new ConsentRequestParameters.Builder().setConsentSyncId(CONSENT_SYNC_IDENTIFIER).build();

    // Proceed with calling the UMP SDK as normal.
    consentInformation.requestConsentInfoUpdate(
        activity,
        parameters,
        () -> {
          // ...
        },
        requestConsentError -> {
          // ...
        });
    // [END sync_consent_identifier]
  }
}
