package com.google.android.gms.example.apidemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.example.apidemo.databinding.FragmentGamFluidSizeBinding

/** The [AdManagerFluidSizeFragment] demonstrates the use of the `AdSize.FLUID` ad size. */
class AdManagerFluidSizeFragment : Fragment() {

  private lateinit var fragmentBinding: FragmentGamFluidSizeBinding

  private val mAdViewWidths = intArrayOf(200, 250, 320)
  private var mCurrentIndex = 0

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View? {
    fragmentBinding = FragmentGamFluidSizeBinding.inflate(inflater)
    return fragmentBinding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    // The size for this PublisherAdView is defined in the XML layout as AdSize.FLUID. It could
    // also be set here by calling mPublisherAdView.setAdSizes(AdSize.FLUID).
    //
    // An ad with fluid size will automatically stretch or shrink to fit the height of its
    // content, which can help layout designers cut down on excess whitespace.

    val publisherAdRequest = AdManagerAdRequest.Builder().build()
    fragmentBinding.fluidAvMain.loadAd(publisherAdRequest)

    fragmentBinding.fluidBtnChangeWidth.setOnClickListener {
      val newWidth = mAdViewWidths[mCurrentIndex % mAdViewWidths.size]
      mCurrentIndex += 1

      // Change the PublisherAdView's width.
      val layoutParams = fragmentBinding.fluidAvMain.layoutParams
      val scale = resources.displayMetrics.density
      layoutParams.width = (newWidth * scale + 0.5f).toInt()
      fragmentBinding.fluidAvMain.layoutParams = layoutParams

      // Update the TextView with the new width.
      fragmentBinding.fluidTvCurrentWidth.text = "$newWidth dp"
    }
  }
}
