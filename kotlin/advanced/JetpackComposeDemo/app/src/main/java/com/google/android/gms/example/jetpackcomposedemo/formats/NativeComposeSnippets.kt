package com.google.android.gms.example.jetpackcomposedemo.formats

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.compose_util.NativeAdView

// [START compose_native_ad]
@Composable
fun DisplayNativeAdView(nativeAd: NativeAd) {
  // `NativeAdView` is a custom composable that wraps the SDK's `NativeAdView`.
  NativeAdView {
    // Access assets from the ad object
    nativeAd.headline?.let { Text(text = it) }
    nativeAd.body?.let { Text(text = it) }
  }
}
// [END compose_native_ad]
