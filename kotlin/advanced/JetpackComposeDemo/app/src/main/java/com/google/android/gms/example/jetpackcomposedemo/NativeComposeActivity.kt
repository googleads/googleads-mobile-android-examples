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

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.google.android.gms.example.jetpackcomposedemo.composables.NativeAdBodyView
import com.google.android.gms.example.jetpackcomposedemo.composables.NativeAdCallToActionView
import com.google.android.gms.example.jetpackcomposedemo.composables.NativeAdChoicesView
import com.google.android.gms.example.jetpackcomposedemo.composables.NativeAdHeadlineView
import com.google.android.gms.example.jetpackcomposedemo.composables.NativeAdIconView
import com.google.android.gms.example.jetpackcomposedemo.composables.NativeAdMediaView
import com.google.android.gms.example.jetpackcomposedemo.composables.NativeAdPriceView
import com.google.android.gms.example.jetpackcomposedemo.composables.NativeAdStarRatingView
import com.google.android.gms.example.jetpackcomposedemo.composables.NativeAdState
import com.google.android.gms.example.jetpackcomposedemo.composables.NativeAdStoreView
import com.google.android.gms.example.jetpackcomposedemo.composables.NativeAdView
import com.google.android.gms.example.jetpackcomposedemo.ui.StatusText
import com.google.android.gms.example.jetpackcomposedemo.ui.theme.ColorStateError
import com.google.android.gms.example.jetpackcomposedemo.ui.theme.ColorStateLoaded
import com.google.android.gms.example.jetpackcomposedemo.ui.theme.ColorStateUnloaded
import com.google.android.gms.example.jetpackcomposedemo.ui.theme.JetpackComposeDemoTheme

class NativeComposeActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      JetpackComposeDemoTheme {
        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
          NativeLayoutScreen()
        }
      }
    }
  }

  @Preview
  @Composable
  fun NativeLayoutScreenPreview() {
    JetpackComposeDemoTheme {
      // A surface container using the 'background' color from the theme
      Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        NativeLayoutScreen()
      }
    }
  }

  @OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)
  @Composable
  fun NativeLayoutScreen() {
    // Cache the mutable state.
    val context = LocalContext.current
    var messageText by remember { mutableStateOf("Native ad is not loaded.") }
    var messageColor by remember { mutableStateOf(ColorStateUnloaded) }
    var nativeAd by remember { mutableStateOf<NativeAd?>(null) }

    // Construct a native state to configure the NativeComposable.
    val nativeAdState = remember {
      NativeAdState(
        adUnitId = ADUNIT_ID,
        adRequest = AdRequest.Builder().build(),
        onAdLoaded = {
          messageColor = ColorStateLoaded
          messageText = "Native ad is loaded."
          Log.i(TAG, messageText)
        },
        onAdFailedToLoad = { error: LoadAdError ->
          messageColor = ColorStateError
          messageText = "Native ad failed to load with error: ${error.message}"
          Log.e(TAG, messageText)
        },
        onAdImpression = { Log.i(TAG, "Native ad had an impression.") },
        onAdClicked = { Log.i(TAG, "Native ad was clicked.") },
        onAdOpened = { Log.i(TAG, "Native ad was opened.") },
        onAdClosed = { Log.i(TAG, "Native ad was closed.") },
      )
    }

    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
      // Render title.
      TopAppBar(
        title = { Text(text = "Native Compose") },
        navigationIcon = {
          IconButton(
            onClick = {
              val intent = Intent(context, MainActivity::class.java)
              context.startActivity(intent)
            }
          ) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
          }
        },
      )
      // Render ad status.
      StatusText(messageColor, messageText, modifier = Modifier)
      // Native ad composable.
      SampleNativeTemplate(nativeAdState, nativeAd, nativeAdResult = { nativeAd = it })
    }
  }

  @Composable
  fun SampleNativeTemplate(
    nativeAdState: NativeAdState,
    nativeAd: NativeAd?,
    nativeAdResult: (NativeAd) -> Unit,
  ) {
    Box(modifier = Modifier.padding(8.dp).wrapContentHeight(Alignment.Top)) {
      NativeAdView(nativeAdState, nativeAdResult = { nativeAdResult(it) }) {
        Column(Modifier.align(Alignment.TopStart).wrapContentHeight(Alignment.Top)) {
          NativeAdChoicesView()
          Row {
            NativeAdIconView(Modifier.padding(5.dp)) {
              nativeAd?.icon?.let {
                nativeAd.icon?.drawable?.toBitmap()?.let { icon ->
                  Image(bitmap = icon.asImageBitmap(), "Icon")
                }
              }
            }
            Column {
              NativeAdHeadlineView {
                nativeAd?.headline?.let {
                  Text(text = it, style = MaterialTheme.typography.headlineLarge)
                }
              }
              NativeAdStarRatingView {
                nativeAd?.starRating?.let {
                  Text(text = "Rated $it", style = MaterialTheme.typography.labelMedium)
                }
              }
            }
          }
          NativeAdBodyView() { nativeAd?.body?.let { Text(text = it) } }
          NativeAdMediaView(Modifier.fillMaxWidth().height(500.dp).fillMaxHeight())
          Row(Modifier.align(Alignment.End).padding(5.dp)) {
            NativeAdPriceView(Modifier.padding(5.dp).align(Alignment.CenterVertically)) {
              nativeAd?.price?.let { Text(text = it) }
            }
            NativeAdStoreView(Modifier.padding(5.dp).align(Alignment.CenterVertically)) {
              nativeAd?.store?.let { Text(text = it) }
            }
            NativeAdCallToActionView(Modifier.padding(5.dp)) {
              nativeAd?.callToAction?.let { Button(onClick = {}) { Text(text = it) } }
            }
          }
        }
      }
    }
  }

  companion object {
    const val TAG = "GoogleMobileAdsSample"
    // Test AdUnitID for demonstrative purposes.
    // https://developers.google.com/admob/android/test-ads
    const val ADUNIT_ID = "ca-app-pub-3940256099942544/1044960115"
  }
}
