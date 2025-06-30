/*
 * Copyright 2025 Google LLC
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

package com.google.example.gms.fullscreennativeexample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.example.gms.fullscreennativeexample.databinding.FragmentMainBinding

/** A fragment class that provides the UI to load/show a native ad. */
class MainFragment() : Fragment(R.layout.fragment_main) {

  lateinit var binding: FragmentMainBinding

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = FragmentMainBinding.inflate(layoutInflater)
    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    // Configure "Load ad" button.
    binding.loadAdButton.setOnClickListener {
      binding.loadAdButton.isEnabled = false
      binding.showAdButton.isEnabled = false

      val mainActivity = activity as? MainActivity
      mainActivity?.loadAd()
    }

    // Configure "Show ad" button.
    binding.showAdButton.setOnClickListener {
      binding.loadAdButton.isEnabled = true
      binding.showAdButton.isEnabled = false

      // Display the full-screen native ad.
      val fragmentTransaction = parentFragmentManager.beginTransaction()
      val nativeAdFragment = NativeAdFragment()
      fragmentTransaction.add(R.id.fragment_container_view, nativeAdFragment)
      fragmentTransaction.show(nativeAdFragment).addToBackStack(null)
      fragmentTransaction.commit()
    }
  }
}
