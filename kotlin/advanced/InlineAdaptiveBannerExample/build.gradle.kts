// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
  id("com.android.application") version "9.1.0" apply false
  id("com.android.library") version "9.1.0" apply false
}

tasks.register("clean", Delete::class) { delete(rootProject.buildDir) }

tasks { withType<JavaCompile> { options.compilerArgs.add("-Xlint:deprecation") } }
