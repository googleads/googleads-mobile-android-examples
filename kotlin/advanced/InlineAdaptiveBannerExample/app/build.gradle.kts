plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
}

android {
  namespace = "com.google.android.gms.example.inlineadaptivebannerexample"
  compileSdk = 34

  defaultConfig {
    applicationId = "com.google.android.gms.example.inlineadaptivebannerexample"
    minSdk = 23
    targetSdk = 34
    versionCode = 1
    versionName = "1.0"
  }

  buildTypes {
    release {
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
  }

  compileOptions {
    sourceCompatibility JavaVersion.VERSION_1_8
    targetCompatibility JavaVersion.VERSION_1_8
  }

  kotlinOptions {
    kotlinOptions.jvmTarget = "1.8"
  }

  buildFeatures { compose = true }
  composeOptions { kotlinCompilerExtensionVersion = "1.4.3" }
  packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
}

dependencies {
  implementation("androidx.compose.runtime:runtime:1.0.0")
  implementation("androidx.core:core-ktx:1.8.0")
  implementation("androidx.appcompat:appcompat:1.6.1")
  implementation("androidx.cardview:cardview:1.0.0")
  implementation("androidx.recyclerview:recyclerview:1.3.2")
  implementation("com.google.android.gms:play-services-ads:24.2.0")
}
