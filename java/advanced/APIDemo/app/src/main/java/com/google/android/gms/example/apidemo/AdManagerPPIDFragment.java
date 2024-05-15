/*
 * Copyright (C) 2015 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.google.android.gms.example.apidemo;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.gms.ads.admanager.AdManagerAdRequest;
import com.google.android.gms.ads.admanager.AdManagerAdView;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * The {@link AdManagerPPIDFragment} class demonstrates how to add a PPID value to an
 * AdManagerAdRequest.
 */
public class AdManagerPPIDFragment extends Fragment {

  private AdManagerAdView adView;
  private EditText usernameEditText;

  public AdManagerPPIDFragment() {}

  @Override
  public View onCreateView(
      LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return inflater.inflate(R.layout.fragment_gam_ppid, container, false);
  }

  @Override
  public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    super.onCreate(savedInstanceState);
    usernameEditText = view.findViewById(R.id.ppid_et_username);
    Button loadAdButton = view.findViewById(R.id.ppid_btn_loadad);
    adView = view.findViewById(R.id.ppid_pav_main);

    loadAdButton.setOnClickListener(
        new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            String username = usernameEditText.getText().toString();

            if (username.isEmpty()) {
              Toast.makeText(
                      AdManagerPPIDFragment.this.getActivity(),
                      "The username cannot be empty.",
                      Toast.LENGTH_SHORT)
                  .show();
            } else {
              String ppid = generatePPID(username);
              AdManagerAdRequest request =
                  new AdManagerAdRequest.Builder().setPublisherProvidedId(ppid).build();
              adView.loadAd(request);
            }
          }
        });
  }

  // This method generates a hash of a sample username to use as a Publisher
  // provided identifier (PPID). In your own apps, you can decide for yourself
  // how to generate the PPID value, though there are some restrictions. For
  // details, see:
  // https://support.google.com/admanager/answer/2880055
  private String generatePPID(String username) {
    StringBuilder ppid = new StringBuilder();

    try {
      MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
      digest.update(username.getBytes());
      byte[] bytes = digest.digest();

      for (byte b : bytes) {
        StringBuilder hexed = new StringBuilder(Integer.toHexString(b & 0xFF));

        while (hexed.length() < 2) {
          hexed.insert(0, "0");
        }

        ppid.append(hexed);
      }
    } catch (NoSuchAlgorithmException e) {
      Log.e(MainActivity.LOG_TAG, "Could not locate MD5 Digest.");
    }

    return ppid.toString();
  }
}
