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
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.ads.doubleclick.PublisherAdRequest;
import com.google.android.gms.ads.doubleclick.PublisherAdView;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * The {@link DFPPPIDFragment} class demonstrates how to add a PPID value to a DFP
 * PublisherAdRequest.
 */
public class DFPPPIDFragment extends Fragment {

    private Button loadAdButton;
    private PublisherAdView publisherAdView;
    private EditText usernameEditText;

    public DFPPPIDFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_dfp_ppid, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        usernameEditText = getView().findViewById(R.id.ppid_et_username);
        loadAdButton = getView().findViewById(R.id.ppid_btn_loadad);
        publisherAdView = getView().findViewById(R.id.ppid_pav_main);

        loadAdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameEditText.getText().toString();

                if (username.length() == 0) {
                    Toast.makeText(DFPPPIDFragment.this.getActivity(),
                            "The username cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    String ppid = generatePPID(username);
                    PublisherAdRequest request = new PublisherAdRequest.Builder()
                            .setPublisherProvidedId(ppid)
                            .build();
                    publisherAdView.loadAd(request);
                }
            }
        });
    }

    // This is a simple method to generate a hash of a sample username to use as a PPID. It's being
    // used here as a convenient stand-in for a true Publisher-Provided Identifier. In your own
    // apps, you can decide for yourself how to generate the PPID value, though there are some
    // restrictions on what the values can be. For details, see:
    //
    // https://support.google.com/dfp_premium/answer/2880055

    private String generatePPID(String username) {
        StringBuilder ppid = new StringBuilder();

        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(username.getBytes());
      byte[] bytes = digest.digest();

            for (byte b : bytes) {
                String hexed = Integer.toHexString(b & 0xFF);

                while (hexed.length() < 2) {
                    hexed = "0" + hexed;
                }

                ppid.append(hexed);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e(MainActivity.LOG_TAG, "Could not locate MD5 Digest.");
        }

        return ppid.toString();
    }
}
