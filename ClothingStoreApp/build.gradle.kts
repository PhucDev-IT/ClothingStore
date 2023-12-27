buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.0")
        val nav_version = "2.5.3"
        //noinspection GradleDependency
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$nav_version")



    }
    repositories {
        google()
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("com.google.gms.google-services") version "4.4.0" apply false

}