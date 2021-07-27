package com.google.android.gms.example.appopenexample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/** The main activity in the app. */
class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
  }

  /** Override the default implementation when the user presses the back key. */
  override fun onBackPressed() {
    // Move the task containing the MainActivity to the back of the activity stack, instead of
    // destroying it. Therefore, MainActivity will be shown when the user switches back to the app.
    moveTaskToBack(true)
  }
}
