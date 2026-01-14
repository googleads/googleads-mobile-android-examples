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

package com.google.android.gms.example.jetpackcomposedemo.formats.compose_utils

import android.view.ViewGroup
import android.widget.ImageView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.nativead.AdChoicesView
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

/**
 * A CompositionLocal that can provide a `NativeAdView` to ad attributes such as `NativeHeadline`.
 */
internal val LocalNativeAdView = staticCompositionLocalOf<NativeAdView?> { null }

/**
 * This is the Compose wrapper for a NativeAdView.
 *
 * @param nativeAd The `NativeAd` object containing the ad assets to be displayed in this view.
 * @param modifier The modifier to apply to the native ad.
 * @param content A composable function that defines the rest of the native ad view's elements.
 */
@Composable
fun NativeAdView(
  nativeAd: NativeAd,
  modifier: Modifier = Modifier,
  content: @Composable () -> Unit,
) {
  val nativeAdViewRef = remember { mutableStateOf<NativeAdView?>(null) }
  AndroidView(
    factory = { context ->
      val composeView =
        ComposeView(context).apply {
          layoutParams =
            ViewGroup.LayoutParams(
              ViewGroup.LayoutParams.MATCH_PARENT,
              ViewGroup.LayoutParams.MATCH_PARENT,
            )
        }
      NativeAdView(context)
        .apply {
          layoutParams =
            ViewGroup.LayoutParams(
              ViewGroup.LayoutParams.MATCH_PARENT,
              ViewGroup.LayoutParams.MATCH_PARENT,
            )
          addView(composeView)
        }
        .also { nativeAdViewRef.value = it }
    },
    modifier = modifier,
    update = { view ->
      val composeView = view.getChildAt(0) as? ComposeView
      composeView?.setContent {
        // Set `nativeAdView` as the current LocalNativeAdView so that
        // `content` can access the `NativeAdView` via `LocalNativeAdView.current`.
        // This would allow ad attributes (such as `NativeHeadline`) to attribute
        // its contained View subclass via setter functions (e.g. nativeAdView.headlineView =
        // view)
        CompositionLocalProvider(LocalNativeAdView provides view) { content() }
      }
    },
  )
  val currentNativeAd by rememberUpdatedState(nativeAd)
  SideEffect { nativeAdViewRef.value?.setNativeAd(currentNativeAd) }
}

/**
 * The ComposeWrapper container for an advertiserView inside a NativeAdView. This composable must be
 * invoked from within a `NativeAdView`.
 *
 * @param modifier modify the native ad view element.
 * @param content A composable function that defines the content of this native asset.
 */
@Composable
fun NativeAdAdvertiserView(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
  val nativeAdView = LocalNativeAdView.current ?: throw IllegalStateException("NativeAdView null")
  AndroidView(
    factory = { context -> ComposeView(context) },
    modifier = modifier,
    update = { view ->
      nativeAdView.advertiserView = view
      view.setContent(content)
    },
  )
}

/**
 * The ComposeWrapper container for a bodyView inside a NativeAdView. This composable must be
 * invoked from within a `NativeAdView`.
 *
 * @param modifier modify the native ad view element.
 * @param content A composable function that defines the content of this native asset.
 */
@Composable
fun NativeAdBodyView(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
  val nativeAdView = LocalNativeAdView.current ?: throw IllegalStateException("NativeAdView null")
  AndroidView(
    factory = { context -> ComposeView(context) },
    modifier = modifier,
    update = { view ->
      nativeAdView.bodyView = view
      view.setContent(content)
    },
  )
}

/**
 * The ComposeWrapper container for a callToActionView inside a NativeAdView. This composable must
 * be invoked from within a `NativeAdView`.
 *
 * @param modifier modify the native ad view element.
 * @param content A composable function that defines the content of this native asset.
 */
@Composable
fun NativeAdCallToActionView(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
  val nativeAdView = LocalNativeAdView.current ?: throw IllegalStateException("NativeAdView null")
  AndroidView(
    factory = { context -> ComposeView(context) },
    modifier = modifier,
    update = { view ->
      nativeAdView.callToActionView = view
      view.setContent(content)
    },
  )
}

/**
 * The ComposeWrapper for a adChoicesView inside a NativeAdView. This composable must be invoked
 * from within a `NativeAdView`.
 *
 * @param modifier modify the native ad view element.
 */
@Composable
fun NativeAdChoicesView(modifier: Modifier = Modifier) {
  val nativeAdView = LocalNativeAdView.current ?: throw IllegalStateException("NativeAdView null")
  AndroidView(
    factory = { context ->
      AdChoicesView(context).apply {
        minimumWidth = 15
        minimumHeight = 15
      }
    },
    modifier = modifier,
    update = { view -> nativeAdView.adChoicesView = view },
  )
}

/**
 * The ComposeWrapper container for a headlineView inside a NativeAdView. This composable must be
 * invoked from within a `NativeAdView`.
 *
 * @param modifier modify the native ad view element.
 * @param content A composable function that defines the content of this native asset.
 */
@Composable
fun NativeAdHeadlineView(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
  val nativeAdView = LocalNativeAdView.current ?: throw IllegalStateException("NativeAdView null")
  AndroidView(
    factory = { context -> ComposeView(context) },
    modifier = modifier,
    update = { view ->
      nativeAdView.headlineView = view
      view.setContent(content)
    },
  )
}

