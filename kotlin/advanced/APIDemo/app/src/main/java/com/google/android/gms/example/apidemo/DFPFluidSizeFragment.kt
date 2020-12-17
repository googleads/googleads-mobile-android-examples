package com.google.android.gms.example.apidemo

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.doubleclick.PublisherAdRequest
import kotlinx.android.synthetic.main.fragment_dfp_fluid_size.*

/**
 * The [DFPFluidSizeFragment] demonstrates the use of the `AdSize.FLUID` ad size.
 */
class DFPFluidSizeFragment : Fragment() {

  private val mAdViewWidths = intArrayOf(200, 250, 320)
  private var mCurrentIndex = 0

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_dfp_fluid_size, container, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    // The size for this PublisherAdView is defined in the XML layout as AdSize.FLUID. It could
    // also be set here by calling mPublisherAdView.setAdSizes(AdSize.FLUID).
    //
    // An ad with fluid size will automatically stretch or shrink to fit the height of its
    // content, which can help layout designers cut down on excess whitespace.

    val publisherAdRequest = PublisherAdRequest.Builder().build()
    fluid_av_main.loadAd(publisherAdRequest)

    fluid_btn_change_width.setOnClickListener {
      val newWidth = mAdViewWidths[mCurrentIndex % mAdViewWidths.size]
      mCurrentIndex += 1

      // Change the PublisherAdView's width.
      val layoutParams = fluid_av_main.layoutParams
      val scale = resources.configuration.densityDpi / DisplayMetrics.DENSITY_DEFAULT.toFloat()
      layoutParams.width = (newWidth * scale + 0.5f).toInt()
      fluid_av_main.layoutParams = layoutParams

      // Update the TextView with the new width.
      fluid_tv_current_width.setText("$newWidth dp")
    }
  }
}
