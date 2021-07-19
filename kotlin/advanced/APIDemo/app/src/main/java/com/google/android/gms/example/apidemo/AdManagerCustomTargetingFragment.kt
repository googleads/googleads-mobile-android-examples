package com.google.android.gms.example.apidemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import kotlinx.android.synthetic.main.fragment_gam_custom_targeting.customtargeting_av_main
import kotlinx.android.synthetic.main.fragment_gam_custom_targeting.customtargeting_btn_loadad
import kotlinx.android.synthetic.main.fragment_gam_custom_targeting.customtargeting_spn_sport

/**
 * the [AdManagerCustomTargetingFragment] class demonstrates how to add custom targeting
 * information to a request.
 */
class AdManagerCustomTargetingFragment : Fragment() {

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_gam_custom_targeting, container, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    val adapter = ArrayAdapter.createFromResource(
      requireView().context,
      R.array.customtargeting_sports, android.R.layout.simple_spinner_item
    )
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    customtargeting_spn_sport.adapter = adapter

    customtargeting_btn_loadad.setOnClickListener {
      val adRequest = AdManagerAdRequest.Builder()
        .addCustomTargeting(
          getString(R.string.customtargeting_key),
          customtargeting_spn_sport.selectedItem as String
        )
        .build()

      customtargeting_av_main.loadAd(adRequest)
    }
  }
}
