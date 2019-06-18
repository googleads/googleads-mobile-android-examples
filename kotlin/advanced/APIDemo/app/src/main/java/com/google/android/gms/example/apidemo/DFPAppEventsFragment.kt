package com.google.android.gms.example.apidemo

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.doubleclick.AppEventListener
import com.google.android.gms.ads.doubleclick.PublisherAdRequest
import kotlinx.android.synthetic.main.fragment_dfp_app_events.*

/**
 * The [DFPAppEventsFragment] class demonstrates how to receive App Events from a DFP creative.
 */
class DFPAppEventsFragment : Fragment() {

  private lateinit var mRootView: View

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    mRootView = inflater.inflate(R.layout.fragment_dfp_app_events, container, false)
    return mRootView
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    appevents_av_main.appEventListener = AppEventListener { name, data ->
      // The DFP ad that this fragment loads contains JavaScript code that sends App
      // Events to the host application. This AppEventListener receives those events,
      // and sets the background of the fragment to match the data that comes in.
      // The ad will send "red" when it loads, "blue" five seconds later, and "green"
      // if the user taps the ad.

      // This is just a demonstration, of course. Your apps can do much more interesting
      // things with App Events.

      if (name == "color") {
        val color = when (data) {
          "blue" -> Color.rgb(0xD0, 0xD0, 0xFF)
          "red" -> Color.rgb(0xFF, 0xD0, 0xD0)
          else -> Color.rgb(0xD0, 0xFF, 0xD0) // Green.
        }

        mRootView.setBackgroundColor(color)
      }
    }

    val adRequest = PublisherAdRequest.Builder().build()
    appevents_av_main.loadAd(adRequest)
  }
}
