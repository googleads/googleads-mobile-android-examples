package com.google.android.gms.example.apidemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.example.apidemo.databinding.FragmentGamCategoryExclusionBinding

/**
 * The [AdManagerCategoryExclusionFragment] class demonstrates how to use category exclusions with
 * Ad Manager requests
 */
class AdManagerCategoryExclusionFragment : Fragment() {

  private lateinit var fragmentBinding: FragmentGamCategoryExclusionBinding

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View? {
    fragmentBinding = FragmentGamCategoryExclusionBinding.inflate(layoutInflater)
    return fragmentBinding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    val noExclusionsRequest = AdManagerAdRequest.Builder().build()
    val dogsExcludedRequest =
      AdManagerAdRequest.Builder()
        .addCategoryExclusion(getString(R.string.categoryexclusion_dogscategoryname))
        .build()
    val catsExcludedRequest =
      AdManagerAdRequest.Builder()
        .addCategoryExclusion(getString(R.string.categoryexclusion_catscategoryname))
        .build()

    fragmentBinding.exclusionsAvNone.loadAd(noExclusionsRequest)
    fragmentBinding.exclusionsAvDogsexcluded.loadAd(dogsExcludedRequest)
    fragmentBinding.exclusionsAvCatsexcluded.loadAd(catsExcludedRequest)
  }
}
