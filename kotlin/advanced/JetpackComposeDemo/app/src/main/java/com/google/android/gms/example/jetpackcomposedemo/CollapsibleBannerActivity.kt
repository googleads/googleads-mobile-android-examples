package com.google.android.gms.example.jetpackcomposedemo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.example.jetpackcomposedemo.composables.BannerAd
import com.google.android.gms.example.jetpackcomposedemo.composables.BannerAdState
import com.google.android.gms.example.jetpackcomposedemo.ui.theme.ColorStateError
import com.google.android.gms.example.jetpackcomposedemo.ui.theme.ColorStateLoaded
import com.google.android.gms.example.jetpackcomposedemo.ui.theme.ColorStateUnloaded
import com.google.android.gms.example.jetpackcomposedemo.ui.theme.JetpackComposeDemoTheme

class CollapsibleBannerActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContent {
      JetpackComposeDemoTheme {
        Surface(modifier = Modifier.fillMaxHeight(), color = MaterialTheme.colorScheme.background) {
          CollapsibleBannerScreen()
        }
      }
    }
  }

  @Preview
  @Composable
  fun CollapsibleBannerScreenPreview() {
    JetpackComposeDemoTheme {
      Surface(modifier = Modifier.fillMaxHeight(), color = MaterialTheme.colorScheme.background) {
        CollapsibleBannerScreen()
      }
    }
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  fun CollapsibleBannerScreen() {
    // Cache the mutable state for our notification bar.
    val context = LocalContext.current
    var messageText by remember { mutableStateOf("Collapsible Banner is not loaded.") }
    var messageColor by remember { mutableStateOf(ColorStateUnloaded) }

    // Construct a banner ad state which configures our BannerAd composable.

    // Set the `collapsible` extra. Acceptable values are:
    // `top` if the ad is placed at the top of the screen.
    // 'bottom` if the ad is placed at the bottom of the screen.
    val collapsibleExtras = Bundle()
    collapsibleExtras.putString("collapsible", "top")

    val bannerState =
      BannerAdState(
        adUnitId = ADUNIT_ID,
        adSize = AdSize.BANNER,
        adRequest =
          AdRequest.Builder()
            .addNetworkExtrasBundle(AdMobAdapter::class.java, collapsibleExtras)
            .build(),
        onAdLoaded = {
          messageColor = ColorStateLoaded
          messageText = "Banner is loaded."
          Log.i(TAG, messageText)
        },
        onAdFailedToLoad = { error: LoadAdError ->
          messageColor = ColorStateError
          messageText = "Banner is failed to load with error: ${error.message}"
          Log.e(TAG, messageText)
        },
        onAdImpression = { Log.i(TAG, "Banner ad impression.") },
        onAdClicked = { Log.i(TAG, "Banner ad clicked.") },
        onAdOpened = { Log.i(TAG, "Banner ad opened.") },
        onAdClosed = { Log.i(TAG, "Banner ad closed.") },
      )

    Column(
      modifier = Modifier.verticalScroll(rememberScrollState()),
      content = {
        // Render title.
        TopAppBar(
          title = { Text(text = "Collapsible Banner") },
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
        // Render the BannerAd composable.
        BannerAd(bannerState, modifier = Modifier)
      },
    )
  }

  companion object {
    const val TAG = "GoogleMobileAdsSample"
    // Test AdUnitID for demonstrative purposes.
    // https://developers.google.com/admob/android/test-ads
    const val ADUNIT_ID = "ca-app-pub-3940256099942544/2014213617"
  }
}
