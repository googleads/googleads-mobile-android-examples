package com.google.android.gms.example.jetpackcomposedemo

import androidx.annotation.StringRes
import com.example.jetpackcomposedemo.R

sealed class GoogleMobileAdsScreens(val route: String, @StringRes val resourceId: Int) {
  object HomeNav : GoogleMobileAdsScreens(R.string.nav_home.toString(), R.string.nav_home)
}
