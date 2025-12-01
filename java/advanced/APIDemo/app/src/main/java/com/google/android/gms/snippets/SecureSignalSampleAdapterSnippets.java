// Copyright 2025 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
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
import android.util.Log;
import com.google.android.gms.ads.mediation.InitializationCompleteCallback;
import com.google.android.gms.ads.mediation.MediationConfiguration;
import com.google.android.gms.ads.mediation.VersionInfo;
import com.google.android.gms.ads.mediation.rtb.RtbAdapter;
import com.google.android.gms.ads.mediation.rtb.RtbSignalData;
import com.google.android.gms.ads.mediation.rtb.SignalCallbacks;
import java.util.List;

/** Java code snippets for the developer guide. */
public class SecureSignalSampleAdapterSnippets extends RtbAdapter {

  private static final String TAG = "SecureSignalSampleAdapterSnippets";

  // [START initialize_adapter]
  @Override
  public void initialize(Context context, InitializationCompleteCallback initializationCompleteCallback, List<MediationConfiguration> configurations) {
    // Initialize your ad network's SDK here.
    YourSdk.initialize();

    // Invoke the InitializationCompleteCallback once initialization completes.
    initializationCompleteCallback.onInitializationSucceeded();
  }

  // [END initialize_adapter]

  // [START get_sdk_version_info]
  @Override
  public VersionInfo getSDKVersionInfo() {
    // Get your SDK's version as a string. E.g. "1.2.3"
    String versionString = YourSdk.getVersion();
    String[] splits = versionString.split("\\.");
    if (splits.length >= 3) {
      int major = Integer.parseInt(splits[0]);
      int minor = Integer.parseInt(splits[1]);
      int micro = Integer.parseInt(splits[2]);
      return new VersionInfo(major, minor, micro);
    }

    Log.w(
        TAG,
        String.format(
            "Unexpected SDK version format: %s. Returning 0.0.0 for SDK version.", versionString));
    return new VersionInfo(0, 0, 0);
  }

  // [END get_sdk_version_info]

  // [START get_adapter_version_info]
  @Override
  public VersionInfo getVersionInfo() {
    // If your secure signals SDK implements this adapter in the same binary, return the same
    // version as your SDK:
    // return getSDKVersionInfo();

    // If you built a separate binary for this secure signals adapter, return the adapter's version:
    // Get your adapters's version as a string. E.g. "4.5.6"
    String versionString = "4.5.6"; // Replace with your adapter's version.
    String[] splits = versionString.split("\\.");
    if (splits.length >= 3) {
      int major = Integer.parseInt(splits[0]);
      int minor = Integer.parseInt(splits[1]);
      int micro = Integer.parseInt(splits[2]);
      return new VersionInfo(major, minor, micro);
    }

    Log.w(
        TAG,
        String.format(
            "Unexpected adapter version format: %s. Returning 0.0.0 for adapter version.",
            versionString));
    return new VersionInfo(0, 0, 0);
  }

  // [END get_adapter_version_info]

  // [START collect_signals]
  @Override
  public void collectSignals(RtbSignalData rtbSignalData, SignalCallbacks signalCallbacks) {

    YourSdk.getSignals(
        new YourSdk.GetSignalsCallback() {
          @Override
          public void onSuccess(String signals) {
            signalCallbacks.onSuccess(signals);
          }

          @Override
          public void onFailure(String error) {
            // Handle the error.
          }
        });
  }

  // [END collect_signals]

  /**
   * A mock SDK class used for demonstration purposes in code snippets. In a real adapter
   * implementation, you would replace calls to this class with calls to your actual SDK.
   */
  public static final class YourSdk {

    private static final String SDK_VERSION = "1.2.3";
    private static final String MOCK_SIGNAL = "sample_signal";

    // Private constructor to prevent instantiation.
    private YourSdk() {}

    /** Mock SDK initialization method. */
    public static void initialize() {
      // No-op for mock.
    }

    /**
     * Mock SDK version retrieval method.
     *
     * @return The mock SDK version string.
     */
    public static String getVersion() {
      return SDK_VERSION;
    }

    /** An interface for receiving signals asynchronously. */
    public interface GetSignalsCallback {
      void onSuccess(String signals);

      void onFailure(String error);
    }

    /**
     * Mock SDK signal collection method.
     *
     * @param callback The callback to be invoked when signal collection completes.
     */
    public static void getSignals(GetSignalsCallback callback) {
      // If your SDK provides signals asynchronously, it might look like this:
      callback.onSuccess(MOCK_SIGNAL);
    }
  }
}
