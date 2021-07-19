/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.gms.example.appopendemo;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

/** Main activity in the app. */
public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
  }

  /** Override the default implementation when the user presses the back key. */
  @Override
  public void onBackPressed() {
    // Move the task containing the MainActivity to the back of the activity stack, instead of
    // destroying it. Therefore, MainActivity will be shown when the user switches back to the app.
    moveTaskToBack(true);
  }
}
