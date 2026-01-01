package com.google.android.gms.example.jetpackcomposedemo.main

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.jetpackcomposedemo.R
import com.google.android.gms.example.jetpackcomposedemo.formats.AdManagerBannerScreen
import com.google.android.gms.example.jetpackcomposedemo.formats.BannerScreen
import com.google.android.gms.example.jetpackcomposedemo.formats.LazyBannerScreen
import com.google.android.gms.example.jetpackcomposedemo.formats.NativeScreen
import com.google.android.gms.example.jetpackcomposedemo.ui.theme.JetpackComposeDemoTheme

@Composable
fun MainScreen(googleMobileAdsViewModel: MainViewModel, modifier: Modifier = Modifier) {
  val context = LocalContext.current
  val activity = context.getActivity()
  val navController = rememberNavController()
  val uiState by googleMobileAdsViewModel.uiState.collectAsState()
  var showNavigationIcon by remember { mutableStateOf(false) }

  LaunchedEffect(navController) {
    navController.addOnDestinationChangedListener { _, destination, _ ->
      showNavigationIcon = destination.route != NavDestinations.Home.name
    }
  }

  Scaffold(
    topBar = {
      MainTopBar(
        isMobileAdsInitialized = uiState.isMobileAdsInitialized,
        isPrivacyOptionsRequired = uiState.isPrivacyOptionsRequired,
        isNavigationEnabled = showNavigationIcon,
        navigateBack = { navController.popBackStack() },
        onOpenAdInspector = { googleMobileAdsViewModel.openAdInspector(context) {} },
        onShowPrivacyOptionsForm = {
          if (activity != null) {
            googleMobileAdsViewModel.showPrivacyOptionsForm(activity) {}
          }
        },
        modifier,
      )
    },
    contentWindowInsets =
      WindowInsets.systemBars.only(WindowInsetsSides.Vertical + WindowInsetsSides.Horizontal),
  ) { innerPadding ->
    Column(Modifier.padding(innerPadding)) {
      NavHost(navController = navController, startDestination = NavDestinations.Home.name) {
        composable(NavDestinations.Home.name) { HomeScreen(uiState, navController) }
        composable(NavDestinations.AdManagerBanner.name) { AdManagerBannerScreen() }
        composable(NavDestinations.Banner.name) { BannerScreen() }
        composable(NavDestinations.LazyBanner.name) { LazyBannerScreen() }
        composable(NavDestinations.Native.name) { NativeScreen() }
      }
      Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MainTopBar(
  isMobileAdsInitialized: Boolean,
  isPrivacyOptionsRequired: Boolean,
  isNavigationEnabled: Boolean,
  navigateBack: () -> Unit,
  onOpenAdInspector: () -> Unit,
  onShowPrivacyOptionsForm: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val context = LocalContext.current
  var menuExpanded by remember { mutableStateOf(false) }

  TopAppBar(
    modifier = modifier,
    title = { Text(context.getString(R.string.main_title)) },
    navigationIcon = {
      if (isNavigationEnabled) {
        IconButton(onClick = navigateBack) {
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
          text = { Text(context.getString(R.string.adinspector_open_button)) },
          enabled = isMobileAdsInitialized,
          onClick = {
            menuExpanded = false
            onOpenAdInspector()
          },
        )
        if (isPrivacyOptionsRequired) {
          DropdownMenuItem(
            text = { Text(context.getString(R.string.privacy_options_open_button)) },
            onClick = {
              menuExpanded = false
              onShowPrivacyOptionsForm()
            },
          )
        }
      }
    },
  )
}

private fun Context.getActivity(): ComponentActivity? =
  when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
  }

@Preview
@Composable
private fun MainScreenPreview() {
  JetpackComposeDemoTheme {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
      MainScreen(MainViewModel.getInstance())
    }
  }
}
