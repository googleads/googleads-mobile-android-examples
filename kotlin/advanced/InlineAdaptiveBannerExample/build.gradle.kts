// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  id("com.android.application") version "8.6.0" apply false
  id("com.android.library") version "8.6.0" apply false
  id("org.jetbrains.kotlin.android") version "2.1.0" apply false
}

tasks.register("clean", Delete::class) { delete(rootProject.buildDir) }

tasks { withType<JavaCompile> { options.compilerArgs.add("-Xlint:deprecation") } }
