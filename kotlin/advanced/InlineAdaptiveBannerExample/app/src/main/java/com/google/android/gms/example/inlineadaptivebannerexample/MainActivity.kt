package com.google.android.gms.example.inlineadaptivebannerexample

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.WindowMetrics
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONException

/** A simple activity showing the use of [AdView] ads in a [RecyclerView] widget. */
class MainActivity : AppCompatActivity() {
  // The RecyclerView that holds and displays banner ads and menu items.

  // List of banner ads and MenuItems that populate the RecyclerView.
  private val recyclerViewItems: MutableList<Any> = ArrayList()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    // Set your test devices.
    MobileAds.setRequestConfiguration(
      RequestConfiguration.Builder().setTestDeviceIds(listOf(TEST_DEVICE_HASHED_ID)).build()
    )

    CoroutineScope(Dispatchers.IO).launch {
      // Initialize the Google Mobile Ads SDK on a background thread.
      MobileAds.initialize(this@MainActivity) {}
    }

    val recyclerView: RecyclerView? = findViewById(R.id.recycler_view)

    // The size of the RecyclerView depends on the height of the ad.
    recyclerView?.setHasFixedSize(false)

    // Specify a linear layout manager.
    val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
    recyclerView?.layoutManager = layoutManager

    // Update the RecyclerView item's list with menu items and banner ads.
    addMenuItemsFromJson()
    addBannerAds()
    loadBannerAds()

    // Specify an adapter.
    val adapter: RecyclerView.Adapter<ViewHolder> = RecyclerViewAdapter(this, recyclerViewItems)
    recyclerView?.adapter = adapter
  }

  // Determine the screen width to use for the ad width.
  // [START get_ad_width]
  private val adWidth: Int
    get() {
      val displayMetrics = resources.displayMetrics
      val adWidthPixels =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
          val windowMetrics: WindowMetrics = this.windowManager.currentWindowMetrics
          windowMetrics.bounds.width()
        } else {
          displayMetrics.widthPixels
        }
      val density = displayMetrics.density
      return (adWidthPixels / density).toInt()
    }

  // [END get_ad_width]

  override fun onResume() {
    for (item in recyclerViewItems) {
      if (item is AdView) {
        item.resume()
      }
    }
    super.onResume()
  }

  override fun onPause() {
    for (item in recyclerViewItems) {
      if (item is AdView) {
        item.pause()
      }
    }
    super.onPause()
  }

  override fun onDestroy() {
    for (item in recyclerViewItems) {
      if (item is AdView) {
        item.destroy()
      }
    }
    super.onDestroy()
  }

  /** Adds banner ads to the items list. */
  private fun addBannerAds() {
    // Loop through the items array and place a new banner ad in every ith position in
    // the items List.
    var i = 0
    while (i <= recyclerViewItems.size) {
      // [START create_banner_ad_view]
      val adView = AdView(this@MainActivity)
      adView.setAdSize(AdSize.getCurrentOrientationInlineAdaptiveBannerAdSize(this, adWidth))
      // [END create_banner_ad_view]
      adView.adUnitId = AD_UNIT_ID
      recyclerViewItems.add(i, adView)
      i += ITEMS_PER_AD
    }
  }

  /** Sets up and loads the banner ads. */
  private fun loadBannerAds() {
    // Load the first banner ad in the items list (subsequent ads will be loaded automatically
    // in sequence).
    loadBannerAd(0)
  }

  /** Loads the banner ads in the items list. */
  private fun loadBannerAd(index: Int) {
    if (index >= recyclerViewItems.size) {
      return
    }
    val item =
      recyclerViewItems[index] as? AdView
        ?: throw ClassCastException("Expected item at index $index to be a banner ad ad.")

    // Set an AdListener on the AdView to wait for the previous banner ad
    // to finish loading before loading the next ad in the items list.
    item.adListener =
      object : AdListener() {
        override fun onAdLoaded() {
          super.onAdLoaded()
          // The previous banner ad loaded successfully, call this method again to
          // load the next ad in the items list.
          loadBannerAd(index + ITEMS_PER_AD)
        }

        override fun onAdFailedToLoad(loadAdError: LoadAdError) {
          // The previous banner ad failed to load. Call this method again to load
          // the next ad in the items list.
          val error =
            String.format(
              "domain: %s, code: %d, message: %s",
              loadAdError.domain,
              loadAdError.code,
              loadAdError.message,
            )
          Log.e(
            "MainActivity",
            "The previous banner ad failed to load with error: " +
              error +
              ". Attempting to" +
              " load the next banner ad in the items list.",
          )
          loadBannerAd(index + ITEMS_PER_AD)
        }
      }

    // Load the banner ad.
    item.loadAd(AdRequest.Builder().build())
  }

  /** Adds [MenuItem]'s from a JSON file. */
  private fun addMenuItemsFromJson() {
    try {
      val jsonDataString = readJsonDataFromFile()
      val menuItemsJsonArray = JSONArray(jsonDataString)
      for (i in 0 until menuItemsJsonArray.length()) {
        val menuItemObject = menuItemsJsonArray.getJSONObject(i)
        val menuItemName = menuItemObject.getString("name")
        val menuItemDescription = menuItemObject.getString("description")
        val menuItemPrice = menuItemObject.getString("price")
        val menuItemCategory = menuItemObject.getString("category")
        val menuItemImageName = menuItemObject.getString("photo")
        val menuItem =
          MenuItem(
            menuItemName,
            menuItemDescription,
            menuItemPrice,
            menuItemCategory,
            menuItemImageName,
          )
        recyclerViewItems.add(menuItem)
      }
    } catch (exception: IOException) {
      Log.e(MainActivity::class.java.name, "Unable to parse JSON file.", exception)
    } catch (exception: JSONException) {
      Log.e(MainActivity::class.java.name, "Unable to parse JSON file.", exception)
    }
  }

  /**
   * Reads the JSON file and converts the JSON data to a [String].
   *
   * @return A [String] representation of the JSON data.
   * @throws IOException if unable to read the JSON file.
   */
  @Throws(IOException::class)
  private fun readJsonDataFromFile(): String {
    var inputStream: InputStream? = null
    val builder = StringBuilder()
    try {
      var jsonDataString: String? = null
      inputStream = resources.openRawResource(R.raw.menu_items_json)
      val bufferedReader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
      while (bufferedReader.readLine().also { jsonDataString = it } != null) {
        builder.append(jsonDataString)
      }
    } finally {
      inputStream?.close()
    }
    return builder.toString()
  }

  companion object {
    private const val AD_UNIT_ID = "ca-app-pub-3940256099942544/1039341195"

    // A banner ad is placed in every 8th position in the RecyclerView.
    const val ITEMS_PER_AD = 8

    // Check your logcat output for the test device hashed ID e.g.
    // "Use RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("ABCDEF012345"))
    // to get test ads on this device" or
    // "Use new ConsentDebugSettings.Builder().addTestDeviceHashedId("ABCDEF012345") to set this as
    // a debug device".
    const val TEST_DEVICE_HASHED_ID = "ABCDEF012345"
  }
}
