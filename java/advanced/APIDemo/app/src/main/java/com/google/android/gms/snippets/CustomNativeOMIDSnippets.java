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

package com.google.android.gms.snippets;

import android.content.Context;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.nativead.NativeCustomFormatAd;
import com.google.android.gms.ads.nativead.NativeCustomFormatAd.DisplayOpenMeasurement;

/** Java code snippets for the developer guide. */
public class CustomNativeOMIDSnippets {

  private static final String AD_UNIT_ID = "/21775744923/example/native";
  private static final String CUSTOM_TEMPLATE_ID = "12406343";

  // [START load_custom_native_ad]
  private void loadCustomNativeAd(Context context, ViewGroup nativeCustomFormatAdContainer) {

    AdLoader adLoader =
        new AdLoader.Builder(context, AD_UNIT_ID)
            .forCustomFormatAd(
                CUSTOM_TEMPLATE_ID,
                new NativeCustomFormatAd.OnCustomFormatAdLoadedListener() {
                  @Override
                  public void onCustomFormatAdLoaded(@NonNull NativeCustomFormatAd ad) {
                    // Show the ad first and then register your view and begin open measurement.
                    // Make sure to do this on the main thread.

                    // ...
                    // Show ad
                    // ...

                    startOpenMeasurement(ad, nativeCustomFormatAdContainer);
                  }
                },
                new NativeCustomFormatAd.OnCustomClickListener() {
                  @Override
                  public void onCustomClick(NativeCustomFormatAd ad, String assetName) {
                    // Handle the click action
                  }
                })
            .build();
    adLoader.loadAd(new AdRequest.Builder().build());
  }

  // [END load_custom_native_ad]

  // [START start_open_measurement]
  private void startOpenMeasurement(
      NativeCustomFormatAd ad, ViewGroup nativeCustomFormatAdContainer) {
    DisplayOpenMeasurement displayOpenMeasurement = ad.getDisplayOpenMeasurement();
    displayOpenMeasurement.setView(nativeCustomFormatAdContainer);
    displayOpenMeasurement.start();
  }
  // [END start_open_measurement]
}
