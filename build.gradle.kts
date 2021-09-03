buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        mavenLocal()
    }

    dependencies {
        classpath(libs.android.gradle)
        classpath(libs.kotlin.gradle)
        classpath(libs.metalava.gradle)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
