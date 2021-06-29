package com.google.android.gms.example.apidemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import kotlinx.android.synthetic.main.fragment_gam_multiple_ad_sizes.adsizes_btn_loadad
import kotlinx.android.synthetic.main.fragment_gam_multiple_ad_sizes.adsizes_cb_120x20
import kotlinx.android.synthetic.main.fragment_gam_multiple_ad_sizes.adsizes_cb_300x250
import kotlinx.android.synthetic.main.fragment_gam_multiple_ad_sizes.adsizes_cb_320x50
import kotlinx.android.synthetic.main.fragment_gam_multiple_ad_sizes.adsizes_pav_main

/**
 * The [AdManagerMultipleAdSizesFragment] class demonstrates how to set specific ad sizes for a
 * request.
 */
class AdManagerMultipleAdSizesFragment : Fragment() {

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_gam_multiple_ad_sizes, container, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    adsizes_pav_main.adListener = object : AdListener() {
      override fun onAdLoaded() {
        adsizes_pav_main.visibility = View.VISIBLE
      }
    }

    adsizes_btn_loadad.setOnClickListener {
      if (!adsizes_cb_120x20.isChecked &&
        !adsizes_cb_320x50.isChecked &&
        !adsizes_cb_300x250.isChecked
      ) {
        Toast.makeText(
          this.activity, "At least one size is required.",
          Toast.LENGTH_SHORT
        ).show()
      } else {
        val sizeList = ArrayList<AdSize>()

        if (adsizes_cb_120x20.isChecked) {
          sizeList.add(AdSize(120, 20))
        }

        if (adsizes_cb_320x50.isChecked) {
          sizeList.add(AdSize.BANNER)
        }

        if (adsizes_cb_300x250.isChecked) {
          sizeList.add(AdSize.MEDIUM_RECTANGLE)
        }

        adsizes_pav_main.visibility = View.INVISIBLE
        adsizes_pav_main.setAdSizes(*sizeList.toTypedArray())
        adsizes_pav_main.loadAd(AdManagerAdRequest.Builder().build())
      }
    }
  }
}
