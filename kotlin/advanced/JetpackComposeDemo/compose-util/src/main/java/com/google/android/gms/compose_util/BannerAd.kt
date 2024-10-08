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

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.google.android.gms.ads.AdView

/**
 * A composable function to display a banner advertisement.
 *
 * @param adView The banner [AdView].
 * @param modifier The modifier to apply to the banner ad.
 */
@Composable
fun BannerAd(adView: AdView, modifier: Modifier = Modifier) {
  var parent by remember { mutableStateOf<FrameLayout?>(null) }
  // Ad load does not work in preview mode because it requires a network connection.
  if (LocalInspectionMode.current) {
    Box { Text(text = "Google Mobile Ads preview banner.", modifier.align(Alignment.Center)) }
    return
  }

  AndroidView(
    modifier = modifier.wrapContentSize(),
    factory = { context -> FrameLayout(context).also { parent = it } },
    update = { layout ->
      disposeLayout(adView, layout)
      layout.addView(adView)
    },
  )

  // Pause and resume the AdView when the lifecycle is paused and resumed.
  LifecycleResumeEffect(adView) {
    adView.resume()
    onPauseOrDispose { adView.pause() }
  }

  // Clean up the AdView after use.
  DisposableEffect(Unit) { onDispose { disposeLayout(adView, parent) } }
}

/** Clean up the AdView after use. */
private fun disposeLayout(adView: AdView, layout: FrameLayout?) {
  // Ensure AdViews and Composable references are up to date.
  (adView.parent as? ViewGroup)?.removeView(adView)
  layout?.removeAllViews()
}
