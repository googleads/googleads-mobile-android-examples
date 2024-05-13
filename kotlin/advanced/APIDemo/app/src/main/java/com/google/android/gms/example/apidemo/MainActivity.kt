package com.google.android.gms.example.apidemo

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.example.apidemo.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

  private lateinit var mainActivityBinding: ActivityMainBinding

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    mainActivityBinding = ActivityMainBinding.inflate(layoutInflater)
    setContentView(mainActivityBinding.root)
    val toolbar = findViewById<Toolbar>(R.id.toolbar)
    setSupportActionBar(toolbar)

    // Handling onBackPressed.
    val onBackPressedCallback: OnBackPressedCallback =
      object : OnBackPressedCallback(/* enabled= */ true) {
        override fun handleOnBackPressed() {
          if (mainActivityBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            mainActivityBinding.drawerLayout.closeDrawer(GravityCompat.START)
          }
        }
      }
    onBackPressedDispatcher.addCallback(this, onBackPressedCallback)

    // Initialize the Mobile Ads SDK with an empty completion listener.
    MobileAds.initialize(this) {}

    val toggle =
      ActionBarDrawerToggle(
        this,
        mainActivityBinding.drawerLayout,
        toolbar,
        R.string.navigation_drawer_open,
        R.string.navigation_drawer_close,
      )
    mainActivityBinding.drawerLayout.addDrawerListener(toggle)
    toggle.syncState()

    mainActivityBinding.navView.setNavigationItemSelectedListener(this)
  }

  override fun onNavigationItemSelected(item: MenuItem): Boolean {
    val trans = this.supportFragmentManager.beginTransaction()

    val newFragment =
      when (item.itemId) {
        R.id.nav_admob_adlistener -> AdMobAdListenerFragment()
        R.id.nav_admob_adtargeting -> AdMobAdTargetingFragment()
        R.id.nav_admob_bannersizes -> AdMobBannerSizesFragment()
        R.id.nav_collapsible_banner -> CollapsibleBannerFragment()
        R.id.nav_admob_custommute -> AdMobCustomMuteThisAdFragment()
        R.id.nav_gam_adsizes -> AdManagerMultipleAdSizesFragment()
        R.id.nav_gam_appevents -> AdManagerAppEventsFragment()
        R.id.nav_gam_customtargeting -> AdManagerCustomTargetingFragment()
        R.id.nav_gam_fluid -> AdManagerFluidSizeFragment()
        R.id.nav_gam_ppid -> AdManagerPPIDFragment()
        R.id.nav_gam_customcontrols -> AdManagerCustomControlsFragment()
        else -> AdManagerCategoryExclusionFragment()
      }

    trans.replace(R.id.container, newFragment)
    trans.commit()
    mainActivityBinding.drawerLayout.closeDrawer(GravityCompat.START)
    return true
  }

  companion object {
    const val LOG_TAG = "APIDEMO"
  }
}
