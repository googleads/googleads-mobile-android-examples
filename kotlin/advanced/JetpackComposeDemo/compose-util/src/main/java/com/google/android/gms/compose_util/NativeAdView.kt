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

package com.google.android.gms.compose_util

import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.nativead.AdChoicesView
import com.google.android.gms.ads.nativead.MediaView
import com.google.android.gms.ads.nativead.NativeAdView

/**
 * A CompositionLocal that can provide a `NativeAdView` to ad attributes such as `NativeHeadline`.
 */
internal val LocalNativeAdView = staticCompositionLocalOf<NativeAdView?> { null }

/**
 * This is the Compose wrapper for a NativeAdView.
 *
 * @param modifier The modifier to apply to the native ad.
 * @param content A composable function that defines the rest of the native ad view's elements.
 */
@Composable
fun NativeAdView(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
  val localContext = LocalContext.current
  val nativeAdView = remember { NativeAdView(localContext).apply { id = View.generateViewId() } }

  AndroidView(
    factory = {
      nativeAdView.apply {
        layoutParams =
          ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
          )
        addView(
          ComposeView(context).apply {
            layoutParams =
              ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
              )
            setContent {
              // Set `nativeAdView` as the current LocalNativeAdView so that
              // `content` can access the `NativeAdView` via `LocalNativeAdView.current`.
              // This would allow ad attributes (such as `NativeHeadline`) to attribute
              // its contained View subclass via setter functions (e.g. nativeAdView.headlineView =
              // view)
              CompositionLocalProvider(LocalNativeAdView provides nativeAdView) { content.invoke() }
            }
          }
        )
      }
    },
    modifier = modifier,
  )
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
    factory = { context ->
      ComposeView(context).apply {
        id = View.generateViewId()
        setContent(content)
        nativeAdView.advertiserView = this
      }
    },
    modifier = modifier,
    update = { view -> view.setContent(content) },
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
  val localContext = LocalContext.current
  val localComposeView = remember { ComposeView(localContext).apply { id = View.generateViewId() } }
  AndroidView(
    factory = {
      nativeAdView.bodyView = localComposeView
      localComposeView.apply { setContent(content) }
    },
    modifier = modifier,
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
  val localContext = LocalContext.current
  val localComposeView = remember { ComposeView(localContext).apply { id = View.generateViewId() } }
  AndroidView(
    factory = {
      nativeAdView.callToActionView = localComposeView
      localComposeView.apply { setContent(content) }
    },
    modifier = modifier,
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
  val localContext = LocalContext.current
  AndroidView(
    factory = {
      AdChoicesView(localContext).apply {
        minimumWidth = 15
        minimumHeight = 15
      }
    },
    update = { view -> nativeAdView.adChoicesView = view },
    modifier = modifier,
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
  val localContext = LocalContext.current
  val localComposeView = remember { ComposeView(localContext).apply { id = View.generateViewId() } }
  AndroidView(
    factory = {
      nativeAdView.headlineView = localComposeView
      localComposeView.apply { setContent(content) }
    },
    modifier = modifier,
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
  val localContext = LocalContext.current
  val localComposeView = remember { ComposeView(localContext).apply { id = View.generateViewId() } }
  AndroidView(
    factory = {
      nativeAdView.iconView = localComposeView
      localComposeView.apply { setContent(content) }
    },
    modifier = modifier,
  )
}

/**
 * The ComposeWrapper for a mediaView inside a NativeAdView. This composable must be invoked from
 * within a `NativeAdView`.
 *
 * @param modifier modify the native ad view element.
 */
@Composable
fun NativeAdMediaView(modifier: Modifier = Modifier) {
  val nativeAdView = LocalNativeAdView.current ?: throw IllegalStateException("NativeAdView null")
  val localContext = LocalContext.current
  AndroidView(
    factory = { MediaView(localContext) },
    update = { view -> nativeAdView.mediaView = view },
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
  val localContext = LocalContext.current
  val localComposeView = remember { ComposeView(localContext).apply { id = View.generateViewId() } }
  AndroidView(
    factory = {
      nativeAdView.priceView = localComposeView
      localComposeView.apply { setContent(content) }
    },
    modifier = modifier,
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
  val localContext = LocalContext.current
  val localComposeView = remember { ComposeView(localContext).apply { id = View.generateViewId() } }
  AndroidView(
    factory = {
      nativeAdView.starRatingView = localComposeView
      localComposeView.apply { setContent(content) }
    },
    modifier = modifier,
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
  val localContext = LocalContext.current
  val localComposeView = remember { ComposeView(localContext).apply { id = View.generateViewId() } }
  AndroidView(
    factory = {
      nativeAdView.storeView = localComposeView
      localComposeView.apply { setContent(content) }
    },
    modifier = modifier,
  )
}

/**
 * The composable for a ad attribution inside a NativeAdView. This composable must be invoked from
 * within a `NativeAdView`.
 *
 * @param text The string identifying this view as an advertisement.
 * @param modifier modify the native ad view element.
 */
@Composable
fun NativeAdAttribution(text: String = "Ad", modifier: Modifier = Modifier) {
  Box(
    modifier =
      modifier.background(ButtonDefaults.buttonColors().containerColor).clip(ButtonDefaults.shape)
  ) {
    Text(color = ButtonDefaults.buttonColors().contentColor, text = text)
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
 */
@Composable
fun NativeAdButton(text: String, modifier: Modifier = Modifier) {
  Box(
    modifier =
      modifier
        .background(ButtonDefaults.buttonColors().containerColor)
        .clip(ButtonDefaults.shape)
        .padding(ButtonDefaults.ContentPadding)
  ) {
    Text(color = ButtonDefaults.buttonColors().contentColor, text = text)
  }
}
