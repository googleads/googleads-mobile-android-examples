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
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jetpackcomposedemo.R
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.example.jetpackcomposedemo.composables.NativeAd
import com.google.android.gms.example.jetpackcomposedemo.composables.NativeAdState
import com.google.android.gms.example.jetpackcomposedemo.ui.theme.ColorStateError
import com.google.android.gms.example.jetpackcomposedemo.ui.theme.ColorStateLoaded
import com.google.android.gms.example.jetpackcomposedemo.ui.theme.ColorStateUnloaded
import com.google.android.gms.example.jetpackcomposedemo.ui.theme.JetpackComposeDemoTheme

class NativeActivity : ComponentActivity() {

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
    // Cache the mutable state for our notification bar.
    val context = LocalContext.current
    var messageText by remember { mutableStateOf("Native ad is not loaded.") }
    var messageColor by remember { mutableStateOf(ColorStateUnloaded) }

    // Construct a banner state to configure the BannerComposable
    val nativeState =
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
        onAdImpression = { Log.i(TAG, "Native ad impression") },
        onAdClicked = { Log.i(TAG, "Native ad clicked") },
        onAdOpened = { Log.i(TAG, "Native ad opened") },
        onAdClosed = { Log.i(TAG, "Native ad closed.") },
      )
    Column(
      modifier = Modifier.verticalScroll(rememberScrollState()),
      content = {
        // Render title.
        TopAppBar(
          title = { Text(text = "Native") },
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
        // Render status.
        Box(modifier = Modifier.fillMaxSize().background(messageColor)) {
          Text(text = messageText, style = MaterialTheme.typography.bodyLarge)
        }
        // Render NativeAd composable.
        Box(modifier = Modifier.fillMaxWidth().background(Color.Gray).padding(8.dp)) {
          NativeAd(
            nativeAdState = nativeState,
            R.layout.nativead,
            modifier = Modifier.fillMaxWidth(),
          )
        }
      },
    )
  }

  companion object {
    const val TAG = "GoogleMobileAdsSample"
    // Test AdUnitID for demonstrative purposes.
    // https://developers.google.com/admob/android/test-ads
    const val ADUNIT_ID = "ca-app-pub-3940256099942544/2247696110"
  }
}
