package com.google.android.gms.example.apidemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import kotlinx.android.synthetic.main.fragment_gam_category_exclusion.exclusions_av_catsexcluded
import kotlinx.android.synthetic.main.fragment_gam_category_exclusion.exclusions_av_dogsexcluded
import kotlinx.android.synthetic.main.fragment_gam_category_exclusion.exclusions_av_none

/**
 * The [AdManagerCategoryExclusionFragment] class demonstrates how to use category exclusions with
 * Ad Manager requests
 */
class AdManagerCategoryExclusionFragment : Fragment() {

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_gam_category_exclusion, container, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    val noExclusionsRequest = AdManagerAdRequest.Builder().build()
    val dogsExcludedRequest = AdManagerAdRequest.Builder()
      .addCategoryExclusion(getString(R.string.categoryexclusion_dogscategoryname))
      .build()
    val catsExcludedRequest = AdManagerAdRequest.Builder()
      .addCategoryExclusion(getString(R.string.categoryexclusion_catscategoryname))
      .build()

    exclusions_av_none.loadAd(noExclusionsRequest)
    exclusions_av_dogsexcluded.loadAd(dogsExcludedRequest)
    exclusions_av_catsexcluded.loadAd(catsExcludedRequest)
  }
}
