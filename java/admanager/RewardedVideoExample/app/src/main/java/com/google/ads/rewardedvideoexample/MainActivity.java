package com.google.ads.rewardedvideoexample;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

/** Main Activity. Inflates main activity xml. */
public class MainActivity extends Activity {
  private static final String AD_UNIT_ID = "/6499/example/rewarded-video";
  private static final long COUNTER_TIME = 10;
  private static final int GAME_OVER_REWARD = 1;

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
    if (rewardedAd == null || !rewardedAd.isLoaded()) {
      isLoading = true;
      rewardedAd = new RewardedAd(this, AD_UNIT_ID);

      rewardedAd.loadAd(
          new PublisherAdRequest.Builder().build(),
          new RewardedAdLoadCallback() {
            @Override
            public void onRewardedAdLoaded() {
              // Ad successfully loaded.
              MainActivity.this.isLoading = false;
              Toast.makeText(MainActivity.this, "onRewardedAdLoaded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedAdFailedToLoad(LoadAdError loadAdError) {
              // Ad failed to load.
              MainActivity.this.isLoading = false;
              Toast.makeText(MainActivity.this, "onRewardedAdFailedToLoad", Toast.LENGTH_SHORT)
                  .show();
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
    if (!rewardedAd.isLoaded() && !isLoading) {
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
            if (rewardedAd.isLoaded()) {
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
    showVideoButton.setVisibility(View.INVISIBLE);
    if (rewardedAd.isLoaded()) {
      RewardedAdCallback adCallback =
          new RewardedAdCallback() {
            @Override
            public void onRewardedAdOpened() {
              // Ad opened.
              Toast.makeText(MainActivity.this, "onRewardedAdOpened", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRewardedAdClosed() {
              // Ad closed.
              Toast.makeText(MainActivity.this, "onRewardedAdClosed", Toast.LENGTH_SHORT).show();
              // Preload the next rewarded ad.
              MainActivity.this.loadRewardedAd();
            }

            @Override
            public void onUserEarnedReward(RewardItem rewardItem) {
              // User earned reward.
              Toast.makeText(MainActivity.this, "onUserEarnedReward", Toast.LENGTH_SHORT).show();
              addCoins(rewardItem.getAmount());
            }

            @Override
            public void onRewardedAdFailedToShow(int errorCode) {
              // Ad failed to display
              Toast.makeText(MainActivity.this, "onRewardedAdFailedToShow", Toast.LENGTH_SHORT)
                  .show();
            }
          };
      rewardedAd.show(this, adCallback);
    }
  }
}
