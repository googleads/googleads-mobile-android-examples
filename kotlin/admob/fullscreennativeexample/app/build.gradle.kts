plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  // If you add plugins to libs.versions.toml, you would change this:
  // id(libs.plugins.android.application)
  // id(libs.plugins.kotlin.android)
}

android {
  namespace = "com.google.example.gms.fullscreennativeexample"
  compileSdk = libs.versions.compileSdk.get().toInt() // Get version from TOML
  defaultConfig {
    applicationId = "com.google.android.gms.example.fullscreennativeexample"
    minSdk = libs.versions.minSdk.get().toInt() // Get version from TOML
    multiDexEnabled = true
    targetSdk = libs.versions.targetSdk.get().toInt() // Get version from TOML
    versionCode = 1
    versionName = "1.0"
    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }
  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }
  buildFeatures { viewBinding = true }
  // This is the key part for setting JVM target compatibility
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_17 // For Java sources
    targetCompatibility = JavaVersion.VERSION_17 // For Java bytecode
  }
  kotlin { compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17) } }
}

dependencies {
  // Referencing libraries using 'libs.<alias>'
  implementation(libs.androidx.appcompat)
  implementation(libs.androidx.constraintlayout)
  implementation(
    libs.play.services.ads
  ) // Note: Gradle automatically converts 'play-services-ads' to 'play.services.ads'
  implementation(libs.androidx.core.ktx)
  implementation(libs.kotlinx.coroutines.android)
}
