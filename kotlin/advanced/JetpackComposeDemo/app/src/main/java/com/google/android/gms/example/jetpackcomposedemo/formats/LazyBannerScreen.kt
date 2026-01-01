/*
 * Copyright 2024 Google LLC
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

package com.google.android.gms.example.jetpackcomposedemo.formats

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetpackcomposedemo.R
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.compose_util.BannerAd
import com.google.android.gms.example.jetpackcomposedemo.GoogleMobileAdsApplication.Companion.BANNER_AD_UNIT_ID
import com.google.android.gms.example.jetpackcomposedemo.GoogleMobileAdsApplication.Companion.TAG
import com.google.android.gms.example.jetpackcomposedemo.ui.theme.JetpackComposeDemoTheme
import kotlin.coroutines.resume
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.supervisorScope
import kotlinx.coroutines.suspendCancellableCoroutine

@Composable
fun LazyBannerScreen(modifier: Modifier = Modifier) {
  val context = LocalContext.current
  val isPreviewMode = LocalInspectionMode.current
  val deviceCurrentWidth = LocalConfiguration.current.screenWidthDp
  val adsToLoad = 5

  // Indicate whether the banner ads are currently being loaded.
  var isLoadingAds by remember { mutableStateOf(true) }
  var loadedAds by remember { mutableStateOf<List<AdView>>(emptyList()) }
  val fillerText = loadFillerText(context)

  // Load ads when on launch of the composition.
  LaunchedEffect(Unit) {
    if (!isPreviewMode) {
      loadedAds = loadBannerAds(context, adsToLoad, deviceCurrentWidth)
    }
    isLoadingAds = false
  }

  // Display a loading indicator if ads are still being fetched.
  if (isLoadingAds) {
    CircularProgressIndicator(modifier = modifier.width(100.dp).height(100.dp))
  } else {
    // Display a lazy list with loaded ads and filler content.
    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
      items(loadedAds) { adView ->
        Column {
          BannerAd(adView, modifier.fillMaxWidth())
          // Display the filler content.
          fillerText.forEach { content ->
            Box(
              modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(8.dp)
            ) {
              Text(
                text = content,
                modifier.padding(8.dp),
                style = MaterialTheme.typography.bodyMedium,
              )
            }
          }
        }
      }
    }
  }

  // Clean up the AdViews after use.
  DisposableEffect(Unit) { onDispose { loadedAds.forEach { adView -> adView.destroy() } } }
}

@Preview
@Composable
private fun LazyBannerScreenPreview() {
  JetpackComposeDemoTheme {
    Surface(color = MaterialTheme.colorScheme.background) { LazyBannerScreen() }
  }
}

private suspend fun loadBannerAd(context: Context, width: Int): Result<AdView> {
  return suspendCancellableCoroutine { continuation ->
    val adView = AdView(context)
    adView.adUnitId = BANNER_AD_UNIT_ID
    val adSize = AdSize.getCurrentOrientationInlineAdaptiveBannerAdSize(context, width)
    adView.setAdSize(adSize)
    adView.adListener =
      object : AdListener() {
        override fun onAdLoaded() {
          Log.d(TAG, "Banner ad was loaded.")
          continuation.resume(Result.success(adView))
        }

        override fun onAdFailedToLoad(error: LoadAdError) {
          Log.e(TAG, "Banner ad failed to load: ${error.message}")
          adView.destroy()
          continuation.resume(Result.failure(Error(error.message)))
        }

        override fun onAdImpression() {
          Log.d(TAG, "Banner ad recorded an impression.")
        }

        override fun onAdClicked() {
          Log.d(TAG, "Banner ad was clicked.")
        }
      }

    continuation.invokeOnCancellation {
      // When the coroutine is cancelled, clean up resources.
      adView.destroy()
    }

    val adRequest = AdRequest.Builder().build()
    adView.loadAd(adRequest)
  }
}

private suspend fun loadBannerAds(context: Context, count: Int, width: Int): List<AdView> =
  supervisorScope {
    List(count) {
        async {
          try {
            loadBannerAd(context, width).getOrNull()
          } catch (e: Error) {
            null
          }
        }
      }
      .awaitAll()
      .filterNotNull()
  }

fun loadFillerText(context: Context): List<String> {
  val fillerContent = context.resources.getStringArray(R.array.lazy_banner_filler_content)
  return fillerContent.toList()
}
