package com.google.android.gms.example.apidemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.doubleclick.PublisherAdRequest
import kotlinx.android.synthetic.main.fragment_dfp_custom_targeting.*

/**
 * the [DFPCustomTargetingFragment] class demonstrates how to add custom targeting
 * information to a request.
 */
class DFPCustomTargetingFragment : Fragment() {

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_dfp_custom_targeting, container, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    val adapter = ArrayAdapter.createFromResource(
      view!!.context,
      R.array.customtargeting_sports, android.R.layout.simple_spinner_item
    )
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    customtargeting_spn_sport.adapter = adapter

    customtargeting_btn_loadad.setOnClickListener {
      val adRequest = PublisherAdRequest.Builder()
        .addCustomTargeting(
          getString(R.string.customtargeting_key),
          customtargeting_spn_sport.selectedItem as String
        )
        .build()

      customtargeting_av_main.loadAd(adRequest)
    }
  }
}
