package com.google.android.gms.example.jetpackcomposedemo

import android.app.Activity
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jetpackcomposedemo.R
import com.google.android.gms.example.jetpackcomposedemo.ui.theme.JetpackComposeDemoTheme

@Composable
fun MainScreen(googleMobileAdsViewModel: GoogleMobileAdsViewModel) {
  val navController = rememberNavController()
  Scaffold(topBar = { MainTopBar(googleMobileAdsViewModel, navController) }) { innerPadding ->
    Column(
      modifier = Modifier.padding(innerPadding),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      NavHost(navController = navController, startDestination = NavDestinations.Home.name) {
        composable(NavDestinations.Home.name) {}
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainTopBar(
  googleMobileAdsViewModel: GoogleMobileAdsViewModel,
  navController: NavHostController,
) {
  val context = LocalContext.current
  var menuExpanded by remember { mutableStateOf(false) }
  val uiState by googleMobileAdsViewModel.uiState.collectAsState()
  TopAppBar(
    title = { Text(context.getString(R.string.main_title)) },
    navigationIcon = {
      navController.currentBackStackEntry.apply {
        IconButton(onClick = { navController.popBackStack() }) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = Icons.AutoMirrored.Filled.ArrowBack.name,
          )
        }
      }
    },
    actions = {
      IconButton(onClick = { menuExpanded = true }) {
        Icon(imageVector = Icons.Filled.MoreVert, contentDescription = Icons.Filled.MoreVert.name)
      }
      DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
        DropdownMenuItem(
          text = { Text(context.getString(R.string.adinspector_open)) },
          enabled = uiState.canRequestAds,
          onClick = {
            googleMobileAdsViewModel.openAdInspector(context) { error ->
              if (error != null) {
                Log.e(
                  GoogleMobileAdsApplication.TAG,
                  context.getString(R.string.privacyoptions_error),
                )
              }
              menuExpanded = false
            }
          },
        )
        if (uiState.isPrivacyOptionsRequired) {
          DropdownMenuItem(
            text = { Text(context.getString(R.string.privacyoptions_open)) },
            onClick = {
              if (context is Activity) {
                googleMobileAdsViewModel.showPrivacyOptionsForm(context) { error ->
                  if (error != null) {
                    Log.e(GoogleMobileAdsApplication.TAG, R.string.privacyoptions_error.toString())
                  }
                  menuExpanded = false
                }
              }
            },
          )
        }
      }
    },
  )
}

@Preview
@Composable
private fun MainScreenPreview() {
  JetpackComposeDemoTheme {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
      MainScreen(GoogleMobileAdsViewModel.getInstance())
    }
  }
}
