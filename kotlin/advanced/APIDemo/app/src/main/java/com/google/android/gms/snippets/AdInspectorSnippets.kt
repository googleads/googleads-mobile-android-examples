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
