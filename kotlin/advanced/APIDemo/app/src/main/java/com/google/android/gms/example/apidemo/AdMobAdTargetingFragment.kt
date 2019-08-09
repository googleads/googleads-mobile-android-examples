package com.google.android.gms.example.apidemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.RequestConfiguration.*
import kotlinx.android.synthetic.main.fragment_admob_ad_targeting.*

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

    targeting_rg_child.setOnCheckedChangeListener { _, _ ->
      var setting = RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_UNSPECIFIED

      if (targeting_rb_unspecified.isChecked) {
        // Default setting.
      } else if (targeting_rb_child.isChecked) {
        setting = RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_TRUE
      } else if (targeting_rb_notchild.isChecked) {
        setting = RequestConfiguration.TAG_FOR_CHILD_DIRECTED_TREATMENT_FALSE
      }
      val requestConfiguration = MobileAds.getRequestConfiguration().toBuilder()
              .setTagForChildDirectedTreatment(setting)
              .build()
      MobileAds.setRequestConfiguration(requestConfiguration)
    }

    targeting_rg_uac.setOnCheckedChangeListener { _, _ ->
      var setting = RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_UNSPECIFIED
      if (targeting_rb_uac_unspecified.isChecked) {
        // Default setting.
      } else if (targeting_rb_uac.isChecked) {
        setting = RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_TRUE
      } else if (targeting_rb_notuac.isChecked) {
        setting = RequestConfiguration.TAG_FOR_UNDER_AGE_OF_CONSENT_FALSE
      }
      val requestConfiguration = MobileAds.getRequestConfiguration().toBuilder()
              .setTagForUnderAgeOfConsent(setting)
              .build()
      MobileAds.setRequestConfiguration(requestConfiguration)
    }
    val ratingsArray = resources.getStringArray(R.array.targeting_ratings)

    val adapter = ArrayAdapter<CharSequence>(
        this.context!!,
        android.R.layout.simple_spinner_dropdown_item, ratingsArray
    )
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    targeting_content_rating_spinner.adapter = adapter

    targeting_content_rating_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
      override fun onItemSelected(unusedView1: AdapterView<*>, unusedView2: View, position: Int, unusedId: Long) {
        val rating = when (ratingsArray[position]) {
          "G" -> MAX_AD_CONTENT_RATING_G
          "PG" -> MAX_AD_CONTENT_RATING_PG
          "T" -> MAX_AD_CONTENT_RATING_T
          "MA" -> MAX_AD_CONTENT_RATING_MA
          else -> MAX_AD_CONTENT_RATING_G
        }
        val requestConfiguration = MobileAds.getRequestConfiguration().toBuilder()
                .setMaxAdContentRating(rating).build();
        MobileAds.setRequestConfiguration(requestConfiguration);
      }

      override fun onNothingSelected(unused: AdapterView<*>) {
        return
      }
    }

    targeting_btn_loadad.setOnClickListener {
      val request = AdRequest.Builder().build()
      targeting_ad_view.loadAd(request)
    }
  }

}
