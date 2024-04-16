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

package com.google.android.gms.example.jetpackcomposedemo.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * A composable function to create a standard button with text.
 *
 * @param name The text to be displayed on the button.
 * @param enabled Controls whether the button is enabled or disabled (defaults to true).
 * @param modifier The Modifier to be applied to this button.
 * @param onClick The lambda function to be executed when the button is clicked.
 */
@Composable
fun TextButton(
  name: String,
  modifier: Modifier = Modifier,
  enabled: Boolean = true,
  onClick: () -> Unit,
) {
  Button(onClick = { onClick() }, enabled = enabled, modifier = modifier.fillMaxWidth()) {
    Text(name)
  }
}
