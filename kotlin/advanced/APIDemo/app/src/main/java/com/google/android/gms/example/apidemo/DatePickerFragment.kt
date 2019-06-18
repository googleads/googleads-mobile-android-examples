package com.google.android.gms.example.apidemo

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import java.util.*

/**
 * The [DatePickerFragment] displays a simple date picker for use in other fragments and
 * activities.
 */
class DatePickerFragment : DialogFragment() {

  private var mOnDateSetListener: DatePickerDialog.OnDateSetListener? = null

  fun setOnDateSetListener(listener: DatePickerDialog.OnDateSetListener) {
    mOnDateSetListener = listener
  }

  override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
    val calendar = Calendar.getInstance()

    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)

    return DatePickerDialog(activity!!, mOnDateSetListener, year, month, day)
  }
}
