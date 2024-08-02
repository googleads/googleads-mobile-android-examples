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

package com.google.android.gms.example.jetpackcomposedemo

import android.content.Context
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.graphics.Color
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
import com.google.android.gms.example.jetpackcomposedemo.GoogleMobileAdsApplication.Companion.BANNER_ADUNIT_ID
import com.google.android.gms.example.jetpackcomposedemo.GoogleMobileAdsApplication.Companion.TAG
import com.google.android.gms.example.jetpackcomposedemo.ui.theme.JetpackComposeDemoTheme
import com.google.android.gms.example.jetpackcomposedemo.ui.theme.LightBlue
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

@Composable
fun LazyBannerScreen(modifier: Modifier = Modifier) {
  val context = LocalContext.current
  val isPreviewMode = LocalInspectionMode.current
  val deviceCurrentWidth = LocalConfiguration.current.screenWidthDp
  val adsToLoad = 5

  // State for loading and completed.
  var isLoading by remember { mutableStateOf(true) }
  var loadedAds by remember { mutableStateOf<List<AdView>>(emptyList()) }
  val tips by remember { mutableStateOf(loadTips(context)) }

  // Load ads when on launch of the composition.
  LaunchedEffect(Unit) {
    if (!isPreviewMode) {
      loadedAds = loadBannerAds(context, adsToLoad, deviceCurrentWidth)
    }
    isLoading = false
  }

  Column {
    // Display page subtitle.
    Box(modifier.fillMaxWidth().background(Color.LightGray)) {
      Text(
        text = context.getString(R.string.text_best_practices),
        modifier.padding(8.dp),
        style = MaterialTheme.typography.titleMedium,
      )
    }
    // Display a loading indicator if ads are still being fetched.
    if (isLoading) {
      CircularProgressIndicator(modifier = modifier.width(100.dp).height(100.dp))
    }
    // Display a Lazy list of loaded ads.
    LazyColumn {
      items(loadedAds) { adView ->
        Column {
          Spacer(modifier = modifier.height(8.dp))
          BannerAd(adView, modifier.fillMaxWidth())
          // Display the loaded content.
          tips.forEach { content ->
            Spacer(modifier = modifier.height(8.dp))
            Box(modifier.fillMaxWidth().background(LightBlue).padding(8.dp)) {
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

private suspend fun loadBannerAd(context: Context, width: Int): AdView {
  return suspendCoroutine { continuation ->
    val adView = AdView(context)
    adView.adUnitId = BANNER_ADUNIT_ID
    val adSize = AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, width)
    adView.setAdSize(adSize)
    adView.adListener =
      object : AdListener() {
        override fun onAdLoaded() {
          super.onAdLoaded()
          Log.i(TAG, context.getString(R.string.banner_loaded))
          continuation.resume(adView)
        }

        override fun onAdFailedToLoad(error: LoadAdError) {
          Log.e(TAG, context.getString(R.string.banner_failedToLoad))
          continuation.resume(adView)
        }

        override fun onAdImpression() {
          super.onAdImpression()
          Log.i(TAG, context.getString(R.string.banner_impression))
        }

        override fun onAdClicked() {
          super.onAdClicked()
          Log.i(TAG, context.getString(R.string.banner_clicked))
        }
      }
    val adRequest = AdRequest.Builder().build()
    adView.loadAd(adRequest)
  }
}

private suspend fun loadBannerAds(context: Context, count: Int, width: Int): List<AdView> =
  coroutineScope {
    val deferredAds = mutableListOf<Deferred<AdView>>()
    // Concurrently load the specified number of ads.
    (1..count).forEach { _ -> deferredAds.add(async { loadBannerAd(context, width) }) }
    return@coroutineScope deferredAds.awaitAll()
  }

fun loadTips(context: Context): List<String> {
  val tipsArray = context.resources.getStringArray(R.array.tips)
  return tipsArray.toList()
}
