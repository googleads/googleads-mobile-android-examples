// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  id("com.android.application") version "8.6.0" apply false
  id("org.jetbrains.kotlin.android") version "1.9.0" apply false
  id("com.android.library") version "8.6.0" apply false
}

compileOptions {
  sourceCompatibility JavaVersion.VERSION_1_8
  targetCompatibility JavaVersion.VERSION_1_8
}

kotlinOptions {
  kotlinOptions.jvmTarget = "1.8"
}

tasks.register("clean", Delete::class) { delete(rootProject.buildDir) }
