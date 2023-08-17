package com.google.ads.rewardedinterstitialexample;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.ads.rewardedinterstitialexample.AdDialogFragment.AdDialogInteractionListener;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;
import java.util.concurrent.atomic.AtomicBoolean;

/** Main Activity. Inflates main activity xml. */
@SuppressLint("SetTextI18n")
public class MainActivity extends AppCompatActivity {
  private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/5354046379";
  private final AtomicBoolean isMobileAdsInitializeCalled = new AtomicBoolean(false);
  private static final long COUNTER_TIME = 10;
  private static final int GAME_OVER_REWARD = 1;
  private static final String TAG = "MainActivity";

  private int coinCount;
  private TextView coinCountText;
  private CountDownTimer countDownTimer;
  private boolean gameOver;
  private boolean gamePaused;

  private GoogleMobileAdsConsentManager googleMobileAdsConsentManager;
  private RewardedInterstitialAd rewardedInterstitialAd;
  private Button retryButton;
  private long timeRemaining;
  boolean isLoadingAds;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // Log the Mobile Ads SDK version.
    Log.d(TAG, "Google Mobile Ads SDK Version: " + MobileAds.getVersion());

    googleMobileAdsConsentManager = new GoogleMobileAdsConsentManager(this);

    googleMobileAdsConsentManager.gatherConsent(
        consentError -> {
          if (consentError != null) {
            // Consent not obtained in current session.
            Log.w(
                TAG,
                String.format("%s: %s", consentError.getErrorCode(), consentError.getMessage()));
          }

          startGame();

          if (googleMobileAdsConsentManager.canRequestAds()) {
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

    // Create the "retry" button, which tries to show a rewarded ad between game plays.
    retryButton = findViewById(R.id.retry_button);
    retryButton.setVisibility(View.INVISIBLE);
    retryButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            startGame();
            if (
                rewardedInterstitialAd != null
                    && !isLoadingAds
                    && googleMobileAdsConsentManager.canRequestAds()
            ) {
              loadRewardedInterstitialAd();
            }
          }
        });

