package com.google.android.gms.example.apidemo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import java.security.NoSuchAlgorithmException
import kotlinx.android.synthetic.main.fragment_gam_ppid.ppid_btn_loadad
import kotlinx.android.synthetic.main.fragment_gam_ppid.ppid_et_username
import kotlinx.android.synthetic.main.fragment_gam_ppid.ppid_pav_main

/**
 * The [AdManagerPPIDFragment] class demonstrates how to add a PPID value to an AdManagerAdRequest.
 */
class AdManagerPPIDFragment : Fragment() {

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View? {
    return inflater.inflate(R.layout.fragment_gam_ppid, container, false)
  }

  override fun onActivityCreated(savedInstanceState: Bundle?) {
    super.onActivityCreated(savedInstanceState)

    ppid_btn_loadad.setOnClickListener {
      val username = ppid_et_username.text.toString()

      if (username.isEmpty()) {
        Toast.makeText(
          this.activity, "The username cannot be empty",
          Toast.LENGTH_SHORT
        ).show()
      } else {
        val ppid = generatePPID(username)
        val request = AdManagerAdRequest.Builder()
          .setPublisherProvidedId(ppid)
          .build()
        ppid_pav_main.loadAd(request)
      }
    }
  }

  // This is a simple method to generate a hash of a sample username to use as a PPID. It's being
  // used here as a convenient stand-in for a true Publisher-Provided Identifier. In your own
  // apps, you can decide for yourself how to generate the PPID value, though there are some
  // restrictions on what the values can be. For details, see:
  //
  // https://support.google.com/dfp_premium/answer/2880055

  private fun generatePPID(username: String): String {
    val ppid = StringBuilder()

    try {
      val digest = java.security.MessageDigest.getInstance("MD5")
      digest.update(username.toByteArray())
      val bytes = digest.digest()

      for (b in bytes) {
        // Bitwise operators for bytes still reside in kotlin.experimental, hence the call
        // to toInt() here.
        var hexed = Integer.toHexString(b.toInt() and 0xFF)

        while (hexed.length < 2) {
          hexed = "0" + hexed
        }

        ppid.append(hexed)
      }
    } catch (e: NoSuchAlgorithmException) {
      Log.e(MainActivity.LOG_TAG, "Could not locate MD5 Digest.")
    }

    return ppid.toString()
  }
}
