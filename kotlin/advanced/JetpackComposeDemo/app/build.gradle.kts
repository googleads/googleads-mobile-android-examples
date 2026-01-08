plugins {
  id("com.android.application")
  id("org.jetbrains.kotlin.android")
  id("org.jetbrains.kotlin.plugin.compose") version "2.3.0"
}

android {
  namespace = "com.example.jetpackcomposedemo"
  compileSdk = 36

  defaultConfig {
    applicationId = "com.google.android.gms.example.jetpackcomposedemo"
    minSdk = 24
    targetSdk = 36
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

  buildFeatures { compose = true }
  composeCompiler { reportsDestination = layout.buildDirectory.dir("compose_compiler") }
  packaging { resources { excludes += "/META-INF/{AL2.0,LGPL2.1}" } }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>().configureEach {
  compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17) }
}

dependencies {
  implementation("androidx.activity:activity")
  implementation("androidx.activity:activity-ktx")
  implementation("androidx.activity:activity-compose:1.12.2")
  implementation("androidx.core:core-ktx:1.17.0")
  implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.10.0")
  implementation(platform("androidx.compose:compose-bom:2025.12.01"))
  implementation("androidx.compose.ui:ui")
  implementation("androidx.compose.ui:ui-graphics")
  implementation("androidx.compose.ui:ui-tooling-preview")
  implementation("androidx.compose.material3:material3")
  implementation("androidx.compose.material:material-icons-extended")
  implementation("androidx.compose.foundation:foundation")
  implementation("androidx.navigation:navigation-runtime-ktx:2.9.6")
  implementation("com.google.android.gms:play-services-ads:24.9.0")
  implementation("com.google.android.ump:user-messaging-platform:4.0.0")
  implementation(project(":compose-util"))
  implementation("androidx.navigation:navigation-compose:2.9.6")
  debugImplementation("androidx.compose.ui:ui-tooling")
}
