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
import kotlinx.android.synthetic.main.fragment_admob_banner_sizes.bannersizes_btn_loadad
import kotlinx.android.synthetic.main.fragment_admob_banner_sizes.bannersizes_fl_adframe
import kotlinx.android.synthetic.main.fragment_admob_banner_sizes.bannersizes_spn_size

/**
 * The [AdMobBannerSizesFragment] class demonstrates how to set a desired banner size prior to
 * loading an ad.
 */

class AdMobBannerSizesFragment : Fragment() {

  private var mAdView: AdView? = null

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_admob_banner_sizes, container, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    val sizesArray: Array<String>

    // It's a Mobile Ads SDK policy that only the banner, large banner, and smart banner ad
    // sizes are shown on phones, and that the full banner, leaderboard, and medium rectangle
    // sizes are reserved for use on tablets.  The conditional below checks the screen size
    // and retrieves the correct list.

    val screenSize = (
      resources.configuration.screenLayout
        and Configuration.SCREENLAYOUT_SIZE_MASK
      )

    if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE ||
      screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE
    ) {
      sizesArray = resources.getStringArray(R.array.bannersizes_largesizes)
    } else {
      sizesArray = resources.getStringArray(R.array.bannersizes_smallsizes)
    }

    val adapter = ArrayAdapter<CharSequence>(
      this.requireContext(),
      android.R.layout.simple_spinner_dropdown_item, sizesArray
    )
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    bannersizes_spn_size.adapter = adapter

    bannersizes_btn_loadad.setOnClickListener {
      mAdView?.apply {
        bannersizes_fl_adframe.removeView(mAdView)
        mAdView?.destroy()
      }

      mAdView = AdView(activity)
      mAdView?.adUnitId = getString(R.string.admob_banner_ad_unit_id)
      bannersizes_fl_adframe.addView(mAdView)

      mAdView?.adSize = when (bannersizes_spn_size.selectedItemPosition) {
        0 -> AdSize.BANNER
        1 -> AdSize.LARGE_BANNER
        2 -> AdSize.SMART_BANNER
        3 -> AdSize.FULL_BANNER
        4 -> AdSize.MEDIUM_RECTANGLE
        else -> AdSize.LEADERBOARD
      }

      mAdView?.loadAd(AdRequest.Builder().build())
    }
  }
}
