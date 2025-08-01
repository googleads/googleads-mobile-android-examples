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
import android.util.Log
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private class MediationSnippets {

  fun checkMediationAdapterStatusOnIntitialize(context: Context) {
    // [START initialize_sdk]
    CoroutineScope(Dispatchers.IO).launch {
      // Initialize the Google Mobile Ads SDK on a background thread.
      MobileAds.initialize(context) { initializationStatus ->
        // Check each adapter's initialization status.
        for ((adapterClass, status) in initializationStatus.adapterStatusMap) {
          Log.d(
            TAG,
            "Adapter: $adapterClass, Status: ${status.description}, Latency: ${status.latency}ms",
          )
        }
      }
    }
    // [END initialize_sdk]
  }

  private companion object {
    const val TAG = "MediationSnippets"
  }
}
