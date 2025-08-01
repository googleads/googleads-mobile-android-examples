// You may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.android.gms.snippets

import android.content.Context
import android.view.ViewGroup
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeCustomFormatAd

/** Kotlin code snippets for the developer guide. */
class CustomNativeOMIDSnippets {

  // [START load_custom_native_ad]
  private fun loadCustomNativeAd(context: Context, nativeCustomFormatAdContainer: ViewGroup) {
    val adLoader =
      AdLoader.Builder(context, AD_UNIT_ID)
        .forCustomFormatAd(
          CUSTOM_TEMPLATE_ID,
          NativeCustomFormatAd.OnCustomFormatAdLoadedListener { ad ->
            // Show the ad first and then register your view and begin open measurement. Make sure
            // to do this on the main thread.

            // ...
            // Show ad
            // ...

            startOpenMeasurement(ad, nativeCustomFormatAdContainer)
          },
          NativeCustomFormatAd.OnCustomClickListener { ad, assetName ->
            // Handle the click action.
          },
        )
        .build()

    adLoader.loadAd(AdRequest.Builder().build())
  }

  // [END load_custom_native_ad]

  // [START start_open_measurement]
  private fun startOpenMeasurement(
    ad: NativeCustomFormatAd,
    nativeCustomFormatAdContainer: ViewGroup,
  ) {
    val displayOpenMeasurement = ad.displayOpenMeasurement
    displayOpenMeasurement.setView(nativeCustomFormatAdContainer)
    displayOpenMeasurement.start()
  }

  // [END start_open_measurement]

  private companion object {
    const val AD_UNIT_ID = "/21775744923/example/native"
    const val CUSTOM_TEMPLATE_ID = "12406343"
  }
}
