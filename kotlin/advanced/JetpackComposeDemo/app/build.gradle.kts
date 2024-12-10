plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
}

android {
  namespace = "com.example.jetpackcomposedemo"
  compileSdk = 35

  defaultConfig {
    applicationId = "com.google.android.gms.example.jetpackcomposedemo"
    minSdk = 24
    targetSdk = 35
    versionCode = 1
    versionName = "1.0"

    vectorDrawables { useSupportLibrary = true }
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }

  buildFeatures { compose = true }
  composeOptions { kotlinCompilerExtensionVersion = "1.5.1" }
  packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
}

dependencies {
  implementation("androidx.activity:activity")
  implementation("androidx.activity:activity-ktx")
  implementation("androidx.activity:activity-compose:1.9.1")
  implementation("androidx.core:core-ktx:1.13.1")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
  implementation(platform("androidx.compose:compose-bom:2024.06.00"))
  implementation("androidx.compose.ui:ui:1.7.3")
  implementation("androidx.compose.ui:ui-graphics:1.7.3")
  implementation("androidx.compose.material3:material3")
  implementation("androidx.compose.foundation:foundation")
  implementation("androidx.navigation:navigation-compose:2.7.7")
  implementation("androidx.navigation:navigation-runtime-ktx:2.7.7")
  implementation("com.google.android.gms:play-services-ads:23.6.0")
  implementation("com.google.android.ump:user-messaging-platform:3.1.0")
  implementation(project(":compose-util"))
  debugImplementation("androidx.compose.ui:ui-tooling")
}
