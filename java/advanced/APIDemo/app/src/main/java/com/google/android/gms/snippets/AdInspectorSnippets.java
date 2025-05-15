package com.google.android.gms.snippets;

import android.content.Context;
import androidx.annotation.Nullable;
import com.google.android.gms.ads.AdInspectorError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnAdInspectorClosedListener;

/** Java code snippets for the developer guide. */
class AdInspectorSnippets {

  private void openAdInspector(Context context) {
    // [START open_ad_inspector]
    MobileAds.openAdInspector(
        context,
        new OnAdInspectorClosedListener() {
          public void onAdInspectorClosed(@Nullable AdInspectorError error) {
            // Error will be non-null if ad inspector closed due to an error.
          }
        });
    // [END open_ad_inspector]
  }
}
