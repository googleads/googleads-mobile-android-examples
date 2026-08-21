package com.google.android.gms.example.apidemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AgeRestrictedTreatment
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.example.apidemo.databinding.FragmentAdmobAdTargetingBinding

/** The [AdMobAdTargetingFragment] class demonstrates how to use ad targeting with AdMob. */
class AdMobAdTargetingFragment : Fragment() {

  private lateinit var fragmentBinding: FragmentAdmobAdTargetingBinding

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View? {
    fragmentBinding = FragmentAdmobAdTargetingBinding.inflate(inflater)
    return fragmentBinding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    fragmentBinding.targetingBtnLoadad.setOnClickListener {
      val builder = MobileAds.getRequestConfiguration().toBuilder()

      when {
        fragmentBinding.targetingRbChild.isChecked -> {
          builder.setAgeRestrictedTreatment(AgeRestrictedTreatment.CHILD)
        }
        fragmentBinding.targetingRbTeen.isChecked -> {
          builder.setAgeRestrictedTreatment(AgeRestrictedTreatment.TEEN)
        }
        fragmentBinding.targetingRbUnspecified.isChecked -> {
          builder.setAgeRestrictedTreatment(AgeRestrictedTreatment.UNSPECIFIED)
        }
      }

      when {
        fragmentBinding.targetingRbRatingG.isChecked -> {
          builder.setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G)
        }
        fragmentBinding.targetingRbRatingPg.isChecked -> {
          builder.setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_PG)
        }
        fragmentBinding.targetingRbRatingT.isChecked -> {
          builder.setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_T)
        }
        fragmentBinding.targetingRbRatingMa.isChecked -> {
          builder.setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_MA)
        }
      }

      // Update the request configuration.
      MobileAds.setRequestConfiguration(builder.build())

      // Load an ad.
      fragmentBinding.targetingAdView.loadAd(AdRequest.Builder().build())
    }
  }
}
