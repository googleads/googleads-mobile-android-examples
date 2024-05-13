package com.google.android.gms.example.apidemo

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AppEventListener
import com.google.android.gms.example.apidemo.databinding.FragmentGamAppEventsBinding

/**
 * The [AdManagerAppEventsFragment] class demonstrates how to receive App Events from an AdManager
 * creative.
 */
class AdManagerAppEventsFragment : Fragment() {

  private lateinit var fragmentBinding: FragmentGamAppEventsBinding

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View? {
    fragmentBinding = FragmentGamAppEventsBinding.inflate(layoutInflater)
    return fragmentBinding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    fragmentBinding.appeventsAvMain.appEventListener = AppEventListener { name, data ->
      // The Ad Manager ad that this fragment loads contains JavaScript code that sends App
      // Events to the host application. This AppEventListener receives those events,
      // and sets the background of the fragment to match the data that comes in.
      // The ad will send "red" when it loads, "blue" five seconds later, and "green"
      // if the user taps the ad.

      // This is just a demonstration, of course. Your apps can do much more interesting
      // things with App Events.

      if (name == "color") {
        val color =
          when (data) {
            "blue" -> Color.rgb(0xD0, 0xD0, 0xFF)
            "red" -> Color.rgb(0xFF, 0xD0, 0xD0)
            else -> Color.rgb(0xD0, 0xFF, 0xD0) // Green.
          }

        fragmentBinding.root.setBackgroundColor(color)
      }
    }

    val adRequest = AdManagerAdRequest.Builder().build()
    fragmentBinding.appeventsAvMain.loadAd(adRequest)
  }
}
