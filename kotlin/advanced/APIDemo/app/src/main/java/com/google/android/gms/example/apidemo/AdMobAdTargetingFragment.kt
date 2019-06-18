package com.google.android.gms.example.apidemo

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.android.synthetic.main.fragment_admob_ad_targeting.*

/**
 * The [AdMobAdTargetingFragment] class demonstrates how to use ad targeting with AdMob.
 */
class AdMobAdTargetingFragment : Fragment(), DatePickerDialog.OnDateSetListener {

  private val mDateFormat = SimpleDateFormat("M/d/yyyy", Locale.getDefault())

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_admob_ad_targeting, container, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    targeting_btn_datepick.setOnClickListener {
      val newFragment = DatePickerFragment()
      newFragment.setOnDateSetListener(this)
      newFragment.show(activity!!.supportFragmentManager, "datePicker")
    }

    targeting_btn_loadad.setOnClickListener {
      val builder = AdRequest.Builder()

      try {
        val birthdayString = targeting_et_birthday.text.toString()
        val birthday = mDateFormat.parse(birthdayString)
        builder.setBirthday(birthday)
      } catch (ex: ParseException) {
        Log.d(MainActivity.LOG_TAG, "Failed to parse birthday")
      }

      if (targeting_rb_male.isChecked) {
        builder.setGender(AdRequest.GENDER_MALE)
      } else if (targeting_rb_female.isChecked) {
        builder.setGender(AdRequest.GENDER_FEMALE)
      }

      if (targeting_rb_unspecified.isChecked) {
        // There's actually nothing to be done here. If you're unsure whether or not
        // the user should receive child-directed treatment, simply avoid calling the
        // tagForChildDirectedTreatment method. The ad request will not contain any
        // indication one way or the other.
      } else if (targeting_rb_child.isChecked) {
        builder.tagForChildDirectedTreatment(true)
      } else if (targeting_rb_notchild.isChecked) {
        builder.tagForChildDirectedTreatment(false)
      }

      val request = builder.build()
      targeting_ad_view.loadAd(request)
    }
  }

  override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
    val calendar = Calendar.getInstance()
    calendar.set(year, monthOfYear, dayOfMonth)
    targeting_et_birthday.setText(mDateFormat.format(calendar.time))
  }
}
