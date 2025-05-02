plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("org.jetbrains.kotlin.plugin.compose") version "2.0.0"
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
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
  }
  kotlinOptions { jvmTarget = "17" }
  buildFeatures { compose = true }
  composeOptions { kotlinCompilerExtensionVersion = "1.5.1" }
  composeCompiler { reportsDestination = layout.buildDirectory.dir("compose_compiler") }
  packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
}

dependencies {
  implementation("androidx.activity:activity")
  implementation("androidx.activity:activity-ktx")
  implementation("androidx.activity:activity-compose:1.10.1")
  implementation("androidx.core:core-ktx:1.16.0")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
  implementation(platform("androidx.compose:compose-bom:2025.04.01"))
  implementation("androidx.compose.ui:ui:1.8.0")
  implementation("androidx.compose.ui:ui-graphics:1.8.0")
  implementation("androidx.compose.material3:material3")
  implementation("androidx.compose.foundation:foundation")
  implementation("androidx.navigation:navigation-runtime-ktx:2.8.9")
  implementation("com.google.android.gms:play-services-ads:24.2.0")
  implementation("com.google.android.ump:user-messaging-platform:3.2.0")
  implementation(project(":compose-util"))
  implementation("androidx.navigation:navigation-compose:2.8.9")
  debugImplementation("androidx.compose.ui:ui-tooling")
}
