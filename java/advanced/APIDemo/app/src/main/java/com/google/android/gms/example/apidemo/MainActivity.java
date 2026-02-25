package com.google.android.gms.example.apidemo;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.example.apidemo.databinding.ActivityMainBinding;
import com.google.android.gms.example.apidemo.preloading.AdMobPreloadingAdsFragment;
import com.google.android.material.navigation.NavigationView;
import java.util.Arrays;

/** The main activity that controls the menu of the APIDemo app. */
public class MainActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {

  // Check your logcat output for the test device hashed ID e.g.
  // "Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
  // to get test ads on this device" or
  // "Use new ConsentDebugSettings.Builder().addTestDeviceHashedId("ABCDEF012345") to set this as
  // a debug device".
  public static final String TEST_DEVICE_HASHED_ID = "ABCDEF012345";

  public static final String LOG_TAG = "API_DEMO";
  private ActivityMainBinding activityMainBinding;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
    setContentView(activityMainBinding.getRoot());
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    // Set your test devices.
    MobileAds.setRequestConfiguration(
        new RequestConfiguration.Builder()
            .setTestDeviceIds(Arrays.asList(TEST_DEVICE_HASHED_ID))
            .build());

    new Thread(
            () -> {
              // Initialize the Google Mobile Ads SDK on a background thread.
              MobileAds.initialize(this, initializationStatus -> {});
            })
        .start();

    ActionBarDrawerToggle toggle =
        new ActionBarDrawerToggle(
            this,
            activityMainBinding.drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close);
    activityMainBinding.drawerLayout.addDrawerListener(toggle);
    toggle.syncState();

    activityMainBinding.navigationView.setNavigationItemSelectedListener(this);

    OnBackPressedCallback onBackPressedCallback =
        new OnBackPressedCallback(true) {
          @Override
          public void handleOnBackPressed() {
            if (activityMainBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
              activityMainBinding.drawerLayout.closeDrawer(GravityCompat.START);
            }
          }
        };
    getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
  }

  @Override
  public boolean onNavigationItemSelected(MenuItem item) {
    FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
    Fragment newFragment = new Fragment();
    int id = item.getItemId();

    if (id == R.id.nav_admob_adlistener) {
      newFragment = new AdMobAdListenerFragment();
    } else if (id == R.id.nav_admob_adtargeting) {
      newFragment = new AdMobAdTargetingFragment();
    } else if (id == R.id.nav_admob_bannersizes) {
      newFragment = new AdMobBannerSizesFragment();
    } else if (id == R.id.nav_collapsible_banner) {
      newFragment = new CollapsibleBannerFragment();
    } else if (id == R.id.nav_admob_custommute) {
      newFragment = new AdMobCustomMuteThisAdFragment();
    } else if (id == R.id.nav_admob_preload) {
      newFragment = new AdMobPreloadingAdsFragment();
    } else if (id == R.id.nav_admob_browser) {
      newFragment = new InAppBrowserFragment();
    } else if (id == R.id.nav_gam_adsizes) {
      newFragment = new AdManagerMultipleAdSizesFragment();
    } else if (id == R.id.nav_gam_appevents) {
      newFragment = new AdManagerAppEventsFragment();
    } else if (id == R.id.nav_gam_customtargeting) {
      newFragment = new AdManagerCustomTargetingFragment();
    } else if (id == R.id.nav_gam_fluid) {
      newFragment = new AdManagerFluidSizeFragment();
    } else if (id == R.id.nav_gam_ppid) {
      newFragment = new AdManagerPPIDFragment();
    } else if (id == R.id.nav_gam_customcontrols) {
      newFragment = new AdManagerCustomControlsFragment();
    } else {
      newFragment = new AdManagerCategoryExclusionFragment();
    }
    trans.replace(R.id.container, newFragment);
    trans.commit();
    activityMainBinding.drawerLayout.closeDrawer(GravityCompat.START);
    return true;
  }
}
