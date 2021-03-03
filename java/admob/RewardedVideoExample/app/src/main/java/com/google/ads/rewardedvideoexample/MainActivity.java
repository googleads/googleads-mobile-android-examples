package com.google.ads.rewardedvideoexample;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

/** Main Activity. Inflates main activity xml. */
public class MainActivity extends Activity {
  private static final String AD_UNIT_ID = "ca-app-pub-3940256099942544/5224354917";
  private static final long COUNTER_TIME = 10;
  private static final int GAME_OVER_REWARD = 1;
  private static final String TAG = "MyActivity";

  private int coinCount;
  private TextView coinCountText;
  private CountDownTimer countDownTimer;
  private boolean gameOver;
  private boolean gamePaused;

  private RewardedAd rewardedAd;
  private Button retryButton;
  private Button showVideoButton;
  private long timeRemaining;
  boolean isLoading;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    MobileAds.initialize(this, new OnInitializationCompleteListener() {
      @Override
      public void onInitializationComplete(InitializationStatus initializationStatus) {
      }
    });

    loadRewardedAd();

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

    // Create the "show" button, which shows a rewarded video if one is loaded.
    showVideoButton = findViewById(R.id.show_video_button);
    showVideoButton.setVisibility(View.INVISIBLE);
    showVideoButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            showRewardedVideo();
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
    if (rewardedAd == null) {
      isLoading = true;
      AdRequest adRequest = new AdRequest.Builder().build();
      RewardedAd.load(
          this,
          AD_UNIT_ID,
          adRequest,
          new RewardedAdLoadCallback() {
            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
              // Handle the error.
              Log.d(TAG, loadAdError.getMessage());
              rewardedAd = null;
              MainActivity.this.isLoading = false;
              Toast.makeText(MainActivity.this, "onAdFailedToLoad", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
              MainActivity.this.rewardedAd = rewardedAd;
              Log.d(TAG, "onAdLoaded");
              MainActivity.this.isLoading = false;
              Toast.makeText(MainActivity.this, "onAdLoaded", Toast.LENGTH_SHORT).show();
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
    showVideoButton.setVisibility(View.INVISIBLE);
    if (rewardedAd != null && !isLoading) {
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
            if (rewardedAd != null) {
              showVideoButton.setVisibility(View.VISIBLE);
            }
            textView.setText("You Lose!");
            addCoins(GAME_OVER_REWARD);
            retryButton.setVisibility(View.VISIBLE);
            gameOver = true;
          }
        };
    countDownTimer.start();
  }

  private void showRewardedVideo() {

    if (rewardedAd == null) {
      Log.d("TAG", "The rewarded ad wasn't ready yet.");
      return;
    }
    showVideoButton.setVisibility(View.INVISIBLE);

    rewardedAd.setFullScreenContentCallback(
        new FullScreenContentCallback() {
          @Override
          public void onAdShowedFullScreenContent() {
            // Called when ad is shown.
            Log.d(TAG, "onAdShowedFullScreenContent");
            Toast.makeText(MainActivity.this, "onAdShowedFullScreenContent", Toast.LENGTH_SHORT)
                .show();
          }

          @Override
          public void onAdFailedToShowFullScreenContent(AdError adError) {
            // Called when ad fails to show.
            Log.d(TAG, "onAdFailedToShowFullScreenContent");
            // Don't forget to set the ad reference to null so you
            // don't show the ad a second time.
            rewardedAd = null;
            Toast.makeText(
                    MainActivity.this, "onAdFailedToShowFullScreenContent", Toast.LENGTH_SHORT)
                .show();
          }

          @Override
          public void onAdDismissedFullScreenContent() {
            // Called when ad is dismissed.
            // Don't forget to set the ad reference to null so you
            // don't show the ad a second time.
            rewardedAd = null;
            Log.d(TAG, "onAdDismissedFullScreenContent");
            Toast.makeText(MainActivity.this, "onAdDismissedFullScreenContent", Toast.LENGTH_SHORT)
                .show();
            // Preload the next rewarded ad.
            MainActivity.this.loadRewardedAd();
          }
        });
    Activity activityContext = MainActivity.this;
    rewardedAd.show(
        activityContext,
        new OnUserEarnedRewardListener() {
          @Override
          public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
            // Handle the reward.
            Log.d("TAG", "The user earned the reward.");
            int rewardAmount = rewardItem.getAmount();
            String rewardType = rewardItem.getType();
          }
        });
  }
}
