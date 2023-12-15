package com.google.ads.rewardedinterstitialexample;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

/**
 * A dialog fragment to inform the users about an upcoming interstitial video ad and let the user
 * click the cancel button to skip the ad. This fragment inflates the dialog_ad.xml layout.
 */
public class AdDialogFragment extends DialogFragment {

  /** Bundle argument's name for number of coins rewarded by watching an ad. */
  public static final String REWARD_AMOUNT = "rewardAmount";

  /** Bundle argument's name for the unit of the reward amount. */
  public static final String REWARD_TYPE = "rewardType";

  /** Number of milliseconds to count down before showing ads. */
  private static final long COUNTER_TIME_IN_MILLISECONDS = 5000;

  /** A string that represents this class in the logcat. */
  private static final String TAG = "AdDialogFragment";

  /** A timer for counting down until showing ads. */
  private CountDownTimer countDownTimer;

  /** Number of remaining seconds while the count down timer runs. */
  private long timeRemaining;

  /** Delivers the events to the Main Activity when the user interacts with this dialog. */
  private AdDialogInteractionListener listener;

  /**
   * Creates an instance of the AdDialogFragment and sets reward information for its title.
   *
   * @param rewardAmount Number of coins rewarded by watching an ad.
   * @param rewardType The unit of the reward amount. For example: coins, tokens, life, etc.
   */
  public static AdDialogFragment newInstance(int rewardAmount, String rewardType) {
    AdDialogFragment fragment = new AdDialogFragment();
    Bundle args = new Bundle();
    args.putInt(REWARD_AMOUNT, rewardAmount);
    args.putString(REWARD_TYPE, rewardType);
    fragment.setArguments(args);
    return fragment;
  }

  /**
   * Registers the callbacks to be invoked when the user interacts with this dialog. If there is no
   * user interactions, the dialog is dismissed and the user will see a video interstitial ad.
   *
   * @param listener The callbacks that will run when the user interacts with this dialog.
   */
  public void setAdDialogInteractionListener(AdDialogInteractionListener listener) {
    this.listener = listener;
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    // Inflate and set the layout for the dialog.
    // Pass null as the parent view because its going in the dialog layout.
    View view = requireActivity().getLayoutInflater().inflate(R.layout.dialog_ad, null);
    builder.setView(view);

    Bundle args = getArguments();
    int rewardAmount = -1;
    if (args != null) {
      rewardAmount = args.getInt(REWARD_AMOUNT);
    }
    if (rewardAmount > 0) {
      builder.setTitle(
          getResources().getQuantityString(
              R.plurals.more_coins_text, rewardAmount, rewardAmount));
    }

    builder.setNegativeButton(
        getString(R.string.negative_button_text),
        new DialogInterface.OnClickListener() {
          @Override
          public void onClick(DialogInterface dialog, int id) {
                getDialog().cancel();
              }
        });
    Dialog dialog = builder.create();
    createTimer(COUNTER_TIME_IN_MILLISECONDS, view);
    return dialog;
  }

  /**
   * Creates the a timer to count down until the rewarded interstitial ad.
   *
   * @param time Number of milliseconds to count down.
   * @param dialogView The view of this dialog for updating the remaining seconds count.
   */
  private void createTimer(long time, View dialogView) {
    final TextView textView = dialogView.findViewById(R.id.timer);
    countDownTimer =
        new CountDownTimer(time, 50) {
          @Override
          public void onTick(long millisUnitFinished) {
            timeRemaining = MILLISECONDS.toSeconds(millisUnitFinished) + 1;
            textView.setText(
                String.format(getString(R.string.video_starting_in_text), timeRemaining));
          }

          /** Called when the count down finishes and the user hasn't cancelled the dialog. */
          @Override
          public void onFinish() {
            getDialog().dismiss();

            if (listener != null) {
              Log.d(TAG, "onFinish: Calling onShowAd().");
              listener.onShowAd();
            }
          }
        };
    countDownTimer.start();
  }

  /** Called when the user clicks the "No, Thanks" button or press the back button. */
  @Override
  public void onCancel(@NonNull DialogInterface dialog) {
    super.onCancel(dialog);
    if (listener != null) {
      Log.d(TAG, "onCancel: Calling onCancelAd().");
      listener.onCancelAd();
    }
  }

  /** Called when the fragment is destroyed. */
  @Override
  public void onDestroy() {
    super.onDestroy();
    Log.d(TAG, "onDestroy: Cancelling the timer.");
    countDownTimer.cancel();
    countDownTimer = null;
  }

  /** Callbacks when the user interacts with this dialog. */
  public interface AdDialogInteractionListener {

    /** Called when the timer finishes without user's cancellation. */
    void onShowAd();

    /** Called when the user clicks the "No, thanks" button or press the back button. */
    void onCancelAd();
  }
}
