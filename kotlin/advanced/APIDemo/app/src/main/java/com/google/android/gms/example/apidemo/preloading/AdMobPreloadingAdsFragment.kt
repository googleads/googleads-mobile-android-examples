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

package com.google.android.gms.example.apidemo.preloading

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.example.apidemo.R
import com.google.android.gms.example.apidemo.databinding.FragmentPreloadMainBinding

/** Demonstrates how to preload ads. */
class AdMobPreloadingAdsFragment : Fragment() {
  private lateinit var viewBinding: FragmentPreloadMainBinding

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?,
  ): View {
    viewBinding = FragmentPreloadMainBinding.inflate(inflater, container, false)

    addFragments()

    return viewBinding.root
  }

  private fun addFragments() {
    addFragment(AppOpenFragment())
    addFragment(InterstitialFragment())
    addFragment(RewardedFragment())
  }

  private fun <T : Fragment> addFragment(fragment: T) {
    val fragmentManager = getParentFragmentManager()
    val fragmentTransaction = fragmentManager.beginTransaction()
    fragmentTransaction.add(R.id.list_formats, fragment)
    fragmentTransaction.commit()
  }
}
