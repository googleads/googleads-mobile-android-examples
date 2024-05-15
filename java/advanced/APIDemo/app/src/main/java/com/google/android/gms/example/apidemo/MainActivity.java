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
import com.google.android.gms.example.apidemo.databinding.ActivityMainBinding;
import com.google.android.material.navigation.NavigationView;

/** The main activity that controls the menu of the APIDemo app. */
public class MainActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {

  public static final String LOG_TAG = "API_DEMO";
  private ActivityMainBinding activityMainBinding;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
    setContentView(activityMainBinding.getRoot());
    Toolbar toolbar = findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    // Initialize the Mobile Ads SDK with an empty completion listener.
    MobileAds.initialize(this, initializationStatus -> {});

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

    switch (item.getItemId()) {
      case R.id.nav_admob_adlistener:
        newFragment = new AdMobAdListenerFragment();
        break;
      case R.id.nav_admob_adtargeting:
        newFragment = new AdMobAdTargetingFragment();
        break;
      case R.id.nav_admob_bannersizes:
        newFragment = new AdMobBannerSizesFragment();
        break;
      case R.id.nav_collapsible_banner:
        newFragment = new CollapsibleBannerFragment();
        break;
      case R.id.nav_admob_custommute:
        newFragment = new AdMobCustomMuteThisAdFragment();
        break;
      case R.id.nav_gam_adsizes:
        newFragment = new AdManagerMultipleAdSizesFragment();
        break;
      case R.id.nav_gam_appevents:
        newFragment = new AdManagerAppEventsFragment();
        break;
      case R.id.nav_gam_customtargeting:
        newFragment = new AdManagerCustomTargetingFragment();
        break;
      case R.id.nav_gam_fluid:
        newFragment = new AdManagerFluidSizeFragment();
        break;
      case R.id.nav_gam_ppid:
        newFragment = new AdManagerPPIDFragment();
        break;
      case R.id.nav_gam_customcontrols:
        newFragment = new AdManagerCustomControlsFragment();
        break;
      default:
        newFragment = new AdManagerCategoryExclusionFragment();
        break;
    }
    trans.replace(R.id.container, newFragment);
    trans.commit();
    activityMainBinding.drawerLayout.closeDrawer(GravityCompat.START);
    return true;
  }
}