    // Display current coin count to user.
    coinCountText = findViewById(R.id.coin_count_text);
    coinCount = 0;
    coinCountText.setText("Coins: " + coinCount);
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
            pauseGame();
            googleMobileAdsConsentManager.showPrivacyOptionsForm(
                this,
                formError -> {
                  if (formError != null) {
                    Toast.makeText(this, formError.getMessage(), Toast.LENGTH_SHORT).show();
                  }
                  resumeGame();
                });
            return true;
          }
          return false;
        });
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onPause() {
    super.onPause();
    pauseGame();
  }

  @Override
  public void onResume() {
    super.onResume();
    resumeGame();
  }

  private void pauseGame() {
    if (gameOver || gamePaused) {
      return;
    }
    countDownTimer.cancel();
    gamePaused = true;
  }

  private void resumeGame() {
    if (gameOver || !gamePaused) {
      return;
    }
    createTimer(timeRemaining);
    gamePaused = false;
  }

  private void loadRewardedInterstitialAd() {
    if (rewardedInterstitialAd == null) {
      isLoadingAds = true;

      AdRequest adRequest = new AdRequest.Builder().build();
      // Use the test ad unit ID to load an ad.
      RewardedInterstitialAd.load(
          MainActivity.this,
          AD_UNIT_ID,
          adRequest,
          new RewardedInterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(RewardedInterstitialAd ad) {
              Log.d(TAG, "onAdLoaded");

              rewardedInterstitialAd = ad;
              isLoadingAds = false;
              Toast.makeText(MainActivity.this, "onAdLoaded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
              Log.d(TAG, "onAdFailedToLoad: " + loadAdError.getMessage());

              // Handle the error.
              rewardedInterstitialAd = null;
              isLoadingAds = false;
              Toast.makeText(MainActivity.this, "onAdFailedToLoad", Toast.LENGTH_SHORT).show();
            }
          });
    }
  }

  private void addCoins(int coins) {
    coinCount += coins;
    coinCountText.setText("Coins: " + coinCount);
  }

  private void startGame() {
    // Hide the retry button, load the ad, and start the timer.
    retryButton.setVisibility(View.INVISIBLE);
    createTimer(COUNTER_TIME);
    gamePaused = false;
    gameOver = false;
  }

  // Create the game timer, which counts down to the end of the level
  // and shows the "retry" button.
  private void createTimer(long time) {
    final TextView textView = findViewById(R.id.timer);
    if (countDownTimer != null) {
      countDownTimer.cancel();
    }
    countDownTimer =
        new CountDownTimer(time * 1000, 50) {
          @Override
          public void onTick(long millisUnitFinished) {
            timeRemaining = ((millisUnitFinished / 1000) + 1);
            textView.setText("seconds remaining: " + timeRemaining);
          }

          @Override
          public void onFinish() {
            textView.setText("You Lose!");
            addCoins(GAME_OVER_REWARD);
            retryButton.setVisibility(View.VISIBLE);
            gameOver = true;

            if (rewardedInterstitialAd == null) {
              Log.d(TAG, "The rewarded interstitial ad is not ready.");
              return;
            }

            RewardItem rewardItem = rewardedInterstitialAd.getRewardItem();
            int rewardAmount = rewardItem.getAmount();
            String rewardType = rewardItem.getType();

            Log.d(TAG, "The rewarded interstitial ad is ready.");
            introduceVideoAd(rewardAmount, rewardType);
          }
        };
    countDownTimer.start();
  }

  private void introduceVideoAd(int rewardAmount, String rewardType) {
    AdDialogFragment dialog = AdDialogFragment.newInstance(rewardAmount, rewardType);
    dialog.setAdDialogInteractionListener(
        new AdDialogInteractionListener() {
          @Override
          public void onShowAd() {
            Log.d(TAG, "The rewarded interstitial ad is starting.");

            showRewardedVideo();
          }

          @Override
          public void onCancelAd() {
            Log.d(TAG, "The rewarded interstitial ad was skipped before it starts.");
          }
        });
    dialog.show(getSupportFragmentManager(), "AdDialogFragment");
  }

  private void showRewardedVideo() {

    if (rewardedInterstitialAd == null) {
      Log.d(TAG, "The rewarded interstitial ad wasn't ready yet.");
      return;
    }

    rewardedInterstitialAd.setFullScreenContentCallback(
        new FullScreenContentCallback() {
          /** Called when ad showed the full screen content. */
          @Override
          public void onAdShowedFullScreenContent() {
            Log.d(TAG, "onAdShowedFullScreenContent");

            Toast.makeText(MainActivity.this, "onAdShowedFullScreenContent", Toast.LENGTH_SHORT)
                .show();
          }

          /** Called when the ad failed to show full screen content. */
          @Override
          public void onAdFailedToShowFullScreenContent(AdError adError) {
            Log.d(TAG, "onAdFailedToShowFullScreenContent: " + adError.getMessage());

            // Don't forget to set the ad reference to null so you
            // don't show the ad a second time.
            rewardedInterstitialAd = null;
            if (googleMobileAdsConsentManager.canRequestAds()) {
              loadRewardedInterstitialAd();
            }

            Toast.makeText(
                    MainActivity.this, "onAdFailedToShowFullScreenContent", Toast.LENGTH_SHORT)
                .show();
          }

          /** Called when full screen content is dismissed. */
          @Override
          public void onAdDismissedFullScreenContent() {
            // Don't forget to set the ad reference to null so you
            // don't show the ad a second time.
            rewardedInterstitialAd = null;
            Log.d(TAG, "onAdDismissedFullScreenContent");
            Toast.makeText(MainActivity.this, "onAdDismissedFullScreenContent", Toast.LENGTH_SHORT)
                .show();
            if (googleMobileAdsConsentManager.canRequestAds()) {
              // Preload the next rewarded interstitial ad.
              loadRewardedInterstitialAd();
            }
          }
        });

    Activity activityContext = MainActivity.this;
    rewardedInterstitialAd.show(
        activityContext,
        new OnUserEarnedRewardListener() {
          @Override
          public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
            // Handle the reward.
            Log.d(TAG, "The user earned the reward.");
            addCoins(rewardItem.getAmount());
          }
        });
  }

  private void initializeMobileAdsSdk() {
    if (isMobileAdsInitializeCalled.getAndSet(true)) {
      return;
    }

    // Initialize the Mobile Ads SDK.
    MobileAds.initialize(
        this,
        new OnInitializationCompleteListener() {
          @Override
          public void onInitializationComplete(InitializationStatus initializationStatus) {
            // Load an ad.
            loadRewardedInterstitialAd();
          }
        });
  }
}