/**
 * The ComposeWrapper container for a iconView inside a NativeAdView. This composable must be
 * invoked from within a `NativeAdView`.
 *
 * @param modifier modify the native ad view element.
 * @param content A composable function that defines the content of this native asset.
 */
@Composable
fun NativeAdIconView(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
  val nativeAdView = LocalNativeAdView.current ?: throw IllegalStateException("NativeAdView null")
  AndroidView(
    factory = { context -> ComposeView(context) },
    modifier = modifier,
    update = { view ->
      nativeAdView.iconView = view
      view.setContent(content)
    },
  )
}

/**
 * The ComposeWrapper for a mediaView inside a NativeAdView. This composable must be invoked from
 * within a `NativeAdView`.
 *
 * @param modifier modify the native ad view element.
 * @param scaleType The ImageView.ScaleType to apply to the image/media within the MediaView.
 */
@Composable
fun NativeAdMediaView(modifier: Modifier = Modifier, scaleType: ImageView.ScaleType? = null) {
  val nativeAdView = LocalNativeAdView.current ?: throw IllegalStateException("NativeAdView null")
  AndroidView(
    factory = { context -> MediaView(context) },
    update = { view ->
      nativeAdView.mediaView = view
      scaleType?.let { type -> view.setImageScaleType(type) }
    },
    modifier = modifier,
  )
}

/**
 * The ComposeWrapper container for a priceView inside a NativeAdView. This composable must be
 * invoked from within a `NativeAdView`.
 *
 * @param modifier modify the native ad view element.
 * @param content A composable function that defines the content of this native asset.
 */
@Composable
fun NativeAdPriceView(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
  val nativeAdView = LocalNativeAdView.current ?: throw IllegalStateException("NativeAdView null")
  AndroidView(
    factory = { context -> ComposeView(context) },
    modifier = modifier,
    update = { view ->
      nativeAdView.priceView = view
      view.setContent(content)
    },
  )
}

/**
 * The ComposeWrapper container for a starRatingView inside a NativeAdView. This composable must be
 * invoked from within a `NativeAdView`.
 *
 * @param modifier modify the native ad view element.
 * @param content A composable function that defines the content of this native asset.
 */
@Composable
fun NativeAdStarRatingView(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
  val nativeAdView = LocalNativeAdView.current ?: throw IllegalStateException("NativeAdView null")
  AndroidView(
    factory = { context -> ComposeView(context) },
    modifier = modifier,
    update = { view ->
      nativeAdView.starRatingView = view
      view.setContent(content)
    },
  )
}

/**
 * The ComposeWrapper container for a storeView inside a NativeAdView. This composable must be
 * invoked from within a `NativeAdView`.
 *
 * @param modifier modify the native ad view element.
 * @param content A composable function that defines the content of this native asset.
 */
@Composable
fun NativeAdStoreView(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
  val nativeAdView = LocalNativeAdView.current ?: throw IllegalStateException("NativeAdView null")
  AndroidView(
    factory = { context -> ComposeView(context) },
    modifier = modifier,
    update = { view ->
      nativeAdView.storeView = view
      view.setContent(content)
    },
  )
}

/**
 * The composable for a ad attribution inside a NativeAdView. This composable must be invoked from
 * within a `NativeAdView`.
 *
 * @param modifier modify the native ad view element.
 * @param text The string identifying this view as an advertisement.
 * @param shape The shape of the attribution.
 * @param containerColor The background color of the attribution.
 * @param contentColor The text color of the attribution.
 * @param padding The padding around the attribution text.
 */
@Composable
fun NativeAdAttribution(
  modifier: Modifier = Modifier,
  text: String = "Ad",
  shape: Shape = ButtonDefaults.shape,
  containerColor: Color = ButtonDefaults.buttonColors().containerColor,
  contentColor: Color = ButtonDefaults.buttonColors().contentColor,
  padding: PaddingValues = PaddingValues(horizontal = 4.dp, vertical = 2.dp),
) {
  Box(modifier = modifier.background(containerColor, shape).padding(padding)) {
    Text(color = contentColor, text = text)
  }
}

/**
 * The composable for a button inside a NativeAdView. This composable must be invoked from within a
 * NativeAdView.
 *
 * The Jetpack Compose button implements a click handler which overrides the native ad click
 * handler, causing issues. The NativeAdButton does not implement a click handler. To handle native
 * ad clicks, use the NativeAd AdListener onAdClicked callback.
 *
 * @param text The string identifying this view as an advertisement.
 * @param modifier modify the native ad view element.
 * @param shape The shape of the button.
 * @param containerColor The background color of the button.
 * @param contentColor The text color of the button.
 * @param padding The padding around the button text.
 */
@Composable
fun NativeAdButton(
  text: String,
  modifier: Modifier = Modifier,
  shape: Shape = ButtonDefaults.shape,
  containerColor: Color = ButtonDefaults.buttonColors().containerColor,
  contentColor: Color = ButtonDefaults.buttonColors().contentColor,
  padding: PaddingValues = ButtonDefaults.ContentPadding,
) {
  Box(modifier = modifier.background(containerColor, shape).padding(padding)) {
    Text(color = contentColor, text = text)
  }
}
