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

/** An example {@link RtbAdapter} implementation for secure signal collection guidance. */
// [START extend_adapter_class]
public class SampleAdapterSnippets extends RtbAdapter {
  // [END extend_adapter_class]
  private static final String TAG = "SampleAdapterSnippets";
  private static final String SDK_VERSION_STRING = "1.2.3";
  private static final String SAMPLE_SIGNAL_PLACEHOLDER = "your_sdk_secure_signal_string";

  // [START initialize_adapter]
  @Override
  public void initialize(
      Context context,
      InitializationCompleteCallback initializationCompleteCallback,
      List<MediationConfiguration> configurations) {

    // Add your SDK initialization logic here.

    // Invoke the InitializationCompleteCallback once initialization completes.
    initializationCompleteCallback.onInitializationSucceeded();
  }

  // [END initialize_adapter]

  // [START get_adapter_version_info]
  @Override
  public VersionInfo getVersionInfo() {
    // If your SDK implements this adapter in the same binary, return
    // the same version as your SDK.
    // return getSDKVersionInfo();

    // If you built a separate binary for this adapter, return
    // the adapter's version here.
    int major = 4;
    int minor = 5;
    int micro = 6;
    return new VersionInfo(major, minor, micro);
  }

  // [END get_adapter_version_info]

  // [START get_sdk_version_info]
  @Override
  public VersionInfo getSDKVersionInfo() {

    // Return your SDK's version string here.
    String versionString = SDK_VERSION_STRING;
    String[] splits = versionString.split("\\.");
    if (splits.length >= 3) {
      try {
        int major = Integer.parseInt(splits[0]);
        int minor = Integer.parseInt(splits[1]);
        int micro = Integer.parseInt(splits[2]);
        return new VersionInfo(major, minor, micro);
      } catch (NumberFormatException e) {
        // Fall through to log warning and return 0.0.0.
      }
    }

    Log.w(
        TAG,
        String.format(
            "Unexpected SDK version format: %s. Returning 0.0.0 for SDK version.", versionString));
    return new VersionInfo(0, 0, 0);
  }

  // [END get_sdk_version_info]

  // [START collect_signals]
  @Override
  public void collectSignals(RtbSignalData rtbSignalData, SignalCallbacks signalCallbacks) {

    // Add your signal collection logic here.
    String signals = SAMPLE_SIGNAL_PLACEHOLDER;

    // Return the signals as a string to the Google Mobile Ads SDK.
    signalCallbacks.onSuccess(signals);
  }

  // [END collect_signals]

}
