package com.google.android.gms.example.apidemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.doubleclick.PublisherAdRequest
import kotlinx.android.synthetic.main.fragment_dfp_category_exclusion.*

/**
 * The [DFPCategoryExclusionFragment] class demonstrates how to use category exclusions with
 * DFP requests
 */
class DFPCategoryExclusionFragment : Fragment() {

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_dfp_category_exclusion, container, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    val noExclusionsRequest = PublisherAdRequest.Builder().build()
    val dogsExcludedRequest = PublisherAdRequest.Builder()
      .addCategoryExclusion(getString(R.string.categoryexclusion_dogscategoryname))
      .build()
    val catsExcludedRequest = PublisherAdRequest.Builder()
      .addCategoryExclusion(getString(R.string.categoryexclusion_catscategoryname))
      .build()

    exclusions_av_none.loadAd(noExclusionsRequest)
    exclusions_av_dogsexcluded.loadAd(dogsExcludedRequest)
    exclusions_av_catsexcluded.loadAd(catsExcludedRequest)
  }
}
