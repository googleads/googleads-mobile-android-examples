plugins {
  id("com.android.library")
  id("org.jetbrains.kotlin.android")
  id("org.jetbrains.kotlin.plugin.compose") version "2.0.0"
}

android {
  namespace = "com.google.android.gms.example"
  compileSdk = 35

  defaultConfig {
    minSdk = 24

    consumerProguardFiles("consumer-rules.pro")
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
  composeCompiler { reportsDestination = layout.buildDirectory.dir("compose_compiler") }
  composeOptions { kotlinCompilerExtensionVersion = "1.5.1" }
}

dependencies {
  implementation("androidx.core:core-ktx:1.16.0")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
  implementation(platform("androidx.compose:compose-bom:2025.04.01"))
  implementation("androidx.compose.ui:ui:1.8.0")
  implementation("androidx.compose.ui:ui-graphics:1.8.0")
  implementation("androidx.compose.material3:material3")
  implementation("androidx.compose.foundation:foundation")
  implementation("com.google.android.gms:play-services-ads:24.2.0")
  implementation("androidx.lifecycle:lifecycle-runtime-compose:2.8.7")
  debugImplementation("androidx.compose.ui:ui-tooling")
}
