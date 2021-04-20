package com.google.ads.rewardedinterstitialexample;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
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

/** Main Activity. Inflates main activity xml. */
public class MainActivity extends FragmentActivity {
  private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/5354046379";
  private static final long COUNTER_TIME = 3;
  private static final int GAME_OVER_REWARD = 1;
  private static final int AD_REWARD = 10;
  private static final String TAG = "MyActivity";

  private int coinCount;
  private TextView coinCountText;
  private CountDownTimer countDownTimer;
  private boolean gameOver;
  private boolean gamePaused;

  private RewardedInterstitialAd rewardedInterstitialAd;
  private Button retryButton;
  private long timeRemaining;
  boolean isLoadingAds;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    MobileAds.initialize(
        this,
        new OnInitializationCompleteListener() {
          @Override
          public void onInitializationComplete(InitializationStatus initializationStatus) {
            loadRewardedAd();
          }
        });

    // Create the "retry" button, which tries to show a rewarded ad between game plays.
    retryButton = findViewById(R.id.retry_button);
    retryButton.setVisibility(View.INVISIBLE);
    retryButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            startGame();
          }
        });

    // Display current coin count to user.
    coinCountText = findViewById(R.id.coin_count_text);
    coinCount = 0;
    coinCountText.setText("Coins: " + coinCount);

    startGame();
  }

  @Override
  public void onPause() {
    super.onPause();
    pauseGame();
  }

  @Override
  public void onResume() {
    super.onResume();
    if (!gameOver && gamePaused) {
      resumeGame();
    }
  }

  private void pauseGame() {
    countDownTimer.cancel();
    gamePaused = true;
  }

  private void resumeGame() {
    createTimer(timeRemaining);
    gamePaused = false;
  }

  private void loadRewardedAd() {
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
            public void onAdLoaded(RewardedInterstitialAd rewardedAd) {
              Log.d(TAG, "onAdLoaded");

              MainActivity.this.rewardedInterstitialAd = rewardedAd;
              MainActivity.this.isLoadingAds = false;
              Toast.makeText(MainActivity.this, "onAdLoaded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
              Log.e(TAG, "onAdFailedToLoad");
              Log.d(TAG, loadAdError.getMessage());

              // Handle the error.
              rewardedInterstitialAd = null;
              MainActivity.this.isLoadingAds = false;
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
    if (rewardedInterstitialAd != null && !isLoadingAds) {
      loadRewardedAd();
    }
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

            introduceVideoAd();
          }
        };
    countDownTimer.start();
  }

  private void introduceVideoAd() {
    AdDialogFragment dialog = new AdDialogFragment();
    dialog.show(getSupportFragmentManager(), "AdDialogFragment");
    dialog.setAdDialogInteractionListener(
        new AdDialogInteractionListener() {
          @Override
          public void onShowAd() {
            Log.d(TAG, "The rewarded ad is starting.");

            showRewardedVideo();
          }

          @Override
          public void onCancelAd() {
            Log.d(TAG, "The rewarded ad was skipped before it starts.");
          }
        });
  }

  private void showRewardedVideo() {

    if (rewardedInterstitialAd == null) {
      Log.d("TAG", "The rewarded ad wasn't ready yet.");
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
            Log.d(TAG, "onAdFailedToShowFullScreenContent");

            // Don't forget to set the ad reference to null so you
            // don't show the ad a second time.
            rewardedInterstitialAd = null;

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
            // Preload the next rewarded interstitial ad.
            MainActivity.this.loadRewardedAd();
          }
        });

    Activity activityContext = MainActivity.this;
    rewardedInterstitialAd.show(
        activityContext,
        new OnUserEarnedRewardListener() {
          @Override
          public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
            // Handle the reward.
            Log.d("TAG", "The user earned the reward.");
            // Uncomment the following lines to get the reward information returned with the ad:
            // int rewardAmount = rewardItem.getAmount();
            // String rewardType = rewardItem.getType();
            addCoins(AD_REWARD);
          }
        });
  }
}
