package com.google.ads.rewardedinterstitialexample;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.fragment.app.DialogFragment;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/** Ad Dialog. Inflates dialog_ad.xml. */
public class AdDialogFragment extends DialogFragment {

  private static final long COUNTER_TIME = 5;
  private AdDialogInteractionListener listener;
  private CountDownTimer countDownTimer;
  private long timeRemaining;

  public void setAdDialogInteractionListener(AdDialogInteractionListener listener) {
    this.listener = listener;
  }

  @Override
  public void onDismiss(@NonNull DialogInterface dialog) {
    super.onDismiss(dialog);
    if (listener != null) {
      listener.onShowAd();
    }
  }

  @Override
  public void onCancel(@NonNull DialogInterface dialog) {
    super.onCancel(dialog);
    if (listener != null) {
      listener.onCancelAd();
    }
  }

  @NonNull
  @Override
  public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
    LayoutInflater inflater = requireActivity().getLayoutInflater();

    // Inflate and set the layout for the dialog
    // Pass null as the parent view because its going in the dialog layout
    View view = inflater.inflate(R.layout.dialog_ad, null);
    builder
        .setView(view)
        .setNegativeButton(
            "No, thanks",
            new DialogInterface.OnClickListener() {
              public void onClick(DialogInterface dialog, int id) {
                getDialog().cancel();
              }
            });
    Dialog dialog = builder.create();
    createTimer(COUNTER_TIME, view);
    return dialog;
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    countDownTimer.cancel();
    countDownTimer = null;
  }

  // Create the a timer to count down until the rewarded interstitial ad.
  private void createTimer(long time, View dialogView) {
    final TextView textView = dialogView.findViewById(R.id.timer);
    if (countDownTimer != null) {
      countDownTimer.cancel();
    }
    countDownTimer =
        new CountDownTimer(time * 1000, 50) {
          @Override
          public void onTick(long millisUnitFinished) {
            timeRemaining = ((millisUnitFinished / 1000) + 1);
            textView.setText(
                String.format(getString(R.string.video_starting_in_text), timeRemaining));
          }

          @Override
          public void onFinish() {
            getDialog().dismiss();
          }
        };
    countDownTimer.start();
  }

  /** Callbacks when the user interactions with this dialog. */
  public interface AdDialogInteractionListener {

    /** Called when the timer finishes without user's cancellation. */
    void onShowAd();

    /** Called when the user clicks the "No, thanks" button or press the back button. */
    void onCancelAd();
  }
}
