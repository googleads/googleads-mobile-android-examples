package com.google.android.gms.example.apidemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import kotlinx.android.synthetic.main.fragment_admob_ad_targeting.targeting_ad_view
import kotlinx.android.synthetic.main.fragment_admob_ad_targeting.targeting_btn_loadad
import kotlinx.android.synthetic.main.fragment_admob_ad_targeting.targeting_rb_rating_g
import kotlinx.android.synthetic.main.fragment_admob_ad_targeting.targeting_rb_rating_ma
import kotlinx.android.synthetic.main.fragment_admob_ad_targeting.targeting_rb_rating_pg
import kotlinx.android.synthetic.main.fragment_admob_ad_targeting.targeting_rb_rating_t
import kotlinx.android.synthetic.main.fragment_admob_ad_targeting.targeting_rb_tfcd_no
import kotlinx.android.synthetic.main.fragment_admob_ad_targeting.targeting_rb_tfcd_unspecified
import kotlinx.android.synthetic.main.fragment_admob_ad_targeting.targeting_rb_tfcd_yes
import kotlinx.android.synthetic.main.fragment_admob_ad_targeting.targeting_rb_tfua_no
import kotlinx.android.synthetic.main.fragment_admob_ad_targeting.targeting_rb_tfua_unspecified
import kotlinx.android.synthetic.main.fragment_admob_ad_targeting.targeting_rb_tfua_yes

/**
 * The [AdMobAdTargetingFragment] class demonstrates how to use ad targeting with AdMob.
 */
class AdMobAdTargetingFragment : Fragment() {

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_admob_ad_targeting, container, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    targeting_btn_loadad.setOnClickListener {
      val builder = MobileAds.getRequestConfiguration().toBuilder()

      when {
        targeting_rb_tfcd_unspecified.isChecked -> {
          builder.setTagForChildDirectedTreatment(
            RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_UNSPECIFIED)
        }
        targeting_rb_tfcd_yes.isChecked -> {
          builder.setTagForChildDirectedTreatment(
            RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE)
        }
        targeting_rb_tfcd_no.isChecked -> {
          builder.setTagForChildDirectedTreatment(
            RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_FALSE)
        }
      }

      when {
        targeting_rb_tfua_unspecified.isChecked -> {
          builder.setTagForUnderAgeOfConsent(
            RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_UNSPECIFIED)
        }
        targeting_rb_tfua_yes.isChecked -> {
          builder.setTagForUnderAgeOfConsent(
            RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_TRUE)
        }
        targeting_rb_tfua_no.isChecked -> {
          builder.setTagForUnderAgeOfConsent(
            RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_FALSE)
        }
      }

      when {
        targeting_rb_rating_g.isChecked -> {
          builder.setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_G)
        }
        targeting_rb_rating_pg.isChecked -> {
          builder.setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_PG)
        }
        targeting_rb_rating_t.isChecked -> {
          builder.setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_T)
        }
        targeting_rb_rating_ma.isChecked -> {
          builder.setMaxAdContentRating(RequestConfiguration.MAX_AD_CONTENT_RATING_MA)
        }
      }

      // Update the request configuration.
      MobileAds.setRequestConfiguration(builder.build())

      // Load an ad.
      targeting_ad_view.loadAd(AdRequest.Builder().build())
    }
  }
}
