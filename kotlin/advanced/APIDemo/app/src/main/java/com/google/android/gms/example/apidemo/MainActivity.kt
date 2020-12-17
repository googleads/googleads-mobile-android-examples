package com.google.android.gms.example.apidemo

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    setSupportActionBar(toolbar)

    val toggle = ActionBarDrawerToggle(
      this, drawer_layout, toolbar, R.string.navigation_drawer_open,
      R.string.navigation_drawer_close
    )
    drawer_layout.addDrawerListener(toggle)
    toggle.syncState()

    nav_view.setNavigationItemSelectedListener(this)

    val trans = this.supportFragmentManager.beginTransaction()
    trans.replace(R.id.container, AdMobAdListenerFragment())
    trans.commit()
  }

  override fun onBackPressed() {
    if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
      drawer_layout.closeDrawer(GravityCompat.START)
    } else {
      super.onBackPressed()
    }
  }

  override fun onNavigationItemSelected(item: MenuItem): Boolean {
    val trans = this.supportFragmentManager.beginTransaction()

    val newFragment = when (item.itemId) {
      R.id.nav_admob_adlistener -> AdMobAdListenerFragment()
      R.id.nav_admob_adtargeting -> AdMobAdTargetingFragment()
      R.id.nav_admob_bannersizes -> AdMobBannerSizesFragment()
      R.id.nav_admob_custommute -> AdMobCustomMuteThisAdFragment()
      R.id.nav_dfp_adsizes -> DFPMultipleAdSizesFragment()
      R.id.nav_dfp_appevents -> DFPAppEventsFragment()
      R.id.nav_dfp_customtargeting -> DFPCustomTargetingFragment()
      R.id.nav_dfp_fluid -> DFPFluidSizeFragment()
      R.id.nav_dfp_ppid -> DFPPPIDFragment()
      R.id.nav_dfp_customcontrols -> DFPCustomControlsFragment()
      else -> DFPCategoryExclusionFragment()
    }

    trans.replace(R.id.container, newFragment)
    trans.commit()
    drawer_layout.closeDrawer(GravityCompat.START)
    return true
  }

  companion object {
    const val LOG_TAG = "APIDEMO"
  }
}
