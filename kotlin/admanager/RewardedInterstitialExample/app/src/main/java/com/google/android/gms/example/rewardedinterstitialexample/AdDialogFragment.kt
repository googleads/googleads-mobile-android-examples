package com.google.android.gms.example.rewardedinterstitialexample

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import java.util.concurrent.TimeUnit

/** Number of milliseconds to count down before showing ads. */
private const val AD_COUNTER_TIME_IN_MILLISECONDS = 5000L

/** A string that represents this class in the logcat. */
private const val AD_DIALOG_TAG = "AdDialogFragment"

/**
 * A dialog fragment to inform the users about an upcoming interstitial video ad and let the user
 * click the cancel button to skip the ad. This fragment inflates the dialog_ad.xml layout.
 */
class AdDialogFragment : DialogFragment() {

  /** Delivers the events to the Main Activity when the user interacts with this dialog. */
  private var listener: AdDialogInteractionListener? = null

  /** A timer for counting down until showing ads. */
  private var countDownTimer: CountDownTimer? = null

  /** Number of remaining seconds while the count down timer runs. */
  private var timeRemaining: Long = 0

  /**
   * Registers the callbacks to be invoked when the user interacts with this dialog. If there is no
   * user interactions, the dialog is dismissed and the user will see a video interstitial ad.
   *
   * @param listener The callbacks that will run when the user interacts with this dialog.
   */
  fun setAdDialogInteractionListener(listener: AdDialogInteractionListener) {
    this.listener = listener
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val view: View = requireActivity().layoutInflater.inflate(R.layout.dialog_ad, null)

    // Inflate and set the layout for the dialog
    // Pass null as the parent view because its going in the dialog layout
    val builder: AlertDialog.Builder = AlertDialog.Builder(this.activity!!)
    builder.setView(view)

    val args = arguments
    var rewardAmount = -1
    if (args != null) {
      rewardAmount = args.getInt(REWARD_AMOUNT)
    }
    if (rewardAmount > 0) {
      builder.setTitle(
        resources.getQuantityString(R.plurals.more_coins_text, rewardAmount, rewardAmount)
      )
    }

    builder.setNegativeButton(getString(R.string.negative_button_text)) { _, _ -> dialog?.cancel() }
    val dialog: Dialog = builder.create()
    createTimer(AD_COUNTER_TIME_IN_MILLISECONDS, view)
    return dialog
  }

  /**
   * Creates the a timer to count down until the rewarded interstitial ad.
   *
   * @param time Number of milliseconds to count down.
   * @param dialogView The view of this dialog for updating the remaining seconds count.
   */
  private fun createTimer(time: Long, dialogView: View) {
    val textView: TextView = dialogView.findViewById(R.id.timer)
    countDownTimer =
      object : CountDownTimer(time, 50) {
        override fun onTick(millisUnitFinished: Long) {
          timeRemaining = TimeUnit.MILLISECONDS.toSeconds(millisUnitFinished) + 1
          textView.text = String.format(getString(R.string.video_starting_in_text), timeRemaining)
        }

        // Called when the count down finishes and the user hasn't cancelled the dialog.
        override fun onFinish() {
          dialog?.dismiss()

          if (listener != null) {
            Log.d(AD_DIALOG_TAG, "onFinish: Calling onShowAd().")
            listener?.onShowAd()
          }
        }
      }
    countDownTimer?.start()
  }

  /** Called when the user clicks the "No, Thanks" button or press the back button. */
  override fun onCancel(dialog: DialogInterface) {
    super.onCancel(dialog)

    if (listener != null) {
      Log.d(AD_DIALOG_TAG, "onCancel: Calling onCancelAd().")
      listener?.onCancelAd()
    }
  }

  /** Called when the fragment is destroyed. */
  override fun onDestroy() {
    super.onDestroy()
    Log.d(AD_DIALOG_TAG, "onDestroy: Cancelling the timer.")
    countDownTimer?.cancel()
    countDownTimer = null
  }

  /** Callbacks when the user interacts with this dialog. */
  interface AdDialogInteractionListener {
    /** Called when the timer finishes without user's cancellation. */
    fun onShowAd()

    /** Called when the user clicks the "No, thanks" button or press the back button. */
    fun onCancelAd()
  }

  companion object {
    private const val REWARD_AMOUNT = "rewardAmount"
    private const val REWARD_TYPE = "rewardType"

    /**
     * Creates an instance of the AdDialogFragment and sets reward information for its title.
     *
     * @param rewardAmount Number of coins rewarded by watching an ad.
     * @param rewardType The unit of the reward amount. For example: coins, tokens, life, etc.
     */
    fun newInstance(rewardAmount: Int, rewardType: String): AdDialogFragment {
      val args = Bundle()
      args.putInt(REWARD_AMOUNT, rewardAmount)
      args.putString(REWARD_TYPE, rewardType)
      val fragment = AdDialogFragment()
      fragment.arguments = args
      return fragment
    }
  }
}
