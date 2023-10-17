package com.google.android.gms.example.appopenexample

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/** The main activity in the app. */
class MainActivity : AppCompatActivity() {

  private lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(applicationContext)
  }

  override fun onCreateOptionsMenu(menu: Menu?): Boolean {
    menuInflater.inflate(R.menu.action_menu, menu)
    val moreMenu = menu?.findItem(R.id.action_more)
    moreMenu?.isVisible = googleMobileAdsConsentManager.isPrivacyOptionsRequired
    return super.onCreateOptionsMenu(menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    val menuItemView = findViewById<View>(item.itemId)
    val activity = this
    PopupMenu(this, menuItemView).apply {
      menuInflater.inflate(R.menu.popup_menu, menu)
      show()
      setOnMenuItemClickListener { popupMenuItem ->
        when (popupMenuItem.itemId) {
          R.id.privacy_settings -> {
            // Handle changes to user consent.
            googleMobileAdsConsentManager.showPrivacyOptionsForm(activity) { formError ->
              if (formError != null) {
                Toast.makeText(activity, formError.message, Toast.LENGTH_SHORT).show()
              }
            }
            true
          }
          // Handle other branches here.
          else -> false
        }
      }
      return super.onOptionsItemSelected(item)
    }
  }

  /** Override the default implementation when the user presses the back key. */
  override fun onBackPressed() {
    // Move the task containing the MainActivity to the back of the activity stack, instead of
    // destroying it. Therefore, MainActivity will be shown when the user switches back to the app.
    moveTaskToBack(true)
  }
}
