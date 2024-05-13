package com.google.android.gms.example.apidemo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.example.apidemo.databinding.FragmentAdmobAdListenerBinding

/**
 * The [AdMobAdListenerFragment] demonstrates the use of the [com.google.android.gms.ads.AdListener]
 * class.
 */
class AdMobAdListenerFragment : Fragment() {

  private lateinit var fragmentBinding: FragmentAdmobAdListenerBinding

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View? {
    fragmentBinding = FragmentAdmobAdListenerBinding.inflate(inflater)
    return fragmentBinding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    fragmentBinding.listenerAdView.adListener =
      object : AdListener() {
        private fun showToast(message: String) {
          val context = this@AdMobAdListenerFragment.view?.context
          if (context != null) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
          }
        }

        override fun onAdLoaded() {
          showToast("Ad loaded.")
        }

        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
          val error =
            "domain: ${loadAdError.domain}, code: ${loadAdError.code}, " +
              "message: ${loadAdError.message}"
          showToast("Ad failed to load with error $error.")
        }

        override fun onAdOpened() {
          showToast("Ad opened.")
        }

        override fun onAdClosed() {
          showToast("Ad closed.")
        }
      }

    val adRequest = AdRequest.Builder().build()
    fragmentBinding.listenerAdView.loadAd(adRequest)
  }
}
