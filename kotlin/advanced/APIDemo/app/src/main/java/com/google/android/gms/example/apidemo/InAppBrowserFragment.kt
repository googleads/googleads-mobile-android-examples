package com.google.android.gms.example.apidemo

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.annotation.Dimension
import androidx.browser.customtabs.CustomTabsCallback
import androidx.browser.customtabs.CustomTabsClient
import androidx.browser.customtabs.CustomTabsIntent
import androidx.browser.customtabs.CustomTabsServiceConnection
import androidx.browser.customtabs.CustomTabsSession
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.example.apidemo.databinding.FragmentBrowserBinding
import java.net.MalformedURLException
import java.net.URL

/** The [InAppBrowserFragment] class demonstrates how to load ads within web content. */
class InAppBrowserFragment : Fragment() {
  private lateinit var binding: FragmentBrowserBinding
  private var customTabsClient: CustomTabsClient? = null
  private var customTabsSession: CustomTabsSession? = null
  private lateinit var webView: WebView

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    binding = FragmentBrowserBinding.inflate(layoutInflater)

    binding.launchWebviewButton.setOnClickListener { launchWebView() }
    binding.launchCustomTabButton.setOnClickListener { launchCustomTab() }

    webView = binding.webView
    return binding.root
  }

  private fun launchWebView() {
    webView.visibility = View.VISIBLE

    webView.webViewClient =
      object : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
          // Handle null URL. Return false to allow the WebView to handle it.
          val url = request.url ?: return false

          // Handle null host. Return false to allow the WebView to handle it.
          if (url.host == null) {
            return false
          }

          val scheme = url.scheme

          // Handle custom URL schemes (e.g., market://, intent://) by attempting to
          // launch an external application.
          if (scheme != null && scheme != "http" && scheme != "https") {
            val intent = Intent(Intent.ACTION_VIEW, url)
            try {
              requireContext().startActivity(intent)
            } catch (_: ActivityNotFoundException) {
              // Activity cannot be found.
              Toast.makeText(
                  activity,
                  "Failed to load URL with scheme: $scheme",
                  Toast.LENGTH_SHORT,
                )
                .show()
            }
            return true // URL handled externally.
          }

          val currentDomain: String? =
            try {
              URL(view.url).host
            } catch (_: MalformedURLException) {
              Toast.makeText(
                  activity,
                  "Could not load the page. Please check the URL.",
                  Toast.LENGTH_SHORT,
                )
                .show()
              return false
            }

          val targetDomain = url.host

          // If the current domain equals the target domain, the
          // assumption is the user is not navigating away from
          // the site. Reload the URL within the existing web view.
          if (currentDomain == targetDomain) {
            return false
          }

          // User is navigating away from the site, open the URL in
          // Custom Tabs to preserve the state of the web view.
          val customTabsIntent = CustomTabsIntent.Builder().build()
          customTabsIntent.launchUrl(requireContext(), url)
          return true
        }
      }

    webView.settings.javaScriptEnabled = true
    webView.settings.domStorageEnabled = true
    webView.settings.mediaPlaybackRequiresUserGesture = false
    CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)

    MobileAds.registerWebView(webView)
    webView.loadUrl(WEB_VIEW_URL)
  }

  private fun launchCustomTab() {
    // Get the default browser package name, this will be null if
    // the default browser does not provide a CustomTabsService.
    val packageName = CustomTabsClient.getPackageName(requireContext(), null)
    if (packageName == null) {
      // Do nothing as service connection is not supported.
      return
    }

    // Listen for events from the CustomTabsSession delegated by the Google Mobile Ads SDK.
    val customTabsCallback =
      object : CustomTabsCallback() {
        override fun onNavigationEvent(navigationEvent: Int, extras: Bundle?) {
          // Called when a navigation event happens.
          Log.i(TAG, "onNavigationEvent: $navigationEvent")
        }

        override fun onMessageChannelReady(extras: Bundle?) {
          // Called when the channel is ready for sending and receiving messages on both
          // ends. This frequently happens, such as each time the SDK requests a
          // new channel.
          Log.i(TAG, "onMessageChannelReady")
        }

        override fun onPostMessage(message: String, extras: Bundle?) {
          // Called when a tab controlled by this CustomTabsSession has sent a postMessage.
          Log.i(TAG, "onPostMessage: $message")
        }

        override fun onRelationshipValidationResult(
          relation: Int,
          requestedOrigin: Uri,
          result: Boolean,
          extras: Bundle?,
        ) {
          // Called when a relationship validation result is available.
          Log.i(TAG, "onRelationshipValidationResult: $result")
        }

        override fun onActivityResized(
          @Dimension(unit = 1) height: Int,
          @Dimension(unit = 1) width: Int,
          extras: Bundle,
        ) {
          // Called when the tab is resized.
          Log.i(TAG, "onActivityResized")
        }
      }

    val connection =
      object : CustomTabsServiceConnection() {
        override fun onCustomTabsServiceConnected(name: ComponentName, client: CustomTabsClient) {
          customTabsClient = client

          // Warm up the browser process.
          customTabsClient?.warmup(0L)

          // Create a new browser session using the Google Mobile Ads SDK.
          customTabsSession =
            MobileAds.registerCustomTabsSession(
              requireContext(),
              // Checks the "Digital Asset Link" to connect the postMessage channel.
              client,
              ORIGIN,
              // Optional parameter to receive the delegated callbacks.
              customTabsCallback,
            )

          // Create a new browser session if the Google Mobile Ads SDK is
          // unable to create one.
          if (customTabsSession == null) {
            customTabsSession = client.newSession(customTabsCallback)
          }

          // Pass the custom tabs session into the intent.
          val customTabsIntent = CustomTabsIntent.Builder(customTabsSession).build()
          customTabsIntent.launchUrl(requireContext(), CUSTOM_TABS_URL.toUri())
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
          // Remove the custom tabs client and custom tabs session.
          customTabsClient = null
          customTabsSession = null
        }
      }

    CustomTabsClient.bindCustomTabsService(requireContext(), packageName, connection)
  }

  private companion object {
    const val WEB_VIEW_URL = "https://google.github.io/webview-ads/test/"
    const val CUSTOM_TABS_URL = "https://google.github.io/webview-ads/test/?browser=cct"

    // TODO: Replace this URL with an associated website.
    const val ORIGIN = "https://www.google.com"
    const val TAG = "InAppBrowserFragment"
  }
}
