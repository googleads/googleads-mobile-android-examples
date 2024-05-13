package com.google.android.gms.example.apidemo

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.example.apidemo.databinding.FragmentAdmobBannerSizesBinding

/**
 * The [AdMobBannerSizesFragment] class demonstrates how to set a desired banner size prior to
 * loading an ad.
 */
class AdMobBannerSizesFragment : Fragment() {

  private lateinit var fragmentBinding: FragmentAdmobBannerSizesBinding
  private var mAdView: AdView? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    fragmentBinding = FragmentAdmobBannerSizesBinding.inflate(inflater)
    return fragmentBinding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    // It's a Mobile Ads SDK policy that only the banner, large banner, and smart banner ad
    // sizes are shown on phones, and that the full banner, leaderboard, and medium rectangle
    // sizes are reserved for use on tablets.  The conditional below checks the screen size
    // and retrieves the correct list.

    val screenSize = (resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK)

    val sizesArray =
      if (
        screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE ||
          screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE
      ) {
        resources.getStringArray(R.array.bannersizes_largesizes)
      } else {
        resources.getStringArray(R.array.bannersizes_smallsizes)
      }

    val adapter =
      ArrayAdapter<CharSequence>(
        this.requireContext(),
        android.R.layout.simple_spinner_dropdown_item,
        sizesArray,
      )
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    fragmentBinding.bannersizesSpnSize.adapter = adapter

    fragmentBinding.bannersizesBtnLoadad.setOnClickListener {
      mAdView?.apply {
        fragmentBinding.bannersizesFlAdframe.removeView(mAdView)
        mAdView?.destroy()
      }

      mAdView = activity?.let { AdView(it) }
      mAdView?.adUnitId = getString(R.string.admob_banner_ad_unit_id)
      fragmentBinding.bannersizesFlAdframe.addView(mAdView)

      mAdView?.setAdSize(
        when (fragmentBinding.bannersizesSpnSize.selectedItemPosition) {
          0 -> AdSize.BANNER
          1 -> AdSize.LARGE_BANNER
          2 -> AdSize.FULL_BANNER
          3 -> AdSize.MEDIUM_RECTANGLE
          else -> AdSize.LEADERBOARD
        }
      )

      mAdView?.loadAd(AdRequest.Builder().build())
    }
  }
}
