package com.google.android.gms.example.apidemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
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
        fragmentBinding.targetingRbTfcdUnspecified.isChecked -> {
          builder.setTagForChildDirectedTreatment(
            RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_UNSPECIFIED
          )
        }
        fragmentBinding.targetingRbTfcdYes.isChecked -> {
          builder.setTagForChildDirectedTreatment(
            RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE
          )
        }
        fragmentBinding.targetingRbTfcdNo.isChecked -> {
          builder.setTagForChildDirectedTreatment(
            RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_FALSE
          )
        }
      }

      when {
        fragmentBinding.targetingRbTfuaUnspecified.isChecked -> {
          builder.setTagForUnderAgeOfConsent(
            RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_UNSPECIFIED
          )
        }
        fragmentBinding.targetingRbTfuaYes.isChecked -> {
          builder.setTagForUnderAgeOfConsent(RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_TRUE)
        }
        fragmentBinding.targetingRbTfuaNo.isChecked -> {
          builder.setTagForUnderAgeOfConsent(
            RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_FALSE
          )
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
