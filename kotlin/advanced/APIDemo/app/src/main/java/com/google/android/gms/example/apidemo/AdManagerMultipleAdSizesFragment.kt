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
import com.google.android.gms.example.apidemo.databinding.FragmentGamMultipleAdSizesBinding

/**
 * The [AdManagerMultipleAdSizesFragment] class demonstrates how to set specific ad sizes for a
 * request.
 */
class AdManagerMultipleAdSizesFragment : Fragment() {

  private lateinit var fragmentBinding: FragmentGamMultipleAdSizesBinding

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View? {
    fragmentBinding = FragmentGamMultipleAdSizesBinding.inflate(inflater)
    return fragmentBinding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    fragmentBinding.adsizesPavMain.adListener =
      object : AdListener() {
        override fun onAdLoaded() {
          fragmentBinding.adsizesPavMain.visibility = View.VISIBLE
        }
      }

    fragmentBinding.adsizesBtnLoadad.setOnClickListener {
      if (
        !fragmentBinding.adsizesCb120x20.isChecked &&
          !fragmentBinding.adsizesCb320x50.isChecked &&
          !fragmentBinding.adsizesCb300x250.isChecked
      ) {
        Toast.makeText(this.activity, "At least one size is required.", Toast.LENGTH_SHORT).show()
      } else {
        val sizeList = ArrayList<AdSize>()

        if (fragmentBinding.adsizesCb120x20.isChecked) {
          sizeList.add(AdSize(120, 20))
        }

        if (fragmentBinding.adsizesCb320x50.isChecked) {
          sizeList.add(AdSize.BANNER)
        }

        if (fragmentBinding.adsizesCb300x250.isChecked) {
          sizeList.add(AdSize.MEDIUM_RECTANGLE)
        }

        fragmentBinding.adsizesPavMain.visibility = View.INVISIBLE
        fragmentBinding.adsizesPavMain.setAdSizes(*sizeList.toTypedArray())
        fragmentBinding.adsizesPavMain.loadAd(AdManagerAdRequest.Builder().build())
      }
    }
  }
}
