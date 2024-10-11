package com.google.example.gms.fullscreennativeexample;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.nativead.NativeAd;
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
  public GoogleMobileAdsConsentManager googleMobileAdsConsentManager;

  private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/2247696110";
  private static final String TAG = "MainActivity";
  private final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);
  private final FragmentManager fragmentManager = getSupportFragmentManager();
  private NativeAd nativeAd;

  public NativeAd getNativeAd() {
    return nativeAd;
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

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
            MainFragment mainFragment =
                (MainFragment) fragmentManager.findFragmentById(R.id.fragment_container_view);
            if (mainFragment != null) {
              mainFragment.binding.loadAdButton.setVisibility(View.VISIBLE);
            }

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
          if (popupMenuItem.getItemId() != R.id.privacy_settings) {
            return false;
          }

          // Handle changes to user consent.
          googleMobileAdsConsentManager.showPrivacyOptionsForm(
              this,
              formError -> {
                if (formError != null) {
                  Toast.makeText(this, formError.getMessage(), Toast.LENGTH_SHORT).show();
                }
              });
          return true;
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
          runOnUiThread(this::loadAd);
        })
        .start();
  }

  public void loadAd() {
    MainFragment mainFragment =
        (MainFragment) fragmentManager.findFragmentById(R.id.fragment_container_view);
    if (mainFragment != null) {
      mainFragment.binding.loadAdButton.setEnabled(false);
    }

    AdLoader adLoader =
        new AdLoader.Builder(this, AD_UNIT_ID)
            .forNativeAd(nativeAd -> {
              MainActivity.this.nativeAd = nativeAd;
              if (mainFragment != null) {
                mainFragment.binding.showAdButton.setEnabled(true);
              }
            })
            .withAdListener(new AdListener() {
              @Override
              public void onAdFailedToLoad(@NonNull LoadAdError adError) {
                if (mainFragment != null) {
                  mainFragment.binding.loadAdButton.setEnabled(true);
                }
                Log.d(
                    TAG,
                    adError.getCode() + "Native ad failed to load : " + adError);
                Toast.makeText(
                    MainActivity.this,
                    adError.getMessage(),
                    Toast.LENGTH_SHORT)
                    .show();
              }
            })
            .build();

    adLoader.loadAd(new AdRequest.Builder().build());
  }
}
