package com.google.android.gms.example.jetpackcomposedemo.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.jetpackcomposedemo.R
import com.google.android.gms.example.jetpackcomposedemo.ui.theme.JetpackComposeDemoTheme

@Composable
fun HomeScreen(
  uiState: MainUiState,
  navController: NavHostController,
  modifier: Modifier = Modifier,
) {
  Column {
    Button(
      onClick = { navController.navigate(NavDestinations.AdManagerBanner.name) },
      enabled = uiState.canRequestAds,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Text(LocalContext.current.getString(R.string.nav_admanager_banner))
    }
    Button(
      onClick = { navController.navigate(NavDestinations.Banner.name) },
      enabled = uiState.canRequestAds,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Text(LocalContext.current.getString(R.string.nav_banner))
    }
    Button(
      onClick = { navController.navigate(NavDestinations.LazyBanner.name) },
      enabled = uiState.canRequestAds,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Text(LocalContext.current.getString(R.string.nav_lazy_banner))
    }
    Button(
      onClick = { navController.navigate(NavDestinations.Native.name) },
      enabled = uiState.canRequestAds,
      modifier = Modifier.fillMaxWidth(),
    ) {
      Text(LocalContext.current.getString(R.string.nav_native))
    }
  }
}

@Preview
@Composable
private fun HomeScreenPreview() {
  JetpackComposeDemoTheme {
    Surface(color = MaterialTheme.colorScheme.background) {
      HomeScreen(MainUiState(), rememberNavController())
    }
  }
}
