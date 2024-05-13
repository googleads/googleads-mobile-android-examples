package com.google.android.gms.example.apidemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.example.apidemo.databinding.FragmentGamCustomTargetingBinding

/**
 * the [AdManagerCustomTargetingFragment] class demonstrates how to add custom targeting information
 * to a request.
 */
class AdManagerCustomTargetingFragment : Fragment() {

  private lateinit var fragmentBinding: FragmentGamCustomTargetingBinding

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View? {
    fragmentBinding = FragmentGamCustomTargetingBinding.inflate(inflater)
    return fragmentBinding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    val adapter =
      ArrayAdapter.createFromResource(
        requireView().context,
        R.array.customtargeting_sports,
        android.R.layout.simple_spinner_item,
      )
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    fragmentBinding.customtargetingSpnSport.adapter = adapter

    fragmentBinding.customtargetingBtnLoadad.setOnClickListener {
      val adRequest =
        AdManagerAdRequest.Builder()
          .addCustomTargeting(
            getString(R.string.customtargeting_key),
            fragmentBinding.customtargetingSpnSport.selectedItem as String,
          )
          .build()

      fragmentBinding.customtargetingAvMain.loadAd(adRequest)
    }
  }
}
