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

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.example.jetpackcomposedemo.R
import com.google.android.gms.example.jetpackcomposedemo.ui.theme.JetpackComposeDemoTheme

class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    // Display content edge-to-edge.
    enableEdgeToEdge()
    super.onCreate(savedInstanceState)

    // Handle systemWindowInsets.
    val window = window
    val rootView = window.decorView.findViewById<View>(android.R.id.content)
    ViewCompat.setOnApplyWindowInsetsListener(rootView) { v, windowInsets ->
      val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
      v.updateLayoutParams<ViewGroup.MarginLayoutParams> {
        leftMargin = insets.left
        bottomMargin = insets.bottom
        rightMargin = insets.right
        topMargin = insets.top
      }
      WindowInsetsCompat.CONSUMED
    }

    setContent {
      JetpackComposeDemoTheme {
        Surface(modifier = Modifier.fillMaxHeight(), color = MaterialTheme.colorScheme.background) {
          MainScreen()
        }
      }
    }
  }

  @Composable
  @Preview
  fun MainScreenPreview() = JetpackComposeDemoTheme {
    Surface(modifier = Modifier.fillMaxHeight(), color = MaterialTheme.colorScheme.background) {
      MainScreen()
    }
  }

  @OptIn(ExperimentalMaterial3Api::class)
  @Composable
  fun MainScreen() =
    Scaffold(
      topBar = { TopAppBar(title = { Text(resources.getString(R.string.main_title)) }) },
      // Consume only at the top because we want the column to handle the bottom.
      contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top),
    ) { innerPadding ->
      Column(
        Modifier
          // add the top padding to make sure the column is pushed to under
          // the app bar.
          .padding(top = innerPadding.calculateTopPadding())
          .verticalScroll(rememberScrollState())
      ) {
        Text(resources.getString(R.string.main_title))
        // The column should handle the bottom nav bar padding,
        // but not as padding because it would cut off the content.
        Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
      }
    }
}
