package com.google.example.gms.fullscreennativeexample;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.example.gms.fullscreennativeexample.databinding.ActivityMainBinding;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;

/** An activity class that loads a native ad. */
@SuppressLint("SetTextI18n")
public class MainActivity extends AppCompatActivity {

  // Check your logcat output for the test device hashed ID e.g.
  // "Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
  // to get test ads on this device" or
  // "Use new ConsentDebugSettings.Builder().addTestDeviceHashedId("ABCDEF012345") to set this as
  // a debug device".
  public static final String TEST_DEVICE_HASHED_ID = "ABCDEF012345";
  private static final String TAG = "MainActivity";
  private final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);
  private ActivityMainBinding binding;
  private GoogleMobileAdsConsentManager googleMobileAdsConsentManager;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityMainBinding.inflate(getLayoutInflater());
    View view = binding.getRoot();
    setContentView(view);

    // Log the Mobile Ads SDK version.
    Log.d(TAG, "Google Mobile Ads SDK Version: " + MobileAds.getVersion());

    googleMobileAdsConsentManager =
        GoogleMobileAdsConsentManager.getInstance(getApplicationContext());
    googleMobileAdsConsentManager.gatherConsent(
        this,
        consentError -> {
          if (consentError != null) {
            // Consent not obtained in current session.
            Log.w(
                TAG,
                String.format("%s: %s", consentError.getErrorCode(), consentError.getMessage()));
          }

          if (googleMobileAdsConsentManager.canRequestAds()) {
            binding.loadAdButton.setVisibility(View.VISIBLE);
            initializeMobileAdsSdk();
          }

          if (googleMobileAdsConsentManager.isPrivacyOptionsRequired()) {
            // Regenerate the options menu to include a privacy setting.
            invalidateOptionsMenu();
          }
        });

    // This sample attempts to load ads using consent obtained in the previous session.
    if (googleMobileAdsConsentManager.canRequestAds()) {
      initializeMobileAdsSdk();
    }

    // Configure "Load ad" button.
    binding.loadAdButton.setOnClickListener(
        unusedView -> {
          if (googleMobileAdsConsentManager.canRequestAds()) {
            binding.loadAdButton.setEnabled(false);
            binding.showAdButton.setEnabled(false);

            Application application = getApplication();
            ((MyApplication) application).loadAd(this,
                this::handleNativeAdEventListener);
          }
        });

    // Configure "Show ad" button.
    binding.showAdButton.setOnClickListener(
        unusedView -> {
          binding.loadAdButton.setEnabled(true);
          binding.showAdButton.setEnabled(false);
          startActivity(new Intent(MainActivity.this, NativeAdActivity.class));
        }
    );
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.action_menu, menu);
    MenuItem moreMenu = menu.findItem(R.id.action_more);
    moreMenu.setVisible(googleMobileAdsConsentManager.isPrivacyOptionsRequired());
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    View menuItemView = findViewById(item.getItemId());
    PopupMenu popup = new PopupMenu(this, menuItemView);
    popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());
    popup.show();
    popup.setOnMenuItemClickListener(
        popupMenuItem -> {
          if (popupMenuItem.getItemId() == R.id.privacy_settings) {
            // Handle changes to user consent.
            googleMobileAdsConsentManager.showPrivacyOptionsForm(
                this,
                formError -> {
                  if (formError != null) {
                    Toast.makeText(this, formError.getMessage(), Toast.LENGTH_SHORT).show();
                  }
                });
            return true;
          }
          return false;
        });
    return super.onOptionsItemSelected(item);
  }

  private void initializeMobileAdsSdk() {
    if (isMobileAdsInitializeCalled.getAndSet(true)) {
      return;
    }

    // Set your test devices.
    MobileAds.setRequestConfiguration(
        new RequestConfiguration.Builder()
            .setTestDeviceIds(Collections.singletonList(TEST_DEVICE_HASHED_ID))
            .build());

    new Thread(
        () -> {
          // Initialize the Google Mobile Ads SDK on a background thread.
          MobileAds.initialize(this, initializationStatus -> {});

          // Load an ad on the main thread.
          runOnUiThread(
              () -> {
                Application application = getApplication();
                ((MyApplication) application).loadAd(this,
                    this::handleNativeAdEventListener);
              });
        })
        .start();
  }

  private void handleNativeAdEventListener(boolean success) {
    binding.loadAdButton.setEnabled(!success);
    binding.showAdButton.setEnabled(success);
    if (!success) {
      Toast.makeText(
              this,
              "Failed to load native ad.",
              Toast.LENGTH_SHORT)
          .show();
    }
  }
}
