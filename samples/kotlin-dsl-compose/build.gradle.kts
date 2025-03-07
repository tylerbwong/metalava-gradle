import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.library")
    alias(libs.plugins.compose.compiler)
    id("me.tylerbwong.gradle.metalava")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "me.tylerbwong.gradle.metalava.sample"
    compileSdk = 34

    defaultConfig {
        minSdk = 21
    }
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_1_8
    }
}

metalava {
    filename.set("api/$name-api.txt")
    apiCompatAnnotations.set(listOf("androidx.compose.runtime.Composable"))
}

dependencies {
    implementation(libs.androidx.compose.runtime)
}
